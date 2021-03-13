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

import java.util.HashMap;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

public class DelegatingParser extends AbstractParser {
  private final HashMap<String, Parser> parserMap = new HashMap<>();

  @Override
  public ParseResult parse(RSyntaxDocument doc, String style) {
    final DefaultParseResult result = new DefaultParseResult(this);
    Parser parser = parserMap.get(style);
    if (parser != null) {
      // The result must reference this parser, not the delegated one
      ParseResult delegatedResult = parser.parse(doc, style);
      result.setError(delegatedResult.getError());
      result.setParsedLines(result.getFirstLineParsed(), result.getLastLineParsed());
      result.setParseTime(delegatedResult.getParseTime());

      delegatedResult.getNotices().stream()
          .map(
              n -> {
                // The notice must reference this parser, not the delegated one
                final DefaultParserNotice notice =
                    new DefaultParserNotice(
                        this, n.getMessage(), n.getLine(), n.getOffset(), n.getLength());
                notice.setShowInEditor(n.getShowInEditor());
                notice.setColor(n.getColor());
                notice.setLevel(n.getLevel());
                notice.setToolTipText(n.getToolTipText());
                return notice;
              })
          .forEach(result::addNotice);
    }
    return result;
  }

  public void addParser(String style, Parser parser) {
    parserMap.put(style, parser);
  }
}
