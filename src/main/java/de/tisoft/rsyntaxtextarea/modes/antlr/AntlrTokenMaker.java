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
import org.antlr.v4.runtime.*;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerBase;

public abstract class AntlrTokenMaker extends TokenMakerBase {

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
    } else {
      return convertType(type);
    }
  }

  protected abstract int convertType(int type);

  public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    String line = text.toString();
    resetTokenList();
    MultiLineTokenInfo initialMultiLineTokenInfo =
        getMultiLineTokenInfo(getLanguageIndex(), initialTokenType);
    String multilineTokenStart =
        initialMultiLineTokenInfo == null ? null : initialMultiLineTokenInfo.tokenStart;
    if (initialMultiLineTokenInfo != null) {
      // we are inside a multi line token, so prefix the text with the token start
      line = multilineTokenStart + line;
      setLanguageIndex(initialMultiLineTokenInfo.languageIndex);
    }

    // check if we have a multi line token start without an end
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
          line += multilineTokenEnd;
          break;
        }
      }
    }

    Lexer lexer = createLexer(line);
    if (getLanguageIndex() != 0) {
      lexer.pushMode(getLanguageIndex());
    }
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
          int end = currentArrayOffset + at.getText().length() - 1;
          if (initialMultiLineTokenInfo != null
              && multilineTokenStart != null
              && at.getText().startsWith(multilineTokenStart)) {
            // need to subtract our inserted token start
            end -= multilineTokenStart.length();
          }
          if (multilineTokenEnd != null && at.getText().endsWith(multilineTokenEnd)) {
            // need to subtract our inserted token end
            end -= multilineTokenEnd.length();
          }
          addToken(
              text,
              currentArrayOffset,
              end,
              getClosestStandardTokenTypeForInternalType(at.getType()),
              currentDocumentOffset);
          // update from current token
          currentArrayOffset = currentToken.textOffset + currentToken.textCount;
          currentDocumentOffset = currentToken.getEndOffset();
        }
      }
    } catch (AntlrException exceptionInstanceNotNeeded) {
      // mark the rest of the line as error
      final String remainingText =
          String.valueOf(
              text.array, currentArrayOffset, text.offset - currentArrayOffset + text.count);
      int type;

      if (initialMultiLineTokenInfo != null) {
        type = initialMultiLineTokenInfo.token;
      } else {
        type = Token.ERROR_IDENTIFIER;
      }

      addToken(
          text,
          currentArrayOffset,
          currentArrayOffset + remainingText.length() - 1,
          type,
          currentDocumentOffset);

      if (initialMultiLineTokenInfo == null) {
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
      firstToken.setType(initialTokenType);
      firstToken.text = new char[0];
      firstToken.textCount = 0;
    }
    return firstToken;
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
