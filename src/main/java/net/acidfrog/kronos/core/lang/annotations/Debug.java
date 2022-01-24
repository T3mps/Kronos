package net.acidfrog.kronos.core.lang.annotations;

import java.lang.annotation.*;

/**
 * Debug annotation for methods.
 * 
 * @author Ethan Temprovich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Debug {   }
