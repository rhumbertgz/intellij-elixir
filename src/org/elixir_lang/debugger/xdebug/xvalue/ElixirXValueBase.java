/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
 * Copyright 2017 Luke Imhoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elixir_lang.debugger.xdebug.xvalue;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import org.elixir_lang.debugger.ElixirXValuePresentation;
import org.intellij.erlang.icons.ErlangIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class ElixirXValueBase<T extends OtpErlangObject> extends XValue {
  private final T myValue;
  private final int myChildrenCount;
  private int myNextChildIdxToCompute;

  ElixirXValueBase(T value) {
    this(value, 0);
  }

  ElixirXValueBase(T value, int childrenCount) {
    myValue = value;
    myChildrenCount = childrenCount;
  }

  T getValue() {
    return myValue;
  }

  @Override
  public void computeChildren(@NotNull XCompositeNode node) {
    int nextToLastChildIdx = Math.min(myNextChildIdxToCompute + XCompositeNode.MAX_CHILDREN_TO_SHOW, myChildrenCount);
    XValueChildrenList children = new XValueChildrenList(nextToLastChildIdx - myNextChildIdxToCompute);
    for (int i = myNextChildIdxToCompute; i < nextToLastChildIdx; i++) {
      computeChild(children, i);
    }
    myNextChildIdxToCompute = nextToLastChildIdx;
    boolean computedAllChildren = myNextChildIdxToCompute == myChildrenCount;
    if (!computedAllChildren) {
      node.tooManyChildren(myChildrenCount - myNextChildIdxToCompute);
    }
    node.addChildren(children, computedAllChildren);
  }

  @Override
  public final void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
    XValuePresentation presentation = getPresentation(node, place);
    if (presentation != null) {
      node.setPresentation(getIcon(), presentation, hasChildren());
    }
    else {
      String repr = getStringRepr();
      if (repr.length() > XValueNode.MAX_VALUE_LENGTH) {
        node.setFullValueEvaluator(new ImmediateFullValueEvaluator(repr));
        repr = repr.substring(0, XValueNode.MAX_VALUE_LENGTH - 3) + "...";
      }
      node.setPresentation(getIcon(), getType(), repr, hasChildren());
    }
  }

  @Override
  public boolean canNavigateToSource() {
    return false;
  }

  void computeChild(XValueChildrenList children, int childIdx) {
  }

  @Nullable
  XValuePresentation getPresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
    return new ElixirXValuePresentation(getValue());
  }

  @Nullable
  String getType() {
    return null;
  }

  @NotNull
  String getStringRepr() {
    return getValue().toString();
  }

  Icon getIcon() {
    return ErlangIcons.DEBUGGER_VALUE;
  }

  private boolean hasChildren() {
    return myChildrenCount != 0;
  }

  static void addIndexedChild(@NotNull XValueChildrenList childrenList, long numericChild, int childIdx) {
    addIndexedChild(childrenList, new OtpErlangLong(numericChild), childIdx);
  }

  static void addIndexedChild(@NotNull XValueChildrenList childrenList, OtpErlangObject child, int childIdx) {
    addIndexedChild(childrenList, ElixirXValueFactory.create(child), childIdx);
  }

  static void addIndexedChild(@NotNull XValueChildrenList childrenList, @NotNull XValue child, int childIdx) {
    addNamedChild(childrenList, child, "[" + (childIdx + 1) + "]");
  }

  static void addNamedChild(@NotNull XValueChildrenList childrenList, long numericChild, String name) {
    addNamedChild(childrenList, new OtpErlangLong(numericChild), name);
  }

  static void addNamedChild(@NotNull XValueChildrenList childrenList,
                            @NotNull String atomicChild,
                            @SuppressWarnings("SameParameterValue") String name) {
    addNamedChild(childrenList, new OtpErlangAtom(atomicChild), name);
  }

  static void addNamedChild(@NotNull XValueChildrenList childrenList, OtpErlangObject child, String name) {
    addNamedChild(childrenList, ElixirXValueFactory.create(child), name);
  }

  private static void addNamedChild(@NotNull XValueChildrenList childrenList, @NotNull XValue child, String name) {
    childrenList.add(name, child);
  }
}

class ElixirPrimitiveXValueBase<T extends OtpErlangObject> extends ElixirXValueBase<T> {
  ElixirPrimitiveXValueBase(T value) {
    super(value);
  }

  @Override
  protected Icon getIcon() {
    return ErlangIcons.DEBUGGER_PRIMITIVE_VALUE;
  }
}

class ElixirArrayXValueBase<T extends OtpErlangObject> extends ElixirXValueBase<T> {
  ElixirArrayXValueBase(T value, int childrenCount) {
    super(value, childrenCount);
  }

  @Override
  protected Icon getIcon() {
    return ErlangIcons.DEBUGGER_ARRAY;
  }
}
