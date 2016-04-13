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

import com.qunar.flight.java.ast.parser.BoundListTreeImpl;
import com.qunar.flight.java.model.AbstractTypedTree;
import com.qunar.flight.java.model.InternalSyntaxToken;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.ListTree;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;
import com.qunar.flight.plugins.java.api.tree.TypeCastTree;
import com.qunar.flight.plugins.java.api.tree.TypeTree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class TypeCastExpressionTreeImpl extends AbstractTypedTree implements TypeCastTree {

    private InternalSyntaxToken openParenToken;
    private final TypeTree type;
    @Nullable
    private final InternalSyntaxToken andToken;
    private final ListTree<Tree> bounds;
    private final InternalSyntaxToken closeParenToken;
    private final ExpressionTree expression;

    public TypeCastExpressionTreeImpl(TypeTree type, InternalSyntaxToken closeParenToken, ExpressionTree expression) {
        super(Kind.TYPE_CAST);
        this.type = Preconditions.checkNotNull(type);
        this.bounds = BoundListTreeImpl.emptyList();
        this.closeParenToken = closeParenToken;
        this.expression = Preconditions.checkNotNull(expression);
        andToken = null;
    }

    public TypeCastExpressionTreeImpl(TypeTree type, InternalSyntaxToken andToken, ListTree<Tree> bounds,
            InternalSyntaxToken closeParenToken, ExpressionTree expression) {
        super(Kind.TYPE_CAST);
        this.type = Preconditions.checkNotNull(type);
        this.bounds = bounds;
        this.closeParenToken = closeParenToken;
        this.expression = Preconditions.checkNotNull(expression);
        this.andToken = andToken;
    }

    public TypeCastExpressionTreeImpl complete(InternalSyntaxToken openParenToken) {
        Preconditions.checkState(this.openParenToken == null && closeParenToken != null);
        this.openParenToken = openParenToken;

        return this;
    }

    @Override
    public Kind kind() {
        return Kind.TYPE_CAST;
    }

    @Override
    public SyntaxToken openParenToken() {
        return openParenToken;
    }

    @Override
    public TypeTree type() {
        return type;
    }

    @Nullable
    @Override
    public SyntaxToken andToken() {
        return andToken;
    }

    @Override
    public ListTree<Tree> bounds() {
        return bounds;
    }

    @Override
    public SyntaxToken closeParenToken() {
        return closeParenToken;
    }

    @Override
    public ExpressionTree expression() {
        return expression;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitTypeCast(this);
    }

    @Override
    public Iterable<Tree> children() {
        return Iterables.concat(Lists.newArrayList(openParenToken, type),
                andToken == null ? Collections.<Tree> emptyList() : Collections.singletonList(andToken()),
                Lists.newArrayList(bounds, closeParenToken, expression));
    }

}
