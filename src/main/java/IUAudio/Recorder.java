/*
 * Recorder.java
 *
 * Created on July 3, 2007, 3:43 PM
 *
 */

package IUAudio;

/**
 *
 * @author John
 */


import java.io.*;


import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.*;


public class Recorder extends Thread
{
       
        private TargetDataLine	m_line;
        private OutputStream out = null;       
        private boolean keepGoing;        
        public boolean paused = false;
        public AudioFormat audioFormat;
        int packetSize;
        int internalBuf;
        
        //for UDP if first constructor is chosen
        DatagramSocket outSock;
    	InetAddress serverAddy;
    	private boolean useUDP = false;
    	private int portUDP;
        
        public Recorder(InetAddress addy,int port, AudioFormat format, int whichMixer, int packetSize,int internalBufSize)
        {
        	
        	try
        	{
        		this.outSock = new DatagramSocket();
        	}
        	catch(SocketException e)
        	{
        		e.printStackTrace();
        		
        	}
        	
        	
        	
        	this.serverAddy = addy;
        	this.portUDP = port;
        	this.audioFormat = format;
        	this.useUDP = true;
        	this.packetSize = packetSize;
        	this.keepGoing = true;
        	
        	try
        	{
        		this.m_line = Util.getTargetLine(audioFormat, whichMixer, internalBufSize);
        		this.m_line.start();
        	}
        	catch(LineUnavailableException e)
        	{
        		e.printStackTrace();
        	}
        	
        	this.start();
        	
        }

	//constructor to make a recorder with System chosen targetDataLine
    	public Recorder(OutputStream output, AudioFormat format, int messageSize) 
        {
            this.out=output;
            this.audioFormat = format;
            this.m_line = Util.getDefaultTargetLine(audioFormat, messageSize);
            this.m_line.start();
            this.keepGoing = true;
            this.packetSize = messageSize;
            this.start();
	}
        //constructor that allows you to specify which mixer to obtain a line from.
        public Recorder(OutputStream output, AudioFormat format, 
                int whichMixer, int buf)
        {
            this.out=output;
            this.audioFormat = format;
            this.keepGoing = true;
            this.packetSize = buf;
            try
            {
                this.m_line = Util.getTargetLine(audioFormat, whichMixer, buf);
                this.m_line.start();
            }
            catch(LineUnavailableException e)
            {
                e.printStackTrace();
            }
            
            this.start();
	}
        public void pauseRecording(boolean b) 
        {
            paused = b;
        }

        public void run() 
        {

            byte buffer[] = new byte[packetSize];
            ByteArrayOutputStream audioClip = new ByteArrayOutputStream();

            while(true)
            {
                while(!paused) 
                {  //thread stays alive but recording is "paused"
                    
                    int count = m_line.read(buffer, 0, buffer.length);

                    
                    audioClip.write(buffer,0,count);



                    //this might have to go in another thread
                    if(audioClip.size()>=packetSize)
                    {
                        byte[] tooFullMeasure = audioClip.toByteArray();
                        
                        

                        try
                        {
                        	if(useUDP)
                        	{
                        		DatagramPacket packet = new DatagramPacket(tooFullMeasure,packetSize,this.serverAddy,this.portUDP);
                        		outSock.send(packet);
                        		/*
                        		try{
                        			Thread.sleep(5);
                        		}catch(InterruptedException ie){ie.printStackTrace();}; */
                        		
                        	}
                        	else
                        	{
                        		out.write(tooFullMeasure,0,packetSize);
                        	}
                        	audioClip.reset();
                        	audioClip.write(tooFullMeasure,packetSize,tooFullMeasure.length-packetSize);
                            
                        }
                        catch(IOException e)
                        {
                        		e.printStackTrace();
                                System.out.println("Recorder: Couldn't write to stream.");
                                return;
                        }
                        
                    }//end if audioClip.size()>0
                    
                    if(!keepGoing) return;  //process is killed
                }//end while
                if(!keepGoing) return;  //process is killed
            }//end while       
            
            
        }//end public void run()
        
        
        public void kill()
        {
            keepGoing = false;
            try
            {
                m_line.close();
                out.close();
                outSock.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }           
        }
    
}//end Recorder
