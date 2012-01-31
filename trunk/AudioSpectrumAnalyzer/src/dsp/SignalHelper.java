package dsp;

import android.util.Log;
import fft.Constants;
import fft.FFTHelper;

public class SignalHelper {
	
	private static int signal[];
	private static int signalWithXAxis[] = {0,0,10,0,20,0,30,0,40,0,50,0,60,0,70,0,80,0,90,5,100,20,110,35,120,100,130,45,140,35,150,15,160,5,170,0,180,0,190,0,200,0,210,0,220,0,230,0,240,0,250,0,260,0,270,0,280,0,290,0,300,0,310,0,320,0,330,0,340,0,350,0};
		
	static public int[] getTestSignal(){
		signal = new int[AudioProcessing.getNumberOfFFTPoints()/2];
		signal[9] = 5;
		signal[10] = 20;
		signal[11] = 35;
		signal[12] = 100;
		signal[13] = 45;
		signal[14] = 35;
		signal[15] = 15;
		signal[16] = 5;
		
		return signal;
	}
	
	private static double mScaleFator = 100;
	
	static public int[] getTestSignalWithSpecificFrequency(double freq){
		signal = new int[AudioProcessing.getNumberOfFFTPoints()/2];
		double pos = freq*((double)AudioProcessing.getNumberOfFFTPoints()/AudioProcessing.getSampleRateInHz());
		int posInt = (int) pos;
		signal[posInt]=100;
		return signal;
	}
	
	static public int[] getTestSignalWithXAxis(){
		return signalWithXAxis; 
	}
	
	static public int[] getDrawableFFTSignal(int[] signal) {
		
		if(signal.length!=(AudioProcessing.getNumberOfFFTPoints()/2)){
			throw new RuntimeException("Number of FFT Points is different!!!");
		}
		
		int drawableFFTSignal[] = new int[2*signal.length];
		
		int i = 0;
		for(int s : signal){
			drawableFFTSignal[2*i] = i;
			drawableFFTSignal[2*i+1] = (int)(mScaleFator*s);
			i++;
		}
		
		return drawableFFTSignal;
	}
	
	static public int[] getDrawableFFTSignal(double[] absSignal) {
		
		if(absSignal.length!=(AudioProcessing.getNumberOfFFTPoints()/2)){
			throw new RuntimeException("Number of FFT Points is different!!!");
		}
		
		int drawableFFTSignal[] = new int[2*absSignal.length];
		
		int i = 0;
		for(double s : absSignal){
			drawableFFTSignal[2*i] = i;
			drawableFFTSignal[2*i+1] = (int)(mScaleFator*s);
			i++;
		}
		
		return drawableFFTSignal;
	}
	
	public static void setScaleFator(double factor){
		mScaleFator = factor;
	}
	
	public static class SignalGenerator {
		
		public static int read(byte[] audioData, int frequency, double samplingRate, boolean twoSinoids, boolean addNoise) {
			
			double T = (1/samplingRate); // Sample time
			int L = (int)samplingRate; // Length of signal - it will always be equal to 1 [s] once L is equal to the sampling frequency.
			short s;
			double temp;
			
			for(int i = 0; i < L; i++){
				double arg = (double)(2*Constants.PI*frequency*((double)i*T));
				
				// first sinoid
				temp = (((double)(32767.0F/2))*Math.sin(arg));
				//second sinoid with 2*frequency
				if(twoSinoids){
					temp = temp + (((double)(32767.0F/2))*Math.sin(2*arg)); 
				}
				// add noise to the signal
				if(addNoise){
					s = (short)(temp + (double)((32767.0F/2)*Math.random()));
				} else {
					s = (short)temp;
				}
				audioData[2*i] = (byte)(s & 0xFF);
				audioData[2*i+1] = (byte)((s >> 8) & 0xFF);
			}
			
			return L;
		}
	}
}