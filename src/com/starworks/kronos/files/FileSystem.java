package com.starworks.kronos.files;

import java.io.IOException;
import java.util.Iterator;

public class FileSystem {

	public static final char separatorChar = '/';
	public static final String separator = "" + separatorChar;

	private static final FileTree s_tree = new FileTree();
	
	private FileSystem() {}
	
	public static FileHandle getFileHandle(String fileName) throws IOException {
		FileHandle handle = s_tree.findOrCreate(fileName);
		handle.m_consumers.incrementAndGet();
		return handle;
	}

	public static boolean removeFileHandle(String fileName) throws IOException {
		FileHandle fileHandle = s_tree.find(fileName);
		if (fileHandle != null) {
			if (fileHandle.m_consumers.decrementAndGet() == 0) {
				fileHandle.close();
				s_tree.remove(fileName);
				return true;
			}
		}
		return false;
	}

	public static void closeFileHandle(String fileName) throws IOException {
		FileHandle fileHandle = s_tree.find(fileName);
		if (fileHandle != null && !fileHandle.isClosed()) {
			fileHandle.close();
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
}
