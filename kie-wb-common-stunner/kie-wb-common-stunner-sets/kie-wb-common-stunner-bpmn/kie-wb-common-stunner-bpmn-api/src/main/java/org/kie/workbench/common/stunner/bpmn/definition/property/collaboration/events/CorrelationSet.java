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

package org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "correlationProperty")
public class CorrelationSet implements BaseCorrelationSet {

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "false")},
            afterElement = "isInterrupting"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.CorrelationsProvider"
    )
    @Valid
    private CorrelationProperty correlationProperty;

    @Property
    @FormField(
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")},
            afterElement = "correlationProperty"
    )

    @Valid
    private MessagePath messagePath;

    @Property
    @FormField(
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")},
            afterElement = "messagePath"
    )
    @Valid
    private DataPath dataPath;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "false")},
            afterElement = "isInterrupting"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.DataTypeProvider"
    )
    @Valid
    private CorrelationPropertyType correlationPropertyType;

    public CorrelationSet() {
        this(new CorrelationProperty(),
             new MessagePath(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                    ""))),
             new DataPath(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                 ""))),
             new CorrelationPropertyType());
    }

    public CorrelationSet(final @MapsTo("correlationProperty") CorrelationProperty correlationProperty,
                          final @MapsTo("messagePath") MessagePath messagePath,
                          final @MapsTo("dataPath") DataPath dataPath,
                          final @MapsTo("correlationPropertyType") CorrelationPropertyType correlationPropertyType) {
        this.correlationProperty = correlationProperty;
        this.messagePath = messagePath;
        this.dataPath = dataPath;
        this.correlationPropertyType = correlationPropertyType;
    }

    @Override
    public CorrelationProperty getCorrelationProperty() {
        return correlationProperty;
    }

    public void setCorrelationProperty(final CorrelationProperty correlationProperty) {
        this.correlationProperty = correlationProperty;
    }

    @Override
    public MessagePath getMessagePath() {
        return messagePath;
    }

    public void setMessagePath(final MessagePath messagePath) {
        this.messagePath = messagePath;
    }

    @Override
    public DataPath getDataPath() {
        return dataPath;
    }

    public void setDataPath(final DataPath dataPath) {
        this.dataPath = dataPath;
    }

    @Override
    public CorrelationPropertyType getCorrelationPropertyType() {
        return correlationPropertyType;
    }

    public void setCorrelationPropertyType(final CorrelationPropertyType correlationPropertyType) {
        this.correlationPropertyType = correlationPropertyType;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(correlationProperty),
                                         Objects.hashCode(messagePath),
                                         Objects.hashCode(dataPath),
                                         Objects.hashCode(correlationPropertyType));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CorrelationSet) {
            CorrelationSet other = (CorrelationSet) o;
            return Objects.equals(correlationProperty, other.correlationProperty) &&
                    Objects.equals(messagePath, other.messagePath) &&
                    Objects.equals(dataPath, other.dataPath) &&
                    Objects.equals(correlationPropertyType, other.correlationPropertyType);
        }
        return false;
    }
}
