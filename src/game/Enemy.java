package game;

import java.util.Random;

public class Enemy extends Player{

	private boolean gotAttacked;
	private Random movementRng = new Random();
	
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
		
		boolean canMove = false;
		int direction;
		
		while (!canMove){
			direction = movementRng.nextInt(4);
			switch(direction){
			case 0: if(floor.floorLayout[x1 / TILE_SIZE - 1][y1 / TILE_SIZE] == floor.FLOOR_TILE && 
					   floor.characterLocations[x1 / TILE_SIZE - 1][y1 / TILE_SIZE] == floor.NO_CHARACTER){
							x1 -= TILE_SIZE;
							x2 -= TILE_SIZE;
							canMove = true;
					}
					break;
			case 1: if(floor.floorLayout[x1 / TILE_SIZE + 1][y1 / TILE_SIZE] == floor.FLOOR_TILE && 
					   floor.characterLocations[x1 / TILE_SIZE + 1][y1 / TILE_SIZE] == floor.NO_CHARACTER){
							x1 += TILE_SIZE;
							x2 += TILE_SIZE;
							canMove = true;
					}
					break;
			case 2: if(floor.floorLayout[x1 / TILE_SIZE][y1 / TILE_SIZE + 1] == floor.FLOOR_TILE && 
					   floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE + 1] == floor.NO_CHARACTER){
							y1 += TILE_SIZE;
							y2 += TILE_SIZE;
							canMove = true;
					}
					break;
			case 3: if(floor.floorLayout[x1 / TILE_SIZE][y1 / TILE_SIZE - 1] == floor.FLOOR_TILE && 
					   floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE - 1] == floor.NO_CHARACTER){
							y1 -= TILE_SIZE;
							y2 -= TILE_SIZE;
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
