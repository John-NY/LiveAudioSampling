package com.donovan.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

/*
 * Thread to manage live recording/playback of voice input from the device's microphone.
 */
public class Audio extends Thread
{ 
    private boolean stopped = false;
    int amplitude = 0;

    /**
     * Give the thread high priority so that it's not canceled unexpectedly, and start it
     */
    Audio()
    { 
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        start();
    }

    @Override
    public void run()
    { 
        Log.i("Audio", "Running Audio Thread");
        AudioRecord recorder = null;
        short[][]   buffers  = new short[256][160];
        int ix = 0;
//        AudioTrack track = null;

        /*
         * Initialize buffer to hold continuously recorded audio data, start recording, and start
         * playback.
         */
        try
        {
            int N = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, 
            		AudioFormat.ENCODING_PCM_16BIT);
            recorder = new AudioRecord(AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, 
            		AudioFormat.ENCODING_PCM_16BIT, N*10);
//            track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
//                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, N*10, 
//                    AudioTrack.MODE_STREAM);
            recorder.startRecording();
//            track.play();
            /*
             * Loops until something outside of this thread stops it.
             * Reads the data from the recorder and writes it to the audio track for playback.
             */
            while(!stopped)
            { 
            	Log.i("Map", "Getting new data");
		        short[] buffer = buffers[ix++ % buffers.length];
		        N = recorder.read(buffer,0,buffer.length);
//                track.write(buffer, 0, buffer.length);
		        
		        short maxval = Short.MIN_VALUE;
		        short minval = Short.MIN_VALUE;
		        for ( int i = 0 ; i < N; i++ )
		        {
		        	maxval = (buffer[i] > maxval) ? (buffer[i]) : (maxval);
		        	minval = (buffer[i] < minval) ? (buffer[i]) : (minval);
		        } 
		        amplitude = (int)maxval - (int)minval - (int)Short.MAX_VALUE;
		        sleep(1);
            }
        }
        catch(Throwable x)
        { 
            Log.w("Audio", "Error reading voice audio", x);
        }
        /*
         * Frees the thread's resources after the loop completes so that it can be run again
         */
        finally
        { 
            recorder.stop();
            recorder.release();
            recorder = null;
//            track.stop();
//            track.release();
//            track = null;
        }
    }

    public int getBufferVolume()
    {
    	return amplitude;
    }
    
    /**
     * Called from outside of the thread in order to stop the recording/playback loop
     */
    public void close()
    { 
         stopped = true;
    }

} /* end class Audio */
