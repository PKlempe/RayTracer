package com.klemenz.Surface.Material;

import com.klemenz.Utility.Color;
import org.jdom2.Element;

import java.util.Arrays;

public class MaterialSolid extends Material {
    private final Color color;

    public MaterialSolid(Color color) {
        super();

        this.color = color;
    }

    public MaterialSolid(Element materialValues) {
        super(materialValues);

        color = new Color(Double.parseDouble(materialValues.getChild("color").getAttributes().get(0).getValue()),
                          Double.parseDouble(materialValues.getChild("color").getAttributes().get(1).getValue()),
                          Double.parseDouble(materialValues.getChild("color").getAttributes().get(2).getValue()));
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return String.format("-> Material: Solid\n" +
                "color: %s\n" +
                "phong: %s\n" +
                "reflectance: %s\n" +
                "transmittance: %s\n" +
                "refraction: %s",
                color,
                Arrays.toString(phongValues),
                reflectance,
                transmittance,
                refractionIOF);
    }
}
