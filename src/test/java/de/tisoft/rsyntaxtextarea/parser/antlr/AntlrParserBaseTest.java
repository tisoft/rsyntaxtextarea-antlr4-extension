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
