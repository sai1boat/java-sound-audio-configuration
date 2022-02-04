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

public class Player extends Thread{

    private SourceDataLine line;
    private AudioFormat audioFormat;
    private InputStream input = null;
    private int bufSize;
    private boolean keepGoing;
    
    public Player (InputStream in, AudioFormat aF, int whichMixer, int buf, int internalBuf){
        System.out.print("Player initializing: ");

        this.audioFormat = aF;
        this.input = in;
        this.bufSize = buf;
        this.keepGoing = true;
        this.line = null;

        try
        {
                line = Util.getSourceLine(audioFormat, whichMixer,internalBuf);
                line.open(audioFormat,internalBuf);
                line.start();
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
        this.start();
        System.out.println("done.");
    }
    public Player (InputStream in, AudioFormat aF,int buf, int internalBuf)
    {
	
        System.out.print("Player initializing: ");

        this.audioFormat = aF;
        this.input = in;
        this.bufSize = buf;
        this.keepGoing = true;
        this.line = null;

        try
        {
                line = Util.getDefaultSourceLine(audioFormat, internalBuf);
                line.open(audioFormat,internalBuf);
                line.start();
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
        this.start();
        System.out.println("done.");
    }
    public void run() 
    {		

        byte[] abData = new byte[bufSize];
        int numRead = bufSize; //for now; until a read op changes it.
        while (true)
        {
            
            try
            {
                numRead = input.read(abData); 
            }
            catch(IOException e)
            {
                e.printStackTrace();
                return;
            }
            if(abData != null)
            {            
                line.write(abData, 0, numRead);
            }
            if(!keepGoing) return;
        }

    }//end playAudio(byte[] audioBuff)
    
    
    /** Free up resources **/
    public void kill() {
        this.keepGoing = false;
        line.flush();
        line.stop();
        line.close(); 
        /* leave inputStream open */
    }


}
