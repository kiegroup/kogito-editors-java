/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InputClauseTest {

    private static final String UNARY_ID = "UNARY-ID";
    private static final String INPUT_ID = "INPUT-ID";
    private static final String TEXT = "TEXT";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String CLAUSE_ID = "CLAUSE-ID";
    private InputClause inputClause;

    @Before
    public void setup() {
        this.inputClause = spy(new InputClause());
    }

    @Test
    public void testGetHasTypeRefs() {
        final InputClauseLiteralExpression literalExpression = mock(InputClauseLiteralExpression.class);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        doReturn(literalExpression).when(inputClause).getInputExpression();

        when(literalExpression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));

        final List<HasTypeRef> actualHasTypeRefs = inputClause.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(hasTypeRef1, hasTypeRef2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final InputClause source = new InputClause(
                new Id(INPUT_ID),
                new Description(DESCRIPTION),
                buildInputClauseLiteralExpression(),
                buildInputClauseUnaryTests()
        );

        final InputClause target = source.copy();

        assertNotNull(target);
        assertNotEquals(INPUT_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertNotNull(target.getInputExpression());
        assertNotEquals(CLAUSE_ID, target.getInputExpression().getId().getValue());
        assertEquals(TEXT, target.getInputExpression().getText().getValue());
        assertEquals(DESCRIPTION, target.getInputExpression().getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getInputExpression().getTypeRef());
        assertNotNull(target.getInputValues());
        assertNotEquals(UNARY_ID, target.getInputValues().getId().getValue());
        assertEquals(TEXT, target.getInputValues().getText().getValue());
        assertEquals(ConstraintType.ENUMERATION, target.getInputValues().getConstraintType());
    }

    @Test
    public void testEqualsNotIgnoringId_WithDifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final InputClause inputClause1 = new InputClause();
        final InputClause inputClause2 = new InputClause();

        inputClause1.setId(id1);
        inputClause2.setId(id2);

        assertFalse(inputClause1.equals(inputClause2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_WithSameId() {
        final Id same = new Id();
        final InputClause inputClause1 = new InputClause();
        final InputClause inputClause2 = new InputClause();
        final InputClauseUnaryTests value1 = mock(InputClauseUnaryTests.class);
        final InputClauseUnaryTests value2 = mock(InputClauseUnaryTests.class);
        final InputClauseLiteralExpression expression1 = mock(InputClauseLiteralExpression.class);
        final InputClauseLiteralExpression expression2 = mock(InputClauseLiteralExpression.class);

        when(value1.equals(value2, false)).thenReturn(true);
        when(expression1.equals(expression2, false)).thenReturn(true);

        inputClause1.setId(same);
        inputClause1.setInputValues(value1);
        inputClause1.setInputExpression(expression1);

        inputClause2.setId(same);
        inputClause2.setInputValues(value2);
        inputClause2.setInputExpression(expression2);

        final boolean result = inputClause1.equals(inputClause2, false);

        assertTrue(result);

        verify(value1).equals(value2, false);
        verify(expression1).equals(expression2, false);

        verify(value1, never()).equals(value2, true);
        verify(expression1, never()).equals(expression2, true);
    }

    @Test
    public void testEqualsIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final InputClause inputClause1 = new InputClause();
        final InputClause inputClause2 = new InputClause();
        final InputClauseUnaryTests value1 = mock(InputClauseUnaryTests.class);
        final InputClauseUnaryTests value2 = mock(InputClauseUnaryTests.class);
        final InputClauseLiteralExpression expression1 = mock(InputClauseLiteralExpression.class);
        final InputClauseLiteralExpression expression2 = mock(InputClauseLiteralExpression.class);

        when(value1.equals(value2, true)).thenReturn(true);
        when(expression1.equals(expression2, true)).thenReturn(true);

        inputClause1.setId(id1);
        inputClause1.setInputValues(value1);
        inputClause1.setInputExpression(expression1);

        inputClause2.setId(id2);
        inputClause2.setInputValues(value2);
        inputClause2.setInputExpression(expression2);

        final boolean result = inputClause1.equals(inputClause2, true);

        assertTrue(result);

        verify(value1).equals(value2, true);
        verify(expression1).equals(expression2, true);

        verify(value1, never()).equals(value2, false);
        verify(expression1, never()).equals(expression2, false);
    }

    @Test
    public void testEqualsIgnoringId_SameId() {
        final Id same = new Id();
        final InputClause inputClause1 = new InputClause();
        final InputClause inputClause2 = new InputClause();
        final InputClauseUnaryTests value1 = mock(InputClauseUnaryTests.class);
        final InputClauseUnaryTests value2 = mock(InputClauseUnaryTests.class);
        final InputClauseLiteralExpression expression1 = mock(InputClauseLiteralExpression.class);
        final InputClauseLiteralExpression expression2 = mock(InputClauseLiteralExpression.class);

        when(value1.equals(value2, true)).thenReturn(true);
        when(expression1.equals(expression2, true)).thenReturn(true);

        inputClause1.setId(same);
        inputClause1.setInputValues(value1);
        inputClause1.setInputExpression(expression1);

        inputClause2.setId(same);
        inputClause2.setInputValues(value2);
        inputClause2.setInputExpression(expression2);

        final boolean result = inputClause1.equals(inputClause2, true);

        assertTrue(result);

        verify(value1).equals(value2, true);
        verify(expression1).equals(expression2, true);

        verify(value1, never()).equals(value2, false);
        verify(expression1, never()).equals(expression2, false);
    }

    private InputClauseUnaryTests buildInputClauseUnaryTests() {
        return new InputClauseUnaryTests(
                new Id(UNARY_ID),
                new Text(TEXT),
                ConstraintType.ENUMERATION
        );
    }

    private InputClauseLiteralExpression buildInputClauseLiteralExpression() {
        return new InputClauseLiteralExpression(
                new Id(CLAUSE_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                new ImportedValues()
        );
    }
}
