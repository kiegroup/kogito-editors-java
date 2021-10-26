/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Annotation;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Clause;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Column;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextEntryProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableRule;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class UserActionCheckerTest {

    private static final String NAME = "name";
    private static final String AGGREGATION = "aggregation";
    private static final String DATA_TYPE = "dataType";
    private static final String HIT_POLICY = "hitPolicy";

    private UserActionChecker checker;

    @Before
    public void setup() {
        checker = spy(new UserActionChecker());
    }

    @Test
    public void testIsUserAction_WhenIsDecisionTable() {

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             "data type",
                                                                             "hit policy",
                                                                             AGGREGATION,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             null);

        doReturn(true).when(checker).isUserAction(any(DecisionTableProps.class));

        final boolean isUserAction = checker.isUserAction((ExpressionProps) decisionTableProps);

        verify(checker).isUserAction(decisionTableProps);
        verify(checker, never()).isUserAction(any(ContextProps.class));
        verify(checker, never()).isUserAction(any(InvocationProps.class));
        verify(checker, never()).isUserAction(any(RelationProps.class));

        assertTrue(isUserAction);
    }

    @Test
    public void testUserAction_WhenIsContext() {

        final ContextProps contextProps = new ContextProps(NAME,
                                                           null,
                                                           null,
                                                           null,
                                                           null,
                                                           null);

        doReturn(true).when(checker).isUserAction(any(ContextProps.class));

        final boolean isUserAction = checker.isUserAction((ExpressionProps) contextProps);

        verify(checker, never()).isUserAction(any(DecisionTableProps.class));
        verify(checker).isUserAction(contextProps);
        verify(checker, never()).isUserAction(any(InvocationProps.class));
        verify(checker, never()).isUserAction(any(RelationProps.class));

        assertTrue(isUserAction);
    }

    @Test
    public void testIsUserAction_WhenIsRelation() {

        final RelationProps relationProps = new RelationProps(NAME,
                                                              null,
                                                              null,
                                                              null);

        doReturn(true).when(checker).isUserAction(any(RelationProps.class));

        final boolean isUserAction = checker.isUserAction((ExpressionProps) relationProps);

        verify(checker, never()).isUserAction(any(DecisionTableProps.class));
        verify(checker, never()).isUserAction(any(ContextProps.class));
        verify(checker, never()).isUserAction(any(InvocationProps.class));
        verify(checker).isUserAction(relationProps);

        assertTrue(isUserAction);
    }

    @Test
    public void testIsUserAction_WhenIsInvocation() {

        final InvocationProps invocationProps = new InvocationProps(NAME,
                                                                    null,
                                                                    null,
                                                                    null,
                                                                    null,
                                                                    null);

        doReturn(true).when(checker).isUserAction(any(InvocationProps.class));

        final boolean isUserAction = checker.isUserAction((ExpressionProps) invocationProps);

        verify(checker, never()).isUserAction(any(DecisionTableProps.class));
        verify(checker, never()).isUserAction(any(ContextProps.class));
        verify(checker).isUserAction(invocationProps);
        verify(checker, never()).isUserAction(any(RelationProps.class));

        assertTrue(isUserAction);
    }

    @Test
    public void testIsUserAction_DecisionTablePropsOverload_WhenItIs() {

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             "data type",
                                                                             "hit policy",
                                                                             AGGREGATION,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             null);

        doReturn(true).when(checker).haveAllClauses(decisionTableProps);
        doReturn(true).when(checker).haveAtLeastOneColumnSizeDefined(decisionTableProps);
        doReturn(true).when(checker).areRulesLoaded(decisionTableProps);

        final boolean isUserAction = checker.isUserAction(decisionTableProps);

        assertTrue(isUserAction);

        verify(checker).haveAllClauses(decisionTableProps);
        verify(checker).haveAtLeastOneColumnSizeDefined(decisionTableProps);
        verify(checker).areRulesLoaded(decisionTableProps);
    }

    @Test
    public void testIsUserAction_DecisionTablePropsOverload_WhenRulesAreNotLoaded() {

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             "data type",
                                                                             "hit policy",
                                                                             AGGREGATION,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             null);

        doReturn(true).when(checker).haveAllClauses(decisionTableProps);
        doReturn(true).when(checker).haveAtLeastOneColumnSizeDefined(decisionTableProps);
        doReturn(false).when(checker).areRulesLoaded(decisionTableProps);

        final boolean isUserAction = checker.isUserAction(decisionTableProps);

        assertFalse(isUserAction);

        verify(checker).haveAllClauses(decisionTableProps);
        verify(checker).haveAtLeastOneColumnSizeDefined(decisionTableProps);
        verify(checker).areRulesLoaded(decisionTableProps);
    }

    @Test
    public void testIsUserAction_DecisionTablePropsOverload_WhenDoesntHaveColumnSizeDefined() {

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             "data type",
                                                                             "hit policy",
                                                                             AGGREGATION,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             null);

        doReturn(true).when(checker).haveAllClauses(decisionTableProps);
        doReturn(false).when(checker).haveAtLeastOneColumnSizeDefined(decisionTableProps);
        doReturn(true).when(checker).areRulesLoaded(decisionTableProps);

        final boolean isUserAction = checker.isUserAction(decisionTableProps);

        assertFalse(isUserAction);

        verify(checker).haveAllClauses(decisionTableProps);
        verify(checker).haveAtLeastOneColumnSizeDefined(decisionTableProps);
        verify(checker, never()).areRulesLoaded(decisionTableProps);
    }

    @Test
    public void tesIsUserAction_DecisionTablePropsOverload_WhenDoesntHaveAllClauses() {

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             "data type",
                                                                             "hit policy",
                                                                             AGGREGATION,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             null);

        doReturn(false).when(checker).haveAllClauses(decisionTableProps);

        final boolean isUserAction = checker.isUserAction(decisionTableProps);

        assertFalse(isUserAction);

        verify(checker).haveAllClauses(decisionTableProps);
        verify(checker, never()).haveAtLeastOneColumnSizeDefined(decisionTableProps);
        verify(checker, never()).areRulesLoaded(decisionTableProps);
    }

    @Test
    public void testAreRulesLoaded() {

        final DecisionTableRule rule1 = mock(DecisionTableRule.class);
        final DecisionTableRule rule2 = mock(DecisionTableRule.class);

        final DecisionTableRule[] rules = new DecisionTableRule[]{rule1, rule2};

        final DecisionTableProps expressionProps = new DecisionTableProps(NAME,
                                                                          "data type",
                                                                          "hit policy",
                                                                          AGGREGATION,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          rules);

        doReturn(false).when(checker).ruleHaveNullClauses(rule1);
        doReturn(false).when(checker).ruleHaveNullClauses(rule2);
        doReturn(true).when(checker).haveAllEntries(expressionProps, rule1);
        doReturn(true).when(checker).haveAllEntries(expressionProps, rule2);

        final boolean areRulesLoaded = checker.areRulesLoaded(expressionProps);

        assertTrue(areRulesLoaded);

        verify(checker).haveAllEntries(expressionProps, rule1);
        verify(checker).haveAllEntries(expressionProps, rule2);
        verify(checker).ruleHaveNullClauses(rule1);
        verify(checker).ruleHaveNullClauses(rule2);
    }

    @Test
    public void testAreRulesLoaded_WhenDoesntHaveAllEntries() {

        final DecisionTableRule rule1 = mock(DecisionTableRule.class);

        final DecisionTableRule[] rules = new DecisionTableRule[]{rule1};

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             "data type",
                                                                             "hit policy",
                                                                             AGGREGATION,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             rules);

        doReturn(false).when(checker).ruleHaveNullClauses(rule1);
        doReturn(false).when(checker).haveAllEntries(decisionTableProps, rule1);

        final boolean areRulesLoaded = checker.areRulesLoaded(decisionTableProps);

        assertFalse(areRulesLoaded);

        verify(checker).haveAllEntries(decisionTableProps, rule1);
    }

    @Test
    public void testAreRulesLoaded_WhenRuleHaveNullClauses() {

        final DecisionTableRule rule1 = mock(DecisionTableRule.class);

        final DecisionTableRule[] rules = new DecisionTableRule[]{rule1};

        final DecisionTableProps expressionProps = new DecisionTableProps(NAME,
                                                                          "data type",
                                                                          "hit policy",
                                                                          AGGREGATION,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          rules);

        doReturn(true).when(checker).ruleHaveNullClauses(rule1);
        doReturn(true).when(checker).haveAllEntries(expressionProps, rule1);

        final boolean areValid = checker.areRulesLoaded(expressionProps);

        assertFalse(areValid);

        verify(checker).haveAllEntries(expressionProps, rule1);
        verify(checker).ruleHaveNullClauses(rule1);
    }

    @Test
    public void testRuleHaveNullClauses() {

        final String[] inputEntries = new String[]{"input"};
        final String[] outputEntries = new String[]{"output"};
        final String[] annotationEntries = new String[]{"annotation"};

        final DecisionTableRule rule = new DecisionTableRule(inputEntries,
                                                             outputEntries,
                                                             annotationEntries);

        final boolean haveHull = checker.ruleHaveNullClauses(rule);

        assertFalse(haveHull);
    }

    @Test
    public void testRuleHaveNullClauses_WhenInputHasNull() {

        final String[] inputEntries = new String[]{"input", null};
        final String[] outputEntries = new String[]{"output"};
        final String[] annotationEntries = new String[]{"annotation"};

        final DecisionTableRule rule = new DecisionTableRule(inputEntries,
                                                             outputEntries,
                                                             annotationEntries);

        final boolean haveHull = checker.ruleHaveNullClauses(rule);

        assertTrue(haveHull);
    }

    @Test
    public void testRuleHaveNullClauses_WhenOutputHasNull() {

        final String[] inputEntries = new String[]{"input"};
        final String[] outputEntries = new String[]{"output", null};
        final String[] annotationEntries = new String[]{"annotation"};

        final DecisionTableRule rule = new DecisionTableRule(inputEntries,
                                                             outputEntries,
                                                             annotationEntries);

        final boolean haveHull = checker.ruleHaveNullClauses(rule);

        assertTrue(haveHull);
    }

    @Test
    public void testRuleHaveNullClauses_WhenAnnotationsHasNull() {

        final String[] inputEntries = new String[]{"input"};
        final String[] outputEntries = new String[]{"output"};
        final String[] annotationEntries = new String[]{"annotation", null};

        final DecisionTableRule rule = new DecisionTableRule(inputEntries,
                                                             outputEntries,
                                                             annotationEntries);

        final boolean haveHull = checker.ruleHaveNullClauses(rule);

        assertTrue(haveHull);
    }

    @Test
    public void testIsUserAction_ContextPropsOverload() {

        final ExpressionProps expression1 = mock(ExpressionProps.class);
        final ExpressionProps expression2 = mock(ExpressionProps.class);

        final ContextEntryProps entry1 = new ContextEntryProps(null, expression1);
        final ContextEntryProps entry2 = new ContextEntryProps(null, expression2);

        final ContextProps props = new ContextProps(null,
                                                    null,
                                                    new ContextEntryProps[]{entry1, entry2},
                                                    null,
                                                    null,
                                                    null);

        doReturn(true).when(checker).isUserAction(expression1);
        doReturn(true).when(checker).isUserAction(expression2);

        final boolean isValid = checker.isUserAction(props);

        assertTrue(isValid);

        verify(checker).isUserAction(expression1);
        verify(checker).isUserAction(expression2);
    }

    @Test
    public void testIsUserAction_ContextPropsOverload_WhenItIsNot() {

        final ExpressionProps expression1 = mock(ExpressionProps.class);

        final ContextEntryProps entry1 = new ContextEntryProps(null, expression1);

        final ContextProps props = new ContextProps(null,
                                                    null,
                                                    new ContextEntryProps[]{entry1},
                                                    null,
                                                    null,
                                                    null);

        doReturn(false).when(checker).isUserAction(expression1);

        final boolean isUserAction = checker.isUserAction(props);

        assertFalse(isUserAction);

        verify(checker).isUserAction(expression1);
    }

    @Test
    public void testIsUserAction_ContextPropsOverload_WhenDoesNotContainsEntries() {

        final ContextProps props = new ContextProps(null,
                                                    null,
                                                    null,
                                                    null,
                                                    null,
                                                    null);

        final boolean isUserAction = checker.isUserAction(props);

        assertFalse(isUserAction);
    }

    @Test
    public void testColumnsMatchesRows() {

        final Column[] columns = new Column[3];
        final String[][] rows = new String[1][3];

        final boolean matches = checker.columnsMatchesRows(columns, rows);

        assertTrue(matches);
    }

    @Test
    public void testColumnsMatchesRows_WhenDoesnt() {

        final Column[] columns = new Column[2];
        final String[][] rows = new String[1][3];

        final boolean matches = checker.columnsMatchesRows(columns, rows);

        assertFalse(matches);
    }

    @Test
    public void testIsUserAction_RelationPropsOverload() {

        final Column column1 = new Column(null, null, 0.0d);
        final Column column2 = new Column(null, null, 0.0d);
        final Column[] columns = Arrays.array(column1, column2);
        final String[][] rows = new String[0][];

        final RelationProps relationProps = new RelationProps(NAME,
                                                              DATA_TYPE,
                                                              columns,
                                                              new String[0][]);
        doReturn(true).when(checker).columnsMatchesRows(columns, rows);

        final boolean isUserAction = checker.isUserAction(relationProps);

        assertTrue(isUserAction);

        verify(checker).columnsMatchesRows(columns, rows);
    }

    @Test
    public void testIsUserAction_RelationPropsOverload_WhenColumnsDoesntMatchRows() {

        final Column column1 = new Column(null, null, 0.0d);
        final Column column2 = new Column(null, null, 0.0d);
        final Column[] columns = Arrays.array(column1, column2);
        final String[][] rows = new String[0][];

        final RelationProps relationProps = new RelationProps(NAME,
                                                              DATA_TYPE,
                                                              columns,
                                                              new String[0][]);
        doReturn(false).when(checker).columnsMatchesRows(columns, rows);

        final boolean isValid = checker.isUserAction(relationProps);

        assertFalse(isValid);

        verify(checker).columnsMatchesRows(columns, rows);
    }

    @Test
    public void testUserAction_RelationPropsOverload_WhenDoestHaveColumnWithNullWidth() {

        final Column column1 = new Column(null, null, 0.0d);
        final Column column2 = new Column(null, null, null);
        final Column[] columns = Arrays.array(column1, column2);
        final String[][] rows = new String[0][];

        final RelationProps relationProps = new RelationProps(NAME,
                                                              DATA_TYPE,
                                                              columns,
                                                              new String[0][]);
        doReturn(true).when(checker).columnsMatchesRows(columns, rows);

        final boolean isValid = checker.isUserAction(relationProps);

        assertFalse(isValid);

        verify(checker).columnsMatchesRows(columns, rows);
    }

    @Test
    public void testHaveAllClauses() {

        final Annotation[] annotations = new Annotation[0];
        final Clause[] input = new Clause[0];
        final Clause[] output = new Clause[0];

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean haveAllClauses = checker.haveAllClauses(decisionTableProps);

        assertTrue(haveAllClauses);
    }

    @Test
    public void testHaveAllClauses_WhenDoesntHaveInput() {

        final Annotation[] annotations = new Annotation[0];
        final Clause[] output = new Clause[0];

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             null,
                                                                             output,
                                                                             null);

        final boolean haveAllClauses = checker.haveAllClauses(decisionTableProps);

        assertFalse(haveAllClauses);
    }

    @Test
    public void testHaveAllClauses_WhenDoesntHaveAnnotations() {

        final Clause[] input = new Clause[0];
        final Clause[] output = new Clause[0];

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             null,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean haveAllClauses = checker.haveAllClauses(decisionTableProps);

        assertFalse(haveAllClauses);
    }

    @Test
    public void testHaveAllClauses_WhenDoesntHaveOutput() {

        final Annotation[] annotations = new Annotation[0];
        final Clause[] input = new Clause[0];

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             null,
                                                                             null);

        final boolean haveAllClauses = checker.haveAllClauses(decisionTableProps);

        assertFalse(haveAllClauses);
    }

    @Test
    public void testHaveAtLeastOneColumnSizeDefined_WhenDoesnt() {

        final Annotation annotation1 = new Annotation(NAME, null);
        final Annotation annotation2 = new Annotation(NAME, null);
        final Clause input1 = new Clause(NAME, DATA_TYPE, null);
        final Clause input2 = new Clause(NAME, DATA_TYPE, null);
        final Clause output1 = new Clause(NAME, DATA_TYPE, null);
        final Clause output2 = new Clause(NAME, DATA_TYPE, null);
        final Annotation[] annotations = Arrays.array(annotation1, annotation2);
        final Clause[] input = Arrays.array(input1, input2);
        final Clause[] output = Arrays.array(output1, output2);

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean isDefined = checker.haveAtLeastOneColumnSizeDefined(decisionTableProps);

        assertFalse(isDefined);
    }

    @Test
    public void testHaveAtLeastOneColumnSizeDefined_WhenIsAnInput() {

        final Annotation annotation1 = new Annotation(NAME, null);
        final Annotation annotation2 = new Annotation(NAME, null);
        final Clause input1 = new Clause(NAME, DATA_TYPE, null);
        final Clause input2 = new Clause(NAME, DATA_TYPE, 1.0);
        final Clause output1 = new Clause(NAME, DATA_TYPE, null);
        final Clause output2 = new Clause(NAME, DATA_TYPE, null);
        final Annotation[] annotations = Arrays.array(annotation1, annotation2);
        final Clause[] input = Arrays.array(input1, input2);
        final Clause[] output = Arrays.array(output1, output2);

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean isDefined = checker.haveAtLeastOneColumnSizeDefined(decisionTableProps);

        assertTrue(isDefined);
    }

    @Test
    public void testHaveAtLeastOneColumnSizeDefined_WhenIsAnOutput() {

        final Annotation annotation1 = new Annotation(NAME, null);
        final Annotation annotation2 = new Annotation(NAME, null);
        final Clause input1 = new Clause(NAME, DATA_TYPE, null);
        final Clause input2 = new Clause(NAME, DATA_TYPE, null);
        final Clause output1 = new Clause(NAME, DATA_TYPE, null);
        final Clause output2 = new Clause(NAME, DATA_TYPE, 1.0);
        final Annotation[] annotations = Arrays.array(annotation1, annotation2);
        final Clause[] input = Arrays.array(input1, input2);
        final Clause[] output = Arrays.array(output1, output2);

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean isDefined = checker.haveAtLeastOneColumnSizeDefined(decisionTableProps);

        assertTrue(isDefined);
    }

    @Test
    public void testHaveAtLeastOneColumnSizeDefined_WhenIsAnAnnotation() {

        final Annotation annotation1 = new Annotation(NAME, null);
        final Annotation annotation2 = new Annotation(NAME, 1.0);
        final Clause input1 = new Clause(NAME, DATA_TYPE, null);
        final Clause input2 = new Clause(NAME, DATA_TYPE, null);
        final Clause output1 = new Clause(NAME, DATA_TYPE, null);
        final Clause output2 = new Clause(NAME, DATA_TYPE, null);
        final Annotation[] annotations = Arrays.array(annotation1, annotation2);
        final Clause[] input = Arrays.array(input1, input2);
        final Clause[] output = Arrays.array(output1, output2);

        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean isDefined = checker.haveAtLeastOneColumnSizeDefined(decisionTableProps);

        assertTrue(isDefined);
    }

    @Test
    public void testHaveAllEntries() {

        final String[] inputEntries = new String[0];
        final String[] annotationEntries = new String[0];
        final String[] outputEntries = new String[0];
        final DecisionTableRule rule = new DecisionTableRule(inputEntries, outputEntries, annotationEntries);

        final Annotation[] annotations = new Annotation[0];
        final Clause[] input = new Clause[0];
        final Clause[] output = new Clause[0];
        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean haveAllEntries = checker.haveAllEntries(decisionTableProps, rule);

        assertTrue(haveAllEntries);
    }

    @Test
    public void testHaveAllEntries_WhenDoesntHaveInputEntries() {

        final String[] inputEntries = new String[1];
        final String[] annotationEntries = new String[0];
        final String[] outputEntries = new String[0];
        final DecisionTableRule rule = new DecisionTableRule(inputEntries, outputEntries, annotationEntries);

        final Annotation[] annotations = new Annotation[0];
        final Clause[] input = new Clause[0];
        final Clause[] output = new Clause[0];
        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean haveAllEntries = checker.haveAllEntries(decisionTableProps, rule);

        assertFalse(haveAllEntries);
    }

    @Test
    public void testHaveAllEntries_WhenDoesntHaveOutputEntries() {

        final String[] inputEntries = new String[0];
        final String[] annotationEntries = new String[0];
        final String[] outputEntries = new String[1];
        final DecisionTableRule rule = new DecisionTableRule(inputEntries, outputEntries, annotationEntries);

        final Annotation[] annotations = new Annotation[0];
        final Clause[] input = new Clause[0];
        final Clause[] output = new Clause[0];
        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean haveAllEntries = checker.haveAllEntries(decisionTableProps, rule);

        assertFalse(haveAllEntries);
    }

    @Test
    public void testHaveAllEntries_WhenDoesntHaveAnnotationEntries() {

        final String[] inputEntries = new String[0];
        final String[] annotationEntries = new String[1];
        final String[] outputEntries = new String[0];
        final DecisionTableRule rule = new DecisionTableRule(inputEntries, outputEntries, annotationEntries);

        final Annotation[] annotations = new Annotation[0];
        final Clause[] input = new Clause[0];
        final Clause[] output = new Clause[0];
        final DecisionTableProps decisionTableProps = new DecisionTableProps(NAME,
                                                                             DATA_TYPE,
                                                                             HIT_POLICY,
                                                                             AGGREGATION,
                                                                             annotations,
                                                                             input,
                                                                             output,
                                                                             null);

        final boolean haveAllEntries = checker.haveAllEntries(decisionTableProps, rule);

        assertFalse(haveAllEntries);
    }

    @Test
    public void testUserAction_InvocationPropsOverload() {

        final ExpressionProps entryExpression1 = mock(ExpressionProps.class);
        final ExpressionProps entryExpression2 = mock(ExpressionProps.class);

        final ContextEntryProps entry1 = new ContextEntryProps(null, entryExpression1);
        final ContextEntryProps entry2 = new ContextEntryProps(null, entryExpression2);

        final ContextEntryProps[] bindingEntries = Arrays.array(entry1, entry2);

        doReturn(true).when(checker).isUserAction(entryExpression1);
        doReturn(true).when(checker).isUserAction(entryExpression2);

        final InvocationProps props = new InvocationProps(NAME,
                                                          DATA_TYPE,
                                                          null,
                                                          bindingEntries,
                                                          null,
                                                          null);

        final boolean isUserAction = checker.isUserAction(props);

        assertTrue(isUserAction);

        verify(checker).isUserAction(entryExpression1);
        verify(checker).isUserAction(entryExpression2);
    }

    @Test
    public void testUserAction_InvocationPropsOverload_WhenThereIsNoBindingEntries() {

        final InvocationProps props = new InvocationProps(NAME,
                                                          DATA_TYPE,
                                                          null,
                                                          null,
                                                          null,
                                                          null);

        final boolean isUserAction = checker.isUserAction(props);

        assertTrue(isUserAction);
    }

    @Test
    public void testUserAction_InvocationPropsOverload_WhenThereIsAnNonUserExpression() {

        final ExpressionProps entryExpression1 = mock(ExpressionProps.class);
        final ExpressionProps entryExpression2 = mock(ExpressionProps.class);

        final ContextEntryProps entry1 = new ContextEntryProps(null, entryExpression1);
        final ContextEntryProps entry2 = new ContextEntryProps(null, entryExpression2);

        final ContextEntryProps[] bindingEntries = Arrays.array(entry1, entry2);

        doReturn(true).when(checker).isUserAction(entryExpression1);
        doReturn(false).when(checker).isUserAction(entryExpression2);

        final InvocationProps props = new InvocationProps(NAME,
                                                          DATA_TYPE,
                                                          null,
                                                          bindingEntries,
                                                          null,
                                                          null);

        final boolean isUserAction = checker.isUserAction(props);

        assertFalse(isUserAction);

        verify(checker).isUserAction(entryExpression1);
        verify(checker).isUserAction(entryExpression2);
    }
}
