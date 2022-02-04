/**
 * 
 */
package IUAudio;

import static org.junit.Assert.*;

import javax.sound.sampled.AudioFormat;

import org.junit.Before;
import org.junit.Test;

/**
 * @author john
 *
 */
public class UtilTest {

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

	/**
	 * Test method for {@link IUAudio.Util#writeWaveBytesToFile(byte[], java.io.File)}.
	 */
	@Test
	public void testWriteWaveBytesToFile() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#getAudioFromFile(java.lang.String)}.
	 */
	@Test
	public void testGetAudioFromFile() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#addTwoSounds(byte[], byte[])}.
	 */
	@Test
	public void testAddTwoSounds() {
		byte[] sound = null;
		byte[] otherSound = null;
		try{
		sound = Util.getAudioFromFile(new java.io.File("Sinewave.wav"));
		
		for(int i=0;i<90;i++)
		{
			otherSound = Util.addTwoSounds(sound, sound);
		}
		//otherSound = Util.getAudioFromFile("Sinewave.wav");
		
		}catch(Exception e){e.printStackTrace();};
		
		
		for(int i=0;i<3;i++)
			bytePlayer.play(otherSound,0,otherSound.length);
		//bytePlayer.play(sound,0,sound.length);
		
	}

	/**
	 * Test method for {@link IUAudio.Util#byte2Short(byte[], boolean)}.
	 */
	@Test
	public void testByte2Short() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#short2byte(short[])}.
	 */
	@Test
	public void testShort2byte() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#byte2Float(byte[])}.
	 */
	@Test
	public void testByte2Float() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#float2NormalizedShort2Byte(float[])}.
	 */
	@Test
	public void testFloat2NormalizedShort2Byte() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#float2Byte(float[])}.
	 */
	@Test
	public void testFloat2Byte() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#getDefaultTargetLine(javax.sound.sampled.AudioFormat, int)}.
	 */
	@Test
	public void testGetDefaultTargetLine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#getTargetLine(javax.sound.sampled.AudioFormat, int, int)}.
	 */
	@Test
	public void testGetTargetLine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#getDefaultSourceLine(javax.sound.sampled.AudioFormat, int)}.
	 */
	@Test
	public void testGetDefaultSourceLine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link IUAudio.Util#getSourceLine(javax.sound.sampled.AudioFormat, int, int)}.
	 */
	@Test
	public void testGetSourceLine() {
		fail("Not yet implemented");
	}

}
