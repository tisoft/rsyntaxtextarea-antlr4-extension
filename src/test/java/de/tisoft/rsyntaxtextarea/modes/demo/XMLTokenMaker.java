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

package de.tisoft.rsyntaxtextarea.modes.demo;

import de.tisoft.rsyntaxtextarea.modes.antlr.AntlrTokenMaker;
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
