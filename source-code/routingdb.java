//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with routinginfodb
//  objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.util.ArrayList;

public class routingdb {
   private int nbrDestination;
   private ArrayList<destinationpathcost> destinationPathCosts;

   public routingdb() {
      nbrDestination = 0;
      destinationPathCosts = new ArrayList<destinationpathcost>();
   } // Constructor

   // Returns the number of destinations in the routing database
   public int getNbrDestination() {
      return nbrDestination;
   } // getNbrDestination

   // Returns the destinations in the routing database
   public ArrayList<destinationpathcost> getDestinationPathCosts() {
      return destinationPathCosts;
   } // getDestinationPathCosts

   // Adds a destination to the routing database with the associated next-hop
   // router on the shortest path from the current router to the destination
   // and cost of the shortest path from the current router to the destination
   public void addDestination(destinationpathcost destination) {
      destinationPathCosts.add(destination);
      nbrDestination += 1;
   } // addDestination

   // Checks if a destination is in the routing database
   public boolean containsDestination(int destinationID) {
      for (destinationpathcost destinationPathCost : destinationPathCosts) {
         if (destinationID == destinationPathCost.getDestinationID()) {
            return true;
         } // if
      } // for

      return false;
   } // containsDestination

   // Returns the destination from the routing database with the associated
   // next-hop router on the shortest path from the current router to the
   // destination and cost of the shortest path from the current router to the
   // destination 
   public destinationpathcost getDestination(int destinationID) {
      for (destinationpathcost destinationPathCost : destinationPathCosts) {
         if (destinationID == destinationPathCost.getDestinationID()) {
            return destinationPathCost;
         } // if
      } // for

      return null;
   } // getDestination

   // Updates the routing database with the next-hop routers on the shortest
   // paths from the current router to all destinations and cost of the
   // shortest paths from the current router to all destinations
   public void update(topologygraph topologyGraph, int routerID) {
      int[][] results = topologyGraph.findShortestPaths(routerID);
      int[] distances = results[0];
      int[] predecessors = results[1];
      int[] nextHopRouters = calculateNextHopRouters(predecessors, routerID);

      for (int i = 0; i < router.NBR_ROUTER; i++) {
         int destinationID = (i + 1);
         int path = (nextHopRouters[i] == topologygraph.infinity) ?
            nextHopRouters[i] : (nextHopRouters[i] + 1);
         int cost = distances[i];

         if (containsDestination(destinationID)) {
            destinationpathcost destinationPathCost =
               getDestination(destinationID);
            destinationPathCost.setPath(path);
            destinationPathCost.setCost(cost);
         } else {
            destinationpathcost newDestinationPathCost =
               new destinationpathcost(destinationID, path, cost);
            addDestination(newDestinationPathCost);
         } // if
      } // for
   } // update

   // Calculates the next-hop routers on the shortest paths from the current
   // router to all destinations
   private int[] calculateNextHopRouters(int[] predecessors, int routerID) {
      int[] nextHopRouters = new int[router.NBR_ROUTER];

      for (int i = 0; i < router.NBR_ROUTER; i++) {
         nextHopRouters[i] = i;

         while (true) {
            if (predecessors[nextHopRouters[i]] == topologygraph.infinity) {
               nextHopRouters[i] = topologygraph.infinity;
               break;
            } else if (predecessors[nextHopRouters[i]] == (routerID - 1)) {
               break;
            } else {
               nextHopRouters[i] = predecessors[nextHopRouters[i]];
            } // if
         } // while
      } // for

      return nextHopRouters;
   } // calculateNextHopRouters
}
