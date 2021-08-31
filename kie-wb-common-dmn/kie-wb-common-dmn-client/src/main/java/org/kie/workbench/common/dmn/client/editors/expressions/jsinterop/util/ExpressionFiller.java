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

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Column;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextEntryProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.EntryInfo;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FeelFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.JavaFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PmmlFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;

import static org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT;
import static org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.CONTEXT;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.DECISION_TABLE;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.FUNCTION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.INVOCATION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.LIST;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.LITERAL_EXPRESSION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.RELATION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.UNDEFINED;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.java.JavaFunctionEditorDefinition.VARIABLE_CLASS;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.java.JavaFunctionEditorDefinition.VARIABLE_METHOD_SIGNATURE;

public class ExpressionFiller {

    public static void fillLiteralExpression(final LiteralExpression literalExpression, final LiteralExpressionProps literalExpressionProps) {
        literalExpression.setText(new Text(literalExpressionProps.content));
    }

    public static void fillContextExpression(final Context contextExpression, final ContextProps contextProps) {
        contextExpression.getContextEntry().clear();
        contextExpression.getContextEntry().addAll(contextEntriesConvertForContextExpression(contextProps));
        contextExpression.getContextEntry().add(entryResultConvertForContextExpression(contextProps));
    }

    public static void fillRelationExpression(final Relation relationExpression, final RelationProps relationProps) {
        relationExpression.getColumn().clear();
        relationExpression.getColumn().addAll(columnsConvertForRelationExpression(relationProps));
        relationExpression.getRow().clear();
        relationExpression.getRow().addAll(rowsConvertForRelationExpression(relationProps, relationExpression));
    }

    public static void fillListExpression(final List listExpression, final ListProps listProps) {
        listExpression.getExpression().clear();
        listExpression.getExpression().addAll(itemsConvertForListExpression(listProps, listExpression));
    }

    public static void fillInvocationExpression(final Invocation invocationExpression, final InvocationProps invocationProps) {
        final LiteralExpression invokedFunction = new LiteralExpression();
        invokedFunction.setText(new Text(invocationProps.invokedFunction));
        invokedFunction.setTypeRef(BuiltInType.STRING.asQName());
        invocationExpression.setExpression(invokedFunction);
        invocationExpression.getBinding().clear();
        invocationExpression.getBinding().addAll(bindingsConvertForInvocationExpression(invocationProps));
    }

    public static void fillFunctionExpression(final FunctionDefinition functionExpression, final FunctionProps functionProps) {
        final FunctionDefinition.Kind functionKind = FunctionDefinition.Kind.fromValue(functionProps.functionKind);
        functionExpression.getFormalParameter().clear();
        functionExpression.getFormalParameter().addAll(formalParametersConvertForFunctionExpression(functionProps));
        functionExpression.setKind(functionKind);
        functionExpression.setExpression(wrappedExpressionBasedOnKind(functionKind, functionProps));
    }

    public static ExpressionProps buildAndFillJsInteropProp(final Expression wrappedExpression, final String expressionName, final String dataType) {
        if (wrappedExpression instanceof IsLiteralExpression) {
            final LiteralExpression literalExpression = (LiteralExpression) wrappedExpression;
            return new LiteralExpressionProps(expressionName, dataType, literalExpression.getText().getValue());
        } else if (wrappedExpression instanceof Context) {
            final Context contextExpression = (Context) wrappedExpression;
            return new ContextProps(expressionName, dataType, contextEntriesConvertForContextProps(contextExpression), contextResultConvertForContextProps(contextExpression));
        } else if (wrappedExpression instanceof Relation) {
            final Relation relationExpression = (Relation) wrappedExpression;
            return new RelationProps(expressionName, dataType, columnsConvertForRelationProps(relationExpression), rowsConvertForRelationProps(relationExpression));
        } else if (wrappedExpression instanceof List) {
            final List listExpression = (List) wrappedExpression;
            return new ListProps(expressionName, dataType, itemsConvertForListProps(listExpression));
        } else if (wrappedExpression instanceof Invocation) {
            final Invocation invocationExpression = (Invocation) wrappedExpression;
            final String invokedFunction = ((LiteralExpression) Optional.ofNullable(invocationExpression.getExpression()).orElse(new LiteralExpression())).getText().getValue();
            return new InvocationProps(expressionName, dataType, invokedFunction, bindingsConvertForInvocationProps(invocationExpression));
        } else if (wrappedExpression instanceof FunctionDefinition) {
            final FunctionDefinition functionExpression = (FunctionDefinition) wrappedExpression;
            final EntryInfo[] formalParameters = formalParametersConvertForFunctionProps(functionExpression);
            return specificFunctionPropsBasedOnFunctionKind(expressionName, dataType, functionExpression, formalParameters);
        } else if (wrappedExpression instanceof DecisionTable) {

        }
        return new ExpressionProps(expressionName, dataType, null);
    }

