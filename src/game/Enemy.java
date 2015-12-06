package game;

public class Enemy extends Player{

	private boolean gotAttacked;
	
	public Enemy(int x, int y, int X, int Y, int floorLevel) {
		super(x, y, X, Y);
		this.setAtk(5);           //replace these 5's with some floorLevel based stats
		this.setDef(5);
		this.setHealth(5);
	}
	
	public void AI(Player player, Floor floor){
		
		if (gotAttacked){
			if (isAdjacentToPlayer(floor)){
				
			}
			else{
				
			}
		}
		else{
			
		}
		
	}
	
	public boolean isAdjacentToPlayer(Floor floor){
		
		boolean isAdjacent;
		
		if(floor.characterLocations[this.x1 / TILE_SIZE - 1][this.y1 / TILE_SIZE] == 1 ||
		   floor.characterLocations[this.x2 / TILE_SIZE + 1][this.y1 / TILE_SIZE] == 1 ||
		   floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE - 1] == 1 ||
		   floor.characterLocations[this.x1 / TILE_SIZE][this.y2 / TILE_SIZE + 1] == 1){
			isAdjacent = true;
		}
		else{
			isAdjacent = false;
		}
		
		return isAdjacent;
	}
	
	public void moveRandomly(Floor floor){
		
	}
	
	public void moveTowardsPlayer(Floor floor){
		
	}
	
	public void attackPlayer(Player player){
		
	}
	
}
