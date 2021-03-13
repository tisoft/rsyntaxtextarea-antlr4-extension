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
