package net.acidfrog.kronos.core.io.serialization;

import static net.acidfrog.kronos.core.io.serialization.KronParser.*;

public class KronArray extends KronContainer {

    public byte type;
	public int count;
	public byte[] data;
	
	private boolean[] booleanData;
	private short[] shortData;
	private char[] charData;
	private int[] intData;
	private long[] longData;
	private float[] floatData;
	private double[] doubleData;

    private KronArray() {
        super(KronContainer.Type.ARRAY);
        this.size += KronField.Type.sizeOf(KronField.Type.BYTE) +
                     KronField.Type.sizeOf(KronField.Type.BYTE) +
                     KronField.Type.sizeOf(KronField.Type.INT);
    }

    public static KronArray create(String name, Object[] value) throws KronException {
        KronArray array = new KronArray();
        Class<?> clazz = value.getClass();

        if (clazz.equals(Boolean.TYPE)) {
            final boolean[] primatives = new boolean[value.length];
            Boolean[] objects = (Boolean[]) value;
            int index = 0;
            for (Boolean object : objects) primatives[index++] = object.booleanValue();

            array = create(name, primatives);
        } 
        else throw new KronException("Invalid type: " + value.getClass().getName());
        
        return array;
    }

    public static KronArray create(String name, boolean[] data) throws KronException {
        KronArray array = new KronArray();
        array.setName(name);
        array.type = KronField.Type.BOOLEAN.value;
        array.count = data.length;
        array.booleanData = data;
        array.updateSize();
        return array;
    }

    public static KronArray create(String name, byte[] data) throws KronException {
        KronArray array = new KronArray();
        array.setName(name);
        array.type = KronField.Type.BYTE.value;
        array.count = data.length;
        array.data = data;
        array.updateSize();
        return array;
    }

    public static KronArray create(String name, char[] data) throws KronException {
        KronArray array = new KronArray();
        array.setName(name);
        array.type = KronField.Type.CHAR.value;
        array.count = data.length;
        array.charData = data;
        array.updateSize();
        return array;
    }

    public static KronArray create(String name, short[] data) throws KronException {
        KronArray array = new KronArray();
        array.setName(name);
        array.type = KronField.Type.SHORT.value;
        array.count = data.length;
        array.shortData = data;
        array.updateSize();
        return array;
    }

    public static KronArray create(String name, int[] data) throws KronException {
        KronArray array = new KronArray();
        array.setName(name);
        array.type = KronField.Type.INT.value;
        array.count = data.length;
        array.intData = data;
        array.updateSize();
        return array;
    }

    public static KronArray create(String name, long[] data) throws KronException {
        KronArray array = new KronArray();
        array.setName(name);
        array.type = KronField.Type.LONG.value;
        array.count = data.length;
        array.longData = data;
        array.updateSize();
        return array;
    }

    public static KronArray create(String name, float[] data) throws KronException {
        KronArray array = new KronArray();
        array.setName(name);
        array.type = KronField.Type.FLOAT.value;
        array.count = data.length;
        array.floatData = data;
        array.updateSize();
        return array;
    }

    public static KronArray create(String name, double[] data) throws KronException {
        KronArray array = new KronArray();
        array.setName(name);
        array.type = KronField.Type.DOUBLE.value;
        array.count = data.length;
        array.doubleData = data;
        array.updateSize();
        return array;
    }

    public int getBytes(byte[] dest, int pointer) throws KronException {
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, size);
		pointer = writeBytes(dest, pointer, type);
		pointer = writeBytes(dest, pointer, count);
		
		switch(KronField.Type.getType(type)) {
            case BOOLEAN: pointer = writeBytes(dest, pointer, booleanData); break;
            case BYTE:    pointer = writeBytes(dest, pointer, data);        break;
            case CHAR:    pointer = writeBytes(dest, pointer, charData);    break;
            case SHORT:   pointer = writeBytes(dest, pointer, shortData);   break;
            case INT:     pointer = writeBytes(dest, pointer, intData);     break;
            case LONG:    pointer = writeBytes(dest, pointer, longData);    break;
            case FLOAT:   pointer = writeBytes(dest, pointer, floatData);   break;
            case DOUBLE:  pointer = writeBytes(dest, pointer, doubleData);  break;
            default: throw new KronException("Unknown array type: " + type);
		}

		return pointer;
	}

    public int getDataSize() throws KronException {
		switch(KronField.Type.getType(type)) {
            case BOOLEAN:	return booleanData.length * KronField.Type.sizeOf(KronField.Type.BOOLEAN);
		    case BYTE:      return data.length        * KronField.Type.sizeOf(KronField.Type.BYTE);
            case CHAR:		return charData.length    * KronField.Type.sizeOf(KronField.Type.CHAR);
            case SHORT:	    return shortData.length   * KronField.Type.sizeOf(KronField.Type.SHORT);
            case INT:       return intData.length     * KronField.Type.sizeOf(KronField.Type.INT);
            case LONG:		return longData.length    * KronField.Type.sizeOf(KronField.Type.LONG);
            case FLOAT:	    return floatData.length   * KronField.Type.sizeOf(KronField.Type.FLOAT);
            case DOUBLE:	return doubleData.length  * KronField.Type.sizeOf(KronField.Type.DOUBLE);
            default: throw new KronException("Unknown array type: " + type);
		}
	}

    private void updateSize() throws KronException {
		size += getDataSize();
	}

    @Override
    public int size() {
        return size;
    }


    
}
