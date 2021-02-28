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
import third_party.AssemblerLexer;
import third_party.AssemblerParser;

public class AssemblerTokenMaker extends AntlrTokenMaker {
  @Override
  protected Lexer createLexer(String text) {
    return new AssemblerLexer(CharStreams.fromString(text)) {
      @Override
      public void skip() {
        setChannel(HIDDEN);
      }
    };
  }

  @Override
  protected int convertType(int type) {
    switch (type) {
      case AssemblerParser.OPCODE:
        return Token.OPERATOR;
      case AssemblerParser.COMMENT:
        return Token.COMMENT_EOL;
      case AssemblerParser.INT:
        return Token.LITERAL_NUMBER_DECIMAL_INT;
      case AssemblerParser.HEX:
        return Token.LITERAL_NUMBER_HEXADECIMAL;
      case AssemblerParser.OCT:
        return Token.LITERAL_NUMBER_FLOAT;
      case AssemblerParser.BIN:
        return Token.LITERAL_BOOLEAN;
      case AssemblerParser.STRING:
        return Token.LITERAL_STRING_DOUBLE_QUOTE;
      case AssemblerParser.CHAR:
        return Token.LITERAL_CHAR;
      case AssemblerParser.DIRECTIVE:
        return Token.RESERVED_WORD;
      case AssemblerParser.ID:
        return Token.VARIABLE;
    }
    return Token.IDENTIFIER;
  }

  @Override
  public String[] getLineCommentStartAndEnd(int languageIndex) {
    return new String[] {";", null};
  }
}