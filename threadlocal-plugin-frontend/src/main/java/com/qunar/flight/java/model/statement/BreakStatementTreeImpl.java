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

import java.util.Collections;

import javax.annotation.Nullable;

import com.qunar.flight.java.model.InternalSyntaxToken;
import com.qunar.flight.java.model.JavaTree;
import com.qunar.flight.java.model.expression.IdentifierTreeImpl;
import com.qunar.flight.plugins.java.api.tree.BreakStatementTree;
import com.qunar.flight.plugins.java.api.tree.IdentifierTree;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;

import com.google.common.collect.Iterables;

public class BreakStatementTreeImpl extends JavaTree implements BreakStatementTree {
    private final InternalSyntaxToken breakToken;
    @Nullable
    private final IdentifierTree label;
    private final InternalSyntaxToken semicolonToken;

    public BreakStatementTreeImpl(InternalSyntaxToken breakToken, @Nullable IdentifierTreeImpl label,
            InternalSyntaxToken semicolonToken) {
        super(Kind.BREAK_STATEMENT);
        this.breakToken = breakToken;
        this.label = label;
        this.semicolonToken = semicolonToken;
    }

    @Override
    public Kind kind() {
        return Kind.BREAK_STATEMENT;
    }

    @Override
    public SyntaxToken breakKeyword() {
        return breakToken;
    }

    @Nullable
    @Override
    public IdentifierTree label() {
        return label;
    }

    @Override
    public SyntaxToken semicolonToken() {
        return semicolonToken;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitBreakStatement(this);
    }

    @Override
    public Iterable<Tree> children() {
        return Iterables.concat(Collections.singletonList(breakToken), label != null ? Collections.singletonList(label)
                : Collections.<Tree> emptyList(), Collections.<Tree> singletonList(semicolonToken));
    }

}
