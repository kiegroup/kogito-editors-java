/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RuleAnnotationClauseTextTest {

    private static final String TEXT_VALUE = "TEXT-VALUE";

    @Test
    public void testCopy() {
        final RuleAnnotationClauseText source = new RuleAnnotationClauseText();
        source.setText(new Text(TEXT_VALUE));

        final RuleAnnotationClauseText target = source.copy();

        assertNotNull(target);
        assertEquals(TEXT_VALUE, target.getText().getValue());
    }

    @Test
    public void testEqualsNotIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final RuleAnnotationClauseText ruleAnnotationClause1 = new RuleAnnotationClauseText();
        final RuleAnnotationClauseText ruleAnnotationClause2 = new RuleAnnotationClauseText();

        ruleAnnotationClause1.setId(id1);
        ruleAnnotationClause2.setId(id2);

        assertFalse(ruleAnnotationClause1.equals(ruleAnnotationClause2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_SameId() {
        final Id same = new Id();
        final RuleAnnotationClauseText ruleAnnotationClause1 = new RuleAnnotationClauseText();
        final RuleAnnotationClauseText ruleAnnotationClause2 = new RuleAnnotationClauseText();

        ruleAnnotationClause1.setId(same);
        ruleAnnotationClause2.setId(same);

        assertTrue(ruleAnnotationClause1.equals(ruleAnnotationClause2, false));
    }

    @Test
    public void testEqualsIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final RuleAnnotationClauseText ruleAnnotationClause1 = new RuleAnnotationClauseText();
        final RuleAnnotationClauseText ruleAnnotationClause2 = new RuleAnnotationClauseText();

        ruleAnnotationClause1.setId(id1);
        ruleAnnotationClause2.setId(id2);

        assertTrue(ruleAnnotationClause1.equals(ruleAnnotationClause2, true));
    }

    @Test
    public void testEqualsIgnoringId_SameId() {
        final Id same = new Id();
        final RuleAnnotationClauseText ruleAnnotationClause1 = new RuleAnnotationClauseText();
        final RuleAnnotationClauseText ruleAnnotationClause2 = new RuleAnnotationClauseText();

        ruleAnnotationClause1.setId(same);
        ruleAnnotationClause2.setId(same);

        assertTrue(ruleAnnotationClause1.equals(ruleAnnotationClause2, true));
    }
}
