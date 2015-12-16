package game;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.Display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	Random damageRng = new Random();
	private Texture fireballTexture = new Texture("assets/fireball.png");
	private TextureRegion[] fireballAnimation = new TextureRegion[4];
	public Sprite fireballSprite;
	private Texture iceLanceTexture = new Texture("assets/icelance.png");
	private TextureRegion[] iceLanceAnimation = new TextureRegion[4];
	private Sprite iceLanceSprite;
	public String directionFaced;
	public boolean isDead;
	private BitmapFont damageFont = new BitmapFont();
	private BitmapFont regenFont = new BitmapFont();
	private CharSequence damageText;
	private CharSequence regenText;
	public Texture emptyBarTexture = new Texture ("assets/EmptyBar.png");
	public Texture healthBarTexture = new Texture ("assets/RedBar.png");
	public Texture manaBarTexture = new Texture ("assets/BlueBar.png");
	
	//constructor for the player, it gets passed coordinates for the player 
	public Player(int x, int y, int X, int Y){
		x1 = x;
		y1 = y;
		x2 = X;
		y2 = Y;
		
		//sets up players initial stats and a couple other things
		setHealth(100);
		setMana(100);
		setAtk(15);
		setDef(15);
		directionFaced = "down";
		isDead = false;
		damageFont.setColor(Color.RED);
		
		//This is an ugly way of differentiating between the player and enemies, which extend the player
		if (this instanceof Enemy){
			
		}
		else{
			//this sets up the players sprite and animations
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
			equipmentWeight = 0;
			
//			in case an inventory system is later implemented
//			itemsInInventory = 0; 
//			for (int i = 0; i < 10; i++){
//				inventorySpaces[i] = false;
//			}
		}
		
	}
	
	//draws the player
	public void drawPlayer(SpriteBatch batch){
		character.draw(batch);
	}
	
	//draws the players health and mana bars at the top of the screen
	public void drawBars(SpriteBatch batch){
		batch.draw(emptyBarTexture, (float) this.x1 - 250, (float) this.y1 + 155, (float) (emptyBarTexture.getWidth() * .75), (float) (emptyBarTexture.getHeight() * .75), 0, 0, emptyBarTexture.getWidth(), emptyBarTexture.getHeight(), false, false);
		batch.draw(healthBarTexture, (float) this.x1 - 250, (float) this.y1 + 155, (float) (healthBarTexture.getWidth() * .75 * this.getHealth() * .01), (float) (healthBarTexture.getHeight() * .75), 0, 0, (int) (this.getHealth() * .01 * healthBarTexture.getWidth()), healthBarTexture.getHeight(), false, false);
		batch.draw(emptyBarTexture, (float) this.x1 + 95, (float) this.y1 + 155, (float) (emptyBarTexture.getWidth() * .75), (float) (emptyBarTexture.getHeight() * .75), 0, 0, emptyBarTexture.getWidth(), emptyBarTexture.getHeight(), false, false);
		batch.draw(manaBarTexture, (float) this.x1 + 95, (float) this.y1 + 155, (float) (manaBarTexture.getWidth() * .75 * this.getMana() * .01), (float) (manaBarTexture.getHeight() * .75), 0, 0, (int) (this.getMana() * .01 * manaBarTexture.getWidth()), manaBarTexture.getHeight(), false, false);
	}
	
	//handles player movement and animation
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
	
	//used when the player gets to a new floor, just resets the character sprite
	public void moveToNewFloor(){
		downFrame = 0;
		character.setRegion(charAnimation[0]);
	}
	
	//equips whatever item is passed
	public void equipItem(Item item, Floor floor){
		floor.itemLocations[(int) (item.itemSprite.getX() / TILE_SIZE)][(int) (item.itemSprite.getY() / TILE_SIZE)] = null;
		floor.itemsOnFloor.remove(item);
		
		if (item instanceof Weapon){
			if (equippedWeapon == null){
				equippedWeapon = item;
				setAtk(atk + item.strength);
			}
			else {
				unequipItem(equippedWeapon, floor);
				equippedWeapon = item;
				setAtk(atk + item.strength);
			}
			item.isEquipped = true;
			equipmentWeight += item.weight;
		}
		else if (item instanceof Armor){
			if (equippedArmor == null){
				equippedArmor = item;
				setDef(def + item.strength);
			}
			else {
				unequipItem(equippedArmor, floor);
				equippedArmor = item;
				setDef(def + item.strength);
			}
			item.isEquipped = true;
			equipmentWeight += item.weight;
		}
	}
	
	//unequips whatever item is currently equipped
	public void unequipItem(Item item, Floor floor){
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
			this.dropItem(item, floor);
		}
		
	}
	
	//uses a potion and applies its effect based on the type of the potion
	public void usePotion(Potion potion, Floor floor, SpriteBatch batch){
		floor.itemLocations[(int) (potion.itemSprite.getX() / TILE_SIZE)][(int) (potion.itemSprite.getY() / TILE_SIZE)] = null;
		floor.itemsOnFloor.remove(potion);
		
		if(potion.classification == Potion.HEALTH_POTION){
			regenFont.setColor(Color.GREEN);
			setHealth(getHealth() + 50);
			regenText = "+50";
			if (getHealth() > 100)
				setHealth(100);
		}
		else if (potion.classification == Potion.MANA_POTION){
			regenFont.setColor(Color.BLUE);
			setMana(getMana() + 50);
			regenText = "+50";
			if (getMana() > 100)
				setMana(100);
		}
		
		regenFont.draw(batch, regenText, this.x1 + 8, this.y2 + 16);
	}
	
	//picks up an item, won't be used unless an inventory system is implemented
	public void pickUpItem(Item item, Floor floor){
//		these are just commented out in case we implement an inventory display later
		
//		if (itemsInInventory < 10){
//			for (int i = 0; inventorySpaces[i] != false; i++){
//				inventory[i] = item;
//			}
//		}
//		floor.itemLocations[(int) (item.itemSprite.getX() / TILE_SIZE)][(int) (item.itemSprite.getY() / TILE_SIZE)] = null;
//		floor.itemsOnFloor.remove(item);
	}
	
	//drops an item on floor
	public void dropItem(Item item, Floor floor){
//		these are just commented out in case we implement an inventory display later
		
//      inventorySpace refers to the position in your inventory of the item being dropped i.e. the first item in your inventory is 0, next is 1, etc.
//		inventory[inventorySpace] = null;
//		inventorySpaces[inventorySpace] = false;
//		itemsInInventory--;
		if (item.isEquipped){
			if (item instanceof Weapon)
				equippedWeapon = null;
			else if (item instanceof Armor)
				equippedArmor = null;
		}
		floor.itemLocations[this.x1 / TILE_SIZE][this.y1 / TILE_SIZE] = item;
		item.itemSprite.setPosition(this.x1, this.y1);
		floor.itemsOnFloor.add(item);
	}
	
	//used to atttack, and draw combat animations
	public void attack(Player enemy, SpriteBatch batch, Floor floor){
		
		int damageDealt;
		
		if (equippedWeapon != null){
			this.equippedWeapon.itemSprite.setPosition(enemy.x1, enemy.y1);
			if (this.directionFaced == "up" || this.directionFaced == "right"){
				this.equippedWeapon.itemSprite.rotate(-45);
				for (int i = 1; i <= 6; i++){
					this.equippedWeapon.itemSprite.rotate(-15);
					this.equippedWeapon.itemSprite.draw(batch);
				}
				this.equippedWeapon.itemSprite.rotate(135);
			}
			else if (this.directionFaced == "down" || this.directionFaced == "left"){
				this.equippedWeapon.itemSprite.flip(true, false);
				this.equippedWeapon.itemSprite.rotate(45);
				for (int i = 1; i <= 6; i++){
					this.equippedWeapon.itemSprite.rotate(15);
					this.equippedWeapon.itemSprite.draw(batch);
				}
				this.equippedWeapon.itemSprite.rotate(-135);
				this.equippedWeapon.itemSprite.flip(true, false);
			}
		}
		damageDealt = this.getAtk() + 3 - damageRng.nextInt(7) - enemy.getDef(); 
		if (damageDealt < 0)
			damageDealt = 0;
		damageText = Integer.toString(-damageDealt);
		damageFont.draw(batch, damageText, enemy.x1 + 8, enemy.y2 + 16);
		enemy.setHealth(enemy.getHealth()-damageDealt);
		
		if (enemy instanceof Enemy){
			((Enemy) enemy).gotAttacked = true;
		}
		if (enemy.getHealth() <= 0){
			if (enemy instanceof Enemy)
				Game.score += floor.floorLevel * 10 + 100;
			enemy.die(floor);
		}
	}
	
	//casts fireball, draws flames to the screen and damages the first enemy it hits for 40 damage
	public void castFireball(Floor floor, SpriteBatch batch){
		if (this.getMana() >= 30){
			fireballSprite = new Sprite(fireballAnimation[0]);
			int animationRoll = 0;
//			Gdx.graphics.setContinuousRendering(true);
			if (directionFaced == "right"){
				fireballSprite.rotate(90);
				fireballSprite.setPosition(this.x1, this.y1);
//				while (floor.characterLocations[(int) (fireballSprite.getX() / TILE_SIZE)][this.y1 / TILE_SIZE] == null)
				while (floor.floorLayout[(int) ((fireballSprite.getX() + TILE_SIZE) / TILE_SIZE)][this.y1 / TILE_SIZE] == FLOOR_TILE && !(floor.characterLocations[(int) (fireballSprite.getX() / TILE_SIZE)][this.y1 / TILE_SIZE] instanceof Enemy)){
					//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					fireballSprite.setRegion(fireballAnimation[animationRoll++]);
					fireballSprite.translateX(8);
					fireballSprite.draw(batch);
					if (animationRoll > 3)
						animationRoll = 0;
				}
				
			//Gdx.graphics.setContinuousRendering(false);		
			}
			else if (directionFaced == "left"){
				fireballSprite.rotate(270);
				
			}
			else if (directionFaced == "up"){
				fireballSprite.rotate(180);
				
			}
			else if (directionFaced == "down"){
				
			}
			
			if (floor.characterLocations[(int) (fireballSprite.getX() / TILE_SIZE)][(int) (fireballSprite.getY() / TILE_SIZE)] instanceof Enemy){
				floor.characterLocations[(int) (fireballSprite.getX() / TILE_SIZE)][(int) (fireballSprite.getY() / TILE_SIZE)].setHealth(floor.characterLocations[(int) (fireballSprite.getX() / TILE_SIZE)][this.y1 / TILE_SIZE].getHealth() - 40);
				((Enemy) floor.characterLocations[(int) (fireballSprite.getX() / TILE_SIZE)][(int) (fireballSprite.getY() / TILE_SIZE)]).gotAttacked = true;
				damageText = "-40";
				damageFont.draw(batch, damageText, fireballSprite.getX() + 8, fireballSprite.getY() + 42);
			}
			
			this.setMana(this.getMana() - 30);
		}
	}
	
	//casts ice lance, draws ice beam to the screen and damages the first enemy it hits for 20 damage with a chance to slow them
	public void castIceLance(Floor floor, SpriteBatch batch){
		if (this.getMana() >= 30){
			iceLanceSprite = new Sprite(iceLanceAnimation[0]);
			int animationRoll = 0;
			//Add animations here
			if (directionFaced == "right"){
				
			}
			else if (directionFaced == "left"){
				
			}
			else if (directionFaced == "up"){
				
			}
			else if (directionFaced == "down"){
				
			}
			
			
			iceLanceSprite.setPosition(this.x1, this.y1);
			if (floor.characterLocations[(int) (iceLanceSprite.getX() / TILE_SIZE)][(int) (iceLanceSprite.getY() / TILE_SIZE)] instanceof Enemy){
				floor.characterLocations[(int) (iceLanceSprite.getX() / TILE_SIZE)][(int) (iceLanceSprite.getY() / TILE_SIZE)].setHealth(floor.characterLocations[(int) (iceLanceSprite.getX() / TILE_SIZE)][this.y1 / TILE_SIZE].getHealth() - 20);
				((Enemy) floor.characterLocations[(int) (iceLanceSprite.getX() / TILE_SIZE)][(int) (iceLanceSprite.getY() / TILE_SIZE)]).gotAttacked = true;
				damageText = "-20";
				damageFont.draw(batch, damageText, iceLanceSprite.getX() + 8, iceLanceSprite.getY() + 42);
				if (damageRng.nextInt(10) < 3){
					((Enemy) floor.characterLocations[(int) (iceLanceSprite.getX() / TILE_SIZE)][this.y1 / TILE_SIZE]).isSlowed = true;
					((Enemy) floor.characterLocations[(int) (iceLanceSprite.getX() / TILE_SIZE)][this.y1 / TILE_SIZE]).slowTurn = true;
					damageText = "Slowed";
					damageFont.setColor(Color.GREEN);
					damageFont.draw(batch, damageText, iceLanceSprite.getX() + 8, iceLanceSprite.getY() + 60);
					damageFont.setColor(Color.RED);
				}
			}
			
			this.setMana(this.getMana() - 30);
		}
	}
	
	//casts healing touch on the player, healing 30 HP
	public void castHealingTouch(SpriteBatch batch){
		if (this.getMana() >= 30){
			this.setHealth(this.getHealth() + 30);
			if (this.getHealth() > 100)
				this.setHealth(100);
			this.setMana(this.getMana() - 30);
			regenFont.setColor(Color.GREEN);
			regenText = "+30";
			regenFont.draw(batch, regenText, this.x1 + 4, this.y2 + 16);
		}
	}
	
	//sets up the TextureRegions for spell animations
	private void setUpSpells(){
		for(int i = 0; i < 4; i++){
			fireballAnimation[i] = new TextureRegion(fireballTexture, i * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
			iceLanceAnimation[i] = new TextureRegion(fireballTexture, i * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
		}
	}
	
	//changes the direction the player is facing if next to an enemy and hits an arrow key
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
	
	//called when the players HP drops to 0 or below, brings up a game over screen and asks the player to enter intitials for the high score table
	public void die(Floor floor){
		
		isDead = true;
		
		try {
			new GameOver();
			Display.destroy();
			Game.stopMusic();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//these are setters and getters for the players stats
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
