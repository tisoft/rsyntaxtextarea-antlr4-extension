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
import third_party.Python3Lexer;
import third_party.Python3Parser;

public class Python3AntlrParser extends AntlrParserBase<Python3Lexer, Python3Parser> {
  @Override
  protected Python3Lexer createLexer(CharStream input) {
    return new Python3Lexer(input);
  }

  @Override
  protected Python3Parser createParser(TokenStream input) {
    return new Python3Parser(input);
  }

  @Override
  protected void parse(Python3Parser parser) {
    parser.file_input();
  }
}
