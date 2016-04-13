/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.se;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qunar.flight.java.ast.visitors.SubscriptionVisitor;
import com.qunar.flight.java.se.symbolicvalues.BinaryRelation;
import com.qunar.flight.plugins.java.api.JavaFileScanner;
import com.qunar.flight.plugins.java.api.tree.Tree;

import com.google.common.collect.Lists;

public class SymbolicExecutionVisitor extends SubscriptionVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(SymbolicExecutionVisitor.class);

    private final ExplodedGraphWalker.ExplodedGraphWalkerFactory egwFactory;

    public SymbolicExecutionVisitor(List<JavaFileScanner> executableScanners) {
        egwFactory = new ExplodedGraphWalker.ExplodedGraphWalkerFactory(executableScanners);
    }

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Lists.newArrayList(Tree.Kind.METHOD, Tree.Kind.CONSTRUCTOR);
    }

    @Override
    public void visitNode(Tree tree) {
        try {
            tree.accept(egwFactory.createWalker(context));
        } catch (ExplodedGraphWalker.MaximumStepsReachedException | ExplodedGraphWalker.ExplodedGraphTooBigException
                | BinaryRelation.TransitiveRelationExceededException exception) {
            LOG.debug("Could not complete symbolic execution: ", exception);
        }

    }
}
