package game;

import java.util.Random;

public class Enemy extends Player{

	private boolean gotAttacked;
	private Random movementRng = new Random();
	
	public Enemy(int x, int y, int X, int Y, int floorLevel, Floor floor) {
		super(x, y, X, Y);
		setAtk(5);           //replace these 5's with some floorLevel based stats
		setDef(5);
		setHealth(5);
		floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
	}
	
	public void AI(Player player, Floor floor){
		
		if (gotAttacked){
			if (isAdjacentToPlayer(floor, player)){
				
			}
			else{
				
			}
		}
		else{
			
		}
		
	}
	
	public boolean isAdjacentToPlayer(Floor floor, Player player){
		
		boolean isAdjacent;
		
		if(floor.characterLocations[this.x1 / TILE_SIZE - 1][this.y1 / TILE_SIZE] == player ||
		   floor.characterLocations[this.x2 / TILE_SIZE + 1][this.y1 / TILE_SIZE] == player ||
		   floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE - 1] == player ||
		   floor.characterLocations[this.x1 / TILE_SIZE][this.y2 / TILE_SIZE + 1] == player){
			isAdjacent = true;
		}
		else{
			isAdjacent = false;
		}
		
		return isAdjacent;
	}
	
	public void moveRandomly(Floor floor){
		
		boolean canMove = false;
		int direction;
		
		while (!canMove){
			direction = movementRng.nextInt(4);
			switch(direction){
			case 0: if(floor.floorLayout[x1 / TILE_SIZE - 1][y1 / TILE_SIZE] == floor.FLOOR_TILE && 
					   floor.characterLocations[x1 / TILE_SIZE - 1][y1 / TILE_SIZE] == null){
							floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
							x1 -= TILE_SIZE;
							x2 -= TILE_SIZE;
							floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
							canMove = true;
					}
					break;
			case 1: if(floor.floorLayout[x1 / TILE_SIZE + 1][y1 / TILE_SIZE] == floor.FLOOR_TILE && 
					   floor.characterLocations[x1 / TILE_SIZE + 1][y1 / TILE_SIZE] == null){
							floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
							x1 += TILE_SIZE;
							x2 += TILE_SIZE;
							floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
							canMove = true;
					}
					break;
			case 2: if(floor.floorLayout[x1 / TILE_SIZE][y1 / TILE_SIZE + 1] == floor.FLOOR_TILE && 
					   floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE + 1] == null){
							floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
							y1 += TILE_SIZE;
							y2 += TILE_SIZE;
							floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
							canMove = true;
					}
					break;
			case 3: if(floor.floorLayout[x1 / TILE_SIZE][y1 / TILE_SIZE - 1] == floor.FLOOR_TILE && 
					   floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE - 1] == null){
				            floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
							y1 -= TILE_SIZE;
							y2 -= TILE_SIZE;
							floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
							canMove = true;
					}
					break;
			}
		}
	}
	
	public void moveTowardsPlayer(Floor floor){
		
	}
	
	public void attackPlayer(Player player){
		
	}
	
}
