/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.core.style.Style.OutlineStyle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.ResizeCallback;
import com.ait.lienzo.client.widget.panel.ResizeObserver;
import elemental2.dom.CSSProperties;
import elemental2.dom.Element;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;

import static com.ait.lienzo.client.widget.panel.util.LienzoPanelUtils.createDiv;
import static com.ait.lienzo.client.widget.panel.util.LienzoPanelUtils.setPanelSize;

public class ScrollablePanel extends LienzoBoundsPanel {

    private static final Bounds EMPTY = Bounds.empty();

    private final HTMLDivElement domElementContainer = createDiv();
    private final HTMLDivElement internalScrollPanel = createDiv();
    private final HTMLDivElement scrollPanel = createDiv();
    private final HTMLDivElement rootPanel = createDiv();
    private EventListener mouseDownListener;
    private EventListener mouseUpListener;
    private EventListener mouseOutListener;
    private EventListener scrollListener;
    private EventListener mouseMoveListener;
    private EventListener mouseWheelListener;
    private EventListener primitiveDragStartListener;
    private EventListener primitiveDragUpdateListener;
    private EventListener primitiveDragEndListener;
    private int widePx;
    private int highPx;
    private boolean isMouseDown = false;
    private boolean isScrolling = false;
    private double dragDifferenceX;
    private double dragDifferenceY;
    private ResizeObserver resizeObserver;
    private ResizeCallback m_resizeCallback;
    private static int PADDING_OFFSET = 4;

    public static ScrollablePanel newPanel(final BoundsProvider layerBoundsProvider) {
        return new ScrollablePanel(layerBoundsProvider);
    }

    protected ScrollablePanel(final BoundsProvider layerBoundsProvider) {
        this(LienzoFixedPanel.newPanel(),
             layerBoundsProvider);
    }

    public ScrollablePanel(final LienzoPanel lienzoPanel,
                           final BoundsProvider layerBoundsProvider) {
        super(lienzoPanel,
              layerBoundsProvider);
        setupPanels();
    }

    @Override
    public LienzoBoundsPanel set(final Layer layer) {
        super.set(layer);
        scrollPanel.style.position = Style.Position.RELATIVE.getCssName();
        scrollPanel.style.overflow = Style.Overflow.SCROLL.getCssName();
        internalScrollPanel.style.position = Style.Position.ABSOLUTE.getCssName();
        domElementContainer.style.position = Style.Position.ABSOLUTE.getCssName();
        domElementContainer.style.zIndex = CSSProperties.ZIndexUnionType.of(1);
        initViewport();
        return this;
    }

    @Override
    public LienzoFixedPanel getLienzoPanel() {
        return (LienzoFixedPanel) super.getLienzoPanel();
    }

    @Override
    public Bounds getDefaultBounds() {
        return getVisibleBounds();
    }

    public Bounds getVisibleBounds() {
        if (null != getViewport()) {
            final Viewport viewport = getViewport();
            Transform transform = viewport.getTransform();
            if (transform == null) {
                viewport.setTransform(transform = new Transform());
            }
            final double x = -transform.getTranslateX() / transform.getScaleX();
            final double y = -transform.getTranslateY() / transform.getScaleY();
            final Bounds bounds = Bounds.empty();
            bounds.setX(x);
            bounds.setY(y);
            bounds.setHeight(Math.max(0, viewport.getHeight() / transform.getScaleX()));
            bounds.setWidth(Math.max(0, viewport.getWidth() / transform.getScaleY()));
            return bounds;
        }
        return EMPTY;
    }

    public EventListener addBoundsChangedEventListener(EventListener eventListener) {
        LienzoPanelEvents.addBoundsChangedEventListener(this, eventListener);
        return eventListener;
    }

    public void removeBoundsChangedEventListener(EventListener eventListener) {
        LienzoPanelEvents.removeBoundsChangedEventListener(this, eventListener);
    }

    private void fireBoundsChangedEvent() {
        LienzoPanelEvents.fireBoundsChangedEvent(this);
    }

    public EventListener addResizeEventListener(EventListener eventListener) {
        LienzoPanelEvents.addResizeEventListener(this, eventListener);
        return eventListener;
    }

