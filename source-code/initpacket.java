//==============================================================================
// Shortest Path Routing Algorithm
//
// @description: Module for providing functions to work with initpacket objects
// @author: Elisha Lai
// @version: 1.0 25/11/2016
//==============================================================================

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class initpacket {
   private int routerID;

   public initpacket(int routerID) {
      this.routerID = routerID;
   } // Constructor

   // Returns the ID of the router that send the INIT packet
   public int getRouterID() {
      return routerID;
   } // getRouterID

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
      ByteBuffer buffer = ByteBuffer.allocate(4);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      buffer.putInt(routerID);
      return buffer.array();
   } // getDataToEmulator
}
