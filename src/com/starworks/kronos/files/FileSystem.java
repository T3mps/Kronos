package com.starworks.kronos.files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import com.starworks.kronos.toolkit.SystemInfo;

public enum FileSystem {

	INSTANCE;

	public static final char separatorChar = '/';
	public static final String separator = "" + separatorChar;

	private static final FileTree s_tree = new FileTree();

	private String m_workingDirectory = getDefaultWorkingDirectory();

	private FileSystem() {
	}

	public FileHandle getFileHandle(String fileName, boolean createDirectories, boolean generateIfNotExist) throws IOException {
		if (createDirectories) {
			File file = new File(fileName);
			String parent = file.getParent();
			if (parent == null) {
				parent = "";
			}
			File directory = new File(parent);
			if (!directory.exists()) {
				directory.mkdirs();
			}
		}
		FileHandle handle = s_tree.find(fileName, generateIfNotExist);
		return handle;
	}

	public boolean removeFileHandle(String fileName) throws IOException {
		FileHandle fileHandle = s_tree.find(fileName);
		if (fileHandle != null) {
			s_tree.remove(fileName);
			return true;
		}
		return false;
	}

	public void closeFileHandle(String fileName) throws IOException {
		FileHandle fileHandle = s_tree.find(fileName);
		if (fileHandle != null && !fileHandle.isClosed()) {
			s_tree.remove(fileName);
		}
	}

	public void shutdown() {
		Iterator<FileHandle> iterator = s_tree.iterator();
		while (iterator.hasNext()) {
			FileHandle fileHandle = iterator.next();
			if (!fileHandle.isClosed()) {
				try {
					fileHandle.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void writeTree(OutputStream stream) throws IOException {
		stream.write(stringify().getBytes());
		stream.flush();
	}

	public String stringify() {
		return s_tree.toString();
	}

	public String get(String filepath) {
		return m_workingDirectory + separator + filepath;
	}

	public String getDefaultWorkingDirectory() {
		String wkdir = SystemInfo.getUserHome() + "\\AppData\\Roaming\\Kronos\\";
		return wkdir.replace("\\\\", separator).replace("\\", separator);
	}

	public String getWorkingDirectory() {
		return m_workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		if (workingDirectory != null) {
			m_workingDirectory = workingDirectory.replace("\\\\", separator).replace("\\", separator);
			if (!m_workingDirectory.endsWith(separator)) {
				m_workingDirectory += separator;
			}
		}
	}
}
