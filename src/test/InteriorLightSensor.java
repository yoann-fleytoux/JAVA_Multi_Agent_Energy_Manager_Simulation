package test;

import java.util.ArrayList;


//TODO gerer besoin avec agenda
public class InteriorLightSensor{

	private ArrayList<BrightnessAgents> impactedBy;
	private float brightnessNeeded = Settings.baseBrightnessNeeded;
	protected int id;
	private float acceptable_percentage_lower =Settings.baseAcceptable_percentage_lower;
	
	public InteriorLightSensor(int idIn) {
		id = idIn;
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

	public float getBrightnessWanted(float feedback_factor){
		float brightnessLevel=0;
		for(int i=0;i<impactedBy.size();i++) {
			brightnessLevel+=impactedBy.get(i).getBrightnessImpact(this.id);
		}
		//System.out.println("        sensor "+this.id+" wants "+(brightnessNeeded-brightnessLevel));
		float return_value= getBrightnessNeeded(feedback_factor)-brightnessLevel;
		if(return_value <= acceptable_percentage_lower*getBrightnessNeeded(feedback_factor) && return_value >= 0)
			return_value=0.f;
		else
			return_value=(float)Math.round(return_value * 1000d) / 1000f;
		return return_value;
	}

	public float getBrightnessNeeded(float feedback_factor) {
		//System.out.println("        sensor "+this.id+" needs "+brightnessNeeded);
		return brightnessNeeded*feedback_factor;
	}

}
