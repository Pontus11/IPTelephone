import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by pontu on 2018-02-24.
 */
/*
Class used to receive audio from another user when in a phone call.
 */
public class AudioReceiver extends Thread{
    private DatagramSocket socket;
    private AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
    private DataLine.Info dataLineInfo;
    private SourceDataLine sourceDataLine;
    private AudioInputStream ais;
    private FloatControl volumeControl;
    private float volumeValue = 100;
    private IPTele ipTele;
    private boolean alive = true;
    /*
  Standard constructor.
  Sets the socket and the sourceDataline.
   */
    public AudioReceiver(IPTele ipt, int port, float savedVolume) {
        ipTele = ipt;
        volumeValue = savedVolume;
        try {
            socket = new DatagramSocket(port);
        }catch(SocketException e) {
            e.printStackTrace();
        }
        try {
            dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
        }catch(LineUnavailableException e) {
            e.printStackTrace();
        }
        volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        setVolumeValue(volumeValue);
        start();
    }

    /*
     Method that is run for as long as the user is in a call with another user.
     constantly reads the incoming data messages containing audio.
     Writes the audio data to the sourceDataline by calling receivePacket() which then calls the playAudio() method.
    */
    public void run() {
        byte[] buf = new byte[4096];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());

        while(alive) {
            receivePacket(packet,bais);
        }
    }
    /*
    Method used to open audio packet from another user.
    Calls playAudio() to play it.
     */
    private void receivePacket(DatagramPacket packet, ByteArrayInputStream bais) {
        try {
            socket.receive(packet);
            ais = new AudioInputStream(bais, audioFormat, packet.getLength());
            playAudio(packet.getData());
        }catch(IOException e) {
            System.out.println("socket closed");
        }
    }
    /*
    Plays the audio in the byte[] data
     */
    public void playAudio(byte[] data) {
        try {
            sourceDataLine.write(data, 0, data.length);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    /*
    Method used to set the volume of the audio played by audioReceiver.
     */
    public void setVolumeValue(float value) {
        float volumeMultiplier = (volumeControl.getMaximum() - volumeControl.getMinimum())/100;
        volumeValue = volumeControl.getMinimum() +  (value * volumeMultiplier);
        volumeControl.setValue(volumeValue);
    }
    public void killThread(boolean a) {
        alive = a;
        sourceDataLine.stop();
        sourceDataLine.drain();
        sourceDataLine.close();
        socket.close();
        System.out.println("RECEIVER: stopped, drained, closed socket");
    }
}
