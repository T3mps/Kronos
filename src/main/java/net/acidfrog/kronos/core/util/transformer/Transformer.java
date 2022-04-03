package net.acidfrog.kronos.core.util.transformer;

public interface Transformer<I, O> {

    public O transform(I input);
    
}
