package game;

public class Armor extends Item {
	
	public Armor(int floorLevel){
		super();
		strength = itemRng.nextInt(floorLevel) + 3; //Fix this up later
	}
	
}