    public void removeResizeEventListener(EventListener eventListener) {
        LienzoPanelEvents.removeResizeEventListener(this, eventListener);
    }

    private void fireResizeChangedEvent() {
        LienzoPanelEvents.fireResizeEvent(this);
    }

    public EventListener addScaleEventListener(EventListener eventListener) {
        LienzoPanelEvents.addScaleEventListener(this, eventListener);
        return eventListener;
    }

    public void removeScaleEventListener(EventListener eventListener) {
        LienzoPanelEvents.removeScaleEventListener(this, eventListener);
    }

    private void fireScaleEvent() {
        LienzoPanelEvents.fireScaleEvent(this);
    }

    public EventListener addScrollEventListener(EventListener eventListener) {
        LienzoPanelEvents.addScrollEventListener(this, eventListener);
        return eventListener;
    }

    public void removeScrollEventListener(EventListener eventListener) {
        LienzoPanelEvents.removeScrollEventListener(this, eventListener);
    }

    private void fireScrollEvent(double px,
                                 double py) {
        LienzoPanelEvents.fireScrollEvent(this, px, py);
    }

    private void fitToParentSize() {
        HTMLDivElement parent = (HTMLDivElement) rootPanel.parentNode.parentNode;
        int offsetWidth = parent.offsetWidth;
        int offsetHeight = parent.offsetHeight;
        if (offsetWidth > 0 && offsetHeight > 0) {
            setPxSize(offsetWidth, offsetHeight);
        }
    }

    private void setPxSize(final int widePx,
                           final int highPx) {
        this.widePx = widePx;
        this.highPx = highPx;
        updatePanelsSizes(widePx, highPx);
        fireResizeChangedEvent();
    }

    @Override
    public HTMLDivElement getElement() {
        return rootPanel;
    }

    @Override
    public int getWidePx() {
        return widePx;
    }

    @Override
    public int getHighPx() {
        return highPx;
    }

    @Override
    public LienzoBoundsPanel onRefresh() {
        synchronizeScrollSize();
        refreshScrollPosition();
        batch();
        return this;
    }

    @Override
    public void onResize() {
        super.onResize();
        initResizeObserver();
    }

    @Override
    protected void doDestroy() {
        removeHandlers();
        resizeObserver.disconnect();
        resizeObserver = null;
        rootPanel.remove();
        isMouseDown = false;
    }

    private void setupPanels() {
        // DOM tree.
        scrollPanel.appendChild(internalScrollPanel);
        domElementContainer.appendChild(getLienzoPanel().getElement());
        rootPanel.appendChild(domElementContainer);
        rootPanel.appendChild(scrollPanel);
        rootPanel.style.outlineStyle = OutlineStyle.NONE.getCssName();

        // Event listeners.
        mouseDownListener = e -> ScrollablePanel.this.onStart();
        mouseUpListener = e -> ScrollablePanel.this.onComplete();
        mouseOutListener = e -> ScrollablePanel.this.onComplete();
        scrollListener = e -> ScrollablePanel.this.onScroll();
        mouseMoveListener = e -> ScrollablePanel.this.enablePointerEvents();
        mouseWheelListener = e -> ScrollablePanel.this.disablePointerEvents();
        primitiveDragStartListener = e -> ScrollablePanel.this.onPrimitiveDragStart(
                LienzoPanelPrimitiveDragEventDetail.getDragDetail(e).getPrimitive());
        primitiveDragUpdateListener = e -> ScrollablePanel.this.onPrimitiveDragUpdate(
                LienzoPanelPrimitiveDragEventDetail.getDragDetail(e).getPrimitive());
        primitiveDragEndListener = e -> ScrollablePanel.this.onPrimitiveDragEnd(
                LienzoPanelPrimitiveDragEventDetail.getDragDetail(e).getPrimitive());

        // Attach event listeners.
        rootPanel.addEventListener("mousedown", mouseDownListener);
        rootPanel.addEventListener("mouseup", mouseUpListener);
        rootPanel.addEventListener("mouseout", mouseOutListener);
        rootPanel.addEventListener("mousemove", mouseMoveListener);
        domElementContainer.addEventListener("mousewheel", mouseWheelListener);
        scrollPanel.addEventListener("scroll", scrollListener);
        LienzoPanelEvents.addPrimitiveDragStartEventListener(getLienzoPanel(), primitiveDragStartListener);
        LienzoPanelEvents.addPrimitiveDragUpdateEventListener(getLienzoPanel(), primitiveDragUpdateListener);
        LienzoPanelEvents.addPrimitiveDragEndEventListener(getLienzoPanel(), primitiveDragEndListener);

        // ResizeObserver callback.
        m_resizeCallback = e -> {
            if (isContainerStillOpened()) {
                onScroll();
                ScrollablePanel.this.fitToParentSize();
                ScrollablePanel.this.refresh();
            }
        };
    }

