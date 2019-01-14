package test;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.xml.ws.WebServiceException;

import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.amak.ui.MainWindow;

/*class 108 U3 simulation (see pictures)
 * 10 lights (2 front_tableau, 4 in a row front, 4 in a row back)
 * light 1 -> interiorSensor1 ; Neighbors-> 2
 * light 2 -> interiorSensor1 ; Neighbors-> 1
 * light 3 -> interiorSensor2 ; Neighbors-> 4
 * light 4 -> interiorSensor2, interiorSensor3 ; Neighbors-> 3, 5
 * light 5 -> interiorSensor3, interiorSensor4 ; Neighbors-> 4, 6
 * light 6 -> interiorSensor4 ; Neighbors-> 5
 * light 7 -> interiorSensor5 ; Neighbors-> 8
 * light 8 -> interiorSensor5, interiorSensor6 ; Neighbors-> 7, 9
 * light 9 -> interiorSensor6, interiorSensor7 ; Neighbors-> 8,10 
 * light 10 -> interiorSensor7 ; Neighbors-> 9
 * 4 blinds (blinds impact all the sensors but at different rates (descending order bellow)), every blind has every other agents as a neighbor
 * blind 1 -> ExteriorLightSensor1, (2,3,1,4,5,6,7) (0.8f,1.f,0.9f,0.7f,0.6f,0.5f,0.4f)
 * blind 2 -> ExteriorLightSensor1, (2,3,4,1,5,6,7) (0.7f,1.f,0.9f,0.8f,0.6f,0.5f,0.4f)
 * blind 3 -> ExteriorLightSensor2, (5,6,2,7,3,4,1) (0.4f,0.8f,0.6f,0.5f,1.f,0.9f,0.6f)
 * blind 4 -> ExteriorLightSensor2, (5,6,7,2,3,4,1) (0.4f,0.6f,0.6f,0.5f,1.f,0.9f,0.8f)
*/


public class AMAS extends Amas<ClassRoom> {
	private JLabel feedback;
	private JLabel title;
	private JLabel separationStatus;
	private JLabel weeklyConsumLabel;
	private JLabel hourlyConsumLabel;
	private JLabel disclaimerConsumption;
	
	private Light[] lights;
	Blind[] blinds;
	private float feedbackFactor= 1.f;//TODO check how I coded that
	private static ArrayList<JLabel> labelsSensors = new ArrayList<JLabel>();
	private static ArrayList<JLabel> labelsAgenda = new ArrayList<JLabel>();
	private static ArrayList<JLabel> labelsMeteo = new ArrayList<JLabel>();
	private boolean isSeparated = false; //False = 1 room, True = 2 rooms
	public float weeklyConsumption = 0.0f;
	public float hourlyConsumption = 0.0f;
	
	private String[] dayOfWeek = {"Lundi", "Mardi", "Mercredi","Jeudi","Vendredi","Samedi","Dimanche"};
	private int currentDayOfWeek = 0;
	private int hourOfTheDay = 0;
	private int minutes = 0;
	public boolean isThereClass;
	
	public int getDay() {
		return currentDayOfWeek;
	}
	
	public int getHours() {
		return hourOfTheDay;
	}
	
	public int getMinutes() {
		return minutes;
	}
	public AMAS(ClassRoom env) {
		super(env, Scheduling.UI);
	}

	@Override
	protected void onInitialConfiguration() {
		JPanel sensorNeeds = manageTabSensorNeeds();
		JPanel agendaMeteo = manageTabAgendaMeteo();
		JToolBar toolbar = manageToolBar();
		MainWindow.addTabbedPanel("Light needs",sensorNeeds);
		MainWindow.addTabbedPanel("Agenda & Meteo", agendaMeteo);
		MainWindow.addToolbar(toolbar);
	}

	public JToolBar manageToolBar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
		feedback = new JLabel("Feedback");
		feedback.setPreferredSize(new Dimension(200, 100));
		
		title = new JLabel(" ");
		separationStatus = new JLabel(" ");
		disclaimerConsumption = new JLabel("Consumption is in arbitrary units");
		hourlyConsumLabel = new JLabel("Hourly consumption : ");
		weeklyConsumLabel = new JLabel("Weekly consumption : ");
		
		
		JButton separate = new JButton("Separate classrooms");
		JButton feedbackMinus1 = new JButton("Too much light"); //These values are simulations of the means of multiple feedbacks
		JButton feedbackMinus05 = new JButton("A bit less light"); //We imagine that in real situation choices would be -1,0,1
		JButton feedbackPlus05 = new JButton("A bit more light");
		JButton feedbackPlus1 = new JButton("Not enough light");

