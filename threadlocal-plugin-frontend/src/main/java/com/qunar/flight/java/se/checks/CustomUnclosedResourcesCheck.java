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

import java.util.List;

import javax.annotation.Nullable;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import com.qunar.flight.java.matcher.MethodMatcherCollection;
import com.qunar.flight.java.matcher.MethodMatcherFactory;
import com.qunar.flight.java.se.CheckerContext;
import com.qunar.flight.java.se.ProgramState;
import com.qunar.flight.java.se.constraint.ConstraintManager;
import com.qunar.flight.java.se.constraint.ObjectConstraint;
import com.qunar.flight.java.se.symbolicvalues.SymbolicValue;
import com.qunar.flight.plugins.java.api.tree.Arguments;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.MethodInvocationTree;
import com.qunar.flight.plugins.java.api.tree.NewClassTree;
import com.qunar.flight.plugins.java.api.tree.ReturnStatementTree;
import com.qunar.flight.plugins.java.api.tree.Tree;
import org.sonar.squidbridge.annotations.NoSqale;
import org.sonar.squidbridge.annotations.RuleTemplate;

@Rule(key = "S3546", name = "Resources as defined by user should be closed", priority = Priority.CRITICAL, tags = {
        "denial-of-service", "security" })
@RuleTemplate
@NoSqale
public class CustomUnclosedResourcesCheck extends SECheck {

    enum Status {
        OPENED, CLOSED
    }

    @RuleProperty(key = "constructor", description = "the fully-qualified name of a constructor that creates an open resource."
            + " An optional signature may be specified after the class name."
            + " E.G. \"org.assoc.res.MyResource\" or \"org.assoc.res.MySpecialResource(java.lang.String, int)\"")
    public String constructor = "";

    @RuleProperty(key = "factoryMethod", description = "the fully-qualified name of a factory method that returns an open resource, with or without a parameter list."
            + " E.G. \"org.assoc.res.ResourceFactory#create\" or \"org.assoc.res.SpecialResourceFactory #create(java.lang.String, int)\"")
    public String factoryMethod = "";

    @RuleProperty(key = "openingMethod", description = "the fully-qualified name of a method that opens an existing resource, with or without a parameter list."
            + " E.G. \"org.assoc.res.ResourceFactory#create\" or \"org.assoc.res.SpecialResourceFactory #create(java.lang.String, int)\"")
    public String openingMethod = "";

    @RuleProperty(key = "closingMethod", description = "the fully-qualified name of the method which closes the open resource, with or without a parameter list."
            + " E.G. \"org.assoc.res.MyResource#closeMe\" or \"org.assoc.res.MySpecialResource#closeMe(java.lang.String, int)\"")
    public String closingMethod = "";

    private MethodMatcherCollection classConstructor;

    private MethodMatcherCollection factoryList;
    private MethodMatcherCollection openingList;
    private MethodMatcherCollection closingList;

    @Override
    public ProgramState checkPreStatement(CheckerContext context, Tree syntaxNode) {
        AbstractStatementVisitor visitor = new PreStatementVisitor(context);
        syntaxNode.accept(visitor);
        return visitor.programState;
    }

    @Override
    public ProgramState checkPostStatement(CheckerContext context, Tree syntaxNode) {
        PostStatementVisitor visitor = new PostStatementVisitor(context);
        syntaxNode.accept(visitor);
        return visitor.programState;
    }

    @Override
    public void checkEndOfExecutionPath(CheckerContext context, ConstraintManager constraintManager) {
        List<ObjectConstraint> constraints = context.getState().getFieldConstraints(Status.OPENED);
        for (ObjectConstraint constraint : constraints) {
            Tree syntaxNode = constraint.syntaxNode();
            String name = null;
            if (syntaxNode.is(Tree.Kind.NEW_CLASS)) {
                name = ((NewClassTree) syntaxNode).identifier().symbolType().name();
            } else if (syntaxNode.is(Tree.Kind.METHOD_INVOCATION)) {
                name = ((MethodInvocationTree) syntaxNode).symbolType().name();
            }
            if (name != null) {
                context.reportIssue(syntaxNode, this, "Close this \"" + name + "\".");
            }
        }
    }