    private static ExpressionProps contextResultConvertForContextProps(final Context contextExpression) {
        final ContextEntry resultContextEntry = !contextExpression.getContextEntry().isEmpty() ?
                contextExpression.getContextEntry().get(contextExpression.getContextEntry().size() - 1) :
                new ContextEntry();
        return buildAndFillJsInteropProp(resultContextEntry.getExpression(), "Result Expression", UNDEFINED.getText());
    }

    private static ContextEntryProps[] contextEntriesConvertForContextProps(final Context contextExpression) {
        return contextExpression.getContextEntry()
                .stream()
                .limit(contextExpression.getContextEntry().size() - 1)
                .map(contextEntry -> fromModelToPropsContextEntryMapper(contextEntry.getVariable(), contextEntry.getExpression()))
                .toArray(ContextEntryProps[]::new);
    }

    private static Expression buildAndFillNestedExpression(final ExpressionProps props) {
        if (LITERAL_EXPRESSION.getText().equals(props.logicType)) {
            final LiteralExpression literalExpression = new LiteralExpression();
            fillLiteralExpression(literalExpression, (LiteralExpressionProps) props);
            return literalExpression;
        } else if (CONTEXT.getText().equals(props.logicType)) {
            final Context contextExpression = new Context();
            fillContextExpression(contextExpression, (ContextProps) props);
            return contextExpression;
        } else if (RELATION.getText().equals(props.logicType)) {
            final Relation relationExpression = new Relation();
            fillRelationExpression(relationExpression, (RelationProps) props);
            return relationExpression;
        } else if (LIST.getText().equals(props.logicType)) {
            final List listExpression = new List();
            fillListExpression(listExpression, (ListProps) props);
            return listExpression;
        } else if (INVOCATION.getText().equals(props.logicType)) {
            final Invocation invocationExpression = new Invocation();
            fillInvocationExpression(invocationExpression, (InvocationProps) props);
            return invocationExpression;
        } else if (FUNCTION.getText().equals(props.logicType)) {
            final FunctionDefinition functionExpression = new FunctionDefinition();
            fillFunctionExpression(functionExpression, (FunctionProps) props);
            return functionExpression;
        } else if (DECISION_TABLE.getText().equals(props.logicType)) {

        }
        return null;
    }

    private static Collection<ContextEntry> contextEntriesConvertForContextExpression(final ContextProps contextProps) {
        return Arrays.stream(Optional.ofNullable(contextProps.contextEntries).orElse(new ContextEntryProps[0])).map(entryRow -> {
            final ContextEntry contextEntry = new ContextEntry();
            final InformationItem informationItem = new InformationItem();
            informationItem.setName(new Name(entryRow.entryInfo.name));
            informationItem.setTypeRef(BuiltInTypeUtils
                                               .findBuiltInTypeByName(entryRow.entryInfo.dataType)
                                               .orElse(BuiltInType.UNDEFINED)
                                               .asQName());
            contextEntry.setVariable(informationItem);
            contextEntry.setExpression(buildAndFillNestedExpression(entryRow.entryExpression));
            return contextEntry;
        }).collect(Collectors.toList());
    }

    private static ContextEntry entryResultConvertForContextExpression(final ContextProps contextProps) {
        final ContextEntry contextEntryResult = new ContextEntry();
        if (contextProps.result != null) {
            contextEntryResult.setExpression(buildAndFillNestedExpression(contextProps.result));
        }
        return contextEntryResult;
    }

    private static Collection<List> rowsConvertForRelationExpression(final RelationProps relationProps, final Relation relationExpression) {
        return Arrays
                .stream(Optional.ofNullable(relationProps.rows).orElse(new String[0][]))
                .map(row -> {
                    final List list = new List();
                    list.getExpression().addAll(
                            IntStream.range(0, Optional.ofNullable(relationProps.columns).orElse(new Column[0]).length).mapToObj(columnIndex -> {
                                final String cell = row.length <= columnIndex ? "" : row[columnIndex];
                                final LiteralExpression wrappedExpression = new LiteralExpression();
                                wrappedExpression.setText(new Text(cell));
                                wrappedExpression.setTypeRef(BuiltInType.STRING.asQName());
                                return HasExpression.wrap(relationExpression, wrappedExpression);
                            }).collect(Collectors.toList())
                    );
                    return list;
                })
                .collect(Collectors.toList());
    }

