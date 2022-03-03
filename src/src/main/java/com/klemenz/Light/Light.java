package com.klemenz.Light;

import com.klemenz.Utility.Color;
import org.jdom2.Attribute;

import java.util.List;

public abstract class Light {
    protected final Color color;

    public Light(Color color) {
        this.color = color;
    }

    public Light(List<Attribute> colorValues) {
        this.color = new Color(Double.parseDouble(colorValues.get(0).getValue()),
                               Double.parseDouble(colorValues.get(1).getValue()),
                               Double.parseDouble(colorValues.get(2).getValue()));
    }

    public Color getColor() {
        return color;
    }
}
