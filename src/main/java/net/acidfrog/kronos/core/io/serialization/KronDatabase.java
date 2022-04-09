package net.acidfrog.kronos.core.io.serialization;

import static net.acidfrog.kronos.core.io.serialization.KronParser.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.core.Config;

public class KronDatabase extends KronContainer {

    private short objectCount;
    public List<KronObject> objects = new ArrayList<KronObject>();

    public KronDatabase() {
        super(KronContainer.Type.DATABASE);
    }

    public KronDatabase(String name) {
        super(KronContainer.Type.DATABASE);
        setName(name);

        size += Kron.HEADER.length                          +
                KronField.Type.sizeOf(KronField.Type.SHORT) +
                KronField.Type.sizeOf(KronField.Type.BYTE)  +
                KronField.Type.sizeOf(KronField.Type.SHORT);
    }

    public KronObject getObject(String key) throws KronException {
        for (KronObject object : objects) if (object.getName().equals(key)) return object;
        throw new KronException("Object not found: " + key);
    }

    public void addObject(KronObject object) {
        objects.add(object);
        size += object.size();

        objectCount = (short) objects.size();
    }

    public int getBytes(byte[] dest, int pointer) throws KronException {
        pointer = writeBytes(dest, pointer, Kron.HEADER);
        pointer = writeBytes(dest, pointer, Kron.VERSION);
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);

        pointer = writeBytes(dest, pointer, objectCount);
        for (KronObject object : objects) pointer = object.getBytes(dest, pointer);

        return pointer;
    }

    public void serializeToFile(String path) throws KronException {
        if (!path.contains(Kron.DOT_EXTENSION)) path += Kron.DOT_EXTENSION;
        path = Config.getInstance().getString("kronos.kronSavePath") + path;
        
		byte[] data = new byte[size()];
		getBytes(data, 0);
        
		try {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
			stream.write(data);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @Override
    public int size() {
        return size;
    }
    
}
