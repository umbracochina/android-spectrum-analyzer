package com.helloandroid.canvastutorial;

import java.util.ArrayList;

import dsp.AudioProcessing;
import dsp.SignalHelper;
import fft.Constants;
import fft.FFTHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
	
	private CanvasDrawing mCanvasDrawing;

	private Context mContext;
	private int mWidth; 
	private int mHeight;
	private int mOrientation;
	private Display mDisplay;

	private int mDrawableSignal[];

	private static final int SHIFT_CONST = 10;

	public Panel(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d("ZZ4FAP: ","Panel created");
		getHolder().addCallback(this);
		mCanvasDrawing = new CanvasDrawing(getHolder(), this);
		setFocusable(true);
		mContext = context;
		mDisplay = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}

	public Panel(Context context) {
		super(context);
		Log.d("ZZ4FAP: ","Panel created");
		getHolder().addCallback(this);
		mCanvasDrawing = new CanvasDrawing(getHolder(), this);
		setFocusable(true);
		mContext = context;
		mDisplay = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}

	@Override
	public void onDraw(Canvas canvas) {
		//drawSpectrum(canvas);
	}
	
	public void drawSpectrum(Canvas canvas, int[] drawableSignal, double samplingRate, int numberOfFFTPoints){
		canvas.drawColor(Color.BLACK);
		drawSpectrumMarks(canvas, samplingRate, numberOfFFTPoints);
		drawFFTSignal(canvas, drawableSignal);
		drawCenterFrequencyMarkAndText(canvas,samplingRate/4);
	}

	void drawSpectrumMarks(Canvas canvas, double samplingRate, int numberOfFFTPoints) {
		int freqStep = 1000;
		Paint p = new Paint();

		for(int freq = 0; freq <= (int)(samplingRate/2); freq = freq + freqStep)
		{
			double point = freq*(((double)numberOfFFTPoints)/(samplingRate));
			int pointInt = (int)point;

			pointInt = pointInt + SHIFT_CONST;//add 10 pixels in order to make room for first freq string to be totally written on the screen.

			double freqDouble = ((double)freq)/1000.0;

			p.setColor(Color.WHITE);
			canvas.drawText(Double.toString(freqDouble),(pointInt-8),(mHeight-1),p);// plot frequencies

			p.setColor(Color.BLUE);
			canvas.drawLine(pointInt,(mHeight-15),pointInt,(mHeight-30), p);// plot markers
		}
	}

	void drawFFTSignal(Canvas canvas, int[] drawableSignal) {
		Paint p = new Paint();
		p.setColor(Color.RED);		
		for(int count=0;count<=(drawableSignal.length-4);count=count+2){
			canvas.drawLine((drawableSignal[count]+SHIFT_CONST), ((mHeight-30)-drawableSignal[count+1]), (drawableSignal[count+2]+SHIFT_CONST), ((mHeight-30)-drawableSignal[count+3]), p);
		}
	}

	void drawPeakFrequencyMarkAndText(Canvas canvas, double peakFreq) {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		canvas.drawText("Peak Freq: "+peakFreq+" Hz",100,(mHeight-150),p);// plot frequencies
	}
	
	void drawCenterFrequencyMarkAndText(Canvas canvas, double centerFreq) {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		canvas.drawText("Center Freq: "+centerFreq+" Hz",(mWidth/2),(mHeight-150),p);
	}

	void getScreenInfo() {
		mWidth = mDisplay.getWidth();
		mHeight = mDisplay.getHeight();
		mOrientation = mDisplay.getOrientation();
		Log.i("ZZ4FAP: ","Width: "+mWidth+" - Height: "+mHeight+" - Orientation: "+mOrientation);
	}

	void getViewInfo() {
		mWidth = getWidth();
		mHeight = getHeight();
		mOrientation = mDisplay.getOrientation();
		Log.i("ZZ4FAP: ","Width: "+getWidth()+" - Height: "+getHeight());
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
		mHeight = height;
		mOrientation = mDisplay.getOrientation();
		Log.i("ZZ4FAP: ","Surface Changed: new width: "+width+" - new height: "+height);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("ZZ4FAP: ","surfaceCreated");
		mCanvasDrawing.setIsSurfaceCreated(true);
		AudioProcessing.registerDrawableFFTSamplesAvailableListener(mCanvasDrawing);
		getViewInfo();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCanvasDrawing.setIsSurfaceCreated(false);
		AudioProcessing.unregisterDrawableFFTSamplesAvailableListener();
	}
}   