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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Objects;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.util.SavedStatus;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public class SaveCurrentStatusCommand extends AbstractCanvasCommand {

    private final HasExpression hasExpression;
    private final Event<ExpressionEditorChanged> editorSelectedEvent;
    private final ExpressionEditorView view;
    private final String nodeUUID;

    private SavedStatus originalStatus;
    private SavedStatus statusBeforeUndo;

    public SaveCurrentStatusCommand(final HasExpression hasExpression,
                                    final Event<ExpressionEditorChanged> editorSelectedEvent,
                                    final ExpressionEditorView view,
                                    final String nodeUUID) {
        this.hasExpression = hasExpression;
        this.editorSelectedEvent = editorSelectedEvent;
        this.view = view;
        this.nodeUUID = nodeUUID;
        this.originalStatus = new SavedStatus(hasExpression,
                                              editorSelectedEvent,
                                              view,
                                              nodeUUID);
    }

    public Event<ExpressionEditorChanged> getEditorSelectedEvent() {
        return editorSelectedEvent;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        if (!Objects.isNull(statusBeforeUndo)) {
            statusBeforeUndo.apply();
        }
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        if (Objects.isNull(statusBeforeUndo)) {
            statusBeforeUndo = new SavedStatus(hasExpression,
                                               editorSelectedEvent,
                                               view,
                                               nodeUUID);
        }
        originalStatus.apply();
        return buildResult();
    }
}
