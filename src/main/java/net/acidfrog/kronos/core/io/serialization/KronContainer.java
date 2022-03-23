package net.acidfrog.kronos.core.io.serialization;

public abstract class KronContainer {

    public enum Type {
    
        UNKNOWN((byte) 0), FIELD((byte) 1), ARRAY((byte) 2), STRING((byte) 3), OBJECT((byte) 4), DATABASE((byte) 5);
    
        public final byte value;
    
        private Type(byte value) {
            this.value = value;
        }
    }

    protected final byte CONTAINER_TYPE;
    protected short nameLength;
    protected byte[] name;

    protected int size = 2 + 4;

    public KronContainer(Type type) {
        this.CONTAINER_TYPE = type.value;
    }

    public Type getType() {
        return Type.values()[CONTAINER_TYPE];
    }

    public String getName() { return new String(name, KronParser.DEFAULT_POINTER, nameLength); }
    
    public void setName(String name) {        
        if (this.name != null) size -= this.name.length;

        this.nameLength = (short) name.length();
        this.name = name.getBytes();

        size += nameLength;
    }

    public abstract int size();

}
