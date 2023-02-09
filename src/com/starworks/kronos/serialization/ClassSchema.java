package com.starworks.kronos.serialization;

import java.lang.reflect.Field;

public class ClassSchema<T> {

    private final T m_instance;
    private final Class<T> m_type;
    private final int m_classIndex;
    private final int m_size;
    private final ContainerType[] m_fieldContainerTypes;
    private final DataType[] m_dataTypes;

    @SuppressWarnings("unchecked")
	public ClassSchema(T instance) {
        this.m_instance = instance;
        this.m_type = (Class<T>) instance.getClass();
        this.m_classIndex = Serial.s_classMap.add(m_type);

        Field[] fields = m_type.getDeclaredFields();
        m_fieldContainerTypes = new ContainerType[fields.length];
        m_dataTypes = new DataType[fields.length];

        int size = 0;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Class<?> fieldType = field.getType();
            m_fieldContainerTypes[i] = ContainerType.get(fieldType);
            m_dataTypes[i] = DataType.get(fieldType);
            size += m_dataTypes[i].size();
        }
        m_size = size;
    }

    public T getInstance() {
        return m_instance;
    }

    public Class<T> getType() {
        return m_type;
    }

    public int getClassIndex() {
        return m_classIndex;
    }

    public int getSize() {
        return m_size;
    }

    public ContainerType[] getFieldContainerTypes() {
        return m_fieldContainerTypes;
    }

    public DataType[] getDataTypes() {
        return m_dataTypes;
    }
    
    public static void main(String[] args) {
        DataType booleanType = DataType.get(boolean.class);
        System.out.println("Data type for boolean: " + booleanType);

        ClassSchema<TestClass> schema = new ClassSchema<TestClass>(new TestClass());
        System.out.println("Class index: " + schema.getClassIndex());
        System.out.println("Class size: " + schema.getSize());
    }

    private static class TestClass {
        private boolean m_flag;
        private byte m_byteValue;
        private short m_shortValue;
        private char m_charValue;
        private int m_intValue;
        private long m_longValue;
        private float m_floatValue;
        private double m_doubleValue;
    }
}

