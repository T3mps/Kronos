package net.acidfrog.kronos.core.io.serialization;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class KronWriter {

    public static boolean writeBytesRaw(String path, byte[] data) throws IOException {
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
        stream.write(data);
        stream.close();
        return true; // if we get here, we succeeded
    }
    
}
