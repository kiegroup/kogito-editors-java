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
public class ContextTest {

    private static final String CONTEXT_ID = "CONTEXT-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private Context context;

    @Before
    public void setup() {
        this.context = spy(new Context());
    }

    @Test
    public void testGetHasTypeRefs() {

        final ContextEntry contextEntry1 = mock(ContextEntry.class);
        final ContextEntry contextEntry2 = mock(ContextEntry.class);
        final List<ContextEntry> contextEntry = asList(contextEntry1, contextEntry2);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        doReturn(contextEntry).when(context).getContextEntry();

        when(contextEntry1.getHasTypeRefs()).thenReturn(asList(hasTypeRef1));
        when(contextEntry2.getHasTypeRefs()).thenReturn(asList(hasTypeRef2));

        final List<HasTypeRef> actualHasTypeRefs = context.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(context, hasTypeRef1, hasTypeRef2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(context.getRequiredComponentWidthCount(),
                     context.getComponentWidths().size());
        context.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final Context source = new Context(new Id(CONTEXT_ID), new Description(DESCRIPTION), BuiltInType.BOOLEAN.asQName());

        final Context target = source.copy();

        assertNotNull(target);
        assertNotEquals(CONTEXT_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
    }

    @Test
    public void testEqualsNotIgnoringId() {
        final ContextEntry c1 = new ContextEntry();
        final ContextEntry c2 = new ContextEntry();
        final Id id1 = new Id();
        final Id id2 = new Id();
        final Context context1 = new Context();
        final Context context2 = new Context();

        context1.getContextEntry().add(c1);
        context2.getContextEntry().add(c2);

        context1.setId(id1);
        context2.setId(id2);

        assertFalse(context1.equals(context2, false));
    }

    @Test
    public void testEqualsIgnoringId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final Context context1 = new Context();
        final Context context2 = new Context();

        context1.setId(id1);
        context2.setId(id2);

        assertTrue(context1.equals(context2, true));
    }

    @Test
    public void testEqualsIgnoringIdWithDifferentContextEntries() {
        final ContextEntry c1 = new ContextEntry();
        final ContextEntry c2 = new ContextEntry();
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);
        final Id id1 = new Id();
        final Id id2 = new Id();
        final Context context1 = new Context();
        final Context context2 = new Context();

        when(expression1.equals(expression2, true)).thenReturn(false);

        c1.setExpression(expression1);
        c2.setExpression(expression2);

        context1.getContextEntry().add(c1);
        context2.getContextEntry().add(c2);

        context1.setId(id1);
        context2.setId(id2);

        assertFalse(context1.equals(context2, true));

        verify(expression1).equals(expression2, true);
    }

    @Test
    public void testEqualsNotIgnoringIdWithDifferentContextEntries() {
        final ContextEntry c1 = new ContextEntry();
        final ContextEntry c2 = new ContextEntry();
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);
        final Id sameId = new Id();
        final Context context1 = new Context();
        final Context context2 = new Context();

        c1.setExpression(expression1);
        c2.setExpression(expression2);

        context1.getContextEntry().add(c1);
        context2.getContextEntry().add(c2);

        context1.setId(sameId);
        context2.setId(sameId);

        assertFalse(context1.equals(context2, false));

        verify(expression1, never()).equals(expression2, true);
    }
}

