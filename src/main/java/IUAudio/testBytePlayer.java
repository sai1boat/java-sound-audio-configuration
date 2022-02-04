package IUAudio;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javax.sound.sampled.*;
import java.io.File;

public class testBytePlayer {

	private BytePlayer bytePlayer;
	
	@Before
	public void setUp() throws Exception {
		//int whichMixer = 0;
		int internalBuf = 4096*2;
		int externalBuf = 512;
		bytePlayer = new BytePlayer(new AudioFormat(
	            AudioFormat.Encoding.PCM_SIGNED,44100.0F, 16, 1,2,44100.0F, false),
	            externalBuf,internalBuf);
	}

	@Test
	public void testBytePlayerAudioFormatIntIntInt() {
		byte[] sound = null;
		try{
		sound = Util.getAudioFromFile(new File("Sinewave.wav"));
		}catch(Exception e){e.printStackTrace();};
		for(int i=0;i<20;i++)
		{
			bytePlayer.play(sound,0,sound.length);
		}

		byte[] sound2 = null;
		try{
			sound2 = Util.getAudioFromFile(new File("wavLoop.wav"));
		}catch(Exception e){e.printStackTrace();};
		for(int i=0;i<20;i++)
		{
			bytePlayer.play(sound2,0,sound2.length);
		}
		System.out.println("Heard any obvious artifacting? Then the test probably fails");
	}
}
