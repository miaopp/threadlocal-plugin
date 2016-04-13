package com.qunar.flight.rules;

import java.util.Map;

import com.google.common.collect.Maps;
import com.qunar.flight.plugins.java.api.JavaCheck;
import com.qunar.flight.plugins.java.api.JavaFileScanner;
import com.qunar.flight.plugins.java.api.JavaFileScannerContext;
import com.qunar.flight.plugins.java.api.tree.BaseTreeVisitor;
import com.qunar.flight.plugins.java.api.tree.ExpressionTree;
import com.qunar.flight.plugins.java.api.tree.IdentifierTree;
import com.qunar.flight.plugins.java.api.tree.MemberSelectExpressionTree;
import com.qunar.flight.plugins.java.api.tree.MethodInvocationTree;
import com.qunar.flight.plugins.java.api.tree.Tree;
import com.qunar.flight.plugins.java.api.tree.VariableTree;

/**
 * Created by ping.miao on 2016/4/8.
 */
public class ThreadLocalRemoveVisitor extends BaseTreeVisitor implements JavaFileScanner {
    private JavaFileScannerContext context;

    protected class KeyPair {
        private Tree tree;
        private JavaFileScannerContext context;
        private JavaCheck javaCheck;

        public KeyPair() {
        }

        public KeyPair(JavaFileScannerContext context, Tree tree, JavaCheck javaCheck) {
            this.context = context;
            this.tree = tree;
            this.javaCheck = javaCheck;
        }

        public JavaFileScannerContext getContext() {
            return context;
        }

        public void setContext(JavaFileScannerContext context) {
            this.context = context;
        }

        public Tree getTree() {
            return tree;
        }

        public void setTree(Tree tree) {
            this.tree = tree;
        }

        public JavaCheck getJavaCheck() {
            return javaCheck;
        }

        public void setJavaCheck(JavaCheck javaCheck) {
            this.javaCheck = javaCheck;
        }

        @Override
        public String toString() {
            return "KeyPair{" +
                    "tree=" + tree +
                    ", context=" + context +
                    ", javaCheck=" + javaCheck +
                    '}';
        }
    }

    protected static Map<String, KeyPair> threadLocalVariables = Maps.newConcurrentMap();
    protected static Map<String, Boolean> threadLocalRemoveVariables = Maps.newConcurrentMap();

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitVariable(VariableTree tree) {
        if (tree.symbol().type().is("java.lang.ThreadLocal")) {
            threadLocalVariables.put(tree.simpleName().name(), new KeyPair(context, tree, this));
        }
        super.visitVariable(tree);
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        ExpressionTree methodSelect = tree.methodSelect();
        IdentifierTree identifierTree = null;
        if (methodSelect instanceof IdentifierTree) {
            identifierTree = (IdentifierTree) methodSelect;
        } else if (methodSelect instanceof MemberSelectExpressionTree) {
            identifierTree = ((MemberSelectExpressionTree) methodSelect).identifier();
        }
        if ("remove".equals(identifierTree.identifierToken().text())) {
            ExpressionTree expressionTree = ((MemberSelectExpressionTree) tree.methodSelect()).expression();
            if (expressionTree instanceof IdentifierTree) {
                threadLocalRemoveVariables.put(((IdentifierTree) expressionTree).identifierToken().text(), true);
            } else if (expressionTree instanceof MemberSelectExpressionTree) {
                threadLocalRemoveVariables.put(((MemberSelectExpressionTree) expressionTree).identifier()
                        .identifierToken().text(), true);
            }
        }
        super.visitMethodInvocation(tree);
    }
}
