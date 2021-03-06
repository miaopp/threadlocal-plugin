/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.resolve;

import java.util.Map;

import javax.annotation.Nullable;

import com.qunar.flight.java.model.expression.IdentifierTreeImpl;
import com.qunar.flight.java.model.statement.LabeledStatementTreeImpl;
import com.qunar.flight.java.resolve.JavaSymbol.JavaLabelSymbol;
import com.qunar.flight.plugins.java.api.tree.BaseTreeVisitor;
import com.qunar.flight.plugins.java.api.tree.BreakStatementTree;
import com.qunar.flight.plugins.java.api.tree.ContinueStatementTree;
import com.qunar.flight.plugins.java.api.tree.IdentifierTree;
import com.qunar.flight.plugins.java.api.tree.LabeledStatementTree;

import com.google.common.collect.Maps;

public class LabelsVisitor extends BaseTreeVisitor {

    private final Map<String, LabeledStatementTree> labelTrees;
    // FIXME (benzonico) The dependency of this class upon SemanticModel should be removed. This holds as long as Result
    // relies on SemanticModel.
    // As a result of this removal, this visitor should always be executed, regardless semantic analysis is activated or
    // not.
    private final SemanticModel semanticModel;

    public LabelsVisitor(SemanticModel semanticModel) {
        this.semanticModel = semanticModel;
        this.labelTrees = Maps.newHashMap();
    }

    @Override
    public void visitLabeledStatement(LabeledStatementTree tree) {
        JavaLabelSymbol symbol = new JavaLabelSymbol(tree);
        ((LabeledStatementTreeImpl) tree).setSymbol(symbol);
        semanticModel.associateSymbol(tree, symbol);
        labelTrees.put(tree.label().name(), tree);
        super.visitLabeledStatement(tree);
    }

    @Override
    public void visitBreakStatement(BreakStatementTree tree) {
        associateLabel(tree.label());
        super.visitBreakStatement(tree);
    }

    @Override
    public void visitContinueStatement(ContinueStatementTree tree) {
        associateLabel(tree.label());
        super.visitContinueStatement(tree);
    }

    private void associateLabel(@Nullable IdentifierTree label) {
        if (label == null) {
            return;
        }
        LabeledStatementTree labelTree = labelTrees.get(label.name());
        if (labelTree != null) {
            JavaSymbol symbol = (JavaSymbol) labelTree.symbol();
            semanticModel.associateReference(label, symbol);
            ((IdentifierTreeImpl) label).setSymbol(symbol);
            symbol.addUsage(label);
        }
    }
}
