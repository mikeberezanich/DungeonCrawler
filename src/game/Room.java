package game;


public class Room{

	//These hold grid coordinates for each corner of the room
	public int x1;
	public int x2;
	public int y1;
	public int y2;
	private int tileSize = 32;

	//Width and height of room in terms of grid
	public int w;
	public int h;
	
	Room(int x, int y, int w, int h){
		
		x1 = x * tileSize;
		x2 = x * tileSize + w * tileSize;
		y1 = y * tileSize;
		y2 = y * tileSize + h * tileSize;
		this.w = w;
		this.h = h;
		
	}
	
	public boolean intersects(Room room1, Room room2){
		
		return (room1.x1 <= room2.x2 && room1.x2 >= room2.x1 &&
				room1.y1 <= room2.y2 && room1.y2 >= room2.y1);
		
	}
	
	
}
