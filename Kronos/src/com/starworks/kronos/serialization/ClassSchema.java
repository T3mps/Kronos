package com.starworks.kronos.serialization;

import com.starworks.kronos.toolkit.crypto.CRC32;

import static com.starworks.kronos.serialization.KronWriter.*;

public class ClassSchema<T> {

    private final Class<T> type;
    private final int classIndex;
    
    private final int size; // non-synthetic non-transient fields

    private final ContainerType[] fieldContainerTypes; // primitive, primitive array, object, object array
    private final DataType[] dataTypes; // boolean, byte, short, char, int, long, float, double, string, object 

    protected ClassSchema(final Class<T> type) {
        this.type = type;
        this.classIndex = CRC32.hash(type.getName());
        this.size = serializableFieldCount(type);
        this.fieldContainerTypes = new ContainerType[size];
        this.dataTypes = new DataType[size];

        computeFields(type);
    }

    public int getMemoryLayout(final byte[] data, int pointer) {
        pointer = writeBytes(data, pointer, classIndex);
        pointer = writeBytes(data, pointer, size);

        for (int i = 0; i < size; i++) {
            pointer = writeBytes(data, pointer, (byte) fieldContainerTypes[i].ordinal());
            pointer = writeBytes(data, pointer, (byte) dataTypes[i].ordinal());
        }

        return pointer;
    }

    private void computeFields(Class<? extends Object> clazz) {
        var fieldsRaw = clazz.getDeclaredFields();
        int frLen = fieldsRaw.length;
        for (int i = 0, j = 0; i < frLen; i++) {
            var field = fieldsRaw[i];
            if (!Kron.isSerializable(field)) {
                continue;
            }

            var containerType = ContainerType.get(field.getType());
            var dataType = DataType.get(field.getType());

            if (containerType == null || dataType == null) {
                throw new IllegalArgumentException("Field " + field.getName() + " is not serializable");
            }

            fieldContainerTypes[j] = containerType;
            dataTypes[j++] = dataType;
        }
    }

    private int serializableFieldCount(Class<? extends Object> clazz) {
        int count = 0;

        while (clazz != Object.class) {
            var fields = clazz.getDeclaredFields();

            for (var field : fields) {
                if (Kron.isSerializable(field)) {
                    count++;
                }
            }

            clazz = clazz.getSuperclass();
        }

        return count;
    }

    public Class<T> type() {
        return type;
    }

    public int classIndex() {
        return classIndex;
    }

    public int size() {
        return size;
    }

    public ContainerType[] fieldContainerTypes() {
        return fieldContainerTypes;
    }

    public DataType[] dataTypes() {
        return dataTypes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String tab = "\s\s\s\s";
        sb.append("ClassSchema<").append(type.getSimpleName()).append("> {\n");
        sb.append(tab).append("classIndex: ").append(classIndex).append("\n");
        sb.append(tab).append("size: ").append(size).append("\n");
        
        for (int i = 0; i < fieldContainerTypes.length; i++) {
            sb.append(tab).append("field[").append(i).append("]: ");
            sb.append(fieldContainerTypes[i]).append(" ");

            String size = "" + dataTypes[i].size() + (fieldContainerTypes[i] == ContainerType.ARRAY ? " * array.length bytes)" : " bytes)");

            sb.append(dataTypes[i]).append(" (").append(size).append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
}
