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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)

public class InvocationTest {

    private static final String INVOCATION_ID = "INVOCATION-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private Invocation invocation;

    @Before
    public void setup() {
        this.invocation = spy(new Invocation());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression = mock(Expression.class);
        final Binding binding1 = mock(Binding.class); //added
        final Binding binding2 = mock(Binding.class); //added
        final List<Binding> binding = asList(binding1, binding2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);

        doReturn(expression).when(invocation).getExpression();
        doReturn(binding).when(invocation).getBinding();

        when(expression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(binding1.getHasTypeRefs()).thenReturn(asList(hasTypeRef3));
        when(binding2.getHasTypeRefs()).thenReturn(asList(hasTypeRef4));

        final List<HasTypeRef> actualHasTypeRefs = invocation.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(invocation, hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(invocation.getRequiredComponentWidthCount(),
                     invocation.getComponentWidths().size());
        invocation.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final Invocation source = new Invocation(
                new Id(INVOCATION_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                null,
                new ArrayList<>()
        );

        final Invocation target = source.copy();

        assertNotNull(target);
        assertNotEquals(INVOCATION_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertNull(target.getExpression());
        assertTrue(target.getBinding().isEmpty());
    }

    @Test
    public void testEqualsNotIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final Invocation invocation1 = new Invocation();
        final Invocation invocation2 = new Invocation();

        invocation1.setId(id1);
        invocation2.setId(id2);

        assertFalse(invocation1.equals(invocation2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_SameId() {
        final Id same = new Id();
        final Invocation invocation1 = new Invocation();
        final Invocation invocation2 = new Invocation();
        final Binding binding = mock(Binding.class);
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        when(expression1.equals(expression2, false)).thenReturn(true);

        invocation1.setId(same);
        invocation1.setExpression(expression1);
        invocation1.getBinding().add(binding);

        invocation2.setId(same);
        invocation2.setExpression(expression2);
        invocation2.getBinding().add(binding);

        final boolean result = invocation1.equals(invocation2, false);

        assertTrue(result);

        verify(binding, never()).equals(binding, true);
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
        final Invocation invocation1 = new Invocation();
        final Invocation invocation2 = new Invocation();
        final Binding binding = mock(Binding.class);
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        when(binding.equals(binding, true)).thenReturn(true);
        when(expression1.equals(expression2, true)).thenReturn(true);

        invocation1.setId(id1);
        invocation1.setExpression(expression1);
        invocation1.getBinding().add(binding);

        invocation2.setId(id2);
        invocation2.setExpression(expression2);
        invocation2.getBinding().add(binding);

        final boolean result = invocation1.equals(invocation2, true);

        assertTrue(result);

        verify(binding).equals(binding, true);
        verify(expression1).equals(expression2, true);

        verify(binding, never()).equals(binding, false);
        verify(expression1, never()).equals(expression2, false);
    }
}
