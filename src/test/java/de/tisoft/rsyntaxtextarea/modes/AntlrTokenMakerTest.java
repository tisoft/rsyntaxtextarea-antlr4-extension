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

package de.tisoft.rsyntaxtextarea.modes;

import de.tisoft.rsyntaxtextarea.modes.antlr.AntlrTokenMaker;
import de.tisoft.rsyntaxtextarea.modes.antlr.MultiLineTokenInfo;
import de.tisoft.rsyntaxtextarea.modes.antlr.TestLexer;
import de.tisoft.rsyntaxtextarea.modes.demo.XMLTokenMaker;
import javax.swing.text.Segment;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AntlrTokenMakerTest {

  private Segment createSegment(String code) {
    return new Segment(code.toCharArray(), 0, code.length());
  }

  @Test
  public void testTokens() {
    Segment segment = createSegment("+ / * /* test */ /** javadoc */ \"short\" \"\"\"long\"\"\"");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.NULL, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.OPERATOR, t.getType());
    Assertions.assertEquals("+", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.WHITESPACE, t.getType());
    Assertions.assertEquals(" ", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.OPERATOR, t.getType());
    Assertions.assertEquals("/", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.WHITESPACE, t.getType());
    Assertions.assertEquals(" ", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.OPERATOR, t.getType());
    Assertions.assertEquals("*", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.WHITESPACE, t.getType());
    Assertions.assertEquals(" ", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.COMMENT_MULTILINE, t.getType());
    Assertions.assertEquals("/* test */", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.WHITESPACE, t.getType());
    Assertions.assertEquals(" ", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.COMMENT_DOCUMENTATION, t.getType());
    Assertions.assertEquals("/** javadoc */", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.WHITESPACE, t.getType());
    Assertions.assertEquals(" ", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, t.getType());
    Assertions.assertEquals("\"short\"", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.WHITESPACE, t.getType());
    Assertions.assertEquals(" ", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, t.getType());
    Assertions.assertEquals("\"\"\"long\"\"\"", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.NULL, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testEmpty() {
    Segment segment = createSegment("");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.NULL, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.NULL, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testEmptyInMultiline() {
    Segment segment = createSegment("");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.COMMENT_MULTILINE, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.COMMENT_MULTILINE, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteMultilineBegin() {
    Segment segment = createSegment("/* incomplete");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.NULL, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.COMMENT_MULTILINE, t.getType());
    Assertions.assertEquals("/* incomplete", t.getLexeme());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteMultilineMiddle() {
    Segment segment = createSegment("incomplete");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.COMMENT_MULTILINE, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.COMMENT_MULTILINE, t.getType());
    Assertions.assertEquals("incomplete", t.getLexeme());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteMultilineEnd() {
    Segment segment = createSegment("incomplete */");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.COMMENT_MULTILINE, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.COMMENT_MULTILINE, t.getType());
    Assertions.assertEquals("incomplete */", t.getLexeme());
    t = t.getNextToken();
    Assertions.assertEquals(TokenTypes.NULL, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteDocBegin() {
    Segment segment = createSegment("/** incomplete");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.NULL, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.COMMENT_DOCUMENTATION, t.getType());
    Assertions.assertEquals("/** incomplete", t.getLexeme());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteDocMiddle() {
    Segment segment = createSegment("incomplete");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.COMMENT_DOCUMENTATION, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.COMMENT_DOCUMENTATION, t.getType());
    Assertions.assertEquals("incomplete", t.getLexeme());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteDocEnd() {
    Segment segment = createSegment("incomplete */");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.COMMENT_DOCUMENTATION, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.COMMENT_DOCUMENTATION, t.getType());
    Assertions.assertEquals("incomplete */", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.NULL, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteLongStringBegin() {
    Segment segment = createSegment("\"\"\" incomplete");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.NULL, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, t.getType());
    Assertions.assertEquals("\"\"\" incomplete", t.getLexeme());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteLongStringMiddle() {
    Segment segment = createSegment("incomplete");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, t.getType());
    Assertions.assertEquals("incomplete", t.getLexeme());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testIncompleteLongStringEnd() {
    Segment segment = createSegment("incomplete \"\"\"");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, t.getType());
    Assertions.assertEquals("incomplete \"\"\"", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.NULL, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testLexerModeHandling() {
    Segment segment = createSegment("<xml name=\"test\"");
    TokenMaker tm = new XMLTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.NULL, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_DELIMITER, t.getType());
    Assertions.assertEquals("<", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_NAME, t.getType());
    Assertions.assertEquals("xml", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.WHITESPACE, t.getType());
    Assertions.assertEquals(" ", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_NAME, t.getType());
    Assertions.assertEquals("name", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.OPERATOR, t.getType());
    Assertions.assertEquals("=", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE, t.getType());
    Assertions.assertEquals("\"test\"", t.getLexeme());
    t = t.getNextToken();

    // internal token!
    Assertions.assertEquals(-1, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);

    // parse next line, the tag is still open
    segment = createSegment("name2=\"test\"");
    t = tm.getTokenList(segment, -1, 0);

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_NAME, t.getType());
    Assertions.assertEquals("name2", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.OPERATOR, t.getType());
    Assertions.assertEquals("=", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE, t.getType());
    Assertions.assertEquals("\"test\"", t.getLexeme());
    t = t.getNextToken();

    // same internal token!
    Assertions.assertEquals(-1, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);

    // parse next line, the tag is still open
    segment = createSegment("name3=\"test\">");
    t = tm.getTokenList(segment, -1, 0);

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_NAME, t.getType());
    Assertions.assertEquals("name3", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.OPERATOR, t.getType());
    Assertions.assertEquals("=", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE, t.getType());
    Assertions.assertEquals("\"test\"", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.MARKUP_TAG_DELIMITER, t.getType());
    Assertions.assertEquals(">", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(0, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  @Test
  public void testErrorHandling() {
    Segment segment = createSegment("+ invalid");
    TokenMaker tm = createTokenMaker();

    Token t = tm.getTokenList(segment, TokenTypes.NULL, 0);
    assertTokenText(segment, t);

    Assertions.assertEquals(TokenTypes.OPERATOR, t.getType());
    Assertions.assertEquals("+", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.WHITESPACE, t.getType());
    Assertions.assertEquals(" ", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.ERROR_IDENTIFIER, t.getType());
    Assertions.assertEquals("invalid", t.getLexeme());
    t = t.getNextToken();

    Assertions.assertEquals(TokenTypes.NULL, t.getType());
    t = t.getNextToken();
    Assertions.assertNull(t);
  }

  /**
   * asserts that the token sequence can be used to exactly reconstruct the source segment
   *
   * @param segment the source segment
   * @param startToken the token sequence
   */
  private void assertTokenText(Segment segment, Token startToken) {
    StringBuilder sb = new StringBuilder();
    for (Token t = startToken; t != null; t = t.getNextToken()) {
      if (t.isPaintable()) {
        sb.append(t.getLexeme());
      }
    }
    Assertions.assertEquals(segment.toString(), sb.toString());
  }

  protected TokenMaker createTokenMaker() {
    return new TestAntlrTokenMaker();
  }

  static class TestAntlrTokenMaker extends AntlrTokenMaker {
    public TestAntlrTokenMaker() {
      super(
          new MultiLineTokenInfo(0, TokenTypes.COMMENT_DOCUMENTATION, "/**", "*/"),
          new MultiLineTokenInfo(0, TokenTypes.COMMENT_MULTILINE, "/*", "*/"),
          new MultiLineTokenInfo(0, TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, "\"\"\"", "\"\"\""));
    }

    @Override
    protected int convertType(int type) {
      switch (type) {
        case TestLexer.T__0:
        case TestLexer.MUL:
        case TestLexer.DIV:
          return TokenTypes.OPERATOR;
        case TestLexer.STRING_LITERAL:
          return TokenTypes.LITERAL_STRING_DOUBLE_QUOTE;
        case TestLexer.COMMENT:
          return TokenTypes.COMMENT_MULTILINE;
        case TestLexer.COMMENT_DOC:
          return TokenTypes.COMMENT_DOCUMENTATION;
        case TestLexer.WS:
          return TokenTypes.WHITESPACE;
        default:
          throw new IllegalArgumentException("Unsupported type " + type);
      }
    }

    @Override
    protected Lexer createLexer(String text) {
      return new TestLexer(CharStreams.fromString(text)) {
        @Override
        public void skip() {
          // We need the skipped tokens in the stream, move them to the HIDDEN channel instead
          setChannel(Lexer.HIDDEN);
        }
      };
    }
  }
}
