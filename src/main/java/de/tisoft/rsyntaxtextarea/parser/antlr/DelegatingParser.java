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
