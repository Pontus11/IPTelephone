import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by pontu on 2018-02-28.
 */
/*
Call receiver class that takes care of messages send to the users client from another user.
 */
public class CallReceiver extends Thread{
    private DatagramSocket socket;
    private IPTele ipTele;
    private boolean alive = true;
    public CallReceiver(IPTele ipt, int port) {
        ipTele = ipt;
        try {
            socket = new DatagramSocket(port);
        }catch(SocketException e) {
            e.printStackTrace();
        }
        start();
    }
    /*
      Run method that's constantly waiting for new messages from another user.
   */
    public void run() {
        byte[] buf = new byte[4];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while(alive) {
            try {
                socket.receive(packet);
                String packString = new String(packet.getData());
                System.out.println(packet.getAddress() + ": " + packString);
                dealWithPacket(packet);
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Method used to redirect messages to the correct method in IPTele.
     */
    private void dealWithPacket(DatagramPacket packet) {
        try {
            String packetRequest = new String(packet.getData());
            InetAddress address = packet.getAddress();
            if(packetRequest.equals("CALL")) {
                if(!ipTele.isInPhoneCall() && ipTele.getCallingAddress() == null) {
                    ipTele.displayIncomingCall(address);
                }else{
                    ipTele.sendMessage("DECL", address);
                }
            }
            if(packetRequest.equals("CANC") && ipTele.getCallingAddress() != null && ipTele.getCallingAddress().equals(address)) {
                ipTele.callStoppedByOther();
            }
            if(packetRequest.equals("ACCE") && ipTele.getCallingAddress() != null && ipTele.getCallingAddress().equals(address)) {
                ipTele.startPhoneCall();
            }
            if(packetRequest.equals("DECL") && ipTele.getCallingAddress() != null && ipTele.getCallingAddress().equals(address)) {
                ipTele.stopCall();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Method that's used to kill the thread.
     */
    public void setAlive(boolean a) {
        alive = a;
    }
}
