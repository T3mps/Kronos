package net.acidfrog.kronos.crates;

public interface Identifiable {

    public abstract int getID();

    public abstract int setID(int id);

    public default Identifiable getPrevious() {
        return null;
    }

    public default Identifiable setPrevious(Identifiable prev) {
        return null;
    }

    public default Identifiable getNext() {
        return null;
    }

    public default Identifiable setNext(Identifiable next) {
        return null;
    }
}
