/*
 * Util.java
 *
 * Created on July 3, 2007, 3:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IUAudio;

/**
 *
 */
import java.io.*;
import javax.sound.sampled.*;
import java.nio.ByteBuffer;

public class Util 
{


	/**
	 * Returns a single byte array consisting of interleaved audio data,
	 * or a stereo sound. The supplied left and right byte arrays
	 * are used to create the result. NOTE: The sound is assumed to be
	 * 16-bit signed PCM.
	 *
	 * @param  left a track of 16-bit PCM sound we want to use as the left.
	 * @param  right a track of 16-bit PCM sound we want to use as the right.
	 * @return      A single byte array that contains the interleaved tracks.
	 * @see         IUAudio.Util
	 */

	public static byte[] interleave(byte[] left, byte[] right)
	{
		ByteBuffer leftBuf;
		ByteBuffer rightBuf;
		  ByteBuffer stereoBuf;
	
			  leftBuf = ByteBuffer.wrap(left);
			  rightBuf= ByteBuffer.wrap(right);
			  stereoBuf = ByteBuffer.allocate(left.length*2);
			  for(int i=0;i<left.length;i++)
			  {
				  short leftShorty = leftBuf.getShort();
				  short rightShorty= rightBuf.getShort();
				  stereoBuf.putShort(leftShorty);
				  stereoBuf.putShort(rightShorty);
			  }
			  return(stereoBuf.array());
	}
	/**
	 * Returns a single byte array consisting of interleaved audio data,
	 * or a stereo sound. The supplied byte array is copied to both the left
	 * and right channels. NOTE: The sound is assumed to be
	 * 16-bit signed PCM.
	 *
	 * @param  sound A byte array to be used as both the left and right channel.
	 * @return      A single byte array that contains interleaved (stereo) audio data.
	 * @see         IUAudio.Util
	 */
	public static byte[] interleave(byte[] sound){
		ByteBuffer monoBuf;
		  ByteBuffer stereoBuf;
	
			  monoBuf = ByteBuffer.wrap(sound);
			  stereoBuf = ByteBuffer.allocate(sound.length*2);
			  for(int i=0;i<sound.length/2;i++)
			  {
				  short myShorty = monoBuf.getShort();
				  stereoBuf.putShort(myShorty);
				  stereoBuf.putShort(myShorty);
			  }
			  return(stereoBuf.array());
	}
	
	
	
	
	
	public static byte[] deinterleave(byte[] sound)
	{
		System.out.println("IUAudio.Util.deinterleave: deinterlacing sound.length "+sound.length+".");
		ByteBuffer stereoBuf = ByteBuffer.wrap(sound);
		ByteBuffer monoBuf = ByteBuffer.allocate(sound.length/2);
		for(int i=0;i<sound.length/4;i++)
		{
			short myShorty = stereoBuf.getShort();
			stereoBuf.getShort(); //discard the right channel
			monoBuf.putShort(myShorty);
		}
		
		return(monoBuf.array());
	}
	
	
	
	
	
	//a static method for saving sound bytes to a file. Assumes 16-bit samples at 44,100hz
	public static boolean writeWaveBytesToFile(byte[] data, File file, int channels) 
	{
		ByteArrayInputStream bAIS = new ByteArrayInputStream(data);
		
		AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;

		int framesize = 2*channels;
		AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100.0F, 16, channels,framesize,44100.0F, false);
		
		AudioInputStream audioInputStream = new AudioInputStream(bAIS,audioFormat ,data.length);

		
		try
		{
			AudioSystem.write(audioInputStream, type, file);
		}
		catch(IOException e)
		{
			System.out.println("Error writing to Wave file.");
			return false;
		} 
		
		try
		{
			audioInputStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Error Closing AudioInputStream");
			return false;
		}
		
