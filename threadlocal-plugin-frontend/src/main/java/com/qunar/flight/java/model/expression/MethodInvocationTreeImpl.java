/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.model.expression;

import java.util.Collections;

import javax.annotation.Nullable;

import com.qunar.flight.java.ast.parser.ArgumentListTreeImpl;
import com.qunar.flight.java.model.AbstractTypedTree;
import com.qunar.flight.java.resolve.Symbols;
import com.qunar.flight.plugins.java.api.semantic.Symbol;
import com.qunar.flight.plugins.java.api.tree.Arguments;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.MethodInvocationTree;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;
import com.qunar.flight.plugins.java.api.tree.TypeArguments;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MethodInvocationTreeImpl extends AbstractTypedTree implements MethodInvocationTree {

    private final ExpressionTree methodSelect;
    private final Arguments arguments;
    @Nullable
    private TypeArguments typeArguments;
    private Symbol symbol = Symbols.unknownSymbol;

    public MethodInvocationTreeImpl(ExpressionTree methodSelect, @Nullable TypeArguments typeArguments,
            ArgumentListTreeImpl arguments) {
        super(Kind.METHOD_INVOCATION);
        this.methodSelect = Preconditions.checkNotNull(methodSelect);
        this.typeArguments = typeArguments;
        this.arguments = Preconditions.checkNotNull(arguments);
    }

    @Override
    public Kind kind() {
        return Kind.METHOD_INVOCATION;
    }

    @Nullable
    @Override
    public TypeArguments typeArguments() {
        return typeArguments;
    }

    @Override
    public ExpressionTree methodSelect() {
        return methodSelect;
    }

    @Override
    public Arguments arguments() {
        return arguments;
    }

    @Override
    public Symbol symbol() {
        return symbol;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitMethodInvocation(this);
    }

    @Override
    public Iterable<Tree> children() {
        return Iterables.concat(
                typeArguments != null ? Collections.singletonList(typeArguments) : Collections.<Tree> emptyList(),
                Lists.newArrayList(methodSelect, arguments));
    }

    public void setSymbol(Symbol symbol) {
        Preconditions.checkState(this.symbol.equals(Symbols.unknownSymbol));
        this.symbol = symbol;
    }
}
