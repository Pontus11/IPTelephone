import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Created by pontu on 2018-02-07.
 */
/*
Audio player class used to play audio for the user.
sets the audio format, audio input stream and sound file.
 */
public class AudioPlayer extends Thread {
    private byte[] audioArray;
    private AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 8000.0F, false);
    private AudioInputStream ais;
    private File soundFile;
    private boolean alive = true;

    //constructor for starting the audioPlayer using an audioArray.
    AudioPlayer(byte[] audioArray) {
        try {
            this.audioArray = audioArray;
            ByteArrayInputStream bais = new ByteArrayInputStream(audioArray);
            ais = new AudioInputStream(bais, audioFormat, audioArray.length / audioFormat.getFrameSize());
            start();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    /*
   Standard constructor used when using the audioPlayer.
   Accepts an audio file name as the param and calls the start() method.
    */
    AudioPlayer(String fileName) {
        try {
            soundFile = new File(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        start();
    }
    //constructor for starting an audioplayer with an input stream.
    AudioPlayer(AudioInputStream inputStream) {
        ais = inputStream;
        start();
    }

    /*
     Run method that runs for as long as the audioPlayer has audio to play.
     sets the audio format from the sound file and opens the dataline.
     reads the audio input stream and plays it to the user.
    */
    public void run() {
        System.out.println("now playing");
        while (alive) {
            try {
                ais = AudioSystem.getAudioInputStream(soundFile);
                audioFormat = ais.getFormat();
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                dataLine.open(audioFormat);
                dataLine.start();
                int bytesRead = 0;
                int bufferSize = 1000;
                byte[] soundData = new byte[bufferSize];
                while (bytesRead != -1 && alive) {
                    try {
                        bytesRead = ais.read(soundData, 0, soundData.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bytesRead >= 0 && alive) {
                        int bytesWritten = dataLine.write(soundData, 0, bytesRead);
                    }
                }
                dataLine.drain();
                dataLine.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void setAlive(boolean a) {
        alive = a;
    }
}
