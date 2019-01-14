package test;

public class Settings {
	public static int lightSensorsIn = 7;//HARDCODED do not change alone
	public static int lightSensorsExt = 2;//HARDCODED do not change alone
	//InteriorLightSensor
	public static float baseBrightnessNeeded =100.f;//how much the interior sensors needs without feedback
	//Light
	public static float baseAcceptable_percentage_lower =0.05f; //how much in percentage we allow the system to not match the interior sensors needs 
	public static float baseAcceptable_blind_aperture_during_day_factor= 0.8f;//when the blinds are below this, we dont activate the lights during the day if there s a need for light
	public static float basemaxIntensity = 100.f;//max intensity for lights
	//Blind
	public static float basemaxApertureLevel =1.f;//max aperture for blinds
	public static float factorConsommation =10.f;//how much does it cost in electricity to use blinds
	public static float stepBlind=0.025f;//how much do we move the blind per round
	//ExteriorLightSensor
	public static float[] baseSunCycle = {0,0,0,0,0,0,0,0,1,4,7,10,12,11,10,9,8,7,6,5,3,0,0,0};//Base sunCycle (affect the exterior sensors)

}
