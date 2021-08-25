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
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;

public class ExpressionFiller {

    public static final String EDIT_INFO_POPOVER_LABEL = "editInfoPopoverLabel";

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

        } else if (wrappedExpression instanceof Invocation) {

        } else if (wrappedExpression instanceof FunctionDefinition) {

        } else if (wrappedExpression instanceof DecisionTable) {

        }
        return new ExpressionProps(expressionName, dataType, null);
    }

    private static ExpressionProps contextResultConvertForContextProps(final Context contextExpression) {
        final ContextEntry resultContextEntry = !contextExpression.getContextEntry().isEmpty() ?
                contextExpression.getContextEntry().get(contextExpression.getContextEntry().size() - 1) :
                new ContextEntry();
        return buildAndFillJsInteropProp(resultContextEntry.getExpression(), "Result Expression", "<Undefined>");
    }

    private static ContextEntryProps[] contextEntriesConvertForContextProps(final Context contextExpression) {
        return contextExpression.getContextEntry()
                .stream()
                .limit(contextExpression.getContextEntry().size() - 1)
                .map(contextEntry -> {
                    final InformationItem contextEntryVariable = contextEntry.getVariable();
                    final String entryName = contextEntryVariable.getName().getValue();
                    final String entryDataType = contextEntryVariable.getTypeRef().getLocalPart();
                    final String editInfoPopoverLabel = contextEntryVariable.getNsContext().get(EDIT_INFO_POPOVER_LABEL);
                    final EntryInfo entryInfo = new EntryInfo(entryName, entryDataType);
                    final ExpressionProps entryExpression = buildAndFillJsInteropProp(contextEntry.getExpression(), entryName, entryDataType);
                    return new ContextEntryProps(entryInfo, entryExpression, editInfoPopoverLabel);
                })
                .toArray(ContextEntryProps[]::new);
    }

    private static Expression buildAndFillNestedExpression(final ExpressionProps props) {
        switch (props.logicType) {
            case "Literal expression":
                final LiteralExpression literalExpression = new LiteralExpression();
                fillLiteralExpression(literalExpression, (LiteralExpressionProps) props);
                return literalExpression;
            case "Context":
                final Context contextExpression = new Context();
                fillContextExpression(contextExpression, (ContextProps) props);
                return contextExpression;
            case "Relation":
                final Relation relationExpression = new Relation();
                fillRelationExpression(relationExpression, (RelationProps) props);
                return relationExpression;
            default:
                return null;
        }
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
            informationItem.getNsContext().put(EDIT_INFO_POPOVER_LABEL, entryRow.editInfoPopoverLabel);
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
}
