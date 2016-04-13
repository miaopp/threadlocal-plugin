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

import com.qunar.flight.java.model.AbstractTypedTree;
import com.qunar.flight.java.model.InternalSyntaxToken;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;
import com.qunar.flight.plugins.java.api.tree.UnaryExpressionTree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class InternalPostfixUnaryExpression extends AbstractTypedTree implements UnaryExpressionTree {

    private final Kind kind;
    private final ExpressionTree expression;
    private final InternalSyntaxToken operatorToken;

    public InternalPostfixUnaryExpression(Kind kind, ExpressionTree expression, InternalSyntaxToken operatorToken) {
        super(kind);
        this.kind = Preconditions.checkNotNull(kind);
        this.expression = Preconditions.checkNotNull(expression);
        this.operatorToken = operatorToken;
    }

    @Override
    public Kind kind() {
        return kind;
    }

    @Override
    public SyntaxToken operatorToken() {
        return operatorToken;
    }

    @Override
    public ExpressionTree expression() {
        return expression;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitUnaryExpression(this);
    }

    @Override
    public Iterable<Tree> children() {
        return Lists.newArrayList(expression, operatorToken);
    }

}
