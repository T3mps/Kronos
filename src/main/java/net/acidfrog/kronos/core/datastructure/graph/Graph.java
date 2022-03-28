package net.acidfrog.kronos.core.datastructure.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.acidfrog.kronos.core.lang.annotations.Null;

@SuppressWarnings("unchecked")
public class Graph<T extends Comparable<T>> {

    private List<Vertex<T>> vertexList = new ArrayList<Vertex<T>>();
	private Map<T, Vertex<T>> vertexMap = new HashMap<T, Vertex<T>>();
    private List<Edge<T>> edgeList = new ArrayList<Edge<T>>();

    public enum Type {
        DIRECTED, UNDIRECTED
    }

    /** Defaulted to undirected */
    private Type type = Type.UNDIRECTED;

    public Graph() { }

    public Graph(Type type) {
        this.type = type;
    }

    /** Deep copies **/
    public Graph(Graph<T> g) {
        type = g.getType();

        // Copy the vertices which also copies the edges
        for (Vertex<T> v : g.vertexList) this.vertexList.add(new Vertex<T>(v));

		for (Map.Entry<T, Vertex<T>> entry : g.vertexMap.entrySet()) this.vertexMap.put(entry.getKey(), entry.getValue());

        for (Vertex<T> v : this.vertexList) {
            for (Edge<T> e : v.getEdges()) this.edgeList.add(e);
        }
    }

    /**
     * Creates a Graph from the vertices and edges. This defaults to an undirected Graph
     * 
     * NOTE: Duplicate vertices and edges ARE allowed.
     * NOTE: Copies the vertex and edge objects but does NOT store the Collection parameters itself.
     * 
     * @param vertices Collection of vertices
     * @param edges Collection of edges
     */
    public Graph(Collection<Vertex<T>> vertices, Collection<Edge<T>> edges) {
        this(Type.UNDIRECTED, vertices, edges);
    }

    /**
     * Creates a Graph from the vertices and edges.
     * 
     * NOTE: Duplicate vertices and edges ARE allowed.
     * NOTE: Copies the vertex and edge objects but does NOT store the Collection parameters itself.
     * 
     * @param vertices Collection of vertices
     * @param edges Collection of edges
     */
    public Graph(Type type, Collection<Vertex<T>> vertices, Collection<Edge<T>> edges) {
        this(type);

        for (Vertex<T> v : vertices) addVertex(v);

        this.edgeList.addAll(edges);

        for (Edge<T> e : edges) {
            final Vertex<T> from = e.from;
            final Vertex<T> to = e.to;

            if (!this.vertexList.contains(from) || !this.vertexList.contains(to)) continue;

            from.addEdge(e);
            if (this.type == Type.UNDIRECTED) {
                Edge<T> reciprical = new Edge<T>(to, from, e.cost);
                to.addEdge(reciprical);
                this.edgeList.add(reciprical);
            }
        }
    }

	public Vertex<T> addVertex(T value) {
		Vertex<T> v = new Vertex<T>(value);
		this.vertexList.add(v);
		this.vertexMap.put(value, v);
		return v;
	}

	public Vertex<T> addVertex(Vertex<T> v) {
		this.vertexList.add(v);
		this.vertexMap.put(v.getValue(), v);
		return v;
	}

	public Edge<T> addEdge(T from, T to, int cost) {
		Vertex<T> fromVertex = getVertex(from);
		if (fromVertex == null) fromVertex = addVertex(from);
		Vertex<T> toVertex = getVertex(to);
		if (toVertex == null) toVertex = addVertex(to);
		return addEdge(fromVertex, toVertex, cost);
	}

	public Edge<T> addEdge(Vertex<T> from, Vertex<T> to, int cost) {
		Edge<T> e = new Edge<T>(from, to, cost);
		this.edgeList.add(e);
		from.addEdge(e);
		
		if (this.type == Type.UNDIRECTED) {
			Edge<T> reciprical = new Edge<T>(to, from, e.cost);
			this.edgeList.add(reciprical);
			to.addEdge(reciprical);
		}
		
		return e;
	}
	
	public Edge<T> addEdge(Edge<T> edge) {
		return addEdge(edge.from, edge.to, edge.cost);
	}

	public Vertex<T> getVertex(int index) {
		return this.vertexList.get(index);
	}

	public @Null Vertex<T> getVertex(T value) {
		Vertex<T> v = this.vertexMap.get(value);
		if (!(v == null)) return v;
		
		for (Vertex<T> vertex : this.vertexList) {
			if (vertex.getValue().equals(value)) {
				this.vertexMap.put(value, vertex);
				return vertex;
			}
		}

		return null;
	}

