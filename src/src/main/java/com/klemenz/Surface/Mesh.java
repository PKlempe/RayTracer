package com.klemenz.Surface;

import com.klemenz.Main;
import com.klemenz.OBJParser;
import com.klemenz.Surface.Material.MaterialSolid;
import com.klemenz.Surface.Material.MaterialTextured;
import com.klemenz.Utility.Color;
import com.klemenz.Utility.HitPoint;
import com.klemenz.Utility.OBJInput;
import com.klemenz.Utility.Ray;
import com.klemenz.Utility.Vec3;
import com.klemenz.Utility.VertexIndices;
import org.jdom2.Element;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Mesh extends Surface {
    private final String filename;
    private final ArrayList<Vec3> vertices;
    private final ArrayList<Vec3> normals;
    private final ArrayList<Vec3> textures;
    private final ArrayList<VertexIndices> indices;

    public Mesh(Element surfaceValues) {
        super(surfaceValues.getChildren().get(0), surfaceValues.getChild("transforms"));

        this.filename = surfaceValues.getAttributes().get(0).getValue();

        OBJInput input = OBJParser.importOBJ(Main.inputDirectory + this.filename);
        this.vertices = input.vertices;
        this.normals = input.normals;
        this.textures = input.textures;
        this.indices = input.indices;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(String.format("# Mesh (%s):\n" +
                        "%s",
                filename,
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

    @Override
    public boolean checkIfBeingHit(Ray ray, HitPoint hitPoint, double tMin, double tMax) {
        final double EPSILON = 0.0000001;
        boolean isTriangleBeingHit = false;
        double closestT = tMax;
        Vec3 outwardNormal = null;
        Vec3 barycentricCoords = null;
        Vec3[] textureCoords = new Vec3[3];

        // Check all Triangles
        for(int i = 0; i < indices.size(); i += 3) {
            // Inspired by the implementation of the Möller-Trumbore intersection algorithm on Wikipedia
            // https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
            // #########################################################################################################
            Vec3 v0 = vertices.get(indices.get(i).vertexIndex);
            Vec3 v1 = vertices.get(indices.get(i + 1).vertexIndex);
            Vec3 v2 = vertices.get(indices.get(i + 2).vertexIndex);

            Vec3 edge1 = Vec3.subtract(v1, v0);
            Vec3 edge2 = Vec3.subtract(v2, v0);

            Vec3 h = Vec3.cross(ray.getDirection(), edge2);
            double a = Vec3.dot(edge1, h);
            if(a > -EPSILON && a < EPSILON) {
                continue;   // The ray is parallel to this triangle.
            }

            double f = 1.0 / a;
            Vec3 s = Vec3.subtract(ray.getOrigin(), v0);
            double u = f * Vec3.dot(s, h);
            if(u < 0.0 || u > 1.0) {
                continue;
            }

            Vec3 q = Vec3.cross(s, edge1);
            double v = f * Vec3.dot(ray.getDirection(), q);
            if(v < 0.0 || u + v > 1.0) {
                continue;
            }

            double t = f * Vec3.dot(edge2, q);
            // Check if the ray intersects with the triangle and if its the closest intersection regarding the cameras position.
            if(t > EPSILON && t < closestT) {
                isTriangleBeingHit = true;
                closestT = t;

                outwardNormal = normals.get(indices.get(i).normalIndex);
                barycentricCoords = new Vec3(u, v, 1 - u - v);
                textureCoords[0] = textures.get(indices.get(i).textureIndex);
                textureCoords[1] = textures.get(indices.get(i + 1).textureIndex);
                textureCoords[2] = textures.get(indices.get(i + 2).textureIndex);
            }
            // #########################################################################################################
        }

        if(isTriangleBeingHit) {
            hitPoint.t = closestT;
            hitPoint.position = ray.at(hitPoint.t);
            hitPoint.phongValues = material.getPhongValues();
            hitPoint.reflectance = material.getReflectance();
            hitPoint.transmittance = material.getTransmittance();
            hitPoint.refractionIOF = material.getRefractionIOF();
            hitPoint.setFaceNormal(ray, outwardNormal);

            if(material instanceof MaterialSolid) {
                hitPoint.color = ((MaterialSolid) material).getColor();
            }
            else {
                double u = barycentricCoords.getZ() * textureCoords[0].getX()
                         + barycentricCoords.getX() * textureCoords[1].getX()
                         + barycentricCoords.getY() * textureCoords[2].getX();
                double v = barycentricCoords.getZ() * textureCoords[0].getY()
                         + barycentricCoords.getX() * textureCoords[1].getY()
                         + barycentricCoords.getY() * textureCoords[2].getY();

                hitPoint.color = getPixelColorFromTexture(u, v);
            }
        }

        return isTriangleBeingHit;
    }
}
