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
import com.qunar.flight.plugins.java.api.tree.ArrayAccessExpressionTree;
import com.qunar.flight.plugins.java.api.tree.ArrayDimensionTree;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;

import com.google.common.collect.Lists;

public class ArrayAccessExpressionTreeImpl extends AbstractTypedTree implements ArrayAccessExpressionTree {

    private ExpressionTree expression;
    private final ArrayDimensionTree dimension;

    public ArrayAccessExpressionTreeImpl(ArrayDimensionTree dimension) {
        super(Kind.ARRAY_ACCESS_EXPRESSION);
        this.dimension = dimension;
    }

    public ArrayAccessExpressionTreeImpl complete(ExpressionTree expression) {
        this.expression = expression;
        return this;
    }

    @Override
    public Kind kind() {
        return Kind.ARRAY_ACCESS_EXPRESSION;
    }

    @Override
    public ExpressionTree expression() {
        return expression;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitArrayAccessExpression(this);
    }

    @Override
    public ArrayDimensionTree dimension() {
        return dimension;
    }

    @Override
    public Iterable<Tree> children() {
        return Lists.newArrayList(expression, dimension);
    }
}