    private boolean isContainerStillOpened() {
        return this.getElement().parentNode != null && this.getElement().parentNode.parentNode != null;
    }

    private void initResizeObserver() {
        if (null == resizeObserver && isContainerStillOpened()) {
            resizeObserver = new ResizeObserver(m_resizeCallback);
            resizeObserver.observe((Element) this.getElement().parentNode.parentNode);
        }
    }

    private void initViewport() {
        getViewport().addViewportTransformChangedHandler(event -> onViewportTransformChanged());
    }

    private void removeHandlers() {
        rootPanel.removeEventListener("mousedown", mouseDownListener);
        rootPanel.removeEventListener("mouseup", mouseUpListener);
        rootPanel.removeEventListener("mouseout", mouseOutListener);
        rootPanel.removeEventListener("mousemove", mouseMoveListener);
        domElementContainer.removeEventListener("mousewheel", mouseWheelListener);
        scrollPanel.removeEventListener("scroll", scrollListener);
        LienzoPanelEvents.removePrimitiveDragStartEventListener(getLienzoPanel(), primitiveDragStartListener);
        LienzoPanelEvents.removePrimitiveDragUpdateEventListener(getLienzoPanel(), primitiveDragUpdateListener);
        LienzoPanelEvents.removePrimitiveDragEndEventListener(getLienzoPanel(), primitiveDragEndListener);
    }

    private void enablePointerEvents() {
        domElementContainer.style.pointerEvents = "initial";
    }

    private void disablePointerEvents() {
        domElementContainer.style.pointerEvents = "none";
    }

    private void onStart() {
        isMouseDown = true;
        rootPanel.focus();
    }

    private void onComplete() {
        if (isMouseDown) {
            isMouseDown = false;
            refresh();
        }
    }

    private void onScroll() {
        isScrolling = true;
        // Prevent DOMElements scrolling into view when they receive the focus
        domElementContainer.scrollTop = 0;
        domElementContainer.scrollLeft = 0;
        if (null != getLayer()) {
            // If some layer is attached, apply the right translation given from scroll state
            final double sh = getHorizontalScrollRate();
            final double sv = getVerticalScrollRate();
            applyScrollRateToLayer(sh, sv);
        }
        isScrolling = false;
    }

    private void onPrimitiveDragStart(IPrimitive<?> primitive) {
        final BoundingBox primitiveBoundingBox = new BoundingBox();
        for (Point2D point2D : primitive.getComputedBoundingPoints()) {
            primitiveBoundingBox.add(point2D.getX(), point2D.getY());
        }

        final BoundingBox visibleBoundingBox = new BoundingBox();
        final Bounds visibleBounds = getVisibleBounds();
        visibleBoundingBox.add(visibleBounds.getX(), visibleBounds.getY());
        visibleBoundingBox.add(visibleBounds.getX() + visibleBounds.getWidth(), visibleBounds.getY() + visibleBounds.getHeight());

        double padding = getBoundsProvider().getPadding();

        double primMinX = primitiveBoundingBox.getMinX();
        double primMaxX = primitiveBoundingBox.getMaxX();

        double visMinX = visibleBoundingBox.getMinX();
        double visMaxX = visibleBoundingBox.getMaxX();

        if (primMinX - padding < visMinX) {
            dragDifferenceX = visMinX - primMinX;
        } else if (primMaxX + padding > visMaxX) {
            dragDifferenceX = visMaxX - primMaxX;
        }
    }

