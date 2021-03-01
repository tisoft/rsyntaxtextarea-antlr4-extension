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

package de.tisoft.rsyntaxtextarea.modes.antlr;

public class MultiLineTokenInfo {
  final int languageIndex;

  final int token;

  final String tokenStart;

  final String tokenEnd;

  public MultiLineTokenInfo(int languageIndex, int token, String tokenStart, String tokenEnd) {
    this.languageIndex = languageIndex;
    this.token = token;
    this.tokenStart = tokenStart;
    this.tokenEnd = tokenEnd;
  }
}