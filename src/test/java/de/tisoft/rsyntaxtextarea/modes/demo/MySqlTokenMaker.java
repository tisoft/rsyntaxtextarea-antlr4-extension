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
import third_party.MySqlLexer;

public class MySqlTokenMaker extends AntlrTokenMaker {
  public MySqlTokenMaker() {
    super(
        new MultiLineTokenInfo(0, Token.COMMENT_DOCUMENTATION, "/*!", "*/"),
        new MultiLineTokenInfo(0, Token.COMMENT_MULTILINE, "/*", "*/"));
  }

  @Override
  protected Lexer createLexer(String text) {
    return new MySqlLexer(CharStreams.fromString(text));
  }

  @Override
  protected int convertType(int type) {
    // fast path for keywords (they are neatly aligned)
    if (type >= MySqlLexer.ADD && type <= MySqlLexer.ZEROFILL) {
      return Token.RESERVED_WORD;
    }
    switch (type) {
      case MySqlLexer.LINE_COMMENT:
        return Token.COMMENT_EOL;
      case MySqlLexer.SPEC_MYSQL_COMMENT:
        return Token.COMMENT_DOCUMENTATION;
      case MySqlLexer.COMMENT_INPUT:
        return Token.COMMENT_MULTILINE;

      case MySqlLexer.DECIMAL_LITERAL:
        return Token.LITERAL_NUMBER_DECIMAL_INT;
      case MySqlLexer.REAL_LITERAL:
        return Token.LITERAL_NUMBER_FLOAT;
      case MySqlLexer.HEXADECIMAL_LITERAL:
        return Token.LITERAL_NUMBER_HEXADECIMAL;
      case MySqlLexer.STRING_LITERAL:
        return Token.LITERAL_STRING_DOUBLE_QUOTE;
    }
    return Token.IDENTIFIER;
  }

  @Override
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] {"-- ", null};
  }
}
