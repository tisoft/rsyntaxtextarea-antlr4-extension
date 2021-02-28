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
