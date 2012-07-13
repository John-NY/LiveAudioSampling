package com.donovan.audio;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class Ball extends View{
	/* I can reset these private properties from a public function */
	private float x;
	private float y;
	private int r;
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public Ball(Context context, float x, float y, int r){
		super(context);
		mPaint.setColor(0xAA000000);
		this.x = x;
		this.y = y;
		this.r = r;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawCircle(x, y, r, mPaint);
	}


	public void Change(int color)
	{
		mPaint.setColor(color);
		this.postInvalidate(); /* triggers redraw */
	}

	public void SetXPos(int new_x) {
		x = new_x;
		this.postInvalidate(); /* triggers redraw */
	}
	
	public void SetYPos(int new_y) {
		y = new_y;
		this.postInvalidate(); /* triggers redraw */
	}

}