    private static MethodMatcherCollection createMethodMatchers(String rule) {
        if (rule.length() > 0) {
            return MethodMatcherCollection.create(MethodMatcherFactory.methodMatcher(rule));
        } else {
            return MethodMatcherCollection.create();
        }
    }

    private abstract class AbstractStatementVisitor extends CheckerTreeNodeVisitor {

        protected AbstractStatementVisitor(ProgramState programState) {
            super(programState);
        }

        protected void closeResource(@Nullable SymbolicValue target) {
            if (target != null) {
                ObjectConstraint oConstraint = programState.getConstraintWithStatus(target, Status.OPENED);
                if (oConstraint != null) {
                    programState = programState.addConstraint(target.wrappedValue(),
                            oConstraint.withStatus(Status.CLOSED));
                }
            }
        }

        protected void openResource(Tree syntaxNode) {
            SymbolicValue sv = programState.peekValue();
            programState = programState
                    .addConstraint(sv, new ObjectConstraint(false, false, syntaxNode, Status.OPENED));
        }

        protected boolean isClosingResource(MethodInvocationTree mit) {
            return closingMethods().anyMatch(mit);
        }

        private MethodMatcherCollection closingMethods() {
            if (closingList == null) {
                closingList = createMethodMatchers(closingMethod);
            }
            return closingList;
        }
    }

    private class PreStatementVisitor extends AbstractStatementVisitor {

        protected PreStatementVisitor(CheckerContext context) {
            super(context.getState());
        }

        @Override
        public void visitNewClass(NewClassTree syntaxNode) {
            closeArguments(syntaxNode.arguments(), 0);
        }

        @Override
        public void visitMethodInvocation(MethodInvocationTree mit) {
            if (isOpeningResource(mit)) {
                openResource(mit);
            } else if (isClosingResource(mit)) {
                closeResource(programState.peekValue());
            } else {
                closeArguments(mit.arguments(), 1);
            }
        }

        private boolean isOpeningResource(MethodInvocationTree syntaxNode) {
            return openingMethods().anyMatch(syntaxNode);
        }

        @Override
        public void visitReturnStatement(ReturnStatementTree syntaxNode) {
            ExpressionTree expression = syntaxNode.expression();
            if (expression != null) {
                closeResource(programState.peekValue());
            }
        }

        private void closeArguments(Arguments arguments, int stackOffset) {
            List<SymbolicValue> values = programState.peekValues(arguments.size() + stackOffset);
            List<SymbolicValue> argumentValues = values.subList(stackOffset, values.size());
            for (SymbolicValue target : argumentValues) {
                closeResource(target);
            }
        }

        private MethodMatcherCollection openingMethods() {
            if (openingList == null) {
                openingList = createMethodMatchers(openingMethod);
            }
            return openingList;
        }
    }

    private class PostStatementVisitor extends AbstractStatementVisitor {

        protected PostStatementVisitor(CheckerContext context) {
            super(context.getState());
        }

        @Override
        public void visitNewClass(NewClassTree newClassTree) {
            if (isCreatingResource(newClassTree)) {
                openResource(newClassTree);
            }
        }

        @Override
        public void visitMethodInvocation(MethodInvocationTree mit) {
            if (isCreatingResource(mit)) {
                openResource(mit);
            }
        }

        private boolean isCreatingResource(NewClassTree newClassTree) {
            return constructorClasses().anyMatch(newClassTree);
        }

        private MethodMatcherCollection constructorClasses() {
            if (classConstructor == null) {
                classConstructor = MethodMatcherCollection.create();
                if (constructor.length() > 0) {
                    classConstructor.add(MethodMatcherFactory.constructorMatcher(constructor));
                }
            }
            return classConstructor;
        }

        private boolean isCreatingResource(MethodInvocationTree mit) {
            return factoryMethods().anyMatch(mit);
        }

        private MethodMatcherCollection factoryMethods() {
            if (factoryList == null) {
                factoryList = createMethodMatchers(factoryMethod);
            }
            return factoryList;
        }

    }

}
