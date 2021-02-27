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

import javax.swing.text.BadLocationException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

public abstract class AntlrParserBase<L extends Lexer, P extends Parser> extends AbstractParser {

  protected abstract L createLexer(CharStream input);

  protected abstract P createParser(TokenStream input);

  protected abstract void parse(P parser);

  @Override
  public ParseResult parse(RSyntaxDocument doc, String style) {
    DefaultParseResult parseResult = new DefaultParseResult(this);
    parseResult.setParsedLines(0, doc.getDefaultRootElement().getElementCount());
    long start = System.currentTimeMillis();
    try {
      L lexer = createLexer(CharStreams.fromString(doc.getText(0, doc.getLength())));
      lexer.removeErrorListeners();
      lexer.addErrorListener(new ParseResultErrorListener(parseResult));
      P parser = createParser(new CommonTokenStream(lexer));
      parser.removeErrorListeners();
      parser.addErrorListener(new ParseResultErrorListener(parseResult));
      parse(parser);
    } catch (BadLocationException e) {
      parseResult.setError(e);
    }
    parseResult.setParseTime(System.currentTimeMillis() - start);
    return parseResult;
  }
}
