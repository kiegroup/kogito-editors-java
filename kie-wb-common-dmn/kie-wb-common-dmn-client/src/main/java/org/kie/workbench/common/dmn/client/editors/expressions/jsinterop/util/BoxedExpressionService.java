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

import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl;

public class BoxedExpressionService {
    public static void registerBroadcastForExpression(final ExpressionEditorViewImpl boxedExpressionEditorPanel) {
        createNamespace();
        registerResetExpressionDefinition(boxedExpressionEditorPanel);
        registerBroadcastForLiteralExpression(boxedExpressionEditorPanel);
        registerBroadcastForContextExpression(boxedExpressionEditorPanel);
        registerBroadcastForRelationExpression(boxedExpressionEditorPanel);
        registerBroadcastForListExpressionDefinition(boxedExpressionEditorPanel);
    }

    private static native void createNamespace()/*-{
        $wnd["beeApiWrapper"] = {};
    }-*/;

    private static native void registerResetExpressionDefinition(final ExpressionEditorViewImpl boxedExpressionEditorPanel)/*-{
        $wnd["beeApiWrapper"].resetExpressionDefinition = function() {
            return boxedExpressionEditorPanel.@ExpressionEditorViewImpl::resetExpressionDefinition(*)();
        };
    }-*/;

    private static native void registerBroadcastForLiteralExpression(final ExpressionEditorViewImpl boxedExpressionEditorPanel)/*-{
        $wnd["beeApiWrapper"].broadcastLiteralExpressionDefinition = function(literalExpressionDefinition) {
            return boxedExpressionEditorPanel.@ExpressionEditorViewImpl::broadcastLiteralExpressionDefinition(*)(literalExpressionDefinition);
        };
    }-*/;

    private static native void registerBroadcastForContextExpression(final ExpressionEditorViewImpl boxedExpressionEditorPanel)/*-{
        $wnd["beeApiWrapper"].broadcastContextExpressionDefinition = function(contextExpressionDefinition) {
            return boxedExpressionEditorPanel.@ExpressionEditorViewImpl::broadcastContextExpressionDefinition(*)(contextExpressionDefinition);
        };
    }-*/;

    private static native void registerBroadcastForRelationExpression(final ExpressionEditorViewImpl boxedExpressionEditorPanel)/*-{
        $wnd["beeApiWrapper"].broadcastRelationExpressionDefinition = function(relationExpressionDefinition) {
            return boxedExpressionEditorPanel.@ExpressionEditorViewImpl::broadcastRelationExpressionDefinition(*)(relationExpressionDefinition);
        };
    }-*/;

    private static native void registerBroadcastForListExpressionDefinition(final ExpressionEditorViewImpl boxedExpressionEditorPanel)/*-{
        $wnd["beeApiWrapper"].broadcastListExpressionDefinition = function(listExpressionDefinition) {
            return boxedExpressionEditorPanel.@ExpressionEditorViewImpl::broadcastListExpressionDefinition(*)(listExpressionDefinition);
        };
    }-*/;
}
