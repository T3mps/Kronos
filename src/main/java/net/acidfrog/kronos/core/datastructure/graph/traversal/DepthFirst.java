package net.acidfrog.kronos.core.datastructure.graph.traversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.acidfrog.kronos.core.datastructure.graph.Graph;
import net.acidfrog.kronos.core.datastructure.graph.Graph.Vertex;

public final class DepthFirst {
    
    private DepthFirst() {}

    public static <T extends Comparable<T>> Graph.Vertex<T>[] traverse(Graph<T> graph, Graph.Vertex<T> source) {
        // use for look-up via index
        List<Vertex<T>> vertices = new ArrayList<Vertex<T>>();
        vertices.addAll(graph.getVertices());

        // used for look-up via vertex
        int n = vertices.size();
        Map<Vertex<T>,Integer> vertexToIndex = new HashMap<Vertex<T>,Integer>();
        for (int i=0; i<n; i++) {
            Vertex<T> v = vertices.get(i);
            vertexToIndex.put(v,i);
        }

        // adjacency matrix
        byte[][] adj = new byte[n][n];
        for (int i=0; i<n; i++) {
            Vertex<T> v = vertices.get(i);
            int idx = vertexToIndex.get(v);
            byte[] array = new byte[n];
            adj[idx] = array;
            List<Graph.Edge<T>> edges = v.getEdges();
            for (Graph.Edge<T> e : edges) array[vertexToIndex.get(e.getToVertex())] = 1;
        }

        // visited array
        final byte[] visited = new byte[n];
        for (int i = 0; i < visited.length; i++) visited[i] = -1;

        // for holding results
        @SuppressWarnings("unchecked")
        Graph.Vertex<T>[] arr = new Graph.Vertex[n];

        // start at the source
        Vertex<T> element = source;       
        int c = 0;
        int i = vertexToIndex.get(element);
        int k = 0;

        visited[i] = 1;
        arr[k] = element;
        k++;

        final Stack<Vertex<T>> stack = new Stack<Vertex<T>>();
        stack.push(source);
        while (!stack.isEmpty()) {    
            element = stack.peek();
            c = vertexToIndex.get(element);
            i = 0;
            while (i < n) {
                if (adj[c][i] == 1 && visited[i] == -1) {
                    final Vertex<T> v = vertices.get(i);
                    stack.push(v);
                    visited[i] = 1;

                    element = v;
                    c = vertexToIndex.get(element);
                    i = 0;

                    arr[k] = v;
                    k++;
                    continue;
                }
                i++;
            }
            stack.pop();    
        }
        return arr;
    }

}
