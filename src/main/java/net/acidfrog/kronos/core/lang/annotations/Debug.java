package net.acidfrog.kronos.core.lang.annotations;

import java.lang.annotation.*;
import net.acidfrog.kronos.core.Config;

/**
 * {@link Annotation} used for notating methods used only when the
 * {@link Config#DEBUG Config.DEBUG} method is set to true.
 * 
 * @author Ethan Temprovich
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Debug {   }
