/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.kogito.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.JsLienzo;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.editor.EditorSessionCommands;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasDiagramValidator;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.forms.client.widgets.FormsFlushManager;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPreviewAndExplorerDock;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.service.AbstractKogitoClientDiagramService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.bridge.Notification;
import org.uberfire.workbench.model.bridge.NotificationSeverity;

@ApplicationScoped
//@Named(BPMNDiagramEditor.EDITOR_ID) uncomment after removing BPMNDiagramEditorActivity
public class BPMNDiagramEditor {

    public static final String EDITOR_ID = "BPMNDiagramEditor";

    private final Promises promises;
    private final ReadOnlyProvider readOnlyProvider;
    private final StunnerEditor stunnerEditor;
    private final ClientTranslationService translationService;
    private final AbstractKogitoClientDiagramService diagramServices;
    private final CanvasFileExport canvasFileExport;
    private final DiagramEditorPreviewAndExplorerDock diagramPreviewAndExplorerDock;
    private final DiagramEditorPropertiesDock diagramPropertiesDock;
    private final FormsFlushManager formsFlushManager;
    private final EditorSessionCommands commands;
    private CanvasDiagramValidator<AbstractCanvasHandler> validator;

    private static final Map<Violation.Type, String> validationSeverityTable = new HashMap<Violation.Type, String>() {{
        put(Violation.Type.INFO, NotificationSeverity.INFO);
        put(Violation.Type.WARNING, NotificationSeverity.WARNING);
        put(Violation.Type.ERROR, NotificationSeverity.ERROR);
    }};

    @Inject
    public BPMNDiagramEditor(Promises promises,
                             ReadOnlyProvider readOnlyProvider,
                             StunnerEditor stunnerEditor,
                             ClientTranslationService translationService,
                             AbstractKogitoClientDiagramService diagramServices,
                             CanvasFileExport canvasFileExport,
                             DiagramEditorPreviewAndExplorerDock diagramPreviewAndExplorerDock,
                             DiagramEditorPropertiesDock diagramPropertiesDock,
                             FormsFlushManager formsFlushManager,
                             EditorSessionCommands commands,
                             final CanvasDiagramValidator<AbstractCanvasHandler> validator) {
        this.promises = promises;
        this.readOnlyProvider = readOnlyProvider;
        this.stunnerEditor = stunnerEditor;
        this.translationService = translationService;
        this.diagramServices = diagramServices;
        this.canvasFileExport = canvasFileExport;
        this.diagramPreviewAndExplorerDock = diagramPreviewAndExplorerDock;
        this.diagramPropertiesDock = diagramPropertiesDock;
        this.formsFlushManager = formsFlushManager;
        this.commands = commands;
        this.validator = validator;
    }

    public void onStartup(final PlaceRequest place) {
        boolean isReadOnly = place.getParameter("readOnly", null) != null;
        isReadOnly |= readOnlyProvider.isReadOnlyDiagram();
        stunnerEditor.setReadOnly(isReadOnly);
        docksInit();
    }

    public void onOpen() {
    }

    public void onClose() {
        close();
    }

    private void close() {
        commands.clear();
        docksClose();
        stunnerEditor.close();
    }

    public IsWidget asWidget() {
        return stunnerEditor.getView();
    }

    public Promise<String> getContent() {
        flush();
        return diagramServices.transform(stunnerEditor.getDiagram());
    }

    public Promise<String> getPreview() {
        CanvasHandler canvasHandler = stunnerEditor.getCanvasHandler();
        if (canvasHandler != null) {
            return promises.resolve(canvasFileExport.exportToSvg((AbstractCanvasHandler) canvasHandler));
        } else {
            return promises.resolve("");
        }
    }

    public Promise validate() {
        CanvasHandler canvasHandler = stunnerEditor.getCanvasHandler();
        stunnerEditor.getPresenter().displayNotifications(t -> false);

        List<Notification> violationMessages = new ArrayList<>();

        validator.validate((AbstractCanvasHandler) canvasHandler, violations -> {

            if (!violations.isEmpty()) {
                for (DiagramElementViolation<RuleViolation> next : violations) {
                    final Collection<DomainViolation> domainViolations = next.getDomainViolations();
                    domainViolations.forEach(item -> violationMessages.add(createNotification(item)));
                }
            }
        });

        return Promise.resolve(violationMessages.toArray(new Notification[0]));
    }

