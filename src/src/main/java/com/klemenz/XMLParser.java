package com.klemenz;

import java.io.*;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class XMLParser {

    public static Scene importXMLScene(String filepath){
        Document document = null;

        try {
            File input = new File(filepath);
            SAXBuilder saxBuilder = new SAXBuilder();
            // Disable validation and loading of external DTD files.
            saxBuilder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            document = saxBuilder.build(input);
        }
        catch(JDOMException | IOException e) {
            e.printStackTrace();
        }

        return new Scene(document.getRootElement());
    }

}
