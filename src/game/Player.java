package game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
	
	//lowercase x and y are x1 and y1 and capital X and Y are x2 and y2
	public Player(int x, int y, int X, int Y){
		x1 = x;
		y1 = y;
		x2 = X;
		y2 = Y;
		itemsInInventory = 0;
		for (int i = 0; i < 10; i++){
			inventorySpaces[i] = false;
		}
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
		}
		equipmentWeight += item.weight;
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
		
	}
	
	public void pickUpItem(Item item){
		if (itemsInInventory < 10){
			for (int i = 0; inventorySpaces[i] != false; i++){
				inventory[i] = item;
			}
		}
	}
	
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
		floor.itemLocations[(int) (item.itemSprite.getX() / TILE_SIZE)][(int) (item.itemSprite.getY() / TILE_SIZE)] = item;
	}
	
	public void attack(Player enemy, SpriteBatch batch){
		if (equippedWeapon != null){
			this.equippedWeapon.itemSprite.setPosition(this.x1, this.y2);
			this.equippedWeapon.itemSprite.draw(batch);
			this.equippedWeapon.itemSprite.rotate(90);
		}
		enemy.setHealth(enemy.getHealth()-this.getAtk()); //Fix this up later
		if (enemy instanceof Enemy)
			((Enemy) enemy).gotAttacked = true;
	}
	
	public void castFireball(){
		if (this.getMana() >= 30){
			
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
