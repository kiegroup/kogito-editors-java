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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

import static com.google.gwt.dom.client.Style.Display.NONE;
import static com.google.gwt.dom.client.Style.Display.TABLE;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.createDataTypeDisplayName;

@Dependent
public class CorrelationsEditorWidget implements CorrelationsEditorWidgetView.Presenter {

    private static List<String> defaultTypes = new ArrayList<>(Arrays.asList("Boolean", "Float", "Integer", "Object", "String"));
    protected SessionManager sessionManager;
    protected DataTypeNamesService dataTypeNamesService;
    protected Event<NotificationEvent> notification;
    protected Event<RefreshFormPropertiesEvent> refreshFormsEvent;
    protected Map<String, String> dataTypes = new TreeMap<>();

    @Inject
    protected CorrelationsEditorWidgetView view;

    @Inject
    public CorrelationsEditorWidget(final SessionManager sessionManager,
                                    final DataTypeNamesService dataTypeNamesService,
                                    final Event<NotificationEvent> notification,
                                    final Event<RefreshFormPropertiesEvent> refreshFormsEvent) {
        this.sessionManager = sessionManager;
        this.dataTypeNamesService = dataTypeNamesService;
        this.notification = notification;
        this.refreshFormsEvent = refreshFormsEvent;

        loadDefaultPropertyTypes();
        loadServerPropertyTypes();
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public Correlation createCorrelation() {
        return new Correlation();
    }

    @Override
    public void addCorrelation() {
        List<Correlation> correlations = view.getCorrelations();
        if (correlations.isEmpty()) {
            view.setDisplayStyle(TABLE);
        }

        correlations.add(createCorrelation());

        CorrelationListItemWidgetView widget = view.getCorrelationWidget(view.getCorrelationsCount() - 1);
        widget.setParentWidget(this);
    }

    @Override
    public void removeCorrelation(final Correlation correlation) {
        List<Correlation> correlations = view.getCorrelations();
        correlations.remove(correlation);
        if (view.getCorrelations().isEmpty()) {
            view.setDisplayStyle(NONE);
        }
    }

    @Override
    public boolean isDuplicateCorrelation(final Correlation correlation) {
        List<Correlation> correlations = view.getCorrelations();
        if (correlations != null && !correlations.isEmpty()) {
            for (Correlation compareCorrelation : correlations) {
                if (compareCorrelation.equals(correlation)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Widget getWidget() {
        return view;
    }

    @Override
    public List<Correlation> getData() {
        List<Correlation> correlations = new ArrayList<>();
        if (!view.getCorrelations().isEmpty()) {
            correlations.addAll(view.getCorrelations());
        }
        return correlations;
    }

    @Override
    public void setData(final List<Correlation> correlations) {
        if (correlations == null || correlations.isEmpty()) {
            view.setDisplayStyle(NONE);
        } else {
            view.setDisplayStyle(TABLE);
        }

        if (correlations != null) {
            view.setCorrelations(correlations);
            for (int i = 0; i < correlations.size(); i++) {
                view.getCorrelationWidget(i).setParentWidget(this);
            }
        }
    }

    public Map<String, String> getPropertyTypes() {
        return dataTypes;
    }

    public String getPropertyType(String displayName) {
        return dataTypes.keySet()
                .stream()
                .filter(key -> displayName.equals(dataTypes.get(key)))
                .findFirst()
                .orElse(displayName);
    }

    protected void loadDefaultPropertyTypes() {
        addPropertyTypes(defaultTypes, false);
    }

    protected void loadServerPropertyTypes() {
        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        final Path path = diagram.getMetadata().getPath();
        dataTypeNamesService
                .call(path)
                .then(serverDataTypes -> {
                    addPropertyTypes(serverDataTypes, true);
                    return null;
                })
                .catch_(exception -> {
                    notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.Error_retrieving_datatypes(),
                                                            NotificationEvent.NotificationType.ERROR));
                    return null;
                });
    }

    protected void addPropertyTypes(List<String> dataTypesList, boolean useDisplayNames) {
        for (String dataType : dataTypesList) {
            if (dataType.contains("Asset-")) {
                dataType = dataType.substring(6);
            }

            String displayName = useDisplayNames ? createDataTypeDisplayName(dataType) : dataType;
            dataTypes.put(dataType, displayName);
        }
    }
}

