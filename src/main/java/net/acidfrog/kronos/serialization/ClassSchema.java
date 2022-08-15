package net.acidfrog.kronos.serialization;

import net.acidfrog.kronos.toolkit.crypto.CRC32;

public class ClassSchema<T> {

    private final Class<T> type;
    private final int classIndex;
    
    private final int size; // non-synthetic non-transient fields
    private final int rawSize; // all fields

    private final ContainerType[] fieldContainerTypes; // primitive, primitive array, object, object array
    private final DataType[] dataTypes; // boolean, byte, short, char, int, long, float, double, string, object

    protected ClassSchema(Class<T> type) {
        this.type = type;
        this.classIndex = CRC32.hash(type.getName());
        
        var fields = type.getDeclaredFields();
        int rawFieldCount = 0;
        int fieldCount = 0;
        for (var field : fields) {
            rawFieldCount++;
            if (!Kron.isSerializable(field)) {
                continue;
            }

            fieldCount++;
        }

        this.size = fieldCount;
        this.rawSize = rawFieldCount;

        this.fieldContainerTypes = new ContainerType[fieldCount];
        this.dataTypes = new DataType[fieldCount];

        for (int i = 0, j = 0; i < fields.length; i++) {
            var field = fields[i];
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

    public Class<T> type() {
        return type;
    }

    public int classIndex() {
        return classIndex;
    }

    public int size() {
        return size;
    }

    public int rawSize() {
        return rawSize;
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
        sb.append(tab).append("rawSize: ").append(rawSize).append("\n");
        
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
