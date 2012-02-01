package com.spectrumanalyzer.ui;

import com.spectrumanalyzer.ui.R;

import dsp.AudioProcessing;
import dsp.AudioProcessingListener;
import fft.Constants;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SpectrumAnalyzer extends Activity implements Button.OnClickListener, AudioProcessingListener {
	
	private static final String TAG = SpectrumAnalyzer.class.getSimpleName();
	
	private Spinner fs_spinner;
	private Spinner fft_spinner;
	private Button move_center_freq_to_left_button;
	private Button move_center_freq_to_right_button;
	private TextView peak_freq_text_view;
	private Panel spectrum_display;
	
	private SurfaceHolder surface_holder;
	
	private AudioProcessing mAudioCapture;
	
	private double mSampleRateInHz = Constants.SAMPLING_FREQUENCY;
	private int mNumberOfFFTPoints = Constants.NUMBER_OF_FFT_POINTS;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setSpectrumAnalyzer();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mAudioCapture = new AudioProcessing(mSampleRateInHz,mNumberOfFFTPoints);
		AudioProcessing.registerDrawableFFTSamplesAvailableListener(this);
	}
	
	private void setSpectrumAnalyzer(){
		// Spinner code with the Available Sampling Frequencies. 
		fs_spinner = (Spinner) findViewById(R.id.sampling_rate_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.frequencies_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fs_spinner.setAdapter(adapter);
		fs_spinner.setOnItemSelectedListener(new OnSamplingRateItemSelectedListener());
		
		// Spinner code with the Available Number of FFt Points. 
		fft_spinner = (Spinner) findViewById(R.id.number_of_fft_points_spinner);
		adapter = ArrayAdapter.createFromResource(this, R.array.fft_points_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fft_spinner.setAdapter(adapter);
		fft_spinner.setOnItemSelectedListener(new OnNumberOfFFTPointsItemSelectedListener());
		
		//center frequency buttons
		move_center_freq_to_left_button = (Button) findViewById(R.id.btn_shift_center_freq_to_left);
		move_center_freq_to_left_button.setOnClickListener(this);
		move_center_freq_to_right_button = (Button) findViewById(R.id.btn_shift_center_freq_to_right);
		move_center_freq_to_right_button.setOnClickListener(this);
		
		// get text view which is used to display the current peak frequency.
		peak_freq_text_view = (TextView) findViewById(R.id.txt_peak_freq);
		
		// get the Surface view which is used to draw the spectrum.
		spectrum_display = (Panel) findViewById(R.id.SurfaceView01);
		surface_holder = spectrum_display.getHolder();
	}

	public class OnSamplingRateItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			
			double samplingRate;
			
			switch(pos){
			case 0:
				samplingRate = 8000.0;
				break;
			case 1:
				samplingRate = 16000.0;
				break;
			case 2:
				samplingRate = 22050.0;
				break;
			case 3:
				samplingRate = 44100.0;
				break;
			case 4:
				samplingRate = 48000.0;
				break;
			case 5:
				samplingRate = 96000.0;
				break;
			default:
				samplingRate = 8000.0;
				break;
			}
			onSamplingRateChanged(samplingRate);
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}
	
	public class OnNumberOfFFTPointsItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			
			int numberOfFFTPoints;
			
			switch(pos){
			case 0:
				Log.i("ZZ4FAP: ","Automatic");
				numberOfFFTPoints = 512;
				break;
			case 1:
				numberOfFFTPoints = 64;
				break;
			case 2:
				numberOfFFTPoints = 128;
				break;
			case 3:
				numberOfFFTPoints = 256;
				break;
			case 4:
				numberOfFFTPoints = 512;
				break;
			case 5:
				numberOfFFTPoints = 1024;
				break;
			case 6:
				numberOfFFTPoints = 2048;
				break;
			default:
				numberOfFFTPoints = 512;
				break;
			}
			onNumberOfFFTPointsChanged(numberOfFFTPoints);
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	@Override
	public void onClick(View v) {
    	int buttonID;
    	buttonID = v.getId();
    	switch(buttonID){
    	case R.id.btn_shift_center_freq_to_left:
    		Log.i("ZZ4FAP: ","Shift center freq to left");
    		break;
    		
    	case R.id.btn_shift_center_freq_to_right:
    		Log.i("ZZ4FAP: ","Shift center freq to right");
    		break;
    		
    	default:
			Log.e(TAG,"Invalid Option!!!");
			break;
    	}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		mAudioCapture.close();
		AudioProcessing.unregisterDrawableFFTSamplesAvailableListener();
	}
	
	private void onNumberOfFFTPointsChanged(int numberOfFFTPoints){
		if(numberOfFFTPoints!=mNumberOfFFTPoints){
			mNumberOfFFTPoints = numberOfFFTPoints;
			mAudioCapture.close();
			mAudioCapture = new AudioProcessing(mSampleRateInHz,mNumberOfFFTPoints);
		}
	}
	
	private void onSamplingRateChanged(double samplingRate){
		if(samplingRate!=mSampleRateInHz){
			mSampleRateInHz = samplingRate;
			mAudioCapture.close();
			mAudioCapture = new AudioProcessing(mSampleRateInHz,mNumberOfFFTPoints);
		}
	}

	@Override
	public void onDrawableFFTSignalAvailable(final double[] absSignal) {
		SpectrumAnalyzer.this.runOnUiThread(new Runnable() {
            public void run() {
        		spectrum_display.drawSpectrum(absSignal, mSampleRateInHz, mNumberOfFFTPoints, mAudioCapture.getMaxFFTSample());
        		peak_freq_text_view.setText(Double.toString(mAudioCapture.getPeakFrequency()));
            }
        });
	}
}
