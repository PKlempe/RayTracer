package com.klemenz.Light;

import com.klemenz.Utility.Vec3;
import org.jdom2.Element;

public class PointLight extends Light {
    private final Vec3 position;

    public PointLight(Element lightValues) {
        super(lightValues.getChild("color").getAttributes());

        this.position = new Vec3(Double.parseDouble(lightValues.getChild("position").getAttributes().get(0).getValue()),
                                 Double.parseDouble(lightValues.getChild("position").getAttributes().get(1).getValue()),
                                 Double.parseDouble(lightValues.getChild("position").getAttributes().get(2).getValue()));
    }

    public Vec3 getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("# Point Light\n" +
                             "color: %s\n" +
                             "position: %s",
                             color,
                             position);
    }
}
