package net.acidfrog.kronos.math.jre.java.io;

import java.io.IOException;

public interface Externalizable extends java.io.Serializable {
    void writeExternal(ObjectOutput out) throws IOException;
    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;
}
