package game;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy extends Player{

	public boolean gotAttacked;
	private Random movementRng = new Random();
	private Texture enemySpritesheet = new Texture("assets/zombies2.png");
	private TextureRegion zombieSprites = new TextureRegion(enemySpritesheet, 0, 0, 3 * TILE_SIZE, 192);
//	private TextureRegion skeletonSprites = new TextureRegion(enemySpritesheet, 3 * TILE_SIZE, 0, 6 * TILE_SIZE, 256);
	private TextureRegion[] enemyAnimation = new TextureRegion[12];
	
	public Enemy(int x, int y, int X, int Y, Floor floor) {
		super(x, y, X, Y);
		setAtk(5);           //replace these 5's with some floorLevel based stats
		setDef(5);
		setHealth(5);
		floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
		int k = 0;
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 3; j++){
				enemyAnimation[k] = new TextureRegion(zombieSprites, j * TILE_SIZE, i * 48, TILE_SIZE, 48);
				k++;
			}
		}
		character = new Sprite (enemyAnimation[1]);
//		character.setScale(1f, .75f);
		character.setSize(32, 32);
		character.setPosition(x1,y1); 
	}
	
	public void AI(Player player, Floor floor, SpriteBatch batch){
		
		if (gotAttacked){
			if (isAdjacentToPlayer(floor, player)){
				attack(player, batch);
			}
			else{
				moveTowardsPlayer(floor);
			}
		}
		else{
			if(checkForPlayer(player, floor)){
				if (isAdjacentToPlayer(floor, player)){
					attack(player, batch);
				}
				else{
					moveTowardsPlayer(floor);
				}
			}
			else{
				moveRandomly(floor);
			}
		}
		
	}
	
	public boolean isAdjacentToPlayer(Floor floor, Player player){
		
		boolean isAdjacent;
		
		//the && conditions are to avoid going out of bounds of the array
		if((this.x1 / TILE_SIZE - 1 >= 0 && floor.characterLocations[this.x1 / TILE_SIZE - 1][this.y1 / TILE_SIZE] == player) ||
		   (this.x1 / TILE_SIZE + 1 < 32 && floor.characterLocations[this.x2 / TILE_SIZE + 1][this.y1 / TILE_SIZE] == player) ||
		   (this.y1 / TILE_SIZE - 1 >= 0 && floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE - 1] == player) ||
		   (this.y1 / TILE_SIZE + 1 < 24 && floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE + 1] == player)){
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
			case 0: //reset the animations for other directions
				downFrame = 0;
				upFrame = 0;
				rightFrame = 0;
				
				if (leftFrame == 0)
					character.setRegion(enemyAnimation[4]);
				else if (leftFrame == 1)
					character.setRegion(enemyAnimation[3]);
				else if (leftFrame == 2)
					character.setRegion(enemyAnimation[4]);
				else if (leftFrame == 3)
					character.setRegion(enemyAnimation[5]);
				leftFrame++;
				if (leftFrame == 4) //once it gets to end of animation roll, reset
					leftFrame = 0;
				
				if ((floor.floorLayout[x1/TILE_SIZE - 1][y1/TILE_SIZE] == FLOOR_TILE ||
					floor.floorLayout[x1/TILE_SIZE - 1][y1/TILE_SIZE] == STAIR_TILE) && 
					floor.characterLocations[x1 / TILE_SIZE - 1][y1 / TILE_SIZE] == null){
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
					character.translateX(-TILE_SIZE);
					x1 -= TILE_SIZE;
					x2 -= TILE_SIZE;
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
					canMove = true;
				}
					break;
//			
			case 1: //reset the animations for other directions
				downFrame = 0;
				upFrame = 0;
				leftFrame = 0;
				
				if (rightFrame == 0)
					character.setRegion(enemyAnimation[7]);
				else if (rightFrame == 1)
					character.setRegion(enemyAnimation[8]);
				else if (rightFrame == 2)
					character.setRegion(enemyAnimation[7]);
				else if (rightFrame == 3)
					character.setRegion(enemyAnimation[6]);
				rightFrame++;
				if (rightFrame == 4) //once it gets to end of animation roll, reset
					rightFrame = 0;
				
				if ((floor.floorLayout[x1/TILE_SIZE + 1][y1/TILE_SIZE] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE + 1][y1/TILE_SIZE] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE + 1][y1 / TILE_SIZE] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateX(TILE_SIZE);
						x1 += TILE_SIZE;
						x2 += TILE_SIZE;
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
						canMove = true;
					}
					break;