		separate.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
			  isSeparated = !isSeparated;
			  if (isSeparated) {
				  separate.setText("Merge classrooms");
				  separationStatus.setText("You now have 2 classrooms !");
				  setForSeparatedClassroom();
				  
			  } else {
				  separate.setText("Separate classrooms");
				  separationStatus.setText("You now have 1 classroom !");
				  unSetForSeparatedClassroom();
			  }
		   
		  }});
		feedbackMinus1.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
		   if (getFeedbackFactor() >= 0.2f) {
			   setFeedbackFactor(getFeedbackFactor() - 0.2f);
			 }
		  }});
		feedbackMinus05.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			 if (getFeedbackFactor() >= 0.1f) {
				 setFeedbackFactor(getFeedbackFactor() - 0.1f);
			 }
			}});
		feedbackPlus05.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
		   if (getFeedbackFactor() <= 0.9f) {
			   setFeedbackFactor(getFeedbackFactor() + 0.1f);
			 }
		  }});
		feedbackPlus1.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
		   if (getFeedbackFactor() <= 0.8f) {
			   setFeedbackFactor(getFeedbackFactor() + 0.2f);
			 }
		  }});

		
		toolbar.add(title);
		toolbar.addSeparator();
		toolbar.add(separate);
		toolbar.add(separationStatus);
		toolbar.addSeparator();
		toolbar.add(feedback);
		toolbar.add(feedbackMinus1);
		toolbar.add(feedbackMinus05);
		toolbar.add(feedbackPlus05);
		toolbar.add(feedbackPlus1);
		toolbar.addSeparator();
		toolbar.add(disclaimerConsumption);
		toolbar.add(hourlyConsumLabel);
		toolbar.add(weeklyConsumLabel);
		
		return toolbar;
		
	}
	public JPanel manageTabSensorNeeds() {
		JPanel pan1 = new JPanel();
		pan1.setLayout(new BoxLayout(pan1, BoxLayout.Y_AXIS));
		for(int i=0;i< Settings.lightSensorsIn;i++) {
			labelsSensors.add(new JLabel("Sensor "+i+" needs will appear here when simulation is launched."));
			
			pan1.add(labelsSensors.get(i));
		}
		return pan1;
	}
	
	private JPanel manageTabAgendaMeteo() {
		JPanel pan2 = new JPanel();
		pan2.setLayout(new GridLayout(0, 2));
		for (int i = 0; i<13; i++) {
			labelsAgenda.add(new JLabel("Agenda info for " + (i+8) + ":00 will appear here."));
			pan2.add(labelsAgenda.get(i));
			labelsMeteo.add(new JLabel("Meteo info for " + (i+8)+ ":00 will appear here."));
			pan2.add(labelsMeteo.get(i));
		}
		return pan2;
	}

	
	@Override
	protected void onInitialAgentsCreation() {
		
		lights = new Light[10];
		for(int i=0;i< lights.length ;i++) {
			//create lights
			lights[i] = new Light(i,this, params);
			//set lights and sensors
			setSensorsLights(i);
			
		}
		
		blinds = new Blind[4];
		for(int i=0;i<blinds.length;i++) {
			//create lights
			blinds[i] = new Blind(i,this, params);
			//set lights and sensors
			setSensorsBlinds(i);
			
		}
		for(int i=0;i< lights.length ;i++)
			setNeighborLights(i);
		
		//set neighbor agents
		for(int i=0;i<blinds.length;i++)
			setNeighborBlinds(i);
	}
	

	@Override
	protected void onSystemCycleBegin() {
		
		//We will do one step per minute, so every hour is 60 steps
		minutes = getCycle()%60;
		
		if (minutes == 0) {
			hourOfTheDay = (hourOfTheDay+1)%24;
			isThereClass = isThereClass();
			hourlyConsumLabel.setText("Hourly consumption : " + hourlyConsumption);
			hourlyConsumption = 0.0f;
			if (hourOfTheDay == 0) {
				for(int i=0;i<getEnvironment().getExteriorLightSensorLenght();i++) {
					getEnvironment().getExteriorLightSensor(i).randomizeSun();
				}
				currentDayOfWeek = (currentDayOfWeek+1)%7;
				if (currentDayOfWeek == 0) {
					weeklyConsumLabel.setText("Weekly consumption : " + weeklyConsumption);
					weeklyConsumption = 0.0f;
					getEnvironment().getOccupiedHours().randomizeOccupation();
				}
			}
		}
		title.setText(dayOfWeek[currentDayOfWeek] + " " + hourOfTheDay + ":" + minutes);
		//System.out.println(dayOfWeek[currentDayOfWeek] + " " + hourOfTheDay + ":" + minutes);
		
		
		for(int i=0;i< getEnvironment().getInteriorLightSensorLenght();i++) {
			if(isThereClass)
				labelsSensors.get(i).setText("Sensor "+i+" wants "+getEnvironment().getInteriorLightSensor(i).getBrightnessWanted(feedbackFactor)+" at the end of this cycle");
			else
				labelsSensors.get(i).setText("Sensor "+i+" wants "+0.f+" at the end of this cycle");
			//System.out.println("Light "+i+" wants "+getImpactWantedWithFactors(getEnvironment().getInteriorLightSensor(i).getBrightnessWanted())+" at the end of this cycle");
		}
		for (int i=0; i < labelsAgenda.size(); i++) {
			float meteo1[] = getEnvironment().getExteriorLightSensor(0).sunCycle; //On affiche la météo que du 1er capteur pour exemple
			
			String hourStr = (new Integer(i+8).toString());
			if (getEnvironment().getOccupiedHours().classroomUsed(i+8, currentDayOfWeek)) {
				labelsAgenda.get(i).setText(hourStr + ":00 CLASS");
			} else {
				labelsAgenda.get(i).setText(hourStr + ":00 FREE");
			}
			
			labelsMeteo.get(i).setText("Brightness of the sun : " + meteo1[i+8]);

		}
		//TODO dans des nouveaux tabs
		//TODO afficher l'agenda (getEnvironment().getOccupiedHours().occupied du jour
		//TODO afficher la météo du jour par sensor (for(int i=0;i< getEnvironment().getExteriorLightSensorLenght();i++) {getExteriorLightSensor(i).sunCycle
		//TODO afficher le coef de feed back qui est dans AMAS (0 <= feedbackFactor <= 1.f) 
		//TODO afficher la conso qui est updater à chaque cycle pour chaque agents (10 lights 4 blinds)
		float conso_last_cycle=0.f;
		for(int i=0;i< lights.length;i++) {
			conso_last_cycle+=lights[i].consomation;
		}
		for(int i=0;i< blinds.length;i++) {
			conso_last_cycle+=blinds[i].consomation;
		}
		hourlyConsumption+=conso_last_cycle;
		weeklyConsumption+=conso_last_cycle;
		System.out.println("Conso= "+conso_last_cycle);
	}
	
	
	
	//everyBlind has everyone as neighbor
	private void setNeighborBlinds(int idBlind) {
		/*
		for(int i=0;i< lights.length ;i++) {
			blinds[idBlind].addNeighbor(lights[i]);
		}
		*/
		for(int i=0;i<blinds.length;i++) {
			if(i!=idBlind) {
				blinds[idBlind].addNeighbor(blinds[i]);
			}
		}
	}

	private void setNeighborLights(int idLight) {
		/*
		for(int i=0;i<blinds.length;i++)
			lights[idLight].addNeighbor(blinds[i]);
		*/
		switch (idLight) {
		case 0:  
			lights[idLight].addNeighbor(lights[1]);
        	break;
        case 1:  
        	lights[idLight].addNeighbor(lights[0]);
        	break;
        case 2:  
        	lights[idLight].addNeighbor(lights[3]);
        	break;
        case 3:  
        	lights[idLight].addNeighbor(lights[2]);
        	lights[idLight].addNeighbor(lights[4]);
        	break;
        case 4: 
        	lights[idLight].addNeighbor(lights[3]);
        	lights[idLight].addNeighbor(lights[5]);
        	break;
        case 5: 
        	lights[idLight].addNeighbor(lights[4]);
        	break;
        case 6: 
        	lights[idLight].addNeighbor(lights[7]);
        	break;
        case 7:  
        	lights[idLight].addNeighbor(lights[6]);
        	lights[idLight].addNeighbor(lights[8]);
        	break;
        case 8:  
        	lights[idLight].addNeighbor(lights[7]);
        	lights[idLight].addNeighbor(lights[9]);
        	break;
        case 9:  
        	lights[idLight].addNeighbor(lights[8]);
        	break;
        default: 
        	System.out.println("Error id Lights too high");
            break;
		}
	}
	
	private void addAllLightsSensor(Blind blind) {
		for(int i=0;i<getEnvironment().getInteriorLightSensorLenght();i++) {
			getEnvironment().getInteriorLightSensor(i).tryTake(blind);
			blind.addInteriorLightSensor(getEnvironment().getInteriorLightSensor(i));
		}
	}
	
	private void setForSeparatedClassroom() {
		if(isSeparated) {
			for(int j=0;j<blinds.length;j++) {
				if(blinds[j].id ==0 || blinds[j].id==1) {
					for(int i=4;i<getEnvironment().getInteriorLightSensorLenght();i++) {
						getEnvironment().getInteriorLightSensor(i).release(blinds[j]);
						blinds[j].removeInteriorLightSensor(getEnvironment().getInteriorLightSensor(i));
					}
				}else {
					for(int i=0;i<getEnvironment().getInteriorLightSensorLenght()-3;i++) {
						getEnvironment().getInteriorLightSensor(i).release(blinds[j]);
						blinds[j].removeInteriorLightSensor(getEnvironment().getInteriorLightSensor(i));
					}
				}
			}	
		}
	}
	
	private void unSetForSeparatedClassroom() {
		if(!isSeparated) {
			for(int j=0;j<blinds.length;j++) {
				if(blinds[j].id ==0 || blinds[j].id==1) {
					for(int i=0;i<getEnvironment().getInteriorLightSensorLenght()-3;i++) {
						getEnvironment().getInteriorLightSensor(i).tryTake(blinds[j]);
						blinds[j].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(i));
					}
					
				}else {
					for(int i=4;i<getEnvironment().getInteriorLightSensorLenght();i++) {
						getEnvironment().getInteriorLightSensor(i).tryTake(blinds[j]);
						blinds[j].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(i));
					}
				}
			}
		}
	}
	
	private void setSensorsBlinds(int idBlind) {
		switch (idBlind) {
			case 0: 
				getEnvironment().getExteriorLightSensor(0).tryTake(blinds[idBlind]);
				blinds[idBlind].addExteriorLightSensor(getEnvironment().getExteriorLightSensor(0));
				addAllLightsSensor(blinds[idBlind]);
				break;
			case 1:
				getEnvironment().getExteriorLightSensor(0).tryTake(blinds[idBlind]);
				blinds[idBlind].addExteriorLightSensor(getEnvironment().getExteriorLightSensor(0));
				addAllLightsSensor(blinds[idBlind]);
				break;
			case 2:
				getEnvironment().getExteriorLightSensor(1).tryTake(blinds[idBlind]);
				blinds[idBlind].addExteriorLightSensor(getEnvironment().getExteriorLightSensor(1));
				addAllLightsSensor(blinds[idBlind]);
				break;
			case 3:
				getEnvironment().getExteriorLightSensor(1).tryTake(blinds[idBlind]);
				blinds[idBlind].addExteriorLightSensor(getEnvironment().getExteriorLightSensor(1));
				addAllLightsSensor(blinds[idBlind]);
				break;
			default: 
				System.out.println("Error id Lights too high");
				break;
		}
	}

	private void setSensorsLights(int idLight) {
		switch (idLight) {
		case 0:  
			getEnvironment().getInteriorLightSensor(0).tryTake(lights[idLight]);
			lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(0));
			
            break;
        case 1:  
        	getEnvironment().getInteriorLightSensor(0).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(0));
             break;
        case 2:  
        	getEnvironment().getInteriorLightSensor(1).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(1));
             break;
        case 3:  
        	getEnvironment().getInteriorLightSensor(1).tryTake(lights[idLight]);
        	getEnvironment().getInteriorLightSensor(2).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(1));
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(2));
             break;
        case 4: 
        	getEnvironment().getInteriorLightSensor(2).tryTake(lights[idLight]);
        	getEnvironment().getInteriorLightSensor(3).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(2));
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(3));
             break;
        case 5: 
        	getEnvironment().getInteriorLightSensor(3).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(3));
             break;
        case 6: 
        	getEnvironment().getInteriorLightSensor(4).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(4));
             break;
        case 7:  
        	getEnvironment().getInteriorLightSensor(4).tryTake(lights[idLight]);
        	getEnvironment().getInteriorLightSensor(5).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(4));
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(5));
             break;
        case 8:  
        	getEnvironment().getInteriorLightSensor(5).tryTake(lights[idLight]);
        	getEnvironment().getInteriorLightSensor(6).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(5));
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(6));
             break;
        case 9:  
        	getEnvironment().getInteriorLightSensor(6).tryTake(lights[idLight]);
        	lights[idLight].addInteriorLightSensor(getEnvironment().getInteriorLightSensor(6));
             break;

        default: 
        	System.out.println("Error id Lights too high");
             break;
		}
	}
	
	public boolean isThereClass() {
		//System.out.println("Check if class");
		boolean return_value= getEnvironment().getOccupiedHours().occupied[currentDayOfWeek][hourOfTheDay];
		//if(return_value) {
			//System.out.println("There's class on the "+currentDayOfWeek+" at "+hourOfTheDay);
		//}
		return return_value;
	}

	public float getFeedbackFactor() {
		//System.out.println("feedbackFactor= "+feedbackFactor);
		return feedbackFactor;
	}

	public void setFeedbackFactor(float feedbackFactor) {
		this.feedbackFactor = (float)Math.round(feedbackFactor * 1000d) / 1000f;
	}

	public float getImpactWantedWithFactors(float brightnessWanted) {
		return brightnessWanted * getFeedbackFactor();
	}
}
