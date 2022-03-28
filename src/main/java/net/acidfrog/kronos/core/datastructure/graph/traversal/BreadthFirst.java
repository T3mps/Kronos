package net.acidfrog.kronos.core.datastructure.graph.traversal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.acidfrog.kronos.core.datastructure.graph.Graph;

public final class BreadthFirst {

    private BreadthFirst() {}

    @SuppressWarnings("unchecked")
    public static final <T extends Comparable<T>> Graph.Vertex<T>[] traverse(Graph<T> graph, Graph.Vertex<T> start) {
        final List<Graph.Vertex<T>> vertices = new ArrayList<Graph.Vertex<T>>();
        vertices.addAll(graph.getVertices());

        Map<Graph.Vertex<T>, Integer> verticesMap = new HashMap<Graph.Vertex<T>, Integer>();
        for (int i = 0; i < vertices.size(); i++) verticesMap.put(vertices.get(i), i);

        byte[][] adj = new byte[vertices.size()][vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            Graph.Vertex<T> v = vertices.get(i);
            int idx = verticesMap.get(v);
            byte[] arr = new byte[vertices.size()];

            adj[idx] = arr;

            List<Graph.Edge<T>> edges = v.getEdges();
            for (Graph.Edge<T> e : edges) arr[verticesMap.get(e.getToVertex())] = 1;
        }

        byte[] visited = new byte[vertices.size()];
        for (int i = 0; i < visited.length; i++) visited[i] = -1;

        Graph.Vertex<T>[] arr = new Graph.Vertex[vertices.size()];

        Graph.Vertex<T> element = start;
        int c = 0;
        int i = verticesMap.get(element);
        int k = 0;

        arr[k] = element;
        visited[i] = 1;
        k++;

        Deque<Graph.Vertex<T>> queue = new ArrayDeque<Graph.Vertex<T>>();
        queue.add(element);

        do {
            element = queue.peek();
            c = verticesMap.get(element);
            i = 0;
            while (i < vertices.size()) {
                if (adj[c][i] == 1 && visited[i] == -1) {
                    Graph.Vertex<T> v = vertices.get(i);
                    queue.add(v);
                    visited[i] = 1;

                    arr[k] = v;
                    k++;
                }
                i++;
            }
            queue.poll();
        } while (!queue.isEmpty());

        return arr;
    }

}
