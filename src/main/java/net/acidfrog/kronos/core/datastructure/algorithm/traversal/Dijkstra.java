package net.acidfrog.kronos.core.datastructure.algorithm.traversal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import net.acidfrog.kronos.core.datastructure.graph.Path;
import net.acidfrog.kronos.core.datastructure.graph.CostVertex;
import net.acidfrog.kronos.core.datastructure.graph.Graph;
import net.acidfrog.kronos.core.lang.annotations.Out;

public final class Dijkstra {

    private Dijkstra() {}

    public static final Map<Graph.Vertex<Integer>, Path<Integer>> getShortestPaths(Graph<Integer> graph, Graph.Vertex<Integer> start) {
        Map<Graph.Vertex<Integer>, List<Graph.Edge<Integer>>> paths = new HashMap<Graph.Vertex<Integer>, List<Graph.Edge<Integer>>>();
        Map<Graph.Vertex<Integer>, CostVertex<Integer>> costs = new HashMap<Graph.Vertex<Integer>, CostVertex<Integer>>();

        getShortestPath(graph, start, null, paths, costs);

        Map<Graph.Vertex<Integer>, Path<Integer>> map = new HashMap<Graph.Vertex<Integer>, Path<Integer>>();
        
        for (CostVertex<Integer> pair : costs.values()) {
            int cost = pair.getCost();
            Graph.Vertex<Integer> vertex = pair.getVertex();
            List<Graph.Edge<Integer>> path = paths.get(vertex);
            map.put(vertex, new Path<Integer>(cost, path));
        }
        
        return map;
    }

    public static final Path<Integer> getShortestPath(Graph<Integer> graph, Graph.Vertex<Integer> start, Graph.Vertex<Integer> end) {
        if (graph == null) throw (new NullPointerException());

        // Dijkstra's algorithm only works on positive cost graphs
        boolean hasNegativeEdge = checkForNegativeEdges(graph.getVertices());
        if (hasNegativeEdge) throw (new IllegalArgumentException("Negative cost Edges are not allowed."));

        Map<Graph.Vertex<Integer>, List<Graph.Edge<Integer>>> paths = new HashMap<Graph.Vertex<Integer>, List<Graph.Edge<Integer>>>();
        Map<Graph.Vertex<Integer>, CostVertex<Integer>> costs = new HashMap<Graph.Vertex<Integer>, CostVertex<Integer>>();
        return getShortestPath(graph, start, end, paths, costs);
    }

    private static Path<Integer> getShortestPath(Graph<Integer> graph, Graph.Vertex<Integer> start, Graph.Vertex<Integer> end,
                                                               @Out Map<Graph.Vertex<Integer>, List<Graph.Edge<Integer>>> paths,
                                                               @Out Map<Graph.Vertex<Integer>, CostVertex<Integer>> costs) {
        if (graph == null) throw new NullPointerException();
        if (start == null) throw new NullPointerException();

        // Dijkstra's algorithm only works on positive cost graphs
        boolean hasNegativeEdge = checkForNegativeEdges(graph.getVertices());
        if (hasNegativeEdge) throw (new IllegalArgumentException("Negative cost Edges are not allowed."));

        for (Graph.Vertex<Integer> v : graph.getVertices()) paths.put(v, new ArrayList<Graph.Edge<Integer>>());

        for (Graph.Vertex<Integer> v : graph.getVertices()) {
            if (v.equals(start)) costs.put(v, new CostVertex<Integer>(0, v));
            else costs.put(v, new CostVertex<Integer>(Integer.MAX_VALUE, v));
        }

        Queue<CostVertex<Integer>> unvisited = new PriorityQueue<CostVertex<Integer>>();
        unvisited.add(costs.get(start));

        while (!unvisited.isEmpty()) {
            CostVertex<Integer> pair = unvisited.remove();
            Graph.Vertex<Integer> vertex = pair.getVertex();

            // Compute costs from current vertex to all reachable vertices which haven't been visited
            for (Graph.Edge<Integer> e : vertex.getEdges()) {
                CostVertex<Integer> toPair = costs.get(e.getToVertex()); // O(1)
                CostVertex<Integer> lowestCostToThisVertex = costs.get(vertex); // O(1)
                int cost = lowestCostToThisVertex.getCost() + e.getCost();
                
                if (toPair.getCost() == Integer.MAX_VALUE) {
                    // Haven't seen this vertex yet

                    // Need to remove the pair and re-insert, so the priority queue keeps it's invariants
                    unvisited.remove(toPair); // O(n)
                    toPair.setCost(cost);
                    unvisited.add(toPair); // O(log n)

                    // Update the paths
                    List<Graph.Edge<Integer>> set = paths.get(e.getToVertex()); // O(log n)
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                } else if (cost < toPair.getCost()) {
                    // Found a shorter path to a reachable vertex

                    // Need to remove the pair and re-insert, so the priority queue keeps it's invariants
                    unvisited.remove(toPair); // O(n)
                    toPair.setCost(cost);
                    unvisited.add(toPair); // O(log n)

                    // Update the paths
                    List<Graph.Edge<Integer>> set = paths.get(e.getToVertex()); // O(log n)
                    set.clear();
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                }
            }

            if (end != null && vertex.equals(end)) break;
        }

        if (end != null) {
            CostVertex<Integer> pair = costs.get(end);
            List<Graph.Edge<Integer>> set = paths.get(end);
            return (new Path<Integer>(pair.getCost(), set));
        }

        return null;
    }

    private static boolean checkForNegativeEdges(Collection<Graph.Vertex<Integer>> vertitices) {
        for (Graph.Vertex<Integer> v : vertitices) {
            for (Graph.Edge<Integer> e : v.getEdges()) if (e.getCost() < 0) {
                return true;
            }
        }
        return false;
    }
    
}
