package com.starworks.kronos.serialization;

import static com.starworks.kronos.serialization.KronWriter.writeBytes;

import com.starworks.kronos.toolkit.crypto.Hash;

public class ClassSchema<T> {

    private final Class<T> m_type;
    private final int m_classIndex;
    
    private final int m_size; // non-synthetic non-transient fields

    private final ContainerType[] m_fieldContainerTypes; // primitive, primitive array, object, object array
    private final DataType[] m_dataTypes; // boolean, byte, short, char, int, long, float, double, string, object 

    protected ClassSchema(final Class<T> type) {
        this.m_type = type;
        this.m_classIndex = Hash.CRC32.hash(type.getName());
        this.m_size = serializableFieldCount(type);
        this.m_fieldContainerTypes = new ContainerType[m_size];
        this.m_dataTypes = new DataType[m_size];

        computeFields(type);
    }

    public int getMemoryLayout(final byte[] data, int pointer) {
        pointer = writeBytes(data, pointer, m_classIndex);
        pointer = writeBytes(data, pointer, m_size);

        for (int i = 0; i < m_size; i++) {
            pointer = writeBytes(data, pointer, (byte) m_fieldContainerTypes[i].ordinal());
            pointer = writeBytes(data, pointer, (byte) m_dataTypes[i].ordinal());
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

            m_fieldContainerTypes[j] = containerType;
            m_dataTypes[j++] = dataType;
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
        return m_type;
    }

    public int classIndex() {
        return m_classIndex;
    }

    public int size() {
        return m_size;
    }

    public ContainerType[] fieldContainerTypes() {
        return m_fieldContainerTypes;
    }

    public DataType[] dataTypes() {
        return m_dataTypes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String tab = "\s\s\s\s";
        sb.append("ClassSchema<").append(m_type.getSimpleName()).append("> {\n");
        sb.append(tab).append("classIndex: ").append(m_classIndex).append("\n");
        sb.append(tab).append("size: ").append(m_size).append("\n");
        
        for (int i = 0; i < m_fieldContainerTypes.length; i++) {
            sb.append(tab).append("field[").append(i).append("]: ");
            sb.append(m_fieldContainerTypes[i]).append(" ");

            String size = "" + m_dataTypes[i].size() + (m_fieldContainerTypes[i] == ContainerType.ARRAY ? " * array.length bytes)" : " bytes)");

            sb.append(m_dataTypes[i]).append(" (").append(size).append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
}
