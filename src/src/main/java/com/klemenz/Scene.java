package com.klemenz;

import java.util.*;

import com.klemenz.Light.*;
import com.klemenz.Surface.*;
import com.klemenz.Utility.Color;
import com.klemenz.Utility.HitPoint;
import com.klemenz.Utility.Ray;
import org.jdom2.*;

public class Scene {
    private final String outputFile;
    private final Color backgroundColor;
    private final Camera camera;
    private final List<Light> lights = new ArrayList<>();
    private final List<Surface> surfaces = new ArrayList<>();

    public Scene(String filename) {
        this.outputFile = filename;
        this.backgroundColor = new Color(0, 0, 0);
        this.camera = new Camera();
    }

    public Scene(Element root) {
        this.outputFile = root.getAttributes().get(0).getValue();

        List<Attribute> backgroundColorValues = root.getChild("background_color").getAttributes();
        this.backgroundColor = new Color(Double.parseDouble(backgroundColorValues.get(0).getValue()),
                                         Double.parseDouble(backgroundColorValues.get(1).getValue()),
                                         Double.parseDouble(backgroundColorValues.get(2).getValue()));

        this.camera = new Camera(root.getChild("camera"));

        for(Element light : root.getChild("lights").getChildren()) {
            Light temp;
            switch (light.getName()) {
                case "ambient_light":
                    temp = new AmbientLight(light);
                    break;
                case "parallel_light":
                    temp = new ParallelLight(light);
                    break;
                case "point_light":
                    temp = new PointLight(light);
                    break;
                default:
                    temp = new SpotLight(light);
                    break;
            }
            this.lights.add(temp);
        }

        for(Element surface : root.getChild("surfaces").getChildren()) {
            Surface temp;
            if(surface.getName().equals("sphere")) {
                temp = new Sphere(surface);
            }
            else {
                temp = new Mesh(surface);
            }
            this.surfaces.add(temp);
        }
    }

    public String getOutputFile() {
        return outputFile;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Camera getCamera() {
        return camera;
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }

    public void addSurface(Surface surface) {
        surfaces.add(surface);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    // Inspired by the book "Ray Tracing in One Weekend"
    // https://raytracing.github.io/books/RayTracingInOneWeekend.html#surfacenormalsandmultipleobjects/alistofhittableobjects
    // #################################################################################################################
    public boolean checkForIntersection(Ray ray, HitPoint hitPoint, double tMin, double tMax) {
        boolean isSomethingBeingHit = false;
        double closestT = tMax;

        for (Surface s : surfaces) {
            if (s.checkIfBeingHit(ray, hitPoint, tMin, closestT)) {
                isSomethingBeingHit = true;
                closestT = hitPoint.t;
            }
        }

        return isSomethingBeingHit;
    }
    // #################################################################################################################

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(String.format("\n\noutputFile: %s\n" +
                        "backgroundColor: %s\n\n" +
                "%s\n\n" +
                "====== Lights ======\n",
                outputFile,
                backgroundColor,
                camera));

        for(Light light : lights) {
            out.append(String.format("%s\n\n", light));
        }

        out.append("====== Surfaces ======\n");

        for(Surface surface : surfaces) {
            out.append(String.format("%s\n\n", surface));
        }

        return out.toString();
    }
}
