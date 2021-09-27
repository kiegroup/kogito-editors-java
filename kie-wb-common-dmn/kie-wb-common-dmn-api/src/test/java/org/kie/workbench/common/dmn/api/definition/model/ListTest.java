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
import org.kie.workbench.common.dmn.api.definition.HasExpression;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListTest {

    private static final String LIST_ID = "LIST_ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private org.kie.workbench.common.dmn.api.definition.model.List list;

    @Before
    public void setup() {
        this.list = spy(new org.kie.workbench.common.dmn.api.definition.model.List());
    }

    @Test
    public void testGetHasTypeRefs() {
        final Expression expression1 = mock(Expression.class); //added
        final Expression expression2 = mock(Expression.class); //added
        final List<HasExpression> hasExpressions = asList(HasExpression.wrap(list, expression1), HasExpression.wrap(list, expression2));
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        doReturn(hasExpressions).when(list).getExpression();

        when(expression1.getHasTypeRefs()).thenReturn(asList(hasTypeRef1));
        when(expression2.getHasTypeRefs()).thenReturn(asList(hasTypeRef2));

        final List<HasTypeRef> actualHasTypeRefs = list.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(list, hasTypeRef1, hasTypeRef2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(list.getRequiredComponentWidthCount(),
                     list.getComponentWidths().size());
        list.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final org.kie.workbench.common.dmn.api.definition.model.List source = new org.kie.workbench.common.dmn.api.definition.model.List(
                new Id(LIST_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new ArrayList<>()
        );

        final org.kie.workbench.common.dmn.api.definition.model.List target = source.copy();

        assertNotNull(target);
        assertNotEquals(LIST_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertTrue(target.getExpression().isEmpty());
    }

    @Test
    public void testEqualsNotIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final org.kie.workbench.common.dmn.api.definition.model.List list1 = new org.kie.workbench.common.dmn.api.definition.model.List();
        final org.kie.workbench.common.dmn.api.definition.model.List list2 = new org.kie.workbench.common.dmn.api.definition.model.List();

        list1.setId(id1);
        list2.setId(id2);

        assertFalse(list1.equals(list2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_SameId() {
        final Id same = new Id();
        final org.kie.workbench.common.dmn.api.definition.model.List list1 = new org.kie.workbench.common.dmn.api.definition.model.List();
        final org.kie.workbench.common.dmn.api.definition.model.List list2 = new org.kie.workbench.common.dmn.api.definition.model.List();
        final HasExpression expression1 = mock(HasExpression.class);
        final HasExpression expression2 = mock(HasExpression.class);

        list1.setId(same);
        list1.getExpression().add(expression1);
        list1.getExpression().add(expression2);

        list2.setId(same);
        list2.getExpression().add(expression1);
        list2.getExpression().add(expression2);

        final boolean result = list1.equals(list2, false);

        assertTrue(result);

        verify(expression1, never()).equals(expression1, true);
        verify(expression2, never()).equals(expression2, true);
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
        final org.kie.workbench.common.dmn.api.definition.model.List list1 = new org.kie.workbench.common.dmn.api.definition.model.List();
        final org.kie.workbench.common.dmn.api.definition.model.List list2 = new org.kie.workbench.common.dmn.api.definition.model.List();
        final HasExpression expression1 = mock(HasExpression.class);
        final HasExpression expression2 = mock(HasExpression.class);

        when(expression1.equals(expression1, true)).thenReturn(true);
        when(expression2.equals(expression2, true)).thenReturn(true);

        list1.setId(id1);
        list1.getExpression().add(expression1);
        list1.getExpression().add(expression2);

        list2.setId(id2);
        list2.getExpression().add(expression1);
        list2.getExpression().add(expression2);

        final boolean result = list1.equals(list2, true);

        assertTrue(result);

        verify(expression1).equals(expression1, true);
        verify(expression2).equals(expression2, true);

        verify(expression1, never()).equals(expression1, false);
        verify(expression2, never()).equals(expression2, false);
    }
}
