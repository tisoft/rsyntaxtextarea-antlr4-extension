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

import de.tisoft.rsyntaxtextarea.parser.antlr.AntlrParserBase;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenStream;
import third_party.asm6502Lexer;
import third_party.asm6502Parser;

public class AssemblerAntlrParser extends AntlrParserBase<asm6502Lexer, asm6502Parser> {
  @Override
  protected asm6502Lexer createLexer(CharStream input) {
    return new asm6502Lexer(input);
  }

  @Override
  protected asm6502Parser createParser(TokenStream input) {
    return new asm6502Parser(input);
  }

  @Override
  protected void parse(asm6502Parser parser) {
    parser.prog();
  }
}
