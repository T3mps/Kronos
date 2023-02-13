package com.starworks.kronos.files;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class FileSystem {

	public static final char separatorChar = '/';
	public static final String separator = "" + separatorChar;

	private static final FileTree s_tree = new FileTree();

	private FileSystem() {}

	public static FileHandle getFileHandle(String fileName) throws IOException {
		FileHandle handle = s_tree.findOrCreate(fileName);
		return handle;
	}

	public static boolean removeFileHandle(String fileName) throws IOException {
		FileHandle fileHandle = s_tree.find(fileName);
		if (fileHandle != null) {
			s_tree.remove(fileName);
			return true;
		}
		return false;
	}

	public static void closeFileHandle(String fileName) throws IOException {
		FileHandle fileHandle = s_tree.find(fileName);
		if (fileHandle != null && !fileHandle.isClosed()) {
			s_tree.remove(fileName);
		}
	}

	public static void shutdown() {
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

	public static void writeTree(OutputStream stream) throws IOException {
		stream.write(stringify().getBytes());
		stream.flush();
	}

	public static String stringify() {
		return s_tree.toString();
	}
}
