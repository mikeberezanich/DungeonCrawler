package game;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.Display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Player {

	private int lvl;
	private int exp;
	private int health;
	private int atk;
	private int def;
	private int mana;
	public int x1;
	public int x2;
	public int y1;
	public int y2;
	private Texture charTexture = new Texture("assets/Panda.png");
	private TextureRegion[] charAnimation = new TextureRegion[9]; //0-2 are standing, 3-5 are one step, 6-8 are another
	public Sprite character;
	public int downFrame = 1;
	public int upFrame = 0;
	public int rightFrame = 0;
	public int leftFrame = 0;
	public static final int TILE_SIZE = 32;
	public static final int FLOOR_TILE = 15;
	public static final int STAIR_TILE = 30;
	public Item[] inventory = new Item[10];
	public int itemsInInventory;
	public boolean[] inventorySpaces = new boolean[10];
	public Item equippedWeapon;
	public Item equippedArmor;
	public int equipmentWeight;
	private Texture fireballTexture = new Texture("assets/fireball.png");
	private TextureRegion[] fireballAnimation = new TextureRegion[4];
	private Sprite fireballSprite;
	private Texture iceLanceTexture = new Texture("assets/icelance.png");
	private TextureRegion[] iceLanceAnimation = new TextureRegion[4];
	private Sprite iceLanceSprite;
	public String directionFaced;
	public boolean isDead;
	public Game game;
	
	//lowercase x and y are x1 and y1 and capital X and Y are x2 and y2
	public Player(int x, int y, int X, int Y){
		x1 = x;
		y1 = y;
		x2 = X;
		y2 = Y;
		
		setHealth(100);
		setAtk(15);
		setDef(15);
		directionFaced = "down";
		isDead = false;
		
		//This is an ugly way of differentiating between the player and enemies, which extend the player
		if (this instanceof Enemy){
			
		}
		else{
			int k = 0;
			for (int i = 0; i < 3; i++){
				for (int j = 1; j < 4; j++){
					charAnimation[k] = new TextureRegion(charTexture, i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
					k++;
				}
			}
			character = new Sprite (charAnimation[0]);
			character.setPosition(x1,y1); 
			setUpSpells();
			setMana(100);
			equipmentWeight = 0;
			itemsInInventory = 0;
			for (int i = 0; i < 10; i++){
				inventorySpaces[i] = false;
			}
		}
		
	}
	
	public void drawPlayer(SpriteBatch batch){
		character.draw(batch);
	}
	
	public void movePlayer(String direction, SpriteBatch batch, Floor floor){
		if (direction == "up"){
			
			//reset the animations for other directions
			downFrame = 0;
			rightFrame = 0;
			leftFrame = 0;
			
			//Code to handle the animation
			if (upFrame == 0)
				character.setRegion(charAnimation[1]);
			else if (upFrame == 1)
				character.setRegion(charAnimation[4]);
			else if (upFrame == 2)
				character.setRegion(charAnimation[1]);
			else if (upFrame == 3)
				character.setRegion(charAnimation[7]);
			upFrame++;
			if (upFrame == 4) //once it gets to end of animation roll, reset
				upFrame = 0;
			
			directionFaced = "up";
			
			if ((floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE + 1] == FLOOR_TILE ||
					floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE + 1] == STAIR_TILE) && 
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE + 1] == null){
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
					character.translateY(TILE_SIZE);
					y1 += TILE_SIZE;
					y2 += TILE_SIZE;
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
				}
		}
		else if (direction == "down"){
			
			//reset the animations for other directions
			upFrame = 0;
			rightFrame = 0;
			leftFrame = 0;
			
			//Code to handle the animation
			if (downFrame == 0)
				character.setRegion(charAnimation[0]);
			else if (downFrame == 1)
				character.setRegion(charAnimation[3]);
			else if (downFrame == 2)
				character.setRegion(charAnimation[0]);
			else if (downFrame == 3)
				character.setRegion(charAnimation[6]);
			downFrame++;
			if (downFrame == 4) //once it gets to end of animation roll, reset
				downFrame = 0;
			
			directionFaced = "down";
			
			if ((floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE - 1] == FLOOR_TILE ||
					floor.floorLayout[x1/TILE_SIZE][y1/TILE_SIZE - 1] == STAIR_TILE) && 
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE - 1] == null){
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
					character.translateY(-TILE_SIZE);
					y1 -= TILE_SIZE;
					y2 -= TILE_SIZE;
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
				}
			
		}
		else if (direction == "right"){
			
			//reset the animations for other directions
			downFrame = 0;
			upFrame = 0;
			leftFrame = 0;
			
			if (rightFrame == 0)
				character.setRegion(charAnimation[2]);
			else if (rightFrame == 1)
				character.setRegion(charAnimation[5]);
			else if (rightFrame == 2)
				character.setRegion(charAnimation[2]);
			else if (rightFrame == 3)
				character.setRegion(charAnimation[8]);
			rightFrame++;
			if (rightFrame == 4) //once it gets to end of animation roll, reset
				rightFrame = 0;
			
			directionFaced = "right";
			
			if ((floor.floorLayout[x1/TILE_SIZE + 1][y1/TILE_SIZE] == FLOOR_TILE ||
					floor.floorLayout[x1/TILE_SIZE + 1][y1/TILE_SIZE] == STAIR_TILE) && 
					floor.characterLocations[x1 / TILE_SIZE + 1][y1 / TILE_SIZE] == null){
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
					character.translateX(TILE_SIZE);
					x1 += TILE_SIZE;
					x2 += TILE_SIZE;
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
				}
		}
		else if (direction == "left"){
			
			//reset the animations for other directions
			downFrame = 0;
			upFrame = 0;
			rightFrame = 0;
			
			if (leftFrame == 0){
				character.setRegion(charAnimation[2]);
				character.flip(true, false);
			}
			else if (leftFrame == 1){
				character.setRegion(charAnimation[5]);
				character.flip(true, false);
			}
			else if (leftFrame == 2){
				character.setRegion(charAnimation[2]);
				character.flip(true, false);
			}
			else if (leftFrame == 3){
				character.setRegion(charAnimation[8]);
				character.flip(true, false);
			}
			leftFrame++;
			if (leftFrame == 4) //once it gets to end of animation roll, reset
				leftFrame = 0;
			
			directionFaced = "left";
			
			if ((floor.floorLayout[x1/TILE_SIZE - 1][y1/TILE_SIZE] == FLOOR_TILE ||
					floor.floorLayout[x1/TILE_SIZE - 1][y1/TILE_SIZE] == STAIR_TILE) && 
					floor.characterLocations[x1 / TILE_SIZE - 1][y1 / TILE_SIZE] == null){
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = null;
					character.translateX(-TILE_SIZE);
					x1 -= TILE_SIZE;
					x2 -= TILE_SIZE;
					floor.characterLocations[x1 / TILE_SIZE][y1 / TILE_SIZE] = this;
				}
		}
	}
	
	public int getPositionTile(Floor floor){
		return floor.floorLayout[(int) (character.getX()/TILE_SIZE)][(int) (character.getY()/TILE_SIZE)];
	}
	
	public void moveToNewFloor(){
		downFrame = 0;
		character.setRegion(charAnimation[0]);
	}
	
	public void equipItem(Item item){
		if (item instanceof Weapon){
			if (equippedWeapon == null){
				equippedWeapon = item;
				setAtk(atk + item.strength);
			}
			else {
				unequipItem(equippedWeapon);
				equippedWeapon = item;
				setAtk(atk + item.strength);
			}
			equipmentWeight += item.weight;
		}
		else if (item instanceof Armor){
			if (equippedArmor == null){
				equippedArmor = item;
				setDef(def + item.strength);
			}
			else {
				unequipItem(equippedWeapon);
				equippedWeapon = item;
				setDef(def + item.strength);
			}
			equipmentWeight += item.weight;
		}
		
	}
	public void unequipItem(Item item){
		if (item.isEquipped){
			if (item instanceof Weapon){
				setAtk(atk - item.strength);
				equippedWeapon = null;
			}
			else if (item instanceof Armor){
				setDef(def - item.strength);
				equippedArmor = null;
			}
			equipmentWeight -= item.weight;
		}
		
	}
	
	public void usePotion(Potion potion){
		if(potion.classification == Potion.HEALTH_POTION){
			setHealth(getHealth() + 50);
			if (getHealth() > 100)
				setHealth(100);
		}
		else if (potion.classification == Potion.MANA_POTION){
			setMana(getMana() + 50);
			if (getMana() > 100)
				setMana(100);
		}
	}
	
	public void pickUpItem(Item item, Floor floor){
		if (itemsInInventory < 10){
			for (int i = 0; inventorySpaces[i] != false; i++){
				inventory[i] = item;
			}
		}
		floor.itemLocations[(int) (item.itemSprite.getX() / TILE_SIZE)][(int) (item.itemSprite.getY() / TILE_SIZE)] = null;
		floor.itemsOnFloor.remove(item);
	}
	
	//inventorySpace refers to the position in your inventory of the item being dropped i.e. the first item in your inventory is 0, next is 1, etc.
	public void dropItem(Item item, Floor floor, int inventorySpace){
		inventory[inventorySpace] = null;
		inventorySpaces[inventorySpace] = false;
		itemsInInventory--;
		if (item.isEquipped){
			if (item instanceof Weapon)
				equippedWeapon = null;
			else if (item instanceof Armor)
				equippedArmor = null;
		}
		floor.itemLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE] = item;
		floor.itemsOnFloor.add(item);
	}
	
	public void attack(Player enemy, SpriteBatch batch, Floor floor){
		
		int damageDealt;
		Random damageRng = new Random();
		
		if (equippedWeapon != null){
			this.equippedWeapon.itemSprite.setPosition(this.x1, this.y2);
			this.equippedWeapon.itemSprite.rotate(-45);
			for (int i = 1; i <= 6; i++){
				this.equippedWeapon.itemSprite.rotate(-15);
				this.equippedWeapon.itemSprite.draw(batch);
			}
			this.equippedWeapon.itemSprite.rotate(135);
		}
		damageDealt = this.getAtk() + 3 - damageRng.nextInt(7) - enemy.getDef(); 
		if (damageDealt < 0)
			damageDealt = 0;
		enemy.setHealth(enemy.getHealth()-damageDealt);
		
		if (enemy instanceof Enemy){
			((Enemy) enemy).gotAttacked = true;
			System.out.println("Enemy health " + enemy.getHealth());
		}
		else{
			System.out.println("Player health " + enemy.getHealth());
		}
		if (enemy.getHealth() <= 0){
			enemy.die(floor);
		}
	}
	
	public void castFireball(String direction, Floor floor, SpriteBatch batch){
		if (this.getMana() >= 30){
			fireballSprite = new Sprite(fireballAnimation[0]);
			int animationRoll = 0;
			if (direction == "right"){
				fireballSprite.rotate(270);
				fireballSprite.setPosition(this.x2, this.y1);
//				while (floor.characterLocations[(int) (fireballSprite.getX() / TILE_SIZE)][this.y1 / TILE_SIZE] == null)
				while (floor.floorLayout[(int) (fireballSprite.getX() / TILE_SIZE)][this.y1 / TILE_SIZE] == 15){
					fireballSprite.draw(batch);
					fireballSprite.setRegion(fireballAnimation[animationRoll++]);
					fireballSprite.translateX(8);
					if (animationRoll > 3)
						animationRoll = 0;
				}
					
			}
			else if (direction == "left"){
				fireballSprite.rotate(90);
				
			}
			else if (direction == "up"){
				fireballSprite.rotate(180);
				
			}
			else if (direction == "down"){
				
			}
			
			this.setMana(this.getMana() - 30);
		}
	}
	
	public void castIceLance(){
		if (this.getMana() >= 30){
			
			
			this.setMana(this.getMana() - 30);
		}
	}
	
	public void castHealingTouch(){
		if (this.getMana() >= 30){
			this.setHealth(this.getHealth() + 50);
			if (this.getHealth() > 100)
				this.setHealth(100);
			this.setMana(this.getMana() - 30);
		}
	}
	
	private void setUpSpells(){
		for(int i = 0; i < 4; i++){
			fireballAnimation[i] = new TextureRegion(fireballTexture, i * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
			iceLanceAnimation[i] = new TextureRegion(fireballTexture, i * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
		}
	}
	
	public void changeDirection(String direction){
		if (direction == "left"){
			directionFaced = "left";
			character.setRegion(charAnimation[2]);
			character.flip(true, false);
		}
		else if (direction == "right"){
			directionFaced = "right";
			character.setRegion(charAnimation[2]);
		}
		else if (direction == "up"){
			directionFaced = "up";
			character.setRegion(charAnimation[1]);
		}
		else if (direction == "down"){
			directionFaced = "down";
			character.setRegion(charAnimation[0]);
		}
	}
	
	public void die(Floor floor){
		
		isDead = true;
		//code to bring up death screen, maybe a death animation
		
		//need to pass the score to this eventually
		try {
			new GameOver();
//			game.dispose();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//window.stop();
		//window.exit();
		
	}
	
	public int getLvl(){
		return lvl;
	}
	public void setLvl(int l){
		lvl = l;
	}
	
	public int getExp(){
		return exp;
	}
	public void setExp(int e){
		exp = e;
	}
	
	public int getHealth(){
		return health;
	}
	public void setHealth(int h){
		health = h;
	}
	
	public int getAtk(){
		return atk;
	}
	public void setAtk(int a){
		atk = a;
	}
	
	public int getDef(){
		return def;
	}
	public void setDef(int d){
		def = d;
	}
	
	public int getMana(){
		return mana;
	}
	public void setMana(int m){
		mana = m;
	}
	
}
