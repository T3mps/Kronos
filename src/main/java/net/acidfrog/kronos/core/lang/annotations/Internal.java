package net.acidfrog.kronos.core.lang.annotations;

import java.lang.annotation.*;

/**
 * {@link Annotation} used for notating internal methods and classes.
 * 
 * @author Ethan Temprovich
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface Internal {}
