package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.CustomEvent;
import elemental2.dom.Event;

public class LienzoPanelPrimitiveDragEventDetail extends LienzoPanelEventDetail {

    private final IPrimitive<?> primitive;

    public static LienzoPanelPrimitiveDragEventDetail getDragDetail(Event event) {
        return (LienzoPanelPrimitiveDragEventDetail) ((CustomEvent) event).detail;
    }

    public LienzoPanelPrimitiveDragEventDetail(LienzoPanel panel,
                                               IPrimitive<?> primitive) {
        super(panel);
        this.primitive = primitive;
    }

    public IPrimitive<?> getPrimitive() {
        return primitive;
    }
}

