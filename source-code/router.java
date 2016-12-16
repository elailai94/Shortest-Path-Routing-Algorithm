//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: An implementation of a router program in Java
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.net.DatagramSocket;
import java.net.DatagramPacket;

import java.util.ArrayList;

public class router {
   public static final int NBR_ROUTER = 5;

   private static int routerID;
   private static String emulatorAddress;
   private static int emulatorPort;
   private static int routerPort;
   private static logger routerLogger;
   private static DatagramSocket routerSocket;
   private static circuitdb circuitDB;
   private static topologydb topologyDB;
   private static topologygraph topologyGraph;
   private static routingdb routingDB;
   private static ArrayList<hellopacket> helloPacketsReceived;

   public static void main(String[] args) throws Exception {
      // Checks the number and formats of the command line arguments passed
      checkCommandLineArguments(args);

      routerID = Integer.parseInt(args[0]);
      emulatorAddress = args[1];
      emulatorPort = Integer.parseInt(args[2]);
      routerPort = Integer.parseInt(args[3]);

      // Creates the logger
      routerLogger = new logger(routerID);

      // Creates the router socket
      routerSocket = new DatagramSocket(routerPort);

      // Creates an INIT packet to send data to the emulator and writes
      // it out to the router socket
      initpacket initPacketToEmulator = new initpacket(routerID);
      initPacketToEmulator.sendTo(emulatorAddress, emulatorPort, routerSocket);

      // Logs INIT packet sent to the log file 
      routerLogger.logInitPacketSent(initPacketToEmulator);

      // Creates a circuit database to receive data from the emulator and
      // reads into it from the router socket
      circuitDB = circuitdb.receiveFrom(routerSocket);

      // Logs circuit database received to the log file
      routerLogger.logCircuitDBReceived(circuitDB);

      // Creates the topology database and adds the router with the
      // associated circuit database to the topology database
      routercircuitdb newRouterCircuitDB = new routercircuitdb(routerID, circuitDB);
      topologyDB = new topologydb();
      topologyDB.addRouter(newRouterCircuitDB);

      // Logs topology database to the log file
      routerLogger.logTopologyDB(topologyDB);

      // Creates the topology graph
      topologyGraph = new topologygraph();

      // Creates the routing database
      routingDB = new routingdb();

      // Updates the routing database
      routingDB.update(topologyGraph, routerID);

      // Logs routing database to the log file
      routerLogger.logRoutingDB(routingDB);

      // Creates an array to store all the HELLO packets received
      helloPacketsReceived = new ArrayList<hellopacket>();

      // Sends a HELLO packet to all of the router's neighbours
      sendHelloPacketsToAllNeighbours();

      // Receives HELLO or LS PDU packets from the the emulator
      while (true) {
         // Creates a packet to receive data from the emulator and reads
         // into it from the router socket
         DatagramPacket packetFromEmulator = receiveFrom(routerSocket);

         if (isHelloPacket(packetFromEmulator)) { // Received a HELLO packet?
            handleHelloPacketReceived(packetFromEmulator);
         } else { // Received a LS PDU packet?
            handleLspduPacketReceived(packetFromEmulator);
         } // if
      } // while
	} // main

