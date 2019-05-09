import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by pontu on 2018-02-24.
 */
/*
Class used to record the users audio so that it can be sent to the other user that's in the phone call.
This class is only used when in a phone call.
Recorders the users audio through the mic using the dataline.
 */
public class AudioRecorder extends Thread{
    private DatagramSocket socket;
    private TargetDataLine targetDataLine;
    private AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
    private IPTele ipTele;
    private int volumeValue;
    private ByteArrayOutputStream baos;
    private boolean alive = true;
    private InetAddress ipAddress;
    /*
    Standard constructor that sets up the socket and the dataline.
     */
    public AudioRecorder(IPTele ipt, InetAddress address, int savedVolume) {
        volumeValue = savedVolume;
        ipTele = ipt;
        ipAddress = address;
        try {
            socket = new DatagramSocket();
        }catch(SocketException e) {
            e.printStackTrace();
        }
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open();
            targetDataLine.start();
        }catch(LineUnavailableException e) {
            e.printStackTrace();
        }
        start();
    }

    /*
    method that's constantly running as long as the call is active.
    constantly calls the record method to record the sound and send it to the other user.
     */
    public void run() {
        while(alive) {
            record();
        }
        targetDataLine.stop();
        targetDataLine.drain();
        targetDataLine.close();
        socket.close();
        System.out.println("RECORDER: stopped, drained, closed, closed socket");
    }

    /*
    Records the users audio using the targetdataline.
    then sends it to the other user using the socket as a datagram packet.
     */
    private void record(){
        DatagramPacket datagramPacket;
        try {
            baos = new ByteArrayOutputStream();

            int buffsize = 4096;
            int numBytesRead;
            byte[] data = new byte[buffsize];

            DatagramSocket socket = new DatagramSocket();
            while (alive) {
                numBytesRead = targetDataLine.read(data, 0, data.length);
                data = setByteArrayVolume(data);
                datagramPacket = new DatagramPacket(data, data.length, ipAddress, 3010);

                socket.send(datagramPacket);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        try {
            baos.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
        targetDataLine.stop();
        targetDataLine.drain();
        targetDataLine.close();
    }


    /*
    Method used to set the microphone volume so that the user can lower their mic volume.
     */
    private byte[] setByteArrayVolume(byte[] audioSamples) {
        byte[] array = new byte[audioSamples.length];
        for (int i = 0; i < array.length; i+=2) {
            short byte1 = audioSamples[i+1];
            short byte2 = audioSamples[i];

            byte1 = (short) ((byte1 & 0xff) << 8);
            byte2 = (short) (byte2 & 0xff);

            short twoBytes = (short) (byte1 | byte2);
            twoBytes = (short) (twoBytes * 0.01 * volumeValue);

            //convert back first byte and second byte from short
            array[i] = (byte) twoBytes;
            array[i+1] = (byte) (twoBytes >> 8);

        }
        return array;
    }
    public void setVolumeValue(int volume) {
        volumeValue = volume;
    }
    public void killThread(boolean a) {
        alive = a;
    }
}
