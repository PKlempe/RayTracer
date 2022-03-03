package com.klemenz.Light;

import com.klemenz.Utility.Vec3;
import org.jdom2.Element;

public class SpotLight extends Light {
    private final Vec3 position;
    private final Vec3 direction;
    private final int falloffAlpha1;
    private final int falloffAlpha2;

    public SpotLight(Element lightValues) {
        super(lightValues.getChild("color").getAttributes());

        this.falloffAlpha1 = Integer.parseInt(lightValues.getChild("falloff").getAttributes().get(0).getValue());
        this.falloffAlpha2 = Integer.parseInt(lightValues.getChild("falloff").getAttributes().get(1).getValue());

        this.position = new Vec3(Double.parseDouble(lightValues.getChild("position").getAttributes().get(0).getValue()),
                                 Double.parseDouble(lightValues.getChild("position").getAttributes().get(1).getValue()),
                                 Double.parseDouble(lightValues.getChild("position").getAttributes().get(2).getValue()));

        this.direction = new Vec3(Double.parseDouble(lightValues.getChild("direction").getAttributes().get(0).getValue()),
                                  Double.parseDouble(lightValues.getChild("direction").getAttributes().get(1).getValue()),
                                  Double.parseDouble(lightValues.getChild("direction").getAttributes().get(2).getValue()));
    }

    @Override
    public String toString() {
        return String.format("# Parallel Light\n" +
                             "color: %s\n" +
                             "position: %s\n" +
                             "direction: %s\n" +
                             "falloff: %s - %s",
                             color,
                             position,
                             direction,
                             falloffAlpha1, falloffAlpha2);
    }
}
