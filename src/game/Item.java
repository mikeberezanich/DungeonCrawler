package game;

public class Item {

	private String itemType;
	private int atkIncrease;
	private int defIncrease;
	
	public Item(String type){
		itemType = type;
		
		if (itemType == "weapon"){
			atkIncrease = 0;
		}
		else if (itemType == "armor"){
			defIncrease = 0;
		}
		else if (itemType == "potion"){
			
		}
	}
	
	public void equipWeapon(Player player){
		
	}
	
	public void equipArmor(Player player){
		
	}
	
	public void usePotion(Player player){
		
	}
	
	public String getItemType(){
		return itemType;
	}
	
}
