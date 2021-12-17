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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.Correlations;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.CorrelationsValue;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.Pair;

public class CorrelationsProvider
        extends AbstractProcessFilteredNodeProvider {

    @Inject
    public CorrelationsProvider(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public Predicate<Node> getFilter() {
        //not used in this implementation.
        return node -> true;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        //not used in this implementation.
        return null;
    }

    @Override
    protected Collection<Pair<Object, String>> findElements(Predicate<Node> filter,
                                                            Function<Node, Pair<Object, String>> mapper) {
        Collection<Pair<Object, String>> result = new ArrayList<>();
        String elementUUID = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getMetadata().getCanvasRootUUID();
        Node node;
        if (elementUUID != null) {
            node = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph().getNode(elementUUID);
            Object oDefinition = ((View) node.getContent()).getDefinition();
            if (oDefinition instanceof BPMNDiagram) {
                BPMNDiagram bpmnDiagram = (BPMNDiagram) oDefinition;

                Correlations correlations = bpmnDiagram.getCollaborationSet().getCorrelations();
                if (correlations != null) {
                    CorrelationsValue correlationsValue = correlations.getValue();
                    List<Correlation> correlationList = correlationsValue.getCorrelations();
                    for (Correlation correlation : correlationList) {
                        addCorrelationToResult(result, correlation);
                    }
                }
            }
        }

        return result;
    }

    private void addCorrelationToResult(Collection<Pair<Object, String>> result, Correlation correlation) {
        String optionText = correlation.getName() + " - " + correlation.getPropertyName();
        Pair<Object, String> resultPair = new Pair<>(correlation, optionText);
        result.add(resultPair);
    }
}
