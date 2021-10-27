/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HasExpressionTest {

    @Mock
    private DMNModelInstrumentedBase parent;

    @Test
    public void testNOP() {
        final HasExpression hasExpression = HasExpression.NOP;

        assertNull(hasExpression.getExpression());
        assertNull(hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNull(hasExpression.getExpression());
        assertNull(hasExpression.asDMNModelInstrumentedBase());
    }

    @Test
    public void testWrapNull() {
        final HasExpression hasExpression = HasExpression.wrap(parent, null);

        assertNull(hasExpression.getExpression());
        assertEquals(parent, hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNotNull(hasExpression.getExpression());
        assertEquals(context, hasExpression.getExpression());
        assertEquals(parent, hasExpression.asDMNModelInstrumentedBase());
    }

    @Test
    public void testWrapNonNull() {
        final LiteralExpression le = new LiteralExpression();
        final HasExpression hasExpression = HasExpression.wrap(parent, le);

        assertNotNull(hasExpression.getExpression());
        assertEquals(le, hasExpression.getExpression());
        assertEquals(parent, hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNotNull(hasExpression.getExpression());
        assertEquals(context, hasExpression.getExpression());
        assertEquals(parent, hasExpression.asDMNModelInstrumentedBase());
    }

    @Test
    public void testWrapEquals() {

        final Expression expression = mock(Expression.class);
        final HasExpression hasExpression = HasExpression.wrap(parent, expression);

        final Expression otherExpression = mock(Expression.class);
        final HasExpression otherHasExpression = HasExpression.wrap(parent, otherExpression);

        when(expression.equals(otherExpression, true)).thenReturn(true);

        final boolean isEquals = hasExpression.equals(otherHasExpression, true);

        assertTrue(isEquals);
        verify(expression).equals(otherExpression, true);
    }

    @Test
    public void testWrapEquals_NotIgnoringId() {

        final Expression expression = mock(Expression.class);
        final HasExpression hasExpression = HasExpression.wrap(parent, expression);

        final Expression otherExpression = mock(Expression.class);
        final HasExpression otherHasExpression = HasExpression.wrap(parent, otherExpression);

        when(expression.equals(otherExpression, false)).thenReturn(true);

        final boolean isEquals = hasExpression.equals(otherHasExpression, false);

        assertTrue(isEquals);
        verify(expression).equals(otherExpression, false);
    }

    @Test
    public void testWrapEquals_WhenOtherIsNotWrapped() {

        final Expression expression = mock(Expression.class);
        final HasExpression hasExpression = HasExpression.wrap(parent, expression);

        final Object otherHasExpression = mock(Object.class);

        final boolean isEquals = hasExpression.equals(otherHasExpression, true);

        assertFalse(isEquals);
    }
}
