//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with linkcost objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

public class linkcost {
   private int linkID;
   private int cost;

   public linkcost(int linkID, int cost) {
      this.linkID = linkID;
      this.cost = cost;
   } // Constructor

   // Returns the link ID
   public int getLinkID() {
      return linkID;
   } // getLink

   // Returns the associated cost
   public int getCost() {
      return cost;
   } // getCost
}
