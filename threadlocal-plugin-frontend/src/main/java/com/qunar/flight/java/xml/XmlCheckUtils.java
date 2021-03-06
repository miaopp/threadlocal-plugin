/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.xml;

import javax.annotation.CheckForNull;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class XmlCheckUtils {

    private XmlCheckUtils() {
    }

    @CheckForNull
    public static Integer nodeLine(Node node) {
        Node lineAttribute = nodeAttribute(node, XmlParser.START_LINE_ATTRIBUTE);
        if (lineAttribute != null) {
            return Integer.valueOf(lineAttribute.getNodeValue());
        }
        return null;
    }

    @CheckForNull
    public static Node nodeAttribute(Node node, String attribute) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getNamedItem(attribute);
    }

}
