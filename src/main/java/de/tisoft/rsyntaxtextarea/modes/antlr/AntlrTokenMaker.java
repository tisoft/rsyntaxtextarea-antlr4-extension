/*-
 * #%L
 * RSyntaxTextArea ANTLR 4 Extension
 * %%
 * Copyright (C) 2021 Markus Heberling
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.tisoft.rsyntaxtextarea.modes.antlr;

import java.util.Arrays;
import java.util.List;
import javax.swing.text.Segment;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerBase;

public abstract class AntlrTokenMaker extends TokenMakerBase {

  private final ModeInfoManager modeInfoManager = new ModeInfoManager();

  private final List<MultiLineTokenInfo> multiLineTokenInfos;

  protected AntlrTokenMaker(MultiLineTokenInfo... multiLineTokenInfos) {
    super();
    this.multiLineTokenInfos = Arrays.asList(multiLineTokenInfos);
  }

  @Override
  public int getClosestStandardTokenTypeForInternalType(int type) {
    if (type == CommonToken.INVALID_TYPE) {
      // mark as error
      return Token.ERROR_IDENTIFIER;
    } else if (type < 0) {
      return modeInfoManager.getModeInfo(type).tokenType;
    } else {
      return convertType(type);
    }
  }

  protected abstract int convertType(int type);

  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    String line = text.toString();
    resetTokenList();

    // the modes to push
    ModeInfoManager.ModeInfo modeInfo = modeInfoManager.getModeInfo(initialTokenType);
    // we need to set it, so that the correct multiline token can be found
    setLanguageIndex(modeInfo.currentMode);

    String multilineTokenStart = getTokenStart(modeInfo);
    if (multilineTokenStart != null) {
      // we are inside a multi line token, so prefix the text with the token start
      line = multilineTokenStart + line;
    }

    // check if we have a multi line token start without an end
    String multilineTokenEnd = getMultilineTokenEnd(line);
    if (multilineTokenEnd != null) {
      line += multilineTokenEnd;
    }

    Lexer lexer = createLexer(line);
    for (int mode : modeInfo.modeStack.toArray()) {
      // push the modes into the lexer, so it knows where it is
      lexer.pushMode(mode);
    }
    lexer.mode(modeInfo.currentMode);
    lexer.removeErrorListeners();
    lexer.addErrorListener(new AlwaysThrowingErrorListener());

    int currentArrayOffset = text.getBeginIndex();
    int currentDocumentOffset = startOffset;

    try {
      while (true) {
        org.antlr.v4.runtime.Token at = lexer.nextToken();
        setLanguageIndex(lexer._mode);
        if (at.getType() == CommonToken.EOF) {
          if (multilineTokenEnd == null) {
            addNullToken();
          }
          break;
        } else {
          addToken(
              text,
              currentArrayOffset,
              currentDocumentOffset,
              multilineTokenStart,
              multilineTokenEnd,
              at);
          // update from current token
          currentArrayOffset = currentToken.textOffset + currentToken.textCount;
          currentDocumentOffset = currentToken.getEndOffset();
        }
      }
    } catch (AlwaysThrowingErrorListener.AntlrException exceptionInstanceNotNeeded) {
      // mark the rest of the line as error
      final String remainingText =
          String.valueOf(
              text.array, currentArrayOffset, text.offset - currentArrayOffset + text.count);

      int type = multilineTokenStart != null ? modeInfo.tokenType : Token.ERROR_IDENTIFIER;

      addToken(
          text,
          currentArrayOffset,
          currentArrayOffset + remainingText.length() - 1,
          type,
          currentDocumentOffset);

      if (multilineTokenStart == null) {
        // we are not in a multiline token, so we assume the line ends here
        addNullToken();
      }
    }

    if (firstToken == null) {
      // make sure we always have a token
      addNullToken();
    }

    if (firstToken.getType() == Token.NULL && firstToken == currentToken) {
      // empty line, copy type from last line
      firstToken.setType(modeInfo.tokenType);
      firstToken.text = new char[0];
      firstToken.textCount = 0;
    }

    if (!lexer._modeStack.isEmpty() || lexer._mode != Lexer.DEFAULT_MODE) {
      currentToken.setType(
          modeInfoManager.storeModeInfo(currentToken.getType(), lexer._mode, lexer._modeStack));
    }

    return firstToken;
  }

  private void addToken(
      Segment text,
      int start,
      int startOffset,
      String multilineTokenStart,
      String multilineTokenEnd,
      org.antlr.v4.runtime.Token at) {
    addToken(
        text,
        start,
        calculateTokenEnd(multilineTokenStart, multilineTokenEnd, start, at),
        getClosestStandardTokenTypeForInternalType(at.getType()),
        startOffset);
  }

  private int calculateTokenEnd(
      String multilineTokenStart,
      String multilineTokenEnd,
      int currentArrayOffset,
      org.antlr.v4.runtime.Token at) {
    int end = currentArrayOffset + at.getText().length() - 1;
    if (multilineTokenStart != null && at.getText().startsWith(multilineTokenStart)) {
      // need to subtract our inserted token start
      end -= multilineTokenStart.length();
    }
    if (multilineTokenEnd != null && at.getText().endsWith(multilineTokenEnd)) {
      // need to subtract our inserted token end
      end -= multilineTokenEnd.length();
    }
    return end;
  }

  private String getTokenStart(ModeInfoManager.ModeInfo modeInfo) {
    MultiLineTokenInfo initialMultiLineTokenInfo =
        getMultiLineTokenInfo(getLanguageIndex(), modeInfo.tokenType);
    return initialMultiLineTokenInfo == null ? null : initialMultiLineTokenInfo.tokenStart;
  }

  private String getMultilineTokenEnd(String line) {
    String multilineTokenEnd = null;
    for (MultiLineTokenInfo info : multiLineTokenInfos) {
      if (info.languageIndex == getLanguageIndex()) {
        // the language index matches our current language
        int tokenStartPos = line.indexOf(info.tokenStart);
        if (tokenStartPos > -1
            && line.indexOf(info.tokenEnd, tokenStartPos + info.tokenStart.length()) == -1) {
          // we are in the middle of a multi line token, we need to end it so the lexer can
          // recognize it
          multilineTokenEnd = info.tokenEnd;
          break;
        }
      }
    }
    return multilineTokenEnd;
  }

  private MultiLineTokenInfo getMultiLineTokenInfo(int languageIndex, int token) {
    return multiLineTokenInfos.stream()
        .filter(i -> i.languageIndex == languageIndex)
        .filter(i -> i.token == token)
        .findFirst()
        .orElse(null);
  }

  protected abstract Lexer createLexer(String text);

  protected static class MultiLineTokenInfo {
    private final int languageIndex;

    private final int token;

    private final String tokenStart;

    private final String tokenEnd;

    public MultiLineTokenInfo(int languageIndex, int token, String tokenStart, String tokenEnd) {
      this.languageIndex = languageIndex;
      this.token = token;
      this.tokenStart = tokenStart;
      this.tokenEnd = tokenEnd;
    }
  }
}
