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
import java.util.Optional;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public abstract class FillExpressionCommand<E extends ExpressionProps> extends AbstractCanvasCommand {

    private final HasExpression hasExpression;
    private final E expressionProps;
    private final String nodeUUID;
    private final Event<ExpressionEditorChanged> editorSelectedEvent;
    private final ExpressionEditorView view;
    private String savedExpressionName;
    private QName savedTypeRef;
    private Optional<Expression> savedExpression;
    private boolean enableRedo;

    public FillExpressionCommand(final HasExpression hasExpression,
                                 final E expressionProps,
                                 final Event<ExpressionEditorChanged> editorSelectedEvent,
                                 final String nodeUUID,
                                 final ExpressionEditorView view) {
        this.hasExpression = hasExpression;
        this.expressionProps = expressionProps;
        this.nodeUUID = nodeUUID;
        this.editorSelectedEvent = editorSelectedEvent;
        this.view = view;
    }

    public HasExpression getHasExpression() {
        return hasExpression;
    }

    public E getExpressionProps() {
        return expressionProps;
    }

    public String getSavedExpressionName() {
        return savedExpressionName;
    }

    public void setSavedExpressionName(final String savedExpressionName) {
        this.savedExpressionName = savedExpressionName;
    }

    public QName getSavedTypeRef() {
        return savedTypeRef;
    }

    public void setSavedTypeRef(final QName savedTypeRef) {
        this.savedTypeRef = savedTypeRef;
    }

    public Optional<Expression> getSavedExpression() {
        return savedExpression;
    }

    public void setSavedExpression(final Optional<Expression> savedExpression) {
        this.savedExpression = savedExpression;
    }

    public boolean isEnableRedo() {
        return enableRedo;
    }

    public void setEnableRedo(boolean enableRedo) {
        this.enableRedo = enableRedo;
    }

    public ExpressionEditorView getView() {
        return view;
    }

    public Event<ExpressionEditorChanged> getEditorSelectedEvent() {
        return editorSelectedEvent;
    }

    public String getNodeUUID() {
        return nodeUUID;
    }

    protected abstract void fill();

    protected abstract Expression getTemporaryExpression();

    protected boolean hasChangesInExpression() {
        final Expression newExpression = getTemporaryExpression();
        return !newExpression.equals(getHasExpression().getExpression(), true);
    }

    public boolean hasChanges() {
        return hasNewNameToApply() || hasNewTypeRefToApply() || hasNewExpressionToApply();
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        fireEditorSelectedEvent();
        saveCurrentState();
        setExpressionName(getExpressionProps().name);
        setTypeRef(getExpressionProps().dataType);
        createExpression();
        fill();
        if (isEnableRedo()) {
            setEnableRedo(false);
            view.activate();
        }
        return buildResult();
    }

    void createExpression() {
        if (getHasExpression().getExpression() == null) {
            getHasExpression().setExpression(getNewExpression());
        }
    }

    protected abstract Expression getNewExpression();

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        restoreExpression();
        restoreTypeRef();
        restoreExpressionName();
        fireEditorSelectedEvent();
        view.activate();
        setEnableRedo(true);
        return buildResult();
    }

    boolean hasNewExpressionToApply() {
        return Objects.isNull(getHasExpression().getExpression())
                || hasChangesInExpression();
    }

    boolean hasNewTypeRefToApply() {
        final QName current = getCurrentTypeRef();
        final QName newTypeRef = getTypeRef(expressionProps.dataType);
        return !current.equals(newTypeRef);
    }

    boolean hasNewNameToApply() {
        if (hasExpression instanceof HasName) {
            final HasName hasName = (HasName) hasExpression;
            return !hasName.getName().getValue().equals(expressionProps.name);
        }
        return true;
    }

    void fireEditorSelectedEvent() {
        getEditorSelectedEvent().fire(new ExpressionEditorChanged(getNodeUUID()));
    }

    void restoreExpressionName() {
        setExpressionName(getSavedExpressionName());
    }

    void restoreTypeRef() {
        if (hasExpression instanceof HasVariable) {
            final HasVariable<InformationItemPrimary> hasVariable = (HasVariable<InformationItemPrimary>) hasExpression;
            hasVariable.getVariable().setTypeRef(getSavedTypeRef());
        }
    }

    QName getCurrentTypeRef() {
        if (hasExpression instanceof HasVariable) {
            final HasVariable<InformationItemPrimary> hasVariable = (HasVariable<InformationItemPrimary>) hasExpression;
            return hasVariable.getVariable().getTypeRef();
        }
        return BuiltInType.UNDEFINED.asQName();
    }

    void restoreExpression() {
        getHasExpression().setExpression(getSavedExpression().orElse(null));
    }

    void saveCurrentExpression() {
        if (Objects.isNull(getHasExpression().getExpression())) {
            setSavedExpression(Optional.empty());
        } else {
            setSavedExpression(Optional.of(getHasExpression().getExpression().copy()));
        }
    }

    void setTypeRef(final String dataType) {
        final QName typeRef = getTypeRef(dataType);
        if (hasExpression instanceof HasVariable) {
            @SuppressWarnings("unchecked")
            final HasVariable<InformationItemPrimary> hasVariable = (HasVariable<InformationItemPrimary>) hasExpression;
            hasVariable.getVariable().setTypeRef(typeRef);
        }
    }

    QName getTypeRef(final String dataType) {
        return BuiltInTypeUtils
                .findBuiltInTypeByName(dataType)
                .orElse(BuiltInType.UNDEFINED)
                .asQName();
    }

    void setExpressionName(final String expressionName) {
        if (getHasExpression() instanceof HasName) {
            final HasName hasName = (HasName) getHasExpression();
            final Name name;
            if (Objects.isNull(hasName.getName())) {
                name = new Name();
            } else {
                name = hasName.getName();
            }
            name.setValue(expressionName);
        }
    }

    void saveCurrentState() {
        saveCurrentExpressionName();
        saveCurrentTypeRef();
        saveCurrentExpression();
    }

    void saveCurrentExpressionName() {
        if (getHasExpression() instanceof HasName) {
            final HasName hasName = (HasName) getHasExpression();
            setSavedExpressionName(hasName.getName().getValue());
        }
    }

    void saveCurrentTypeRef() {
        setSavedTypeRef(getCurrentTypeRef());
    }
}
