package net.acidfrog.kronos.physics.collision;

public sealed interface Filter permits LayerMask {

    public default boolean evaluate(Filter filter) {
        return true;
    }
    
}
