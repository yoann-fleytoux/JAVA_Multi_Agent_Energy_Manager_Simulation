package test;

import java.util.ArrayList;
import java.util.Random;

public class ExteriorLightSensor{

	private ArrayList<BrightnessAgents> impactedBy;
	
	public float[] sunCycle = new float[24];

	public float[] factor_sun = {0.8f,1.1f};
	protected int id;

	public ExteriorLightSensor(int idIn){
		id=idIn;
		sunCycle = Settings.baseSunCycle.clone();
		impactedBy = new ArrayList<BrightnessAgents>();
	}

	public synchronized boolean tryTake(BrightnessAgents asker) {
		impactedBy.add(asker);
		return true;
	}

	public synchronized void release(BrightnessAgents asker) {
		impactedBy.remove(asker);
	}

	public synchronized boolean owned(BrightnessAgents asker) {
		return impactedBy.contains(asker);
	}

	public float getBrightness(int id, int hour){
		return factor_sun[id]*sunCycle[hour];
	}
	
	public void randomizeSun() {
		Random rg = new Random();
		sunCycle = Settings.baseSunCycle.clone();
		for (int i=0; i<sunCycle.length; i++) {
			float valToAdd = rg.nextFloat();
			valToAdd = (valToAdd*4)-2;
			sunCycle[i] = sunCycle[i]+valToAdd;
			if (sunCycle[i] < 0) {
				sunCycle[i] = 0.0f;
			}
		}
	}

}
