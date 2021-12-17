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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.CorrelationsValue;

public class CorrelationsEditor implements CorrelationsEditorView.Presenter {

    CorrelationsEditor.GetDataCallback callback = null;
    @Inject
    CorrelationsEditorView view;
    private CorrelationsValue correlationsValue;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public void ok() {
        if (callback != null) {
            CorrelationsValue correlationsValue = new CorrelationsValue(view.getCorrelations());
            callback.getData(correlationsValue);
        }
        view.hideView();
    }

    @Override
    public CorrelationsValue getCorrelations() {
        return new CorrelationsValue(view.getCorrelations());
    }

    @Override
    public void cancel() {
        view.hideView();
    }

    @Override
    public void setCallback(final CorrelationsEditor.GetDataCallback callback) {
        this.callback = callback;
    }

    @Override
    public void show() {
        view.showView();
    }

    public void setCorrelationsValue(CorrelationsValue correlationsValue) {
        this.correlationsValue = correlationsValue;
        if (correlationsValue != null) {
            view.setCorrelations(correlationsValue.getCorrelations());
        }
    }

    public interface GetDataCallback {
        void getData(CorrelationsValue correlationsValue);
    }
}
