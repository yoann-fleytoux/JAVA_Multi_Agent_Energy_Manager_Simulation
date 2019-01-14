package test;

import fr.irit.smac.amak.Environment;


public class ClassRoom extends Environment {
	
	private InteriorLightSensor[] interiorLightSensors;
	private ExteriorLightSensor[] exteriorLightSensors;
	private OccupiedHours occupiedHours;

	@Override
	public void onInitialization() {
		
		occupiedHours = new OccupiedHours();
		
		// Set 7 lights on the table
		interiorLightSensors = new InteriorLightSensor[Settings.lightSensorsIn];
		for (int i = 0; i < interiorLightSensors.length; i++)
			interiorLightSensors[i] = new InteriorLightSensor(i);

		exteriorLightSensors = new ExteriorLightSensor[Settings.lightSensorsExt];
		for (int i = 0; i < exteriorLightSensors.length; i++)
			exteriorLightSensors[i] = new ExteriorLightSensor(i);
	}

	public InteriorLightSensor[] getInteriorLightSensor() {
		return interiorLightSensors;
	}
	
	public ExteriorLightSensor[] getExteriorLightSensor() {
		return exteriorLightSensors;
	}
	
	public InteriorLightSensor getInteriorLightSensor(int idIntSensor) {
		return interiorLightSensors[idIntSensor];
	}
	
	public ExteriorLightSensor getExteriorLightSensor(int idExtSensor) {
		return exteriorLightSensors[idExtSensor];
	}

	public int getInteriorLightSensorLenght() {
		return this.interiorLightSensors.length;
	}

	public OccupiedHours getOccupiedHours() {
		return this.occupiedHours;
	}

	public int getExteriorLightSensorLenght() {
		return this.exteriorLightSensors.length;
	}

}
