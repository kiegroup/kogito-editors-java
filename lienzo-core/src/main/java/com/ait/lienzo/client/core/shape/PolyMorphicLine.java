/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsProperty;

import static com.ait.lienzo.client.core.shape.OrthogonalLineUtils.addPoint;
import static com.ait.lienzo.shared.core.types.Direction.EAST;
import static com.ait.lienzo.shared.core.types.Direction.NONE;
import static com.ait.lienzo.shared.core.types.Direction.NORTH;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH;
import static com.ait.lienzo.shared.core.types.Direction.WEST;

public class PolyMorphicLine extends AbstractDirectionalMultiPointShape<PolyMorphicLine> {

    private static final double DEFAULT_OFFSET = 10;

    private Point2D m_headOffsetPoint;

    private Point2D m_tailOffsetPoint;

    @JsProperty
    private double cornerRadius;

    private double m_breakDistance;

    private List<Point2D> nonOrthogonalPoints = new ArrayList<Point2D>();
    private List<Point2D> upIndexesToRecalculate = new ArrayList<Point2D>();

    public PolyMorphicLine(final Point2D... points) {
        this(Point2DArray.fromArrayOfPoint2D(points));
    }

    public PolyMorphicLine(final Point2DArray points) {
        super(ShapeType.ORTHOGONAL_POLYLINE);

        setControlPoints(points);
        setHeadDirection(NONE);
        setTailDirection(NONE);
    }

    public PolyMorphicLine(final Point2DArray points, final double corner) {
        this(points);

        setCornerRadius(corner);
    }

    @Override
    public boolean parse() {
        if (0 == points.size()) {
            return false;
        }

        infer();

        inferDirectionChanges();

        if (parsePoints()) {
            calculateNonOrthogonalPoints();
            return true;
        }

        return false;
    }

    private boolean parsePoints() {
        Point2DArray list = points;

        list = list.noAdjacentPoints();
        final int size = list.size();

        if (0 == size) {
            return false;
        }

        final PathPartList path = getPathPartList();
        final double headOffset = getHeadOffset();
        final double tailOffset = getTailOffset();

        if (size > 1) {
            m_headOffsetPoint = Geometry.getProjection(list.get(0), list.get(1), headOffset);
            m_tailOffsetPoint = Geometry.getProjection(list.get(size - 1), list.get(size - 2), tailOffset);

            path.M(m_headOffsetPoint);

            final double corner = getCornerRadius();
            if (corner <= 0) {
                for (int i = 1; i < size - 1; i++) {
                    path.L(list.get(i));
                }

                path.L(m_tailOffsetPoint);
            } else {
                list = list.copy();
                list.set(size - 1, m_tailOffsetPoint);

                Geometry.drawArcJoinedLines(path, list, corner);
            }
        } else if (size == 1) {
            m_headOffsetPoint = list.get(0).copy().offset(headOffset, headOffset);
            m_tailOffsetPoint = list.get(0).copy().offset(tailOffset, tailOffset);

            path.M(m_headOffsetPoint);

            final double corner = getCornerRadius();
            if (corner <= 0) {
                path.L(m_tailOffsetPoint);
            } else {
                list = Point2DArray.fromArrayOfPoint2D(list.get(0).copy(), list.get(0).copy());

                Geometry.drawArcJoinedLines(path, list, corner);
            }
        }

        return true;
    }

