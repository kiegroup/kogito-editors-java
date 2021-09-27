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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.junit.MockitoJUnitRunner;

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
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExpressionTest {

    private static final String LITERAL_ID = "LITERAL-ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String TEXT = "TEXT";
    private static final String EXPRESSION_LANGUAGE = "EXPRESSION-LANGUAGE";
    private LiteralExpression literalExpression;

    @Before
    public void setup() {
        this.literalExpression = new LiteralExpression();
    }

    @Test
    public void testGetHasTypeRefs() {
        final java.util.List<HasTypeRef> actualHasTypeRefs = literalExpression.getHasTypeRefs();
        final java.util.List<HasTypeRef> expectedHasTypeRefs = singletonList(literalExpression);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(literalExpression.getRequiredComponentWidthCount(),
                     literalExpression.getComponentWidths().size());
        literalExpression.getComponentWidths().forEach(Assert::assertNull);
    }

    @Test
    public void testCopy() {
        final LiteralExpression source = new LiteralExpression(
                new Id(LITERAL_ID),
                new Description(DESCRIPTION),
                BuiltInType.BOOLEAN.asQName(),
                new Text(TEXT),
                null,
                new ExpressionLanguage(EXPRESSION_LANGUAGE)
        );

        final LiteralExpression target = source.copy();

        assertNotNull(target);
        assertNotEquals(LITERAL_ID, target.getId().getValue());
        assertEquals(DESCRIPTION, target.getDescription().getValue());
        assertEquals(BuiltInType.BOOLEAN.asQName(), target.getTypeRef());
        assertEquals(TEXT, target.getText().getValue());
        assertNull(target.getImportedValues());
        assertEquals(EXPRESSION_LANGUAGE, target.getExpressionLanguage().getValue());
    }

    @Test
    public void testEqualsNotIgnoringId_DifferentId() {
        final Id id1 = new Id();
        final Id id2 = new Id();
        final LiteralExpression literalExpression1 = new LiteralExpression();
        final LiteralExpression literalExpression2 = new LiteralExpression();

        literalExpression1.setId(id1);
        literalExpression2.setId(id2);

        assertFalse(literalExpression1.equals(literalExpression2, false));
    }

    @Test
    public void testEqualsNotIgnoringId_SameId() {
        final Id same = new Id();
        final LiteralExpression literalExpression1 = new LiteralExpression();
        final LiteralExpression literalExpression2 = new LiteralExpression();
        final ImportedValues importedValues1 = mock(ImportedValues.class);
        final ImportedValues importedValues2 = mock(ImportedValues.class);

        when(importedValues1.equals(importedValues2, false)).thenReturn(true);

        literalExpression1.setId(same);
        literalExpression1.setImportedValues(importedValues1);

        literalExpression2.setId(same);
        literalExpression2.setImportedValues(importedValues2);

        final boolean result = literalExpression1.equals(literalExpression2, false);

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
        final LiteralExpression literalExpression1 = new LiteralExpression();
        final LiteralExpression literalExpression2 = new LiteralExpression();
        final ImportedValues importedValues1 = mock(ImportedValues.class);
        final ImportedValues importedValues2 = mock(ImportedValues.class);

        when(importedValues1.equals(importedValues2, true)).thenReturn(true);

        literalExpression1.setId(id1);
        literalExpression1.setImportedValues(importedValues1);

        literalExpression2.setId(id2);
        literalExpression2.setImportedValues(importedValues2);

        final boolean result = literalExpression1.equals(literalExpression2, true);

        assertTrue(result);

        verify(importedValues1).equals(importedValues2, true);

        verify(importedValues1, never()).equals(importedValues2, false);
    }
}
