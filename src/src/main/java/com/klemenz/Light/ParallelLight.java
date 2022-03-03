package com.klemenz.Light;

import com.klemenz.Utility.Color;
import com.klemenz.Utility.Vec3;
import org.jdom2.Element;

public class ParallelLight extends Light {
    private final Vec3 direction;

    public ParallelLight(Color color, Vec3 direction) {
        super(color);

        this.direction = direction;
    }

    public ParallelLight(Element lightValues) {
        super(lightValues.getChild("color").getAttributes());

        this.direction = new Vec3(Double.parseDouble(lightValues.getChild("direction").getAttributes().get(0).getValue()),
                                  Double.parseDouble(lightValues.getChild("direction").getAttributes().get(1).getValue()),
                                  Double.parseDouble(lightValues.getChild("direction").getAttributes().get(2).getValue()));
    }

    public Vec3 getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("# Parallel Light\n" +
                             "color: %s\n" +
                             "direction: %s",
                             color,
                             direction);
    }
}
