package game;

public class Armor extends Item {
	
	public Armor(int floorLevel, int[] coordinates){
		super(coordinates);
		strength = itemRng.nextInt(floorLevel) + 3; //Fix this up later
	}
	
}
