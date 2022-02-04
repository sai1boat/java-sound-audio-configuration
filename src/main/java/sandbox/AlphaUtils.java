package sandbox;


import javax.sound.sampled.*;

public class AlphaUtils {

	
	
	
	
	public static void printTargetFileEncodings()
	{
		//Encoding[] encodings = org.tritonus.share.sampled.Encodings.getEncodings();
		
		AudioFileFormat.Type[] canWriteTo = AudioSystem.getAudioFileTypes();
		System.out.print("The following Audio formats can be written: ");
		for(int i=0;i<canWriteTo.length;i++)
		{
			System.out.print(canWriteTo[i]+", ");
		}
		System.out.println("");
	}
	public static void printEncodings()
	{
		
		
	}
	
	public static void main(String args)
	{
		System.out.println("hello world.");
	}
	
}
