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

package de.tisoft.rsyntaxtextarea.modes.demo;

import de.tisoft.rsyntaxtextarea.modes.antlr.AntlrTokenMaker;
import de.tisoft.rsyntaxtextarea.modes.antlr.MultiLineTokenInfo;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.fife.ui.rsyntaxtextarea.Token;
import third_party.XMLLexer;

public class XMLTokenMaker extends AntlrTokenMaker {

  public XMLTokenMaker() {
    super(new MultiLineTokenInfo(0, Token.MARKUP_COMMENT, "<!--", "-->"));
  }

  @Override
  protected Lexer createLexer(String text) {
    return new XMLLexer(CharStreams.fromString(text)) {
      @Override
      public void skip() {
        setChannel(HIDDEN);
      }
    };
  }

  @Override
  protected int convertType(int type) {
    switch (type) {
      case XMLLexer.COMMENT:
        return Token.MARKUP_COMMENT;
      case XMLLexer.STRING:
        return Token.MARKUP_TAG_ATTRIBUTE_VALUE;
      case XMLLexer.OPEN:
      case XMLLexer.XMLDeclOpen:
      case XMLLexer.CLOSE:
      case XMLLexer.SLASH:
      case XMLLexer.SLASH_CLOSE:
      case XMLLexer.SPECIAL_CLOSE:
        return Token.MARKUP_TAG_DELIMITER;
      case XMLLexer.Name:
        return Token.MARKUP_TAG_NAME;
      case XMLLexer.EQUALS:
        return Token.OPERATOR;
      case XMLLexer.CDATA:
        return Token.MARKUP_CDATA;
      case XMLLexer.DTD:
        return Token.MARKUP_DTD;
      case XMLLexer.PI:
        return Token.MARKUP_PROCESSING_INSTRUCTION;
      case XMLLexer.SEA_WS:
      case XMLLexer.S:
        return Token.WHITESPACE;
      case XMLLexer.EntityRef:
      case XMLLexer.CharRef:
        return Token.MARKUP_ENTITY_REFERENCE;
    }
    return Token.IDENTIFIER;
  }

  @Override
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] {"<!--", "-->"};
  }
}
