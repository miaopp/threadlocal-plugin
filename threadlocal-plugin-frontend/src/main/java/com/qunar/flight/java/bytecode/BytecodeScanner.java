/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.bytecode;

import java.io.File;
import java.io.InterruptedIOException;
import java.util.Collection;
import java.util.Collections;

import com.qunar.flight.java.bytecode.asm.AsmClass;
import com.qunar.flight.java.bytecode.asm.AsmClassProvider;
import com.qunar.flight.java.bytecode.asm.AsmClassProvider.DETAIL_LEVEL;
import com.qunar.flight.java.bytecode.asm.AsmClassProviderImpl;
import com.qunar.flight.java.bytecode.asm.AsmMethod;
import com.qunar.flight.java.bytecode.loader.SquidClassLoader;
import com.qunar.flight.java.bytecode.visitor.BytecodeContext;
import com.qunar.flight.java.bytecode.visitor.BytecodeVisitor;
import org.sonar.squid.api.CodeScanner;
import org.sonar.squid.api.CodeVisitor;
import org.sonar.squidbridge.api.AnalysisException;

import com.google.common.base.Throwables;

public class BytecodeScanner extends CodeScanner<BytecodeVisitor> {

    private final BytecodeContext context;

    public BytecodeScanner(BytecodeContext context) {
        this.context = context;
    }

    public BytecodeScanner scan(Collection<File> bytecodeFilesOrDirectories) {
        ClassLoader classLoader = ClassLoaderBuilder.create(bytecodeFilesOrDirectories);
        scanClasses(context.getJavaResourceLocator().classKeys(), new AsmClassProviderImpl(classLoader));
        // TODO unchecked cast
        ((SquidClassLoader) classLoader).close();
        return this;
    }

    protected BytecodeScanner scanClasses(Collection<String> classes, AsmClassProvider classProvider) {
        loadByteCodeInformation(classes, classProvider);
        linkVirtualMethods(classes, classProvider);
        notifyBytecodeVisitors(classes, classProvider);
        return this;
    }

    private static void linkVirtualMethods(Collection<String> keys, AsmClassProvider classProvider) {
        VirtualMethodsLinker linker = new VirtualMethodsLinker();
        for (String key : keys) {
            AsmClass asmClass = classProvider.getClass(key, DETAIL_LEVEL.STRUCTURE_AND_CALLS);
            for (AsmMethod method : asmClass.getMethods()) {
                linker.process(method);
            }
        }
    }

    private void notifyBytecodeVisitors(Collection<String> keys, AsmClassProvider classProvider) {
        BytecodeVisitor[] visitorArray = getVisitors().toArray(new BytecodeVisitor[getVisitors().size()]);
        for (BytecodeVisitor bytecodeVisitor : visitorArray) {
            bytecodeVisitor.setContext(context);
        }
        for (String key : keys) {
            try {
                AsmClass asmClass = classProvider.getClass(key, DETAIL_LEVEL.STRUCTURE_AND_CALLS);
                BytecodeVisitorNotifier visitorNotifier = new BytecodeVisitorNotifier(asmClass, visitorArray);
                visitorNotifier.notifyVisitors();
            } catch (Exception exception) {
                checkInterrrupted(exception);
                throw new AnalysisException("Unable to analyze .class file " + key, exception);
            }
        }
    }

    private static void checkInterrrupted(Exception e) {
        Throwable cause = Throwables.getRootCause(e);
        if (cause instanceof InterruptedException || cause instanceof InterruptedIOException) {
            throw new AnalysisException("Analysis cancelled", e);
        }
    }

    private static void loadByteCodeInformation(Collection<String> keys, AsmClassProvider classProvider) {
        for (String key : keys) {
            classProvider.getClass(key, DETAIL_LEVEL.STRUCTURE_AND_CALLS);
        }
    }

    @Override
    public Collection<Class<? extends BytecodeVisitor>> getVisitorClasses() {
        return Collections.emptyList();
    }

    @Override
    public void accept(CodeVisitor visitor) {
        if (visitor instanceof BytecodeVisitor) {
            super.accept(visitor);
        }
    }

}
