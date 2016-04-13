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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.qunar.flight.java.ast.parser.QualifiedIdentifierListTreeImpl;
import com.qunar.flight.java.ast.parser.TypeParameterListTreeImpl;
import com.qunar.flight.java.model.InternalSyntaxToken;
import com.qunar.flight.java.model.JavaTree;
import com.qunar.flight.java.model.expression.IdentifierTreeImpl;
import com.qunar.flight.java.resolve.JavaSymbol;
import com.qunar.flight.java.resolve.Symbols;
import com.qunar.flight.plugins.java.api.semantic.Symbol;
import com.qunar.flight.plugins.java.api.tree.ClassTree;
import com.qunar.flight.plugins.java.api.tree.IdentifierTree;
import com.qunar.flight.plugins.java.api.tree.ListTree;
import com.qunar.flight.plugins.java.api.tree.ModifiersTree;
import com.qunar.flight.plugins.java.api.tree.SyntaxToken;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.TreeVisitor;
import com.qunar.flight.plugins.java.api.tree.TypeParameters;
import com.qunar.flight.plugins.java.api.tree.TypeTree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public class ClassTreeImpl extends JavaTree implements ClassTree {

    private final Kind kind;
    @Nullable
    private final SyntaxToken openBraceToken;
    private final List<Tree> members;
    @Nullable
    private final SyntaxToken closeBraceToken;
    private ModifiersTree modifiers;
    private SyntaxToken atToken;
    private SyntaxToken declarationKeyword;
    private IdentifierTree simpleName;
    private TypeParameters typeParameters;
    @Nullable
    private SyntaxToken extendsKeyword;
    @Nullable
    private TypeTree superClass;
    @Nullable
    private SyntaxToken implementsKeyword;
    private ListTree<TypeTree> superInterfaces;
    private JavaSymbol.TypeJavaSymbol symbol = Symbols.unknownSymbol;

    public ClassTreeImpl(Kind kind, SyntaxToken openBraceToken, List<Tree> members, SyntaxToken closeBraceToken) {
        super(kind);

        this.kind = kind;
        this.openBraceToken = openBraceToken;
        this.members = members;
        this.closeBraceToken = closeBraceToken;
        this.modifiers = ModifiersTreeImpl.emptyModifiers();
        this.typeParameters = new TypeParameterListTreeImpl();
        this.superInterfaces = QualifiedIdentifierListTreeImpl.emptyList();
    }

    public ClassTreeImpl(ModifiersTree modifiers, SyntaxToken openBraceToken, List<Tree> members,
            SyntaxToken closeBraceToken) {
        super(Kind.ANNOTATION_TYPE);
        this.kind = Preconditions.checkNotNull(Kind.ANNOTATION_TYPE);
        this.modifiers = modifiers;
        this.typeParameters = new TypeParameterListTreeImpl();
        this.superClass = null;
        this.superInterfaces = QualifiedIdentifierListTreeImpl.emptyList();
        this.openBraceToken = openBraceToken;
        this.members = Preconditions.checkNotNull(members);
        this.closeBraceToken = closeBraceToken;
    }

    public ClassTreeImpl completeModifiers(ModifiersTreeImpl modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public ClassTreeImpl completeIdentifier(IdentifierTree identifier) {
        this.simpleName = identifier;
        return this;
    }

    public ClassTreeImpl completeTypeParameters(TypeParameterListTreeImpl typeParameters) {
        this.typeParameters = typeParameters;
        return this;
    }

    public ClassTreeImpl completeSuperclass(SyntaxToken extendsKeyword, TypeTree superClass) {
        this.extendsKeyword = extendsKeyword;
        this.superClass = superClass;
        return this;
    }

    public ClassTreeImpl completeInterfaces(SyntaxToken keyword, QualifiedIdentifierListTreeImpl interfaces) {
        if (is(Kind.INTERFACE)) {
            extendsKeyword = keyword;
        } else {
            implementsKeyword = keyword;
        }
        this.superInterfaces = interfaces;
        return this;
    }

    public ClassTreeImpl complete(InternalSyntaxToken atToken, InternalSyntaxToken interfaceToken,
            IdentifierTree simpleName) {
        Preconditions.checkState(this.simpleName == null);
        completeIdentifier(simpleName);
        this.atToken = atToken;
        completeDeclarationKeyword(interfaceToken);
        return this;
    }

    public ClassTreeImpl completeDeclarationKeyword(SyntaxToken declarationKeyword) {
        this.declarationKeyword = declarationKeyword;
        return this;
    }

    @Override
    public Kind kind() {
        return kind;
    }

    @Nullable
    @Override
    public IdentifierTree simpleName() {
        return simpleName;
    }

    @Override
    public TypeParameters typeParameters() {
        return typeParameters;
    }

    @Override
    public ModifiersTree modifiers() {
        return modifiers;
    }

    @Nullable
    @Override
    public TypeTree superClass() {
        return superClass;
    }

    @Override
    public ListTree<TypeTree> superInterfaces() {
        return superInterfaces;
    }

    @Nullable
    @Override
    public SyntaxToken openBraceToken() {
        return openBraceToken;
    }

    @Override
    public List<Tree> members() {
        return members;
    }

    @Nullable
    @Override
    public SyntaxToken closeBraceToken() {
        return closeBraceToken;
    }

    @Override
    public Symbol.TypeSymbol symbol() {
        return symbol;
    }

    @Nullable
    @Override
    public SyntaxToken declarationKeyword() {
        return declarationKeyword;
    }

    @Override
    public void accept(TreeVisitor visitor) {
        visitor.visitClass(this);
    }

    public void setSymbol(JavaSymbol.TypeJavaSymbol symbol) {
        Preconditions.checkState(this.symbol.equals(Symbols.unknownSymbol));
        this.symbol = symbol;
    }

    @Override
    public int getLine() {
        if (simpleName == null) {
            return super.getLine();
        }
        return ((IdentifierTreeImpl) simpleName).getLine();
    }

    @Override
    public Iterable<Tree> children() {
        return Iterables.concat(Collections.singletonList(modifiers), addIfNotNull(atToken),
                addIfNotNull(declarationKeyword), addIfNotNull(simpleName), Collections.singletonList(typeParameters),
                addIfNotNull(extendsKeyword), addIfNotNull(superClass), addIfNotNull(implementsKeyword),
                Collections.singletonList(superInterfaces), addIfNotNull(openBraceToken), members,
                addIfNotNull(closeBraceToken));
    }

    private static Iterable<Tree> addIfNotNull(@Nullable Tree tree) {
        if (tree == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(tree);
    }

}