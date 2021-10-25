/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.ClearExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillContextExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillDecisionTableExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillFunctionExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillInvocationExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillListExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillLiteralExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillRelationExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Column;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextEntryProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableRule;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.EntryInfo;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ModelsFromDocument;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PMMLParam;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.BoxedExpressionService;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.ExpressionPropsFiller;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.js.DMNLoader;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BoundaryTransformMediator;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.keyboard.KeyboardOperationEditCell;
import org.kie.workbench.common.dmn.client.widgets.grid.keyboard.KeyboardOperationEscapeGridCell;
import org.kie.workbench.common.dmn.client.widgets.grid.keyboard.KeyboardOperationInvokeContextMenuForSelectedCell;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelContainer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetKeyboardHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveDown;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveLeft;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveRight;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveUp;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.CONTEXT;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.DECISION_TABLE;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.INVOCATION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.RELATION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.UNDEFINED;

@Templated
@Dependent
public class ExpressionEditorViewImpl implements ExpressionEditorView {

    static final double VP_SCALE = 1.0;

    static final String ENABLED_BETA_CSS_CLASS = "kie-beta-boxed-expression-editor--enabled";

    private ExpressionEditorView.Presenter presenter;

    @DataField("returnToLink")
    private Anchor returnToLink;

    @DataField("expressionName")
    private Span expressionName;

    @DataField("expressionType")
    private Span expressionType;

    @DataField("dmn-table")
    private DMNGridPanelContainer gridPanelContainer;

    @DataField("try-it")
    private HTMLAnchorElement tryIt;

    @DataField("switch-back")
    private HTMLAnchorElement switchBack;

    @DataField("beta-boxed-expression-toggle")
    private HTMLDivElement betaBoxedExpressionToggle;

    @DataField("dmn-new-expression-editor")
    private HTMLDivElement newBoxedExpression;

    @DataField("dmn-expression-type")
    private HTMLDivElement dmnExpressionType;

    @DataField("dmn-expression-editor")
    private HTMLDivElement dmnExpressionEditor;

    private TranslationService translationService;
    private ListSelectorView.Presenter listSelector;
    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private DefaultCanvasCommandFactory canvasCommandFactory;
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    private Event<DomainObjectSelectionEvent> domainObjectSelectionEvent;
    private Event<ExpressionEditorChanged> editorSelectedEvent;
    private PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider;
    private DefinitionUtils definitionUtils;

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private CellEditorControlsView.Presenter cellEditorControls;
    private RestrictedMousePanMediator mousePanMediator;
    private ExpressionContainerGrid expressionContainerGrid;
    private String nodeUUID;
    private HasExpression hasExpression;
    private Optional<HasName> hasName;
    private boolean isOnlyVisualChangeAllowed;

    public ExpressionEditorViewImpl() {
        //CDI proxy
    }

    @Inject
    public ExpressionEditorViewImpl(final Anchor returnToLink,
                                    final Span expressionName,
                                    final Span expressionType,
                                    final @DMNEditor DMNGridPanelContainer gridPanelContainer,
                                    final TranslationService translationService,
                                    final ListSelectorView.Presenter listSelector,
                                    final SessionManager sessionManager,
                                    final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                    final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                    final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                    final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                    final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                    final Event<ExpressionEditorChanged> editorSelectedEvent,
                                    final PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider,
                                    final DefinitionUtils definitionUtils,
                                    final HTMLAnchorElement tryIt,
                                    final HTMLAnchorElement switchBack,
                                    final HTMLDivElement betaBoxedExpressionToggle,
                                    final HTMLDivElement newBoxedExpression,
                                    final HTMLDivElement dmnExpressionType,
                                    final HTMLDivElement dmnExpressionEditor) {
        this.returnToLink = returnToLink;
        this.expressionName = expressionName;
        this.expressionType = expressionType;
        this.gridPanelContainer = gridPanelContainer;

        this.translationService = translationService;
        this.listSelector = listSelector;

        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.domainObjectSelectionEvent = domainObjectSelectionEvent;
        this.editorSelectedEvent = editorSelectedEvent;
        this.pmmlDocumentMetadataProvider = pmmlDocumentMetadataProvider;
        this.definitionUtils = definitionUtils;

        this.tryIt = tryIt;
        this.switchBack = switchBack;
        this.betaBoxedExpressionToggle = betaBoxedExpressionToggle;
        this.newBoxedExpression = newBoxedExpression;
        this.dmnExpressionType = dmnExpressionType;
        this.dmnExpressionEditor = dmnExpressionEditor;
    }

    @Override
    public void init(final ExpressionEditorView.Presenter presenter) {
        this.presenter = presenter;
        BoxedExpressionService.registerBroadcastForExpression(this);
    }

