/*
 * 
 * IBMAudio.java
 * sample code for IBM Developerworks Article
 * Author: W. Frank Ableson
 * fableson@msiservices.com
 * 
 */
package com.donovan.audio;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class LiveAudioSampling extends Activity {

	private Ball ball; /* the ball we're going to draw */
	private Button startRecording = null;
	private Button stopRecording = null;
	public Audio myAudio;
	public int ballColor = 0; /* ball color saved and writeable */
	private Timer myTimer; /* timer to grab audio */
	private EditText myLabel; /* label to print volume */
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* Let's use the frame to draw the button since we can put anything anywhere */
    	FrameLayout main = (FrameLayout) findViewById(R.id.frame_view);
        	main.addView(ball = new Ball(this, 50, 50, 25));
        	ball.Change(ColorMap(ballColor));
        	
        startRecording = (Button)findViewById(R.id.startrecording);
        stopRecording = (Button)findViewById(R.id.stoprecording);
        myLabel = (EditText)findViewById(R.id.editText1);
        
  		startRecording.setEnabled(false);
        myAudio = new Audio(); /* start the thread */
        
        startRecording.setOnClickListener(new View.OnClickListener(){
          public void onClick(View v) {
        	  startRecording.setEnabled(false);
              myAudio = new Audio(); /* start new thread */
          }
        });
    	
        stopRecording.setOnClickListener(new View.OnClickListener(){
          public void onClick(View v) {
      		startRecording.setEnabled(true);
      		myAudio.close();
          }
        });
        

    	/* Set up the timer to run every 20ms, to change the ball color */
    	myTimer = new Timer();
		myTimer.schedule(new TimerTask() {			
			@Override
			public void run() {
				TimerMethod();
			}
			
		}, 0, 20); /* Timer.schedule(function, delay, repeat) */
		/* end timer stuff */
		
    } /* end function onCreate() */

    /* timer stuff */
    private void TimerMethod() {		
    	//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.
		this.runOnUiThread(Timer_Tick);
    } /* end function TimerMethod */
	
    private Runnable Timer_Tick = new Runnable() {
		public void run() {
			int irange = myAudio.getBufferVolume();	
			ballColor = (int) ( ( (float)irange ) * 256 / 1024 );
			ballColor = ballColor > 255 ? (int)255 : ballColor;
			//This method runs in the same thread as the UI.
			//Do something to the UI thread here
			ball.Change(ColorMap(ballColor));
			ball.SetXPos(ballColor+25); /* also move the ball */
			myLabel.setText(String.format("%d", irange));
		}
	}; /* end Runnable object Timer_Tick */
	
	/* Java doesn't use % for modulus operator, which was annoying */
	/* "mod" is defined here to be a modulus operator */
	private int mod(int x, int y)
	{
	    int result = x % y;
	    return result < 0? result + y : result;
	} /* end function mod (modulus) */

	/* I wanted a pretty colormap, so I used the "hue" to effect this. */
	/* I'm pretty proud of this solution, as it is better than a look-up table. */
    private int ColorMap(int iInput)
    {
    	float[] hsv = new float[3]; /* hue, saturation, value */
    	hsv[0] = (float)(255-iInput); /* hue = [blue -> green -> red] */
    	hsv[1] = (float)(255); /* saturation = [intensity to color] */
    	hsv[2] = (float)(255); /* value = [dark to bright] */
    	int iColor = Color.HSVToColor(hsv);     	
    	return iColor;
    } /* end function ColorMap */

}