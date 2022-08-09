package net.acidfrog.kronos.scribe.callbacks;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.acidfrog.kronos.scribe.ANSI;
import net.acidfrog.kronos.scribe.LogSettings;
import net.acidfrog.kronos.scribe.Logger;

/**
 * Implementation of {@link LogCallback} that writes to a file.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 */
public class FileLogCallback implements LogCallback {
    
    /** Holds the directory this callback outputs to. */
    private String directory;

    /** Holds the name of the file this callback outputs to. */
    private String fileName;

    /** The actual file. */
    private File file;

    /** The file stream. */
    private RandomAccessFile stream;

    /** The open channel of the stream, to manipulate/append data. */
    private FileChannel channel;

    /** Lock to ensure access is synchronized. */
    private FileLock lock;

    /**
     * Default constructor.
     */
    public FileLogCallback() {
        this(new SimpleDateFormat(LogSettings.FILE_DATE_FORMAT).format(Calendar.getInstance().getTime()));
    }

    /**
     * Constructs a callback with a user-defined file name.
     * 
     * @param fileName
     */
    public FileLogCallback(String fileName) {
        this.directory = LogSettings.FILE_OUTPUT_DIRECTORY;
        this.fileName = fileName.endsWith(LogSettings.FILE_EXTENSION) ? fileName : fileName + LogSettings.FILE_EXTENSION;
        this.file = new File(directory + this.fileName);
        
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file: " + file.getAbsolutePath() + ".\n" + e.getMessage());
            }
        }
        
        try {
            this.stream = new RandomAccessFile(file, "rw");
        } catch (IOException e) {
            System.err.println("Error creating stream: " + file.getAbsolutePath() + ".\n" + e.getMessage());
        }

        this.channel = stream.getChannel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Logger.Context ctx) {
        try {
            try {
                lock = channel.tryLock();
            } catch (final OverlappingFileLockException e) {
                System.err.println("Error locking file: " + file.getAbsolutePath() + ", the file is most likely already being accessed by another process.\n" + e.getMessage());
                return;
            }
            
            stream.seek(stream.length());
            stream.writeChars(ctx.message());
            stream.writeChars(ANSI.NEWLINE);
            
            lock.release();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + file.getAbsolutePath() + ".\n" + e.getMessage());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            log(new Logger.Context(null, "", null, null));
            channel.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
