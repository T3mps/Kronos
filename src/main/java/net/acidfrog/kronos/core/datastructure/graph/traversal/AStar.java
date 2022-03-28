package net.acidfrog.kronos.core.datastructure.graph.traversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.acidfrog.kronos.core.datastructure.graph.Graph;
import net.acidfrog.kronos.core.lang.annotations.Internal;

public final class AStar {

    private AStar() {}

    public <T extends Comparable<T>> List<Graph.Edge<T>> getShortestPath(Graph<T> graph, Graph.Vertex<T> start, Graph.Vertex<T> goal) {
        int size = graph.getVertices().size(); // used to size data structures appropriately
        Set<Graph.Vertex<T>> closedSet = new HashSet<Graph.Vertex<T>>(size); // The set of nodes already evaluated.
        List<Graph.Vertex<T>> openSet = new ArrayList<Graph.Vertex<T>>(size); // The set of tentative nodes to be evaluated, initially containing the start node
        openSet.add(start);
        Map<Graph.Vertex<T>,Graph.Vertex<T>> cameFrom = new HashMap<Graph.Vertex<T>,Graph.Vertex<T>>(size); // The map of navigated nodes.

        Map<Graph.Vertex<T>,Integer> gScore = new HashMap<Graph.Vertex<T>,Integer>(); // Cost from start along best known path.
        gScore.put(start, 0);

        // Estimated total cost from start to goal through y.
        Map<Graph.Vertex<T>,Integer> fScore = new HashMap<Graph.Vertex<T>,Integer>();
        for (Graph.Vertex<T> v : graph.getVertices()) fScore.put(v, Integer.MAX_VALUE);
        fScore.put(start, heuristicCostEstimate(start, goal));

        Comparator<Graph.Vertex<T>> comparator = new Comparator<Graph.Vertex<T>>() {
            
            @Override
            public int compare(Graph.Vertex<T> o1, Graph.Vertex<T> o2) {
                if (fScore.get(o1) < fScore.get(o2)) return -1;
                if (fScore.get(o2) < fScore.get(o1)) return +1;
                return 0;
            }
        };

        while (!openSet.isEmpty()) {
            Graph.Vertex<T> current = openSet.get(0);
            if (current.equals(goal)) return reconstructPath(cameFrom, goal);

            openSet.remove(0);
            closedSet.add(current);
            for (Graph.Edge<T> edge : current.getEdges()) {
                Graph.Vertex<T> neighbor = edge.getToVertex();
                if (closedSet.contains(neighbor)) continue; // Ignore the neighbor which is already evaluated.

                int tenativeGScore = gScore.get(current) + distanceBetween(current,neighbor); // length of this path.
                if (!openSet.contains(neighbor)) openSet.add(neighbor); // Discover a new node
                else if (tenativeGScore >= gScore.get(neighbor)) continue;

                // This path is the best until now. Record it!
                cameFrom.put(neighbor, current);
                gScore.put(neighbor, tenativeGScore);
                int estimatedFScore = gScore.get(neighbor) + heuristicCostEstimate(neighbor, goal);
                fScore.put(neighbor, estimatedFScore);

                // fScore has changed, re-sort the list
                Collections.sort(openSet,comparator);
            }
        }

        return null;
    }

    private <T extends Comparable<T>> int distanceBetween(Graph.Vertex<T> start, Graph.Vertex<T> next) {
        for (Graph.Edge<T> e : start.getEdges()) if (e.getToVertex().equals(next)) {
            return e.getCost();
        }
        return Integer.MAX_VALUE;
    }


    /** exists for readability & future changes */
    private @Internal <T extends Comparable<T>> int heuristicCostEstimate(Graph.Vertex<T> start, Graph.Vertex<T> goal) {
        return 1;
    }

    private <T extends Comparable<T>> List<Graph.Edge<T>> reconstructPath(Map<Graph.Vertex<T>,Graph.Vertex<T>> cameFrom, Graph.Vertex<T> current) {
        List<Graph.Edge<T>> totalPath = new ArrayList<Graph.Edge<T>>();

        while (current != null) {
            Graph.Vertex<T> previous = current;
            current = cameFrom.get(current);
            
            if (current != null) {
                Graph.Edge<T> edge = current.getEdge(previous);
                totalPath.add(edge);
            }
        }
        
        Collections.reverse(totalPath);
        return totalPath;
    }
    
}
