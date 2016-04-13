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

import java.util.List;

import com.qunar.flight.java.model.InternalSyntaxToken;
import com.qunar.flight.java.model.declaration.AnnotationTreeImpl;
import com.qunar.flight.java.model.declaration.VariableTreeImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class FormalParametersListTreeImpl extends ListTreeImpl<VariableTreeImpl> {

    private InternalSyntaxToken openParenToken;
    private InternalSyntaxToken closeParenToken;

    public FormalParametersListTreeImpl(InternalSyntaxToken openParenToken, InternalSyntaxToken closeParenToken) {
        super(JavaLexer.FORMAL_PARAMETERS, ImmutableList.<VariableTreeImpl> of());

        this.openParenToken = openParenToken;
        this.closeParenToken = closeParenToken;
    }

    public FormalParametersListTreeImpl(VariableTreeImpl variable) {
        super(JavaLexer.FORMAL_PARAMETERS, Lists.newArrayList(variable));
    }

    public FormalParametersListTreeImpl(List<AnnotationTreeImpl> annotations, InternalSyntaxToken ellipsisToken,
            VariableTreeImpl variable) {
        super(JavaLexer.FORMAL_PARAMETERS, Lists.newArrayList(variable));
    }

    public FormalParametersListTreeImpl complete(InternalSyntaxToken openParenToken, InternalSyntaxToken closeParenToken) {
        this.openParenToken = openParenToken;
        this.closeParenToken = closeParenToken;
        return this;
    }

    public InternalSyntaxToken openParenToken() {
        return openParenToken;
    }

    public InternalSyntaxToken closeParenToken() {
        return closeParenToken;
    }

}