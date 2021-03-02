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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.tisoft.rsyntaxtextarea.modes.demo.XMLAntlrParser;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AntlrParserBaseTest {

  private static final DelegatingParser parser = new DelegatingParser();

  @BeforeAll
  static void addParsers() {
    parser.addParser("text/xml", new XMLAntlrParser());
  }

  @Test
  void testParseValidXML() throws BadLocationException {
    RSyntaxDocument document = new RSyntaxDocument("text/xml");
    document.insertString(0, "<xml name=\"test\">text</xml>", null);
    ParseResult result = parser.parse(document, "text/xml");
    assertNull(result.getError());
    assertTrue(result.getNotices().isEmpty());
  }

  @Test
  void testParseXMLMismatchedInput() throws BadLocationException {
    RSyntaxDocument document = new RSyntaxDocument("text/xml");
    document.insertString(0, "<xml name=\"test\">te", null);
    ParseResult result = parser.parse(document, "text/xml");
    assertNull(result.getError());
    assertEquals(
        "mismatched input '<EOF>' expecting {COMMENT, CDATA, EntityRef, CharRef, '<', PI}",
        result.getNotices().get(0).getMessage());
  }

  @Test
  void testParseInvalidXMLTokenRecognitionError() throws BadLocationException {
    RSyntaxDocument document = new RSyntaxDocument("text/xml");
    document.insertString(0, "<name <name", null);
    ParseResult result = parser.parse(document, "text/xml");
    assertNull(result.getError());
    assertEquals("token recognition error at: '<'", result.getNotices().get(0).getMessage());
  }
}