   // Checks the number and formats of the command line arguments passed
   private static void checkCommandLineArguments(String[] args)
      throws Exception {
      if (args.length != 4) {
         System.out.println("ERROR: Expecting 4 command line arguments," +
            " but got " + args.length + " arguments");
         System.exit(-1);
      } // if

      try {
         routerID = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
         System.out.println("ERROR: Expecting a router ID which is" +
            " an integer, but got " + args[0]);
         System.exit(-1);
      } // try

      if ((!isValidIPAddress(args[1])) && (!isValidHostName(args[1]))) {
         System.out.println("ERROR: Expecting an emulator address which is" +
            " a valid IP address or host name, but got " + args[1]);
         System.exit(-1);
      } // if

      try {
         emulatorPort = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {
         System.out.println("ERROR: Expecting an emulator port which is" +
            " an integer, but got " + args[2]);
         System.exit(-1);
      } // try

      try {
         routerPort = Integer.parseInt(args[3]);
      } catch (NumberFormatException e) {
         System.out.println("ERROR: Expecting a router port which is" +
            " an integer, but got " + args[3]);
         System.exit(-1);
      } // try
   } // checkCommandLineArguments

   // Checks if a string is a valid IP address, which ranges from 0.0.0.0 to
   // 255.255.255.255
   private static boolean isValidIPAddress(String string) throws Exception {
      String regex = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
      boolean isRegexMatch = string.matches(regex);
      return isRegexMatch;
   } // isValidIPAddress

   // Checks if a string is a valid host name, which complies with RFC 1912
   private static boolean isValidHostName(String string) throws Exception {
      String regex = "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])" +
                     "(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
      boolean isRegexMatch = string.matches(regex);
      
      boolean isCorrectLength = (string.length() <= 255);
      
      String[] labels = string.split("\\.");
      boolean isLabelsNotAllNumeric = true;
      for (String label : labels) {
         if (label.matches("^[0-9]+$")) {
            isLabelsNotAllNumeric = false;
            break;
         } // if
      } // for

      if (isRegexMatch && isCorrectLength && isLabelsNotAllNumeric) {
         return true;
      } else {
         return false;
      } // if
   } // isValidHostName

   // Sends a HELLO packet to all of the router's neighbours
   private static void sendHelloPacketsToAllNeighbours() throws Exception {
      ArrayList<linkcost> linkCosts = circuitDB.getLinkCosts();
      
      for (linkcost linkCost : linkCosts) {
         // Creates a HELLO packet to send data to the emulator and writes
         // it out to the router socket
         int linkID = linkCost.getLinkID();
         hellopacket helloPacketToEmulator = new hellopacket(routerID, linkID);
         helloPacketToEmulator.sendTo(emulatorAddress, emulatorPort, routerSocket);

         // Logs HELLO packet sent to the log file
         routerLogger.logHelloPacketSent(helloPacketToEmulator);
      } // for
   } // sendHelloPacketsToAllNeighbours

   // Creates a packet to receive data from the emulator and reads into it
   // from the router socket
   private static DatagramPacket receiveFrom(DatagramSocket routerSocket)
      throws Exception {
      byte[] dataFromEmulator = new byte[20];
      DatagramPacket packetFromEmulator =
            new DatagramPacket(dataFromEmulator, dataFromEmulator.length);
      routerSocket.receive(packetFromEmulator);
      return packetFromEmulator;
   } // receiveFrom

   // Checks if a packet is a HELLO packet
   private static boolean isHelloPacket(DatagramPacket packetFromEmulator) {
      if (packetFromEmulator.getLength() == 8) {
         return true;
      } else {
         return false;
      } // if
   } // isHelloPacket

   // Handles HELLO packet received events
   private static void handleHelloPacketReceived(DatagramPacket packetFromEmulator)
      throws Exception {
      // Parses data received from the emulator
      byte[] dataFromEmulator = packetFromEmulator.getData();
      hellopacket helloPacketFromEmulator =
               hellopacket.parseDataFromEmulator(dataFromEmulator);
      
      // Logs HELLO packet received to the log file
      routerLogger.logHelloPacketReceived(helloPacketFromEmulator);

      // Adds HELLO packet to the array of HELLO packets received
      helloPacketsReceived.add(helloPacketFromEmulator);
      
      // Sends a set of LS PDU packets containing the router's circuit
      // database back to the router's neighbour that send the HELLO
      // packet
      sendLspduPacketsToOneNeighbour(helloPacketFromEmulator);
   } // handleHelloPacket

   // Sends a set of LS PDU packets containing the router's circuit database
   // back to the router's neighbour that send the HELLO packet
   private static void sendLspduPacketsToOneNeighbour(
      hellopacket helloPacketFromEmulator) throws Exception {
      ArrayList<linkcost> linkCosts = circuitDB.getLinkCosts();

      for (linkcost linkCost : linkCosts) {
         // Creates a LS PDU packet to send data to the emulator and writes
         // it out to the router socket
         int linkID = linkCost.getLinkID();
         int cost = linkCost.getCost();
         int via = helloPacketFromEmulator.getLinkID();
         lspdupacket lspduPacketToEmulator = 
            new lspdupacket(routerID, routerID, linkID, cost, via);
         lspduPacketToEmulator.sendTo(emulatorAddress, emulatorPort,
            routerSocket);

         // Logs LS PDU packet sent to the log file
         routerLogger.logLspduPacketSent(lspduPacketToEmulator);
      } // for
   } // sendLspduPacketsToOneNeighbour

   // Handles LS PDU packet received events
   private static void handleLspduPacketReceived(DatagramPacket packetFromEmulator)
      throws Exception {
      // Parses data received from the emulator
      byte[] dataFromEmulator = packetFromEmulator.getData();
      lspdupacket lspduPacketFromEmulator =
               lspdupacket.parseDataFromEmulator(dataFromEmulator);

      // Logs LS PDU packet received to the log file
      routerLogger.logLspduPacketReceived(lspduPacketFromEmulator);

      int router = lspduPacketFromEmulator.getRouterID();
      int linkID = lspduPacketFromEmulator.getLinkID();
      int cost = lspduPacketFromEmulator.getCost();

      if (!topologyDB.containsRouter(router)) { // Is router not in the
                                                // topology database?
         // Creates a new link and updates the topology database
         linkcost newLink = new linkcost(linkID, cost);
         circuitdb newCircuitDB = new circuitdb();
         newCircuitDB.addLink(newLink); 
         routercircuitdb newRouterCircuitDB =
            new routercircuitdb(router, newCircuitDB);
         topologyDB.addRouter(newRouterCircuitDB);
      } else { // Is router in the topology database?
         routercircuitdb routerCircuitDB = topologyDB.getRouter(router);
         
         if (!routerCircuitDB.getCircuitDB().containsLink(linkID)) { // Is link not in the
                                                                     // topology database?
            // Creates a new link and updates the topology database
            linkcost newLink = new linkcost(linkID, cost);
            routerCircuitDB.getCircuitDB().addLink(newLink);
         } else { // Is link in the topology database?
            return;
         } // if
      } // if

      // Logs topology database to the log file
      routerLogger.logTopologyDB(topologyDB);

      // Adds an edge to the topology graph
      topologyGraph.addEdge(topologyDB, router, linkID, cost);

      // Updates routing database
      routingDB.update(topologyGraph, routerID);

      // Logs routing database to the log file
      routerLogger.logRoutingDB(routingDB);

      // Sends the LS PDU packet received from the emulator to all of the
      // router's neighbours except the router's neighbour that send the
      // LS PDU packet and the router's neighbours from which the router
      // didn't receive a HELLO packet yet
      sendLspduPacketsToAllNeighbours(lspduPacketFromEmulator);
   } // handleLspduPacket

   // Sends the LS PDU packet received from the emulator to all of the
   // router's neighbours except the router's neighbour that send the
   // LS PDU packet and the router's neighbours from which the router
   // didn't receive a HELLO packet yet
   private static void sendLspduPacketsToAllNeighbours(
      lspdupacket lspduPacketFromEmulator) throws Exception {
      ArrayList<linkcost> linkCosts = circuitDB.getLinkCosts();
      int previousVia = lspduPacketFromEmulator.getVia();

      for (linkcost linkCost : linkCosts) {
         if ((previousVia != linkCost.getLinkID()) &&
            (isInAnyHelloPacketsReceived(linkCost.getLinkID()))) {
            // Changes the sender and via fields of the LS PDU packet received
            // from the emulator and writes the LS PDU packet to the router
            // socket
            lspduPacketFromEmulator.setSender(routerID);
            lspduPacketFromEmulator.setVia(linkCost.getLinkID());
            lspdupacket lspduPacketToEmulator = lspduPacketFromEmulator;
            lspduPacketToEmulator.sendTo(emulatorAddress, emulatorPort,
               routerSocket);

            // Logs LS PDU packet sent to the log file
            routerLogger.logLspduPacketSent(lspduPacketToEmulator);
         } // if
      } // for
   } // sendLspduPacketsToAllNeighbours

   // Checks if a link ID is found in any of the HELLO packets received
   private static boolean isInAnyHelloPacketsReceived(int linkID) {
      for (hellopacket helloPacket : helloPacketsReceived) {
         if (linkID == helloPacket.getLinkID()) {
            return true;
         } // if
      } // for

      return false;
   } // isInAnyHelloPacketsReceived
}
