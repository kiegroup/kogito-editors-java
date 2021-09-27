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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OutputClauseUnaryTestsTest {

    private static final String UNARY_ID = "UNARY_ID";
    private static final String TEXT = "TEXT";

    @Test
    public void testCopy() {
        final OutputClauseUnaryTests source = new OutputClauseUnaryTests(
                new Id(UNARY_ID),
                new Text(TEXT),
                ConstraintType.ENUMERATION
        );

        final OutputClauseUnaryTests target = source.copy();

        assertNotNull(target);
        assertNotEquals(UNARY_ID, target.getId().getValue());
        assertEquals(TEXT, target.getText().getValue());
        assertEquals(ConstraintType.ENUMERATION, target.getConstraintType());
    }

    @Test
    public void testEqualsNotIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final OutputClauseUnaryTests output1 = new OutputClauseUnaryTests(id1, null, null);
        final OutputClauseUnaryTests output2 = new OutputClauseUnaryTests(id2, null, null);

        assertFalse(output1.equals(output2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_SameId() {
        final Id same = new Id();
        final OutputClauseUnaryTests output1 = new OutputClauseUnaryTests(same, null, null);
        final OutputClauseUnaryTests output2 = new OutputClauseUnaryTests(same, null, null);

        assertTrue(output1.equals(output2, false));
    }

    @Test
    public void testEqualsIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final OutputClauseUnaryTests output1 = new OutputClauseUnaryTests(id1, null, null);
        final OutputClauseUnaryTests output2 = new OutputClauseUnaryTests(id2, null, null);

        assertTrue(output1.equals(output2, true));
    }

    @Test
    public void testEqualsIgnoringId_SameId() {
        final Id same = new Id();
        final OutputClauseUnaryTests output1 = new OutputClauseUnaryTests(same, null, null);
        final OutputClauseUnaryTests output2 = new OutputClauseUnaryTests(same, null, null);

        assertTrue(output1.equals(output2, true));
    }
}
