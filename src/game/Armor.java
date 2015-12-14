package game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Armor extends Item {
	
	public static int LIGHT_ARMOR = 0;
	public static int MEDIUM_ARMOR = 1;
	public static int HEAVY_ARMOR = 2;
	private Texture armorSpritesheet = new Texture("assets/armor.png");
	public TextureRegion lightArmorSprites = new TextureRegion(armorSpritesheet, 0, 0, 3 * TILE_SIZE, TILE_SIZE);
	public TextureRegion mediumArmorSprites = new TextureRegion(armorSpritesheet, 0, TILE_SIZE, 3 * TILE_SIZE, 2 * TILE_SIZE);
	public TextureRegion heavyArmorSprites =  new TextureRegion(armorSpritesheet, 0, 2 * TILE_SIZE, 3 * TILE_SIZE, 3 * TILE_SIZE);
	
	public Armor(Floor floor, int[] coordinates){
		super();

		classification = itemRng.nextInt(3);
		
		if (classification == LIGHT_ARMOR){
			strength = itemRng.nextInt(floor.floorLevel + 1) + 3; //Fix this up later
			weight = 1;
			itemSprite = new Sprite(new TextureRegion(lightArmorSprites, itemRng.nextInt(3) * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE));
		}
		else if (classification == MEDIUM_ARMOR){
			strength = itemRng.nextInt(floor.floorLevel + 1) + 5; //Fix this up later
			weight = 2;
			itemSprite = new Sprite(new TextureRegion(mediumArmorSprites, itemRng.nextInt(3) * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE));
		}
		else if (classification == HEAVY_ARMOR){
			strength = itemRng.nextInt(floor.floorLevel + 1) + 7; //Fix this up later
			weight = 3;
			itemSprite = new Sprite(new TextureRegion(heavyArmorSprites, itemRng.nextInt(3) * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE));
		}
		
		itemSprite.setPosition(coordinates[0], coordinates[1]);
		floor.itemLocations[(int) (itemSprite.getX() / TILE_SIZE)][(int) (itemSprite.getY() / TILE_SIZE)] = this;
	}
	
}
