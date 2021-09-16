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

package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util;

import java.util.Collection;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Annotation;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Clause;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Column;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextEntryProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableRule;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.EntryInfo;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FeelFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.JavaFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PmmlFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionModelFillerTest {

    public static final String EXPRESSION_NAME = "Expression Name";
    public static final String DATA_TYPE = BuiltInType.UNDEFINED.asQName().getLocalPart();
    private static final String ENTRY_INFO_NAME = "Entry Info";
    private static final String ENTRY_INFO_DATA_TYPE = BuiltInType.STRING.asQName().getLocalPart();
    private static final String ENTRY_EXPRESSION_CONTENT = "content";
    private static final Double ENTRY_INFO_WIDTH = 200d;
    private static final Double ENTRY_EXPRESSION_WIDTH = 350d;
    private static final Double PARAMETERS_WIDTH = 450d;
    private static final String PARAM_NAME = "p-1";
    private static final String PARAM_DATA_TYPE = BuiltInType.BOOLEAN.asQName().getLocalPart();

    @Test
    public void testFillLiteralExpression() {
        final LiteralExpression literalExpression = new LiteralExpression();
        final String content = "content";
        final double width = 100d;
        final LiteralExpressionProps literalExpressionProps = new LiteralExpressionProps(EXPRESSION_NAME, DATA_TYPE, content, width);

        ExpressionModelFiller.fillLiteralExpression(literalExpression, literalExpressionProps);

        assertThat(literalExpression).isNotNull();
        assertThat(literalExpression.getText()).isNotNull();
        assertThat(literalExpression.getText().getValue()).isNotNull();
        assertThat(literalExpression.getText().getValue()).isEqualTo(content);
        assertThat(literalExpression.getComponentWidths()).isNotEmpty();
        assertThat(literalExpression.getComponentWidths()).first().isEqualTo(width);
    }

    @Test
    public void testFillContextExpression() {
        final String resultContent = "";
        final Context contextExpression = new Context();
        final ContextEntryProps[] contextEntries = new ContextEntryProps[]{
                buildContextEntryProps()
        };
        final ExpressionProps result = new LiteralExpressionProps("Result Expression", BuiltInType.DATE.asQName().getLocalPart(), resultContent, null);
        final ContextProps contextProps = new ContextProps(EXPRESSION_NAME, DATA_TYPE, contextEntries, result, ENTRY_INFO_WIDTH, ENTRY_EXPRESSION_WIDTH);

        ExpressionModelFiller.fillContextExpression(contextExpression, contextProps);

        assertThat(contextExpression).isNotNull();
        assertThat(contextExpression.getContextEntry()).isNotNull();
        assertThat(contextExpression.getContextEntry()).hasSize(2);
        assertThat(contextExpression.getContextEntry()).first().extracting(ContextEntry::getVariable).isNotNull();
        assertThat(contextExpression.getContextEntry()).first().extracting(entry -> entry.getVariable().getValue().getValue()).isEqualTo(ENTRY_INFO_NAME);
        assertThat(contextExpression.getContextEntry()).first().extracting(entry -> entry.getVariable().getTypeRef().getLocalPart()).isEqualTo(ENTRY_INFO_DATA_TYPE);
        assertThat(contextExpression.getContextEntry()).first().extracting(ContextEntry::getExpression).isNotNull();
        assertThat(contextExpression.getContextEntry()).first().extracting(ContextEntry::getExpression).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(contextExpression.getContextEntry()).first().extracting(entry -> ((LiteralExpression) entry.getExpression()).getText().getValue()).isEqualTo(ENTRY_EXPRESSION_CONTENT);
        assertThat(contextExpression.getContextEntry()).last().extracting(ContextEntry::getVariable).isNull();
        assertThat(contextExpression.getContextEntry()).last().extracting(ContextEntry::getExpression).isNotNull();
        assertThat(contextExpression.getContextEntry()).last().extracting(ContextEntry::getExpression).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(contextExpression.getContextEntry()).last().extracting(entry -> ((LiteralExpression) entry.getExpression()).getText().getValue()).isEqualTo(resultContent);
        assertEntryWidths(contextExpression.getComponentWidths());
    }

    @Test
    public void testFillRelationExpression() {
        final Relation relationExpression = new Relation();
        final String firstColumnName = "Column Name";
        final String firstColumnDataType = BuiltInType.BOOLEAN.asQName().getLocalPart();
        final double firstColumnWidth = 200d;
        final String secondColumnName = "Another Column Name";
        final String secondColumnDataType = BuiltInType.DATE.asQName().getLocalPart();
        final double secondColumnWidth = 315d;
        final String firstCell = "first cell";
        final String secondCell = "second cell";
        final String thirdCell = "third cell";
        final String fourthCell = "fourth cell";
        final Column[] columns = new Column[]{new Column(firstColumnName, firstColumnDataType, firstColumnWidth), new Column(secondColumnName, secondColumnDataType, secondColumnWidth)};
        final String[][] rows = new String[][]{new String[]{firstCell, secondCell}, new String[]{thirdCell, fourthCell}};
        final RelationProps relationProps = new RelationProps(EXPRESSION_NAME, DATA_TYPE, columns, rows);

        ExpressionModelFiller.fillRelationExpression(relationExpression, relationProps);

        assertThat(relationExpression).isNotNull();
        assertThat(relationExpression.getColumn()).isNotNull();
        assertThat(relationExpression.getColumn()).hasSize(2);
        assertThat(relationExpression.getColumn()).first().extracting(HasName::getValue).isNotNull();
        assertThat(relationExpression.getColumn()).first().extracting(informationItem -> informationItem.getValue().getValue()).isEqualTo(firstColumnName);
        assertThat(relationExpression.getColumn()).first().extracting(informationItem -> informationItem.getTypeRef().getLocalPart()).isEqualTo(firstColumnDataType);
        assertThat(relationExpression.getColumn()).last().extracting(HasName::getValue).isNotNull();
        assertThat(relationExpression.getColumn()).last().extracting(informationItem -> informationItem.getValue().getValue()).isEqualTo(secondColumnName);
        assertThat(relationExpression.getColumn()).last().extracting(informationItem -> informationItem.getTypeRef().getLocalPart()).isEqualTo(secondColumnDataType);
        assertThat(relationExpression.getRow()).isNotNull();
        assertThat(relationExpression.getRow()).hasSize(2);
        assertThat(relationExpression.getRow()).first().extracting(list -> list.getExpression().size()).isEqualTo(2);
        assertThat(relationExpression.getRow()).first().extracting(list -> list.getExpression().get(0).getExpression()).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(relationExpression.getRow()).first().extracting(list -> ((LiteralExpression) list.getExpression().get(0).getExpression()).getText().getValue()).isEqualTo(firstCell);
        assertThat(relationExpression.getRow()).first().extracting(list -> ((LiteralExpression) list.getExpression().get(1).getExpression()).getText().getValue()).isEqualTo(secondCell);
        assertThat(relationExpression.getRow()).last().extracting(list -> list.getExpression().size()).isEqualTo(2);
        assertThat(relationExpression.getRow()).last().extracting(list -> list.getExpression().get(0).getExpression()).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(relationExpression.getRow()).last().extracting(list -> ((LiteralExpression) list.getExpression().get(0).getExpression()).getText().getValue()).isEqualTo(thirdCell);
        assertThat(relationExpression.getRow()).last().extracting(list -> ((LiteralExpression) list.getExpression().get(1).getExpression()).getText().getValue()).isEqualTo(fourthCell);
        assertThat(relationExpression.getComponentWidths()).element(1).isEqualTo(firstColumnWidth);
        assertThat(relationExpression.getComponentWidths()).element(2).isEqualTo(secondColumnWidth);
    }

    @Test
    public void testFillListExpression() {
        final List listExpression = new List();
        final String nestedContent = "nested content";
        final ExpressionProps[] items = new ExpressionProps[]{new LiteralExpressionProps("Nested Literal Expression", BuiltInType.UNDEFINED.asQName().getLocalPart(), nestedContent, null)};
        final Double width = 600d;
        final ListProps listProps = new ListProps(EXPRESSION_NAME, DATA_TYPE, items, width);

        ExpressionModelFiller.fillListExpression(listExpression, listProps);

        assertThat(listExpression).isNotNull();
        assertThat(listExpression.getExpression()).isNotNull();
        assertThat(listExpression.getExpression()).hasSize(1);
        assertThat(listExpression.getExpression()).first().extracting(HasExpression::getExpression).isNotNull();
        assertThat(listExpression.getExpression()).first().extracting(HasExpression::getExpression).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(listExpression.getExpression()).first().extracting(item -> ((LiteralExpression) item.getExpression()).getText().getValue()).isEqualTo(nestedContent);
        assertThat(listExpression.getComponentWidths()).element(1).isEqualTo(width);
    }

    @Test
    public void testFillInvocationExpression() {
        final Invocation invocationExpression = new Invocation();
        final String invokedFunction = "f()";
        final ContextEntryProps[] bindingEntries = new ContextEntryProps[]{
                buildContextEntryProps()
        };
        final InvocationProps invocationProps = new InvocationProps(EXPRESSION_NAME, DATA_TYPE, invokedFunction, bindingEntries, ENTRY_INFO_WIDTH, ENTRY_EXPRESSION_WIDTH);

        ExpressionModelFiller.fillInvocationExpression(invocationExpression, invocationProps);

        assertThat(invocationExpression).isNotNull();
        assertThat(invocationExpression.getExpression()).isNotNull();
        assertThat(invocationExpression.getExpression()).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(((LiteralExpression) invocationExpression.getExpression()).getText().getValue()).isEqualTo(invokedFunction);
        assertThat(invocationExpression.getBinding()).isNotNull();
        assertThat(invocationExpression.getBinding()).hasSize(1);
        assertThat(invocationExpression.getBinding()).first().extracting(Binding::getVariable).isNotNull();
        assertThat(invocationExpression.getBinding()).first().extracting(entry -> entry.getVariable().getValue().getValue()).isEqualTo(ENTRY_INFO_NAME);
        assertThat(invocationExpression.getBinding()).first().extracting(entry -> entry.getVariable().getTypeRef().getLocalPart()).isEqualTo(ENTRY_INFO_DATA_TYPE);
        assertThat(invocationExpression.getBinding()).first().extracting(Binding::getExpression).isNotNull();
        assertThat(invocationExpression.getBinding()).first().extracting(Binding::getExpression).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(invocationExpression.getBinding()).first().extracting(entry -> ((LiteralExpression) entry.getExpression()).getText().getValue()).isEqualTo(ENTRY_EXPRESSION_CONTENT);
        assertEntryWidths(invocationExpression.getComponentWidths());
    }

    @Test
    public void testFillPMMLFunctionExpression() {
        final FunctionDefinition functionExpression = new FunctionDefinition();
        final String documentName = "document name";
        final String modelName = "model name";
        final PmmlFunctionProps functionProps = new PmmlFunctionProps(EXPRESSION_NAME, DATA_TYPE, new EntryInfo[]{new EntryInfo(PARAM_NAME, PARAM_DATA_TYPE)}, PARAMETERS_WIDTH, documentName, modelName);

        ExpressionModelFiller.fillFunctionExpression(functionExpression, functionProps);

        assertThat(functionExpression).isNotNull();
        assertFormalParameters(functionExpression);
        assertParameterWidth(functionExpression);
        assertNestedContextEntries(functionExpression, documentName, modelName);
    }

    @Test
    public void testFillJavaFunctionExpression() {
        final FunctionDefinition functionExpression = new FunctionDefinition();
        final String className = "class name";
        final String methodName = "method name";
        final JavaFunctionProps functionProps = new JavaFunctionProps(EXPRESSION_NAME, DATA_TYPE, new EntryInfo[]{new EntryInfo(PARAM_NAME, PARAM_DATA_TYPE)}, PARAMETERS_WIDTH, className, methodName);

        ExpressionModelFiller.fillFunctionExpression(functionExpression, functionProps);

        assertThat(functionExpression).isNotNull();
        assertFormalParameters(functionExpression);
        assertParameterWidth(functionExpression);
        assertNestedContextEntries(functionExpression, className, methodName);
    }

    @Test
    public void testFillFeelFunctionExpression() {
        final FunctionDefinition functionExpression = new FunctionDefinition();
        final String nestedContent = "Nested Content";
        final FeelFunctionProps functionProps = new FeelFunctionProps(EXPRESSION_NAME, DATA_TYPE, new EntryInfo[]{new EntryInfo(PARAM_NAME, PARAM_DATA_TYPE)}, PARAMETERS_WIDTH,
                                                                      new LiteralExpressionProps("Nested Literal Expression", BuiltInType.UNDEFINED.asQName().getLocalPart(), nestedContent, null));

        ExpressionModelFiller.fillFunctionExpression(functionExpression, functionProps);

        assertThat(functionExpression).isNotNull();
        assertFormalParameters(functionExpression);
        assertParameterWidth(functionExpression);
        assertThat(functionExpression.getExpression()).isNotNull();
        assertThat(functionExpression.getExpression()).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(((LiteralExpression) functionExpression.getExpression()).getText().getValue()).isEqualTo(nestedContent);
    }

    @Test
    public void testFillDecisionTableExpression() {
        final DecisionTable decisionTableExpression = new DecisionTable();
        final String annotationName = "Annotation name";
        final double annotationWidth = 456d;
        final Annotation[] annotations = new Annotation[]{new Annotation(annotationName, annotationWidth)};
        final String inputColumn = "Input column";
        final String inputDataType = BuiltInType.DATE_TIME.asQName().getLocalPart();
        final double inputWidth = 123d;
        final Clause[] input = new Clause[]{new Clause(inputColumn, inputDataType, inputWidth)};
        final String outputColumn = "Output column";
        final String outputDataType = BuiltInType.STRING.asQName().getLocalPart();
        final double outputWidth = 223d;
        final Clause[] output = new Clause[]{new Clause(outputColumn, outputDataType, outputWidth)};
        final String inputValue = "input value";
        final String outputValue = "output value";
        final String annotationValue = "annotation value";
        DecisionTableRule[] rules = new DecisionTableRule[]{new DecisionTableRule(new String[]{inputValue}, new String[]{outputValue}, new String[]{annotationValue})};
        final DecisionTableProps decisionTableProps = new DecisionTableProps(EXPRESSION_NAME, DATA_TYPE, HitPolicy.COLLECT.value(), BuiltinAggregator.MAX.getCode(), annotations, input, output, rules);

        ExpressionModelFiller.fillDecisionTableExpression(decisionTableExpression, decisionTableProps);

        assertThat(decisionTableExpression).isNotNull();
        assertThat(decisionTableExpression.getHitPolicy()).isEqualTo(HitPolicy.COLLECT);
        assertThat(decisionTableExpression.getAggregation()).isEqualTo(BuiltinAggregator.MAX);
        assertThat(decisionTableExpression.getAnnotations()).isNotNull();
        assertThat(decisionTableExpression.getAnnotations()).hasSize(1);
        assertThat(decisionTableExpression.getAnnotations()).first().extracting(annotation -> annotation.getValue().getValue()).isEqualTo(annotationName);
        assertThat(decisionTableExpression.getInput()).isNotNull();
        assertThat(decisionTableExpression.getInput()).hasSize(1);
        assertThat(decisionTableExpression.getInput()).first().extracting(InputClause::getInputExpression).isNotNull();
        assertThat(decisionTableExpression.getInput()).first().extracting(inputClause -> inputClause.getInputExpression().getText().getValue()).isEqualTo(inputColumn);
        assertThat(decisionTableExpression.getInput()).first().extracting(inputClause -> inputClause.getInputExpression().getTypeRef().getLocalPart()).isEqualTo(inputDataType);
        assertThat(decisionTableExpression.getOutput()).isNotNull();
        assertThat(decisionTableExpression.getOutput()).hasSize(1);
        assertThat(decisionTableExpression.getOutput()).first().extracting(OutputClause::getName).isEqualTo(outputColumn);
        assertThat(decisionTableExpression.getOutput()).first().extracting(outputClause -> outputClause.getTypeRef().getLocalPart()).isEqualTo(outputDataType);
        assertThat(decisionTableExpression.getRule()).isNotNull();
        assertThat(decisionTableExpression.getRule()).hasSize(1);
        assertThat(decisionTableExpression.getRule()).first().extracting(rule -> rule.getInputEntry().size()).isEqualTo(1);
        assertThat(decisionTableExpression.getRule()).first().extracting(DecisionRule::getInputEntry).extracting(inputEntry -> inputEntry.get(0).getText().getValue()).isEqualTo(inputValue);
        assertThat(decisionTableExpression.getRule()).first().extracting(DecisionRule::getOutputEntry).extracting(outputEntry -> outputEntry.get(0).getText().getValue()).isEqualTo(outputValue);
        assertThat(decisionTableExpression.getRule()).first().extracting(DecisionRule::getAnnotationEntry).extracting(annotationEntry -> annotationEntry.get(0).getText().getValue()).isEqualTo(annotationValue);
        assertThat(decisionTableExpression.getComponentWidths()).isNotNull();
        assertThat(decisionTableExpression.getComponentWidths()).hasSize(4);
        assertThat(decisionTableExpression.getComponentWidths()).element(1).isEqualTo(inputWidth);
        assertThat(decisionTableExpression.getComponentWidths()).element(2).isEqualTo(outputWidth);
        assertThat(decisionTableExpression.getComponentWidths()).element(3).isEqualTo(annotationWidth);
    }

    private ContextEntryProps buildContextEntryProps() {
        return new ContextEntryProps(new EntryInfo(ENTRY_INFO_NAME, ENTRY_INFO_DATA_TYPE), new LiteralExpressionProps("Nested Expression", BuiltInType.UNDEFINED.asQName().getLocalPart(), ENTRY_EXPRESSION_CONTENT, null));
    }

    private void assertEntryWidths(final Collection<Double> componentWidths) {
        assertThat(componentWidths).isNotNull();
        assertThat(componentWidths).hasSize(3);
        assertThat(componentWidths).element(1).isEqualTo(ENTRY_INFO_WIDTH);
        assertThat(componentWidths).element(2).isEqualTo(ENTRY_EXPRESSION_WIDTH);
    }

    private void assertFormalParameters(FunctionDefinition functionExpression) {
        assertThat(functionExpression.getFormalParameter()).isNotNull();
        assertThat(functionExpression.getFormalParameter()).hasSize(1);
        assertThat(functionExpression.getFormalParameter()).first().extracting(param -> param.getValue().getValue()).isEqualTo(PARAM_NAME);
        assertThat(functionExpression.getFormalParameter()).first().extracting(param -> param.getTypeRef().getLocalPart()).isEqualTo(PARAM_DATA_TYPE);
    }

    private void assertParameterWidth(FunctionDefinition functionExpression) {
        assertThat(functionExpression.getComponentWidths()).isNotNull();
        assertThat(functionExpression.getComponentWidths()).hasSize(2);
        assertThat(functionExpression.getComponentWidths()).element(1).isEqualTo(PARAMETERS_WIDTH);
    }

    private void assertNestedContextEntries(FunctionDefinition functionExpression, String documentName, String modelName) {
        assertThat(functionExpression.getExpression()).isNotNull();
        assertThat(functionExpression.getExpression()).isExactlyInstanceOf(Context.class);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).isNotNull();
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).hasSize(2);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).first().extracting(ContextEntry::getExpression).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).first().extracting(contextEntry -> ((LiteralExpression) contextEntry.getExpression()).getText().getValue()).isEqualTo(documentName);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).last().extracting(ContextEntry::getExpression).isExactlyInstanceOf(LiteralExpression.class);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).last().extracting(contextEntry -> ((LiteralExpression) contextEntry.getExpression()).getText().getValue()).isEqualTo(modelName);
    }
}
