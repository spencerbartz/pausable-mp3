package com.spencerbartz.mp3;
// import java.io.*;

public class JLayerPausableTest {
	public static void main(String[] args) {
		SoundJLayer longSound = new SoundJLayer("sound/test.mp3", true);
		SoundJLayer shortSound = new SoundJLayer("sound/test2.mp3", true);

		longSound.play();

		try {
			Thread.sleep(2000);
			
			for (int i = 0; i < 5; i++) {
				// OFF
				longSound.pauseToggle();
				Thread.sleep(1000);
				
				// ON
				longSound.pauseToggle();
				Thread.sleep(2000);
			}
			
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		
		longSound.stop();
		
		shortSound.play();
		try {
			Thread.sleep(1000);
			
			// OFF
			shortSound.pauseToggle();
			Thread.sleep(500);
			
			// ON
			shortSound.pauseToggle();
			Thread.sleep(500);
			
			// OFF
			shortSound.pauseToggle();
			Thread.sleep(500);
			
			// ON
			shortSound.pauseToggle();
			Thread.sleep(500);
			
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
