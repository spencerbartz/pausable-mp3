package com.spencerbartz.mp3;

import javazoom.jl.decoder.JavaLayerException;

class SoundJLayer extends JLayerPlayerPausable.PlaybackListener implements Runnable {
	private String filePath;
	private JLayerPlayerPausable player;
	private Thread playerThread;

	/**
	 * Constructor
	 * @param filePath
	 */
	public SoundJLayer(String filePath) {
		this.filePath = filePath;
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
				this.playerThread.join();
				this.playerThread = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	// PlaybackListener members

	public void playbackStarted(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
		System.out.println("playbackStarted");
	}

	public void playbackFinished(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
		System.out.println("playbackEnded");
	}

	// IRunnable members

	public void run() {
		try {
			this.player.resume();
		} catch (JavaLayerException ex) {
			ex.printStackTrace();
		}

	}
}





/*
class SoundJLayer extends JLayerPlayerPausable.PlaybackListener implements Runnable {
	private String filePath;
	private JLayerPlayerPausable player;
	private Thread playerThread;
	private static final int PLAY_FROM_START = 0;
	private static final int PLAY_FROM_LAST_STOP = 1;
	public int startPosition;

	public SoundJLayer(String filePath) {
		this.filePath = filePath;
		this.startPosition = PLAY_FROM_LAST_STOP;
	}

	public SoundJLayer(String filePath, int startPosition) {
		this.filePath = filePath;
		this.startPosition = startPosition;
	}

	public void pause() {
		this.player.pause();
		try {
			if (this.playerThread != null) {
				this.playerThread.join();
			}
			this.startPosition = PLAY_FROM_LAST_STOP;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.playerThread = null;
	}

	public synchronized void pauseToggle() {
		if (this.player.isPaused == true) {
			this.play();
		} else {
			this.pause();
			this.player.isPaused = true;
		}
	}

	public void play() {
		if (this.startPosition == PLAY_FROM_START || this.player == null) {
			this.playerInitialize();
		}
		this.playerThread = new Thread(this, "AudioPlayerThread");
		this.playerThread.start();
	}

	private void playerInitialize() {
		try {
			String urlAsString = "file:///" + new java.io.File(".").getCanonicalPath() + "/" + this.filePath;

			this.player = new JLayerPlayerPausable(new java.net.URL(urlAsString), this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// PlaybackListener members

	public void playbackStarted(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
		System.out.println("playbackStarted()");
	}

	public void playbackFinished(JLayerPlayerPausable.PlaybackEvent playbackEvent) {
		this.startPosition = PLAY_FROM_START;
		System.out.println("playbackEnded()");
		this.play();
	}

	// IRunnable members

	public void run() {
		try {
			this.player.resume();
		} catch (JavaLayerException ex) {
			ex.printStackTrace();
		}
	}
}
*/
