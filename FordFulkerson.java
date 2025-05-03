//20230355 - Amandi Lochana Alahakoon

import java.util.*;

// Class to implement the Ford-Fulkerson algorithm for computing max flow in a flow network
public class FordFulkerson {
    private boolean[] marked;// marked[v] = true if there is a path from source to v in residual graph
    private FlowEdge[] edgeTo; // edgeTo[v] = last edge on shortest residual path to v
    private int maxFlow = 0;// total flow from source to sink
    private List<String> augmentingPathDetails = new ArrayList<>();// to record details of each augmenting path
    private int augmentingPathCount = 0;// number of augmenting paths found
    private Map<String, FlowEdge> uniqueEdges = new HashMap<>(); // stores unique edges for reporting
    private long algorithmTime; // time taken to run the algorithm

    // Constructor: computes the max flow from s to t in the given flow network G
    public FordFulkerson(FlowNetwork G, int s, int t) {
        long startTime = System.nanoTime();// Record start time for performance measurement

        // Store unique edges for final flow reporting
        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if (v == e.from()) {
                    String key = e.from() + "->" + e.to();
                    uniqueEdges.put(key, e);
                }
            }
        }
        //find augmenting paths while possible
        while (hasAugmentingPath(G, s, t)) {
            augmentingPathCount++;

            // Find bottleneck capacity along augmenting path
            int bottleneck = Integer.MAX_VALUE;
            for (int v = t; v != s; v = edgeTo[v].from()) {
                bottleneck = Math.min(bottleneck, edgeTo[v].residualCapacityTo(v));
            }

            // Reconstruct the augmenting path
            List<Integer> path = new ArrayList<>();
            for (int v = t; v != s; v = edgeTo[v].from()) {
                path.add(0, v);// Insert at the beginning
            }
            path.add(0, s);

            // Augment the flow along the path
            for (int v = t; v != s; v = edgeTo[v].from()) {
                edgeTo[v].addResidualFlowTo(v, bottleneck);
            }
            // Update max flow
            maxFlow += bottleneck;

            // Record the path details
            StringBuilder pathDetail = new StringBuilder();
            pathDetail.append("Step ").append(augmentingPathCount).append(" (bottleneck) =").append(bottleneck)
                    .append(" path: ");

            for (int i = 0; i < path.size(); i++) {
                pathDetail.append(path.get(i));
                if (i < path.size() - 1) {
                    pathDetail.append(" -> ");
                }
            }
            pathDetail.append("  (total flow =").append(maxFlow).append(")");

            augmentingPathDetails.add(pathDetail.toString());
        }

        algorithmTime = System.nanoTime() - startTime;// Record total algorithm time
    }
    // Helper method: finds if there is an augmenting path using BFS
    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        marked = new boolean[G.V()];
        edgeTo = new FlowEdge[G.V()];

        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        marked[s] = true;

        while (!queue.isEmpty()) {
            int v = queue.poll();

            for (FlowEdge e : G.adj(v)) {
                int w = (e.to() == v) ? e.from() : e.to();// Get the other vertex
                // If not yet marked and has residual capacity
                if (!marked[w] && e.residualCapacityTo(w) > 0) {
                    edgeTo[w] = e;// Remember path
                    marked[w] = true;
                    queue.add(w);
                }
            }
        }

        return marked[t];// True if we reached sink
    }
    // Returns the value of the maximum flow
    public int maxFlow() {
        return maxFlow;
    }
    // Returns the number of augmenting paths found
    public int getAugmentingPathCount() {
        return augmentingPathCount;
    }
    // Returns details of each augmenting path found
    public List<String> getAugmentingPathDetails() {
        return augmentingPathDetails;
    }
    // Returns the final flow values for each unique edge
    public List<String> getFinalEdgeFlows() {
        List<String> edgeFlows = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(uniqueEdges.keySet());
        // Sort edges first by from-vertex, then by to-vertex
        Collections.sort(sortedKeys, (a, b) -> {
            String[] aParts = a.split("->");
            String[] bParts = b.split("->");
            int fromA = Integer.parseInt(aParts[0]);
            int fromB = Integer.parseInt(bParts[0]);
            if (fromA != fromB) return fromA - fromB;

            int toA = Integer.parseInt(aParts[1]);
            int toB = Integer.parseInt(bParts[1]);
            return toA - toB;
        });
        // Create formatted string for each edge
        for (String key : sortedKeys) {
            FlowEdge edge = uniqueEdges.get(key);
            edgeFlows.add(edge.from() + "->" + edge.to() + ": " + edge.flow() + "/" + edge.capacity());
        }

        return edgeFlows;
    }
    // Returns the time taken by the algorithm (in nanoseconds)
    public long getAlgorithmTime() {
        return algorithmTime;
    }
}
