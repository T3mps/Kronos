package net.acidfrog.kronos.scribe.callbacks;

import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import net.acidfrog.kronos.scribe.ANSI;
import net.acidfrog.kronos.scribe.Logger;

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

    /** The writer used for outputting to the console. */
    private BufferedWriter writer;

    /**
     * Default constructor.
     */
    public ConsoleLogCallback() {
        try {
            var fos     = new FileOutputStream(FileDescriptor.out);
            var osw     = new OutputStreamWriter(fos, "ASCII");
            this.writer = new BufferedWriter(osw, SIZE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Logger.Context ctx) {
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
