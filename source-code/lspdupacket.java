//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with lspdupacket objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class lspdupacket {
   private int sender;
   private int routerID;
   private int linkID;
   private int cost;
   private int via;

   public lspdupacket(int sender, int routerID, int linkID, int cost,
      int via) {
      this.sender = sender;
      this.routerID = routerID;
      this.linkID = linkID;
      this.cost = cost;
      this.via = via;
   } // Constructor

   // Returns the sender of the LS PDU packet
   public int getSender() {
      return sender;
   } // getSender

   // Returns the router ID
   public int getRouterID() {
      return routerID;
   } // getRouterID

   // Returns the link ID
   public int getLinkID() {
      return linkID;
   } // getLinkID

   // Returns the cost of the link
   public int getCost() {
      return cost;
   } // getCost

   // Returns the ID of the link through which the LS PDU packet is sent
   public int getVia() {
      return via;
   } // getVia

   // Sets the sender of the LS PDU packet
   public void setSender(int sender) {
      this.sender = sender;
   } // setSender

   // Sets the ID of the link through which the LS PDU packet is sent
   public void setVia(int via) {
      this.via = via;
   } // setVia

   // Creates a packet to send data to the emulator and writes it out to the
   // data transfer socket
   public void sendTo(String emulatorAddress, int emulatorPort,
      DatagramSocket dataTransferSocket) throws Exception {
      byte[] dataToEmulator = getDataToEmulator();
      InetAddress emulatorIPAddress = InetAddress.getByName(emulatorAddress);
      DatagramPacket packetToEmulator =
         new DatagramPacket(dataToEmulator, dataToEmulator.length,
            emulatorIPAddress, emulatorPort);
      dataTransferSocket.send(packetToEmulator);
   } // sendTo

   // Gets data to be sent to the emulator
   private byte[] getDataToEmulator() throws Exception {
      ByteBuffer buffer = ByteBuffer.allocate(20);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.putInt(sender);
      buffer.putInt(routerID);
      buffer.putInt(linkID);
      buffer.putInt(cost);
      buffer.putInt(via);
      return buffer.array();
   } // getDataToEmulator

   // Creates a packet to receive data from the emulator and reads into it 
   // from the data transfer socket
   public static lspdupacket receiveFrom(DatagramSocket dataTransferSocket)
      throws Exception {
      byte[] dataFromEmulator = new byte[20];
      DatagramPacket packetFromEmulator =
         new DatagramPacket(dataFromEmulator, dataFromEmulator.length);
      dataTransferSocket.receive(packetFromEmulator);
      lspdupacket parsedPacket = parseDataFromEmulator(dataFromEmulator);
      return parsedPacket;
   } // receiveFrom

   // Parses data received from the emulator
   public static lspdupacket parseDataFromEmulator(byte[] dataFromEmulator)
      throws Exception {
      ByteBuffer buffer = ByteBuffer.wrap(dataFromEmulator);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      int sender = buffer.getInt();
      int routerID = buffer.getInt();
      int linkID = buffer.getInt();
      int cost = buffer.getInt();
      int via = buffer.getInt();
      return new lspdupacket(sender, routerID, linkID, cost, via);
   } // parseDataFromEmulator
}
