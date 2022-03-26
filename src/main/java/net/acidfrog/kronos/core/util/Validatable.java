package net.acidfrog.kronos.core.util;

/**
 * Implemented by classes that could have invalid instances. The
 * {@link Validatable#validate() validate()} method should be called after
 * the inputs are set to test them. This ensures all inputs follow a
 * certain pattern.
 * 
 * @author Ethan Temprovich
 */
@FunctionalInterface
public interface Validatable {

    /**
     * Determines whether the inputs are valid.
     * 
     * @return {@code true} if the inputs are valid, {@code false} otherwise
     */
    public abstract boolean validate();

}
