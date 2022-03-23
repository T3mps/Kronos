package net.acidfrog.kronos.physics.collision;

public final class LayerMask implements Filter {
    
    public static final long MASK_ALL = Long.MAX_VALUE;

    private final long layer;

    private final long mask;

    public LayerMask() {
        this(1, MASK_ALL);
    }

    public LayerMask(long layer) {
        this(layer, MASK_ALL);
    }

    public LayerMask(long layer, long mask) {
        this.layer = layer;
        this.mask = mask;
    }

    @Override
    public boolean evaluate(Filter filter) {
        if (filter == null) return true;

        if (filter instanceof LayerMask) {
            LayerMask other = (LayerMask) filter;
            return (layer & other.layer) > 0 && (mask & other.mask) > 0;
        }

        return true;
    }

    public long getLayer() {
        return layer;
    }

    public long getMask() {
        return mask;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (layer ^ (layer >>> 32));
        result = prime * result + (int) (mask  ^ (mask  >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        if (this == obj) return true;
        if (obj instanceof LayerMask) {
            LayerMask other = (LayerMask) obj;
            return other.layer == layer && other.mask == mask;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LayerMask [layer=");
        builder.append(layer);
        builder.append(", mask=");
        builder.append(mask);
        builder.append("]");
        return builder.toString();
    }

}
