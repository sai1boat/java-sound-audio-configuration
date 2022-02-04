package IUAudio;

public class DSP {

	
	
	
	public static double rms(final short[] sound){

		double sumSquares = 0d;

		for(int i=0;i<sound.length;i++)
		{
			sumSquares+=Math.pow(sound[i], 2);
		}	
		final double rms = Math.sqrt(sumSquares/sound.length);
		return(rms);
	}
	
	public static double rms(final byte[] sound){
		
		final short[] s = IUAudio.Util.byte2Short(sound, false);
		return(rms(s));
	}
	
	
	
	
	public static byte[] attenuate(byte[] sound,double attenuationFactor){
		short[] tempShortArray = IUAudio.Util.byte2Short(sound,true);

		  for(int n=0;n<tempShortArray.length;++n){
			  tempShortArray[n] = (short)((double)tempShortArray[n]*attenuationFactor);
		  }
		  final byte[] tempByteArray = IUAudio.Util.short2byte(tempShortArray);
		  return(tempByteArray);
	}


	public static void main(String[] args)
	{
		byte[] sound = Util.getAudioFromFile(new java.io.File("sinewave.wav"));
		System.out.println("rms of a 440hz sound wav is "+rms(sound));
	}
}