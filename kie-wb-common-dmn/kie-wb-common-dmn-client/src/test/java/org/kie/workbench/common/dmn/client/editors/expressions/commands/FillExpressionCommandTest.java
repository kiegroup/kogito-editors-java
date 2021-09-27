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

import java.util.Optional;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FillExpressionCommandTest {

    @Mock
    private Expression temporaryExpression;

    @Mock
    private Expression newExpression;

    @Mock(extraInterfaces = {HasName.class, HasVariable.class})
    private HasExpression hasExpression;

    private ExpressionProps expressionProps;

    @Mock
    private Event<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private ExpressionEditorView view;

    @Mock
    private Expression existingExpression;

    private final String nodeUUID = "uuid";

    private final String name = "name";

    private final String dataType = "data type";

    private final String logicType = "logic type";

    private FillExpressionCommandMock command;

    @Before
    public void setup() {

        expressionProps = new ExpressionProps(name,
                                              dataType,
                                              logicType);

        when(hasExpression.getExpression()).thenReturn(existingExpression);

        command = spy(new FillExpressionCommandMock(hasExpression,
                                                    expressionProps,
                                                    editorSelectedEvent,
                                                    nodeUUID,
                                                    view));
    }

    @Test
    public void testHasChangesInExpression() {

        when(temporaryExpression.equals(existingExpression, true)).thenReturn(true);

        final boolean actual = command.hasChangesInExpression();

        assertFalse(actual);

        verify(temporaryExpression).equals(existingExpression, true);
        verify(temporaryExpression, never()).equals(existingExpression, false);
    }

    @Test
    public void testExecute_WhenIsNotRedo() {

        final InOrder inOrder = Mockito.inOrder(command, view);

        doReturn(false).when(command).isEnableRedo();

        doNothing().when(command).saveCurrentState();
        doNothing().when(command).setExpressionName(expressionProps);
        doNothing().when(command).setTypeRef(any());

        final CommandResult result = command.execute(mock(AbstractCanvasHandler.class));

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);

        inOrder.verify(command).fireEditorSelectedEvent();
        inOrder.verify(command).saveCurrentState();
        inOrder.verify(command).setExpressionName(expressionProps);
        inOrder.verify(command).setTypeRef(dataType);
        inOrder.verify(command).createExpression();
        inOrder.verify(command).fill();
        inOrder.verify(command, never()).setEnableRedo(false);
        inOrder.verify(view, never()).activate();
    }

    @Test
    public void testExecute_WhenIsRedo() {

        final InOrder inOrder = Mockito.inOrder(command, view);

        doReturn(true).when(command).isEnableRedo();

        doNothing().when(command).saveCurrentState();
        doNothing().when(command).setExpressionName(expressionProps);
        doNothing().when(command).setTypeRef(any());

        final CommandResult result = command.execute(mock(AbstractCanvasHandler.class));

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);

        inOrder.verify(command).fireEditorSelectedEvent();
        inOrder.verify(command).saveCurrentState();
        inOrder.verify(command).setExpressionName(expressionProps);
        inOrder.verify(command).setTypeRef(dataType);
        inOrder.verify(command).createExpression();
        inOrder.verify(command).fill();
        inOrder.verify(command).setEnableRedo(false);
        inOrder.verify(view).activate();
    }

    @Test
    public void testCreateExpression() {

        when(hasExpression.getExpression()).thenReturn(null);

        command.createExpression();

        verify(hasExpression).setExpression(newExpression);
    }

    @Test
    public void testUndo() {

        final InOrder inOrder = Mockito.inOrder(command, view);

        doNothing().when(command).restoreExpression();
        doNothing().when(command).restoreExpressionName();
        doNothing().when(command).restoreTypeRef();

        final CommandResult result = command.undo(mock(AbstractCanvasHandler.class));

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);

        inOrder.verify(command).restoreExpression();
        inOrder.verify(command).restoreTypeRef();
        inOrder.verify(command).restoreExpressionName();
        inOrder.verify(command).fireEditorSelectedEvent();
        inOrder.verify(view).activate();
        inOrder.verify(command).setEnableRedo(true);
    }

    @Test
    public void testHasNewExpressionToApply_WhenExpressionIsNull() {

        when(hasExpression.getExpression()).thenReturn(null);

        final boolean hasNewExpression = command.hasNewExpressionToApply();

        assertTrue(hasNewExpression);
    }

    @Test
    public void testHasNewExpressionToApply_WhenHasChangesInExpression() {

        doReturn(true).when(command).hasChangesInExpression();

        final boolean hasNewExpression = command.hasNewExpressionToApply();

        assertTrue(hasNewExpression);
    }

    @Test
    public void testHasNewExpressionToApply_WhenThereIsNoChanges() {
        doReturn(false).when(command).hasChangesInExpression();

        final boolean hasNewExpression = command.hasNewExpressionToApply();

        assertFalse(hasNewExpression);
    }

    @Test
    public void testHasNewNameToApply() {

        final Name nameExpression = new Name(name);
        when(((HasName) hasExpression).getName()).thenReturn(nameExpression);

        final boolean hasNewNameToApply = command.hasNewNameToApply();

        assertFalse(hasNewNameToApply);
    }

    @Test
    public void testHasNewNameToApply_WhenThereIs() {

        final Name nameExpression = new Name("another name");
        when(((HasName) hasExpression).getName()).thenReturn(nameExpression);

        final boolean hasNewNameToApply = command.hasNewNameToApply();

        assertTrue(hasNewNameToApply);
    }

    @Test
    public void testHasNewTypeRefToApply() {

        final QName currentTypeRef = new QName();
        final QName newTypeRef = new QName();

        doReturn(currentTypeRef).when(command).getCurrentTypeRef();
        doReturn(newTypeRef).when(command).getTypeRef(dataType);

        final boolean nasNewTypeRefToApply = command.hasNewTypeRefToApply();

        assertFalse(nasNewTypeRefToApply);
    }

    @Test
    public void testHasNewTypeRefToApply_WhenThereIs() {

        final QName currentTypeRef = new QName();
        final QName newTypeRef = new QName("something", "another");

        doReturn(currentTypeRef).when(command).getCurrentTypeRef();
        doReturn(newTypeRef).when(command).getTypeRef(dataType);

        final boolean nasNewTypeRefToApply = command.hasNewTypeRefToApply();

        assertTrue(nasNewTypeRefToApply);
    }

    @Test
    public void testFireEditorSelectedEvent() {

        final ArgumentCaptor<ExpressionEditorChanged> captor = ArgumentCaptor.forClass(ExpressionEditorChanged.class);

        command.fireEditorSelectedEvent();

        verify(editorSelectedEvent).fire(captor.capture());

        final ExpressionEditorChanged editorChanged = captor.getValue();

        assertEquals(nodeUUID, editorChanged.getNodeUUID());
    }

    @Test
    public void testRestoreExpressionName() {

        final Name savedExpressionName = mock(Name.class);

        doReturn(savedExpressionName).when(command).getSavedExpressionName();

        command.restoreExpressionName();

        verify((HasName) hasExpression).setName(savedExpressionName);
    }

    @Test
    public void testRestoreTypeRef() {

        final InformationItemPrimary variable = mock(InformationItemPrimary.class);
        final QName savedTypeRef = mock(QName.class);

        doReturn(savedTypeRef).when(command).getSavedTypeRef();
        when(((HasVariable) hasExpression).getVariable()).thenReturn(variable);

        command.restoreTypeRef();

        verify(variable).setTypeRef(savedTypeRef);
    }

    @Test
    public void testGetCurrentTypeRef() {

        final InformationItemPrimary variable = mock(InformationItemPrimary.class);
        final QName expected = mock(QName.class);

        when(((HasVariable) hasExpression).getVariable()).thenReturn(variable);
        when(variable.getTypeRef()).thenReturn(expected);

        final QName actual = command.getCurrentTypeRef();

        assertEquals(expected, actual);
    }

    @Test
    public void testRestoreExpression() {

        final Expression savedExpression = mock(Expression.class);
        final Optional<Expression> optionalSavedExpression = Optional.of(savedExpression);

        doReturn(optionalSavedExpression).when(command).getSavedExpression();

        command.restoreExpression();

        verify(hasExpression).setExpression(savedExpression);
    }

    @Test
    public void testRestoreExpression_WhenThereIsNoSavedExpression() {

        final Optional<Expression> optionalSavedExpression = Optional.empty();

        doReturn(optionalSavedExpression).when(command).getSavedExpression();

        command.restoreExpression();

        verify(hasExpression).setExpression(null);
    }

    @Test
    public void testSaveCurrentExpression_WhenThereIsNotCurrentExpression() {

        when(hasExpression.getExpression()).thenReturn(null);

        command.saveCurrentExpression();

        verify(command).setSavedExpression(Optional.empty());
    }

    @Test
    public void testSaveCurrentExpression_WhenThereIsCurrentExpression() {

        final Expression copyOfExistingExpression = mock(Expression.class);
        when(existingExpression.copy()).thenReturn(copyOfExistingExpression);

        command.saveCurrentExpression();

        verify(command).setSavedExpression(Optional.of(copyOfExistingExpression));
    }

    @Test
    public void testSetTypeRef() {

        final InformationItemPrimary variable = mock(InformationItemPrimary.class);
        final QName typeRef = mock(QName.class);

        doReturn(typeRef).when(command).getTypeRef(dataType);
        when(((HasVariable) hasExpression).getVariable()).thenReturn(variable);

        command.setTypeRef(dataType);

        verify(variable).setTypeRef(typeRef);
    }

    @Test
    public void testSetExpressionName() {

        command.setExpressionName(expressionProps);

        verify((HasName) hasExpression).setName(new Name(name));
    }

    @Test
    public void testSaveCurrentState() {

        doNothing().when(command).saveCurrentExpressionName();
        doNothing().when(command).saveCurrentTypeRef();
        doNothing().when(command).saveCurrentExpression();

        command.saveCurrentState();

        verify(command).saveCurrentExpressionName();
        verify(command).saveCurrentTypeRef();
        verify(command).saveCurrentExpression();
    }

    @Test
    public void testSaveCurrentExpressionName() {

        final Name currentExpressionName = mock(Name.class);

        when(((HasName) hasExpression).getName()).thenReturn(currentExpressionName);

        command.saveCurrentExpressionName();

        verify(command).setSavedExpressionName(currentExpressionName);
    }

    @Test
    public void testSaveCurrentTypeRef() {

        final QName currentTypeRef = mock(QName.class);

        doReturn(currentTypeRef).when(command).getCurrentTypeRef();

        command.saveCurrentTypeRef();

        verify(command).setSavedTypeRef(currentTypeRef);
    }

    class FillExpressionCommandMock extends FillExpressionCommand {

        public FillExpressionCommandMock(final HasExpression hasExpression,
                                         final ExpressionProps expressionProps,
                                         final Event<ExpressionEditorChanged> editorSelectedEvent,
                                         final String nodeUUID,
                                         final ExpressionEditorView view) {
            super(hasExpression, expressionProps, editorSelectedEvent, nodeUUID, view);
        }

        @Override
        protected void fill() {
        }

        @Override
        protected Expression getTemporaryExpression() {
            return temporaryExpression;
        }

        @Override
        protected Expression getNewExpression() {
            return newExpression;
        }
    }
}
