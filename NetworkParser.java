//20230355 - Amandi Lochana Alahakoon

import java.io.*;
import java.nio.file.*;
import java.util.*;
// Class to handle parsing of a flow network from a text file
public class NetworkParser {
    // Parses a network definition from a given file and returns a FlowNetwork object
    public static FlowNetwork parse(String filename) throws IOException {
        // Read all lines from the input file
        List<String> lines = Files.readAllLines(Paths.get(filename));
        // The first line contains the number of vertices
        int V = Integer.parseInt(lines.get(0).trim());
        FlowNetwork network = new FlowNetwork(V);
        // Remaining lines contain edges in the format: from to capacity
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;// Skip empty lines

            // Split the line by whitespace
            String[] parts = line.split("\\s+");
            int from = Integer.parseInt(parts[0]);// Source vertex
            int to = Integer.parseInt(parts[1]);// Destination vertex
            int capacity = Integer.parseInt(parts[2]);// Capacity of the edge

            // Create a new flow edge and add it to the network
            FlowEdge edge = new FlowEdge(from, to, capacity);
            network.addEdge(edge);
        }

        return network;// Return the fully constructed network
    }
}
