package com.klemenz.Surface;

import com.klemenz.Surface.Material.*;
import com.klemenz.Utility.Color;
import com.klemenz.Utility.HitPoint;
import com.klemenz.Utility.Ray;
import org.jdom2.Element;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Surface {
    protected final Material material;
    protected final Map<String, int[]> transformations = new LinkedHashMap<>();

    public Surface(Color color) {
        this.material = new MaterialSolid(color);
    }

    public Surface(Element materialValues, Element transformValues) {
        this.material = materialValues.getName().equals("material_solid") ?
                new MaterialSolid(materialValues) : new MaterialTextured(materialValues);

        if(transformValues != null) {
            for (Element transform : transformValues.getChildren()) {
                int[] values = new int[3];

                for (int i = 0; i < transform.getAttributes().size(); i++) {
                    values[i] = Integer.parseInt(transform.getAttributes().get(i).getValue());
                }

                this.transformations.put(transform.getName(), values);
            }
        }
    }

    public abstract boolean checkIfBeingHit(Ray ray, HitPoint hitPoint, double tMin, double tMax);

    protected Color getPixelColorFromTexture(double u, double v) {
        // TODO: Account for out of bounds indices by repeating the image
        BufferedImage texture = ((MaterialTextured) material).getTexture();
        int xCoord = (int) Math.round(u * texture.getWidth()) - 1;
        int yCoord = (int) Math.round(v * texture.getHeight()) - 1;

        int colorValue = texture.getRGB(Math.max(0, xCoord), Math.max(0, yCoord));
        return new Color(colorValue);
    };
}
