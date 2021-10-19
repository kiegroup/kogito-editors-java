/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.styling.FontSize;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class DecisionTest {

    @Test
    public void testConstructor() {
        final Id id = mock(Id.class);
        final Description description = mock(Description.class);
        final Name name = mock(Name.class);
        final Question question = mock(Question.class);
        final AllowedAnswers allowedAnswers = mock(AllowedAnswers.class);
        final Expression expression = mock(Expression.class);
        final StylingSet stylingSet = mock(StylingSet.class);
        final GeneralRectangleDimensionsSet dimensionsSet = mock(GeneralRectangleDimensionsSet.class);

        final InformationItemPrimary variable = new InformationItemPrimary();
        final Decision expectedParent = new Decision(id,
                                                     description,
                                                     name,
                                                     question,
                                                     allowedAnswers,
                                                     variable,
                                                     expression,
                                                     stylingSet,
                                                     dimensionsSet);

        final DMNModelInstrumentedBase actualParent = variable.getParent();

        assertEquals(expectedParent, actualParent);
    }

    @Test
    public void testDifferentStylingSet() {

        final Decision modelOne = new Decision(new Id("123"),
                                               new Description(),
                                               new Name(),
                                               new Question(),
                                               new AllowedAnswers(),
                                               new InformationItemPrimary(new Id("346"),
                                                                          new Name(),
                                                                          new QName()),
                                               new FunctionDefinition(new Id("789"),
                                                                      new Description(),
                                                                      new QName(),
                                                                      null),
                                               new StylingSet(),
                                               new GeneralRectangleDimensionsSet());

        final Decision modelTwo = new Decision(new Id("123"),
                                               new Description(),
                                               new Name(),
                                               new Question(),
                                               new AllowedAnswers(),
                                               new InformationItemPrimary(new Id("346"),
                                                                          new Name(),
                                                                          new QName()),
                                               new FunctionDefinition(new Id("789"),
                                                                      new Description(),
                                                                      new QName(),
                                                                      null),
                                               new StylingSet(),
                                               new GeneralRectangleDimensionsSet());

        assertEquals(modelOne, modelTwo);

        modelOne.getStylingSet().setFontSize(new FontSize(10.0));
        modelTwo.getStylingSet().setFontSize(new FontSize(11.0));

        assertNotEquals(modelOne, modelTwo);
    }

    @Test
    public void testEqualsNotIgnoringId_WithDifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final Decision decision1 = new Decision();
        final Decision decision2 = new Decision();

        decision1.setId(id1);
        decision2.setId(id2);

        assertFalse(decision1.equals(decision2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_WithSameId() {
        final Id same = new Id();
        final Decision decision1 = new Decision();
        final Decision decision2 = new Decision();
        final InformationItemPrimary variable1 = mock(InformationItemPrimary.class);
        final InformationItemPrimary variable2 = mock(InformationItemPrimary.class);
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        decision1.setId(same);
        decision2.setId(same);

        when(variable1.equals(variable2, false)).thenReturn(true);
        when(expression1.equals(expression2, false)).thenReturn(true);

        decision1.setVariable(variable1);
        decision1.setExpression(expression1);
        decision2.setVariable(variable2);
        decision2.setExpression(expression2);

        final boolean result = decision1.equals(decision2, false);

        assertTrue(result);

        verify(variable1).equals(variable2, false);
        verify(expression1).equals(expression2, false);

        verify(variable1, never()).equals(variable2, true);
        verify(expression1, never()).equals(expression2, true);
    }

    @Test
    public void testEqualsIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final Decision decision1 = new Decision();
        final Decision decision2 = new Decision();
        final InformationItemPrimary variable1 = mock(InformationItemPrimary.class);
        final InformationItemPrimary variable2 = mock(InformationItemPrimary.class);
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        decision1.setId(id1);
        decision2.setId(id2);

        when(variable1.equals(variable2, true)).thenReturn(true);
        when(expression1.equals(expression2, true)).thenReturn(true);

        decision1.setVariable(variable1);
        decision1.setExpression(expression1);
        decision2.setVariable(variable2);
        decision2.setExpression(expression2);

        final boolean result = decision1.equals(decision2, true);

        assertTrue(result);

        verify(variable1).equals(variable2, true);
        verify(expression1).equals(expression2, true);

        verify(variable1, never()).equals(variable2, false);
        verify(expression1, never()).equals(expression2, false);
    }

    @Test
    public void testEqualsIgnoringId_SameId() {
        final Id same = new Id();
        final Decision decision1 = new Decision();
        final Decision decision2 = new Decision();
        final InformationItemPrimary variable1 = mock(InformationItemPrimary.class);
        final InformationItemPrimary variable2 = mock(InformationItemPrimary.class);
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        decision1.setId(same);
        decision2.setId(same);

        when(variable1.equals(variable2, true)).thenReturn(true);
        when(expression1.equals(expression2, true)).thenReturn(true);

        decision1.setVariable(variable1);
        decision1.setExpression(expression1);
        decision2.setVariable(variable2);
        decision2.setExpression(expression2);

        final boolean result = decision1.equals(decision2, true);

        assertTrue(result);

        verify(variable1).equals(variable2, true);
        verify(expression1).equals(expression2, true);

        verify(variable1, never()).equals(variable2, false);
        verify(expression1, never()).equals(expression2, false);
    }
}
