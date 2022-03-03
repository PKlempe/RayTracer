package com.klemenz.Surface;

import com.klemenz.Surface.Material.MaterialSolid;
import com.klemenz.Surface.Material.MaterialTextured;
import com.klemenz.Utility.Color;
import com.klemenz.Utility.HitPoint;
import com.klemenz.Utility.Ray;
import com.klemenz.Utility.Vec3;
import org.jdom2.Element;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;

public class Sphere extends Surface {
    private final double radius;
    private final Vec3 position;

    public Sphere(Vec3 position, double radius, Color color) {
        super(color);

        this.position = position;
        this.radius = radius;
    }

    public Sphere(Element surfaceValues) {
        super(surfaceValues.getChildren().get(1), surfaceValues.getChild("transforms"));

        this.radius = Double.parseDouble(surfaceValues.getAttribute("radius").getValue());

        position = new Vec3(Double.parseDouble(surfaceValues.getChild("position").getAttributes().get(0).getValue()),
                            Double.parseDouble(surfaceValues.getChild("position").getAttributes().get(1).getValue()),
                            Double.parseDouble(surfaceValues.getChild("position").getAttributes().get(2).getValue()));
    }

    public double getRadius() {
        return radius;
    }

    public Vec3 getPosition() {
        return position;
    }

    @Override
    public boolean checkIfBeingHit(Ray ray, HitPoint hitPoint, double tMin, double tMax) {
        // Inspired by the book "Ray Tracing in One Weekend"
        // https://raytracing.github.io/books/RayTracingInOneWeekend.html#surfacenormalsandmultipleobjects
        // #############################################################################################################
        Vec3 oc = Vec3.subtract(ray.getOrigin(), position);
        double a = Vec3.squaredLength(ray.getDirection());
        double bHalf = Vec3.dot(oc, ray.getDirection());
        double c = Vec3.squaredLength(oc) - Math.pow(radius, 2);

        double discriminant = Math.pow(bHalf, 2) - (a * c);
        if (discriminant < 0) {
            return false;
        }
        double sqrtDiscriminant = Math.sqrt(discriminant);

        // Find the nearest value that lies in the specified range.
        double root = (-bHalf - sqrtDiscriminant) / a;
        if (root < tMin || tMax < root) {
            root = (-bHalf + sqrtDiscriminant) / a;
            if (root < tMin || tMax < root)
                return false;
        }
        // #############################################################################################################

        hitPoint.t = root;
        hitPoint.position = ray.at(hitPoint.t);
        hitPoint.phongValues = material.getPhongValues();
        hitPoint.reflectance = material.getReflectance();
        hitPoint.transmittance = material.getTransmittance();
        hitPoint.refractionIOF = material.getRefractionIOF();
        Vec3 outwardNormal = Vec3.scale(Vec3.subtract(hitPoint.position, position), (1.0 / radius));
        hitPoint.setFaceNormal(ray, outwardNormal);

        if(material instanceof MaterialSolid) {
            hitPoint.color = ((MaterialSolid) material).getColor();
        }
        else {
            // Taken from the article "UV mapping" on Wikipedia
            // https://en.wikipedia.org/wiki/UV_mapping
            // #########################################################################################################
            Vec3 normal = Vec3.unitVector(hitPoint.normal);
            double u = 0.5 + Math.atan2(normal.getX(), normal.getZ()) / (2 * Math.PI);
            double v = 0.5 - Math.asin(normal.getY()) / Math.PI;
            // #########################################################################################################

            hitPoint.color = getPixelColorFromTexture(u, v);
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(String.format("# Sphere:\n" +
                        "position: %s\n" +
                        "radius: %s\n" +
                        "%s",
                position,
                radius,
                material));

        if(!transformations.isEmpty()) {
            out.append("\n-> Transformations");

            for (Map.Entry<String, int[]> entry : transformations.entrySet()) {
                out.append(String.format("\n%s: %s",
                        entry.getKey(),
                        Arrays.toString(entry.getValue())));
            }
        }

        return out.toString();
    }
}
