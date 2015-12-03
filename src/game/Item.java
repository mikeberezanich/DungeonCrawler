package game;

public class Item {

	private String itemType;
	
	public Item(String type){
		itemType = type;
		
		if (itemType == "weapon"){
			
		}
		else if (itemType == "potion"){
			
		}
		else if (itemType == "armor"){
			
		}
	}
	
	public String getItemType(){
		return itemType;
	}
	
}
