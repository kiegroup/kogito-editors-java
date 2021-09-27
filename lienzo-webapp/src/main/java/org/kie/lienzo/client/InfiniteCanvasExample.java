package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;

public class InfiniteCanvasExample extends BaseExample implements Example {

    public InfiniteCanvasExample(final String title) {
        super(title);
    }

    @Override
    public void run() {
        Rectangle rectangle1 = new Rectangle(100, 100)
                .setX(0)
                .setY(0)
                .setFillColor(ColorName.LIGHTGREY)
                .setStrokeColor(ColorName.BLACK)
                .setStrokeWidth(1.5)
                .setDraggable(true);

        layer.add(rectangle1);

        Rectangle rectangle2 = new Rectangle(100, 100)
                .setX(400)
                .setY(400)
                .setFillColor(ColorName.RED)
                .setStrokeColor(ColorName.BLACK)
                .setStrokeWidth(1.5)
                .setDraggable(true);

        layer.add(rectangle2);

        Circle circle1 = new Circle(50)
                .setX(50)
                .setY(50)
                .setFillColor(ColorName.LIGHTBLUE)
                .setStrokeColor(ColorName.DARKBLUE)
                .setStrokeWidth(1.5)
                .setDraggable(true);

        layer.add(circle1);

        layer.draw();
    }
}
