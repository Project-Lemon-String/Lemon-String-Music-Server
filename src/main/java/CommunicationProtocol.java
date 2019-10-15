import java.io.*;
import java.net.*;

public class CommunicationProtocol extends Thread{
    public void openConn() throws Exception{
        //Create UDP Socket with port 1337
        DatagramSocket s = new DatagramSocket(1337);

        //Create Dispatcher
        Dispatcher dispatcher = new Dispatcher();

        //Create buffer for connection
        byte[] buffer = new byte[8192];

        //Client Info
        InetAddress clientIP = null;
        int clientPort = 0;

        while(true){
            System.out.println("Server is now listening on UDP for messages.\n");

            DatagramPacket inboundMsg = new DatagramPacket(buffer, buffer.length);
            s.receive(inboundMsg);

            String msgReceived = new String(inboundMsg.getData(), 0, inboundMsg.getLength());

            if(msgReceived.equals("Close Server")){
                System.out.println("Server is now closing.");
                break;
            }

            byte[] outgoingMsg = dispatcher.dispatch(msgReceived).getBytes();

            clientIP = inboundMsg.getAddress();
            clientPort = inboundMsg.getPort();

            DatagramPacket outgoingPacket = new DatagramPacket(outgoingMsg, outgoingMsg.length, clientIP, clientPort);
            s.send(outgoingPacket);
            System.out.println("Server has sent an outgoing packet to " + clientIP + " on port " + clientPort + ".\n");
            System.out.println("-------------------------------------------------------------");
        }

        s.close();
    }
}
