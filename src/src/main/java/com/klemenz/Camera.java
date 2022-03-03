package com.klemenz;

import com.klemenz.Utility.Ray;
import com.klemenz.Utility.Vec3;
import org.jdom2.Attribute;
import org.jdom2.Element;

import java.util.List;

public class Camera {
    private final int width;
    private final int height;
    private final Vec3 position;
    private final Vec3 lookat;
    private final Vec3 up;
    private final int halfHorizontalFOV;
    private final int maxBounces;

    private final double viewportHeight = 2.0;
    private final double viewportWidth;
    private final double focalLength = 1;

    private final Vec3 horizontal;
    private final Vec3 vertical;
    private final Vec3 viewportBottomLeft;

    public Camera() {
        this.width = 960;
        this.height = 540;
        this.position = new Vec3(0.0, 0.0, 0.0);
        this.lookat = new Vec3(0.0, 0.0, -1.0);
        this.up = new Vec3(0.0, 1.0, 0.0);
        this.halfHorizontalFOV = 45;
        this.maxBounces = 2;

        // Inspired by the book "Ray Tracing in One Weekend"
        // https://raytracing.github.io/books/RayTracingInOneWeekend.html#positionablecamera
        // #############################################################################################################
        double theta = halfHorizontalFOV * Math.PI / 180.0;     // Convert degrees to radian
        double h = Math.tan(theta);                             // Don't need to divide the radians by 2 because we only have half of the FOV
        this.viewportWidth = ((double) width / height) * viewportHeight * h;

        Vec3 w = Vec3.unitVector(Vec3.subtract(position, lookat));
        Vec3 u = Vec3.unitVector(Vec3.cross(up, w));
        Vec3 v = Vec3.cross(w, u);

        horizontal = Vec3.scale(u, viewportWidth);
        vertical = Vec3.scale(v, viewportHeight);
        viewportBottomLeft = Vec3.subtract(Vec3.subtract(Vec3.subtract(position, Vec3.scale(horizontal, 0.5)),
                             Vec3.scale(vertical, 0.5)), w);
        // #############################################################################################################
    }

    public Camera(Element cameraValues) {
        this.width = Integer.parseInt(cameraValues.getChild("resolution").getAttributes().get(0).getValue());
        this.height = Integer.parseInt(cameraValues.getChild("resolution").getAttributes().get(1).getValue());
        this.halfHorizontalFOV = Integer.parseInt(cameraValues.getChild("horizontal_fov").getAttributes().get(0).getValue());
        this.maxBounces = Integer.parseInt(cameraValues.getChild("max_bounces").getAttributes().get(0).getValue());

        List<Attribute> positionValues = cameraValues.getChild("position").getAttributes();
        this.position = new Vec3(Double.parseDouble(positionValues.get(0).getValue()),
                                 Double.parseDouble(positionValues.get(1).getValue()),
                                 Double.parseDouble(positionValues.get(2).getValue()));

        List<Attribute> lookatValues = cameraValues.getChild("lookat").getAttributes();
        this.lookat = new Vec3(Double.parseDouble(lookatValues.get(0).getValue()),
                               Double.parseDouble(lookatValues.get(1).getValue()),
                               Double.parseDouble(lookatValues.get(2).getValue()));

        List<Attribute> upValues = cameraValues.getChild("up").getAttributes();
        this.up = new Vec3(Double.parseDouble(upValues.get(0).getValue()),
                           Double.parseDouble(upValues.get(1).getValue()),
                           Double.parseDouble(upValues.get(2).getValue()));

        // Inspired by the book "Ray Tracing in One Weekend"
        // https://raytracing.github.io/books/RayTracingInOneWeekend.html#positionablecamera
        // #############################################################################################################
        double theta = halfHorizontalFOV * Math.PI / 180.0;
        double h = Math.tan(theta);
        this.viewportWidth = ((double) width / height) * viewportHeight * h;

        Vec3 w = Vec3.unitVector(Vec3.subtract(position, lookat));
        Vec3 u = Vec3.unitVector(Vec3.cross(up, w));
        Vec3 v = Vec3.cross(w, u);

        horizontal = Vec3.scale(u, viewportWidth);
        vertical = Vec3.scale(v, viewportHeight);
        viewportBottomLeft = Vec3.subtract(Vec3.subtract(Vec3.subtract(position, Vec3.scale(horizontal, 0.5)),
                             Vec3.scale(vertical, 0.5)), w);
        // #############################################################################################################
    }

    public Vec3 getPosition() {
        return position;
    }

    public int getImageWidth() {
        return width;
    }

    public int getImageHeight() {
        return height;
    }

    public int getMaxBounces() {
        return maxBounces;
    }

    public double getViewportHeight() {
        return viewportHeight;
    }

    public double getViewportWidth() {
        return viewportWidth;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public Ray calculateRay(double hOffset, double vOffset) {
        // Move the vector for the bottom left corner of the view plane to the position where the ray would intersect with it.
        // Subtract the cameras position from this intersection point to get the direction of the ray.
        return new Ray(position, Vec3.subtract(Vec3.add(Vec3.add(viewportBottomLeft, Vec3.scale(horizontal, hOffset)),
                                 Vec3.scale(vertical, vOffset)), position));
    }

    @Override
    public String toString() {
        return String.format("====== Camera ======\n" +
                "position: %s\n" +
                "lookat: %s\n" +
                "up: %s\n" +
                "horizontalFOV: %s\n" +
                "resolution: %s x %s\n" +
                "maxBounces: %s",
                this.position,
                this.lookat,
                this.up,
                this.halfHorizontalFOV,
                this.width, this.height,
                this.maxBounces);
    }
}
