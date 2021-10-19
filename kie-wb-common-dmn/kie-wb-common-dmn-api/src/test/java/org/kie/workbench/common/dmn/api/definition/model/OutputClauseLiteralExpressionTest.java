/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OutputClauseLiteralExpressionTest {

    private static final String TEXT = "TEXT";
    private static final String CLAUSE_ID = "CLAUSE-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private OutputClauseLiteralExpression outputClauseLiteralExpression;

    @Before
    public void setup() {
        this.outputClauseLiteralExpression = new OutputClauseLiteralExpression();
    }

    @Test
    public void testGetHasTypeRefs() {
        final List<HasTypeRef> actualHasTypeRefs = outputClauseLiteralExpression.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = singletonList(outputClauseLiteralExpression);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testCopy() {
        final OutputClauseLiteralExpression source = new OutputClauseLiteralExpression(
                new Id(CLAUSE_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                null
        );

        final OutputClauseLiteralExpression target = source.copy();

        assertNotNull(target);
        assertNotEquals(CLAUSE_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertEquals(TEXT, target.getText().getValue());
        assertNull(target.getImportedValues());
    }

    @Test
    public void testEqualsNotIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final OutputClauseLiteralExpression outputClause1 = new OutputClauseLiteralExpression(id1,
                                                                                              null,
                                                                                              null,
                                                                                              null,
                                                                                              null);
        final OutputClauseLiteralExpression outputClause2 = new OutputClauseLiteralExpression(id2,
                                                                                              null,
                                                                                              null,
                                                                                              null,
                                                                                              null);

        assertFalse(outputClause1.equals(outputClause2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_SameId() {
        final Id same = new Id();
        final OutputClauseLiteralExpression outputClause1 = new OutputClauseLiteralExpression(same,
                                                                                              null,
                                                                                              null,
                                                                                              null,
                                                                                              null);
        final OutputClauseLiteralExpression outputClause2 = new OutputClauseLiteralExpression(same,
                                                                                              null,
                                                                                              null,
                                                                                              null,
                                                                                              null);
        final ImportedValues importedValues1 = mock(ImportedValues.class);
        final ImportedValues importedValues2 = mock(ImportedValues.class);

        when(importedValues1.equals(importedValues2, false)).thenReturn(true);

        outputClause1.setImportedValues(importedValues1);

        outputClause2.setImportedValues(importedValues2);

        final boolean result = outputClause1.equals(outputClause2, false);

        assertTrue(result);

        verify(importedValues1, never()).equals(importedValues2, true);
    }

    @Test
    public void testEqualsIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        testEqualsIgnoringId(id1, id2);
    }

    @Test
    public void testEqualsIgnoringId_SameId() {
        final Id same = new Id();
        testEqualsIgnoringId(same, same);
    }

    private void testEqualsIgnoringId(final Id id1, final Id id2) {
        final OutputClauseLiteralExpression outputClause1 = new OutputClauseLiteralExpression(id1,
                                                                                              null,
                                                                                              null,
                                                                                              null,
                                                                                              null);
        final OutputClauseLiteralExpression outputClause2 = new OutputClauseLiteralExpression(id2,
                                                                                              null,
                                                                                              null,
                                                                                              null,
                                                                                              null);
        final ImportedValues importedValues1 = mock(ImportedValues.class);
        final ImportedValues importedValues2 = mock(ImportedValues.class);

        when(importedValues1.equals(importedValues2, true)).thenReturn(true);
        outputClause1.setImportedValues(importedValues1);

        outputClause2.setImportedValues(importedValues2);

        final boolean result = outputClause1.equals(outputClause2, true);

        assertTrue(result);

        verify(importedValues1).equals(importedValues2, true);

        verify(importedValues1, never()).equals(importedValues2, false);
    }
}
