package com.starworks.kronos.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.starworks.kronos.files.FileSystem;
import com.ximpleware.AutoPilot;
import com.ximpleware.VTDException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

public class XMLMap implements AutoCloseable {

	private final String m_filename;
	private final Map<String, String> m_entries;
	private boolean m_generated;

	public XMLMap(String filename) {
		this.m_filename = filename;
		this.m_entries = new HashMap<String, String>();
		load(filename);
	}
	
	private void load(String filename) {
		VTDGen vtd = new VTDGen();
		try (InputStream input = new FileInputStream(filename)) {
			if (vtd.parseFile(filename, true)) {
				VTDNav nav = vtd.getNav();
				AutoPilot auto = new AutoPilot(nav);
				auto.selectXPath("/map/entries/entry");
				while (auto.evalXPath() != -1) {
					nav.push();
					String key = nav.toString(nav.getAttrVal("key"));
					String value = nav.toString(nav.getText());
					m_entries.put(key, value);
					nav.pop();
				}
			}
		} catch (FileNotFoundException handled) {
			File xmlFile = new File(filename);
			try {
				if (xmlFile.createNewFile()) {
					FileWriter fw = new FileWriter(xmlFile);
					fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<map xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"map.xsd\">\n" + "\t<entries>\n" + "\t</entries>\n" + "</map>");
					fw.close();
					m_generated = true;
				} else {
					System.err.println("File `" + filename + "' failed to be created, for an unknown reason.");
				}
			} catch (IOException e) {
				System.err.println("File `" + filename + "' failed to be created, for an unknown reason.\n" + e.getLocalizedMessage());
			}
		} catch (IOException | VTDException e) {
			e.printStackTrace();
		}
	}

	public void put(String key, String value) {
		m_entries.put(key, value);
	}

	public String get(String key) {
		return m_entries.get(key);
	}
	
	public void export() {
	    StringBuilder entryBuilder = new StringBuilder();
	    StringBuilder mapBuilder = new StringBuilder();
	    mapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<map xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"map.xsd\">\n" + "\t<entries>");
	    for (Map.Entry<String, String> entry : m_entries.entrySet()) {
	        buildEntry(entry.getKey(), entry.getValue(), entryBuilder);
	        mapBuilder.append(entryBuilder);
	    }
	    mapBuilder.append("\n\t</entries>\n" + "</map>");

	    try (FileWriter fw = new FileWriter(m_filename)){
	        fw.write(mapBuilder.toString());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	private static final String FIRST = "\t\t<entry key=\"";
	private static final String SECOND = "\">";
	private static final String THIRD = "</entry>";

	private void buildEntry(String key, String value, StringBuilder sb) {
		sb.setLength(0);
		sb.append(System.lineSeparator());
		sb.append(FIRST);
		sb.append(key);
		sb.append(SECOND);
		sb.append(value);
		sb.append(THIRD);
	}

	public boolean wasGenerated() {
		return m_generated;
	}

	@Override
	public void close() throws Exception {
		FileSystem.closeFileHandle(m_filename);
	}
}
