package com.spencerbartz.mp3;

import java.net.*;
import javazoom.jl.decoder.*;
import javazoom.jl.player.*;
import java.io.IOException;

public class JLayerPlayerPausable {

	private URL urlToStreamFrom;
	private Bitstream bitstream;
	private Decoder decoder;
	private AudioDevice audioDevice;
	private PlaybackListener listener;
	private int frameIndexCurrent;
	private static int AUDIO_FRAMES_TO_REWIND = 5;

	public boolean isPaused;

	/**
	 * Constructor
	 * @param urlToStreamFrom
	 * @param listener
	 * @throws JavaLayerException
	 */
	public JLayerPlayerPausable(URL urlToStreamFrom, PlaybackListener listener) throws JavaLayerException {
		this.urlToStreamFrom = urlToStreamFrom;
		this.listener        = listener;
	}

	/**
	 * pause()
	 */
	public void pause() {
		this.isPaused = true;
		this.close();
		this.listener.playbackPaused(new PlaybackEvent(this, PlaybackEvent.EventType.Instances.Paused, this.frameIndexCurrent));
	}

	/**
	 * play()
	 * @return boolean
	 * @throws JavaLayerException
	 */
	public boolean play() throws JavaLayerException {
		return this.play(0);
	}

	/**
	 * play()
	 * @param frameIndexStart
	 * @return boolean
	 * @throws JavaLayerException
	 */
	public boolean play(int frameIndexStart) throws JavaLayerException {
		return this.play(frameIndexStart, -1, AUDIO_FRAMES_TO_REWIND);
	}

	/**
	 * play()
	 * @param frameIndexStart
	 * @param frameIndexFinal
	 * @param correctionFactorInFrames
	 * @return boolean
	 * @throws JavaLayerException
	 */
	public boolean play(int frameIndexStart, int frameIndexFinal, int correctionFactorInFrames) throws JavaLayerException {
		
		try {
			this.bitstream = new Bitstream(this.urlToStreamFrom.openStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		this.audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
		this.decoder = new Decoder();
		this.audioDevice.open(decoder);

		boolean shouldContinueReadingFrames = true;

		this.isPaused = false;
		this.frameIndexCurrent = 0;

		// If we're resuming play, adjust the current frame index (i.e. the starting position)
		while (shouldContinueReadingFrames == true && this.frameIndexCurrent < frameIndexStart - correctionFactorInFrames) {
			shouldContinueReadingFrames = this.skipFrame();
			this.frameIndexCurrent++;
		}

		if (this.listener != null) {
			this.listener.playbackStarted(new PlaybackEvent(this, PlaybackEvent.EventType.Instances.Started, this.frameIndexCurrent));
		}

		if (frameIndexFinal < 0) {
			frameIndexFinal = Integer.MAX_VALUE;
		}
		
		while (shouldContinueReadingFrames == true && this.frameIndexCurrent < frameIndexFinal) {
			// TODO Find a way to test if this can get stuck. Might need to make it synchronized if the value of isPaused can change
			if (this.isPaused == true) {
				shouldContinueReadingFrames = false;
				try {
					Thread.sleep(1);
				} catch (InterruptedException iex) {
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				shouldContinueReadingFrames = this.decodeFrame();
				this.frameIndexCurrent++;
			}
		}
		
		// last frame, ensure all data flushed to the audio device.
		if (this.audioDevice != null) {
			this.audioDevice.flush();

			synchronized (this) {
				this.close();
			}
			
			// report to listener if we've reached the end of the track (not just pausing)
			if (this.listener != null && this.isPaused == false) {
				this.listener.playbackFinished(new PlaybackEvent(this, PlaybackEvent.EventType.Instances.Finished, this.frameIndexCurrent));
			}
		}

		return shouldContinueReadingFrames;
	}

	/**
	 * resume()
	 * @return boolean
	 * @throws JavaLayerException
	 */
	public boolean resume() throws JavaLayerException {
		return this.play(this.frameIndexCurrent);
	}

	/**
	 * close()
	 */
	public synchronized void close() {
		if (this.audioDevice != null) {
			this.audioDevice.close();
			this.audioDevice = null;

			try {
				this.bitstream.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * decodeFrame()
	 * @return boolean
	 * @throws JavaLayerException
	 */
	protected boolean decodeFrame() throws JavaLayerException {
		try {
			if (this.audioDevice != null) {
				Header header = this.bitstream.readFrame();
				if (header != null) {
					// sample buffer set when decoder constructed
					SampleBuffer output = (SampleBuffer) decoder.decodeFrame(header, bitstream);

					synchronized (this) {
						if (this.audioDevice != null) {
							this.audioDevice.write(output.getBuffer(), 0, output.getBufferLength());
						}
					}

					this.bitstream.closeFrame();
				} else {
					return false;
				}
			}
		} catch (RuntimeException ex) {
			throw new JavaLayerException("Exception decoding audio frame", ex);
		} 
		
		return true;
	}

	/**
	 * skipFrame()
	 * @return boolean
	 * @throws JavaLayerException
	 * Eats up 1 mp3 file audio frame from a bitstream, effectively skipping it
	 */
	protected boolean skipFrame() throws JavaLayerException {
		Header header = bitstream.readFrame();

		if (header != null) {
			bitstream.closeFrame();
			return true;
		}

		return false;
	}

	/**
	 * stop()
	 */
	public void stop() {
		this.close();
		this.listener.playbackStopped(new PlaybackEvent(this, PlaybackEvent.EventType.Instances.Stopped, this.frameIndexCurrent));
	}

	// inner classes

	/**
	 * PlaybackEvent
	 */
	public static class PlaybackEvent {
		public JLayerPlayerPausable source;
		public EventType eventType;
		public int frameIndex;

		public PlaybackEvent(JLayerPlayerPausable source, EventType eventType, int frameIndex) {
			this.source = source;
			this.eventType = eventType;
			this.frameIndex = frameIndex;
		}

		/**
		 * EventType
		 */
		public static class EventType {
			public String name;

			public EventType(String name) {
				this.name = name;
			}
			
			/**
			 * Instances
			 */
			public static class Instances {
				public static EventType Started = new EventType("Started");
				public static EventType Stopped = new EventType("Stopped");
				public static EventType Paused  = new EventType("Paused");
				public static EventType Finished = new EventType("Finished");
			}
		}
	}

	/**
	 * PlaybackListener
	 */
	public static abstract class PlaybackListener {
		public void playbackStarted(PlaybackEvent event) {}
		public void playbackStopped(PlaybackEvent event) {}
		public void playbackPaused(PlaybackEvent event) {}
		public void playbackFinished(PlaybackEvent event) {}
	}
}
