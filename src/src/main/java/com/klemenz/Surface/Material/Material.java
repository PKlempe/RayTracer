package com.klemenz.Surface.Material;

import org.jdom2.Element;

public abstract class Material {
    protected final double[] phongValues = new double[4];
    protected final double reflectance;
    protected final double transmittance;
    protected final double refractionIOF;

    public Material() {
        this.reflectance = 0.0;
        this.transmittance = 0.0;
        this.refractionIOF = 2.3;

        this.phongValues[0] = 0.3;
        this.phongValues[1] = 0.9;
        this.phongValues[2] = 1.0;
        this.phongValues[3] = 200;
    }

    public Material(Element materialValues) {
        this.reflectance = Double.parseDouble(materialValues.getChild("reflectance").getAttributes().get(0).getValue());
        this.transmittance = Double.parseDouble(materialValues.getChild("transmittance").getAttributes().get(0).getValue());
        this.refractionIOF = Double.parseDouble(materialValues.getChild("refraction").getAttributes().get(0).getValue());

        this.phongValues[0] = Double.parseDouble(materialValues.getChild("phong").getAttributes().get(0).getValue());
        this.phongValues[1] = Double.parseDouble(materialValues.getChild("phong").getAttributes().get(1).getValue());
        this.phongValues[2] = Double.parseDouble(materialValues.getChild("phong").getAttributes().get(2).getValue());
        this.phongValues[3] = Double.parseDouble(materialValues.getChild("phong").getAttributes().get(3).getValue());
    }

    public double[] getPhongValues() {
        return phongValues;
    }

    public double getReflectance() {
        return reflectance;
    }

    public double getTransmittance() {
        return transmittance;
    }

    public double getRefractionIOF() {
        return refractionIOF;
    }
}
