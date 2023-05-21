package com.starworks.kronos;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.management.InstanceAlreadyExistsException;

import com.starworks.kronos.files.FileHandle;
import com.starworks.kronos.files.FileSystem;
import com.starworks.kronos.logging.Layout;
import com.starworks.kronos.logging.Level;
import com.starworks.kronos.toolkit.concurrent.ArrivalGate;
import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

public final class Configuration {

	public static RuntimeData runtime;
	public static WindowData window;
	public static LoggingData logging;
	public static JobsData jobs;
	public static MathData math;

	private static final ArrivalGate s_gate = new ArrivalGate(1);

	public static void load(String path) throws InstanceAlreadyExistsException, IllegalStateException {
		try {
			s_gate.arrive();
		} catch (InterruptedException e) {
			throw new InstanceAlreadyExistsException();
		}
		
		tryConfiguration(path);

		try {
			VTDGen vg = new VTDGen();
			if (vg.parseFile(path, true)) {
				VTDNav vn = vg.getNav();
				AutoPilot ap = new AutoPilot(vn);

				int version = -1;
				// version
				ap.selectXPath("//application/@version");
				if (ap.evalXPath() != -1) {
					version = Integer.parseInt(vn.toString(vn.getAttrVal("version")));
				}
				
				if (Version.getVersion() != version) {
					throw new IllegalStateException("Version of config file does not match version of engine!");
				}

				// runtime
				int updatesPerSecond = 60;
				double updateRate = 1.0 / (double) updatesPerSecond;
				int fixedUpdatesPerSecond = 60;
				double fixedUpdateRate = 1.0 / (double) fixedUpdatesPerSecond;
				boolean debug = false;
				String workingDirectory = "";
				String applicationImplementation = null;
				ap.selectXPath("//application/runtime/updatesPerSecond");
				if (ap.evalXPath() != -1) {
					updatesPerSecond = Integer.parseInt(vn.toString(vn.getText()));
					updateRate = 1.0 / (double) updatesPerSecond;
				}
				ap.selectXPath("//application/runtime/fixedUpdatesPerSecond");
				if (ap.evalXPath() != -1) {
					fixedUpdatesPerSecond = Integer.parseInt(vn.toString(vn.getText()));
					fixedUpdateRate = 1.0 / (double) fixedUpdatesPerSecond;
				}
				ap.selectXPath("//application/runtime/debug");
				if (ap.evalXPath() != -1) {
					debug = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/runtime/workingDirectory");
				if (ap.evalXPath() != -1) {
					workingDirectory = vn.toString(vn.getText());
					FileSystem.INSTANCE.setWorkingDirectory(workingDirectory);
				}
				ap.selectXPath("//application/@implementation");
				if (ap.evalXPath() != -1) {
					applicationImplementation = vn.toString(vn.getAttrVal("implementation"));
				}
				runtime = new RuntimeData(applicationImplementation, updatesPerSecond, updateRate, fixedUpdatesPerSecond, fixedUpdateRate, debug, workingDirectory);

				// window
				String windowTitle = null;
				int windowWidth = 0;
				int windowHeight = 0;
				boolean windowFullscreen = false;
				boolean windowVsync = false;
				ap.selectXPath("//application/window/title");
				if (ap.evalXPath() != -1) {
					windowTitle = vn.toString(vn.getText());
				}
				ap.selectXPath("//application/window/width");
				if (ap.evalXPath() != -1) {
					windowWidth = Integer.parseInt(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/window/height");
				if (ap.evalXPath() != -1) {
					windowHeight = Integer.parseInt(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/window/fullscreen");
				if (ap.evalXPath() != -1) {
					windowFullscreen = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/window/vsync");
				if (ap.evalXPath() != -1) {
					windowVsync = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				window = new WindowData(windowTitle, windowWidth, windowHeight, windowFullscreen, windowVsync);

				// logging
				String loggingImpl = null;
				String loggingName = null;
				Level loggingLevel = null;
				Layout loggingLayout = null;
				String loggingDirectory = null;
				String loggingExtension = null;
				String loggingBackupExtension = null;
				int loggingMaxRotatingFileLineCount = 0;
				boolean loggingLogToConsole = false;
				boolean loggingLogToFile = false;
				boolean loggingAnsiFormatting = false;

				ap.selectXPath("//application/logging/name");
				if (ap.evalXPath() != -1) {
					loggingName = vn.toString(vn.getText());
				}
				ap.selectXPath("//application/logging/level");
				if (ap.evalXPath() != -1) {
					loggingLevel = Level.of(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/logging/layout");
				if (ap.evalXPath() != -1) {
					loggingLayout = Layout.of(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/logging/directory");
				if (ap.evalXPath() != -1) {
					loggingDirectory = vn.toString(vn.getText());
				}
				ap.selectXPath("//application/logging/extension");
				if (ap.evalXPath() != -1) {
					loggingExtension = vn.toString(vn.getText());
				}
				ap.selectXPath("//application/logging/backupExtension");
				if (ap.evalXPath() != -1) {
					loggingBackupExtension = vn.toString(vn.getText());
				}
				ap.selectXPath("//application/logging/maxRotatingFileLineCount");
				if (ap.evalXPath() != -1) {
					loggingMaxRotatingFileLineCount = Integer.parseInt(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/logging/logToConsole");
				if (ap.evalXPath() != -1) {
					loggingLogToConsole = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/logging/logToFile");
				if (ap.evalXPath() != -1) {
					loggingLogToFile = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/logging/ansiFormatting");
				if (ap.evalXPath() != -1) {
					loggingAnsiFormatting = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/logging/@implementation");
				if (ap.evalXPath() != -1) {
					loggingImpl = vn.toString(vn.getAttrVal("implementation"));
				}
				logging = new LoggingData(loggingImpl, loggingName, loggingLevel, loggingLayout, loggingDirectory, loggingExtension, loggingBackupExtension, loggingMaxRotatingFileLineCount, loggingLogToConsole, loggingLogToFile, loggingAnsiFormatting);

				// jobs
				int jobsTimeoutSeconds = 0;
				int jobsShutdownTimeoutSeconds = 0;
				int jobsUpdatesPerSecond = 0;
				ap.selectXPath("//application/jobs/timeoutSeconds");
				if (ap.evalXPath() != -1) {
					jobsTimeoutSeconds = Integer.parseInt(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/jobs/shutdownTimeoutSeconds");
				if (ap.evalXPath() != -1) {
					jobsShutdownTimeoutSeconds = Integer.parseInt(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/jobs/updatesPerSecond");
				if (ap.evalXPath() != -1) {
					jobsUpdatesPerSecond = Integer.parseInt(vn.toString(vn.getText()));
				}
				jobs = new JobsData(jobsTimeoutSeconds, jobsShutdownTimeoutSeconds, jobsUpdatesPerSecond);

				// math
				boolean mathDebug = false;
				boolean mathNoUnsafe = false;
				boolean mathForceUnsafe = false;
				boolean mathFastmath = false;
				boolean mathSinLookup = false;
				int mathSinLookupBits = 0;
				boolean mathUseNumberFormat = false;
				boolean mathUseMathFMA = false;
				int mathNumberFormatBigDecimals = 0;
				ap.selectXPath("//application/math/debug");
				if (ap.evalXPath() != -1) {
					mathDebug = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/math/noUnsafe");
				if (ap.evalXPath() != -1) {
					mathNoUnsafe = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/math/forceUnsafe");
				if (ap.evalXPath() != -1) {
					mathForceUnsafe = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/math/fastmath");
				if (ap.evalXPath() != -1) {
					mathFastmath = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/math/sinLookup");
				if (ap.evalXPath() != -1) {
					mathSinLookup = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/math/sinLookupBits");
				if (ap.evalXPath() != -1) {
					mathSinLookupBits = Integer.parseInt(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/math/useNumberFormat");
				if (ap.evalXPath() != -1) {
					mathUseNumberFormat = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/math/useMathFMA");
				if (ap.evalXPath() != -1) {
					mathUseMathFMA = Boolean.parseBoolean(vn.toString(vn.getText()));
				}
				ap.selectXPath("//application/math/numberFormatBigDecimals");
				if (ap.evalXPath() != -1) {
					mathNumberFormatBigDecimals = Integer.parseInt(vn.toString(vn.getText()));
				}
				NumberFormat numberFormat;
				{
					if (mathUseNumberFormat) {
						char[] prec = new char[mathNumberFormatBigDecimals];
						Arrays.fill(prec, '0');
						numberFormat = new DecimalFormat(" 0." + new String(prec) + "E0;-");
					} else {
						numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
						numberFormat.setGroupingUsed(false);
					}
				}
				math = new MathData(mathDebug, mathNoUnsafe, mathForceUnsafe, mathFastmath, mathSinLookup, mathSinLookupBits, mathUseNumberFormat, mathUseMathFMA, mathNumberFormatBigDecimals, numberFormat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void tryConfiguration(String path) {
		String xsdPath = path.substring(0, path.lastIndexOf(".")) + ".xsd";
		try {
			FileHandle handle = FileSystem.INSTANCE.getFileHandle(xsdPath, true, true);
			if (handle.wasGenerated()) {
				String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
						"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\n" + //
						"\t<xs:element name=\"application\">\n" + //
						"\t\t<xs:complexType>\n" + //
						"\t\t\t<xs:sequence>\n" + //
						"\t\t\t\t<xs:element name=\"runtime\" type=\"runtimeType\" />\n" + //
						"\t\t\t\t<xs:element name=\"window\" type=\"windowType\" />\n" + //
						"\t\t\t\t<xs:element name=\"logging\" type=\"loggingType\" />\n" + //
						"\t\t\t\t<xs:element name=\"jobs\" type=\"jobsType\" />\n" + //
						"\t\t\t\t<xs:element name=\"math\" type=\"mathType\" />\n" + //
						"\t\t\t</xs:sequence>\n" + //
						"\t\t\t<xs:attribute name=\"implementation\" type=\"xs:string\" use=\"required\" />\n" + //
						"\t\t\t<xs:attribute name=\"version\" type=\"xs:integer\" use=\"required\" />\n" + //
						"\t\t</xs:complexType>\n" + //
						"\t</xs:element>\n" + //
						"\t<xs:complexType name=\"runtimeType\">\n" + //
						"\t\t<xs:sequence>\n" + //
						"\t\t\t<xs:element name=\"updatesPerSecond\" type=\"xs:integer\" />\n" + //
						"\t\t\t<xs:element name=\"fixedUpdatesPerSecond\" type=\"xs:integer\" />\n" + //
						"\t\t\t<xs:element name=\"debug\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"workingDirectory\" type=\"xs:string\" />\n" + //
						"\t\t</xs:sequence>\n" + //
						"\t</xs:complexType>\n" + //
						"\t<xs:complexType name=\"windowType\">\n" + //
						"\t\t<xs:sequence>\n" + //
						"\t\t\t<xs:element name=\"title\" type=\"xs:string\" />\n" + //
						"\t\t\t<xs:element name=\"width\" type=\"xs:integer\" />\n" + //
						"\t\t\t<xs:element name=\"height\" type=\"xs:integer\" />\n" + //
						"\t\t\t<xs:element name=\"fullscreen\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"vsync\" type=\"xs:boolean\" />\n" + //
						"\t\t</xs:sequence>\n" + //
						"\t</xs:complexType>\n" + //
						"\t<xs:complexType name=\"loggingType\">\n" + //
						"\t\t<xs:sequence>\n" + //
						"\t\t\t<xs:element name=\"name\" type=\"xs:string\" />\n" + //
						"\t\t\t<xs:element name=\"level\" type=\"xs:string\" />\n" + //
						"\t\t\t<xs:element name=\"layout\" type=\"xs:string\" />\n" + //
						"\t\t\t<xs:element name=\"directory\" type=\"xs:string\" />\n" + //
						"\t\t\t<xs:element name=\"extension\" type=\"xs:string\" />\n" + //
						"\t\t\t<xs:element name=\"backupExtension\" type=\"xs:string\" />\n" + //
						"\t\t\t<xs:element name=\"maxRotatingFileLineCount\" type=\"xs:integer\" />\n" + //
						"\t\t\t<xs:element name=\"logToConsole\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"logToFile\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"ansiFormatting\" type=\"xs:boolean\" />\n" + //
						"\t\t</xs:sequence>\n" + //
						"\t\t<xs:attribute name=\"implementation\" type=\"xs:string\" use=\"required\" />\n" + //
						"\t</xs:complexType>\n" + //
						"\t<xs:complexType name=\"jobsType\">\n" + //
						"\t\t<xs:sequence>\n" + //
						"\t\t\t<xs:element name=\"timeoutSeconds\" type=\"xs:integer\" />\n" + //
						"\t\t\t<xs:element name=\"shutdownTimeoutSeconds\" type=\"xs:integer\" />\n" + //
						"\t\t\t<xs:element name=\"updatesPerSecond\" type=\"xs:integer\" />\n" + //
						"\t\t</xs:sequence>\n" + //
						"\t</xs:complexType>\n" + //
						"\t<xs:complexType name=\"mathType\">\n" + //
						"\t\t<xs:sequence>\n" + //
						"\t\t\t<xs:element name=\"debug\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"noUnsafe\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"forceUnsafe\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"fastmath\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"sinLookup\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"sinLookupBits\" type=\"xs:integer\" />\n" + //
						"\t\t\t<xs:element name=\"useNumberFormat\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"useMathFMA\" type=\"xs:boolean\" />\n" + //
						"\t\t\t<xs:element name=\"numberFormatBigDecimals\" type=\"xs:integer\" />\n" + //
						"\t\t</xs:sequence>\n" + //
						"\t</xs:complexType>\n" + //
						"</xs:schema>";
				handle.write(xsd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileHandle handle = FileSystem.INSTANCE.getFileHandle(path, true, true);
			if (handle.wasGenerated()) {
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
						"<application implementation=\"\" version=\"" + Version.getVersion() + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"" + xsdPath + "\">\n" + //
						"\t<runtime>\n" + //
						"\t\t<updatesPerSecond>60</updatesPerSecond>\n" + //
						"\t\t<fixedUpdatesPerSecond>60</fixedUpdatesPerSecond>\n" + //
						"\t\t<debug>true</debug>\n" + //
						"\t\t<workingDirectory>" + FileSystem.INSTANCE.getDefaultWorkingDirectory() + "</workingDirectory>\n" + //
						"\t</runtime>\n" + //
						"\t<window>\n" + //
						"\t\t<title>Kronos</title>\n" + //
						"\t\t<width>1280</width>\n" + //
						"\t\t<height>720</height>\n" + //
						"\t\t<fullscreen>false</fullscreen>\n" + //
						"\t\t<vsync>true</vsync>\n" + //
						"\t</window>\n" + //
						"\t<logging implementation=\"com.starworks.kronos.logging.ConcurrentLogger\">\n" + //
						"\t\t<name>Main</name>\n" + //
						"\t\t<level>ALL</level>\n" + //
						"\t\t<layout>full</layout>\n" + //
						"\t\t<directory>logs/</directory>\n" + //
						"\t\t<extension>.log</extension>\n" + //
						"\t\t<backupExtension>.bak</backupExtension>\n" + //
						"\t\t<maxRotatingFileLineCount>10000</maxRotatingFileLineCount>\n" + //
						"\t\t<logToConsole>true</logToConsole>\n" + //
						"\t\t<logToFile>true</logToFile>\n" + //
						"\t\t<ansiFormatting>true</ansiFormatting>\n" + //
						"\t</logging>\n" + //
						"\t<jobs>\n" + //
						"\t\t<timeoutSeconds>5</timeoutSeconds>\n" + //
						"\t\t<shutdownTimeoutSeconds>2</shutdownTimeoutSeconds>\n" + //
						"\t\t<updatesPerSecond>60</updatesPerSecond>\n" + //
						"\t</jobs>\n" + //
						"\t<math>\n" + //
						"\t\t<debug>false</debug>\n" + //
						"\t\t<noUnsafe>false</noUnsafe>\n" + //
						"\t\t<forceUnsafe>true</forceUnsafe>\n" + //
						"\t\t<fastmath>false</fastmath>\n" + //
						"\t\t<sinLookup>false</sinLookup>\n" + //
						"\t\t<sinLookupBits>14</sinLookupBits>\n" + //
						"\t\t<useNumberFormat>true</useNumberFormat>\n" + //
						"\t\t<useMathFMA>true</useMathFMA>\n" + //
						"\t\t<numberFormatBigDecimals>3</numberFormatBigDecimals>\n" + //
						"\t</math>\n" + //
						"</application>";
				handle.write(xml);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final record RuntimeData(String implementation, int updatesPerSecond, double updateRate, int fixedUpdatesPerSecond, double fixedUpdateRate, boolean debug, String workingDirectory) {
	}

	public final record WindowData(String title, int width, int height, boolean fullscreen, boolean vsync) {
	}

	public final record LoggingData(String implementation, String name, Level level, Layout layout, String directory, String extension, String backupExtension, int maxRotatingFileLineCount, boolean logToConsole, boolean logToFile, boolean ansiFormatting) {
	}

	public final record JobsData(int timeoutSeconds, int shutdownTimeoutSeconds, int updatesPerSecond) {
	}

	public final record MathData(boolean debug, boolean noUnsafe, boolean forceUnsafe, boolean fastmath, boolean sinLookup, int sinLookupBits, boolean useNumberFormat, boolean useMathFMA, int numberFormatBigDecimals, NumberFormat numberFormat) {
	}
}
