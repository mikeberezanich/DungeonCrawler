package game;

public class Weapon extends Item {
	
	public Weapon(int floorLevel) {
		super();
		strength = strengthRNG.nextInt(floorLevel) + 3; //Fix this up later
	}


}