    private static Collection<InformationItem> columnsConvertForRelationExpression(final RelationProps relationProps) {
        return Arrays
                .stream(Optional.ofNullable(relationProps.columns).orElse(new Column[0]))
                .map(column -> {
                    final InformationItem informationItem = new InformationItem();
                    informationItem.setName(new Name(column.name));
                    informationItem.setTypeRef(BuiltInTypeUtils
                                                       .findBuiltInTypeByName(column.dataType)
                                                       .orElse(BuiltInType.UNDEFINED)
                                                       .asQName());
                    return informationItem;
                })
                .collect(Collectors.toList());
    }

    private static Collection<HasExpression> itemsConvertForListExpression(final ListProps listProps, final List listExpression) {
        return Arrays
                .stream(Optional.ofNullable(listProps.items).orElse(new ExpressionProps[0]))
                .map(props -> HasExpression.wrap(listExpression, buildAndFillNestedExpression(props)))
                .collect(Collectors.toList());
    }

    private static Collection<Binding> bindingsConvertForInvocationExpression(final InvocationProps invocationProps) {
        return Arrays
                .stream(Optional.ofNullable(invocationProps.bindingEntries).orElse(new ContextEntryProps[0]))
                .map(binding -> {
                    final Binding bindingModel = new Binding();
                    final InformationItem informationItem = new InformationItem();
                    informationItem.setName(new Name(binding.entryInfo.name));
                    informationItem.setTypeRef(BuiltInTypeUtils
                                                       .findBuiltInTypeByName(binding.entryInfo.dataType)
                                                       .orElse(BuiltInType.UNDEFINED)
                                                       .asQName());
                    bindingModel.setVariable(informationItem);
                    bindingModel.setExpression(buildAndFillNestedExpression(binding.entryExpression));
                    return bindingModel;
                })
                .collect(Collectors.toList());
    }

    private static Collection<InformationItem> formalParametersConvertForFunctionExpression(final FunctionProps functionProps) {
        return Arrays
                .stream(Optional.ofNullable(functionProps.formalParameters).orElse(new EntryInfo[0]))
                .map(entryInfo -> {
                    final InformationItem informationItem = new InformationItem();
                    informationItem.setName(new Name(entryInfo.name));
                    informationItem.setTypeRef(BuiltInTypeUtils
                                                       .findBuiltInTypeByName(entryInfo.dataType)
                                                       .orElse(BuiltInType.UNDEFINED)
                                                       .asQName());
                    return informationItem;
                })
                .collect(Collectors.toList());
    }

    private static Expression wrappedExpressionBasedOnKind(final FunctionDefinition.Kind functionKind, final FunctionProps functionProps) {
        switch (functionKind) {
            case JAVA:
                final JavaFunctionProps javaFunctionProps = (JavaFunctionProps) functionProps;
                final Context javaWrappedContext = new Context();
                javaWrappedContext.getContextEntry().add(buildContextEntry(javaFunctionProps.className, VARIABLE_CLASS));
                javaWrappedContext.getContextEntry().add(buildContextEntry(javaFunctionProps.methodName, VARIABLE_METHOD_SIGNATURE));
                return javaWrappedContext;
            case PMML:
                final PmmlFunctionProps pmmlFunctionProps = (PmmlFunctionProps) functionProps;
                final Context pmmlWrappedContext = new Context();
                pmmlWrappedContext.getContextEntry().add(buildContextEntry(pmmlFunctionProps.document, VARIABLE_DOCUMENT));
                pmmlWrappedContext.getContextEntry().add(buildContextEntry(pmmlFunctionProps.model, VARIABLE_MODEL));
                return pmmlWrappedContext;
            default:
            case FEEL:
                final FeelFunctionProps feelFunctionProps = (FeelFunctionProps) functionProps;
                return buildAndFillNestedExpression(
                        Optional.ofNullable(feelFunctionProps.expression)
                                .orElse(new LiteralExpressionProps("Nested Literal Expression", UNDEFINED.getText(), ""))
                );
        }
    }

