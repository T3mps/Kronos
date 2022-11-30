package com.starworks.kronos.logging.callbacks;

import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.starworks.kronos.logging.ANSI;
import com.starworks.kronos.logging.Logger;

/**
 * Implementation of {@link LogCallback} that writes to the console.
 * <p>
 * Contains a specialized, fast way of writing to the console.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 */
public class ConsoleLogCallback implements LogCallback {
    
    /** Defines the size of the buffer used by the {@link #writer}.  */
    private static final short SIZE = 512;

    /** The buffer used to write. */
    private byte[] buffer;

    /** The current pointer in the buffer. */
    private int index;
    
    /** The writer used for outputting to the console. */
    private BufferedWriter writer;
    
    /** Weather or not to wait until the 512 byte buffer is full to push a message to the console. */
    private boolean buffered;
    
    public ConsoleLogCallback(boolean buffered) {
        this.buffer = new byte[SIZE];
        this.index = 0;

        try {
            var fos     = new FileOutputStream(FileDescriptor.out);
            var osw     = new OutputStreamWriter(fos, "ASCII");
            this.writer = new BufferedWriter(osw, SIZE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.buffered = buffered;
    }

    /**
     * Default constructor.
     */
    public ConsoleLogCallback() {
        this(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Logger.Context ctx) {
        if (buffered) {
            index += ctx.message().length();
            if (index >= SIZE) {
                logInternal(ctx);
                index = 0;
            } else {
                System.arraycopy(ctx.message().getBytes(), 0, buffer, index, ctx.message().length());
            }
        } else {
            logInternal(ctx);
        }
    }

    private void logInternal(Logger.Context ctx) {
        try {
            writer.write(ANSI.colorize(ctx.message(), ctx.level().getForegroundColor(), ctx.level().getBackgroundColor()));
            writer.write(System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to console: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("Error closing console: " + e.getMessage());
        }
    }
}
