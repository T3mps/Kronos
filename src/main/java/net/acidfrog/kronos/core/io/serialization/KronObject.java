package net.acidfrog.kronos.core.io.serialization;

import static net.acidfrog.kronos.core.io.serialization.KronParser.*;

import java.util.ArrayList;
import java.util.List;

public class KronObject extends KronContainer {

	private short fieldCount;
	public List<KronField> fields = new ArrayList<KronField>();
	private short stringCount;
	public List<KronString> strings = new ArrayList<KronString>();
	private short arrayCount;
	public List<KronArray> arrays = new ArrayList<KronArray>();

    private KronObject() {
        super(KronContainer.Type.OBJECT);
    }

    public KronObject(String name) {
        super(KronContainer.Type.OBJECT);
        size += KronField.Type.sizeOf(KronField.Type.BYTE)  +
                KronField.Type.sizeOf(KronField.Type.SHORT) +
                KronField.Type.sizeOf(KronField.Type.SHORT) +
                KronField.Type.sizeOf(KronField.Type.SHORT);
        setName(name);
    }

    public KronField getField(String key) throws KronException {
        for (KronField field : fields) if (field.getName().equals(key)) return field;
        throw new KronException("Field not found: " + key);
    }

    public void addField(KronField field) {
        fields.add(field);
        size += field.size();

        fieldCount = (short) fields.size();
    }

    public KronString getString(String key) throws KronException {
        for (KronString string : strings) if (string.getName().equals(key)) return string;
        throw new KronException("String not found: " + key);
    }

    public void addString(KronString string) {
        strings.add(string);
        size += string.size();

        stringCount = (short) strings.size();
    }

    public KronArray getArray(String key) throws KronException {
        for (KronArray array : arrays) if (array.getName().equals(key)) return array;
        throw new KronException("Array not found: " + key);
    }

    public void addArray(KronArray array) {
        arrays.add(array);
        size += array.size();

        arrayCount = (short) arrays.size();
    }

    public int getBytes(byte[] dest, int pointer) throws KronException {
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);

        pointer = writeBytes(dest, pointer, fieldCount);
        for (KronField field : fields) pointer = field.getBytes(dest, pointer);
        pointer = writeBytes(dest, pointer, stringCount);
        for (KronString string : strings) pointer = string.getBytes(dest, pointer);
        pointer = writeBytes(dest, pointer, arrayCount);
        for (KronArray array : arrays) pointer = array.getBytes(dest, pointer);

        return pointer;
    }

    @Override
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return fields.isEmpty() && strings.isEmpty() && arrays.isEmpty();
    }
    
}
