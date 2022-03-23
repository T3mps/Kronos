package net.acidfrog.kronos.core.lang;

/**
 * Immutable reference to a type.
 * 
 * @author Ethan Temprovich
 */
public final class Reference<T> {
    
    /** The object reference. */
    private final T value;

    /**
     * Constructor with object reference.
     * 
     * @param value the object reference.
     */
    public Reference(T value) {
        this.value = value;
    }

    /**
     * @return the object reference.
     */
    public T get() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || value.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Reference] ");
        builder.append(value.toString());
        return builder.toString();
    }
    
}
