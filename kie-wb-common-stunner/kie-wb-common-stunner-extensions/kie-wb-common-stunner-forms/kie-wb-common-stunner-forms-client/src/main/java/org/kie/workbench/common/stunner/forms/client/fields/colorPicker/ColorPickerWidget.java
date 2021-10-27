/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.forms.client.fields.colorPicker;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition;
import org.uberfire.ext.widgets.common.client.colorpicker.ColorPickerDialog;

@Dependent
@Templated
public class ColorPickerWidget extends Composite implements HasValue<String> {

    @Inject
    @DataField
    private Button colorButton;

    @Inject
    @DataField
    private TextBox colorTextBox;

    private String color;
    private boolean readOnly;

    @EventHandler("colorButton")
    public void onClickColorButton(final ClickEvent clickEvent) {
        showColorDialog(getColorButton());
    }

    @EventHandler("colorTextBox")
    public void onColorTextBoxChange(final ChangeEvent changeEvent) {
        final String newColorValue = getColorTextBox().getValue();
        if (newColorValue != null && newColorValue.matches(ColorPickerFieldDefinition.COLOR_REGEXP)) {
            setValue(newColorValue, true);
        } else {
            setValue(color, false);
        }
    }

    protected void showColorDialog(final UIObject owner) {
        if (readOnly) {
            return;
        }
        final ColorPickerDialog dlg = new ColorPickerDialog();
        dlg.getElement().getStyle().setZIndex(9999);
        dlg.addDialogClosedHandler(event -> {
            if (!event.isCanceled()) {
                setValue("#" + dlg.getColor(),
                         true);
            }
        });
        String color = getValue();
        if (color.startsWith("#")) {
            color = color.substring(1);
        }
        dlg.setColor(color);
        dlg.showRelativeTo(owner);
    }

    @Override
    public String getValue() {
        return color;
    }

    @Override
    public void setValue(final String value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final String value,
                         final boolean fireEvents) {
        String oldValue = color;
        color = value;
        initTextBox();
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            color);
        }
    }

    protected void initTextBox() {
        getColorTextBox().setText(color);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        getColorTextBox().setReadOnly(readOnly);
        getColorButton().setEnabled(!readOnly);
    }

    /**
     * For testing purposes
     */
    TextBox getColorTextBox() {
        return colorTextBox;
    }

    Button getColorButton() {
        return colorButton;
    }
}
