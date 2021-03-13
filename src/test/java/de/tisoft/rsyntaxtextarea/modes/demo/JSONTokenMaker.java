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
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.fife.ui.rsyntaxtextarea.Token;
import third_party.JSONLexer;

public class JSONTokenMaker extends AntlrTokenMaker {
  @Override
  protected Lexer createLexer(String text) {
    return new JSONLexer(CharStreams.fromString(text)) {
      @Override
      public void skip() {
        setChannel(HIDDEN);
      }
    };
  }

  @Override
  protected int convertType(int type) {
    switch (type) {
      case JSONLexer.NUMBER:
        return Token.LITERAL_NUMBER_FLOAT;
      case JSONLexer.STRING:
        return Token.LITERAL_STRING_DOUBLE_QUOTE;
      case JSONLexer.T__0:
      case JSONLexer.T__1:
      case JSONLexer.T__2:
      case JSONLexer.T__3:
      case JSONLexer.T__4:
      case JSONLexer.T__5:
        return Token.SEPARATOR;
      case JSONLexer.T__6:
      case JSONLexer.T__7:
        return Token.LITERAL_BOOLEAN;
      case JSONLexer.T__8:
        return Token.RESERVED_WORD;
    }
    return Token.IDENTIFIER;
  }

  @Override
  public boolean getCurlyBracesDenoteCodeBlocks(int languageIndex) {
    return true;
  }
}
