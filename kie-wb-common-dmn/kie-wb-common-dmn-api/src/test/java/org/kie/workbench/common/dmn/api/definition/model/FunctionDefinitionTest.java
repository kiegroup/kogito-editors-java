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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition.Kind;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
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

public class FunctionDefinitionTest {

    private static final String FUNCTION_ID = "FUNCTION-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private FunctionDefinition functionDefinition;

    @Before
    public void setup() {
        this.functionDefinition = spy(new FunctionDefinition());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression = mock(Expression.class);
        final InformationItem informationItem1 = mock(InformationItem.class);
        final InformationItem informationItem2 = mock(InformationItem.class);
        final List<InformationItem> formalParameter = asList(informationItem1, informationItem2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);

        doReturn(expression).when(functionDefinition).getExpression();
        doReturn(formalParameter).when(functionDefinition).getFormalParameter();

        when(expression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(informationItem1.getHasTypeRefs()).thenReturn(asList(hasTypeRef3));
        when(informationItem2.getHasTypeRefs()).thenReturn(asList(hasTypeRef4));

        final List<HasTypeRef> actualHasTypeRefs = functionDefinition.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(functionDefinition, hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(functionDefinition.getRequiredComponentWidthCount(),
                     functionDefinition.getComponentWidths().size());
        functionDefinition.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final FunctionDefinition source = new FunctionDefinition();
        source.setId(new Id(FUNCTION_ID));
        source.setDescription(new Description(DESCRIPTION));
        source.setTypeRef(BuiltInType.BOOLEAN.asQName());
        source.setKind(Kind.JAVA);

        final FunctionDefinition target = source.copy();

        assertNotNull(target);
        assertNotEquals(FUNCTION_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertEquals(Kind.JAVA, target.getKind());
    }

    @Test
    public void testKindFromValueWithFEEL() {
        assertEquals(Kind.FEEL, Kind.fromValue("FEEL"));
    }

    @Test
    public void testKindFromValueWithJava() {
        assertEquals(Kind.JAVA, Kind.fromValue("Java"));
    }

    @Test
    public void testKindFromValueWithPMML() {
        assertEquals(Kind.PMML, Kind.fromValue("PMML"));
    }

    @Test
    public void testKindFromValueWithDefault() {
        assertEquals(Kind.FEEL, Kind.fromValue("Something"));
    }

    @Test
    public void testEqualsNotIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final FunctionDefinition functionDefinition1 = new FunctionDefinition();
        final FunctionDefinition functionDefinition2 = new FunctionDefinition();

        functionDefinition1.setId(id1);
        functionDefinition2.setId(id2);

        assertFalse(functionDefinition1.equals(functionDefinition2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_SameId() {
        final Id same = new Id();
        final FunctionDefinition functionDefinition1 = new FunctionDefinition();
        final FunctionDefinition functionDefinition2 = new FunctionDefinition();
        final InformationItem formalParameter = mock(InformationItem.class);
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        when(expression1.equals(expression2, false)).thenReturn(true);

        functionDefinition1.setId(same);
        functionDefinition1.setExpression(expression1);
        functionDefinition1.getFormalParameter().add(formalParameter);

        functionDefinition2.setId(same);
        functionDefinition2.setExpression(expression2);
        functionDefinition2.getFormalParameter().add(formalParameter);

        final boolean result = functionDefinition1.equals(functionDefinition2, false);

        assertTrue(result);

        verify(formalParameter, never()).equals(formalParameter, true);
        verify(expression1, never()).equals(expression2, true);
    }

    @Test
    public void testEqualsIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        testEqualsIgnoringId(id1, id2);
    }

    @Test
    public void testEqualsIgnoringId_SameId() {
        final Id same = new Id();
        testEqualsIgnoringId(same, same);
    }

    private void testEqualsIgnoringId(final Id id1, final Id id2) {
        final FunctionDefinition functionDefinition1 = new FunctionDefinition();
        final FunctionDefinition functionDefinition2 = new FunctionDefinition();
        final InformationItem formalParameter = mock(InformationItem.class);
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        when(formalParameter.equals(formalParameter, true)).thenReturn(true);
        when(expression1.equals(expression2, true)).thenReturn(true);

        functionDefinition1.setId(id1);
        functionDefinition1.setExpression(expression1);
        functionDefinition1.getFormalParameter().add(formalParameter);

        functionDefinition2.setId(id2);
        functionDefinition2.setExpression(expression2);
        functionDefinition2.getFormalParameter().add(formalParameter);

        final boolean result = functionDefinition1.equals(functionDefinition2, true);

        assertTrue(result);

        verify(formalParameter).equals(formalParameter, true);
        verify(expression1).equals(expression2, true);

        verify(formalParameter, never()).equals(formalParameter, false);
        verify(expression1, never()).equals(expression2, false);
    }
}
