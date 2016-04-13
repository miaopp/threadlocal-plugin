/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.model.declaration;

import com.qunar.flight.java.model.InternalSyntaxToken;
import com.qunar.flight.plugins.java.api.tree.Modifier;
import com.qunar.flight.plugins.java.api.tree.ModifierKeywordTree;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;

public class ModifierKeywordTreeImpl extends InternalSyntaxToken implements ModifierKeywordTree {

    private final Modifier modifier;

    public ModifierKeywordTreeImpl(Modifier modifier, InternalSyntaxToken internalSyntaxToken) {
        super(internalSyntaxToken);
        this.modifier = modifier;
    }

    @Override
    public Modifier modifier() {
        return modifier;
    }

    @Override
    public SyntaxToken keyword() {
        return this;
    }
}
