//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with hellopacket objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class hellopacket {
   private int routerID;
   private int linkID;

   public hellopacket(int routerID, int linkID) {
      this.routerID = routerID;
      this.linkID = linkID;
   } // Constructor

   // Returns the ID of the router that send the HELLO packet
   public int getRouterID() {
      return routerID;
   } // getRouterID

   // Returns the ID of the link through which the HELLO packet is sent
   public int getLinkID() {
      return linkID;
   } // getLinkID

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
      ByteBuffer buffer = ByteBuffer.allocate(8);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.putInt(routerID);
      buffer.putInt(linkID);
      return buffer.array();
   } // getDataToEmulator

   // Creates a packet to receive data from the emulator and reads into it 
   // from the data transfer socket
   public static hellopacket receiveFrom(DatagramSocket dataTransferSocket)
      throws Exception {
      byte[] dataFromEmulator = new byte[8];
      DatagramPacket packetFromEmulator =
         new DatagramPacket(dataFromEmulator, dataFromEmulator.length);
      dataTransferSocket.receive(packetFromEmulator);
      hellopacket parsedPacket = parseDataFromEmulator(dataFromEmulator);
      return parsedPacket;
   } // receiveFrom

   // Parses data received from the emulator
   public static hellopacket parseDataFromEmulator(byte[] dataFromEmulator)
      throws Exception {
      ByteBuffer buffer = ByteBuffer.wrap(dataFromEmulator);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      int routerID = buffer.getInt();
      int linkID = buffer.getInt();
      return new hellopacket(routerID, linkID);
   } // parseDataFromEmulator
}
