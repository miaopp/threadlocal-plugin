/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.se.checks;

import java.util.Map;

import com.qunar.flight.java.model.DefaultJavaFileScannerContext;
import com.qunar.flight.java.se.CheckerContext;
import com.qunar.flight.java.se.ProgramState;
import com.qunar.flight.java.se.constraint.ConstraintManager;
import com.qunar.flight.plugins.java.api.JavaFileScanner;
import com.qunar.flight.plugins.java.api.JavaFileScannerContext;
import com.qunar.flight.plugins.java.api.tree.MethodTree;
import com.qunar.flight.plugins.java.api.tree.Tree;

import com.google.common.collect.Multimap;

public abstract class SECheck implements JavaFileScanner {

    public void init(MethodTree methodTree) {
    }

    public ProgramState checkPreStatement(CheckerContext context, Tree syntaxNode) {
        return context.getState();
    }

    public ProgramState checkPostStatement(CheckerContext context, Tree syntaxNode) {
        return context.getState();
    }

    public void checkEndOfExecution(CheckerContext context) {
        // By default do nothing
    }

    public void checkEndOfExecutionPath(CheckerContext context, ConstraintManager constraintManager) {
        // By default do nothing
    }

    @Override
    public void scanFile(JavaFileScannerContext context) {
        Multimap<Tree, DefaultJavaFileScannerContext.SEIssue> issues = ((DefaultJavaFileScannerContext) context)
                .getSEIssues(getClass());
        for (Map.Entry<Tree, DefaultJavaFileScannerContext.SEIssue> issue : issues.entries()) {
            DefaultJavaFileScannerContext.SEIssue seIssue = issue.getValue();
            context.reportIssue(this, seIssue.getTree(), seIssue.getMessage(), seIssue.getSecondary(), null);
        }
    }

}
