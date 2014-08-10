// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirInterpolation extends PsiElement {

  @NotNull
  List<ElixirHeredoc> getHeredocList();

  @NotNull
  List<ElixirInterpolatedHeredoc> getInterpolatedHeredocList();

  @NotNull
  List<ElixirInterpolatedString> getInterpolatedStringList();

  @NotNull
  List<ElixirString> getStringList();

}
