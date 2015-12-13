package game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Potion extends Item {

	public static int HEALTH_POTION = 0;
	public static int MANA_POTION = 1;
	private Texture healthPotionTexture = new Texture("assets/PotionShortRuby.PNG");
	private Texture manaPotionTexture = new Texture("assets/PotionShortBlue.PNG");
	
	public Potion(Floor floor, int[] coordinates){
		super();
		
		classification = itemRng.nextInt(2);
		
		if (classification == HEALTH_POTION){
			itemSprite = new Sprite(healthPotionTexture);
		}
		else if (classification == MANA_POTION){
			itemSprite = new Sprite(manaPotionTexture);
		}
		
		itemSprite.setPosition(coordinates[0], coordinates[1]);
		floor.itemLocations[(int) (itemSprite.getX() / TILE_SIZE)][(int) (itemSprite.getY() / TILE_SIZE)] = this;
	}
	
}
