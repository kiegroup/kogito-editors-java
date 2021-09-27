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

package org.kie.workbench.common.dmn.api.definition.model.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.HasEqualsIgnoreId;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListComparerHelperTest {

    @Test
    public void testCompare_NotIgnoringId_EqualsList() {

        final List l1 = Collections.emptyList();
        final List l2 = Collections.emptyList();

        final boolean result = ListComparerHelper.compare(l1, l2, false);

        assertTrue(result);
    }

    @Test
    public void testCompare_NotIgnoringId_NotEqualsList() {

        final List l1 = Arrays.asList(mock(HasEqualsIgnoreId.class));
        final List l2 = Arrays.asList(mock(HasEqualsIgnoreId.class));

        final boolean result = ListComparerHelper.compare(l1, l2, false);

        assertFalse(result);
    }

    @Test
    public void testCompare_IgnoringId_DifferentSizeLists() {

        final List list1 = mock(List.class);
        final List list2 = mock(List.class);

        when(list2.size()).thenReturn(1);
        when(list1.size()).thenReturn(2);

        final boolean result = ListComparerHelper.compare(list1, list2, true);

        assertFalse(result);
    }

    @Test
    public void testCompare_IgnoringId_EmptyLists() {

        final List list1 = new ArrayList<HasEqualsIgnoreId>();
        final List list2 = new ArrayList<HasEqualsIgnoreId>();

        final boolean result = ListComparerHelper.compare(list1, list2, true);

        assertTrue(result);
    }

    @Test
    public void testCompare_IgnoringId_EqualsList() {

        final HasEqualsIgnoreId element1 = mock(HasEqualsIgnoreId.class);
        final HasEqualsIgnoreId element2 = mock(HasEqualsIgnoreId.class);
        final HasEqualsIgnoreId element3 = mock(HasEqualsIgnoreId.class);

        final HasEqualsIgnoreId anotherElement1 = mock(HasEqualsIgnoreId.class);
        final HasEqualsIgnoreId anotherElement2 = mock(HasEqualsIgnoreId.class);
        final HasEqualsIgnoreId anotherElement3 = mock(HasEqualsIgnoreId.class);

        final List list1 = Arrays.asList(element1, element2, element3);
        final List list2 = Arrays.asList(anotherElement1, anotherElement2, anotherElement3);

        when(element1.equals(anotherElement1, true)).thenReturn(true);
        when(element2.equals(anotherElement2, true)).thenReturn(true);
        when(element3.equals(anotherElement3, true)).thenReturn(true);

        final boolean result = ListComparerHelper.compare(list1, list2, true);

        assertTrue(result);

        verify(element1).equals(anotherElement1, true);
        verify(element2).equals(anotherElement2, true);
        verify(element3).equals(anotherElement3, true);
    }

    @Test
    public void testCompare_IgnoringId_NotEqualsList() {

        final HasEqualsIgnoreId element1 = mock(HasEqualsIgnoreId.class);
        final HasEqualsIgnoreId element2 = mock(HasEqualsIgnoreId.class);
        final HasEqualsIgnoreId element3 = mock(HasEqualsIgnoreId.class);

        final HasEqualsIgnoreId anotherElement1 = mock(HasEqualsIgnoreId.class);
        final HasEqualsIgnoreId anotherElement2 = mock(HasEqualsIgnoreId.class);
        final HasEqualsIgnoreId anotherElement3 = mock(HasEqualsIgnoreId.class);

        final List list1 = Arrays.asList(element1, element2, element3);
        final List list2 = Arrays.asList(anotherElement1, anotherElement2, anotherElement3);

        when(element1.equals(anotherElement1, true)).thenReturn(true);
        when(element2.equals(anotherElement2, true)).thenReturn(true);
        when(element3.equals(anotherElement3, true)).thenReturn(false);

        final boolean result = ListComparerHelper.compare(list1, list2, true);

        assertFalse(result);

        verify(element1).equals(anotherElement1, true);
        verify(element2).equals(anotherElement2, true);
        verify(element3).equals(anotherElement3, true);
    }
}
