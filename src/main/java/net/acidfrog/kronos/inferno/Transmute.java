package net.acidfrog.kronos.inferno;

final class Transmute {

    private Transmute() {}

    public sealed interface OfTypes permits Of1<?>,
                                            Of2<?, ?>,
                                            Of3<?, ?, ?>,
                                            Of4<?, ?, ?, ?>,
                                            Of5<?, ?, ?, ?, ?>,
                                            Of6<?, ?, ?, ?, ?, ?>,
                                            Of7<?, ?, ?, ?, ?, ?, ?>,
                                            Of8<?, ?, ?, ?, ?, ?, ?, ?> {

        public abstract Object[] getComponents();

        public abstract Object getContext();
    }

    public non-sealed interface Of1<T> extends OfTypes {

        public abstract OfTypes withValue(T component);
    }
    
    public non-sealed interface Of2<T1, T2> extends OfTypes {

        public abstract OfTypes withValue(T1 component1, T2 component2);
    }

    public non-sealed interface Of3<T1, T2, T3> extends OfTypes {

        public abstract OfTypes withValue(T1 component1, T2 component2, T3 component3);
    }

    public non-sealed interface Of4<T1, T2, T3, T4> extends OfTypes {

        public abstract OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4);
    }

    public non-sealed interface Of5<T1, T2, T3, T4, T5> extends OfTypes {

        public abstract OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5);
    }

    public non-sealed interface Of6<T1, T2, T3, T4, T5, T6> extends OfTypes {

        public abstract OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6);
    }

    public non-sealed interface Of7<T1, T2, T3, T4, T5, T6, T7> extends OfTypes {

        public abstract OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, T7 component7);
    }

    public non-sealed interface Of8<T1, T2, T3, T4, T5, T6, T7, T8> extends OfTypes {

        public abstract OfTypes withValue(T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, T7 component7, T8 component8);
    }

    public sealed interface Modifier permits ByRemoving,ByAdding1AndRemoving<?>,
                                                        ByAdding2AndRemoving<?, ?>,
                                                        ByAdding3AndRemoving<?, ?, ?>,
                                                        ByAdding4AndRemoving<?, ?, ?, ?>,
                                                        ByAdding5AndRemoving<?, ?, ?, ?, ?>,
                                                        ByAdding6AndRemoving<?, ?, ?, ?, ?, ?>,
                                                        ByAdding7AndRemoving<?, ?, ?, ?, ?, ?, ?>,
                                                        ByAdding8AndRemoving<?, ?, ?, ?, ?, ?, ?, ?> {

        public abstract Object getModifier();
    }

    public non-sealed interface ByRemoving extends Modifier {

        public abstract Modifier withValue(Entity entity);
    }

    public non-sealed interface ByAdding1AndRemoving<T> extends Modifier {

        public abstract Modifier withValue(Entity entity, T component);
    }
    
    public non-sealed interface ByAdding2AndRemoving<T1, T2> extends Modifier {

        public abstract Modifier withValue(Entity entity, T1 component1, T2 component2);
    }

    public non-sealed interface ByAdding3AndRemoving<T1, T2, T3> extends Modifier {

        public abstract Modifier withValue(Entity entity, T1 component1, T2 component2, T3 component3);
    }

    public non-sealed interface ByAdding4AndRemoving<T1, T2, T3, T4> extends Modifier {

        public abstract Modifier withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4);
    }

    public non-sealed interface ByAdding5AndRemoving<T1, T2, T3, T4, T5> extends Modifier {

        public abstract Modifier withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4, T5 component5);
    }

    public non-sealed interface ByAdding6AndRemoving<T1, T2, T3, T4, T5, T6> extends Modifier {

        public abstract Modifier withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6);
    }

    public non-sealed interface ByAdding7AndRemoving<T1, T2, T3, T4, T5, T6, T7> extends Modifier {

        public abstract ByRemoving withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, T7 component7);
    }

    public non-sealed interface ByAdding8AndRemoving<T1, T2, T3, T4, T5, T6, T7, T8> extends Modifier {

        public abstract ByRemoving withValue(Entity entity, T1 component1, T2 component2, T3 component3, T4 component4, T5 component5, T6 component6, T7 component7, T8 component8);
    }
}