    private static ContextEntry buildContextEntry(final String expressionText, final String variableName) {
        final ContextEntry entry = new ContextEntry();
        final InformationItem entryVariable = new InformationItem();
        final LiteralExpression entryExpression = new LiteralExpression();
        entryVariable.setName(new Name(variableName));
        entryVariable.setTypeRef(BuiltInType.STRING.asQName());
        entryExpression.setText(new Text(expressionText));
        entry.setVariable(entryVariable);
        entry.setExpression(entryExpression);
        return entry;
    }

    private static Column[] columnsConvertForRelationProps(final Relation relationExpression) {
        return relationExpression
                .getColumn()
                .stream()
                .map(informationItem -> new Column(informationItem.getName().getValue(), informationItem.getTypeRef().getLocalPart(), null))
                .toArray(Column[]::new);
    }

    private static String[][] rowsConvertForRelationProps(final Relation relationExpression) {
        return relationExpression
                .getRow()
                .stream()
                .map(list -> list
                        .getExpression()
                        .stream()
                        .map(wrappedLiteralExpression -> ((LiteralExpression) wrappedLiteralExpression.getExpression()).getText().getValue())
                        .toArray(String[]::new)
                )
                .toArray(String[][]::new);
    }

    private static ExpressionProps[] itemsConvertForListProps(final List listExpression) {
        return listExpression
                .getExpression()
                .stream()
                .map(expression -> buildAndFillJsInteropProp(expression.getExpression(), "List item", UNDEFINED.getText()))
                .toArray(ExpressionProps[]::new);
    }

    private static ContextEntryProps[] bindingsConvertForInvocationProps(final Invocation invocationExpression) {
        return invocationExpression
                .getBinding()
                .stream()
                .map(invocation -> fromModelToPropsContextEntryMapper(invocation.getVariable(), invocation.getExpression()))
                .toArray(ContextEntryProps[]::new);
    }

    private static ContextEntryProps fromModelToPropsContextEntryMapper(final InformationItem contextEntryVariable, final Expression expression) {
        final String entryName = contextEntryVariable.getName().getValue();
        final String entryDataType = contextEntryVariable.getTypeRef().getLocalPart();
        final EntryInfo entryInfo = new EntryInfo(entryName, entryDataType);
        final ExpressionProps entryExpression = buildAndFillJsInteropProp(expression, entryName, entryDataType);
        return new ContextEntryProps(entryInfo, entryExpression);
    }

    private static EntryInfo[] formalParametersConvertForFunctionProps(final FunctionDefinition functionExpression) {
        return functionExpression
                .getFormalParameter()
                .stream()
                .map(parameter -> new EntryInfo(parameter.getName().getValue(), parameter.getTypeRefHolder().getValue().getLocalPart()))
                .toArray(EntryInfo[]::new);
    }

    private static FunctionProps specificFunctionPropsBasedOnFunctionKind(final String expressionName, final String dataType, final FunctionDefinition functionExpression, final EntryInfo[] formalParameters) {
        switch (functionExpression.getKind()) {
            case JAVA:
                final String classNameExpression = getEntryAt(functionExpression.getExpression(), 0);
                final String methodNameExpression = getEntryAt(functionExpression.getExpression(), 1);
                return new JavaFunctionProps(expressionName, dataType, formalParameters, classNameExpression, methodNameExpression);
            case PMML:
                final String documentExpression = getEntryAt(functionExpression.getExpression(), 0);
                final String modelExpression = getEntryAt(functionExpression.getExpression(), 1);
                return new PmmlFunctionProps(expressionName, dataType, formalParameters, documentExpression, modelExpression);
            default:
            case FEEL:
                return new FeelFunctionProps(expressionName, dataType, formalParameters,
                                             buildAndFillJsInteropProp(functionExpression.getExpression(), "Feel Expression", UNDEFINED.getText()));
        }
    }

    private static String getEntryAt(final Expression wrappedExpression, final int index) {
        final Context wrappedContext = (Context) (Optional.ofNullable(wrappedExpression).orElse(new Context()));
        LiteralExpression entryExpression = new LiteralExpression();
        String wrappedTextValue = "";
        if (wrappedContext.getContextEntry().size() > index && wrappedContext.getContextEntry().get(index).getExpression() instanceof LiteralExpression) {
            entryExpression = (LiteralExpression) wrappedContext.getContextEntry().get(index).getExpression();
        }
        if (entryExpression.getText() != null && entryExpression.getText().getValue() != null) {
            wrappedTextValue = entryExpression.getText().getValue();
        }
        return wrappedTextValue;
    }
}