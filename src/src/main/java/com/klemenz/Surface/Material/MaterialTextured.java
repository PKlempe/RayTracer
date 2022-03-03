package com.klemenz.Surface.Material;

import com.klemenz.Main;
import org.jdom2.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MaterialTextured extends Material {
    private final String name;
    private BufferedImage texture;

    public MaterialTextured(Element materialValues) {
        super(materialValues);

        this.name = materialValues.getChild("texture").getAttributes().get(0).getValue();

        try {
            this.texture = ImageIO.read(new File(Main.inputDirectory + this.name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getTexture() {
        return texture;
    }

    @Override
    public String toString() {
        return String.format("-> Material: Textured\n" +
                        "texture: %s\n" +
                        "phong: %s\n" +
                        "reflectance: %s\n" +
                        "transmittance: %s\n" +
                        "refraction: %s",
                name,
                Arrays.toString(phongValues),
                reflectance,
                transmittance,
                refractionIOF);
    }
}
