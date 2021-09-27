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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
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
public class DecisionRuleTest {

    private static final String DECISION_RULE_ID = "DECISION_RULE_ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private DecisionRule decisionRule;

    @Before
    public void setup() {
        this.decisionRule = spy(new DecisionRule());
    }

    @Test
    public void testGetHasTypeRefs() {
        final LiteralExpression literalExpression1 = mock(LiteralExpression.class);
        final LiteralExpression literalExpression2 = mock(LiteralExpression.class);
        final List<LiteralExpression> outputEntry = asList(literalExpression1, literalExpression2);

        doReturn(outputEntry).when(decisionRule).getOutputEntry();

        when(literalExpression1.getHasTypeRefs()).thenReturn(asList(literalExpression1));
        when(literalExpression2.getHasTypeRefs()).thenReturn(asList(literalExpression2));

        final List<HasTypeRef> actualHasTypeRefs = decisionRule.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = asList(literalExpression1, literalExpression2);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final DecisionRule source = new DecisionRule(new Id(DECISION_RULE_ID), new Description(DESCRIPTION), new ArrayList<>(), new ArrayList<>());

        final DecisionRule target = source.copy();

        assertNotNull(target);
        assertNotEquals(DECISION_RULE_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertTrue(target.getInputEntry().isEmpty());
        assertTrue(target.getOutputEntry().isEmpty());
    }

    @Test
    public void testEqualsNotIgnoringId_WithDifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final DecisionRule decisionRule1 = new DecisionRule();
        final DecisionRule decisionRule2 = new DecisionRule();

        decisionRule1.setId(id1);
        decisionRule2.setId(id2);

        final boolean result = decisionRule1.equals(decisionRule2, false);
        assertFalse(result);
    }

    @Test
    public void testEqualsNotIgnoringId_WithSameId() {
        final Id same = new Id();
        final DecisionRule decisionRule1 = new DecisionRule();
        final DecisionRule decisionRule2 = new DecisionRule();
        final UnaryTests unaryTests = mock(UnaryTests.class);
        final LiteralExpression literalExpression = mock(LiteralExpression.class);

        decisionRule1.setId(same);
        decisionRule1.getInputEntry().add(unaryTests);
        decisionRule1.getOutputEntry().add(literalExpression);

        decisionRule2.setId(same);
        decisionRule2.getInputEntry().add(unaryTests);
        decisionRule2.getOutputEntry().add(literalExpression);

        final boolean result = decisionRule1.equals(decisionRule2, false);

        assertTrue(result);

        verify(unaryTests, never()).equals(unaryTests, true);
        verify(literalExpression, never()).equals(literalExpression, true);
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
        final DecisionRule decisionRule1 = new DecisionRule();
        final DecisionRule decisionRule2 = new DecisionRule();
        final UnaryTests unaryTests = mock(UnaryTests.class);
        final LiteralExpression literalExpression = mock(LiteralExpression.class);

        when(unaryTests.equals(unaryTests, true)).thenReturn(true);
        when(literalExpression.equals(literalExpression, true)).thenReturn(true);

        decisionRule1.setId(id1);
        decisionRule1.getInputEntry().add(unaryTests);
        decisionRule1.getOutputEntry().add(literalExpression);

        decisionRule2.setId(id2);
        decisionRule2.getInputEntry().add(unaryTests);
        decisionRule2.getOutputEntry().add(literalExpression);

        final boolean result = decisionRule1.equals(decisionRule2, true);

        assertTrue(result);

        verify(unaryTests).equals(unaryTests, true);
        verify(literalExpression).equals(literalExpression, true);
        verify(unaryTests, never()).equals(unaryTests, false);
        verify(literalExpression, never()).equals(literalExpression, false);
    }
}
