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
import de.tisoft.rsyntaxtextarea.modes.antlr.MultiLineTokenInfo;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.fife.ui.rsyntaxtextarea.Token;
import third_party.GoLexer;

public class GoTokenMaker extends AntlrTokenMaker {
  public GoTokenMaker() {
    super(new MultiLineTokenInfo(0, Token.ERROR_STRING_DOUBLE, "`", "`"));
  }

  @Override
  protected Lexer createLexer(String text) {
    return new GoLexer(CharStreams.fromString(text)) {
      @Override
      public void skip() {
        setChannel(HIDDEN);
      }
    };
  }

  @Override
  protected int convertType(int type) {
    if (type >= GoLexer.BREAK && type <= GoLexer.NIL_LIT) {
      return Token.RESERVED_WORD;
    }

    switch (type) {
      case GoLexer.LINE_COMMENT:
        return Token.COMMENT_EOL;
      case GoLexer.COMMENT:
        return Token.COMMENT_MULTILINE;
      case GoLexer.DECIMAL_LIT:
        return Token.LITERAL_NUMBER_DECIMAL_INT;
      case GoLexer.FLOAT_LIT:
        return Token.LITERAL_NUMBER_FLOAT;
      case GoLexer.HEX_LIT:
        return Token.LITERAL_NUMBER_HEXADECIMAL;
      case GoLexer.INTERPRETED_STRING_LIT:
        return Token.LITERAL_STRING_DOUBLE_QUOTE;
      case GoLexer.RAW_STRING_LIT:
        return Token.ERROR_STRING_DOUBLE;
      case GoLexer.IDENTIFIER:
        return Token.IDENTIFIER;
      case GoLexer.L_BRACKET:
      case GoLexer.R_BRACKET:
      case GoLexer.L_PAREN:
      case GoLexer.R_PAREN:
      case GoLexer.L_CURLY:
      case GoLexer.R_CURLY:
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
