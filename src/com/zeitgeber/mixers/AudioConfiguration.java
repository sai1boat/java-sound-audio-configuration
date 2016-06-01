package com.zeitgeber.mixers;

import java.util.HashMap;
import javax.swing.*;
import java.awt.Component;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.util.prefs.*;




public class AudioConfiguration {

	private static HashMap<String,Mixer> m;
	private static Mixer inputMixer = null;
	private static Mixer outputMixer = null;
	private static int defaultInternalBufferSize;
	private static int defaultExternalBufferSize;
	private static EchoTest et;
	private static boolean go = true;
	static Preferences prefs;
	

	private static JDialog cFrame;
	private static JDialog bufferDialog;
	private static JTextField iField;
	private static JTextField eField;
	private static JTextField cField;
	
	public void setLocationRelativeTo(Component c){
		cFrame.setLocationRelativeTo(c);
	}
	public void setVisible(boolean b){
		cFrame.setVisible(b);
	}

    /**
     * public AudioConfiguration(JFrame parent, 
     * 								String configurationPath,
     * 								int defaultInternalBufSize, 
     * 								int defaultExternalBufSize)
     * 
     * 
	 * Creates an AudioConfiguration dialog that can be used to set, test, and save configuration settings. This sets the "inputMixer",
	 * 	"outputMixer", "internalBufferSize", and "externalBufferSize" preferences in the preferences object associated with the specified
	 * 	configuration path. These values can be retrieved in other code and other projects by using the Preferences API with that 
	 * 	configuration path.
	 *
	 * @param  JFrame parent This is the frame that is the parent of the dialog. The dialog's position is set relative to the parent frame.
	 * @param String configurationPath This is the path node name of the configuration. This can be used to store and retrieve configuration sets. e.g. "/com/myproject/configuration1".
	 * @param int defaultInternalBufSize In the absence of the preference key internalBufferSize for a configuration, this value will be used instead.
	 * @param int defaultExternalBufSize In the absence of the preference key externalBufferSize for a configuration, this value will be used instead.
	 * @see         IUAudio.Util, javax.sound.sampled.AudioFormat
	 * 
	 */
	
	
	public AudioConfiguration(JFrame parent, String configurationPath,int defaultInternalBufSize, int defaultExternalBufSize){
		
		 prefs= Preferences.systemRoot().node(configurationPath);
		 this.defaultInternalBufferSize = defaultInternalBufSize;
		 this.defaultExternalBufferSize = defaultExternalBufSize;
		 
		/*
		 * Grab all available mixers, and put them into a hashmap
		 * keyed by mixer name. That way, we can save the mixer name
		 * as a package-level preference and be able to retrieve
		 * it and use it next time, without going through this
		 * configuration step ever time.
		 * 
		 */
		
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		m = new HashMap<String, Mixer>();
		for(int i=0;i<mixers.length;i++)
		{
			m.put(mixers[i].getName(), AudioSystem.getMixer(mixers[i]));
		}	
		
		/* the main frame */
		
		cFrame = new JDialog(parent,"Configure sound I/O",true);
		//cFrame.setSize(new java.awt.Dimension(200,300));
		cFrame.setLocationRelativeTo(parent);
		cFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent evt){
				cFrame.dispose();
			}
		});
		
		
		/* a nice infomative message */
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new java.awt.GridLayout(0,1));
		String info =" Make a selection for both Input and Output.";
		String info2 = "A Dialog will show if that selection will not work.";
		String info3 = "Test can be used to actually hear if it's working.";
		JLabel infoLabel = new JLabel(info);
		messagePanel.add(infoLabel);
		
		JLabel infoLabel2 = new JLabel(info2);
		messagePanel.add(infoLabel2);
		JLabel infoLabel3 = new JLabel(info3);
		messagePanel.add(infoLabel3);
		cFrame.add(messagePanel);
		
		
		
		JPanel mixerFrame = new JPanel();
		mixerFrame.setLayout(new java.awt.GridLayout(0,1));
		mixerFrame.setPreferredSize(new java.awt.Dimension(300,400));

		ButtonGroup inputGroup = new ButtonGroup();
		//mixerFrame.add(new JLabel("Input driver"));
		JPanel inputFrame = new JPanel();
		inputFrame.setBorder(BorderFactory.createTitledBorder("Input driver"));
		inputFrame.setLayout(new java.awt.GridLayout(0,1));
		
		for(int i=0;i<mixers.length;i++)
		{
			String name = mixers[i].getName();
			JRadioButton button = new JRadioButton(name);
			if(prefs.get("inputMixer", "").compareTo(name)==0){
				button.setSelected(true);
				this.inputMixer = AudioSystem.getMixer(mixers[i]);
			}
			button.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent evt){
					final JRadioButton button = (JRadioButton)evt.getSource();
					inputMixer = m.get(button.getText());
					try {
						TargetDataLine r_line =(TargetDataLine)inputMixer.getLine(new Line.Info(TargetDataLine.class));
						r_line.close();
					} catch (Exception e) {
						javax.swing.JOptionPane.showMessageDialog(cFrame, "Line unavailable, please choose another driver");
						e.printStackTrace();
					}finally{
						System.out.println("Driver set to "+inputMixer);
						prefs.put("inputMixer", inputMixer.getMixerInfo().getName());
					}
				}
			});
			inputGroup.add(button);
			inputFrame.add(button);
		}
		mixerFrame.add(inputFrame,java.awt.BorderLayout.NORTH);
		
		
		
		//mixerFrame.add(new JLabel("Output driver"));
		JPanel outputFrame = new JPanel();
		outputFrame.setBorder(BorderFactory.createTitledBorder("Output driver"));
		outputFrame.setLayout(new java.awt.GridLayout(0,1));
		
		ButtonGroup outputGroup = new ButtonGroup();
		for(int i=0;i<mixers.length;i++)
		{
			String name = mixers[i].getName();
			JRadioButton button = new JRadioButton(name);
			if(prefs.get("outputMixer", "").compareTo(name)==0){
				button.setSelected(true);
				AudioConfiguration.outputMixer = AudioSystem.getMixer(mixers[i]);
			}
			button.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent evt){
					final JRadioButton button = (JRadioButton)evt.getSource();
					AudioConfiguration.outputMixer = m.get(button.getText());
					try {
						SourceDataLine p_line =(SourceDataLine)outputMixer.getLine(new Line.Info(SourceDataLine.class));
						p_line.close();
					} catch (Exception e) {
						javax.swing.JOptionPane.showMessageDialog(cFrame, "Line unavailable, please choose another driver");
						e.printStackTrace();
					}finally{
						System.out.println("Driver set to "+outputMixer);
						prefs.put("outputMixer", outputMixer.getMixerInfo().getName());
					}
				}
			});
			outputGroup.add(button);
			outputFrame.add(button);
		}
		mixerFrame.add(outputFrame, java.awt.BorderLayout.SOUTH);
		
		
		cFrame.add(mixerFrame,java.awt.BorderLayout.NORTH);
		
		
		
		
		
		JPanel buttonFrame = new JPanel();

		JButton advancedButton = new JButton("Advanced");
		advancedButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
					bufferDialog = new JDialog(cFrame,"Advanced",true);
					bufferDialog.setLayout(new java.awt.GridLayout(4,2));
					bufferDialog.setLocationRelativeTo(cFrame);
					
					JLabel iLabel = new JLabel("Internal buffers");
					JLabel eLabel = new JLabel("External buffers");
					JLabel cLabel = new JLabel("Channels");
					
					
					iField = new JTextField(8);
					eField = new JTextField(8);
					cField = new JTextField(8);
					
					/* defaults */
					iField.setText(prefs.get("internalBufferSize", defaultInternalBufferSize+""));
					eField.setText(prefs.get("externalBufferSize", defaultExternalBufferSize+""));
					cField.setText(prefs.get("channels",""+1));
					
					bufferDialog.add(iLabel);
					bufferDialog.add(iField);
					bufferDialog.add(eLabel);
					bufferDialog.add(eField);
					bufferDialog.add(cLabel);
					bufferDialog.add(cField);
					
					
					JButton cancelButton = new JButton("Cancel");
					cancelButton.addActionListener(new ActionListener(){
						public void actionPerformed(java.awt.event.ActionEvent evt){
							bufferDialog.dispose();
						}
					});
					bufferDialog.add(cancelButton);
					
					JButton bufferOkButton = new JButton("Ok");
					bufferOkButton.addActionListener(new ActionListener(){
						public void actionPerformed(java.awt.event.ActionEvent evt){
							
							/* check the validity of the input args */
							
							prefs.put("internalBufferSize", iField.getText());
							prefs.put("externalBufferSize", eField.getText());
							prefs.put("channels", cField.getText());
							bufferDialog.dispose();
						}
					});
					bufferDialog.add(bufferOkButton);
					bufferDialog.pack();
					bufferDialog.setVisible(true);
					
					
				}
			}
		);
		buttonFrame.add(advancedButton,java.awt.BorderLayout.WEST);
		
		JButton testButton = new JButton("Test");
		testButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if(et==null){
					try {
						et = new EchoTest(inputMixer,outputMixer);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
						javax.swing.JOptionPane.showMessageDialog(cFrame, e.getClass()+"\n This configuration is not supported.");
						return;	//return from actionPerformed
					}
				}
				javax.swing.JOptionPane.showMessageDialog(cFrame, "Speak into the microphone. \nYou should hear an echo of your voice. \nclick OK to stop the test.");
				if(et!=null){
					et.stopPlaying();
					et=null;
				}
			}
		});
		buttonFrame.add(testButton,java.awt.BorderLayout.CENTER);
		
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if(et!=null){
					et.stopPlaying();
					et=null;
				}
				cFrame.dispose();
			}
		});
		buttonFrame.add(okButton,java.awt.BorderLayout.EAST);
		
		cFrame.add(buttonFrame,java.awt.BorderLayout.SOUTH);
		cFrame.pack();
		cFrame.setVisible(true);	
	}
	
	
	
	
	
	
	
	private class EchoTest extends Thread{
		
		TargetDataLine r_line = null;
		SourceDataLine p_line = null;
		boolean keepGoing = true;
		
		public EchoTest(Mixer inputMixer, Mixer outputMixer)throws LineUnavailableException{
			

			try {
				int channels = Integer.parseInt(prefs.get("channels", 1+""));
				int bitDepth = 16;
				int frameSize = channels*bitDepth/8;
				int internalBufferSize = Integer.parseInt(prefs.get("internalBufferSize", 102400+""));
				String test_inputMixer = prefs.get("inputMixer", "Java Sound Audio Engine");
				String test_outputMixer = prefs.get("outputMixer", "Java Sound Audio Engine");
				AudioFormat audioFormat = new AudioFormat(
		                AudioFormat.Encoding.PCM_SIGNED,44100.0f, bitDepth, channels,frameSize,44100.0f, false);
				r_line = IUAudio.Util.getTargetLine(audioFormat, test_inputMixer,internalBufferSize);
				p_line = IUAudio.Util.getSourceLine(audioFormat, test_outputMixer,internalBufferSize);
				
				String success = "Audio system has successfully been initialized with the following format/options:";
				success+="\n channels "+channels+" bitDepth "+bitDepth+" frameSize "+frameSize+" internalBufferSize "+internalBufferSize;
				System.out.println(success);
				
			} catch (LineUnavailableException e) {
				throw(e);
				//return;
			} 
			
				
			r_line.start();
			p_line.start();
			this.start();
		}	
		public void run(){
			int externalBufferSize = Integer.parseInt(prefs.get("externalBufferSize", "8192"));
			while (keepGoing){
				byte[] buf = new byte[externalBufferSize];
				r_line.read(buf,0,buf.length);
				p_line.write(buf, 0, buf.length);
			}
			p_line.close();
			r_line.close();
		}
		public void stopPlaying(){
			this.keepGoing = false;
			System.out.println("keepGoing set to "+keepGoing);
		}
	}
	
	
	
	/**
     * public static SetAudioConfiguration(String configurationPath,
     * 								int defaultInternalBufSize, 
     * 								int defaultExternalBufSize,
     * 								String inputMixer,
     * 								String outputMixer)
     * 
     * 
	 * SetAudioConfiguration sets preferences associated with the configuration path node. These preferences are
	 * "internalBufferSize", "externalBufferSize", "inputMixer", and "outputMixer". This static method can be used
	 * to set the above preferences directly, when displaying a dialog is unnecessary.
	 *
	 * @param String configurationPath This is the path node name of the configuration. This can be used to store and retrieve configuration sets. e.g. "/com/myproject/configuration1".
	 * @param int internalBufSize The desired size, in bytes, of Java Sound's circular buffing mechanism.
	 * @param int externalBufSize The desired size, in bytes, of the Java layer buffering mechanism.
	 * @param String inputMixer The name of the mixer that you want to use. This is a name taken from AudioSystem.getMixers().getName().
	 * @param String outputMixer The name of the mixer that you want to use. This is a name taken from AudioSystem.getMixers().getName().
	 * @see         IUAudio.Util, javax.sound.sampled.AudioFormat
	 * 
	 */
	
	
	public static void setAudioConfiguration(String configurationPath, 
			int internalBufSize, 
			int externalBufSize, 
			String inputMixer, 
			String outputMixer){

		prefs= Preferences.systemRoot().node(configurationPath);
		prefs.put("internalBufferSize", internalBufSize+"");
		prefs.put("externalBufferSize", externalBufSize+"");
		prefs.put("inputMixer", inputMixer);
		prefs.put("outputMixer", outputMixer);
	}
	
	
	public static void main(String[] args){
		int internalBufSize = 7056;
		int externalBufSize = 3528;
		
		new AudioConfiguration(new JFrame(),"/com/zeitgeber/test",internalBufSize,externalBufSize);
	}
}
