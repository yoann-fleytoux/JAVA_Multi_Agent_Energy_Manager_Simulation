package test;

import java.util.ArrayList;

import fr.irit.smac.amak.Agent;

public class BrightnessAgents extends Agent<AMAS, ClassRoom>{
	
	protected int id;
	
	protected ArrayList<InteriorLightSensor> interiorLightSensorImpacted;
	protected ArrayList<ExteriorLightSensor> exteriorLightSensorImpacted;
	protected float consomation =0.f;
	
	public BrightnessAgents(int id, AMAS amas, Object[] params) {
		super(amas, id, params);
		this.interiorLightSensorImpacted = new ArrayList<InteriorLightSensor>();
	}
	
	public float getBrightnessImpact(int idSensor) {
		return 0;
	}

	public void addInteriorLightSensor(InteriorLightSensor interiorLightSensor) {
		interiorLightSensorImpacted.add(interiorLightSensor);
	}

	public void removeInteriorLightSensor(InteriorLightSensor interiorLightSensor) {
		interiorLightSensorImpacted.remove(interiorLightSensor);
		
	}
	
	public void addExteriorLightSensor(ExteriorLightSensor exteriorLightSensor) {
		exteriorLightSensorImpacted.add(exteriorLightSensor);
	}

	public void removeExteriorLightSensor(ExteriorLightSensor exteriorLightSensor) {
		exteriorLightSensorImpacted.remove(exteriorLightSensor);
		
	}

	public float getImpactWantedWithFactors(float brightnessWanted) {
		//System.out.println("before "+brightnessWanted);
		float return_value=  getAmas().getImpactWantedWithFactors(brightnessWanted);
		//System.out.println("after "+return_value);
		return return_value;
	}
}
