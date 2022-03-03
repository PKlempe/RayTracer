package com.klemenz.Light;

import com.klemenz.Utility.Color;
import org.jdom2.Element;

public class AmbientLight extends Light {

    public AmbientLight(Color color) {
        super(color);
    }

    public AmbientLight(Element lightValues) {
        super(lightValues.getChild("color").getAttributes());
    }

    @Override
    public String toString() {
        return String.format("# Ambient Light\n" +
                             "color: %s", color);
    }
}
