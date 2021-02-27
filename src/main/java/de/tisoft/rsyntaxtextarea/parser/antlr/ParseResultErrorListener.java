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
