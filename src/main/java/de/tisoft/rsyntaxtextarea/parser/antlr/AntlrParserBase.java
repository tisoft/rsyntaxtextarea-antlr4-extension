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