//		
			case 2:
				
				//reset the animations for other directions
				downFrame = 0;
				rightFrame = 0;
				leftFrame = 0;
				
				//Code to handle the animation
				if (upFrame == 0)
					character.setRegion(enemyAnimation[10]);
				else if (upFrame == 1)
					character.setRegion(enemyAnimation[9]);
				else if (upFrame == 2)
					character.setRegion(enemyAnimation[10]);
				else if (upFrame == 3)
					character.setRegion(enemyAnimation[11]);
				upFrame++;
				if (upFrame == 4) //once it gets to end of animation roll, reset
					upFrame = 0;
				
				if ((floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE + 1] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE + 1] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE + 1] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateY(TILE_SIZE);
						y1 += TILE_SIZE;
						y2 += TILE_SIZE;
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
						canMove = true;
					}
					break;
//			
			case 3: //reset the animations for other directions
				upFrame = 0;
				rightFrame = 0;
				leftFrame = 0;
				
				//Code to handle the animation
				if (downFrame == 0)
					character.setRegion(enemyAnimation[1]);
				else if (downFrame == 1)
					character.setRegion(enemyAnimation[0]);
				else if (downFrame == 2)
					character.setRegion(enemyAnimation[1]);
				else if (downFrame == 3)
					character.setRegion(enemyAnimation[2]);
				downFrame++;
				if (downFrame == 4) //once it gets to end of animation roll, reset
					downFrame = 0;
				
				if ((floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE - 1] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE - 1] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE - 1] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateY(-TILE_SIZE);
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
	
	public boolean checkForPlayer(Player player, Floor floor){
		
		int distance;
		
		switch(player.equipmentWeight){
			case 0:
			case 1:
			case 2: distance = 3;
					break;
			case 3:
			case 4:
			case 5: distance = 4;
					break;
			default: distance = 5;
					 break;
		}
		
		//the && conditions are to avoid going out of bounds of the array
		for (int i = 0; i < distance; i++){
			if ((this.x1 / TILE_SIZE + i < 32 && floor.characterLocations[this.x1 / TILE_SIZE + i][this.y1 / TILE_SIZE] == player) ||
				(this.x1 / TILE_SIZE - i >= 0 && floor.characterLocations[this.x1 / TILE_SIZE - i][this.y1 / TILE_SIZE] == player) ||
				(this.y1 / TILE_SIZE + i < 24 && floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE + i] == player) ||
				(this.y1 / TILE_SIZE - i >= 0 && floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE - i] == player) || 
				(this.x1 / TILE_SIZE + i < 32 && this.y1 / TILE_SIZE + i < 24 && floor.characterLocations[this.x1 / TILE_SIZE + i][this.y1 / TILE_SIZE + i] == player) ||
				(this.x1 / TILE_SIZE + i < 32 && this.y1 / TILE_SIZE + i > 0 && floor.characterLocations[this.x1 / TILE_SIZE + i][this.y1 / TILE_SIZE - i] == player) ||
				(this.x1 / TILE_SIZE + i >= 0 && this.y1 / TILE_SIZE + i > 0 && floor.characterLocations[this.x1 / TILE_SIZE - i][this.y1 / TILE_SIZE - i] == player) ||
				(this.x1 / TILE_SIZE + i >= 0 && this.y1 / TILE_SIZE + i < 24 && floor.characterLocations[this.x1 / TILE_SIZE - i][this.y1 / TILE_SIZE + i] == player))
				return true;
		}
		
		//If the above loop doesn't find the player within so many tiles then return false
		return false;
	}
	
}
