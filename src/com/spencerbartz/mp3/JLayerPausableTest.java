package com.spencerbartz.mp3;
import java.io.*;

public class JLayerPausableTest {
	public static void main(String[] args) {
		SoundJLayer soundToPlay = new SoundJLayer("sound/test.mp3");

		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("About to start playing sound.");
		System.out.println("Press enter to pause...");

		soundToPlay.play();

		while (1 == 1) {
			try {
				consoleReader.readLine();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			System.out.println("toggling");

			soundToPlay.pauseToggle();
		}
	}
}


/*
public class JLayerPausableTest
{
	public static void main(String[] args)
	{
		SoundJLayer soundToPlay = new SoundJLayer("sound/test.mp3");
		
		BufferedReader consoleReader = new BufferedReader
		(
			new InputStreamReader(System.in)
		);

		System.out.println("About to start playing sound.");
		System.out.println("Press enter to pause...");

		soundToPlay.play();

		while(true)
		{
			try
			{
				int code = consoleReader.read();
				//String line = consoleReader.readLine();
				System.out.println("CODE: " + code);
				if(code == 10)
				{
					System.out.println("Toggling pause...");
					soundToPlay.pause();
				}
				else if(code == 113)
				{
					System.out.println("Exiting...");
					System.exit(0);
				}
				else if(code == 114)
				{
					System.out.println("Repeating...");
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
*/
