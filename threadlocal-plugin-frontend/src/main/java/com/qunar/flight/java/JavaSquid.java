/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.qunar.flight.java.ast.JavaAstScanner;
import com.qunar.flight.java.ast.parser.JavaParser;
import com.qunar.flight.java.bytecode.BytecodeScanner;
import com.qunar.flight.java.bytecode.visitor.BytecodeContext;
import com.qunar.flight.java.bytecode.visitor.DefaultBytecodeContext;
import com.qunar.flight.java.bytecode.visitor.DependenciesVisitor;
import com.qunar.flight.java.model.VisitorsBridge;
import com.qunar.flight.java.se.checks.SECheck;
import com.qunar.flight.plugins.java.api.JavaResourceLocator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.sonar.api.design.Dependency;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.TimeProfiler;
import org.sonar.graph.DirectedGraph;
import org.sonar.squid.api.CodeVisitor;

public class JavaSquid {
    private static final Logger LOG = LoggerFactory.getLogger(JavaSquid.class);

    private final JavaAstScanner astScanner;
    private final BytecodeScanner bytecodeScanner;
    private final DirectedGraph<Resource, Dependency> graph = new DirectedGraph<Resource, Dependency>();

    private boolean bytecodeScanned = false;

    public JavaSquid(JavaConfiguration conf, @Nullable SonarComponents sonarComponents,
            JavaResourceLocator javaResourceLocator, List<CodeVisitor> visitors) {
        Iterable<CodeVisitor> codeVisitors = Iterables.concat(Collections.singletonList(javaResourceLocator), visitors);
        List<File> classpath = Lists.newArrayList();
        if (sonarComponents != null) {
            classpath = sonarComponents.getJavaClasspath();
        }

        // AstScanner for main files
        astScanner = new JavaAstScanner(JavaParser.createParser(conf.getCharset()));
        boolean enableSymbolicExecution = hasASymbolicExecutionCheck(visitors);
        astScanner.setVisitorBridge(createVisitorBridge(codeVisitors, classpath, conf, sonarComponents,
                enableSymbolicExecution));

        // Bytecode scanner
        BytecodeContext bytecodeContext = new DefaultBytecodeContext(sonarComponents, javaResourceLocator);
        bytecodeScanner = new BytecodeScanner(bytecodeContext);
        DependenciesVisitor dependenciesVisitor = new DependenciesVisitor(bytecodeContext, graph);
        bytecodeScanner.accept(dependenciesVisitor);
        for (CodeVisitor visitor : visitors) {
            bytecodeScanner.accept(visitor);
        }

    }

    private static boolean hasASymbolicExecutionCheck(List<CodeVisitor> visitors) {
        for (CodeVisitor visitor : visitors) {
            if (visitor instanceof SECheck) {
                return true;
            }
        }
        return false;
    }

    private static VisitorsBridge createVisitorBridge(Iterable<CodeVisitor> codeVisitors, List<File> classpath,
            JavaConfiguration conf, @Nullable SonarComponents sonarComponents, boolean enableSymbolicExecution) {
        VisitorsBridge visitorsBridge = new VisitorsBridge(codeVisitors, classpath, sonarComponents,
                enableSymbolicExecution);
        visitorsBridge.setCharset(conf.getCharset());
        visitorsBridge.setAnalyseAccessors(conf.separatesAccessorsFromMethods());
        visitorsBridge.setJavaVersion(conf.javaVersion());
        return visitorsBridge;
    }

    public void scan(Iterable<File> sourceFiles, Collection<File> bytecodeFilesOrDirectories) {
        scanSources(sourceFiles);
        scanBytecode(bytecodeFilesOrDirectories);
    }

    private void scanSources(Iterable<File> sourceFiles) {
        TimeProfiler profiler = new TimeProfiler(getClass()).start("Java Main Files AST scan");
        astScanner.scan(sourceFiles);
        profiler.stop();
    }

    private void scanBytecode(Collection<File> bytecodeFilesOrDirectories) {
        if (hasBytecode(bytecodeFilesOrDirectories)) {
            TimeProfiler profiler = new TimeProfiler(getClass()).start("Java bytecode scan");

            bytecodeScanner.scan(bytecodeFilesOrDirectories);
            bytecodeScanned = true;
            profiler.stop();
        } else {
            LOG.warn("Java bytecode has not been made available to the analyzer. The "
                    + Joiner.on(", ").join(bytecodeScanner.getVisitors()) + " are disabled.");
            bytecodeScanned = false;
        }
    }

    static boolean hasBytecode(Collection<File> bytecodeFilesOrDirectories) {
        if (bytecodeFilesOrDirectories == null) {
            return false;
        }
        for (File bytecodeFilesOrDirectory : bytecodeFilesOrDirectories) {
            if (bytecodeFilesOrDirectory.exists()
                    && (bytecodeFilesOrDirectory.isFile() || !FileUtils.listFiles(bytecodeFilesOrDirectory,
                            new String[] { "class" }, true).isEmpty())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBytecodeScanned() {
        return bytecodeScanned;
    }

    public DirectedGraph<Resource, Dependency> getGraph() {
        return graph;
    }
}
