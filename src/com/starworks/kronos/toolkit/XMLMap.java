package com.starworks.kronos.toolkit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.starworks.kronos.files.FileHandle;
import com.starworks.kronos.files.FileSystem;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

public class XMLMap implements AutoCloseable {

	private static final String MAP_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
			"<map xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"map.xsd\">\n" + //
			"\t<entries>\n";

	private static final String ENTRY_TEMPLATE = "\t\t<entry key=\"%s\">%s</entry>\n";

	private static final String MAP_FOOTER = "\t</entries>\n" + //
			"</map>";

	static {
		try {
			FileHandle handle = FileSystem.INSTANCE.getFileHandle("map.xsd", true, true);
			if (handle.wasGenerated()) {
				String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
						"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" + //
						"\t<xs:element name=\"map\">\n" + //
						"\t\t<xs:complexType>\n" + //
						"\t\t\t<xs:sequence>\n" + //
						"\t\t\t\t<xs:element name=\"entries\">\n" + //
						"\t\t\t\t\t<xs:complexType>\n" + //
						"\t\t\t\t\t\t<xs:sequence>\n" + //
						"\t\t\t\t\t\t\t<xs:element name=\"entry\" maxOccurs=\"unbounded\">\n" + //
						"\t\t\t\t\t\t\t\t<xs:complexType>\n" + //
						"\t\t\t\t\t\t\t\t\t<xs:simpleContent>\n" + //
						"\t\t\t\t\t\t\t\t\t\t<xs:extension base=\"xs:string\">\n" + //
						"\t\t\t\t\t\t\t\t\t\t\t<xs:attribute name=\"key\" type=\"xs:string\" use=\"required\" />\n" + //
						"\t\t\t\t\t\t\t\t\t\t</xs:extension>\n" + //
						"\t\t\t\t\t\t\t\t\t</xs:simpleContent>\n" + //
						"\t\t\t\t\t\t\t\t</xs:complexType>\n" + //
						"\t\t\t\t\t\t\t</xs:element>\n" + //
						"\t\t\t\t\t\t</xs:sequence>\n" + //
						"\t\t\t\t\t</xs:complexType>\n" + //
						"\t\t\t\t</xs:element>\n" + //
						"\t\t\t</xs:sequence>\n" + //
						"\t\t</xs:complexType>\n" + //
						"\t</xs:element>\n" + //
						"</xs:schema>";
				handle.write(xsd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private FileHandle m_handle;
	private final Map<String, String> m_entries;
	private boolean m_generated;

	public XMLMap(String fileName) {
		this.m_entries = new HashMap<String, String>();
		try {
			load(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public XMLMap(String fileName, String... defaults) {
		int size = defaults.length;
		if (size % 2 != 0) throw new IllegalArgumentException();
		this.m_entries = new HashMap<String, String>();
		try {
			load(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (m_generated) {
			for (int i = 0; i < size; i += 2) {
				put(defaults[i], defaults[i + 1]);
			}

			export();
		}
	}

	private void load(String filename) throws IOException {
		VTDGen vtd = new VTDGen();

		this.m_handle = FileSystem.INSTANCE.getFileHandle(filename, true, true);
		if (m_generated = m_handle.wasGenerated()) {
			try {
				m_handle.write(MAP_HEADER);
				m_handle.write(MAP_FOOTER);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
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
		} catch (XPathParseException | XPathEvalException | NavException e) {
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
		// StringBuilder entryBuilder = new StringBuilder();
		// StringBuilder mapBuilder = new StringBuilder();
		// mapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<map
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
		// xsi:noNamespaceSchemaLocation=\"map.xsd\">\n" + "\t<entries>");
		// for (Map.Entry<String, String> entry : m_entries.entrySet()) {
		// buildEntry(entry.getKey(), entry.getValue(), entryBuilder);
		// mapBuilder.append(entryBuilder);
		// }
		// mapBuilder.append("\n\t</entries>\n" + "</map>");

		// try (FileWriter fw = new FileWriter(m_filename)) {
		// fw.write(mapBuilder.toString());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		try {
			m_handle.clearContents();

			m_handle.write(MAP_HEADER);

			for (Map.Entry<String, String> entry : m_entries.entrySet()) {
				String kv = String.format(ENTRY_TEMPLATE, entry.getKey(), entry.getValue());
				m_handle.write(kv);
			}

			m_handle.write(MAP_FOOTER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean wasGenerated() {
		return m_generated;
	}

	@Override
	public void close() throws Exception {
	}
}
