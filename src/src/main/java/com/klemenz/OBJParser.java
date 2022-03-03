package com.klemenz;

import com.klemenz.Utility.OBJInput;
import com.klemenz.Utility.Vec3;
import com.klemenz.Utility.VertexIndices;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class OBJParser {

    public static OBJInput importOBJ(String filepath){
        List<List<? extends Number>> meshValues = new ArrayList<List<? extends Number>>(4);
        ArrayList<Vec3> vertices = new ArrayList<Vec3>();
        ArrayList<Vec3> normals = new ArrayList<Vec3>();
        ArrayList<Vec3> textures = new ArrayList<Vec3>();
        ArrayList<VertexIndices> indices = new ArrayList<VertexIndices>();

        try (Stream<String> stream = Files.lines(Paths.get(filepath))) {
            stream.forEach(line -> {
                String[] values = line.trim().split(" ");
                Vec3 vector;

                if(line.startsWith("v ")) {
                    vector = new Vec3(Double.parseDouble(values[1]),
                                      Double.parseDouble(values[2]),
                                      Double.parseDouble(values[3]));

                    vertices.add(vector);
                }
                else if(line.startsWith("vt ")) {
                    vector = new Vec3(Double.parseDouble(values[1]),
                                      Double.parseDouble(values[2]),
                                   null);
                    textures.add(vector);
                }
                else if(line.startsWith("vn ")) {
                    vector = new Vec3(Double.parseDouble(values[1]),
                                      Double.parseDouble(values[2]),
                                      Double.parseDouble(values[3]));
                    normals.add(vector);
                }
                else if(line.startsWith("f ")) {
                    Arrays.stream(values)
                            .skip(1)
                            .map(trio -> {
                                String[] indexTrio = trio.split("/");
                                return new VertexIndices(
                                        Integer.parseInt(indexTrio[0]) - 1,
                                        Integer.parseInt(indexTrio[1]) - 1,
                                        Integer.parseInt(indexTrio[2]) - 1);
                            })
                            .forEach(indices::add);
                }
            });
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return new OBJInput(vertices, normals, textures, indices);
    }

}
