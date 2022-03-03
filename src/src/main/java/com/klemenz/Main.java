package com.klemenz;

import java.nio.file.Paths;

public class Main {
    public static String osSeparator = System.getProperty("file.separator");
    public static String inputDirectory;

    public static void main(String[] args) {
        RayTracer rayTracer = new RayTracer();
        inputDirectory = Paths.get(args[0]).getParent().toString() + osSeparator;

        //Scene scene = XMLParser.importXMLScene(args[0]);
        // rayTracer.renderScene(scene);

        // ##################################################
        // ################## Testing Code ##################
        // #################################################

        // ################## Lab 2a ##################
        // T1: Output a valid black image file
        rayTracer.renderBlackImage();

        // T2: Output an image with spheres on it after ray tracing them
        rayTracer.renderDefaultScene();

        // T3 - 5: Render example files
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample1.xml", osSeparator, osSeparator)));
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample2.xml", osSeparator, osSeparator)));
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample3.xml", osSeparator, osSeparator)));


        // ################## Lab 2b ##################
        // T1: Parse and raycast triangles (meshes read from obj files)
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample4.xml", osSeparator, osSeparator)));

        // T2: Implement basic reflection
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample5.xml", osSeparator, osSeparator)));

        // T3 & 4: Implement basic refraction & Add basic (color image) texture mapping for triangles
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample5_Refraction.xml", osSeparator, osSeparator)));
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample6.xml", osSeparator, osSeparator)));

        // T5: Add camera transformations
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample7.xml", osSeparator, osSeparator)));

        // T6: Add texture mapping for spheres
        rayTracer.renderScene(XMLParser.importXMLScene(String.format("..%sInput_Scenes%sexample8.xml", osSeparator, osSeparator)));
    }
}
