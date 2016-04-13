/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.model.statement;

import com.qunar.flight.java.model.InternalSyntaxToken;
import com.qunar.flight.java.model.JavaTree;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;
import com.qunar.flight.plugins.java.api.tree.ThrowStatementTree;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class ThrowStatementTreeImpl extends JavaTree implements ThrowStatementTree {
    private final InternalSyntaxToken throwKeyword;
    private final ExpressionTree expression;
    private final InternalSyntaxToken semicolonToken;

    public ThrowStatementTreeImpl(InternalSyntaxToken throwKeyword, ExpressionTree expression,
            InternalSyntaxToken semicolonToken) {
        super(Kind.THROW_STATEMENT);
        this.throwKeyword = throwKeyword;
        this.expression = Preconditions.checkNotNull(expression);
        this.semicolonToken = semicolonToken;
    }

    @Override
    public Kind kind() {
        return Kind.THROW_STATEMENT;
    }

    @Override
    public SyntaxToken throwKeyword() {
        return throwKeyword;
    }

    @Override
    public ExpressionTree expression() {
        return expression;
    }

    @Override
    public SyntaxToken semicolonToken() {
        return semicolonToken;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitThrowStatement(this);
    }

    @Override
    public Iterable<Tree> children() {
        return Lists.newArrayList(throwKeyword, expression, semicolonToken);
    }

}