    private boolean isHeadDirectionChanged() {
        int size = points.size();
        if (size >= 2) {
            Direction headDirection = getHeadDirection();
            if (null != headDirection && !headDirection.equals(NONE)) {
                Point2D p0 = points.get(0);
                Point2D p1 = points.get(1);
                if (isOrthogonal(p0, p1) &&
                        !nonOrthogonalPoints.contains(p1) &&
                        getDefaultHeadOffset() != 0) {
                    Direction actualHeadDirection = getOrthogonalDirection(p0, p1);
                    if (!headDirection.equals(actualHeadDirection)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isTailDirectionChanged() {
        int size = points.size();
        if (size >= 2) {
            Direction tailDirection = getTailDirection();
            if (null != tailDirection && !tailDirection.equals(NONE)) {
                Point2D pN_1 = points.get(size - 2);
                Point2D pN = points.get(size - 1);
                if (isOrthogonal(pN, pN_1) &&
                        !nonOrthogonalPoints.contains(pN_1) &&
                        getDefaultTailOffset() != 0) {
                    Direction actualTailDirection = getOrthogonalDirection(pN, pN_1);
                    if (!tailDirection.equals(actualTailDirection)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void inferDirectionChanges() {
        if (isHeadDirectionChanged()) {
            DomGlobal.console.log("HEAD DIRECTION CHANGED - REBUILDING POINTS!");
            resetHeadDirectionPoints();
        }
        if (isTailDirectionChanged()) {
            DomGlobal.console.log("TAIL DIRECTION CHANGED - REBUILDING POINTS!");
            resetTailDirectionPoints();
        }
    }

    private void resetHeadDirectionPoints() {
        int size = points.size();
        Point2D p0 = points.get(0);

        int i = 1;
        for (; i < size; i++) {
            Point2D pI = points.get(i);
            if (nonOrthogonalPoints.contains(pI)) {
                break;
            }
        }

        Point2D pI = points.get(i - 1);
        Point2DArray headPoints = inferOrthogonalSegments(p0, pI, getHeadDirection(), getTailDirection(), getDefaultHeadOffset(), getDefaultTailOffset());
        for (; i < size; i++) {
            headPoints.push(points.get(i));
        }
        this.points = correctComputedPoints(headPoints, nonOrthogonalPoints);
    }

    private void resetTailDirectionPoints() {
        int size = points.size();
        Point2D p0 = points.get(size - 1);

        int i = size - 2;
        for (; i >= 0; i--) {
            Point2D pI = points.get(i);
            if (nonOrthogonalPoints.contains(pI)) {
                break;
            }
        }

        Point2D pI = points.get(i + 1);
        Point2DArray tailPoints = inferOrthogonalSegments(pI, p0, getHeadDirection(), getTailDirection(), getDefaultHeadOffset(), getDefaultTailOffset());
        for (; i >= 0; i--) {
            tailPoints.push(points.get(i));
        }
        this.points = correctComputedPoints(tailPoints, nonOrthogonalPoints);
    }

    private void infer() {
        if (!upIndexesToRecalculate.isEmpty()) {
            Point2DArray inferred = inferOrthogonalSegments(getHeadDirection(), getTailDirection(), getDefaultHeadOffset(), getDefaultTailOffset());
            Point2DArray corrected = correctComputedPoints(inferred, nonOrthogonalPoints);
            setPoints(corrected);
            getLayer().batch();
            upIndexesToRecalculate.clear();
        }
    }

    public void calculateNonOrthogonalPoints() {
        if (nonOrthogonalPoints.isEmpty()) {
            int size = points.size();
            if (size > 2) {
                for (int i = 1; i < size - 1; i++) {
                    Point2D lastP = points.get(i - 1);
                    Point2D p = points.get(i);
                    Point2D nextP = points.get(i + 1);
                    if (!isOrthogonal(lastP, p) || !isOrthogonal(p, nextP)) {
                        nonOrthogonalPoints.add(p);
                    }
                }
            }
        }
    }

    @Override
    public int getHeadReferencePointIndex() {
        if (nonOrthogonalPoints.isEmpty()) {
            return -1;
        } else {
            Point2D p = nonOrthogonalPoints.get(nonOrthogonalPoints.size() - 1);
            return indexOfPoint(p);
        }
    }

    @Override
    public int getTailReferencePointIndex() {
        if (nonOrthogonalPoints.isEmpty()) {
            return -1;
        } else {
            Point2D p = nonOrthogonalPoints.get(0);
            return indexOfPoint(p);
        }
    }

    public void setFirstSegmentOrthogonal(boolean orthogonal) {
        if (!orthogonal) {
            nonOrthogonalPoints.add(points.get(1));
        }
    }

    public void setLastSegmentOrthogonal(boolean orthogonal) {
        if (!orthogonal) {
            nonOrthogonalPoints.add(points.get(points.size() - 2));
        }
    }

    @Override
    public PolyMorphicLine refresh() {
        return super.refresh();
    }

    @Override
    public void updatePointAtIndex(int index, double x, double y) {
        boolean isHead = index == 0;
        boolean isTail = index == (points.size() - 1);

        if (!isHead && !isTail) {
            super.updatePointAtIndex(index, x, y);
            return;
        }

        Point2D point = points.get(index);
        double dx = x - point.getX();
        double dy = y - point.getY();
        if (dx == 0 && dy == 0) {
            return;
        }

        if (isHead) {
            propagateUp(index, dx, dy, getDefaultHeadOffset());
        } else {
            propagateDown(index, dx, dy, getDefaultTailOffset());
        }

        Point2DArray corrected = correctComputedPoints(points, nonOrthogonalPoints);
        setPoints(corrected);
        refresh();
    }

    @Override
    public void updatePointCompleted(int index) {
        nonOrthogonalPoints.clear();
        super.updatePointCompleted(index);
    }

    // TODO: Merge with propagateDown
    public void propagateUp(int index, double dx, double dy, double min) {
        if (dx == 0 && dy == 0) {
            return;
        }
        if (index >= (points.size() -1)) {
            return;
        }
        int nextIndex = index + 1;
        Point2D candidate = points.get(index);
        Point2D next = points.get(nextIndex);

        boolean isHorizontal = false;
        boolean isVertical = false;
        if (!nonOrthogonalPoints.contains(next)) {
            isHorizontal = isHorizontal(candidate, next);
            isVertical = isVertical(candidate, next);
            double px = 0;
            double py = 0;

            boolean isNextLast = points.size() > 2 && nextIndex >= (points.size() - 1);
            boolean isFirstOrLast = index < 1 || isNextLast;
            double segmentMin = isFirstOrLast ? min : 0;

            Point2D last = null;
            if (index == 0 || isNextLast) {
                last = points.get(points.size() - 1);
            }

            final double offset = getDefaultHeadOffset() + getDefaultTailOffset();
            if (isHorizontal) {
                px = propagateOrthogonalSegmentUp(candidate.getX(), next.getX(), dx, segmentMin, null != last ? last.getX() - offset : null);
                py = dy;
                dx = isNextLast && px != 0 ? 0 : dx;
                dy = !isNextLast ? dy : 0;
            } else if (isVertical) {
                dx = !isNextLast ? dx : 0;
                px = dx;
                py = propagateOrthogonalSegmentUp(candidate.getY(), next.getY(), dy, segmentMin, null != last ? last.getY() - offset : null);
                dy = isNextLast && py != 0 ? 0 : dy;
            } else {
                // No need to propagate on no orthogonal segments
                px = 0;
                py = 0;
            }

            boolean propagate = px != 0 || py != 0;
            if (propagate) {
                // DomGlobal.console.log("PROPAGATING UP TO [" + nextIndex + "]");
                propagateUp(nextIndex, px, py, min);
            }
        } else {
            // DomGlobal.console.log("NON PROPAGATING [" + (index + 1) + "]");
        }


        if (dx != 0 || dy != 0) {
            // DomGlobal.console.log("SETTING POINT [" + index + "] to [" + (candidate.getX() + dx) + ", " + (candidate.getY() + dy) + "]");
            candidate.setX(candidate.getX() + dx);
            candidate.setY(candidate.getY() + dy);
        }

        if (isHorizontal) {
            if ((dy != 0) && (candidate.getY() != next.getY())) {
                upIndexesToRecalculate.add(candidate);
            }
        }
        if (isVertical) {
            if (dx != 0 && (candidate.getX() != next.getX())) {
                upIndexesToRecalculate.add(candidate);
            }
        }

    }

    // TODO: Merge with propagateOrthogonalSegmentDown
    private double propagateOrthogonalSegmentUp(double candidate, double next, double dist, double min, Double lastValue) {
        double p = 0;
        double ad = Math.abs(next - candidate);
        double cx = candidate + dist;
        double d = next > candidate ? next - cx : cx - next;
        boolean grows = d > Math.abs(ad);

        if (d >= min && !grows) {
            // do not propagate
            p = 0;
        }
        if (d < min && !grows) {
            // propagate?
            p = (cx + (min * (next > candidate ? 1 : -1))) - next;
        }
        if (d < min && grows) {
            // don't propagate, will propagate once d = 0 & grows, if necessary
            p = 0;
        }
        if (d >= min && grows) {
            p = 0;
            // Propagate back
            if (null != lastValue) {
                if (cx < lastValue) {
                    // If last point is: after -> do not propagate
                    p = candidate < lastValue ? 0 : lastValue - candidate;
                } else {
                    // If last point is: before -> propagate?
                    p = dist;
                }
            }
        }
        return p;
    }

    // TODO: Merge with propagateDown
    public void propagateDown(int index, double dx, double dy, double min) {
        if (dx == 0 && dy == 0) {
            return;
        }
        if (index < 1) {
            return;
        }

        int nextIndex = index - 1;
        Point2D candidate = points.get(index);
        Point2D next = points.get(nextIndex);

        boolean isHorizontal = false;
        boolean isVertical = false;
        if (!nonOrthogonalPoints.contains(next)) {
            isHorizontal = isHorizontal(candidate, next);
            isVertical = isVertical(candidate, next);
            double px = 0;
            double py = 0;

            boolean isNextFirst = points.size() > 2 && nextIndex < 1;
            double segmentMin = index >= (points.size() - 1) || isNextFirst ? min : 0;

            Point2D first = null;
            if (points.size() > 2 && (index == (points.size() - 1) || isNextFirst)) {
                first = points.get(0);
            }

            final double offset = getDefaultHeadOffset() + getDefaultTailOffset();
            if (isHorizontal) {
                px = propagateOrthogonalSegmentDown(candidate.getX(), next.getX(), dx, segmentMin, null != first ? first.getX() + offset : null);
                py = dy;
                dx = isNextFirst && px != 0 ? 0 : dx;
                dy = !isNextFirst ? dy : 0;
            } else if (isVertical) {
                dx = !isNextFirst ? dx : 0;
                px = dx;
                py = propagateOrthogonalSegmentDown(candidate.getY(), next.getY(), dy, segmentMin, null != first ? first.getY() + offset : null);
                dy = isNextFirst && py != 0 ? 0 : dy;
            } else {
                // No need to propagate on no orthogonal segments
                px = 0;
                py = 0;
            }

            boolean propagate = px != 0 || py != 0;
            if (propagate) {
                // DomGlobal.console.log("PROPAGATING DOWN TO [" + nextIndex + "]");
                propagateDown(nextIndex, px, py, min);
            }
        }

        if (dx != 0 || dy != 0) {
            // DomGlobal.console.log("SETTING POINT [" + index + "] to [" +  (candidate.getX() + dx) + ", " + (candidate.getY() + dy) + "]");
            candidate.setX(candidate.getX() + dx);
            candidate.setY(candidate.getY() + dy);
        }

        if (isHorizontal) {
            if ((dy != 0) && (candidate.getY() != next.getY())) {
                upIndexesToRecalculate.add(next);
            }
        }
        if (isVertical) {
            if (dx != 0 && (candidate.getX() != next.getX())) {
                upIndexesToRecalculate.add(next);
            }
        }

    }

    // TODO: Merge with propagateOrthogonalSegmentUp
    private double propagateOrthogonalSegmentDown(double candidate, double next, double dist, double min, Double lastValue) {
        double p = 0;
        double ad = Math.abs(next - candidate);
        double cx = candidate + dist;
        double d = next > candidate ? next - cx : cx - next;
        boolean grows = d > Math.abs(ad);

        if (d >= min && !grows) {
            // do not propagate
            p = 0;
        }
        if (d < min && !grows) {
            // propagate?
            p = (cx + (min * (next > candidate ? 1 : -1))) - next;
        }
        if (d < min && grows) {
            // don't propagate, will propagate once d = 0 & grows, if necessary
            p = 0;
        }
        if (d >= min && grows) {
            p = 0;
            // Propagate back
            if (null != lastValue) {
                // TODO: Here comparison differs from propagateOrthogonalSegmentUp
                if (cx > lastValue) {
                    // If last point is: after -> do not propagate
                    p = candidate > lastValue ? 0 : lastValue - candidate;
                } else {
                    // If last point is: before -> propagate?
                    p = dist;
                }
            }
        }
        return p;
    }

    public Point2DArray inferOrthogonalSegments(Direction headDirection, Direction tailDirection, double headOffset, double tailOffset) {
        Point2DArray result = new Point2DArray();
        Point2DArray copy = points;
        int size = copy.size();
        for (int i = 0; i < size; i++) {
            if (upIndexesToRecalculate.contains(copy.get(i)) && (i < size - 1)) {
                boolean isFirstOrLastPoint = (i == 0 || i == (size - 1));
                headOffset = isFirstOrLastPoint ? headOffset : 0;
                tailOffset = isFirstOrLastPoint ? tailOffset : 0;
                Point2DArray inferred = inferOrthogonalSegments(copy, i, headDirection, tailDirection, headOffset, tailOffset);
                DomGlobal.console.log("REBUILDING ORTHOGONAL POINTS FOR INDEX [" + i + "] = [" + inferred + "]");
                for (int j = 0; j < inferred.size(); j++) {
                    Point2D o = inferred.get(j);
                    result.push(o);
                }
                i++;
            } else {
                Point2D point = copy.get(i);
                result.push(point);
            }
        }

        return result;
    }

    public static Point2DArray inferOrthogonalSegments(Point2DArray copy, int index, Direction headDirection, Direction tailDirection, double headOffset, double tailOffset) {
        Point2D p0 = copy.get(index);
        Point2D p1 = copy.get(index + 1);
        return inferOrthogonalSegments(p0, p1, headDirection, tailDirection, headOffset, tailOffset);
    }

    public static Point2DArray inferOrthogonalSegments(Point2D p0, Point2D p1, Direction headDirection, Direction tailDirection, double headOffset, double tailOffset) {
        Point2DArray result = new Point2DArray();
        result.push(p0);
        if (isOrthogonal(p0, p1)) {
            result.push(p1);
            return result;
        }
        Point2DArray ps = new Point2DArray();
        ps.push(p0.copy());
        ps.push(p1.copy());
        NFastDoubleArray p = drawOrthogonalLinePoints(ps, headDirection, tailDirection, 0, headOffset, tailOffset, true);
        Point2DArray array = Point2DArray.fromNFastDoubleArray(p);
        array = correctComputedPoints(array, Collections.<Point2D>emptyList());
        if (array.size() > 2) {
            for (int j = (headOffset != 0 ? 0 : 1); j < array.size(); j++) {
                Point2D op = array.get(j);
                result.push(op);
            }
        }
        if (!p1.equals(result.get(result.size() - 1))) {
            result.push(p1);
        }
        return result;
    }

    private static NFastDoubleArray drawOrthogonalLinePoints(final Point2DArray points,  Direction headDirection, Direction tailDirection,
                                                                   final double correction, double headOffset, double tailOffset, boolean write) {
        final NFastDoubleArray buffer = new NFastDoubleArray();

        Point2D p0 = points.get(0);
        p0 = OrthogonalLineUtils.correctP0(headDirection, correction, headOffset, write, buffer, p0);

        int i = 1;
        Direction direction = headDirection;
        final int size = points.size();
        Point2D p1;

        for (; i < size - 1; i++) {
            p1 = points.get(i);

            if (points.size() > 2 && i > 1) {
                direction = OrthogonalLineUtils.getNextDirection(direction, p0.getX(), p0.getY(), p1.getX(), p1.getY());
                addPoint(buffer, p1.getX(), p1.getY(), write);
            } else {
                direction = OrthogonalLineUtils.drawOrthogonalLineSegment(buffer, direction, null, p0.getX(), p0.getY(), p1.getX(), p1.getY(), write);
            }

            if (null == direction) {
                return null;
            }
            p0 = p1;
        }
        p1 = points.get(size - 1);

        if (points.size() == 2 || (points.size() > 2 && isOrthogonal(p0, p1))) {
            OrthogonalLineUtils.drawTail(points, buffer, direction, tailDirection, p0, p1, correction, headOffset, tailOffset);
        } else {
            addPoint(buffer, p1.getX(), p1.getY(), write);
        }

        return buffer;
    }

    public static Point2DArray correctComputedPoints(Point2DArray points, List<Point2D> nonOrthogonalPoints) {
        Point2DArray result = new Point2DArray();
        if (points.size() == 2) {
            result.push(points.get(0));
            result.push(points.get(1));
        } else if (points.size() > 2) {
            Point2D ref = points.get(0);
            result.push(ref);
            for (int i = 1; i < (points.size() -1); i++) {
                Point2D p0 = points.get(i);
                Point2D p1 = points.get(i + 1);

                boolean write = true;
                if (!nonOrthogonalPoints.contains(p0)) {
                    if (ref.getX() == p0.getX() && p0.getX() == p1.getX()) {
                        write = false;
                    }
                    if (ref.getY() == p0.getY() && p0.getY() == p1.getY()) {
                        write = false;
                    }
                }

                if (write) {
                    result.push(p0);
                }

                if (i == points.size() - 2) {
                    result.push(p1);
                }

                ref = p0;

            }
        }
        return result;
    }

    private static boolean isVertical(Point2D p0, Point2D p1) {
        return p1.getX() == p0.getX();
    }

    private static boolean isHorizontal(Point2D p0, Point2D p1) {
        return p1.getY() == p0.getY();
    }

    private static boolean isOrthogonal(Point2D p0, Point2D p1) {
        return isVertical(p0, p1) || isHorizontal(p0, p1);
    }

    public static Direction getOrthogonalDirection(final Point2D p0, final Point2D p1) {
        if (isHorizontal(p0, p1)) {
            return p0.getX() < p1.getX() ? EAST : WEST;
        }
        if (isVertical(p0, p1)) {
            return p0.getY() < p1.getY() ? SOUTH : NORTH;
        }
        return NONE;
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (getPathPartList().size() < 1) {
            if (!parse()) {
                return BoundingBox.fromDoubles(0, 0, 0, 0);
            }
        }
        return getPathPartList().getBoundingBox();
    }

    @Override
    protected boolean fill(Context2D context, double alpha) {
        return false;
    }

    @Override
    public PolyMorphicLine setHeadDirection(Direction direction) {
        headDirection = direction;
        return this;
    }

    @Override
    public PolyMorphicLine setTailDirection(Direction direction) {
        tailDirection = direction;
        return this;
    }

    @Override
    public PolyMorphicLine setHeadOffset(double offset) {
        this.headOffset = offset;
        return this;
    }

    @Override
    public PolyMorphicLine setTailOffset(double offset) {
        this.tailOffset = offset;
        return this;
    }

    public double getDefaultHeadOffset() {
        return super.getHeadOffset() > 0 ? super.getHeadOffset() : DEFAULT_OFFSET;
    }

    public double getDefaultTailOffset() {
        return super.getTailOffset() > 0 ? super.getTailOffset() : DEFAULT_OFFSET;
    }

    @Override
    public PolyMorphicLine setControlPoints(Point2DArray points) {
        this.points = points;
        return this;
    }

    @Override
    public PolyMorphicLine setPoints(Point2DArray points) {
        this.points = points;
        return this;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public PolyMorphicLine setCornerRadius(final double radius) {
        this.cornerRadius = cornerRadius;

        return refresh();
    }

    public double getBreakDistance() {
        return m_breakDistance;
    }

    public PolyMorphicLine setBreakDistance(double distance) {
        m_breakDistance = distance;

        return refresh();
    }

    @Override
    public PolyMorphicLine setPoint2DArray(final Point2DArray points) {
        return setControlPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray() {
        return getControlPoints();
    }

    @Override
    public boolean isControlPointShape() {
        return true;
    }

    @Override
    public Point2D getHeadOffsetPoint() {
        return m_headOffsetPoint;
    }

    @Override
    public Point2D getTailOffsetPoint() {
        return m_tailOffsetPoint;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return getBoundingBoxAttributesComposed(Attribute.CONTROL_POINTS, Attribute.CORNER_RADIUS);
    }

    @Override
    public Shape<PolyMorphicLine> copyTo(Shape<PolyMorphicLine> other) {
        super.copyTo(other);
        ((PolyMorphicLine) other).m_headOffsetPoint = m_headOffsetPoint.copy();
        ((PolyMorphicLine) other).m_tailOffsetPoint = m_tailOffsetPoint.copy();
        ((PolyMorphicLine) other).m_breakDistance = m_breakDistance;
        ((PolyMorphicLine) other).cornerRadius = cornerRadius;

        return other;
    }

    @Override
    public PolyMorphicLine cloneLine() {
        PolyMorphicLine orthogonalPolyLine = new PolyMorphicLine(this.getControlPoints().copy(), cornerRadius);
        return (PolyMorphicLine) copyTo(orthogonalPolyLine);
    }
}