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
import third_party.CLexer;

public class CTokenMaker extends AntlrTokenMaker {
  public CTokenMaker() {
    super(new MultiLineTokenInfo(0, Token.COMMENT_MULTILINE, "/*", "*/"));
  }

  @Override
  protected Lexer createLexer(String text) {
    return new CLexer(CharStreams.fromString(text)) {
      @Override
      public void skip() {
        setChannel(HIDDEN);
      }
    };
  }

  @Override
  protected int convertType(int type) {
    if (type >= CLexer.Auto && type <= CLexer.While) {
      return Token.RESERVED_WORD;
    }
    if (type >= CLexer.Less && type <= CLexer.Tilde) {
      return Token.OPERATOR;
    }
    switch (type) {
      case CLexer.IncludeDirective:
      case CLexer.ComplexDefine:
      case CLexer.LineAfterPreprocessing:
      case CLexer.LineDirective:
      case CLexer.PragmaDirective:
        return Token.ANNOTATION;
      case CLexer.LineComment:
        return Token.COMMENT_EOL;
      case CLexer.BlockComment:
        return Token.COMMENT_MULTILINE;
      case CLexer.Constant:
        return Token.LITERAL_NUMBER_DECIMAL_INT;
      case CLexer.StringLiteral:
        return Token.LITERAL_STRING_DOUBLE_QUOTE;
      case CLexer.Identifier:
        return Token.IDENTIFIER;
      case CLexer.LeftBracket:
      case CLexer.RightBracket:
      case CLexer.LeftParen:
      case CLexer.RightParen:
      case CLexer.LeftBrace:
      case CLexer.RightBrace:
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