    private void onPrimitiveDragUpdate(IPrimitive<?> primitive) {
        final BoundingBox primitiveBoundingBox = new BoundingBox();
        for (Point2D point2D : primitive.getComputedBoundingPoints()) {
            primitiveBoundingBox.add(point2D.getX(), point2D.getY());
        }

        final BoundingBox visibleBoundingBox = new BoundingBox();
        final Bounds visibleBounds = getVisibleBounds();
        visibleBoundingBox.add(visibleBounds.getX(), visibleBounds.getY());
        visibleBoundingBox.add(visibleBounds.getX() + visibleBounds.getWidth(), visibleBounds.getY() + visibleBounds.getHeight());

        double primMinX = primitiveBoundingBox.getMinX();
        double primMaxX = primitiveBoundingBox.getMaxX();
        double primMinY = primitiveBoundingBox.getMinY();
        double primMaxY = primitiveBoundingBox.getMaxY();

        double visMinX = visibleBoundingBox.getMinX();
        double visMaxX = visibleBoundingBox.getMaxX();
        double visMinY = visibleBoundingBox.getMinY();
        double visMaxY = visibleBoundingBox.getMaxY();

        double differenceX = 0;
        double differenceY = 0;
        double padding = getBoundsProvider().getPadding();

        if (primMinX - padding < visMinX) {
            differenceX = visMinX - primMinX;
            double dir = differenceX - dragDifferenceX;
            if (dir > 0) {
                double difference = visMinX - primMinX + padding;
                getViewport().setTransform(getTransform().translate(difference, 0));
            }
        } else if (primMaxX + padding > visMaxX) {
            differenceX = visMaxX - primMaxX;
            double dir = differenceX - dragDifferenceX;
            if (dir < 0) {
                double difference = visMaxX - primMaxX - padding;
                getViewport().setTransform(getTransform().translate(difference, 0));
            }
        }

        if (primMinY - padding < visMinY) {
            differenceY = visMinY - primMinY;
            double dir = differenceY - dragDifferenceY;
            if (dir > 0) {
                double difference = visMinY - primMinY + padding;
                getViewport().setTransform(getTransform().translate(0, difference));
            }
        } else if (primMaxY + padding > visMaxY) {
            differenceY = visMaxY - primMaxY;
            double dir = differenceY - dragDifferenceY;
            if (dir < 0) {
                double difference = visMaxY - primMaxY - padding;
                getViewport().setTransform(getTransform().translate(0, difference));
            }
        }

        dragDifferenceX = differenceX;
        dragDifferenceY = differenceY;

        refresh();
    }

    private void onPrimitiveDragEnd(IPrimitive<?> primitive) {
        dragDifferenceX = Double.NaN;
        dragDifferenceY = Double.NaN;
    }

    private void onViewportTransformChanged() {
        if (!isScrolling) {
            refresh();
        }
    }

    private void synchronizeScrollSize() {
        final double width = calculateInternalScrollPanelWidth();
        final double height = calculateInternalScrollPanelHeight();
        setPanelSize(internalScrollPanel, (int) width, (int) height);
        fireBoundsChangedEvent();
    }

    private void refreshScrollPosition() {
        final double rx = currentRelativeX();
        final double ry = currentRelativeY();
        setHorizontalScrollRate(rx);
        setVerticalScrollRate(ry);
    }

    private double calculateInternalScrollPanelWidth() {
        final double absWidth = maxBoundX() - minBoundX();
        if (getViewport() != null && deltaX() != 0) {
            final double scaleX = getViewport().getTransform().getScaleX();
            final double width = absWidth * scaleX;
            return width;
        }
        return 1;
    }

    private double calculateInternalScrollPanelHeight() {
        final double absHeight = maxBoundY() - minBoundY();
        if (getViewport() != null && deltaY() != 0) {
            final double scaleY = getViewport().getTransform().getScaleY();
            final double height = absHeight * scaleY;
            return height;
        }
        return 1;
    }

    public double getHorizontalScrollRate() {
        final double scrollLeft = scrollPanel.scrollLeft;
        final int scrollWidth = scrollPanel.scrollWidth;
        final int clientWidth = scrollPanel.clientWidth;
        final int level = scrollWidth - clientWidth;
        return level == 0 ? 0d : 100d * scrollLeft / level;
    }

    private void setHorizontalScrollRate(final double rx) {
        final int scrollWidth = scrollPanel.scrollWidth;
        final int clientWidth = scrollPanel.clientWidth;
        final int max = scrollWidth - clientWidth;
        final double sleft = (max * rx) / 100;
        scrollPanel.scrollLeft = sleft;
    }

