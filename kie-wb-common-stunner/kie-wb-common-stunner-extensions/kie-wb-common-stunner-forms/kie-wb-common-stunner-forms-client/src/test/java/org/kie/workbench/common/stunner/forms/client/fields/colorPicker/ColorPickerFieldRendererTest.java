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

package org.kie.workbench.common.stunner.forms.client.fields.colorPicker;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ColorPickerFieldRendererTest {

    @Mock
    private ColorPickerWidget colorPickerMock;

    private ColorPickerFieldRenderer renderer;

    @Before
    public void setUp() throws Exception {
        renderer = new ColorPickerFieldRenderer(colorPickerMock);
    }

    @Test
    public void testReadOnly() {
        renderer.setReadOnly(true);
        verify(colorPickerMock).setReadOnly(true);

        reset(colorPickerMock);

        renderer.setReadOnly(false);
        verify(colorPickerMock).setReadOnly(false);
    }
}
