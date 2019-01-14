package test;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;

public class Light extends BrightnessAgents {
	
	float lightIntensity = 0.f;
	float maxIntensity = Settings.basemaxIntensity;
	float acceptable_blind_aperture_during_day_factor=Settings.baseAcceptable_blind_aperture_during_day_factor;
	
	private int id;

	public Light(int id, AMAS amas, Object[] params) {
		super(id,amas, params);
	}
	@Override
	protected void onInitialize() {
		this.id = (int) params[0];
	}

	@Override
	protected void onPerceiveDecideAct() {
		//Perceive
		if(getAmas().isThereClass != true){
			this.setTo(0.f);
		}else {

			float impactWanted=0.f;
			//System.out.println("id light "+this.id);
			for(int i=0;i<this.interiorLightSensorImpacted.size();i++) {
				//System.out.println("        id sensor "+this.interiorLightSensorImpacted.get(i).id);
				impactWanted+=this.interiorLightSensorImpacted.get(i).getBrightnessWanted(getAmas().getFeedbackFactor());
			}

			//Decide
			if(getAmas().getHours()> 7 && getAmas().getHours() < 21) {//if it s the day
				int nb_of_blinds_neighbor=0;
				float sum_of_aperture_blinds_neighbor=0.f;
				for(int i=0;i<this.interiorLightSensorImpacted.size();i++) {
					//System.out.println(getAmas().blinds.length);
					for(int j=0;j<getAmas().blinds.length;j++) {
						if(this.interiorLightSensorImpacted.get(i).owned(getAmas().blinds[j])) {
							nb_of_blinds_neighbor++;
							sum_of_aperture_blinds_neighbor+=getAmas().blinds[j].getApertureLevel();
						}
					}
				}	
				
				//System.out.println("        check sum= "+result);
				if(sum_of_aperture_blinds_neighbor< acceptable_blind_aperture_during_day_factor*nb_of_blinds_neighbor && this.lightIntensity > 0.f){//if blinds not totally open and this light is lit
					//System.out.println("lol");
					this.setTo(0.f);//switch off this light
				}else{
					if(impactWanted > 0) {//need more light
						if (getMostCriticalNeighbor(true) != this)//if not the most lighten
							this.adjustBrightness(impactWanted/10.f);//Act increase brightness
					}else {//need less light
						if (getMostCriticalNeighbor(true) == this)//if the most lighten
							this.adjustBrightness(impactWanted/10.f);//Act decrease brightness
					}
				}
			}else {//only pass here if class not during the day
				if(impactWanted > 0) {//need more light
					if (getMostCriticalNeighbor(true) != this)
						this.adjustBrightness(impactWanted/10);//Act
				}else {
					if (getMostCriticalNeighbor(true) == this)
						this.adjustBrightness(impactWanted/10.f);//Act	
				}
			}
		}
		//System.out.println("        provides "+this.lightIntensity);
	}

	private void setTo(float value) {
		this.lightIntensity=value;
		this.lightIntensity=(float)Math.round(this.lightIntensity * 1000d) / 1000f;
		this.consomation=this.lightIntensity;
		
	}
	@Override
	protected double computeCriticality() {
		return  this.lightIntensity ;//the most critic is the one who is the most active
	}

	@Override
	protected void onDraw() {
		LxPlot.getChart("lightIntensity", ChartType.BAR).add(id, lightIntensity);
	}

	public void adjustBrightness(float impact) {
		this.lightIntensity+=impact;
		if(this.lightIntensity < 0)
			this.lightIntensity=0.f;
		if(this.lightIntensity > this.maxIntensity)
			this.lightIntensity=this.maxIntensity;
		this.lightIntensity=(float)Math.round(this.lightIntensity * 1000d) / 1000f;
		this.consomation=this.lightIntensity;
	}
	
	@Override
	public float getBrightnessImpact(int idSensor) {
		return lightIntensity;
	}
}
