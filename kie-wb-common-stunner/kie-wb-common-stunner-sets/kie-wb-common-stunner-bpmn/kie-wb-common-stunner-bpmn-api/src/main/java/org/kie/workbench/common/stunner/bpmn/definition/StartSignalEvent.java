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

package org.kie.workbench.common.stunner.bpmn.definition;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.CorrelationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.InterruptingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@Morph(base = BaseStartEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class StartSignalEvent extends BaseStartEvent {

    @Property
    @FormField(afterElement = "general")
    @Valid
    protected InterruptingSignalEventExecutionSet executionSet;

    @Property
    @FormField(afterElement = "executionSet")
    @Valid
    protected DataIOSet dataIOSet;

    @Property
    @FormField(afterElement = "dataIOset")
    @Valid
    protected CorrelationSet correlationSet;

    public StartSignalEvent() {
        this(new BPMNGeneralSet(""),
             new BackgroundSet(),
             new FontSet(),
             new CircleDimensionSet(new Radius()),
             new SimulationAttributeSet(),
             new AdvancedData(),
             new DataIOSet(),
             new CorrelationSet(),
             new InterruptingSignalEventExecutionSet());
    }

    public StartSignalEvent(final @MapsTo("general") BPMNGeneralSet general,
                            final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                            final @MapsTo("fontSet") FontSet fontSet,
                            final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                            final @MapsTo("simulationSet") SimulationAttributeSet simulationSet,
                            final @MapsTo("advancedData") AdvancedData advancedData,
                            final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                            final @MapsTo("correlationSet") CorrelationSet correlationSet,
                            final @MapsTo("executionSet") InterruptingSignalEventExecutionSet executionSet) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet,
              advancedData);
        this.dataIOSet = dataIOSet;
        this.correlationSet = correlationSet;
        this.executionSet = executionSet;
    }

    public InterruptingSignalEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final InterruptingSignalEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    public CorrelationSet getCorrelationSet() {
        return correlationSet;
    }

    public void setCorrelationSet(CorrelationSet correlationSet) {
        this.correlationSet = correlationSet;
    }

    @Override
    public boolean hasOutputVars() {
        return true;
    }

    @Override
    public boolean isSingleOutputVar() {
        return true;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         dataIOSet.hashCode(),
                                         correlationSet.hashCode(),
                                         executionSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StartSignalEvent) {
            StartSignalEvent other = (StartSignalEvent) o;
            return super.equals(other) &&
                    Objects.equals(dataIOSet, other.dataIOSet) &&
                    Objects.equals(correlationSet, other.correlationSet) &&
                    Objects.equals(executionSet, other.executionSet);
        }
        return false;
    }
}
