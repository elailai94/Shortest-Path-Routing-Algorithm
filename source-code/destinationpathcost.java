//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with destinationpathcost
//  objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

public class destinationpathcost {
   private int destinationID;
   private int path;
   private int cost;

   public destinationpathcost(int destinationID, int path, int cost) {
      this.destinationID = destinationID;
      this.path = path;
      this.cost = cost;
   } // Constructor

   // Returns the destination ID
   public int getDestinationID() {
      return destinationID;
   } // getDestination

   // Returns the path
   public int getPath() {
      return path;
   } // getPath

   // Returns the cost
   public int getCost() {
      return cost;
   } // getCost

   // Sets the path
   public void setPath(int path) {
      this.path = path;
   } // setPath

   // Sets the cost
   public void setCost(int cost) {
      this.cost = cost;
   } // setCost
}
