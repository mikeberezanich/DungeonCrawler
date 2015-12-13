package game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Weapon extends Item {
	
	public static int SWORD_CLASS = 0;
	public static int SPEAR_CLASS = 1;
	public static int AXE_CLASS = 2;
	public TextureRegion swordSprites = new TextureRegion(itemSpritesheet, 0, 0, 6 * TILE_SIZE, TILE_SIZE);
	public TextureRegion spearSprites = new TextureRegion(itemSpritesheet, 0, TILE_SIZE, 6 * TILE_SIZE, 2 * TILE_SIZE);
	public TextureRegion axeSprites =  new TextureRegion(itemSpritesheet, 0, 2 * TILE_SIZE, 6 * TILE_SIZE, 3 * TILE_SIZE);
	
	public Weapon(Floor floor, int[] coordinates) {
		super();
		
		classification = itemRng.nextInt(3);
		
		if (classification == SWORD_CLASS){
			strength = itemRng.nextInt(floor.floorLevel + 1) + 5; //Fix this up later
			weight = 2;
			itemSprite = new Sprite(new TextureRegion(swordSprites, itemRng.nextInt(6) * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE));	
		}
		else if (classification == SPEAR_CLASS){
			strength = itemRng.nextInt(floor.floorLevel + 1) + 3; //Fix this up later
			weight = 1;
			itemSprite = new Sprite(new TextureRegion(spearSprites, itemRng.nextInt(6) * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE));
		}
		else if (classification == AXE_CLASS){
			strength = itemRng.nextInt(floor.floorLevel + 1) + 7; //Fix this up later
			weight = 3;
			itemSprite = new Sprite(new TextureRegion(axeSprites, itemRng.nextInt(6) * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE));
		}
		
		itemSprite.setPosition(coordinates[0], coordinates[1]);
		floor.itemLocations[(int) (itemSprite.getX() / TILE_SIZE)][(int) (itemSprite.getY() / TILE_SIZE)] = this;
	}


}
