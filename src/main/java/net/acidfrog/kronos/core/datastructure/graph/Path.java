package net.acidfrog.kronos.core.datastructure.graph;

import java.util.List;

public final class Path<T extends Comparable<T>> {

    private int cost = 0;
    private List<Graph.Edge<T>> path = null;

    public Path(int cost, List<Graph.Edge<T>> path) {
        if (path == null) throw (new NullPointerException("path cannot be NULL."));

        this.cost = cost;
        this.path = path;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<Graph.Edge<T>> getPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cost;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)  return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        @SuppressWarnings("unchecked")
        Path<T> other = (Path<T>) obj;
        if (cost != other.cost) return false;
        if (path == null) {
            if (other.path != null) return false;
        } else if (!path.equals(other.path)) return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Cost = ").append(cost).append("\n");
        for (Graph.Edge<T> e : path) builder.append("\t").append(e);
        return builder.toString();
    }
}
