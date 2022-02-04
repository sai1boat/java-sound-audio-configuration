/*
 * Player.java
 *
 * Created on June 19, 2007, 10:03 PM
 *
 */

package IUAudio;

/**
 * author@John Bowker
 *
 *
 */

import java.io.*;
import javax.sound.sampled.*;

public class BytePlayer {

    private SourceDataLine line;
    private AudioFormat audioFormat;
    //private int bufSize;
    private boolean keepGoing;
    
    /**
     * Initialize a simple player
     * @param whichMixer an integer index of AudioSystem.getMixerInfo[]. Zero might work.
     */
    
    public BytePlayer(int whichMixer)
    {
    	
    	new BytePlayer(new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,44100.0F, 16, 1,2,44100.0F, false),256,8192);
    }
    
    /**
     * Initialize a more complex player
     * @param aF the AudioEncoding object that describes your audio data.
     * @param buf the "External" buffer size. How much data is written to the lines each iteration.
     * @param internalBuf the size for Java Sound's internal circular buffer.
     * 
     * @see javax.sound.sampled.AudioFormat
     */
    
    public BytePlayer (AudioFormat aF,int buf, int internalBuf)
    {
	
        System.out.print("BytePlayer: Hi, initializing...");

        this.audioFormat = aF;
        //this.bufSize = buf;
        this.keepGoing = true;
        this.line = null;
        

        try
        {
                line = Util.getDefaultSourceLine(audioFormat, internalBuf);
                
                this.line.addLineListener(new MySourceLineListener());
                
                line.open(audioFormat,internalBuf);
                
                //line.start();
        }
        catch (LineUnavailableException e)
        {
                System.out.println("error in Player.java: ");
                e.printStackTrace();
                System.exit(1);
        }
        catch (Exception e)
        {
                System.out.println("error in Player.java: ");
                e.printStackTrace();
                System.exit(1);
        }
        line.start();
        System.out.println("Ready for audio.");
    }
    
    
    
    
    /**
     * Initialize a more complex player
     * @param aF the AudioEncoding object that describes your audio data.
     * @param whichMixer an integer index of AudioSystem.getMixerInfo[]. Zero might work.
     * @param buf the "External" buffer size. How much data is written to the lines each iteration.
     * @param internalBuf the size for Java Sound's internal circular buffer.
     */
    
    //THIS ONE DOES NOT APPEAR TO WORK
    
    public BytePlayer (AudioFormat aF, int whichMixer, int buf, int internalBuf){
        System.out.print("BytePlayer: Hi.");

        this.audioFormat = aF;
        //this.bufSize = buf;
        this.keepGoing = true;
        this.line = null;       
        
        try
        {
                line = Util.getSourceLine(audioFormat, whichMixer,internalBuf);
                
                this.line.addLineListener(new MySourceLineListener());
                
                line.open(audioFormat,internalBuf);
                
                
        }
        catch (LineUnavailableException e)
        {
                e.printStackTrace();
                System.out.println("error in BytePlayer.java: ");
                System.exit(1);
        }
        catch (Exception e)
        {              
                e.printStackTrace();
                System.out.println("error in BytePlayer.java: ");
                System.exit(1);
        }
        line.start();
        System.out.println("Ready for audio.");
    }
    
    
    
    
    
    public  void play(byte[] sound,int offset,int numBytes)
    {	
    	line.write(sound,offset,numBytes);
    	//line.drain();
    }
    public void start()
    {
    	this.line.start();
    }
    public void stop()
    {
    	this.line.stop();
    	line.flush();
    }
    public void drain()
    {
    	this.line.drain();
    }
    public void close(){
    	this.line.close();
    }
    
    public class MySourceLineListener implements LineListener{
    	public void update(LineEvent le)
    	{
    		System.out.println("Line Event:"+le.getType());
    	}
    }
}
