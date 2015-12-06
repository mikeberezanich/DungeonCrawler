package game;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Item {
	
	public int strength;
	protected Random strengthRNG = new Random();
	public boolean isEquipped;
	public Sprite itemSprite;
	public int spriteNumber;
	
	public Item(){
		isEquipped = false;
	}
	
}
