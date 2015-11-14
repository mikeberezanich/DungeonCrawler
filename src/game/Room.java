package game;


public class Room{

	//These hold grid coordinates for each corner of the room
	public int x1;
	public int x2;
	public int y1;
	public int y2;
	public int tileSize = 32;
	public int centerX;
	public int centerY;

	//Width and height of room in terms of grid
	public int w;
	public int h;
	
	public Room(int x, int y, int w, int h){
		
		x1 = x * tileSize;
		x2 = x * tileSize + w * tileSize;
		y1 = y * tileSize;
		y2 = y * tileSize + h * tileSize;
		this.w = w;
		this.h = h;
		centerX = (2 * x + w) / 2 * tileSize;
		centerY = (2 * y + h) / 2 * tileSize;
		
	}
	
	public boolean intersects(Room room1, Room room2){
		
		//changed to requiring at least 1 tile between rooms, I figured this might make graphics situations slightly easier
		return (room1.x1 <= room2.x2 + tileSize && room1.x2 + tileSize >= room2.x1 &&
				room1.y1 <= room2.y2 + tileSize && room1.y2 + tileSize >= room2.y1);
		
	}
	
	
}