	public @Null Edge<T> getEdge(T from, T to) {
		Vertex<T> fromVertex = getVertex(from);
		if (fromVertex == null) return null;
		Vertex<T> toVertex = getVertex(to);
		if (toVertex == null) return null;
		return getEdge(fromVertex, toVertex);
	}

	public @Null Edge<T> getEdge(Vertex<T> from, Vertex<T> to) {
		for (Edge<T> e : from.getEdges()) if (e.to.equals(to)) {
			return e;
		}
		return null;
	}

	public Edge<T> getEdge(int index) {
		return this.edgeList.get(index);
	}

	public List<Edge<T>> getEdges(Vertex<T> v) {
		return v.getEdges();
	}

	public boolean hasEdge(T from, T to) {
		return getEdge(from, to) != null;
	}

	public Vertex<T> removeVertex(T value) {
		Vertex<T> v = getVertex(value);
		if (v == null) return null;
		this.vertexList.remove(v);
		this.vertexMap.remove(value);
		return v;
	}

	public Edge<T> removeEdge(T from, T to) {
		Vertex<T> fromVertex = getVertex(from);
		if (fromVertex == null) return null;
		Vertex<T> toVertex = getVertex(to);
		if (toVertex == null) return null;
		return removeEdge(fromVertex, toVertex);
	}

	public Edge<T> removeEdge(Vertex<T> from, Vertex<T> to) {
		Edge<T> e = getEdge(from, to);
		if (e == null) return null;
		this.edgeList.remove(e);
		from.removeEdge(e);

		if (this.type == Type.UNDIRECTED) {
			Edge<T> reciprical = getEdge(to, from);
			this.edgeList.remove(reciprical);
			to.removeEdge(reciprical);
		}

		return e;
	}

	public Edge<T> removeEdge(int index) {
		Edge<T> e = this.edgeList.remove(index);
		e.from.removeEdge(e);
		if (this.type == Type.UNDIRECTED) {
			Edge<T> reciprical = new Edge<T>(e.to, e.from, e.cost);
			this.edgeList.remove(reciprical);
			e.to.removeEdge(reciprical);
		}
		return e;
	}

    public Type getType() {
        return type;
    }

	public int size() {
		return this.vertexList.size();
	}

	public int edgeCount() {
		return this.edgeList.size();
	}

