package com.klemenz;

import com.klemenz.Light.Light;
import com.klemenz.Light.AmbientLight;
import com.klemenz.Light.PointLight;
import com.klemenz.Light.SpotLight;
import com.klemenz.Light.ParallelLight;
import com.klemenz.Surface.Sphere;
import com.klemenz.Utility.Color;
import com.klemenz.Utility.HitPoint;
import com.klemenz.Utility.Ray;
import com.klemenz.Utility.Vec3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RayTracer {
    private final double acneRemovalSensitivity = 0.001;

    public RayTracer() {

    }

    public void renderScene(Scene scene) {
        System.out.printf("\n\n===== Rendering %s =====%n", scene.getOutputFile());
        render(scene, scene.getCamera());
    }

    public void renderDefaultScene() {
        System.out.println("\n\n===== Rendering Default Scene =====");

        Scene defaultScene = new Scene("defaultScene.png");
        Camera camera = defaultScene.getCamera();

        defaultScene.addSurface(new Sphere(new Vec3(-0.6,0.0,-1.0), 0.5, new Color(1.0, 0, 0)));
        defaultScene.addSurface(new Sphere(new Vec3(0.6,0.0,-1.0), 0.5, new Color(0, 0, 1.0)));

        defaultScene.addLight(new AmbientLight(new Color(1.0, 1.0, 1.0)));
        defaultScene.addLight(new ParallelLight(new Color(1.0, 1.0, 1.0), new Vec3(0.0, -1.0, 0.0)));

        render(defaultScene, camera);
    }

    public void renderBlackImage() {
        System.out.println("\n\n===== Rendering Empty Scene =====");

        Scene emptyScene = new Scene("blackImage.png");
        Camera camera = emptyScene.getCamera();

        render(emptyScene, camera);
    }

    private void render(Scene scene, Camera camera) {
        int imageWidth = camera.getImageWidth();
        int imageHeight = camera.getImageHeight();
        int[][] pixelArray = new int[imageHeight][imageWidth];

        // Calculate Pixel Values
        int lastPercentage = -1;
        for(int i = imageHeight - 1; i >= 0; i--) {
            // Calculate & print current progress
            int currentPercentage = Math.round((1 - ((float) i / (imageHeight - 1))) * 100);
            if(currentPercentage != lastPercentage) {
                lastPercentage = currentPercentage;
                System.out.printf("Progress: %s%%%n", currentPercentage);
            }

            // Calculate and set the color for the individual pixels
            for(int j = 0; j < imageWidth; j++) {
                // Inspired by the book "Ray Tracing in One Weekend"
                // https://raytracing.github.io/books/RayTracingInOneWeekend.html#rays,asimplecamera,andbackground/sendingraysintothescene
                // #####################################################################################################
                double hOffset = (double) j / (imageWidth - 1);
                double vOffset = (double) i / (imageHeight - 1);
                Ray ray = camera.calculateRay(hOffset, vOffset);
                // #####################################################################################################

                Color pixelColor = calculateRayColor(ray, scene, scene.getCamera().getMaxBounces());
                pixelArray[(imageHeight - 1) - i][j] = pixelColor.getRGB();
            }
        }

        // Export Pixel Array as Image
        exportPixelArrayAsImage(pixelArray, imageWidth, imageHeight, scene.getOutputFile());
    }

    private Color calculateRayColor(Ray ray, Scene scene, int remainingBounces) {
        HitPoint hitPoint = new HitPoint();

        if(scene.checkForIntersection(ray, hitPoint, acneRemovalSensitivity, Double.POSITIVE_INFINITY)) {
            /*// Debugging Code for visualizing the Surface Normals
            Vec3 temp = Vec3.scale(Vec3.add(hitPoint.normal, new Vec3(1.0, 1.0, 1.0)), 0.5);
            Color color = new Color(temp.getX(), temp.getY(), temp.getZ());*/

            Color color = applyShading(ray.getDirection(), hitPoint, scene);
            color = Color.multiply(color, 1 - hitPoint.reflectance - hitPoint.transmittance);

            // Reflection
            if(hitPoint.reflectance > 0 && remainingBounces > 0) {
                Vec3 normal = Vec3.unitVector(hitPoint.normal);
                Vec3 reflectionDir = Vec3.subtract(ray.getDirection(), Vec3.scale(normal, 2 * Vec3.dot(ray.getDirection(), normal)));
                Ray reflectionRay = new Ray(hitPoint.position, reflectionDir);

                Color colorReflection = calculateRayColor(reflectionRay, scene, remainingBounces - 1);
                colorReflection = Color.multiply(colorReflection, hitPoint.reflectance);

                color = Color.add(color, colorReflection);
            }

            // Refraction
            if(hitPoint.transmittance > 0 && remainingBounces > 0) {
                Ray refractionRay;
                Vec3 v = Vec3.unitVector(ray.getDirection());
                Vec3 n = Vec3.unitVector(hitPoint.normal);
                double c = Vec3.dot(v, n);
                double r = hitPoint.isOnFrontFace ? 1.0 / hitPoint.refractionIOF : hitPoint.refractionIOF;
                double D = 1 - Math.pow(r, 2) * (1 - Math.pow(c, 2));

                if(D < 0) {
                    // Total Internal Reflection
                    // Need to calculate the reflection ray.
                    Vec3 reflectionDir = Vec3.subtract(v, Vec3.scale(n, 2 * c));
                    refractionRay = new Ray(hitPoint.position, reflectionDir);
                }
                else {
                    Vec3 refractionDir = Vec3.subtract(Vec3.scale(Vec3.add(Vec3.scale(n, -c), v), r), Vec3.scale(n, Math.sqrt(D)));
                    refractionRay = new Ray(hitPoint.position, refractionDir);
                }

                Color colorRefraction = calculateRayColor(refractionRay, scene, remainingBounces - 1);
                colorRefraction = Color.multiply(colorRefraction, hitPoint.transmittance);

                color = Color.add(color, colorRefraction);
            }

            return color;
        }

        return scene.getBackgroundColor();
    }

    private Color applyShading(Vec3 rayDirection, HitPoint hitPoint, Scene scene) {
        Vec3 pointColor = new Vec3(hitPoint.color.getR(), hitPoint.color.getG(), hitPoint.color.getB());
        Vec3 finalColor;
        Vec3 ambient = new Vec3();
        Vec3 diffuse = new Vec3();
        Vec3 specular = new Vec3();

        for(Light light : scene.getLights()) {
            Color lightColor = light.getColor();
            Vec3 vLightColor = new Vec3(lightColor.getR(), lightColor.getG(), lightColor.getB());

            if(!(light instanceof AmbientLight)) {
                Vec3 L;
                double distanceToLight;

                // Point Light
                if(light instanceof PointLight) {
                    Vec3 pointToLight = Vec3.subtract(((PointLight) light).getPosition(), hitPoint.position);
                    L = Vec3.unitVector(pointToLight);
                    distanceToLight = Vec3.length(pointToLight);
                }
                // Spot Light
                else if(light instanceof SpotLight) {
                    // TODO: Implement Phong Shading for spot lights
                    L = new Vec3();
                    distanceToLight = 1;
                }
                // Parallel Light
                else {
                    L = Vec3.negate(Vec3.unitVector(((ParallelLight) light).getDirection()));
                    distanceToLight = Double.POSITIVE_INFINITY;
                }

                // Calculate Shading Components
                Ray shadowRay = new Ray(hitPoint.position, L);

                if (!scene.checkForIntersection(shadowRay, new HitPoint(), acneRemovalSensitivity, distanceToLight)) {
                    Vec3 negatedL = Vec3.negate(L);
                    Vec3 N = Vec3.unitVector(hitPoint.normal);
                    Vec3 R = Vec3.unitVector(Vec3.subtract(negatedL, Vec3.scale(N, 2 * Vec3.dot(negatedL, N))));
                    Vec3 E = Vec3.unitVector(Vec3.negate(rayDirection));

                    diffuse = Vec3.add(diffuse, Vec3.multiply(Vec3.scale(vLightColor, Math.max(0.0, Vec3.dot(L, N))), pointColor));
                    specular = Vec3.add(specular, Vec3.scale(vLightColor, Math.pow(Math.max(Vec3.dot(R, E), 0.0), hitPoint.phongValues[3])));
                }
            }
            // Ambient Light
            else {
                ambient = Vec3.multiply(vLightColor, pointColor);
            }
        }

        ambient = Vec3.scale(ambient, hitPoint.phongValues[0]);
        diffuse = Vec3.scale(diffuse, hitPoint.phongValues[1]);
        specular = Vec3.scale(specular, hitPoint.phongValues[2]);
        finalColor = Vec3.add(ambient, Vec3.add(diffuse, specular));

        return new Color(Math.min(finalColor.getX(), 1.0), Math.min(finalColor.getY(), 1.0), Math.min(finalColor.getZ(), 1.0));
    }

    private void exportPixelArrayAsImage(int[][] pixelArray, int width, int height, String filename) {
        // Flatten the Pixel Array
        int[] pixels = Arrays.stream(pixelArray)
                .flatMapToInt(Arrays::stream)
                .toArray();

        // Inspired by the Answer from "Brendan Cashman" on StackOverflow
        // https://stackoverflow.com/a/125013/11889326
        // #############################################################################################################
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        raster.setDataElements(0, 0, width, height, pixels);
        // #############################################################################################################

        // Write BufferedImage to File
        try {
            String fileExtension = filename.split("\\.")[1];
            File outputFile = new File(String.format("..%soutput%s%s", Main.osSeparator, Main.osSeparator, filename));
            ImageIO.write(image, fileExtension, outputFile);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    // #########################################
    // ######### Implemented Effects ###########
    // #########################################
    private Color applyDepthOfField(Vec3 rayDirection, Scene scene, Color pixelColor) {
        int amountAdditionalRays = 4;
        Vec3 finalColor = new Vec3(pixelColor.getR(), pixelColor.getG(), pixelColor.getB());

        //Vec3 direction = Vec3.scale(rayDirection, scene.getCamera().getFocalLength());
        Vec3 direction = Vec3.scale(rayDirection, scene.getCamera().getFocalLength());

        for(int i = 0; i < amountAdditionalRays; i++) {
            // Calculate random point "o" within a circle around the center of projection
            double r = Math.sqrt(Math.random());
            double theta = Math.random() * 2 * Math.PI;

            double x = scene.getCamera().getPosition().getX() + r * Math.cos(theta);
            double y = scene.getCamera().getPosition().getY() + r * Math.sin(theta);

            Vec3 o = new Vec3(x, y, scene.getCamera().getPosition().getZ());

            // Calculate new DoF ray
            Vec3 d = Vec3.subtract(direction, o);
            Ray rayDoF = new Ray(o, d);

            Color color = calculateRayColor(rayDoF, scene, scene.getCamera().getMaxBounces());
            finalColor = Vec3.add(finalColor, new Vec3(color.getR(), color.getG(), color.getB()));
        }

        finalColor = Vec3.scale(finalColor, 1.0 / (amountAdditionalRays + 1));

        return new Color(finalColor.getX(), finalColor.getY(), finalColor.getZ());
    }
}
