//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with logger objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.io.FileWriter;
import java.io.BufferedWriter;

import java.util.ArrayList;

public class logger {
   private int loggerRouterID;
   private BufferedWriter loggerWriter;

   public logger(int loggerRouterID) throws Exception {
      this.loggerRouterID = loggerRouterID;
      String logFileName = String.format("router%d.log", this.loggerRouterID);
      this.loggerWriter = new BufferedWriter(new FileWriter(logFileName));
   } // Constructor

   // Logs INIT packet sent to the log file
   public void logInitPacketSent(initpacket initPacketToEmulator)
      throws Exception {
      int routerID = initPacketToEmulator.getRouterID();
      String initPacketSentMessage =
         String.format("R%d sends an INIT: router_id %d",
            loggerRouterID, routerID);
      logMessage(initPacketSentMessage);
   } // logInitPacketSent

   // Logs circuit database to the log file
   public void logCircuitDBReceived(circuitdb circuitDB)
      throws Exception {      
      int nbrLink = circuitDB.getNbrLink();
      String circuitDBReceivedMessage =
         String.format("R%d receives a CIRCUIT DB: nbr_link %d",
            loggerRouterID, nbrLink);
      logMessage(circuitDBReceivedMessage);
   } // logCircuitDBReceived

   // Logs HELLO packet sent to the log file
   public void logHelloPacketSent(hellopacket helloPacketToEmulator)
      throws Exception {
      int routerID = helloPacketToEmulator.getRouterID();
      int linkID = helloPacketToEmulator.getLinkID();
      String helloPacketSentMessage =
         String.format("R%d sends a HELLO: router_id %d, link_id %d",
            loggerRouterID, routerID, linkID);
      logMessage(helloPacketSentMessage);
   } // logHelloPacketSent

   // Logs HELLO packet received to the log file
   public void logHelloPacketReceived(hellopacket helloPacketFromEmulator)
      throws Exception {
      int routerID = helloPacketFromEmulator.getRouterID();
      int linkID = helloPacketFromEmulator.getLinkID();
      String helloPacketReceivedMessage =
         String.format("R%d receives a HELLO: router_id %d, link_id %d",
            loggerRouterID, routerID, linkID);
      logMessage(helloPacketReceivedMessage);
   } // logHelloPacketReceived

   // Logs LS PDU packet sent to the log file
   public void logLspduPacketSent(lspdupacket lspduPacketToEmulator)
      throws Exception {
      int sender = lspduPacketToEmulator.getSender();
      int routerID = lspduPacketToEmulator.getRouterID();
      int linkID = lspduPacketToEmulator.getLinkID();
      int cost = lspduPacketToEmulator.getCost();
      int via = lspduPacketToEmulator.getVia();
      String lspduPacketSentMessage =
         String.format("R%d sends a LS PDU: sender %d, router_id %d, " +
            "link_id %d, cost %d, via %d", loggerRouterID, sender,
            routerID, linkID, cost, via);
      logMessage(lspduPacketSentMessage);
   } // logLspduPacketSent

   // Logs LS PDU packet received to the log file
   public void logLspduPacketReceived(lspdupacket lspduPacketFromEmulator)
      throws Exception {
      int sender = lspduPacketFromEmulator.getSender();
      int routerID = lspduPacketFromEmulator.getRouterID();
      int linkID = lspduPacketFromEmulator.getLinkID();
      int cost = lspduPacketFromEmulator.getCost();
      int via = lspduPacketFromEmulator.getVia();
      String lspduPacketReceivedMessage =
         String.format("R%d receives a LS PDU: sender %d, router_id %d, " +
            "link_id %d, cost %d, via %d", loggerRouterID, sender,
            routerID, linkID, cost, via);
      logMessage(lspduPacketReceivedMessage);
   } // logLspduPacketReceived

   // Logs topology database to the log file
   public void logTopologyDB(topologydb topologyDB) throws Exception {
      String titleMessage = "# Topology database";
      logMessage(titleMessage);

      ArrayList<routercircuitdb> routerCircuitDBs = 
         topologyDB.getRouterCircuitDBs();
      for (routercircuitdb routerCircuitDB : routerCircuitDBs) {
         int routerID = routerCircuitDB.getRouterID();
         circuitdb circuitDB = routerCircuitDB.getCircuitDB();

         int nbrLink = circuitDB.getNbrLink();
         String nbrLinkMessage =
            String.format("R%d -> R%d nbr link %d", loggerRouterID,
               routerID, nbrLink);
         logMessage(nbrLinkMessage);

         ArrayList<linkcost> linkCosts = circuitDB.getLinkCosts();
         for (linkcost linkCost : linkCosts) {
            int linkID = linkCost.getLinkID();
            int cost = linkCost.getCost();
            String linkMessage =
               String.format("R%d -> R%d link %d cost %d", loggerRouterID,
                  routerID, linkID, cost);
            logMessage(linkMessage);
         } // for
      } // for
   } // logTopologyDB

   // Logs routing database to the log file
   public void logRoutingDB(routingdb routingDB) throws Exception {
      String titleMessage = "# RIB";
      logMessage(titleMessage);

      ArrayList<destinationpathcost> destinationPathCosts =
         routingDB.getDestinationPathCosts();
      for (destinationpathcost destinationPathCost : destinationPathCosts) {
         int destinationID = destinationPathCost.getDestinationID();
         int path = destinationPathCost.getPath();
         int cost = destinationPathCost.getCost();
         String destinationMessage = "";
         
         if (destinationID == loggerRouterID) {
            destinationMessage =
               String.format("R%d -> R%d -> %s, %s", loggerRouterID,
                  destinationID, "Local", "0");
         } else if (path == topologygraph.infinity) {
            destinationMessage =
               String.format("R%d -> R%d -> %s, %s", loggerRouterID,
                  destinationID, "None", "Infinite");
         } else {
            destinationMessage =
               String.format("R%d -> R%d -> R%d, %d", loggerRouterID,
                  destinationID, path, cost);
         } // if
         
         logMessage(destinationMessage);
      } // for
   } // logRoutingDB

   // Logs message to the log file
   private void logMessage(String message) throws Exception {
      loggerWriter.write(message);
      loggerWriter.newLine();
      loggerWriter.flush();
   } // logMessage

   // Closes the logger
   public void close() throws Exception {
      loggerWriter.close();
   } // close
}