    @Override
    public void bind(final DMNSession session) {
        this.gridPanel = session.getGridPanel();
        this.gridLayer = session.getGridLayer();
        this.cellEditorControls = session.getCellEditorControls();
        this.mousePanMediator = session.getMousePanMediator();

        setupGridPanel();
        setupGridWidget();
        setupGridWidgetPanControl();
    }

    HasExpression getHasExpression() {
        return hasExpression;
    }

    Event<ExpressionEditorChanged> getEditorSelectedEvent() {
        return editorSelectedEvent;
    }

    String getNodeUUID() {
        return nodeUUID;
    }

    Optional<HasName> getHasName() {
        return hasName;
    }

    protected void setupGridPanel() {
        final Transform transform = new Transform().scale(VP_SCALE);
        gridPanel.getElement().setId("dmn_container_" + com.google.gwt.dom.client.Document.get().createUniqueId());
        gridPanel.getViewport().setTransform(transform);

        final BaseGridWidgetKeyboardHandler handler = new BaseGridWidgetKeyboardHandler(gridLayer);
        addKeyboardOperation(handler, new KeyboardOperationEditCell(gridLayer));
        addKeyboardOperation(handler, new KeyboardOperationEscapeGridCell(gridLayer));
        addKeyboardOperation(handler, new KeyboardOperationMoveLeft(gridLayer, gridPanel));
        addKeyboardOperation(handler, new KeyboardOperationMoveRight(gridLayer, gridPanel));
        addKeyboardOperation(handler, new KeyboardOperationMoveUp(gridLayer, gridPanel));
        addKeyboardOperation(handler, new KeyboardOperationMoveDown(gridLayer, gridPanel));
        addKeyboardOperation(handler, new KeyboardOperationInvokeContextMenuForSelectedCell(gridLayer));
        gridPanel.addKeyDownHandler(handler);

        gridPanelContainer.clear();
        gridPanelContainer.setWidget(gridPanel);
    }

    void addKeyboardOperation(final BaseGridWidgetKeyboardHandler handler,
                              final KeyboardOperation operation) {
        handler.addOperation(operation);
    }

    protected void setupGridWidget() {
        expressionContainerGrid = new ExpressionContainerGrid(gridLayer,
                                                              cellEditorControls,
                                                              translationService,
                                                              listSelector,
                                                              sessionManager,
                                                              sessionCommandManager,
                                                              canvasCommandFactory,
                                                              expressionEditorDefinitionsSupplier,
                                                              getExpressionGridCacheSupplier(),
                                                              this::setExpressionTypeText,
                                                              this::setExpressionNameText,
                                                              refreshFormPropertiesEvent,
                                                              domainObjectSelectionEvent);
        gridLayer.removeAll();
        gridLayer.add(expressionContainerGrid);
        gridLayer.select(expressionContainerGrid);
        gridLayer.enterPinnedMode(expressionContainerGrid,
                                  () -> {/*Nothing*/});
    }

    // This class (ExpressionEditorViewImpl) is instantiated when injected into SessionDiagramEditorScreen
    // which is before a Session has been created and the ExpressionGridCache CanvasControl has been registered.
    // Therefore we need to defer instance access to a Supplier.
    protected Supplier<ExpressionGridCache> getExpressionGridCacheSupplier() {
        return () -> ((DMNSession) sessionManager.getCurrentSession()).getExpressionGridCache();
    }

    protected void setupGridWidgetPanControl() {
        final TransformMediator defaultTransformMediator = new BoundaryTransformMediator(getExpressionContainerGrid());
        mousePanMediator.setTransformMediator(defaultTransformMediator);
        mousePanMediator.setBatchDraw(true);
        gridLayer.setDefaultTransformMediator(defaultTransformMediator);
        gridPanel.getViewport().getMediators().push(mousePanMediator);
    }

    @Override
    public void activate() {
        DMNLoader.renderBoxedExpressionEditor(
                ".kie-dmn-new-expression-editor",
                ExpressionPropsFiller.buildAndFillJsInteropProp(hasExpression.getExpression(), getExpressionName(), getTypeRef()),
                getHasExpression().isClearSupported(),
                buildPmmlParams()
        );
    }

    @Override
    public void clear() {
        getExpressionContainerGrid().clearExpressionType();
    }

    @Override
    public void setReturnToLinkText(final String text) {
        returnToLink.setTextContent(translationService.format(DMNEditorConstants.ExpressionEditor_ReturnToLink, text));
    }

