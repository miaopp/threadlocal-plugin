/*
 * SonarQube Java Copyright (C) 2012-2016 SonarSource SA mailto:contact AT sonarsource DOT com This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.qunar.flight.java.se.symbolicvalues;

import java.util.List;

import com.qunar.flight.java.se.ProgramState;
import com.qunar.flight.java.se.constraint.BooleanConstraint;
import com.qunar.flight.java.se.constraint.Constraint;
import com.qunar.flight.java.se.constraint.ObjectConstraint;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public abstract class BinarySymbolicValue extends SymbolicValue {

    SymbolicValue leftOp;
    SymbolicValue rightOp;

    public BinarySymbolicValue(int id) {
        super(id);
    }

    public abstract BooleanConstraint shouldNotInverse();

    @Override
    public boolean references(SymbolicValue other) {
        return leftOp.equals(other) || rightOp.equals(other) || leftOp.references(other) || rightOp.references(other);
    }

    @Override
    public void computedFrom(List<SymbolicValue> symbolicValues) {
        Preconditions.checkArgument(symbolicValues.size() == 2);
        rightOp = symbolicValues.get(0);
        leftOp = symbolicValues.get(1);
    }

    protected List<ProgramState> copyConstraint(SymbolicValue from, SymbolicValue to, ProgramState programState,
            BooleanConstraint booleanConstraint) {
        Constraint constraintLeft = programState.getConstraint(from);
        if (constraintLeft instanceof BooleanConstraint) {
            BooleanConstraint boolConstraint = (BooleanConstraint) constraintLeft;
            return to.setConstraint(programState, shouldNotInverse().equals(booleanConstraint) ? boolConstraint
                    : boolConstraint.inverse());
        } else if (constraintLeft instanceof ObjectConstraint) {
            ObjectConstraint nullConstraint = (ObjectConstraint) constraintLeft;
            if (nullConstraint.isNull()) {
                return to.setConstraint(programState, shouldNotInverse().equals(booleanConstraint) ? nullConstraint
                        : nullConstraint.inverse());
            } else if (shouldNotInverse().equals(booleanConstraint)) {
                return to.setConstraint(programState, nullConstraint);
            }
        }
        return ImmutableList.of(programState);
    }

}
