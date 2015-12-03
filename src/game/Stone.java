package game;

import java.util.Random;

public class Stone {

	private Random rng = new Random();
	private static String mark;
	
	public Stone(){
		switch (rng.nextInt(7)){
			case 0: mark = "gaurdian";
					break;
			case 1: mark = "berserker";
					break;
			case 2: mark = "vampire";
					break;
			case 3: mark = "hunter";
					break;
			case 4: mark = "wizard";
					break;
			case 5: mark = "alchemist";
					break;
			case 6: mark = "ascendant";
					break;
		}
	}
	
	public String getMark(){
		return mark;
	}
	
}
