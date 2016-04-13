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

import com.qunar.flight.java.se.checks.SECheck;
import com.qunar.flight.java.se.constraint.ConstraintManager;
import com.qunar.flight.plugins.java.api.JavaFileScannerContext;
import com.qunar.flight.plugins.java.api.tree.Tree;

public interface CheckerContext {

    Object createSink();

    void reportIssue(Tree tree, SECheck check, String message);

    void reportIssue(Tree tree, SECheck check, String message, List<JavaFileScannerContext.Location> locations);

    void addTransition(ProgramState state);

    ProgramState getState();

    ConstraintManager getConstraintManager();

}