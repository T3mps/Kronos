package net.acidfrog.kronos.core.files;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.logger.Logger;

public class Filek {

    protected File file;
    protected FilekType type;

    protected Filek() {}

    public Filek(String file) {
        this.file = new File(file);
        this.type = FilekType.ABSOLUTE;
    }

    protected Filek(String file, FilekType type) {
        this.file = new File(file);
        this.type = type;
    }

    protected Filek(File file, FilekType type) {
        this.file = file;
        this.type = type;
    }

    public InputStream read() {
		if (type == FilekType.CLASSPATH || (type == FilekType.INTERNAL && !file.exists())
			|| (type == FilekType.LOCAL && !file.exists())) {
			InputStream input = Filek.class.getResourceAsStream("/" + file.getPath().replace('\\', '/'));
			if (input == null) throw new KronosError(KronosErrorLibrary.FILE_NOT_FOUND);
			return input;
		}
		try {
			return new FileInputStream(file);
		} catch (Exception ex) {
			if (file.isDirectory()) throw new KronosError(KronosErrorLibrary.FILE_IS_DIRECTORY);
			throw new KronosError(KronosErrorLibrary.FILE_READ_ERROR);
		}
	}

    public BufferedInputStream read(int bufferSize) {
		return new BufferedInputStream(read(), bufferSize);
	}

    public String readString (String charset) {
		StringBuilder output = new StringBuilder(estimateLength());
		InputStreamReader reader = null;
		try {
			if (charset == null)
				reader = new InputStreamReader(read());
			else
				reader = new InputStreamReader(read(), charset);
			char[] buffer = new char[256];
			while (true) {
				int length = reader.read(buffer);
				if (length == -1) break;
				output.append(buffer, 0, length);
			}
		} catch (IOException e) {
			throw new KronosError(KronosErrorLibrary.FILE_READ_ERROR);
		} finally {
			try {
				reader.close();
			} catch (Throwable ignored) {
                Logger.instance.logWarn("Failed to close file reader.");
			}
		}
		return output.toString();
	}

    private int estimateLength () {
		int length = (int) length();
		return length != 0 ? length : 512;
	}

    public long length () {
		if (type == FilekType.CLASSPATH || (type == FilekType.INTERNAL && !file.exists())) {
			InputStream input = read();
			try {
				return input.available();
			} catch (Exception ignored) {
			} finally {
				try {
                    input.close();
                } catch (Throwable ignored) {
                    Logger.instance.logWarn("Failed to close input stream.");
                }
			}
			return 0;
		}
		return file.length();
	}

    public String path() {
        return file.getPath().replace('\\', '/');
    }

    public String name() {
        return file.getName();
    }

    public String parent() {
        return file.getParent();
    }

    public FilekType type() {
        return type;
    }

    public String extension() {
        return file.getName().substring(file.getName().lastIndexOf('.') + 1);
    }

    public String nameWithoutExtension() {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }

    public File getFile() {
        return file;
    }

    public Reader getReader() {
		return new InputStreamReader(read());
	}

    public Reader getReader(String charset) {
		InputStream stream = read();
		try {
			return new InputStreamReader(stream, charset);
		} catch (UnsupportedEncodingException e) {
			try {
				stream.close();
			} catch (Throwable ignored) {
                Logger.instance.logWarn("Failed to close stream after unsupported encoding exception: " + e.getMessage());
			}
		}
		throw new KronosError(KronosErrorLibrary.FILE_READ_ERROR);
	}
    
}
