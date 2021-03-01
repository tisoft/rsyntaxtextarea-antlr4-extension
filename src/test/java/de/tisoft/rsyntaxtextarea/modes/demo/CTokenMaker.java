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
