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

package com.ait.lienzo.client.core.shape.wires.types;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import jsinterop.annotations.JsType;

@JsType
public class JsWiresShape {

    public WiresShape shape;

    public String getID() {
        return shape.getID();
    }

    public JsWiresShape getParent() {
        WiresContainer parent = shape.getParent();
        if (null != parent) {
            JsWiresShape wiresParent = new JsWiresShape();
            wiresParent.shape = (WiresShape) parent;
            return wiresParent;
        }
        return null;
    }

    public String getParentID() {
        WiresContainer parent = shape.getParent();
        return null != parent ? parent.getID() : null;
    }

    public Point2D getLocation() {
        return shape.getLocation();
    }

    public Point2D getComputedLocation() {
        return shape.getComputedLocation();
    }

    public int getMagnetsSize() {
        int size = 0;
        MagnetManager.Magnets magnets = shape.getMagnets();
        if (null != magnets) {
            size = magnets.size();
        }
        return size;
    }

    public JsWiresMagnet getMagnet(int index) {
        WiresMagnet magnet = shape.getMagnets().getMagnet(index);
        JsWiresMagnet jsWiresMagnet = new JsWiresMagnet();
        jsWiresMagnet.magnet = magnet;
        return jsWiresMagnet;
    }

    public Group asGroup() {
        return shape.getGroup();
    }
}
