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
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextEntryTest {

    private static final String ITEM_ID = "item-id";
    private static final String DESCRIPTION = "description";
    private static final String INFORMATION_ITEM_NAME = "item-name";
    private ContextEntry contextEntry;

    @Before
    public void setup() {
        this.contextEntry = spy(new ContextEntry());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression = mock(Expression.class);
        final InformationItem variable = mock(InformationItem.class);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef3 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef4 = mock(HasTypeRef.class);

        doReturn(expression).when(contextEntry).getExpression();
        doReturn(variable).when(contextEntry).getVariable();

        when(expression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(variable.getHasTypeRefs()).thenReturn(asList(hasTypeRef3, hasTypeRef4));

        final List<HasTypeRef> actualHasTypeRefs = contextEntry.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(hasTypeRef1, hasTypeRef2, hasTypeRef3, hasTypeRef4);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final ContextEntry source = new ContextEntry();
        final InformationItem informationItem = new InformationItem(new Id(ITEM_ID), new Description(DESCRIPTION), new Name(INFORMATION_ITEM_NAME), BuiltInType.BOOLEAN.asQName());
        source.setVariable(informationItem);

        final ContextEntry target = source.copy();

        assertNotNull(target);
        assertNull(target.getExpression());
        assertNotNull(target.getVariable());
        assertNotEquals(ITEM_ID, target.getVariable().getId().getValue());
        assertEquals(DESCRIPTION, target.getVariable().getDescription().getValue());
        assertEquals(INFORMATION_ITEM_NAME, target.getVariable().getName().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getVariable().getTypeRef());
    }

    @Test
    public void testEqualsNotIgnoringId() {
        testEquals(false);
    }

    @Test
    public void testEqualsIgnoringId() {
        testEquals(true);
    }

    private void testEquals(final boolean ignoreId) {
        final ContextEntry c1 = new ContextEntry();
        final ContextEntry c2 = new ContextEntry();
        final InformationItem variable1 = mock(InformationItem.class);
        final InformationItem variable2 = mock(InformationItem.class);
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        when(variable1.equals(variable2, ignoreId)).thenReturn(true);
        when(expression1.equals(expression2, ignoreId)).thenReturn(true);

        c1.setVariable(variable1);
        c1.setExpression(expression1);
        c2.setVariable(variable2);
        c2.setExpression(expression2);

        c1.equals(c2, ignoreId);

        verify(variable1).equals(variable2, ignoreId);
        verify(expression1).equals(expression2, ignoreId);

        verify(variable1, never()).equals(variable2, !ignoreId);
        verify(expression1, never()).equals(expression2, !ignoreId);
    }
}

