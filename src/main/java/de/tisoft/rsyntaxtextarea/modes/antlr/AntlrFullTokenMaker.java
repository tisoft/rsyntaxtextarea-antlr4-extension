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
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.text.Segment;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerBase;

public abstract class AntlrFullTokenMaker extends TokenMakerBase {

  private LinkedHashMap<String, Token> lexerCache =
      new LinkedHashMap<String, Token>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Token> eldest) {
          return size() > 10;
        }
      };

  private final List<MultiLineTokenInfo> multiLineTokenInfos;

  protected AntlrFullTokenMaker(MultiLineTokenInfo... multiLineTokenInfos) {
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

    Token cachedToken =
        lexerCache.computeIfAbsent(
            new String(text.array),
            new Function<String, Token>() {
              @Override
              public Token apply(String s) {
                resetTokenList();
                Lexer lexer = createLexer(new String(text.array));
                lexer.removeErrorListeners();
                lexer.addErrorListener(new AlwaysThrowingErrorListener());

                int currentArrayOffset = text.getBeginIndex();
                int currentDocumentOffset = startOffset;

                try {
                  while (true) {
                    org.antlr.v4.runtime.Token at = lexer.nextToken();
                    if (at.getType() == CommonToken.EOF) {
                      addNullToken();
                      break;
                    } else {
                      addToken(
                          text,
                          currentArrayOffset,
                          at.getStopIndex(),
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
                          text.array,
                          currentArrayOffset,
                          text.offset - currentArrayOffset + text.count);
                  int type;

                  type = Token.ERROR_IDENTIFIER;

                  addToken(
                      text,
                      currentArrayOffset,
                      currentArrayOffset + remainingText.length() - 1,
                      type,
                      currentDocumentOffset);
                }
                if (firstToken == null) {
                  // make sure we always have a token
                  addNullToken();
                }

                return firstToken;
              }
            });

    // extract the real start token for the current line
    firstToken = currentToken = previousToken = null;

    Token t = cachedToken;
    do {
      if (t.getEndOffset() > text.getBeginIndex() && t.getOffset() < text.getEndIndex()) {
        // the token is within the segment
        int offset = t.getOffset();
        if (offset < text.getBeginIndex()) {
          // the token starts before this segment, limit it to segment start
          offset = text.getBeginIndex();
        }

        int endOffset = t.getEndOffset();
        if (endOffset > text.getEndIndex()) {
          // the token ends after this segment, limit it to segment end
          endOffset = text.getEndIndex();
        }
        addToken(text, offset, endOffset-1, t.getType(), offset);
      }
    } while ((t = t.getNextToken()) != null);

       // line end
      addNullToken();

    return firstToken;
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

  /** A {@link ANTLRErrorListener} that throws a RuntimeException for every error */
  private static class AlwaysThrowingErrorListener implements ANTLRErrorListener {
    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int line,
        int charPositionInLine,
        String msg,
        RecognitionException e) {
      throw new AntlrException();
    }

    @Override
    public void reportAmbiguity(
        Parser recognizer,
        DFA dfa,
        int startIndex,
        int stopIndex,
        boolean exact,
        BitSet ambigAlts,
        ATNConfigSet configs) {
      throw new AntlrException();
    }

    @Override
    public void reportAttemptingFullContext(
        Parser recognizer,
        DFA dfa,
        int startIndex,
        int stopIndex,
        BitSet conflictingAlts,
        ATNConfigSet configs) {
      throw new AntlrException();
    }

    @Override
    public void reportContextSensitivity(
        Parser recognizer,
        DFA dfa,
        int startIndex,
        int stopIndex,
        int prediction,
        ATNConfigSet configs) {
      throw new AntlrException();
    }
  }

  private static class AntlrException extends RuntimeException {}
}