    private Notification createNotification(DomainViolation item) {
        CanvasHandler canvasHandler = stunnerEditor.getCanvasHandler();
        String errorMessage = translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_ErrorMessageLabel);
        Notification notification = new Notification();
        notification.setMessage(errorMessage + ": " + item.getUUID() + " - " + item.getMessage());
        notification.setSeverity(translateViolationType(item.getViolationType()));
        notification.setPath(canvasHandler.getDiagram().getMetadata().getPath().toString());
        return notification;
    }

    private String translateViolationType(Violation.Type violationType) {
        return this.validationSeverityTable.getOrDefault(violationType, NotificationSeverity.INFO);
    }

    private void flush() {
        formsFlushManager.flush(stunnerEditor.getSession());
    }

    public Promise<Void> setContent(final String path, final String value) {
        close();
        return promises.create((success, failure) -> {
            diagramServices.transform(path,
                                      value,
                                      new ServiceCallback<Diagram>() {

                                          @Override
                                          public void onSuccess(final Diagram diagram) {
                                              stunnerEditor
                                                      .close()
                                                      .open(diagram, new SessionPresenter.SessionPresenterCallback() {
                                                          @Override
                                                          public void onSuccess() {
                                                              onDiagramOpenSuccess();
                                                              success.onInvoke((Void) null);
                                                          }

                                                          @Override
                                                          public void onError(ClientRuntimeError error) {
                                                              failure.onInvoke(error);
                                                          }
                                                      });
                                          }

                                          @Override
                                          public void onError(final ClientRuntimeError error) {
                                              stunnerEditor.handleError(error);
                                              failure.onInvoke(error);
                                          }
                                      });
        });
    }

    private void onDiagramOpenSuccess() {
        Metadata metadata = stunnerEditor.getCanvasHandler().getDiagram().getMetadata();
        String title = metadata.getTitle();
        Path path = PathFactory.newPath(title, "/" + title + ".bpmn");
        metadata.setPath(path);
        commands.bind(stunnerEditor.getSession());
        docksOpen();
        initLienzoType();
    }

    // TODO: PoC
    public static JsLienzo jsLienzo;

    // TODO: PoC
    private void initLienzoType() {
        LienzoCanvas canvas = (LienzoCanvas) stunnerEditor.getCanvasHandler().getCanvas();
        LienzoPanel panel = (LienzoPanel) canvas.getView().getPanel();
        LienzoBoundsPanel lienzoPanel = panel.getView();
        jsLienzo = new JsLienzo(lienzoPanel, lienzoPanel.getLayer());
        //setupJsLienzoType(jsLienzo);
        editor = stunnerEditor;
        setupJsLienzoTypeNative(jsLienzo);
    }

    // TODO: PoC - Move to J2CL impl
    private static native void setupJsLienzoType(Object jsLienzo) /*-{
        $wnd.jsLienzoSuper = jsLienzo;
    }-*/;

    private static StunnerEditor editor = null;

    private static void setupJsLienzoTypeNative(JsLienzo jsLienzo) {
        WindowJSType.linkLienzoJS(jsLienzo);
        WindowJSType.linkStunnerCommand(() -> logNodes());
        WindowJSType.linkStunnerOperation(UUID -> performOnNode(UUID));
    }

    public static void performOnNode(String UUID) {
        final Node node = editor.getCanvasHandler().getDiagram().getGraph().getNode(UUID);

        if (node.getContent() instanceof View) {
            View view = (View) node.getContent();
            DomGlobal.console.log("View: " + view.getDefinition());
            final Object definition = view.getDefinition();

            // Events
            if (definition instanceof BaseStartEvent) {
                jsLienzo.getWiresShape(UUID).setBorderColorEvent("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorEvent("blue");
            } else if (definition instanceof BaseCatchingIntermediateEvent) {
                jsLienzo.getWiresShape(UUID).setBorderColorEvent("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorEvent("blue");
            } else if (definition instanceof BaseThrowingIntermediateEvent) {
                jsLienzo.getWiresShape(UUID).setBorderColorEvent("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorEvent("blue");
            } else if (definition instanceof BaseEndEvent) {

                jsLienzo.getWiresShape(UUID).setBorderColorEvent("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorEvent("blue");
                // Tasks
            } else if (definition instanceof BaseTask) {
                // Subprocess
                jsLienzo.getWiresShape(UUID).setBorderColorTask("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorTask("blue");
            } else if (definition instanceof BaseSubprocess) {
                jsLienzo.getWiresShape(UUID).setBorderColorTask("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorTask("blue");

                // Gateway
            } else if (definition instanceof BaseGateway) {
                jsLienzo.getWiresShape(UUID).setBorderColorGateway("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorGateway("blue");

                // Lane
            } else if (definition instanceof Lane) {
                jsLienzo.getWiresShape(UUID).setBorderColorLane("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorLane("blue");

                // Text Annotation
            } else if (definition instanceof TextAnnotation) {
                jsLienzo.getWiresShape(UUID).setBorderColorTextAnnotation("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorTextAnnotation("blue");

                // DataObject
            } else if (definition instanceof DataObject) {
                jsLienzo.getWiresShape(UUID).setBorderColorDataObject("red");
                jsLienzo.getWiresShape(UUID).setBackgroundColorDataObject("blue");
            }
        }
    }

    public static void logNodes() {
        final Iterable<Node> nodes = editor.getCanvasHandler().getDiagram().getGraph().nodes();
        final Collection<Shape> shapes = editor.getCanvasHandler().getCanvas().getShapes();

        for (Shape shape : shapes) {
            DomGlobal.console.log("Shape: " + shape);
        }

        for (Node node : nodes) {
            DomGlobal.console.log("Node: " + node.getUUID());
            View view = (View) node.getContent();
            DomGlobal.console.log("View: " + view.getDefinition());
            performOnNode(node.getUUID());
        }
    }

    void docksInit() {
        diagramPropertiesDock.init();
        diagramPreviewAndExplorerDock.init();
    }

    void docksOpen() {
        diagramPropertiesDock.open();
        diagramPreviewAndExplorerDock.open();
    }

    void docksClose() {
        diagramPropertiesDock.close();
        diagramPreviewAndExplorerDock.close();
    }
}
