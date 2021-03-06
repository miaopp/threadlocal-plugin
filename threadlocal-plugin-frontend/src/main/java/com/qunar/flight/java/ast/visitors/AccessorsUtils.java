/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.ast.visitors;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.qunar.flight.java.model.ModifiersUtils;
import com.qunar.flight.plugins.java.api.tree.AssignmentExpressionTree;
import com.qunar.flight.plugins.java.api.tree.ClassTree;
import com.qunar.flight.plugins.java.api.tree.ExpressionStatementTree;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.IdentifierTree;
import com.qunar.flight.plugins.java.api.tree.MemberSelectExpressionTree;
import com.qunar.flight.plugins.java.api.tree.MethodTree;
import com.qunar.flight.plugins.java.api.tree.Modifier;
import com.qunar.flight.plugins.java.api.tree.PrimitiveTypeTree;
import com.qunar.flight.plugins.java.api.tree.ReturnStatementTree;
import com.qunar.flight.plugins.java.api.tree.StatementTree;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.VariableTree;

public class AccessorsUtils {

    private AccessorsUtils() {
    }

    public static boolean isAccessor(ClassTree classTree, MethodTree methodTree) {
        return isPublicMethod(methodTree) && methodTree.block() != null
                && (isGetter(classTree, methodTree) || isSetter(classTree, methodTree));
    }

    private static boolean isPublicMethod(MethodTree methodTree) {
        return methodTree.is(Tree.Kind.METHOD) && ModifiersUtils.hasModifier(methodTree.modifiers(), Modifier.PUBLIC);
    }

    private static boolean isSetter(ClassTree classTree, MethodTree methodTree) {
        return methodTree.simpleName().name().startsWith("set") && methodTree.parameters().size() == 1
                && returnTypeIs(methodTree, "void") && hasOneAssignementStatement(methodTree, classTree);
    }

    private static boolean hasOneAssignementStatement(MethodTree methodTree, ClassTree classTree) {
        List<StatementTree> body = methodTree.block().body();
        return body.size() == 1
                && body.get(0).is(Tree.Kind.EXPRESSION_STATEMENT)
                && ((ExpressionStatementTree) body.get(0)).expression().is(Tree.Kind.ASSIGNMENT)
                && referencePrivateProperty(
                        (AssignmentExpressionTree) ((ExpressionStatementTree) body.get(0)).expression(), classTree);
    }

    private static boolean referencePrivateProperty(AssignmentExpressionTree assignmentExpressionTree,
            ClassTree classTree) {
        return referencePrivateProperty(assignmentExpressionTree.variable(), classTree);
    }

    private static boolean isGetter(ClassTree classTree, MethodTree methodTree) {
        return methodTree.parameters().isEmpty() && hasOneReturnStatement(methodTree, classTree)
                && (isValidGetter(methodTree) || isBooleanGetter(methodTree));
    }

    private static boolean isBooleanGetter(MethodTree methodTree) {
        return methodTree.simpleName().name().startsWith("is") && returnTypeIs(methodTree, "boolean");
    }

    private static boolean isValidGetter(MethodTree methodTree) {
        return methodTree.simpleName().name().startsWith("get");
    }

    private static boolean hasOneReturnStatement(MethodTree methodTree, ClassTree classTree) {
        List<StatementTree> body = methodTree.block().body();
        return body.size() == 1 && body.get(0).is(Tree.Kind.RETURN_STATEMENT)
                && referencePrivateProperty((ReturnStatementTree) body.get(0), classTree);
    }

    private static boolean referencePrivateProperty(ReturnStatementTree returnStatementTree, ClassTree classTree) {
        ExpressionTree expression = returnStatementTree.expression();
        String variableName = "";
        if (expression == null) {
            return false;
        } else if (expression.is(Tree.Kind.IDENTIFIER)) {
            variableName = ((IdentifierTree) expression).name();
        }
        return !StringUtils.isEmpty(variableName) && referencePrivateProperty(variableName, classTree);
    }

    private static boolean referencePrivateProperty(ExpressionTree expression, ClassTree classTree) {
        String variableReturned = "";
        if (expression.is(Tree.Kind.IDENTIFIER)) {
            variableReturned = ((IdentifierTree) expression).name();
        } else if (expression.is(Tree.Kind.MEMBER_SELECT)) {
            variableReturned = ((MemberSelectExpressionTree) expression).identifier().name();
        }
        return !StringUtils.isEmpty(variableReturned) && referencePrivateProperty(variableReturned, classTree);
    }

    private static boolean referencePrivateProperty(String variableName, ClassTree classTree) {
        for (Tree member : classTree.members()) {
            if (member.is(Tree.Kind.VARIABLE)) {
                VariableTree variableTree = (VariableTree) member;
                if (ModifiersUtils.hasModifier(variableTree.modifiers(), Modifier.PRIVATE)
                        && variableTree.simpleName().name().equals(variableName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean returnTypeIs(MethodTree methodTree, String expectedReturnType) {
        Tree returnType = methodTree.returnType();
        return returnType != null && returnType.is(Tree.Kind.PRIMITIVE_TYPE)
                && expectedReturnType.equals(((PrimitiveTypeTree) returnType).keyword().text());
    }
}
