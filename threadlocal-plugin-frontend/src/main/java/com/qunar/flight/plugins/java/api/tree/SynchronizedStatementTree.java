/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.plugins.java.api.tree;

import com.google.common.annotations.Beta;

/**
 * 'synchronized' statement.
 *
 * JLS 14.19
 *
 * <pre>
 *   synchronized ( {@link #expression()} ) {@link #block()}
 * </pre>
 *
 * @since Java 1.3
 */
@Beta
public interface SynchronizedStatementTree extends StatementTree {

    SyntaxToken synchronizedKeyword();

    SyntaxToken openParenToken();

    ExpressionTree expression();

    SyntaxToken closeParenToken();

    BlockTree block();

}