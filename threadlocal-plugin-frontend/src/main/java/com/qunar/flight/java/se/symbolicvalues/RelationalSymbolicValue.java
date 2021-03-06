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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import com.qunar.flight.java.se.ProgramState;
import com.qunar.flight.java.se.constraint.BooleanConstraint;

import com.google.common.collect.ImmutableList;

public class RelationalSymbolicValue extends BinarySymbolicValue {

    public enum Kind {
        EQUAL("=="), NOT_EQUAL("!="), GREATER_THAN(">"), GREATER_THAN_OR_EQUAL(">="), LESS_THAN("<"), LESS_THAN_OR_EQUAL(
                "<="), METHOD_EQUALS(".EQ."), NOT_METHOD_EQUALS(".NE.");

        final String operand;

        Kind(String operand) {
            this.operand = operand;
        }

        public Kind inverse() {
            switch (this) {
            case GREATER_THAN:
                return LESS_THAN_OR_EQUAL;
            case GREATER_THAN_OR_EQUAL:
                return LESS_THAN;
            case LESS_THAN:
                return GREATER_THAN_OR_EQUAL;
            case LESS_THAN_OR_EQUAL:
                return GREATER_THAN;
            case METHOD_EQUALS:
                return NOT_METHOD_EQUALS;
            case NOT_METHOD_EQUALS:
                return METHOD_EQUALS;
            default:
                return this;
            }
        }

        public Kind symmetric() {
            Kind sym;
            switch (this) {
            case GREATER_THAN:
                sym = LESS_THAN;
                break;
            case GREATER_THAN_OR_EQUAL:
                sym = LESS_THAN_OR_EQUAL;
                break;
            case LESS_THAN:
                sym = GREATER_THAN;
                break;
            case LESS_THAN_OR_EQUAL:
                sym = GREATER_THAN_OR_EQUAL;
                break;
            default:
                sym = this;
            }
            return sym;
        }
    }

    private final Kind kind;

    public RelationalSymbolicValue(int id, Kind kind) {
        super(id);
        this.kind = kind;
    }

    @Override
    public BooleanConstraint shouldNotInverse() {
        switch (kind) {
        case EQUAL:
        case METHOD_EQUALS:
            return BooleanConstraint.TRUE;
        default:
            return BooleanConstraint.FALSE;
        }
    }

    @Override
    public List<ProgramState> setConstraint(ProgramState initialProgramState, BooleanConstraint booleanConstraint) {
        ProgramState programState = checkRelation(booleanConstraint, initialProgramState);
        if (programState == null) {
            return ImmutableList.of();
        }
        List<ProgramState> results = new ArrayList<>();
        List<ProgramState> copiedConstraints = copyConstraint(leftOp, rightOp, programState, booleanConstraint);
        for (ProgramState ps : copiedConstraints) {
            List<ProgramState> copiedConstraintsRightToLeft = copyConstraint(rightOp, leftOp, ps, booleanConstraint);
            if (copiedConstraintsRightToLeft.size() == 1 && copiedConstraintsRightToLeft.get(0).equals(programState)) {
                results.add(programState.addConstraint(this, booleanConstraint));
            } else {
                results.addAll(copiedConstraintsRightToLeft);
            }
        }
        return results;
    }

    @Override
    protected List<ProgramState> copyConstraint(SymbolicValue from, SymbolicValue to, ProgramState programState,
            BooleanConstraint booleanConstraint) {
        ProgramState newState = programState;
        if (programState.canReach(from) || programState.canReach(to)) {
            newState = programState.addConstraint(this, booleanConstraint);
        }
        return super.copyConstraint(from, to, newState, booleanConstraint);
    }

    @CheckForNull
    private ProgramState checkRelation(BooleanConstraint booleanConstraint, ProgramState programState) {
        RelationState relationState = binaryRelation().resolveState(programState.getKnownRelations());
        if (relationState.rejects(booleanConstraint)) {
            return null;
        }
        return programState;
    }

    @Override
    public BinaryRelation binaryRelation() {
        return BinaryRelation.binaryRelation(kind, leftOp, rightOp);
    }

    @Override
    public String toString() {
        return binaryRelation().toString();
    }
}