    public double getVerticalScrollRate() {
        final double scrollTop = scrollPanel.scrollTop;
        final int scrollHeight = scrollPanel.scrollHeight;
        final int clientHeight = scrollPanel.clientHeight;
        final int level = scrollHeight - clientHeight;
        return level == 0 ? 0d : 100d * scrollTop / level;
    }

    private void setVerticalScrollRate(final double ry) {

        final int scrollHeight = scrollPanel.scrollHeight;
        final int clientHeight = scrollPanel.clientHeight;
        final int max = scrollHeight - clientHeight;
        final double stop = (max * ry) / 100;
        scrollPanel.scrollTop = stop;
    }

    private void updatePanelsSizes(final int widePx,
                                   final int highPx) {
        // Adjust high to avoid horizontal scrollbar overlap
        final int highPxFixed = highPx - PADDING_OFFSET;

        setPanelSize(scrollPanel, widePx, highPxFixed);
        final int w = widePx - scrollbarWidth();
        final int h = highPxFixed - scrollbarHeight();
        setPanelSize(domElementContainer, w, h);
        getLienzoPanel().setPixelSize(w, h);
    }

    private int scrollbarWidth() {
        return scrollPanel.offsetWidth - scrollPanel.clientWidth;
    }

    private int scrollbarHeight() {
        return scrollPanel.offsetHeight - scrollPanel.clientHeight;
    }

    public void applyScrollRateToLayer(final double px,
                                       final double py) {
        final double cx = currentPositionX(px);
        final double cy = currentPositionY(py);
        final Transform oldTransform = getViewport().getTransform();
        final double dx = cx - (oldTransform.getTranslateX() / oldTransform.getScaleX());
        final double dy = cy - (oldTransform.getTranslateY() / oldTransform.getScaleY());
        final Transform newTransform = oldTransform.copy().translate(dx, dy);
        getViewport().setTransform(newTransform);
        getLayer().batch();
        fireScrollEvent(px, py);
    }

    // -- Scroll Bounds --
    // -----------------------

    private double maxBoundX() {
        return maxBoundX(getBounds());
    }

    private double maxBoundY() {
        return maxBoundY(getBounds());
    }

    private double minBoundX() {
        return minBoundX(getBounds());
    }

    private double minBoundY() {
        return minBoundY(getBounds());
    }

    // -- Scroll Position--
    // -----------------------

    private double currentRelativeX() {

        final double delta = deltaX();

        return delta == 0d ? 0d : 100d * currentX() / delta;
    }

    private double currentRelativeY() {

        final double delta = deltaY();

        return delta == 0d ? 0d : 100d * currentY() / delta;
    }

    private double currentPositionX(final Double level) {

        final double position = deltaX() * level / 100d;

        return -(minBoundX() + position);
    }

    private double currentPositionY(final Double level) {

        final double position = deltaY() * level / 100d;

        return -(minBoundY() + position);
    }

    private double deltaX() {
        return maxBoundX() - minBoundX() - getVisibleBounds().getWidth();
    }

    private double deltaY() {
        return maxBoundY() - minBoundY() - getVisibleBounds().getHeight();
    }

    private double currentX() {
        return -(getTransform().getTranslateX() / getTransform().getScaleX() + minBoundX());
    }

    private double currentY() {
        return -(getTransform().getTranslateY() / getTransform().getScaleY() + minBoundY());
    }

    private static double maxBoundX(Bounds bounds) {
        final double value = bounds.getX() + bounds.getWidth();
        return max(value);
    }

    private static double maxBoundY(Bounds bounds) {
        final double value = bounds.getY() + bounds.getHeight();
        return max(value);
    }

    private static double minBoundX(Bounds bounds) {
        final double value = bounds.getX();
        return min(value);
    }

    private static double minBoundY(Bounds bounds) {
        final double value = bounds.getY();
        return min(value);
    }

    private static double max(double value) {
        return value >= 0 ? value : 0d;
    }

    private static double min(double value) {
        return value > 0 ? 0d : value;
    }

    public HTMLDivElement getDomElementContainer() {
        return domElementContainer;
    }
}