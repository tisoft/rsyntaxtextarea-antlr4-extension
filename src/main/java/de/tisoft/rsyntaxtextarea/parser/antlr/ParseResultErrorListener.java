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

package de.tisoft.rsyntaxtextarea.parser.antlr;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

public class ParseResultErrorListener extends BaseErrorListener {
  private final ParseResult parseResult;

  public ParseResultErrorListener(ParseResult parseResult) {
    this.parseResult = parseResult;
  }

  @Override
  public void syntaxError(
      Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line,
      int charPositionInLine,
      String msg,
      RecognitionException e) {
    int startIndex = -1;
    int length = -1;

    if (offendingSymbol instanceof Token) {
      startIndex = ((Token) offendingSymbol).getStartIndex();
      length = ((Token) offendingSymbol).getStopIndex() - startIndex + 1;
    } else if (e != null) {
      if (e.getOffendingToken() != null) {
        startIndex = e.getOffendingToken().getStartIndex();
        length = e.getOffendingToken().getStopIndex() - startIndex + 1;
      } else if (e instanceof LexerNoViableAltException) {
        startIndex = ((LexerNoViableAltException) e).getStartIndex();
        length = 1;
      }
    }

    // if length is 0, we can't highlight a character, so the whole line needs to be highlighted
    if (length == 0) {
      startIndex = -1;
      length = -1;
    }

    parseResult
        .getNotices()
        .add(new DefaultParserNotice(parseResult.getParser(), msg, line - 1, startIndex, length));
  }
}
