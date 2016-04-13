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
import com.qunar.flight.plugins.java.api.tree.AssignmentExpressionTree;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class AssignmentExpressionTreeImpl extends AbstractTypedTree implements AssignmentExpressionTree {

    private final Kind kind;

    private final ExpressionTree variable;
    private final InternalSyntaxToken operatorToken;
    private final ExpressionTree expression;

    public AssignmentExpressionTreeImpl(Kind kind, ExpressionTree variable, InternalSyntaxToken operatorToken,
            ExpressionTree expression) {
        super(kind);
        this.kind = kind;

        this.variable = variable;
        this.operatorToken = operatorToken;
        this.expression = Preconditions.checkNotNull(expression);
    }

    @Override
    public Kind kind() {
        return kind;
    }

    @Override
    public ExpressionTree variable() {
        return variable;
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
        visitor.visitAssignmentExpression(this);
    }

    @Override
    public Iterable<Tree> children() {
        return Lists.newArrayList(variable, operatorToken, expression);
    }

}
