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

package org.kie.workbench.common.dmn.client.editors.expressions.commands;

import java.util.Objects;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class ClearExpressionCommandTest {

    @Mock
    private HasExpression hasExpression;

    @Mock
    private ExpressionProps expressionProps;

    @Mock
    private Event<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private ExpressionEditorView view;

    private ClearExpressionCommand expressionCommand;

    private final String nodeUUID = "node uuid";

    @Before
    public void setup() {
        this.expressionCommand = new ClearExpressionCommand(hasExpression,
                                                            expressionProps,
                                                            editorSelectedEvent,
                                                            nodeUUID,
                                                            view);
    }

    @Test
    public void testGetTemporaryExpression() {

        final Expression temp = expressionCommand.getTemporaryExpression();

        assertTrue(temp instanceof ClearExpressionCommand.EmptyExpression);
    }

    @Test
    public void testGetNewExpression() {

        assertTrue(Objects.isNull(expressionCommand.getNewExpression()));
    }
}
