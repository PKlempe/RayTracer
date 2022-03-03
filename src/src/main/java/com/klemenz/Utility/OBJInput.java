package com.klemenz.Utility;

import java.util.ArrayList;

public class OBJInput {
    public ArrayList<Vec3> vertices = new ArrayList<Vec3>();
    public ArrayList<Vec3> normals = new ArrayList<Vec3>();
    public ArrayList<Vec3> textures = new ArrayList<Vec3>();
    public ArrayList<VertexIndices> indices = new ArrayList<VertexIndices>();

    public OBJInput(ArrayList<Vec3> vertices, ArrayList<Vec3> normals, ArrayList<Vec3> textures, ArrayList<VertexIndices> indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.textures = textures;
        this.indices = indices;
    }
}
