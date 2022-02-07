/*-
 * #%L
 * RSyntaxTextArea ANTLR 4 Extension
 * %%
 * Copyright (C) 2021 Markus Heberling
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the Markus Heberling nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package de.tisoft.rsyntaxtextarea.modes.antlr;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerBase;

import javax.swing.text.Segment;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    String multilineTokenStart = getMultilineTokenStart(modeInfo);
    if (multilineTokenStart != null) {
      // we are inside a multi line token, so prefix the text with the token start
      line = multilineTokenStart + line;
    }

    // check if we have a multi line token start without an end
    String multilineTokenEnd = null;

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
          if (currentToken != null && isUnfinishedMultilineToken(currentToken, initialTokenType)) {
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
    if (multilineTokenStart != null
            && at.getText().startsWith(multilineTokenStart)
            && at.getCharPositionInLine() == 0) {
      // need to subtract our inserted token start
      end -= multilineTokenStart.length();
    }
    if (multilineTokenEnd != null && at.getText().endsWith(multilineTokenEnd)) {
      // need to subtract our inserted token end
      end -= multilineTokenEnd.length();
    }
    return end;
  }

  private String getMultilineTokenStart(ModeInfoManager.ModeInfo modeInfo) {
    return getMultiLineTokenInfo(getLanguageIndex(), modeInfo.tokenType)
            .map(i -> i.tokenStart)
            .orElse(null);
  }

  private boolean isUnfinishedMultilineToken(Token token, int initialTypeToken) {
    for (MultiLineTokenInfo mti : multiLineTokenInfos) {
      if (mti.token == token.getType()) {
        return isUnfinishedMultilineToken(token, initialTypeToken, mti);
      }
    }
    return false;
  }

  private boolean isUnfinishedMultilineToken(Token token, int initialTypeToken, MultiLineTokenInfo mti) {
    if (!token.endsWith(mti.tokenEnd.toCharArray())) return true;
    if (mti.tokenStart.contentEquals(mti.tokenEnd)) {
      if (token.getOffset() == 0 && initialTypeToken == mti.token) {
        return false;
      } else return token.getLexeme().equals(mti.tokenEnd);
    }
    return false;
  }

  private Optional<MultiLineTokenInfo> getMultiLineTokenInfo(int languageIndex, int token) {
    return multiLineTokenInfos.stream()
            .filter(i -> i.languageIndex == languageIndex)
            .filter(i -> i.token == token)
            .findFirst();
  }

  protected abstract Lexer createLexer(String text);
}
