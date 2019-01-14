package test;

import java.util.Arrays;
import java.util.Random;

public class OccupiedHours {

	public boolean[][] occupied;
	
	public OccupiedHours() {
		occupied = new boolean[7][24];
		randomizeOccupation();
	}
	
	
	public boolean classroomUsed(int heures, int jour) {	
		return occupied[jour][heures];
	}
	
	public void randomizeOccupation() {
		//System.out.println("RANDOMIZE BEGIN");
		Random rg = new Random();
		for (int i = 0; i < occupied.length; i++) {
			for (int j = 0; j < occupied[0].length; j++) {
				if (i >= 5 || j >= 21 || j < 8) {
					occupied[i][j] = false;
				} else if (j >= 18){
					if (i == 0 || i == 2 || i == 4) {
						occupied[i][j] = true;
					} else {
						occupied[i][j] = false;
					}
				} else {
					occupied[i][j] = rg.nextBoolean();
					//if(occupied[i][j]) {
					//	System.out.println("    THERE S A CLASS HERE "+i+" "+j);
					//}
				}
			}
		}
	}
}
