package IUAudio;



import java.io.*;




public class AudioLogger {

	
	private int part=0;
	private int maxPartFileSize = 10240000*10; //10 megabytes.
	private String file;
	private ByteArrayOutputStream baos;

	public AudioLogger(String fileName)
	{
		this.file = fileName;
		this.baos = new ByteArrayOutputStream();
	}
	
	public void logAudioBytes(final byte[] bytes)
	{
		/* all bytes are actually written through the 
		 * logAudioBytes(byte[],int,int) function just below 
		 */
		logAudioBytes(bytes,0,bytes.length);

	}
	
	
	public void logAudioBytes(final byte[] bytes, final int offset, final int numBytes)
	{
		for(int i=offset;i<numBytes;i++)
		{
			baos.write(bytes[i]);
		}
		if(baos.size()>=maxPartFileSize)
		{
			toFile(this.file+"part."+this.part+".wav",true);
			this.part++;
		}
	}
	
	/* AudioLogger automatically saves audio in .part files and then consolidates them into one
	 * with a call to consolidate. However, if you wish to manually save the log to the file, 
	 * this can be accomplished by calling toFile, otherwise this class will handle that for you.
	 */
	
	
	private void toFile(String fileName, boolean clearBuffer)
	{		
		Util.writeWaveBytesToFile(AudioLogger.this.baos.toByteArray(), new File(fileName),2);
		/*
		try {
			FileOutputStream fos = new FileOutputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} */
		//com.sun.media.sound.WaveFileWriter w = new com.sun.media.sound.WaveFileWriter();
		

		if(clearBuffer) baos.reset();
	}
	
	/* 
	 * a call to flush will write all remaining bytes to a file, conveniently without having to
	 * specify a file name or path.
	 */
	public void flush()
	{
		toFile(this.file+"part."+this.part+".wav",true);
		this.part++;
	}
	

	
	
	/* consolidatePartFiles will take all the .part files and consolidate them into one sound file. Call this
	 * when you have finished logging sound.
	 */
	
	public void consolidatePartFiles()
	{
		consolidatePartFiles(this.file);
	}
	
	public void consolidatePartFiles(String wavFile)
	{
		try{
			ByteArrayOutputStream tempBaos = new ByteArrayOutputStream();
			for(int i=0;i<part;i++)
			{
				File curFile = new File(this.file+".part"+i+".wav");
				byte[] curSoundBytes = Util.getAudioFromFile(new File(curFile.getAbsolutePath()));
				tempBaos.write(curSoundBytes);
				curFile.delete();
			}
			
			Util.writeWaveBytesToFile(tempBaos.toByteArray(), new File(wavFile),2);
		}
		catch(IOException e) {e.printStackTrace();}
		catch(Exception e) { e.printStackTrace();};
	}
	
	
	/* purgePartFiles destroys the part files. Do this if you don't want to save any of the previously logged
	 * sound.
	 */
	
	public void purgePartFiles()
	{
		int numFilesDeleted = 0;
		
		for(int i=0;i<part;i++)
		{
			File curFile = new File(this.file+".part"+i);
			if(curFile.exists())
			{
				curFile.delete();
				numFilesDeleted++;
			}
		}
		System.out.println("AudioLogger:"+numFilesDeleted+" .part files were removed from the file system.");
	}
	
}
