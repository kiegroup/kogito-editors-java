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
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import elemental2.core.JsArray;
import jsinterop.annotations.JsType;

@JsType
public class JsWiresShape {

    protected WiresShape shape;

    public JsWiresShape(WiresShape shape) {
        this.shape = shape;
    }

    public String getID() {
        return shape.getID();
    }

    // Task         Index 0 Multipath
    // Background   Index 1 Shape
    // Border       Index 2 Shape

    // DataObject   Index 0 Multipath
    // Background   Index 1 Group           Multipath, Multipath
    // Border       Index 2 Shape

    // Text Annotation Index 0 Multipath **** still need to change the bracket DONE
    // Background   Index 1 Group           Multipath, Group -> [Multipath, Group -> Group -> Rectangle]
    // Border       Index 2 Text

    // Event        Index 0 Multipath
    // Background   Index 1 Group           ->  Index 0 Multipath, Index 1 Group with 1 children Index 0 Group Index 0 Multipath
    // Border       Index 2 Text

    //s.getChild(1).getChildrenAt(1).getChildrenAt(0).getChildrenAt(0) Border Task
    // s.getChild(1).getChildrenAt(0).fillColor = "blue"; Background Event

    // Lane         Index 0 Multipath
    // Background   Index 1 Rectangle
    // Border       Index 2 Rectangle
    // Border       Index 3 Text

    // Gateway        Index 0 Multipath
    // Background   Index 1 Group           -> Miltipath, Group, Group
    // Border       Index 2 Text

/// Lane looks like not changed

    //// Task
    public String getBackgroundColorTask() {
        return ((Shape) this.getChild(1)).getFillColor();
    }

    public void setBackgroundColorTask(String backgroundColor) {
        ((Shape) this.getChild(1)).setFillColor(backgroundColor);
    }

    public String getBorderColorTask() {
        return ((Shape) this.getChild(2)).getStrokeColor();
    }

    public void setBorderColorTask(String borderColor) {
        ((Shape) this.getChild(2)).setStrokeColor(borderColor);
    }

    //// DataObject
    public String getBackgroundColorDataObject() {
        return ((MultiPath) (((Group) this.getChild(1))).getChildrenAt(0)).getFillColor();
    }

    public void setBackgroundColorDataObject(String backgroundColor) {
        ((MultiPath) ((Group) this.getChild(1)).getChildrenAt(0)).setFillColor(backgroundColor);
    }

    public String getBorderColorDataObject() {
        return ((MultiPath) (((Group) this.getChild(1))).getChildrenAt(0)).getStrokeColor();
    }

    public void setBorderColorDataObject(String borderColor) {
        ((MultiPath) (((Group) this.getChild(1))).getChildrenAt(0)).setStrokeColor(borderColor);
    }

    //// Text Annotation
    public String getBackgroundColorTextAnnotation() {
        return ((MultiPath) this.getChild(0)).getFillColor();
    }

    public void setBackgroundColorTextAnnotation(String backgroundColor) {
        ((MultiPath) this.getChild(0)).setFillColor(backgroundColor);
    }

    public String getBorderColorTextAnnotation() {
        return ((Text) this.getChild(2)).getFillColor();
    }

    public void setBorderColorTextAnnotation(String borderColor) {
        ((Text) this.getChild(2)).setFillColor(borderColor);
        ((MultiPath) ((Group) this.getChild(1)).getChildrenAt(0)).setStrokeColor(borderColor);
    }

    ///// Lane
    public String getBackgroundColorLane() {
        return ((Rectangle) this.getChild(1)).getFillColor();
    }

    public void setBackgroundColorLane(String backgroundColor) {
        ((Rectangle) this.getChild(1)).setFillColor(backgroundColor);
    }

    public String getBorderColorLane() {
        return ((Rectangle) this.getChild(2)).getStrokeColor();
    }

    public void setBorderColorLane(String borderColor) {
        ((Rectangle) this.getChild(1)).setStrokeColor(borderColor);
        ((Rectangle) this.getChild(2)).setStrokeColor(borderColor);
    }

    ///// Event
    public String getBackgroundColorEvent() {
        return ((MultiPath) ((Group) this.getChild(1)).getChildrenAt(0)).getFillColor();
    }

    public void setBackgroundColorEvent(String backgroundColor) {
        ((MultiPath) ((Group) this.getChild(1)).getChildrenAt(0)).setFillColor(backgroundColor);
    }

    public String getBorderColorEvent() {
        return ((MultiPath) ((Group) ((Group) ((Group) this.getChild(1)).getChildrenAt(1)).getChildrenAt(0)).getChildrenAt(0)).getFillColor();
    }

    public void setBorderColorEvent(String borderColor) {
        ((MultiPath) ((Group) ((Group) ((Group) this.getChild(1)).getChildrenAt(1)).getChildrenAt(0)).getChildrenAt(0)).setFillColor(borderColor);
    }

    ///// Gateway
    public String getBackgroundColorGateway() {
        return ((MultiPath) ((Group) this.getChild(1)).getChildrenAt(0)).getFillColor();
    }

    public void setBackgroundColorGateway(String backgroundColor) {
        ((MultiPath) ((Group) this.getChild(1)).getChildrenAt(0)).setFillColor(backgroundColor);
    }

    public String getBorderColorGateway() {
        return ((MultiPath) ((Group) ((Group) ((Group) this.getChild(1)).getChildrenAt(1)).getChildrenAt(0)).getChildrenAt(0)).getFillColor();
    }

    public void setBorderColorGateway(String borderColor) {
        ((MultiPath) ((Group) ((Group) ((Group) this.getChild(1)).getChildrenAt(1)).getChildrenAt(0)).getChildrenAt(0)).setFillColor(borderColor);
    }



    /*

    // Create badge
    var jsl = window.jsLienzo;
    var badge = new com.ait.lienzo.client.core.shape.Group();
    badge.listening = false;
    badge.alpha = 0;
    var text = new com.ait.lienzo.client.core.shape.Text("100", "arial", "italic", 12);
    badge.add(text);
    var bb = text.getBoundingBox();
    var decorator = new com.ait.lienzo.client.core.shape.Rectangle(bb.getWidth() + 10, bb.getHeight() + 10);
    decorator.x = bb.getX() - 5;
    decorator.y = bb.getY() - 5;
    decorator.fillAlpha = 0;
    decorator.strokeAlpha = 1;
    decorator.strokeColor = 'red';
    decorator.strokeWitrh = 2;
    decorator.cornerRadius = 5;
    badge.add(decorator);
    badge.x = 100;
    badge.y = 100;
    jsl.add(badge);
    jsl.animations().alpha(badge, 1, 1500);

     */

    public Group addBadge(String badgeString, String x, String y) {
        final Group badge = new Group();
        badge.setListening(false);
        badge.setAlpha(0);
        final Text text = new Text(badgeString, "arial", 12);
        badge.add(text);
        final BoundingBox bb = text.getBoundingBox();
        Rectangle decorator = new Rectangle(bb.getWidth() + 10, bb.getHeight() + 10);
        decorator.setX(bb.getX() - 5);
        decorator.setY(bb.getY() - 5);
        decorator.setFillAlpha(0);
        decorator.setStrokeColor("black");
        decorator.setStrokeWidth(2);
        decorator.setCornerRadius(5);
        badge.add(decorator);
        badge.setX(Integer.parseInt(x));
        badge.setY(Integer.parseInt(y));
        //add(badge);
        this.shape.addChild(badge);
        //animations().alpha(badge, 1, 1500);
        return badge;
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
}
