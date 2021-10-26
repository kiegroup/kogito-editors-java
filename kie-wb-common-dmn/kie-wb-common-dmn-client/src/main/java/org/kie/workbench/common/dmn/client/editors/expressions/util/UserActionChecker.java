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

import java.util.Arrays;
import java.util.Objects;

import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Column;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextEntryProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableRule;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;

import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.CONTEXT;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.DECISION_TABLE;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.INVOCATION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.RELATION;

public class UserActionChecker {

    public boolean isUserAction(final ContextProps contextProps) {
        if (Objects.isNull(contextProps.contextEntries)) {
            return false;
        }
        for (final ContextEntryProps contextEntry : contextProps.contextEntries) {
            if (!isUserAction(contextEntry.entryExpression)) {
                return false;
            }
        }
        return true;
    }

    // The Boxed Expression Editor does broadcast at each change, but we want
    // to create commands only for user action commands.
    public boolean isUserAction(final ExpressionProps entryExpression) {
        if (Objects.equals(entryExpression.logicType, DECISION_TABLE.getText())) {
            return isUserAction((DecisionTableProps) entryExpression);
        } else if (Objects.equals(entryExpression.logicType, CONTEXT.getText())) {
            return isUserAction((ContextProps) entryExpression);
        } else if (Objects.equals(entryExpression.logicType, INVOCATION.getText())) {
            return isUserAction((InvocationProps) entryExpression);
        } else if (Objects.equals(entryExpression.logicType, RELATION.getText())) {
            return isUserAction((RelationProps) entryExpression);
        }
        return true;
    }

    boolean isUserAction(final RelationProps relationProps) {

        if (!columnsMatchesRows(relationProps.columns, relationProps.rows)) {
            return false;
        }

        for (final Column column : relationProps.columns) {
            if (Objects.isNull(column.width)) {
                return false;
            }
        }
        return true;
    }

    boolean columnsMatchesRows(final Column[] columns,
                               final String[][] rows) {

        for (int i = 0; i < rows.length; i++) {
            if (rows[i].length != columns.length) {
                return false;
            }
        }

        return true;
    }

    boolean isUserAction(final DecisionTableProps decisionTableProps) {
        return haveAllClauses(decisionTableProps)
                && haveAtLeastOneColumnSizeDefined(decisionTableProps)
                && areRulesLoaded(decisionTableProps);
    }

    boolean areRulesLoaded(final DecisionTableProps decisionTableProps) {
        return Arrays.stream(decisionTableProps.rules)
                .noneMatch(rule -> !haveAllEntries(decisionTableProps, rule)
                        || ruleHaveNullClauses(rule));
    }

    boolean ruleHaveNullClauses(final DecisionTableRule rule) {
        for (int j = 0; j < rule.inputEntries.length; j++) {
            if (Objects.isNull(rule.inputEntries[j])) {
                return true;
            }
        }

        for (int j = 0; j < rule.outputEntries.length; j++) {
            if (Objects.isNull(rule.outputEntries[j])) {
                return true;
            }
        }

        for (int j = 0; j < rule.annotationEntries.length; j++) {
            if (Objects.isNull(rule.annotationEntries[j])) {
                return true;
            }
        }
        return false;
    }

    boolean haveAllClauses(final DecisionTableProps decisionTableProps) {
        return !Objects.isNull(decisionTableProps.input)
                && !Objects.isNull(decisionTableProps.annotations)
                && !Objects.isNull(decisionTableProps.output);
    }

    boolean haveAtLeastOneColumnSizeDefined(final DecisionTableProps decisionTableProps) {
        for (int i = 0; i < decisionTableProps.input.length; i++) {
            if (!Objects.isNull(decisionTableProps.input[i].width)) {
                return true;
            }
        }

        for (int i = 0; i < decisionTableProps.output.length; i++) {
            if (!Objects.isNull(decisionTableProps.output[i].width)) {
                return true;
            }
        }

        for (int i = 0; i < decisionTableProps.annotations.length; i++) {
            if (!Objects.isNull(decisionTableProps.annotations[i].width)) {
                return true;
            }
        }
        return false;
    }

    boolean haveAllEntries(final DecisionTableProps decisionTableProps,
                           final DecisionTableRule rule) {
        return rule.inputEntries.length == decisionTableProps.input.length
                && rule.outputEntries.length == decisionTableProps.output.length
                && rule.annotationEntries.length == decisionTableProps.annotations.length;
    }

    boolean isUserAction(final InvocationProps invocationProps) {

        if (!Objects.isNull(invocationProps.bindingEntries)) {
            for (final ContextEntryProps bindingEntry : invocationProps.bindingEntries) {
                if (!isUserAction(bindingEntry.entryExpression)) {
                    return false;
                }
            }
        }

        return true;
    }
}
