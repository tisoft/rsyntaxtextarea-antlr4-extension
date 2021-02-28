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
import third_party.Java9Lexer;

public class Java9TokenMaker extends AntlrTokenMaker {
  public Java9TokenMaker() {
    super(new MultiLineTokenInfo(0, Token.COMMENT_MULTILINE, "/*", "*/"));
  }

  @Override
  protected Lexer createLexer(String text) {
    return new Java9Lexer(CharStreams.fromString(text)) {
      @Override
      public void skip() {
        setChannel(HIDDEN);
      }
    };
  }

  @Override
  protected int convertType(int type) {
    switch (type) {
      case Java9Lexer.LINE_COMMENT:
        return Token.COMMENT_EOL;
      case Java9Lexer.COMMENT:
        return Token.COMMENT_MULTILINE;
      case Java9Lexer.BooleanLiteral:
        return Token.LITERAL_BOOLEAN;
      case Java9Lexer.CharacterLiteral:
        return Token.LITERAL_CHAR;
      case Java9Lexer.FloatingPointLiteral:
        return Token.LITERAL_NUMBER_FLOAT;
      case Java9Lexer.IntegerLiteral:
        return Token.LITERAL_NUMBER_DECIMAL_INT;
      case Java9Lexer.NullLiteral:
        return Token.RESERVED_WORD;
      case Java9Lexer.StringLiteral:
        return Token.LITERAL_STRING_DOUBLE_QUOTE;
      case Java9Lexer.Identifier:
        return Token.IDENTIFIER;
      case Java9Lexer.LBRACK:
      case Java9Lexer.RBRACK:
      case Java9Lexer.LPAREN:
      case Java9Lexer.RPAREN:
      case Java9Lexer.LBRACE:
      case Java9Lexer.RBRACE:
        return Token.SEPARATOR;
    }
    return Token.IDENTIFIER;
  }

  @Override
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] {"//", null};
  }

  @Override
  public boolean getCurlyBracesDenoteCodeBlocks(int languageIndex) {
    return true;
  }
}
