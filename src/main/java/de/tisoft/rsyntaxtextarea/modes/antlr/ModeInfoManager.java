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
