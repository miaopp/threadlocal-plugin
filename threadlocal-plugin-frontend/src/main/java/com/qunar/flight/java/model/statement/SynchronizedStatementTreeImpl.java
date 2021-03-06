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
import com.qunar.flight.plugins.java.api.tree.BlockTree;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.SynchronizedStatementTree;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class SynchronizedStatementTreeImpl extends JavaTree implements SynchronizedStatementTree {
    private final ExpressionTree expression;
    private final BlockTree block;
    private final InternalSyntaxToken synchronizedKeyword;
    private final InternalSyntaxToken openParenToken;
    private final InternalSyntaxToken closeParenToken;

    public SynchronizedStatementTreeImpl(InternalSyntaxToken synchronizedKeyword, InternalSyntaxToken openParenToken,
            ExpressionTree expression, InternalSyntaxToken closeParenToken, BlockTreeImpl block) {
        super(Kind.SYNCHRONIZED_STATEMENT);
        this.expression = Preconditions.checkNotNull(expression);
        this.block = Preconditions.checkNotNull(block);
        this.synchronizedKeyword = synchronizedKeyword;
        this.openParenToken = openParenToken;
        this.closeParenToken = closeParenToken;
    }

    @Override
    public Kind kind() {
        return Kind.SYNCHRONIZED_STATEMENT;
    }

    @Override
    public SyntaxToken synchronizedKeyword() {
        return synchronizedKeyword;
    }

    @Override
    public SyntaxToken openParenToken() {
        return openParenToken;
    }

    @Override
    public ExpressionTree expression() {
        return expression;
    }

    @Override
    public SyntaxToken closeParenToken() {
        return closeParenToken;
    }

    @Override
    public BlockTree block() {
        return block;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitSynchronizedStatement(this);
    }

    @Override
    public Iterable<Tree> children() {
        return Lists.newArrayList(synchronizedKeyword, openParenToken, expression, closeParenToken, block);
    }

}
