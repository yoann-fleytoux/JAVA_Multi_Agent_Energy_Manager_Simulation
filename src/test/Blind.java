package test;

import java.util.ArrayList;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;

public class Blind extends BrightnessAgents {

	float impactInteriorLights[][]= {{ 0.8f,1.f,0.9f,0.7f,0.6f,0.5f,0.4f},{0.7f,1.f,0.9f, 0.8f,0.6f,0.5f,0.4f},{  0.4f,0.8f,0.6f,0.5f,1.f,0.9f,0.6f},{ 0.4f,0.6f,0.6f,0.5f,1.f,0.9f,0.8f}};
	private float apertureLevel;

	private float maxApertureLevel=Settings.basemaxApertureLevel;

	public Blind(int id, AMAS amas,Object[] params) {
		super(id,amas, params);
		this.exteriorLightSensorImpacted = new ArrayList<ExteriorLightSensor>();
	}
	@Override
	protected void onInitialize() {
		this.id = (int) params[0];
	}

	@Override
	protected void onPerceiveDecideAct() {
		
		if(getOutsideLuminosity()==0.f) {
			this.setTo(0.f);
		}else{
			if(getAmas().isThereClass == true) {
			//Perceive
			float impactWanted=0.f;
			//System.out.println("id blind "+this.id);
			for(int i=0;i<this.interiorLightSensorImpacted.size();i++) {
				//System.out.println("        id sensor "+this.interiorLightSensorImpacted.get(i).id);
				impactWanted+=this.interiorLightSensorImpacted.get(i).getBrightnessWanted(getAmas().getFeedbackFactor());
			}
			
			if(impactWanted< 0 && apertureLevel > 0.f) {//too much light and blind open
				if (getMostCriticalNeighbor(true) == this)
					this.adjustBrightness(-Settings.stepBlind);//Act
			}else {
				if(impactWanted > 0 && apertureLevel < this.maxApertureLevel) {//need light and blind not fully open
					if (getMostCriticalNeighbor(true) != this)
						this.adjustBrightness(Settings.stepBlind);//Act
				}
			}
			}else {
				this.consomation=0.f;
			}
			//System.out.println("        has an aperture of "+this.apertureLevel);
		}
	}

	@Override
	protected double computeCriticality() {
		return this.apertureLevel;//the most critic is the one who is the most active
	}

	@Override
	protected void onDraw() {
		LxPlot.getChart("apertureLevel", ChartType.BAR).add(id, apertureLevel);
	}
	
	private float getOutsideLuminosity() {
		return this.exteriorLightSensorImpacted.get(0).getBrightness(this.exteriorLightSensorImpacted.get(0).id,getAmas().getHours());
	}
	
	@Override
	public float getBrightnessImpact(int idSensor) {
		float impactLuminosity = apertureLevel*this.impactInteriorLights[this.id][idSensor]*this.getOutsideLuminosity();
		//System.out.println("impact blind "+this.id+" on sensor "+idSensor+" is "+impactLuminosity);
		//impactLuminosity is between 0 and 12
		return impactLuminosity*4.f;
	}
	
	public void adjustBrightness(float impact) {
		//System.out.println("blind impact "+impact);
		float old=apertureLevel;
		this.apertureLevel+=impact;
		if(this.apertureLevel < 0)
			this.apertureLevel=0.f;
		if(this.apertureLevel > this.maxApertureLevel)
			this.apertureLevel=this.maxApertureLevel;
		this.apertureLevel=(float)Math.round(this.apertureLevel * 1000d) / 1000f;
		this.consomation = Math.abs(old - this.apertureLevel);
		this.consomation =(float)Math.round(this.consomation * 1000d) / 1000f;
		this.consomation=this.consomation*Settings.factorConsommation;
	}
	
	private void setTo(float value) {
		float old=apertureLevel;
		this.apertureLevel=(float)Math.round(value * 1000d) / 1000f;
		this.consomation = Math.abs(old - this.apertureLevel);
		this.consomation =(float)Math.round(this.consomation * 1000d) / 1000f;
		this.consomation=this.consomation*Settings.factorConsommation;
	}
	
	public float getApertureLevel() {
		return this.apertureLevel;
	}
}
