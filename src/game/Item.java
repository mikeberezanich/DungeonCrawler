package game;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Item {
	
	public int strength;
	public int classification;
	protected Random itemRng = new Random();
	public boolean isEquipped;
	public Sprite itemSprite;
	public int spriteNumber;
	public int weight;
	protected Texture itemSpritesheet = new Texture("assets/items.png");
	public static int TILE_SIZE = 32;
	
	public Item(int[] coordinates){
		isEquipped = false;
	}
	
}
