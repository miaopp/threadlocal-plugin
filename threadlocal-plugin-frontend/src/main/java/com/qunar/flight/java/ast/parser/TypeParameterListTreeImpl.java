/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.ast.parser;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.qunar.flight.java.model.InternalSyntaxToken;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;
import com.qunar.flight.plugins.java.api.tree.TypeParameterTree;
import com.qunar.flight.plugins.java.api.tree.TypeParameters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class TypeParameterListTreeImpl extends ListTreeImpl<TypeParameterTree> implements TypeParameters {

    @Nullable
    private final InternalSyntaxToken openBracketToken;
    @Nullable
    private final InternalSyntaxToken closeBracketToken;

    public TypeParameterListTreeImpl(InternalSyntaxToken openBracketToken, List<TypeParameterTree> typeParameters,
            List<SyntaxToken> separators, InternalSyntaxToken closeBracketToken) {
        super(JavaLexer.TYPE_PARAMETERS, typeParameters, separators);

        this.openBracketToken = openBracketToken;
        this.closeBracketToken = closeBracketToken;
    }

    public TypeParameterListTreeImpl() {
        super(JavaLexer.TYPE_PARAMETERS, ImmutableList.<TypeParameterTree> of(), ImmutableList.<SyntaxToken> of());
        this.openBracketToken = null;
        this.closeBracketToken = null;
    }

    @Nullable
    @Override
    public SyntaxToken openBracketToken() {
        return openBracketToken;
    }

    @Nullable
    @Override
    public SyntaxToken closeBracketToken() {
        return closeBracketToken;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitTypeParameters(this);
    }

    @Override
    public Iterable<Tree> children() {
        return Iterables.concat(Collections.singletonList(openBracketToken), super.children(),
                Collections.singletonList(closeBracketToken));
    }

    @Override
    public Kind kind() {
        return Kind.TYPE_PARAMETERS;
    }

}
