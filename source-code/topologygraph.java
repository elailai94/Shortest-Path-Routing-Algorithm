//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with topologygraph
//  objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.util.ArrayList;

public class topologygraph {
   public static final int infinity = Integer.MAX_VALUE;

   private int[][] adjacencyMatrix;

   public topologygraph() {
      adjacencyMatrix = new int[router.NBR_ROUTER][router.NBR_ROUTER];

      for (int i = 0; i < router.NBR_ROUTER; i++) {
         for (int j = 0; j < router.NBR_ROUTER; j++) {
            if (i == j) {
               adjacencyMatrix[i][j] = 0;
            } else {
               adjacencyMatrix[i][j] = infinity;
            } // if
         } // for
      } // for
   } // Constructor

   // Adds an edge with the associated cost to the topology graph
   public void addEdge(topologydb topologyDB, int routerID, int linkID,
      int cost) {
      ArrayList<routercircuitdb> routerCircuitDBs =
         topologyDB.getRouterCircuitDBs();
      
      for (routercircuitdb routerCircuitDB : routerCircuitDBs) {
         if (routerID != routerCircuitDB.getRouterID()) {
            circuitdb circuitDB = routerCircuitDB.getCircuitDB();

            if (circuitDB.containsLink(linkID)) {
               int i = (routerID - 1);
               int j = (routerCircuitDB.getRouterID() - 1);
               adjacencyMatrix[i][j] = cost;
               adjacencyMatrix[j][i] = cost;
            } // if
         } // if
      } // for
   } // addEdge

   // Finds shortest paths using Djikstra's algorithm
   public int[][] findShortestPaths(int routerID) {  
      int[] distances = new int[router.NBR_ROUTER];
      int[] predecessors = new int[router.NBR_ROUTER];
      ArrayList<Integer> verticesWithNoFinalDistances = new ArrayList<Integer>();

      initializeArrays(distances, predecessors, verticesWithNoFinalDistances,
         routerID);

      while (verticesWithNoFinalDistances.size() > 0) {
         int i = extractVertexWithSmallestDistance(verticesWithNoFinalDistances,
            distances);

         for (int j = 0; j < router.NBR_ROUTER; j++) {
            if (adjacencyMatrix[i][j] != infinity) {
               int totalDistance = (distances[i] + adjacencyMatrix[i][j]);
               if (totalDistance < distances[j]) {
                  distances[j] = totalDistance;
                  predecessors[j] = i;
               } // if
            } // if
         } // for
      } // while

      return new int[][] {distances, predecessors};
   } // find

   // Initializes distances, predecessors, and vertices with no final
   // distance arrays
   private void initializeArrays(int[] distances, int[] predecessors,
      ArrayList<Integer> verticesWithNoFinalDistances, int routerID) {

      for (int i = 0; i < router.NBR_ROUTER; i++) {
         distances[i] = infinity;
         predecessors[i] = infinity;
         verticesWithNoFinalDistances.add(i);
      } // for

      distances[(routerID - 1)] = 0;
   } // initializeDistanceAndPredecessorArrays

   // Extracts the vertex with the smallest distance from vertices with no
   // final distances
   private int extractVertexWithSmallestDistance(
      ArrayList<Integer> verticesWithNoFinalDistances, int[] distances) {
      int vertexWithSmallestDistance = verticesWithNoFinalDistances.get(0);

      for (int i : verticesWithNoFinalDistances) {
         if (distances[i] < distances[vertexWithSmallestDistance]) {
            vertexWithSmallestDistance = i;
         } // if
      } // for

      int index =
         verticesWithNoFinalDistances.indexOf(vertexWithSmallestDistance);
      verticesWithNoFinalDistances.remove(index);

      return vertexWithSmallestDistance;
   } // extractVertexWithSmallestDistance
}
