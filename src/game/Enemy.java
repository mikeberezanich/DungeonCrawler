package game;

import java.util.Random;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy extends Player{

	public boolean gotAttacked;
	private Random movementRng = new Random();
	private Texture enemySpritesheet = new Texture("assets/zombie.png");
	private TextureRegion zombieSprites = new TextureRegion(enemySpritesheet, 0, 0, 3 * TILE_SIZE, 192);
	private TextureRegion[] enemyAnimation = new TextureRegion[12];
	public static int LEFT = 0;
	public static int RIGHT = 1;
	public static int UP = 2;
	public static int DOWN = 3;
	public boolean isSlowed = false;
	public boolean slowTurn = false;
	
	//contstructor for enemies, takes coordinates and the floor it's on so it can generate stats based on this
	public Enemy(int x, int y, int X, int Y, Floor floor) {
		super(x, y, X, Y);
		setAtk((int)(20 + floor.floorLevel * 1.5));
		setDef((int)(5 + floor.floorLevel * 1.5));
		
		//sets up the enemy's sprites/animations
		int k = 0;
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 3; j++){
				enemyAnimation[k] = new TextureRegion(zombieSprites, j * TILE_SIZE, i * 48, TILE_SIZE, 48);
				k++;
			}
		}
		
		character = new Sprite (enemyAnimation[1]);
		character.setSize(32, 32);
		character.setPosition(x1,y1); 
		floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
	}
	
	//enemy's AI, decides move based on conditions
	public void AI(Player player, Floor floor, SpriteBatch batch){
		
		//first checks if it's a slowed down turn
		if (!slowTurn){
			//if the enemy got attacked, attack the player if adjacent or walk towards them if not
			if (gotAttacked){
				if (isAdjacentToPlayer(floor, player)){
					facePlayer(player, floor);
					attack(player, batch, floor);
				}
				else{
					moveTowardsPlayer(player, floor);
				}
			}
			//if didn't get attacked, check surrounding tiles for player
			else{
				//checks if within range, based on the players equipment weight
				if(checkForPlayer(player, floor)){
					//if adjacent to player, face and attack them
					if (isAdjacentToPlayer(floor, player)){
						facePlayer(player, floor);
						attack(player, batch, floor);
					}
					//if not adjacent but still nearby, move towards the player
					else{
						moveTowardsPlayer(player, floor);
					}
				}
				//if the player is not nearby, just move a random direction
				else{
					moveRandomly(floor);
				}
			}
		}
		
		//If the enemy has been slowed, alternate between getting to move next turn or not
		if (isSlowed){
			if (slowTurn)
				slowTurn = false;
			else
				slowTurn = true;
		}
		
	}
	
	//checks if the player is adjacent to this enemy
	public boolean isAdjacentToPlayer(Floor floor, Player player){
		
		boolean isAdjacent;
		
		//the && conditions are to avoid going out of bounds of the array
		if((this.x1 / TILE_SIZE - 1 >= 0 && floor.characterLocations[this.x1 / TILE_SIZE - 1][this.y1 / TILE_SIZE] == player) ||
		   (this.x1 / TILE_SIZE + 1 < 32 && floor.characterLocations[this.x1 / TILE_SIZE + 1][this.y1 / TILE_SIZE] == player) ||
		   (this.y1 / TILE_SIZE - 1 >= 0 && floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE - 1] == player) ||
		   (this.y1 / TILE_SIZE + 1 < 24 && floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE + 1] == player)){
			isAdjacent = true;
		}
		else{
			isAdjacent = false;
		}
		
		return isAdjacent;
	}
	
	//function to move the enemy randomly
	public void moveRandomly(Floor floor){
		
		boolean canMove = false;
		int direction;
		
		//while the move randomly selected cannot be performed due to another character or wall being in the way
		while (!canMove){
			//pick a random direction
			direction = movementRng.nextInt(4);
			
			//handles whether or not the character can move the direction selected, and if they can, handles the movement and animation
			switch(direction){
			case 0: if ((floor.floorLayout[x1/TILE_SIZE - 1][y1/TILE_SIZE] == FLOOR_TILE ||
					floor.floorLayout[x1/TILE_SIZE - 1][y1/TILE_SIZE] == STAIR_TILE) && 
					floor.characterLocations[x1 / TILE_SIZE - 1][y1 / TILE_SIZE] == null){
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
					character.translateX(-TILE_SIZE);
					x1 -= TILE_SIZE;
					x2 -= TILE_SIZE;
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
					directionFaced = "left";
					canMove = true;
					
					//reset the animations for other directions
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
				}
					break;
	
			case 1: if ((floor.floorLayout[x1/TILE_SIZE + 1][y1/TILE_SIZE] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE + 1][y1/TILE_SIZE] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE + 1][y1 / TILE_SIZE] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateX(TILE_SIZE);
						x1 += TILE_SIZE;
						x2 += TILE_SIZE;
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
						directionFaced = "right";
						canMove = true;
						
						//reset the animations for other directions
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
					}
					break;
		
			case 2: if ((floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE + 1] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE + 1] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE + 1] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateY(TILE_SIZE);
						y1 += TILE_SIZE;
						y2 += TILE_SIZE;
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
						directionFaced = "up";
						canMove = true;
						
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
					}
					break;
			
			case 3: if ((floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE - 1] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE - 1] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE - 1] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateY(-TILE_SIZE);
						y1 -= TILE_SIZE;
						y2 -= TILE_SIZE;
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
						directionFaced = "down";
						canMove = true;
						
						//reset the animations for other directions
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
					}
					break;
			}
		}
	}
	
	//function to move the enemy towards the player
	public void moveTowardsPlayer(Player player, Floor floor){
		int[] directionsToMove = new int[4];
		int i = 0;
		boolean canMove = false;
		
		//finds all the directions that would help the enemy get closer to the player
		if (player.x1 < this.x1){
			directionsToMove[i++] = LEFT;
		}
		if (player.x1 > this.x1){
			directionsToMove[i++] = RIGHT;
		}
		if (player.y1 > this.y1){
			directionsToMove[i++] = UP;
		}
		if (player.y1 < this.y1){
			directionsToMove[i++] = DOWN;
		}
		
		//goes through all the directions that would help the enemy get closer to the player and picks one based on if it can move that way
		//then it handles the movement and animation based on the first succesful move
		for (int j = 0; j <= i && !canMove; j++){
			switch(directionsToMove[j]){
			case 0: if ((floor.floorLayout[x1/TILE_SIZE - 1][y1/TILE_SIZE] == FLOOR_TILE ||
					floor.floorLayout[x1/TILE_SIZE - 1][y1/TILE_SIZE] == STAIR_TILE) && 
					floor.characterLocations[x1 / TILE_SIZE - 1][y1 / TILE_SIZE] == null){
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
					character.translateX(-TILE_SIZE);
					x1 -= TILE_SIZE;
					x2 -= TILE_SIZE;
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
					directionFaced = "left";
					canMove = true;
					
					//reset the animations for other directions
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
				}
					break;
	
			case 1: if ((floor.floorLayout[x1/TILE_SIZE + 1][y1/TILE_SIZE] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE + 1][y1/TILE_SIZE] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE + 1][y1 / TILE_SIZE] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateX(TILE_SIZE);
						x1 += TILE_SIZE;
						x2 += TILE_SIZE;
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
						directionFaced = "right";
						canMove = true;
						
						//reset the animations for other directions
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
					}
					break;
		
			case 2: if ((floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE + 1] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE + 1] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE + 1] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateY(TILE_SIZE);
						y1 += TILE_SIZE;
						y2 += TILE_SIZE;
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
						directionFaced = "up";
						canMove = true;
						
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
					}
					break;
			
			case 3: if ((floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE - 1] == FLOOR_TILE ||
						floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE - 1] == STAIR_TILE) && 
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE - 1] == null){
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
						character.translateY(-TILE_SIZE);
						y1 -= TILE_SIZE;
						y2 -= TILE_SIZE;
						floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
						directionFaced = "down";
						canMove = true;
						
						//reset the animations for other directions
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
					}
					break;
			}
		
		}
	}
	
	//checks surroundings for player based on the player's current equipment weight
	public boolean checkForPlayer(Player player, Floor floor){
		
		int distance;
		
		//the number of tiles checked depends on the players equipment weight
		switch(player.equipmentWeight){
			case 0:
			case 1:
			case 2: distance = 4;
					break;
			case 3:
			case 4:
			case 5: distance = 5;
					break;
			default: distance = 6;
					 break;
		}
		
		//the && conditions are to avoid going out of bounds of the array
		//checks the number of tiles in each of 8 directions, and a couple spots inbetween
		for (int i = 0; i < distance; i++){
			if ((this.x1 / TILE_SIZE + i < 32 && floor.characterLocations[this.x1 / TILE_SIZE + i][this.y1 / TILE_SIZE] == player) ||
				(this.x1 / TILE_SIZE - i >= 0 && floor.characterLocations[this.x1 / TILE_SIZE - i][this.y1 / TILE_SIZE] == player) ||
				(this.y1 / TILE_SIZE + i < 24 && floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE + i] == player) ||
				(this.y1 / TILE_SIZE - i >= 0 && floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE - i] == player) || 
				(this.x1 / TILE_SIZE + i < 32 && this.y1 / TILE_SIZE + i < 24 && floor.characterLocations[this.x1 / TILE_SIZE + i][this.y1 / TILE_SIZE + i] == player) ||
				(this.x1 / TILE_SIZE + i < 32 && this.y1 / TILE_SIZE - i >= 0 && floor.characterLocations[this.x1 / TILE_SIZE + i][this.y1 / TILE_SIZE - i] == player) ||
				(this.x1 / TILE_SIZE - i >= 0 && this.y1 / TILE_SIZE - i >= 0 && floor.characterLocations[this.x1 / TILE_SIZE - i][this.y1 / TILE_SIZE - i] == player) ||
				(this.x1 / TILE_SIZE - i >= 0 && this.y1 / TILE_SIZE + i < 24 && floor.characterLocations[this.x1 / TILE_SIZE - i][this.y1 / TILE_SIZE + i] == player) || 
				(this.x1 / TILE_SIZE + i < 32 && this.x1 / TILE_SIZE - 1 >= 0 && this.y1 / TILE_SIZE + 1 < 24 && floor.characterLocations[this.x1 / TILE_SIZE + i - 1][this.y1 / TILE_SIZE + 1] == player) ||
				(this.x1 / TILE_SIZE + i < 32 && this.x1 / TILE_SIZE - 2 >= 0 && this.y1 / TILE_SIZE + 2 < 24 && floor.characterLocations[this.x1 / TILE_SIZE + i - 2][this.y1 / TILE_SIZE + 2] == player) ||
				(this.x1 / TILE_SIZE - i >= 0 && this.x1 / TILE_SIZE  + 1 < 32 && this.y1 / TILE_SIZE + 1 < 24 && floor.characterLocations[this.x1 / TILE_SIZE - i + 1][this.y1 / TILE_SIZE + 1] == player) ||
				(this.x1 / TILE_SIZE - i >= 0 && this.x1 / TILE_SIZE  + 2 < 32 && this.y1 / TILE_SIZE + 2 < 24 && floor.characterLocations[this.x1 / TILE_SIZE - i + 2][this.y1 / TILE_SIZE + 2] == player) ||
				(this.x1 / TILE_SIZE + i < 32 && this.x1 / TILE_SIZE - 1 >= 0 && this.y1 / TILE_SIZE - 1 >= 0 && floor.characterLocations[this.x1 / TILE_SIZE + i - 1][this.y1 / TILE_SIZE - 1] == player) ||
				(this.x1 / TILE_SIZE + i < 32 && this.x1 / TILE_SIZE - 2 >= 0 && this.y1 / TILE_SIZE - 2 >= 0 && floor.characterLocations[this.x1 / TILE_SIZE + i - 2][this.y1 / TILE_SIZE - 2] == player) ||
				(this.x1 / TILE_SIZE - i >= 0 && this.x1 / TILE_SIZE  + 1 < 32 && this.y1 / TILE_SIZE - 1 >= 0 && floor.characterLocations[this.x1 / TILE_SIZE - i + 1][this.y1 / TILE_SIZE - 1] == player) ||
				(this.x1 / TILE_SIZE - i >= 0 && this.x1 / TILE_SIZE  + 2 < 32 && this.y1 / TILE_SIZE - 2 >= 0 && floor.characterLocations[this.x1 / TILE_SIZE - i + 2][this.y1 / TILE_SIZE - 2] == player))
				return true;
		}
		
		//If the above loop doesn't find the player within so many tiles then return false
		return false;
	}
	
	//used to make the enemy face the player during combat
	public void facePlayer(Player player, Floor floor){
		if(floor.characterLocations[this.x1 / TILE_SIZE - 1][this.y1 / TILE_SIZE] == player){
			this.character.setRegion(enemyAnimation[4]);
		}
		else if(floor.characterLocations[this.x1 / TILE_SIZE + 1][this.y1 / TILE_SIZE] == player){
			this.character.setRegion(enemyAnimation[7]);
		}
		else if(floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE + 1] == player){
			this.character.setRegion(enemyAnimation[10]);
		}
		else if(floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE - 1] == player){
			this.character.setRegion(enemyAnimation[1]);
		}
	}
	
	//when enemy's health reaches 0 or below, removes them from the screen and from the list of enemies on the floor
	public void die(Floor floor){
		
		floor.characterLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE] = null;
		
		floor.enemiesOnFloor.remove(this);
		
		//maybe add a death animation or corpse left over
		
	}

	
}
