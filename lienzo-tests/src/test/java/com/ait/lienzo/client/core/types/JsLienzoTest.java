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
package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.shape.wires.types.JsWiresShape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class JsLienzoTest {

    @Mock
    private JsLienzo jsLienzo;

    @Mock
    private JsWiresShape jsWiresShape;

    @Test
    public void testGetBackgroundColor() {
        when(jsLienzo.getWiresShape(anyString())).thenReturn(jsWiresShape);
        when(jsWiresShape.getBackgroundColor()).thenReturn("blue");
        doCallRealMethod().when(jsLienzo).getBackgroundColor(any());

        final String backgroundColor = jsLienzo.getBackgroundColor("someID");
        assertEquals("blue", backgroundColor);
    }

    @Test
    public void testSetBackgroundColor() {
        when(jsLienzo.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsLienzo).setBackgroundColor(any(), any());

        jsLienzo.setBackgroundColor("someID", "green");
        verify(jsWiresShape).setBackgroundColor("green");
    }

    @Test
    public void testGetBorderColor() {
        when(jsLienzo.getWiresShape(anyString())).thenReturn(jsWiresShape);
        when(jsWiresShape.getBorderColor()).thenReturn("red");
        doCallRealMethod().when(jsLienzo).getBorderColor(any());

        final String borderColor = jsLienzo.getBorderColor("someID");
        assertEquals("red", borderColor);
    }

    @Test
    public void testSetBorderColor() {
        when(jsLienzo.getWiresShape(anyString())).thenReturn(jsWiresShape);
        doCallRealMethod().when(jsLienzo).setBorderColor(any(), any());

        jsLienzo.setBorderColor("someID", "black");
        verify(jsWiresShape).setBorderColor("black");
    }

    @Test
    public void testGetBoundingBox() {
        when(jsLienzo.getWiresShape(anyString())).thenReturn(jsWiresShape);
        BoundingBox box = BoundingBox.fromDoubles(100.0, 100.0, 200.0, 200.0);
        when(jsWiresShape.getBounds()).thenReturn(box);
        doCallRealMethod().when(jsLienzo).getBoundingBox(any());

        BoundingBox box2 = jsLienzo.getBoundingBox("someID");
        assertEquals(box.getMinX(), box2.getMinX(), 0);
        assertEquals(box.getMinY(), box2.getMinY(), 0);
        assertEquals(box.getMaxX(), box2.getMaxX(), 0);
        assertEquals(box.getMaxY(), box2.getMaxY(), 0);
    }

    @Test
    public void testGetAbsoluteBoundingBox() {
        when(jsLienzo.getWiresShape(anyString())).thenReturn(jsWiresShape);
        BoundingBox box = BoundingBox.fromDoubles(100.0, 100.0, 200.0, 200.0);
        when(jsWiresShape.getBounds()).thenReturn(box);
        doCallRealMethod().when(jsLienzo).getBoundingBox(any());

        BoundingBox box2 = jsLienzo.getBoundingBox("someID");
        assertEquals(box.getMinX(), box2.getMinX(), 0);
        assertEquals(box.getMinY(), box2.getMinY(), 0);
        assertEquals(box.getMaxX(), box2.getMaxX(), 0);
        assertEquals(box.getMaxY(), box2.getMaxY(), 0);
    }
}
