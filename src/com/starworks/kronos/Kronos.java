package com.starworks.kronos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.starworks.kronos.files.FileSystem;

public final class Kronos {

	public static void preinclude() {
		validateMaps();
	}
	
	public static void validateMapXSD() {
		File xsdFile = new File("map.xsd");
		if (!xsdFile.exists()) {
            try {
                xsdFile.createNewFile();
                String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                		   + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n"
	                       + "\t<xs:element name=\"map\">\n"
	                       + "\t\t<xs:complexType>\n"
	                       + "\t\t\t<xs:sequence>\n"
	                       + "\t\t\t\t<xs:element name=\"entries\">\n"
	                       + "\t\t\t\t\t<xs:complexType>\n"
	                       + "\t\t\t\t\t\t<xs:sequence>\n"
	                       + "\t\t\t\t\t\t\t<xs:element name=\"entry\" maxOccurs=\"unbounded\">\n"
	                       + "\t\t\t\t\t\t\t\t<xs:complexType>\n"
	                       + "\t\t\t\t\t\t\t\t\t<xs:simpleContent>\n"
	                       + "\t\t\t\t\t\t\t\t\t\t<xs:extension base=\"xs:string\">\n"
	                       + "\t\t\t\t\t\t\t\t\t\t\t<xs:attribute name=\"key\" type=\"xs:string\" use=\"required\" />\n"
	                       + "\t\t\t\t\t\t\t\t\t\t</xs:extension>\n"
	                       + "\t\t\t\t\t\t\t\t\t</xs:simpleContent>\n"
	                       + "\t\t\t\t\t\t\t\t</xs:complexType>\n"
	                       + "\t\t\t\t\t\t\t</xs:element>\n"
	                       + "\t\t\t\t\t\t</xs:sequence>\n"
	                       + "\t\t\t\t\t</xs:complexType>\n"
	                       + "\t\t\t\t</xs:element>\n"
	                       + "\t\t\t</xs:sequence>\n"
	                       + "\t\t</xs:complexType>\n"
	                       + "\t</xs:element>\n"
	                       + "</xs:schema>";
                Files.write(xsdFile.toPath(), xsd.getBytes());
            } catch (IOException e) {
            	System.out.println("Error creating map.xsd: " + e.getMessage());
        	}
		}
	}
	
	private static void validateMaps() {
		try {
			FileSystem.getFileHandle("configuration.xml");
			FileSystem.getFileHandle("logging.xml");
		} catch (IOException e) {
		}
		
	}
}
