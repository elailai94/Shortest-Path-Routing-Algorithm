//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with topologydb objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.util.ArrayList;

public class topologydb {
   private int nbrRouter;
   private ArrayList<routercircuitdb> routerCircuitDBs;

   public topologydb() {
      nbrRouter = 0;
      routerCircuitDBs = new ArrayList<routercircuitdb>();
   } // Constructor

   // Returns the number of routers in the topology database
   public int getNbrRouter() {
      return nbrRouter;
   } // getNbrRouter

   // Returns the routers in the topology database
   public ArrayList<routercircuitdb> getRouterCircuitDBs() {
      return routerCircuitDBs;
   } // getRouterCircuitDB

   // Adds a router with the associated circuit database to the topology
   // database
   public void addRouter(routercircuitdb router) {
      routerCircuitDBs.add(router);
      nbrRouter += 1;
   } // addRouter

   // Checks if a router is in the topology database
   public boolean containsRouter(int routerID) {
      for (routercircuitdb routerCircuitDB : routerCircuitDBs) {
         if (routerID == routerCircuitDB.getRouterID()) { // Found router?
            return true;
         } // if
      } // for

      return false;
   } // containsRouter

   // Returns the router with the associated circuit database from the
   // topology database
   public routercircuitdb getRouter(int routerID) {
      for (routercircuitdb routerCircuitDB : routerCircuitDBs) {
         if (routerID == routerCircuitDB.getRouterID()) { // Found router?
            return routerCircuitDB;
         } // if
      } // for

      return null;
   } // getRouter
}