		System.out.println(data.length+" bytes written to file "+file);
		return true;
	}
    
	
	
    /*
     * a static method for reading in a byte array of audio from a sound file. This
	 * throws an exception so that the process that calls it can know about this.
	 * 
	 */
	public static byte[] getAudioFromURL(java.net.URL url)
	{
		BufferedInputStream stream = null;

		try{
			stream = new BufferedInputStream(url.openStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
			
			return(getAudioFromStream(stream));

	}
	
	
	
	public static byte[] getAudioFromFile(java.io.File file)
	{
		BufferedInputStream fis = null;
		try{
			FileInputStream input = new FileInputStream(file);
			fis = new BufferedInputStream(input);
		}
		catch(FileNotFoundException f)
		{
			f.printStackTrace();
		}
		return(getAudioFromStream(fis));
	}
	
	
	
    public static byte[] getAudioFromStream(BufferedInputStream stream)
    {

        AudioInputStream audioInputStream = null;
        //SourceDataLine	line = null;
        ByteArrayOutputStream array;

  
        array= new ByteArrayOutputStream();

        //System.out.println("soundFile exists? "+soundFile.exists());

        try
        {
                audioInputStream = AudioSystem.getAudioInputStream(stream);
        }
        catch (Exception e)
        {
                //e.printStackTrace();
        	e.printStackTrace();
        }

        //AudioFormat	audioFormat = audioInputStream.getFormat();
        //DataLine.Info	info = new DataLine.Info(SourceDataLine.class,audioFormat);
       // try
        //{
            //line = (SourceDataLine) AudioSystem.getLine(info);
            //line.open(audioFormat);
        //}
        //catch (LineUnavailableException e)
        //{
            //e.printStackTrace();
        	//e.printStackTrace();
        //}
        //catch (Exception e)
        //{
            //e.printStackTrace();
        	//e.printStackTrace();
        //}
        //line.start(); 

        int	nBytesRead = 0;
        byte[] abData = new byte[256];
        while (nBytesRead != -1)
        {
            try
            {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
            }
            catch (IOException e)
            {
                //e.printStackTrace();
            	e.printStackTrace();
                //System.out.println("sound file didn't load.");
            }
            if (nBytesRead >= 0)
            {

            	array.write(abData,0,nBytesRead);
            }
        }
        try
        {
            audioInputStream.close();
        }
        catch(IOException iOE) 
        {
        	iOE.printStackTrace();
            //System.out.println("getAudioFromFile: I have to quit. bye.");
        }
        //line.drain();
        //line.close();

        //metronome = array.toByteArray();
        System.out.println(array.size()+" bytes read from input stream ");
        return(array.toByteArray());
    }  
    
    
    
    
    /* 
     * Here's a function that multiplies each sample by 0.5 attenuation and adds two sounds up 
     * sample by sample and returns a combined sound
     */
    public static byte[] addTwoSounds(byte[] sound0, byte[]sound1) 
    {
    
    	/* do we need to flip endianness if we are on a Intel chipset? */
        short[] sound0Short = byte2Short(sound0,true);
        short[] sound1Short = byte2Short(sound1,true);
        
        int length0 = sound0Short.length;
        int length1 = sound1Short.length;
        
        //System.out.println("length0="+length0);
        //System.out.println("length1="+length1);
        
        short[] combined;
        // add the smaller sound to the bigger sound
        if(length0>length1)
        {
        	
            combined = sound0Short;
            for(int i=0;i<length1;i++)
            {
                combined[i]=(short)((combined[i]*0.5+sound1Short[i]*0.5));
            }
        }
        else
        {
            combined = sound1Short;
            for(int i=0;i<length0;i++)
            {
                combined[i]=(short)((combined[i]*0.5+sound0Short[i]*0.5));
            }
        }
        
        byte[] soundBytes = short2byte(combined);
        //System.out.println("byte[].length="+soundBytes.length);
        return(soundBytes);
    }
    
    
    
    /* convert audio bytes to shorts 
    public static short[] byte2short(byte byteArray[])
    {
        if(byteArray == null)
        {
            return null;
        }
        short shortArray[] = new short[byteArray.length / 2];
        for(int i = 0; i < byteArray.length; i += 2)
        {
            shortArray[i / 2] = (short)((byteArray[i] & 0xff) + ((short)byteArray[i + 1] << 8 & 0xff00));
        }

        return shortArray;
    }
    */
    /*
   public static short[] byte2Short(byte[] byteArray)
   {
	   ByteBuffer buf = ByteBuffer.wrap(byteArray);
	   short[] shortArray = new short[(int)Math.ceil(((float)byteArray.length)/2)];
	   for(int i=0;i<shortArray.length;i++)
	   {
		   shortArray[i] = buf.getShort();
	   }
	   return(shortArray);
   } */
    public static final short[] byte2Short(byte[] inData,boolean byteSwap) {
    	//int j=0;
    	int length=inData.length/2;
    	short[] outData=new short[length];
    	if (!byteSwap) for (int i=0,j=0;i<length;i++,j+=2) {
    		//j=i*2;
    		//outData[i]=(short)( ((inData[j] & 0xff) << 8) + ((inData[j+1] & 0xff) << 0 ) );
    		outData[i]=(short)( (inData[j] << 8) + (inData[j+1] & 0xff) );
    	}
    	else for (int i=0;i<length;i++) {
    		int j=i*2;
    		outData[i]=(short)( ((inData[j+1] & 0xff) << 8) + ((inData[j] & 0xff) << 0) );
    	}

    	return outData;
    }
    
    /* convert audio samples to bytes*/
    public static byte[] short2byte(short shortArray[])
    {
        if(shortArray == null){
            return null;
        }
        byte byteArray[] = new byte[shortArray.length * 2];
        for(int i = 0; i < shortArray.length; i++)
        {
            short anChar = shortArray[i];
            byteArray[i * 2] = (byte)(anChar & 0xff);
            byteArray[i * 2 + 1] = (byte)(anChar >>> 8 & 0xff);
        }

        return byteArray;
    }     
    public static float[] byte2Float(byte byteArray[])
    {
    	ByteBuffer buf = ByteBuffer.wrap(byteArray);
    	float[] floatArray = new float[(int)Math.ceil((float)byteArray.length/4)];
    	for(int i=0;i<floatArray.length;i++)
    	{
    		floatArray[i] = buf.getFloat();
    	}
    	return(floatArray);
    	
    }
    /*
    public static float[] byte2Float(byte byteArray[])
    {
        if(byteArray == null)
        {
        	System.out.println("byte2Float:Error:byteArray[] is null");
            return null;
        }
        float floatArray[] = new float[(int)Math.ceil((float)byteArray.length / 4)];
        for(int i = 0; i < byteArray.length; i += 2)
        {
            floatArray[(int)Math.ceil((float)i / 4)] = (float)((byteArray[i] & 0xff) + ((float)(byteArray[i + 1] << 8 & 0xff00)));
        }

        return floatArray;
    } */
    public static double[] byte2Double(byte[] byteArray)
    {
    	ByteBuffer buf = ByteBuffer.wrap(byteArray);
    	double[] doubleArray = new double[(int)Math.ceil((float)byteArray.length/2)];
    	for(int i=0;i<doubleArray.length;i++)
    	{
    		doubleArray[i] = ((double)buf.getShort())/32767.0f;
    		//doubleArray[i] =  temp* Float.MAX_VALUE;
    	}
    	return(doubleArray);
    }

    
    /* this is just a convienience function for converting a floating point array of nums [-1,1] to [-2^15, 2^15] to shorts, to bytes
     * This is useful when one wants to write to a .wave file and what you have are floats between -1 and 1. Typically a player 
     * expects shorts (16-bits per sample).
     * */
    public static byte[] float2NormalizedShort2Byte(float floatArray[])
    {
    	short shortArray[] = new short[floatArray.length];
    	
    	for(int i=0;i<shortArray.length;i++)
    	{
    		shortArray[i] = (short)(floatArray[i]*32768f);
    	}
    	
    	byte[] byteArray = Util.short2byte(shortArray);
    	
    	return(byteArray);
    }
    
    /*
    public byte[] float2Byte(float floatArray[])
    {
    	byte[] byteArray = new byte[floatArray.length*4];
    	for(int i=0;i<floatArray.length;i++)
    	{
    		floatToByteArray(floatArray[i],byteArray, i*4);
    	}
    	return(byteArray);
    }
    private void floatToByteArray(float value,byte[] array,int offset)
    {
        int intBits=Float.floatToIntBits(value);
        array[offset+0]=(byte)((intBits&0x000000ff)>>0);
        array[offset+1]=(byte)((intBits&0x0000ff00)>>8);
        array[offset+2]=(byte)((intBits&0x00ff0000)>>16);
        array[offset+3]=(byte)((intBits&0xff000000)>>24);
    } */
    
    public static byte[] float2Byte(float floatArray[])
    {
    	ByteBuffer buf = ByteBuffer.allocate(floatArray.length*4);
    	
    	for(int i=0;i<floatArray.length;i++)
    	{
    		buf.putFloat(floatArray[i]);
    	}
    	return(buf.array());
    	
    }  
    /* 
    public static byte[] float2Byte(float floatArray[])
    {
        if(floatArray == null){
            return null;
        }
        byte byteArray[] = new byte[floatArray.length * 4];
        for(int i = 0; i < floatArray.length; i++)
        {
            short anChar = (short)floatArray[i];
            byteArray[i * 2] = (byte)(anChar & 0xff);
            byteArray[i * 2 + 1] = (byte)(anChar >>> 8 & 0xff);
        }
        
        return byteArray;
    }  

    */
    
    
    
    
    
    
    /* These next few methods are for retrieving a TargetDataLine 1)of a specified format
     * and starting buffer and additionally 2) from a specified mixer. This is used by the 
     * Recorder AND additionally can be used to test to see if the line is supported and 
     * available for use, since some of them throw LineUnavailableExceptions
     */
    
    
    
    /**
     * public static TargetDataLine getDefaultTargetLine(AudioFormat audioFormat,
     * 											  int internalBufferSize)
     * 
     * 
	 * Returns a TargetDataLine (a recording line) from the Java Sound audio
	 * system with the supplied attributes. If it cannot get the line it will
	 * throw an exception.
	 *
	 * @param  AudioFormat audioFormat The desired Format of the audio to be obtained from, or written, to the line.
	 * @param int internalBufferSize the internalBuffer size of java sounds internal circular buffer
	 * @return      TargetDataLine
	 * @see         IUAudio.Util, javax.sound.sampled.AudioFormat
	 */
    public static TargetDataLine getDefaultTargetLine(AudioFormat audioFormat,int internalBufferSize) 
    {
        TargetDataLine	targetDataLine = null;
        try 
        {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(new Line.Info(TargetDataLine.class));			
            targetDataLine.open(audioFormat,internalBufferSize);
        }
        catch (LineUnavailableException e)
        {
            System.out.println("Unable to get a recording line.");
            e.printStackTrace();
            System.exit(1);
        }
        return(targetDataLine);
    }
    
    /**
     * public static TargetDataLine getTargetLine(AudioFormat audioFormat,
     * 											  int whichMixer, 
     * 											  int internalBufferSize)
     * 
     * 
	 * Returns a TargetDataLine (a recording line) from the Java Sound audio
	 * system with the supplied attributes. If it cannot get the line it will
	 * throw an exception.
	 *
	 * @param  AudioFormat audioFormat The desired Format of the audio to be obtained from, or written, to the line.
	 * @param int whichMixer the index of Mix.Info's of the Mixer you want (obtained from AudioSystem.getMixers())
	 * @param int internalBufferSize the internalBuffer size of java sounds internal circular buffer
	 * @return      TargetDataLine
	 * @throws LineUnavailableException 
	 * @see         IUAudio.Util, javax.sound.sampled.AudioFormat
	 */
    public static TargetDataLine getTargetLine(AudioFormat audioFormat,
            int whichMixer, int internalBufferSize) throws LineUnavailableException 
    {
        
        TargetDataLine	targetDataLine = null;
        
        try 
        {
            Mixer.Info[] infos = AudioSystem.getMixerInfo();
            Mixer mixer = AudioSystem.getMixer(infos[whichMixer]);
            
            targetDataLine = (TargetDataLine) mixer.getLine(new Line.Info(TargetDataLine.class));
            //targetDataLine.open(audioFormat,internalBufferSize);
            targetDataLine.open(audioFormat);
        }
        catch (LineUnavailableException e)
        {
            //e.printStackTrace();
            //System.exit(1);
            System.out.println("Unable to get a recording line from mixer"+whichMixer+".");
            throw new LineUnavailableException();   
        }
        return(targetDataLine);
    }
    
    /**
     * 
     * public static TargetDataLine getTargetLine(AudioFormat audioFormat,
     * 											  String mixerName, 
     * 											  int internalBufferSize)
     * 
     * 
	 * Returns a TargetDataLine (a recording line) from the Java Sound audio
	 * system with the supplied attributes. If it cannot get the line it will
	 * throw an exception.
	 *
	 * @param  AudioFormat audioFormat The desired Format of the audio to be obtained from, or written, to the line.
	 * @param String the name of the Mixer Mix.Info's you want (obtained from AudioSystem.getMixers())
	 * @param int internalBufferSize the internalBuffer size of java sounds internal circular buffer
	 * @return      TargetDataLine
	 * @throws LineUnavailableException 
	 * @see         IUAudio.Util, javax.sound.sampled.AudioFormat
	 */
    public static TargetDataLine getTargetLine(AudioFormat audioFormat,
            String mixerName, int internalBufferSize) throws LineUnavailableException 
    {
        
        TargetDataLine	targetDataLine = null;
        int whichMixer =0;
        try 
        {
            Mixer.Info[] infos = AudioSystem.getMixerInfo();
            
            for(int i=0;i<infos.length;i++)
            {
            	if(infos[i].getName().compareTo(mixerName)==0){
            		whichMixer = i;
            	}
            }
            Mixer mixer = AudioSystem.getMixer(infos[whichMixer]);
                   
            targetDataLine = (TargetDataLine) mixer.getLine(new Line.Info(TargetDataLine.class));
            //targetDataLine.open(audioFormat,internalBufferSize);
            targetDataLine.open(audioFormat,internalBufferSize);
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
            System.out.println("Unable to get a recording line from mixer"+whichMixer+".");
            throw new LineUnavailableException();   
        }
        return(targetDataLine);
    }
    
    
    
    
    
    
    
    
    
    /* Likewise, here are the methods for playback lines */
    
    /**
     * public static SourceDataLine getDefaultSourceLine(AudioFormat audioFormat,
     * 													 int internalBufferSize)
     * 
     * 
	 * Returns a SourceDataLine (a playback line) from the Java Sound audio
	 * system with the supplied attributes. If it cannot get the line it will
	 * throw an exception.
	 *
	 * @param AudioFormat audioFormat The desired Format of the audio to be obtained from, or written, to the line.
	 * @param int internalBufferSize the internalBuffer size of java sounds internal circular buffer
	 * @return      SourceDataLine
	 * @see         IUAudio.Util,javax.sound.sampled.AudioFormat
	 */
    
    public static SourceDataLine getDefaultSourceLine(AudioFormat audioFormat,int internalBufferSize) 
    {
        DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine	sourceDataLine = null;
        try 
        {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);			
            sourceDataLine.open(audioFormat,internalBufferSize);
        }
        catch (LineUnavailableException e)
        {
            System.out.println("Unable to get a playback line.");
            e.printStackTrace();
            System.exit(1);
        }
        return(sourceDataLine);
    }
    
    
    /**
     * public static SourceDataLine getSourceLine(AudioFormat audioFormat,
            									  int whichMixer, 
            									  int internalBufferSize)
     * 
     * 
	 * Returns a SourceDataLine (a playback line) from the Java Sound audio
	 * system with the supplied attributes. If it cannot get the line it will
	 * throw an exception.
	 *
	 * @param  AudioFormat audioFormat The desired Format of the audio to be obtained from, or written, to the line.
	 * @param int whichMixer The index of the Mixer in Mix.Info's you want (obtained from AudioSystem.getMixers())
	 * @param int internalBufferSize the internalBuffer size of java sounds internal circular buffer
	 * @return      SourceDataLine
	 * @see         IUAudio.Util, javax.sound.sampled.AudioFormat
	 */
    
    
    public static SourceDataLine getSourceLine(AudioFormat audioFormat,
            int whichMixer, int internalBufferSize) throws LineUnavailableException 
    {
        
        SourceDataLine	sourceDataLine = null;
        
        try {
            Mixer.Info[] infos = AudioSystem.getMixerInfo();
            Mixer mixer = AudioSystem.getMixer(infos[whichMixer]);
            //mixer.open();

            DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
            
            sourceDataLine = (SourceDataLine) mixer.getLine(info);		
            sourceDataLine.open(audioFormat,internalBufferSize);
        }
        catch (LineUnavailableException e)
        {

            System.out.println("Unable to get a playback line from mixer "+whichMixer+".");
            throw e; 
        }
        
        return(sourceDataLine);
    }
    /**
     * public static SourceDataLine getSourceLine(AudioFormat audioFormat,
     *      									  String mixerName, 
     *      									  int internalBufferSize)
     * 
     * 
	 * Returns a SourceDataLine (a playback line) from the Java Sound audio
	 * system with the supplied attributes. If it cannot get the line it will
	 * throw an exception.
	 *
	 * @param AudioFormat audioFormat The desired Format of the audio to be obtained from, or written, to the line.
	 * @param String mixerName the name of the Mixer Mix.Info's you want (obtained from AudioSystem.getMixers())
	 * @param int internalBufferSize the internalBuffer size of java sounds internal circular buffer
	 * @return      SourceDataLine
	 * @see         IUAudio.Util, javax.sound.sampled.AudioFormat
	 */
    public static SourceDataLine getSourceLine(AudioFormat audioFormat,
           String mixerName, int internalBufferSize) throws LineUnavailableException 
    {
        
        SourceDataLine	sourceDataLine = null;
        int whichMixer=0;
        try {
            Mixer.Info[] infos = AudioSystem.getMixerInfo();
            for(int i=0;i<infos.length;i++)
            {
            	if(infos[i].getName().compareTo(mixerName)==0){
            		whichMixer = i;
            	}
            }
            Mixer mixer = AudioSystem.getMixer(infos[whichMixer]);

            DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
            
            sourceDataLine = (SourceDataLine) mixer.getLine(info);		
            sourceDataLine.open(audioFormat,internalBufferSize);
        }
        catch (LineUnavailableException e)
        {

            System.out.println("Unable to get a playback line from mixer "+whichMixer+".");
            throw e; 
        }
        
        return(sourceDataLine);
    }
    
    
}
