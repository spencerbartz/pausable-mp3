package com.spencerbartz.mp3;

import javazoom.jl.decoder.JavaLayerException;

class SoundJLayer extends JLayerPlayerPausable.PlaybackListener implements Runnable {
	private String filePath;
	private JLayerPlayerPausable player;
	private Thread playerThread;
	boolean isLooped;

	/**
	 * Constructor
	 * @param filePath
	 */
	public SoundJLayer(String filePath, boolean isLooped) {
		this.filePath = filePath;
		this.isLooped = isLooped;
	}

	/**
	 * play()
	 */
	public void play() {
		if (this.player == null) {
			this.playerInitialize();
		}

		this.playerThread = new Thread(this, "AudioPlayerThread");
		this.playerThread.start();
	}
	
	/**
	 * playerInitialize()
	 */
	private void playerInitialize() {
		try {
			String urlAsString = "file:///" + new java.io.File(".").getCanonicalPath() + "/" + this.filePath;
			this.player = new JLayerPlayerPausable(new java.net.URL(urlAsString), this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * pauseToggle()
	 */
	public void pauseToggle() {
		if (this.player.isPaused == true) {
			this.play();
		} else {
			this.pause();
		}
	}
	
	/**
	 * pause()
	 */
	public void pause() {
		this.player.pause();

		// DEPRECATED this.playerThread.stop();
		//this.playerThread = null;
		
		try {
			if (this.playerThread != null) {
				this.playerThread.interrupt();
				// this.playerThread.join();
				this.playerThread = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * stop()
	 */
	public void stop() {
		this.player.stop();
	}

	// Override functions defined in abstract base class: class JLayerPlayerPausable.PlaybackListener 
	/**
	 * playbackStarted()
	 */
	public void playbackStarted(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
		System.out.println("playbackStarted -- SOURCE: " + playbackEvent.source + " EVENT TYPE NAME: " + playbackEvent.eventType.name + " FRAME INDEX: " + playbackEvent.frameIndex);
	}
	
	/**
	 * playbackStopped
	 */
	public void playbackStopped(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
		System.out.println("playbackStopped -- SOURCE: " + playbackEvent.source + " EVENT TYPE NAME: " + playbackEvent.eventType.name + " FRAME INDEX: " + playbackEvent.frameIndex);
	}
	
	/**
	 * playbackPaused()
	 */
	public void playbackPaused(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
		System.out.println("playbackPaused -- SOURCE: " + playbackEvent.source + " EVENT TYPE NAME: " + playbackEvent.eventType.name + " FRAME INDEX: " + playbackEvent.frameIndex);
	}

	/**
	 * playbackFinished()
	 */
	public void playbackFinished(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
		System.out.println("playbackFinished -- SOURCE: " + playbackEvent.source + " EVENT TYPE NAME: " + playbackEvent.eventType.name + " FRAME INDEX: " + playbackEvent.frameIndex);
		if (this.isLooped) {
			this.player = null;
			this.play();
		}
	}
	
	// Implement function for Runnable interface
	/**
	 * run()
	 */
	public void run() {
		try {
			this.player.resume();
		} catch (JavaLayerException ex) {
			ex.printStackTrace();
		}

	}
}
