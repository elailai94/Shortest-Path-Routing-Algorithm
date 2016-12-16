//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with routercircuitdb
//  objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

public class routercircuitdb {
   private int routerID;
   private circuitdb circuitDB;

   public routercircuitdb(int routerID, circuitdb circuitDB) {
      this.routerID = routerID;
      this.circuitDB = circuitDB;
   } // Constructor

   // Returns the router ID
   public int getRouterID() {
      return routerID;
   } // getRouterID

   // Returns the associated circuit database
   public circuitdb getCircuitDB() {
      return circuitDB;
   } // getCircuitDB
}