    @Override
    public void setExpression(final String nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<HasName> hasName,
                              final boolean isOnlyVisualChangeAllowed) {
        this.nodeUUID = nodeUUID;
        this.hasExpression = hasExpression;
        this.hasName = hasName;
        this.isOnlyVisualChangeAllowed = isOnlyVisualChangeAllowed;
        getExpressionContainerGrid().setExpression(nodeUUID,
                                                   hasExpression,
                                                   hasName,
                                                   isOnlyVisualChangeAllowed);
        setExpressionNameText(hasName);
        setExpressionTypeText(Optional.ofNullable(hasExpression.getExpression()));
    }

    public ExpressionContainerGrid getExpressionContainerGrid() {
        return expressionContainerGrid;
    }

    @Override
    public void setExpressionNameText(final Optional<HasName> hasName) {
        hasName.ifPresent(name -> expressionName.setTextContent(name.getName().getValue()));
    }

    @Override
    public void setExpressionTypeText(final Optional<Expression> expression) {
        final String expressionTypeText = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression).get().getName();
        expressionType.setTextContent(translationService.format(DMNEditorConstants.ExpressionEditor_ExpressionTypeText,
                                                                expression.isPresent() ?
                                                                        expressionTypeText :
                                                                        "<" + expressionTypeText + ">"));
    }

    @EventHandler("try-it")
    public void onTryIt(final ClickEvent event) {
        activate();
        renderNewBoxedExpression();
        toggleBoxedExpression(true);
        preventDefault(event);
    }

    @EventHandler("switch-back")
    public void onSwitchBack(final ClickEvent event) {
        getExpressionGridCacheSupplier()
                .get()
                .removeExpressionGrid(getNodeUUID());
        setExpression(getNodeUUID(), getHasExpression(), getHasName(), isOnlyVisualChangeAllowed);
        renderOldBoxedExpression();
        toggleBoxedExpression(false);
        preventDefault(event);
    }

    public void resetExpressionDefinition(final ExpressionProps expressionProps) {

        final ClearExpressionCommand expressionCommand = new ClearExpressionCommand(getHasExpression(),
                                                                                    expressionProps,
                                                                                    getEditorSelectedEvent(),
                                                                                    getNodeUUID(),
                                                                                    this);

        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      expressionCommand);
    }

    public void broadcastLiteralExpressionDefinition(final LiteralProps literalExpressionProps) {

        final FillLiteralExpressionCommand expression = new FillLiteralExpressionCommand(getHasExpression(),
                                                                                         literalExpressionProps,
                                                                                         getEditorSelectedEvent(),
                                                                                         getNodeUUID(),
                                                                                         this);

        executeIfItHaveChanges(expression);
    }

    public void broadcastContextExpressionDefinition(final ContextProps contextProps) {

        if (!isUserAction(contextProps)) {
            return;
        }
        final FillContextExpressionCommand expression = new FillContextExpressionCommand(getHasExpression(),
                                                                                         contextProps,
                                                                                         getEditorSelectedEvent(),
                                                                                         getNodeUUID(),
                                                                                         this);

        executeIfItHaveChanges(expression);
    }

    boolean isUserAction(final ContextProps contextProps) {
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
    boolean isUserAction(final ExpressionProps entryExpression) {
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

    public void broadcastRelationExpressionDefinition(final RelationProps relationProps) {

        if (!isUserAction(relationProps)) {
            return;
        }

        final FillRelationExpressionCommand expression = new FillRelationExpressionCommand(getHasExpression(),
                                                                                           relationProps,
                                                                                           getEditorSelectedEvent(),
                                                                                           getNodeUUID(),
                                                                                           this);

        executeIfItHaveChanges(expression);
    }

    public void broadcastListExpressionDefinition(final ListProps listProps) {

        final FillListExpressionCommand expression = new FillListExpressionCommand(getHasExpression(),
                                                                                   listProps,
                                                                                   getEditorSelectedEvent(),
                                                                                   getNodeUUID(),
                                                                                   this);

        executeIfItHaveChanges(expression);
    }

    public void broadcastInvocationExpressionDefinition(final InvocationProps invocationProps) {

        if (!isUserAction(invocationProps)) {
            return;
        }

        final FillInvocationExpressionCommand expression = new FillInvocationExpressionCommand(getHasExpression(),
                                                                                               invocationProps,
                                                                                               getEditorSelectedEvent(),
                                                                                               getNodeUUID(),
                                                                                               this);

        executeIfItHaveChanges(expression);
    }

    public void broadcastFunctionExpressionDefinition(final FunctionProps functionProps) {
        final FillFunctionExpressionCommand expression = new FillFunctionExpressionCommand(getHasExpression(),
                                                                                           functionProps,
                                                                                           getEditorSelectedEvent(),
                                                                                           getNodeUUID(),
                                                                                           this);

        executeIfItHaveChanges(expression);
    }

    public void broadcastDecisionTableExpressionDefinition(final DecisionTableProps decisionTableProps) {

        if (!isUserAction(decisionTableProps)) {
            return;
        }

        final FillDecisionTableExpressionCommand expression = new FillDecisionTableExpressionCommand(getHasExpression(),
                                                                                                     decisionTableProps,
                                                                                                     getEditorSelectedEvent(),
                                                                                                     getNodeUUID(),
                                                                                                     this);

        executeIfItHaveChanges(expression);
    }

    void executeIfItHaveChanges(final FillExpressionCommand expressionCommand) {

        if (expressionCommand.hasChanges()) {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = createCommandBuilder();

            addExpressionCommand(expressionCommand, commandBuilder);
            addUpdatePropertyNameCommand(commandBuilder);

            execute(commandBuilder);
        }
    }

    void execute(final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder) {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      commandBuilder.build());
    }

    void addExpressionCommand(final FillExpressionCommand expressionCommand,
                              final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder) {
        commandBuilder.addCommand(expressionCommand);
    }

    void addUpdatePropertyNameCommand(final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder) {
        final AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
        final Element element = canvasHandler.getGraphIndex().get(getNodeUUID());
        if (element.getContent() instanceof Definition) {
            final Definition definition = (Definition) element.getContent();
            final String nameId = definitionUtils.getNameIdentifier(definition.getDefinition());
            commandBuilder.addCommand(canvasCommandFactory.updatePropertyValue(element,
                                                                               nameId,
                                                                               getHasName().orElse(HasName.NOP).getValue()));
        }
    }

    CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> createCommandBuilder() {
        return new CompositeCommand.Builder<>();
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

    void renderNewBoxedExpression() {
        toggleEditorsVisibility();
    }

    void renderOldBoxedExpression() {
        toggleEditorsVisibility();
    }

    void toggleBoxedExpression(final boolean enabled) {
        betaBoxedExpressionToggle.classList.toggle(ENABLED_BETA_CSS_CLASS, enabled);
    }

    private void preventDefault(final ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    private String getExpressionName() {
        final HasName fallbackHasName = getHasExpression() instanceof HasName ? (HasName) getHasExpression() : HasName.NOP;
        return getHasName().orElse(fallbackHasName).getValue().getValue();
    }

    @SuppressWarnings("unchecked")
    private String getTypeRef() {
        QName qName = BuiltInType.UNDEFINED.asQName();
        if (getHasExpression() instanceof HasVariable) {
            qName = ((HasVariable<InformationItemPrimary>) getHasExpression()).getVariable().getTypeRef();
        } else if (getHasExpression().getExpression() != null && getHasExpression().getExpression().asDMNModelInstrumentedBase().getParent() instanceof HasVariable) {
            final HasVariable<InformationItemPrimary> parent = (HasVariable<InformationItemPrimary>) getHasExpression().getExpression().asDMNModelInstrumentedBase().getParent();
            qName = parent != null && parent.getVariable() != null ? parent.getVariable().getTypeRef() : BuiltInType.UNDEFINED.asQName();
        }
        return qName.getLocalPart();
    }

    private void toggleEditorsVisibility() {
        dmnExpressionType.classList.toggle("hidden");
        dmnExpressionEditor.classList.toggle("hidden");
        newBoxedExpression.classList.toggle("hidden");
    }

    private PMMLParam[] buildPmmlParams() {
        return pmmlDocumentMetadataProvider.getPMMLDocumentNames()
                .stream()
                .map(documentToPMMLParamMapper())
                .toArray(PMMLParam[]::new);
    }

    private Function<String, PMMLParam> documentToPMMLParamMapper() {
        return documentName -> {
            final ModelsFromDocument[] modelsFromDocuments = pmmlDocumentMetadataProvider
                    .getPMMLDocumentModels(documentName)
                    .stream()
                    .map(modelToEntryInfoMapper(documentName))
                    .toArray(ModelsFromDocument[]::new);
            return new PMMLParam(documentName, modelsFromDocuments);
        };
    }

    private Function<String, ModelsFromDocument> modelToEntryInfoMapper(String documentName) {
        return modelName -> {
            final EntryInfo[] parametersFromModel = pmmlDocumentMetadataProvider.getPMMLDocumentModelParameterNames(documentName, modelName)
                    .stream()
                    .map(parameter -> new EntryInfo(parameter, UNDEFINED.getText()))
                    .toArray(EntryInfo[]::new);
            return new ModelsFromDocument(modelName, parametersFromModel);
        };
    }

    @EventHandler("returnToLink")
    void onClickReturnToLink(final ClickEvent event) {
        presenter.exit();
    }

    @Override
    public void onResize() {
        gridPanelContainer.onResize();
    }

    @Override
    public void refresh() {
        gridLayer.batch();
    }

    @Override
    public void setFocus() {
        gridPanel.setFocus(true);
    }
}
