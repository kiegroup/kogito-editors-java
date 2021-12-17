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

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;

@Dependent
@Templated("CorrelationsEditorWidget.html#widget")
public class CorrelationsEditorWidgetView extends Composite {

    @DataField
    private final TableElement table = Document.get().createTableElement();

    @Inject
    @DataField
    protected Button addCorrelationButton;

    @DataField
    protected TableCellElement idTableHeader = Document.get().createTHElement();

    @DataField
    protected TableCellElement nameTableHeader = Document.get().createTHElement();

    @DataField
    protected TableCellElement propertyIdTableHeader = Document.get().createTHElement();

    @DataField
    protected TableCellElement propertyNameTableHeader = Document.get().createTHElement();

    @Inject
    @DataField
    @Table(root = "tbody")
    protected ListWidget<Correlation, CorrelationListItemWidgetView> correlations;

    @DataField
    private HeadingElement tableTitle = Document.get().createHElement(3);

    private Presenter presenter;

    public void init(final CorrelationsEditorWidgetView.Presenter presenter) {
        this.presenter = presenter;

        tableTitle.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Correlations_Title());

        addCorrelationButton.setText(StunnerFormsClientFieldsConstants.CONSTANTS.Add());
        addCorrelationButton.setIcon(IconType.PLUS);

        idTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Id());
        nameTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Name());
        propertyIdTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.PropertyId());
        propertyNameTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.PropertyName());
    }

    public int getCorrelationsCount() {
        return correlations.getValue().size();
    }

    public void setDisplayStyle(Style.Display displayStyle) {
        table.getStyle().setDisplay(displayStyle);
    }

    public List<Correlation> getCorrelations() {
        return correlations.getValue();
    }

    public void setCorrelations(List<Correlation> correlations) {
        this.correlations.setValue(correlations);
    }

    public CorrelationListItemWidgetView getCorrelationWidget(final int index) {
        return correlations.getComponent(index);
    }

    @EventHandler("addCorrelationButton")
    public void handleAddCorrelationButton(final ClickEvent e) {
        presenter.addCorrelation();
    }

    interface Presenter {

        List<Correlation> getData();

        void setData(final List<Correlation> correlations);

        Widget getWidget();

        Correlation createCorrelation();

        void addCorrelation();

        void removeCorrelation(final Correlation imp);

        boolean isDuplicateCorrelation(final Correlation imp);
    }
}
