package net.acidfrog.kronos.mathk.jre.java.io;

import java.io.IOException;

public interface DataOutput {
    void writeInt(int value) throws IOException;
    void writeFloat(float value) throws IOException;
    void writeDouble(double value) throws IOException;
}
