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
import third_party.GoLexer;
import third_party.GoParser;

public class GoAntlrParser extends AntlrParserBase<GoLexer, GoParser> {
  @Override
  protected GoLexer createLexer(CharStream input) {
    return new GoLexer(input);
  }

  @Override
  protected GoParser createParser(TokenStream input) {
    return new GoParser(input);
  }

  @Override
  protected void parse(GoParser parser) {
    parser.sourceFile();
  }
}
