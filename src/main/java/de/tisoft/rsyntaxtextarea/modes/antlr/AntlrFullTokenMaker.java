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

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.Segment;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerBase;

public abstract class AntlrFullTokenMaker extends TokenMakerBase {

  private final LinkedHashMap<String, Token> lexerCache =
      new LinkedHashMap<String, Token>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Token> eldest) {
          return size() > 10;
        }
      };

  private String lastLexed = "";

  protected AntlrFullTokenMaker() {
    super();
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
    lastLexed =
        lastLexed.substring(0, startOffset)
            + new String(text.array, text.offset, text.array.length - text.offset)+"\n";
    Token cachedToken =
        lexerCache.computeIfAbsent(
            lastLexed,
            s ->
                new InternalFullTokenMaker(AntlrFullTokenMaker.this)
                    .getTokenList(
                        new Segment(text.array, 0, text.array.length),
                        initialTokenType,
                        startOffset));

    // extract the real start token for the current line
    resetTokenList();

    int currentArrayOffset = text.getBeginIndex();
    int currentDocumentOffset = startOffset;

    Token t = cachedToken;
    do {
      if (t.getEndOffset() >= text.getBeginIndex() && t.getOffset() <= text.getEndIndex()) {
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
        addToken(
            text,
            currentArrayOffset,
            currentArrayOffset + endOffset - offset - 1,
            t.getType(),
            currentDocumentOffset);

        // update from current token
        currentArrayOffset = currentToken.textOffset + currentToken.textCount;
        currentDocumentOffset = currentToken.getEndOffset();
      }
    } while ((t = t.getNextToken()) != null);

    // line end
    addNullToken();

    return firstToken;
  }

  private int lastTokenCounter = -1;

  @Override
  public int getLastTokenTypeOnLine(Segment text, int initialTokenType) {
    if (--lastTokenCounter > 0) {
      // always negative
      lastTokenCounter = -1;
    }
    return lastTokenCounter;
  }

  protected abstract Lexer createLexer(String text);

  private static class InternalFullTokenMaker extends TokenMakerBase {
    private final AntlrFullTokenMaker antlrFullTokenMaker;

    public InternalFullTokenMaker(AntlrFullTokenMaker antlrFullTokenMaker) {
      this.antlrFullTokenMaker = antlrFullTokenMaker;
    }

    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
      resetTokenList();
      Lexer lexer = antlrFullTokenMaker.createLexer(new String(text.array));
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
      } catch (AntlrException ignored) {
        // the Exception is used as control structure here, it breaks the loop on any arror
        // any remaining un-lexed text is added as a token right afterwards
      }
      int remainingTextCount = text.array.length - currentDocumentOffset;
      if (remainingTextCount > 0) {
        // mark the rest of the text as error
        addToken(
            text,
            currentArrayOffset,
            currentArrayOffset + remainingTextCount - 1,
            Token.ERROR_IDENTIFIER,
            currentDocumentOffset);
      }

      if (firstToken == null) {
        // make sure we always have a token
        addNullToken();
      }

      return firstToken;
    }
  }
}
