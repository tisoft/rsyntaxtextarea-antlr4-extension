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
