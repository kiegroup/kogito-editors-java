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
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Attributable;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import elemental2.core.JsArray;
import jsinterop.annotations.JsType;

@JsType
public class JsWiresShape {

    protected WiresShape shape;

    private Attributable attributable;

    public JsWiresShape(WiresShape shape) {
        this.shape = shape;
    }

    public String getID() {
        return shape.getID();
    }

    public JsWiresShape getParent() {
        WiresContainer parent = shape.getParent();
        if (null != parent && !isWiresLayer(parent)) {
            return new JsWiresShape((WiresShape) parent);
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

    public BoundingBox getBoundingBox() {
        return asGroup().getBoundingBox();
    }

    public MultiPath getPath() {
        return shape.getPath();
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
        return new JsWiresMagnet(magnet);
    }

    public IPrimitive<?> getChild(int index) {
        IPrimitive<?> child = null;
        NFastArrayList<IPrimitive<?>> childNodes = shape.getContainer().getChildNodes();
        if (null != childNodes && (index < childNodes.size())) {
            child = childNodes.get(index);
        }
        return child;
    }

    public Shape<?> getShape(int index) {
        return flatShapes().getAt(index);
    }

    public JsArray<Shape> flatShapes() {
        return toFlatShapes(shape.getContainer());
    }

    @SuppressWarnings("all")
    private static JsArray<Shape> toFlatShapes(IContainer container) {
        JsArray<Shape> shapes = new JsArray<Shape>();
        NFastArrayList<IPrimitive<?>> childNodes = container.getChildNodes();
        for (int i = 0; i < childNodes.size(); i++) {
            IPrimitive<?> child = childNodes.get(i);
            if (child instanceof IContainer) {
                JsArray<Shape> children = toFlatShapes((IContainer) child);
                shapes.push(children.asArray(new Shape[children.length]));
            } else {
                shapes.push((Shape) child);
            }
        }
        return shapes;
    }

    public Group asGroup() {
        return shape.getGroup();
    }

    private static boolean isWiresLayer(WiresContainer parent) {
        return parent instanceof WiresLayer;
    }

    public void setBorderColor(String borderColor) {
        final JsArray<Shape> shapeJsArray = flatShapes();

        for (int i = 0; i < shapeJsArray.length; i++) {
            final Shape shape = shapeJsArray.getAt(i);
            final Object userData = shape.getUserData();
            if (userData == null) {
                continue;
            }

            String tag = (String) userData;

            switch (tag) {
                case "?shapeType=BORDER&renderType=STROKE":
                    shape.setStrokeColor(borderColor);
                    draw();
                    break;

                case "?shapeType=BORDER&renderType=FILL":
                    shape.setFillColor(borderColor);
                    draw();
                    break;
            }
        }
    }

    public void draw() {
        shape.refresh();
    }

    public void setBackgroundColor(String backgroundColor) {
        final JsArray<Shape> shapeJsArray = flatShapes();

        for (int i = 0; i < shapeJsArray.length; i++) {
            final Shape shape = shapeJsArray.getAt(i);
            final Object userData = shape.getUserData();
            if (userData == null) {
                continue;
            }

            String tag = (String) userData;
            if (tag.equals("?shapeType=BACKGROUND")) {
                shape.setFillColor(backgroundColor);
                draw();
            }
        }
    }

    // Get Absolute Loction Method
    public BoundingBox getBounds() {
        double shapeX = this.getLocation().getX();
        double shapeY = this.getLocation().getY();
        double width = this.getBoundingBox().getWidth();
        double height = this.getBoundingBox().getHeight();
        final BoundingBox boundingBox = BoundingBox.fromDoubles(shapeX, shapeY, shapeX + width, shapeY + height);
        return boundingBox;
    }

    public void addBadge(String badgeString) {
        if (attributable != null) {
            attributable.addBadge(this.getID(), badgeString);
        }
    }

    public void setAttributable(Attributable attributable) {
        this.attributable = attributable;
    }
}