    public List<Vertex<T>> getVertices() {
        return vertexList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int code = this.type.hashCode() + this.vertexList.size() + this.edgeList.size();
        for (Vertex<T> v : vertexList)
            code *= v.hashCode();
        for (Edge<T> e : edgeList)
            code *= e.hashCode();
        return 31 * code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object g1) {
        if (!(g1 instanceof Graph))
            return false;

        final Graph<T> g = (Graph<T>) g1;

        final boolean typeEquals = this.type == g.type;
        if (!typeEquals)
            return false;

        final boolean verticesSizeEquals = this.vertexList.size() == g.vertexList.size();
        if (!verticesSizeEquals)
            return false;

        final boolean edgesSizeEquals = this.edgeList.size() == g.edgeList.size();
        if (!edgesSizeEquals)
            return false;

        // Vertices can contain duplicates and appear in different order but both arrays should contain the same elements
        final Object[] ov1 = this.vertexList.toArray();
        Arrays.sort(ov1);
        final Object[] ov2 = g.vertexList.toArray();
        Arrays.sort(ov2);
        for (int i=0; i<ov1.length; i++) {
            final Vertex<T> v1 = (Vertex<T>) ov1[i];
            final Vertex<T> v2 = (Vertex<T>) ov2[i];
            if (!v1.equals(v2))
                return false;
        }

        // Edges can contain duplicates and appear in different order but both arrays should contain the same elements
        final Object[] oe1 = this.edgeList.toArray();
        Arrays.sort(oe1);
        final Object[] oe2 = g.edgeList.toArray();
        Arrays.sort(oe2);
        for (int i=0; i<oe1.length; i++) {
            final Edge<T> e1 = (Edge<T>) oe1[i];
            final Edge<T> e2 = (Edge<T>) oe2[i];
            if (!e1.equals(e2))
                return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (Vertex<T> v : vertexList)
            builder.append(v.toString());
        return builder.toString();
    }

    public static class Vertex<T extends Comparable<T>> implements Comparable<Vertex<T>> {

        private T value = null;
        private int weight = 0;
        private List<Edge<T>> edges = new ArrayList<Edge<T>>();

        public Vertex(T value) {
            this.value = value;
        }

        public Vertex(T value, int weight) {
            this(value);
            this.weight = weight;
        }

        /** Deep copies the edges along with the value and weight **/
        public Vertex(Vertex<T> vertex) {
            this(vertex.value, vertex.weight);

            this.edges.addAll(vertex.edges);
        }

        public T getValue() {
            return value;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public void addEdge(Edge<T> e) {
            edges.add(e);
        }

        public List<Edge<T>> getEdges() {
            return edges;
        }

        public Edge<T> getEdge(Vertex<T> v) {
            for (Edge<T> e : edges) {
                if (e.to.equals(v))
                    return e;
            }
            return null;
        }

		public Edge<T> removeEdge(Edge<T> e) {
			edges.remove(e);
			return e;
		}

        public boolean pathTo(Vertex<T> v) {
            for (Edge<T> e : edges) {
                if (e.to.equals(v))
                    return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int code = this.value.hashCode() + this.weight + this.edges.size();
            return 31 * code;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object v1) {
            if (!(v1 instanceof Vertex))
                return false;

            final Vertex<T> v = (Vertex<T>) v1;

            final boolean weightEquals = this.weight == v.weight;
            if (!weightEquals)
                return false;

            final boolean edgesSizeEquals = this.edges.size() == v.edges.size();
            if (!edgesSizeEquals)
                return false;

            final boolean valuesEquals = this.value.equals(v.value);
            if (!valuesEquals)
                return false;

            final Iterator<Edge<T>> iter1 = this.edges.iterator();
            final Iterator<Edge<T>> iter2 = v.edges.iterator();
            while (iter1.hasNext() && iter2.hasNext()) {
                // Only checking the cost
                final Edge<T> e1 = iter1.next();
                final Edge<T> e2 = iter2.next();
                if (e1.cost != e2.cost)
                    return false;
            }

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Vertex<T> v) {
            final int valueComp = this.value.compareTo(v.value);
            if (valueComp != 0)
                return valueComp;

            if (this.weight < v.weight)
                return -1;
            if (this.weight > v.weight)
                return 1;

            if (this.edges.size() < v.edges.size())
                return -1;
            if (this.edges.size() > v.edges.size())
                return 1;

            final Iterator<Edge<T>> iter1 = this.edges.iterator();
            final Iterator<Edge<T>> iter2 = v.edges.iterator();
            while (iter1.hasNext() && iter2.hasNext()) {
                // Only checking the cost
                final Edge<T> e1 = iter1.next();
                final Edge<T> e2 = iter2.next();
                if (e1.cost < e2.cost)
                    return -1;
                if (e1.cost > e2.cost)
                    return 1;
            }

            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Value=").append(value).append(" weight=").append(weight).append("\n");
            for (Edge<T> e : edges)
                builder.append("\t").append(e.toString());
            return builder.toString();
        }
    }

    public static class Edge<T extends Comparable<T>> implements Comparable<Edge<T>> {

        private Vertex<T> from = null;
        private Vertex<T> to = null;
        private int cost = 0;

        public Edge(Vertex<T> from, Vertex<T> to, int cost) {
            if (from == null || to == null) throw (new NullPointerException("Both 'to' and 'from' vertices need to be non-NULL."));

			this.from = from;
			this.to = to;
			this.cost = cost;
        }

        public Edge(Edge<T> e) {
            this(e.from, e.to, e.cost);
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public Vertex<T> getFromVertex() {
            return from;
        }

        public Vertex<T> getToVertex() {
            return to;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int cost = (this.cost * (this.getFromVertex().hashCode() * this.getToVertex().hashCode())); 
            return 31 * cost;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object e1) {
            if (!(e1 instanceof Edge))
                return false;

            final Edge<T> e = (Edge<T>) e1;

            final boolean costs = this.cost == e.cost;
            if (!costs)
                return false;

            final boolean from = this.from.equals(e.from);
            if (!from)
                return false;

            final boolean to = this.to.equals(e.to);
            if (!to)
                return false;

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Edge<T> e) {
            if (this.cost < e.cost)
                return -1;
            if (this.cost > e.cost)
                return 1;

            final int from = this.from.compareTo(e.from);
            if (from != 0)
                return from;

            final int to = this.to.compareTo(e.to);
            if (to != 0)
                return to;

            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[ ").append(from.value).append("(").append(from.weight).append(") ").append("]").append(" -> ")
                   .append("[ ").append(to.value).append("(").append(to.weight).append(") ").append("]").append(" = ").append(cost).append("\n");
            return builder.toString();
        }
    }
    
}