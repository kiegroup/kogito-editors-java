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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor.VariableListItemWidgetView;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;

@Templated("CorrelationsEditorWidget.html#correlation")
public class CorrelationListItemWidgetView
        extends Composite
        implements HasModel<Correlation>,
                   ComboBoxView.ModelPresenter{

    protected static final String CUSTOM_PROMPT = "Custom" + ListBoxValues.EDIT_SUFFIX;
    protected static final String ENTER_TYPE_PROMPT = "Enter type" + ListBoxValues.EDIT_SUFFIX;

    @Inject
    @AutoBound
    protected DataBinder<Correlation> correlationDataBinder;

    @Inject
    @Bound
    @DataField
    protected Input id;

    @Inject
    @Bound
    @DataField
    protected Input name;

    @Inject
    @Bound
    @DataField
    protected Input propertyId;

    @Inject
    @Bound
    @DataField
    protected Input propertyName;

    @DataField
    protected ValueListBox<String> propertyType = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            return object != null ? object : "";
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    @DataField
    protected CustomDataTypeTextBox customPropertyType;

    @Inject
    protected ComboBox typesComboBox;

    @Inject
    @DataField
    protected Button deleteButton;

    private CorrelationsEditorWidget parentWidget;

    public void setParentWidget(final CorrelationsEditorWidgetView.Presenter parentWidget) {
        this.parentWidget = (CorrelationsEditorWidget) parentWidget;
        initListItem();
    }

    @Override
    public Correlation getModel() {
        return correlationDataBinder.getModel();
    }

    @Override
    public void setModel(final Correlation model) {
        correlationDataBinder.setModel(model);
        initControls();
    }

    @Override
    public void setTextBoxModelValue(final TextBox textBox, final String value) {
        if (value != null && !value.isEmpty()) {
            getModel().setPropertyType(value);
        }
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox, final String displayName) {
        String value = parentWidget.getPropertyType(displayName);
        getModel().setPropertyType(value);
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        return getModel().getPropertyType();
    }

    @Override
    public void notifyModelChanged() {
        //There is no need to notify a model change here.
    }

    public String getId() {
        return getModel().getId();
    }

    public void setId(String id) {
        getModel().setId(id);
    }

    public String getName() {
        return getModel().getName();
    }

    public void setName(String name) {
        getModel().setName(name);
    }

    public String getPropertyId() {
        return getModel().getPropertyId();
    }

    public void setPropertyId(String propertyId) {
        getModel().setPropertyId(propertyId);
    }

    public String getPropertyName() {
        return getModel().getPropertyName();
    }

    public void setPropertyName(String propertyName) {
        getModel().setPropertyName(propertyName);
    }

    public String getPropertyType() {
        return getModel().getPropertyType();
    }

    public void setPropertyType(String propertyType) {
        getModel().setPropertyType(propertyType);
    }

    private void initControls() {
        deleteButton.setIcon(IconType.TRASH);
        id.setText(getId());
        name.setText(getName());
        propertyId.setText(getPropertyId());
        propertyName.setText(getPropertyName());

        customPropertyType.addKeyDownHandler(event -> {
            int iChar = event.getNativeKeyCode();
            if (iChar == ' ') {
                event.preventDefault();
            }
        });
    }

    private void initListItem() {
        Map<String, String> propertyTypes = parentWidget.getPropertyTypes();

        ListBoxValues typeNameListBoxValues = new ListBoxValues(VariableListItemWidgetView.CUSTOM_PROMPT,
                                                                 "Edit" + " ",
                                                                 null);

        List<String> displayNames = new ArrayList<>(propertyTypes.values());
        typeNameListBoxValues.addValues(displayNames);
        typesComboBox.setShowCustomValues(true);
        typesComboBox.setListBoxValues(typeNameListBoxValues);

        String propertyTypeName = getModel().getPropertyType();
        if (propertyTypeName == null || propertyTypeName.isEmpty()) {
            propertyTypeName = Object.class.getSimpleName();
        }

        String displayName = parentWidget.getPropertyType(propertyTypeName);

        if ((propertyTypeName.equals(displayName))) {
            displayName = parentWidget.getPropertyTypes().get(propertyTypeName);
        }

        propertyType.setValue(displayName);
        typesComboBox.init(this,
                                true,
                           this.propertyType,
                                customPropertyType,
                                false,
                                true,
                                CUSTOM_PROMPT,
                                ENTER_TYPE_PROMPT);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeCorrelation(getModel());
    }
}
