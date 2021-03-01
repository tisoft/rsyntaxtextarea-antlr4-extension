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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.IntegerStack;

class ModeInfoManager {
  private int nextModeInternalToken = -1;

  private final Map<Integer, ModeInfo> tokenToModeInfo = new HashMap<>();
  private final Map<ModeInfo, Integer> modeInfoToToken = new HashMap<>();

  static final class ModeInfo {
    final int tokenType;
    final int currentMode;
    final IntegerStack modeStack;

    ModeInfo(int tokenType, int currentMode, IntegerStack modeStack) {
      this.tokenType = tokenType;
      this.currentMode = currentMode;
      this.modeStack = modeStack;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ModeInfo modeInfo = (ModeInfo) o;
      return tokenType == modeInfo.tokenType
          && currentMode == modeInfo.currentMode
          && modeStack.equals(modeInfo.modeStack);
    }

    @Override
    public int hashCode() {
      return Objects.hash(tokenType, currentMode, modeStack);
    }
  }

  ModeInfoManager.ModeInfo getModeInfo(int initialTokenType) {
    ModeInfo modeInfo;
    if (initialTokenType < 0) {
      // extract modes
      modeInfo = tokenToModeInfo.get(initialTokenType);
    } else {
      modeInfo = new ModeInfo(initialTokenType, Lexer.DEFAULT_MODE, new IntegerStack());
    }
    return modeInfo;
  }

  int storeModeInfo(int currentType, int currentMode, IntegerStack modeStack) {
    ModeInfo modeInfo = new ModeInfo(currentType, currentMode, new IntegerStack(modeStack));
    Integer token = modeInfoToToken.get(modeInfo);
    if (token != null) {
      return token;
    } else {
      if (nextModeInternalToken > 0) {
        // overflow, we can't store anymore variations of ModeInfos
        throw new ArrayIndexOutOfBoundsException(nextModeInternalToken);
      }
      tokenToModeInfo.put(nextModeInternalToken, modeInfo);
      modeInfoToToken.put(modeInfo, nextModeInternalToken);
      return nextModeInternalToken--;
    }
  }
}
