//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with circuitdb objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.util.ArrayList;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class circuitdb {
   private int nbrLink;
   private ArrayList<linkcost> linkCosts;

   public circuitdb() {
      nbrLink = 0;
      linkCosts = new ArrayList<linkcost>();
   } // Constructor

   // Returns the number of links attached to the router
   public int getNbrLink() {
      return nbrLink;
   } // getNbrLink

   // Returns the links attached to the router
   public ArrayList<linkcost> getLinkCosts() {
      return linkCosts;
   } // getLinkCost

   // Adds a link with the associated cost to the circuit database
   public void addLink(linkcost link) {
      linkCosts.add(link);
      nbrLink += 1;
   } // addLink

   // Checks if a link is in the circuit database
   public boolean containsLink(int linkID) {
      for (linkcost linkCost : linkCosts) {
         if (linkID == linkCost.getLinkID()) {
            return true;
         } // if
      } // for

      return false;
   } // containsLink

   // Creates a circuit database to receive data from the emulator and
   // reads into it from the data transfer socket
   public static circuitdb receiveFrom(DatagramSocket dataTransferSocket)
      throws Exception {
      byte[] dataFromEmulator = new byte[44];
      DatagramPacket packetFromEmulator =
         new DatagramPacket(dataFromEmulator, dataFromEmulator.length);
      dataTransferSocket.receive(packetFromEmulator);
      circuitdb parsedPacket = parseDataFromEmulator(dataFromEmulator);
      return parsedPacket;
   } // receiveFrom

   // Parses data received from the emulator
   private static circuitdb parseDataFromEmulator(byte[] dataFromEmulator)
      throws Exception {
      ByteBuffer buffer = ByteBuffer.wrap(dataFromEmulator);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      int nbrLink = buffer.getInt();
      circuitdb newCircuitDB = new circuitdb();
      
      for (int i = 0; i < nbrLink; i++) {
         int linkID = buffer.getInt();
         int cost = buffer.getInt();
         linkcost newLink = new linkcost(linkID, cost);
         newCircuitDB.addLink(newLink);
      } // for

      return newCircuitDB;
   } // parseDataFromEmulator
}
