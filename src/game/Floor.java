package game;

import java.util.Random;
import java.util.Vector;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Floor{

	public int[][] floorLayout = new int[32][24];
	public Vector<Room> rooms;
	public static final int TILE_SIZE = 32;
	public static final int FLOOR_TILE = 15;
	public static final int EMPTY_TILE = 16;
	public static final int VERT_CORR_TILE = 20;
	public static final int VERT_CORR_TOP_DOOR_TILE = 21;
	public static final int VERT_CORR_BOTTOM_DOOR_TILE = 22;
	public static final int HORIZ_CORR_TILE = 23;
	public static final int HORIZ_CORR_RIGHT_DOOR_TILE = 24;
	public static final int HORIZ_CORR_LEFT_DOOR_TILE = 25;
	public static final int BOTH_CORR_TILE = 26;
	public static final int TOP_CENTER_ROOM_TILE = 27;
	public static final int BOTTOM_CENTER_ROOM_TILE = 28;
	public static final int STAIR_TILE = 30;
	private Random rng = new Random();
	private int roomRng;
	private int cornerRng;
	private Texture floorTileset = new Texture("assets/WallSet.png");
	private TextureRegion[] floorTiles = new TextureRegion[20];
	private Texture stairs = new Texture("assets/Stairs.png");
	public Item[][] itemLocations = new Item[32][24];
	public Item[] itemsOnFloor;
	public int numItemsOnFloor;
	public Enemy[] enemiesOnFloor;
	public int numEnemiesOnFloor;
	public Player[][] characterLocations = new Player[32][24];
	public int floorLevel;
	
	public Floor(int level){
	
		floorLevel = level;
		//floor array is now represented by numbers 0-30 where 16 equals empty space between rooms
		//20-28 represent different corridor pieces that are used later for rendering
		//0-17 refer to tile numbers, reference WallSet (with tile mapping).xcf to find tile numbers
		//30 represents the stairs
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				floorLayout[i][j] = EMPTY_TILE;
			}
		}
		
		numItemsOnFloor = rng.nextInt(3) + 2;
		itemsOnFloor = new Item[numItemsOnFloor];
		numEnemiesOnFloor = rng.nextInt(3) + 2;
		enemiesOnFloor = new Enemy[numEnemiesOnFloor];
		
		int k = 0;
		//for instantiating our tiles array with each tile of the tileset
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 4; j++){
				floorTiles[k] = new TextureRegion(floorTileset, j * 16, i * 16, 16, 16);
				k++;
			}
		}
		
		placeRooms();
		placeItems();
		placeEnemies();
		
		roomRng = rng.nextInt(this.rooms.size());
		cornerRng = rng.nextInt(4);
		if (cornerRng == 0)
			floorLayout[this.rooms.get(roomRng).x1/TILE_SIZE + 1][this.rooms.get(roomRng).y2/TILE_SIZE - 1] = STAIR_TILE;
		else if (cornerRng == 1)
			floorLayout[this.rooms.get(roomRng).x2/TILE_SIZE - 1][this.rooms.get(roomRng).y2/TILE_SIZE - 1] = STAIR_TILE;
		else if (cornerRng == 2)
			floorLayout[this.rooms.get(roomRng).x2/TILE_SIZE - 1][this.rooms.get(roomRng).y1/TILE_SIZE + 1] = STAIR_TILE;
		else if (cornerRng == 3)
			floorLayout[this.rooms.get(roomRng).x1/TILE_SIZE + 1][this.rooms.get(roomRng).y1/TILE_SIZE + 1] = STAIR_TILE;
		
	}
	
	//places rooms on floor
	public void placeRooms() {
		
		rooms = new Vector<Room>();
		boolean failed = false;
		int newRoomCenterX, newRoomCenterY, //these are all used for corridor generation
			prevRoomCenterX = 0, prevRoomCenterY = 0;
		
		for (int i = 0; i < 100; i++){ //generally, as you increase i's limit, the density of rooms per floor will increase here
			int w = 4 + rng.nextInt(5);
			int h = 4 + rng.nextInt(5); 
			int x = rng.nextInt(32 - w - 1) + 1;
			int y = rng.nextInt(24 - h - 1) + 1;
			Room room = new Room(x, y, w, h);
			failed = false;
			for (int j = 0; j < rooms.size(); j++){
				if (room.intersects(room, rooms.get(j))) //check if the room intersects with any rooms made thus far
					failed = true;
				}
			if (!failed){ //if the room above doesn't intercept any rooms, actually make the room
				createRoom(room, rooms);
				if (rooms.size() > 0){
					prevRoomCenterX = rooms.lastElement().centerX;
					prevRoomCenterY = rooms.lastElement().centerY;
				}
				rooms.add(room);
				newRoomCenterX = room.centerX;
				newRoomCenterY = room.centerY;
				if (rooms.size() > 1){
					carveCorridor(prevRoomCenterX, newRoomCenterX, prevRoomCenterY, newRoomCenterY);
				}
			}
		}
		
		fixUpArray();
		
	}
	
	//carves out room in floor array
	private void createRoom(Room room, Vector<Room> rooms){
		for (int i = room.x1/TILE_SIZE; i <= room.x2/TILE_SIZE; i++){
			for (int j = room.y1/TILE_SIZE; j <= room.y2/TILE_SIZE; j++){
				if (i == room.x1/TILE_SIZE && j == room.y1/TILE_SIZE && floorLayout[i][j] == EMPTY_TILE)
					floorLayout[i][j] = 13; //creates bottom left corner
				else if (i == room.x2/TILE_SIZE && j == room.y1/TILE_SIZE && floorLayout[i][j] == EMPTY_TILE)
					floorLayout[i][j] = 14; //creates bottom right corner
				else if (i == room.x1/TILE_SIZE && floorLayout[i][j] == EMPTY_TILE)
					floorLayout[i][j] = 10; //creates left wall
				else if (i == room.x2/TILE_SIZE && floorLayout[i][j] == EMPTY_TILE)
					floorLayout[i][j] = 11; //creates right wall
				else if (j == room.y2/TILE_SIZE && i == room.centerX/TILE_SIZE && (floorLayout[i][j] == EMPTY_TILE || floorLayout[i][j] == VERT_CORR_TILE))
					floorLayout[i][j] = TOP_CENTER_ROOM_TILE; //center of top wall, used for corridors later
				else if (j == room.y1/TILE_SIZE && i == room.centerX/TILE_SIZE && (floorLayout[i][j] == EMPTY_TILE || floorLayout[i][j] == VERT_CORR_TILE))
					floorLayout[i][j] = BOTTOM_CENTER_ROOM_TILE; //center of bottom wall, used for corridors later
				else if ((j == room.y1/TILE_SIZE || j == room.y2/TILE_SIZE) && floorLayout[i][j] == EMPTY_TILE)
					floorLayout[i][j] = rng.nextInt(7); //creates bottom and top walls
				else if (floorLayout[i][j] == HORIZ_CORR_TILE || floorLayout[i][j] == VERT_CORR_TILE || floorLayout[i][j] == BOTH_CORR_TILE){
					//Do nothing
				}
				else
					floorLayout[i][j] = FLOOR_TILE;
			}
		}
	}
	
	//carves out corridor in floor array
	private void carveCorridor(int prevX, int newX, int prevY, int newY){
		
		if (prevX < newX && prevY < newY){
			int k = prevY / TILE_SIZE;
			for (int i = prevX / TILE_SIZE; i <= newX / TILE_SIZE; i++){
				if (floorLayout[i][k] == EMPTY_TILE)
					floorLayout[i][k] = HORIZ_CORR_TILE;
				else if (floorLayout[i][k] == 11)
					floorLayout[i][k] = HORIZ_CORR_RIGHT_DOOR_TILE;
				else if (floorLayout[i][k] == 10)
					floorLayout[i][k] = HORIZ_CORR_LEFT_DOOR_TILE;
				else if (floorLayout[i][k] == VERT_CORR_TILE || floorLayout[i][k] == VERT_CORR_TOP_DOOR_TILE || floorLayout[i][k] == VERT_CORR_BOTTOM_DOOR_TILE || floorLayout[i][k] == BOTH_CORR_TILE || i == newX / TILE_SIZE) 
					//if overlapping with any verical corridor or at end of carving, make a both corridor tile 
					floorLayout[i][k] = BOTH_CORR_TILE;
				else if (floorLayout[i][k] == HORIZ_CORR_RIGHT_DOOR_TILE)
					floorLayout[i][k] = BOTH_CORR_TILE;
				else if (floorLayout[i][k] == HORIZ_CORR_LEFT_DOOR_TILE)
					floorLayout[i][k] = HORIZ_CORR_LEFT_DOOR_TILE;
				else
					floorLayout[i][k] = HORIZ_CORR_TILE;
			}
			k = newX / TILE_SIZE;
			for (int j = prevY / TILE_SIZE; j <= newY / TILE_SIZE; j++){
				if (floorLayout[k][j] == EMPTY_TILE)
					floorLayout[k][j] = VERT_CORR_TILE;
				else if (floorLayout[k][j] == TOP_CENTER_ROOM_TILE)
					floorLayout[k][j] = VERT_CORR_TOP_DOOR_TILE;
				else if (floorLayout[k][j] == BOTTOM_CENTER_ROOM_TILE)
					floorLayout[k][j] = VERT_CORR_BOTTOM_DOOR_TILE;
				else if (floorLayout[k][j] == HORIZ_CORR_TILE || floorLayout[k][j] == HORIZ_CORR_RIGHT_DOOR_TILE || floorLayout[k][j] == HORIZ_CORR_LEFT_DOOR_TILE || floorLayout[k][j] == BOTH_CORR_TILE || j == newY / TILE_SIZE) 
					//if overlapping with any horizontal corridor or at end of carving, make a both corridor tile 
					floorLayout[k][j] = BOTH_CORR_TILE;
				else if (floorLayout[k][j] == VERT_CORR_TOP_DOOR_TILE)
					floorLayout[k][j] = VERT_CORR_TOP_DOOR_TILE;
				else if (floorLayout[k][j] == VERT_CORR_BOTTOM_DOOR_TILE)
					floorLayout[k][j] = VERT_CORR_BOTTOM_DOOR_TILE;
				else
					floorLayout[k][j] = VERT_CORR_TILE;
			}
		}
		else if (prevX < newX && prevY > newY){
			int k = prevY / TILE_SIZE;
			for (int i = prevX / TILE_SIZE; i <= newX / TILE_SIZE; i++){
				if (floorLayout[i][k] == EMPTY_TILE)
					floorLayout[i][k] = HORIZ_CORR_TILE;
				else if (floorLayout[i][k] == 11)
					floorLayout[i][k] = HORIZ_CORR_RIGHT_DOOR_TILE;
				else if (floorLayout[i][k] == 10)
					floorLayout[i][k] = HORIZ_CORR_LEFT_DOOR_TILE;
				else if (floorLayout[i][k] == VERT_CORR_TILE || floorLayout[i][k] == VERT_CORR_TOP_DOOR_TILE || floorLayout[i][k] == VERT_CORR_BOTTOM_DOOR_TILE || floorLayout[i][k] == BOTH_CORR_TILE || i == newX / TILE_SIZE) 
					//if overlapping with any verical corridor or at end of carving, make a both corridor tile 
					floorLayout[i][k] = BOTH_CORR_TILE;
				else if (floorLayout[i][k] == HORIZ_CORR_RIGHT_DOOR_TILE)
					floorLayout[i][k] = BOTH_CORR_TILE;
				else if (floorLayout[i][k] == HORIZ_CORR_LEFT_DOOR_TILE)
					floorLayout[i][k] = HORIZ_CORR_LEFT_DOOR_TILE;
				else
					floorLayout[i][k] = HORIZ_CORR_TILE;
			}
			k = newX / TILE_SIZE;
			for (int j = newY / TILE_SIZE; j <= prevY / TILE_SIZE; j++){
				if (floorLayout[k][j] == EMPTY_TILE)
					floorLayout[k][j] = VERT_CORR_TILE;
				else if (floorLayout[k][j] == TOP_CENTER_ROOM_TILE)
					floorLayout[k][j] = VERT_CORR_TOP_DOOR_TILE;
				else if (floorLayout[k][j] == BOTTOM_CENTER_ROOM_TILE)
					floorLayout[k][j] = VERT_CORR_BOTTOM_DOOR_TILE;
				else if (floorLayout[k][j] == HORIZ_CORR_TILE || floorLayout[k][j] == HORIZ_CORR_RIGHT_DOOR_TILE || floorLayout[k][j] == HORIZ_CORR_LEFT_DOOR_TILE || floorLayout[k][j] == BOTH_CORR_TILE || j == newY / TILE_SIZE) 
					//if overlapping with any horizontal corridor or at end of carving, make a both corridor tile 
					floorLayout[k][j] = BOTH_CORR_TILE;
				else if (floorLayout[k][j] == VERT_CORR_TOP_DOOR_TILE)
					floorLayout[k][j] = VERT_CORR_TOP_DOOR_TILE;
				else if (floorLayout[k][j] == VERT_CORR_BOTTOM_DOOR_TILE)
					floorLayout[k][j] = VERT_CORR_BOTTOM_DOOR_TILE;
				else
					floorLayout[k][j] = VERT_CORR_TILE;
			}
		}
		else if (prevX > newX && prevY < newY){
			int k = prevY / TILE_SIZE;
			for (int i = prevX / TILE_SIZE; i >= newX / TILE_SIZE; i--){
				if (floorLayout[i][k] == EMPTY_TILE)
					floorLayout[i][k] = HORIZ_CORR_TILE;
				else if (floorLayout[i][k] == 11)
					floorLayout[i][k] = HORIZ_CORR_RIGHT_DOOR_TILE;
				else if (floorLayout[i][k] == 10)
					floorLayout[i][k] = HORIZ_CORR_LEFT_DOOR_TILE;
				else if (floorLayout[i][k] == VERT_CORR_TILE || floorLayout[i][k] == VERT_CORR_TOP_DOOR_TILE || floorLayout[i][k] == VERT_CORR_BOTTOM_DOOR_TILE || floorLayout[i][k] == BOTH_CORR_TILE || i == newX / TILE_SIZE) 
					//if overlapping with any verical corridor or at end of carving, make a both corridor tile 
					floorLayout[i][k] = BOTH_CORR_TILE;
				else if (floorLayout[i][k] == HORIZ_CORR_RIGHT_DOOR_TILE)
					floorLayout[i][k] = BOTH_CORR_TILE;
				else if (floorLayout[i][k] == HORIZ_CORR_LEFT_DOOR_TILE)
					floorLayout[i][k] = HORIZ_CORR_LEFT_DOOR_TILE;
				else
					floorLayout[i][k] = HORIZ_CORR_TILE;
			}
			k = newX / TILE_SIZE;
			for (int j = prevY / TILE_SIZE; j <= newY / TILE_SIZE; j++){
				if (floorLayout[k][j] == EMPTY_TILE)
					floorLayout[k][j] = VERT_CORR_TILE;
				else if (floorLayout[k][j] == TOP_CENTER_ROOM_TILE)
					floorLayout[k][j] = VERT_CORR_TOP_DOOR_TILE;
				else if (floorLayout[k][j] == BOTTOM_CENTER_ROOM_TILE)
					floorLayout[k][j] = VERT_CORR_BOTTOM_DOOR_TILE;
				else if (floorLayout[k][j] == 23 || floorLayout[k][j] == HORIZ_CORR_RIGHT_DOOR_TILE || floorLayout[k][j] == HORIZ_CORR_LEFT_DOOR_TILE || floorLayout[k][j] == BOTH_CORR_TILE || j == newY / TILE_SIZE) 
					//if overlapping with any horizontal corridor or at end of carving, make a both corridor tile 
					floorLayout[k][j] = BOTH_CORR_TILE;
				else if (floorLayout[k][j] == VERT_CORR_TOP_DOOR_TILE)
					floorLayout[k][j] = VERT_CORR_TOP_DOOR_TILE;
				else if (floorLayout[k][j] == VERT_CORR_BOTTOM_DOOR_TILE)
					floorLayout[k][j] = VERT_CORR_BOTTOM_DOOR_TILE;
				else
					floorLayout[k][j] = VERT_CORR_TILE;
			}
		}
		else {
			int k = newY / TILE_SIZE;
			for (int i = newX / TILE_SIZE; i <= prevX / TILE_SIZE; i++){
				if (floorLayout[i][k] == EMPTY_TILE)
					floorLayout[i][k] = HORIZ_CORR_TILE;
				else if (floorLayout[i][k] == 11)
					floorLayout[i][k] = HORIZ_CORR_RIGHT_DOOR_TILE;
				else if (floorLayout[i][k] == 10)
					floorLayout[i][k] = HORIZ_CORR_LEFT_DOOR_TILE;
				else if (floorLayout[i][k] == VERT_CORR_TILE || floorLayout[i][k] == VERT_CORR_TOP_DOOR_TILE || floorLayout[i][k] == VERT_CORR_BOTTOM_DOOR_TILE || floorLayout[i][k] == BOTH_CORR_TILE || i == newX / TILE_SIZE) 
					//if overlapping with any verical corridor or at end of carving, make a both corridor tile 
					floorLayout[i][k] = BOTH_CORR_TILE;
				else if (floorLayout[i][k] == HORIZ_CORR_RIGHT_DOOR_TILE)
					floorLayout[i][k] = BOTH_CORR_TILE;
				else if (floorLayout[i][k] == HORIZ_CORR_LEFT_DOOR_TILE)
					floorLayout[i][k] = HORIZ_CORR_LEFT_DOOR_TILE;
				else
					floorLayout[i][k] = HORIZ_CORR_TILE;
			}
			k = prevX / TILE_SIZE;
			for (int j = newY / TILE_SIZE; j <= prevY / TILE_SIZE; j++){
				if (floorLayout[k][j] == EMPTY_TILE)
					floorLayout[k][j] = VERT_CORR_TILE;
				else if (floorLayout[k][j] == TOP_CENTER_ROOM_TILE)
					floorLayout[k][j] = VERT_CORR_TOP_DOOR_TILE;
				else if (floorLayout[k][j] == BOTTOM_CENTER_ROOM_TILE)
					floorLayout[k][j] = VERT_CORR_BOTTOM_DOOR_TILE;
				else if (floorLayout[k][j] == HORIZ_CORR_TILE || floorLayout[k][j] == HORIZ_CORR_RIGHT_DOOR_TILE || floorLayout[k][j] == HORIZ_CORR_LEFT_DOOR_TILE || floorLayout[k][j] == BOTH_CORR_TILE || j == newY / TILE_SIZE) 
					//if overlapping with any horizontal corridor or at end of carving, make a both corridor tile 
					floorLayout[k][j] = BOTH_CORR_TILE;
				else if (floorLayout[k][j] == VERT_CORR_TOP_DOOR_TILE)
					floorLayout[k][j] = VERT_CORR_TOP_DOOR_TILE;
				else if (floorLayout[k][j] == VERT_CORR_BOTTOM_DOOR_TILE)
					floorLayout[k][j] = VERT_CORR_BOTTOM_DOOR_TILE;
				else
					floorLayout[k][j] = VERT_CORR_TILE;
			}
		}
	}
	
	//to fix up the array for rendering
	public void fixUpArray(){
		
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				
				//don't even bother trying to make sense of these, these are just all the different situations being accounted for
				if (floorLayout[i][j] == BOTH_CORR_TILE){
					if (floorLayout[i][j+1] == EMPTY_TILE)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == EMPTY_TILE)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i+1][j] == EMPTY_TILE)
						floorLayout[i+1][j] = 11;
					if (floorLayout[i-1][j] == EMPTY_TILE)
						floorLayout[i-1][j] = 10;
					if (floorLayout[i+1][j] == 11 && floorLayout[i][j+1] < 7)
						floorLayout[i+1][j+1] = 11; //this doesn't seem to work for some reason
					if (floorLayout[i+1][j] == 11 && floorLayout[i][j-1] == 8 && floorLayout[i+1][j-1] != 11)
						floorLayout[i+1][j+1] = 14;
					if (floorLayout[i+1][j] == 11 && floorLayout[i][j-1] < 7 && floorLayout[i+1][j-1] != 11)
						floorLayout[i+1][j-1] = 14;
					if (floorLayout[i-1][j] == 10 && floorLayout[i][j-1] < 8)
						floorLayout[i-1][j-1] = 13; //this doesn't seem to work for some reason
					if (floorLayout[i][j-1] == 10)
						floorLayout[i][j-1] = 7;
					if (floorLayout[i-1][j-1] < 7 && floorLayout[i-1][j-2] == 10)
						floorLayout[i-1][j-1] = 7;
					if (floorLayout[i+1][j-1] < 7 && floorLayout[i+1][j-2] == 11)
						floorLayout[i+1][j-1] = 8;
					if (floorLayout[i+1][j-1] < 7 && floorLayout[i+1][j-2] == 10)
						floorLayout[i+1][j-1] = 8;
					if (floorLayout[i+1][j-1] < 7 && floorLayout[i+1][j-2] == 14)
						floorLayout[i+1][j-1] = 8;
					if (floorLayout[i+1][j] < 7 && floorLayout[i+1][j-1] == 11)
						floorLayout[i+1][j] = 8;
					if (floorLayout[i+1][j] == 14)
						floorLayout[i+1][j] = 11;
					if (floorLayout[i][j-1] == 11)
						floorLayout[i][j-1] = 8;
					if (floorLayout[i][j-1] == 14)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == 11)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i-1][j-1] < 7 && floorLayout[i-1][j-2] == 11)
						floorLayout[i-1][j-1] = 7;
					if (floorLayout[i-1][j-1] < 7 && floorLayout[i-1][j-2] == 14)
						floorLayout[i-1][j-1] = 7;
					if (floorLayout[i+1][j] < 7 && floorLayout[i][j-1] < 7){
						floorLayout[i+1][j] = 8;
						floorLayout[i+1][j-1] = 14;
					}
					if (floorLayout[i-1][j] < 7 && floorLayout[i][j-1] < 7){
						floorLayout[i-1][j] = 7;
						floorLayout[i-1][j-1] = 13;
					}
					if (floorLayout[i+1][j] < 7 && floorLayout[i][j+1] < 7)
						floorLayout[i+1][j+1] = 11;
					if (floorLayout[i-1][j] == 11)
						floorLayout[i-1][j] = 12;
					if (floorLayout[i+1][j] == 10)
						floorLayout[i+1][j] = 12;
					if (floorLayout[i-1][j-1] == 8 && floorLayout[i-1][j-2] == EMPTY_TILE){
						floorLayout[i-1][j-1] = 9;
						floorLayout[i-1][j-2] = 12;
					}
					if (floorLayout[i+1][j] == 13)
						floorLayout[i+1][j] = 17;
					if (floorLayout[i-1][j] < 7 && (floorLayout[i-1][j-1] == 10 || floorLayout[i-1][j-1] < 7))
						floorLayout[i-1][j] = 7;	
					if (floorLayout[i-1][j-1] < 7 && floorLayout[i-1][j-2] < 7)
						floorLayout[i-1][j-1] = 7;
					if (floorLayout[i+1][j-1] < 7 && floorLayout[i+1][j-2] < 7)
						floorLayout[i+1][j-1] = 8;
					if (floorLayout[i-1][j-1] == 8 && floorLayout[i-1][j-2] == 12)
						floorLayout[i-1][j-1] = 9;
					
				}
				else if (floorLayout[i][j] == VERT_CORR_TILE){
					if (floorLayout[i-1][j] == EMPTY_TILE)
						floorLayout[i-1][j] = 10;
					if (floorLayout[i+1][j] == EMPTY_TILE)
						floorLayout[i+1][j] = 11;
					if (floorLayout[i-1][j] == 11)
						floorLayout[i-1][j] = 12;
					if (floorLayout[i+1][j] == 10)
						if (floorLayout[i+1][j+1] == FLOOR_TILE || floorLayout[i+1][j+1] == HORIZ_CORR_TILE || floorLayout[i+1][j+1] == HORIZ_CORR_LEFT_DOOR_TILE)
							floorLayout[i+1][j] = rng.nextInt(7);
						else
							floorLayout[i+1][j] = 12;
					if (floorLayout[i-1][j] == 13)
						floorLayout[i-1][j] = 10;
					if (floorLayout[i+1][j] == 14)
						floorLayout[i+1][j] = 11;
					if (floorLayout[i-1][j] == 14)
						floorLayout[i-1][j] = EMPTY_TILE;
					if (floorLayout[i+1][j] == 13)
						floorLayout[i+1][j] = 17;
					if (floorLayout[i-1][j] == 7)
						floorLayout[i-1][j] = 9;
					if (floorLayout[i+1][j] == 8)
						floorLayout[i+1][j] = 12;
					if (floorLayout[i+1][j] == 7)
						floorLayout[i+1][j] = 7;
					if (floorLayout[i+1][j] < 7 && floorLayout[i+1][j-1] < 7)
						floorLayout[i+1][j] = 8;
					if (floorLayout[i-1][j] < 7 && floorLayout[i-1][j-1] < 7)
						floorLayout[i-1][j] = 7;
					if (floorLayout[i+1][j] < 7 && floorLayout[i+1][j-1] == 11)
						floorLayout[i+1][j] = 8;
					if (floorLayout[i-1][j] < 7 && floorLayout[i-1][j-1] == 10)
						floorLayout[i-1][j] = 7;
					if (floorLayout[i+1][j] == VERT_CORR_BOTTOM_DOOR_TILE && floorLayout[i+1][j-1] == 11)
						floorLayout[i+1][j] = 8;
					if (floorLayout[i-1][j] == VERT_CORR_BOTTOM_DOOR_TILE && floorLayout[i-1][j-1] == 10)
						floorLayout[i-1][j] = 7;
					if (floorLayout[i-1][j] == 8 && floorLayout[i-1][j-1] == EMPTY_TILE){
						floorLayout[i-1][j] = 9;
						floorLayout[i-1][j-1] = 12;
					}
					if (floorLayout[i+1][j] == 8 && (floorLayout[i+2][j] == VERT_CORR_TILE || floorLayout[i+2][j] == VERT_CORR_BOTTOM_DOOR_TILE)){
						floorLayout[i+1][j] = 9;
					}
					if (floorLayout[i-1][j-1] == 8 && floorLayout[i-1][j-2] == 12)
						floorLayout[i-1][j-1] = 9;
				}
				else if (floorLayout[i][j] == VERT_CORR_TOP_DOOR_TILE){
					if (floorLayout[i+1][j] < 7 && floorLayout[i+1][j+1] < 7)
						floorLayout[i+1][j+1] = 8;
				}
				else if (floorLayout[i][j] == VERT_CORR_BOTTOM_DOOR_TILE){
					if (floorLayout[i-1][j] < 7 && floorLayout[i-2][j] == FLOOR_TILE )
						floorLayout[i-1][j] = 9;
					if (floorLayout[i-1][j] < 7 && floorLayout[i-1][j-1] == 10)
						floorLayout[i-1][j] = 7;
					if (floorLayout[i-1][j] < 7 && floorLayout[i-1][j-1] < 10)
						floorLayout[i-1][j] = 7;
					if (floorLayout[i+1][j] < 7 && (floorLayout[i+1][j-1] == 11 || floorLayout[i+1][j-1] < 7))
						floorLayout[i+1][j] = 8;
				}
				else if (floorLayout[i][j] == HORIZ_CORR_TILE){
					if (floorLayout[i][j-1] == EMPTY_TILE)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == EMPTY_TILE)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == 10)
						floorLayout[i][j-1] = 7;
					if (floorLayout[i][j+1] == 10)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == 11)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == 11)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == 13)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == 14)
						floorLayout[i][j+1] = rng.nextInt(7);
//					if (floorLayout[i][j+1] == 8)
//						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] < 7 && floorLayout[i][j-2] == 11)
						floorLayout[i][j-1] = 8;
					if (floorLayout[i][j-1] < 7 && floorLayout[i][j-2] == 14)
						floorLayout[i][j-1] = 8;
					if (floorLayout[i][j-1] == 14)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == 13)
						floorLayout[i][j-1] = rng.nextInt(7);
				}
				else if (floorLayout[i][j] == HORIZ_CORR_RIGHT_DOOR_TILE){
					if (floorLayout[i][j-1] == 11)
						floorLayout[i][j-1] = 8;
					if (floorLayout[i][j+1] == 11)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == 11)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == 14)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j-1] < 7 && floorLayout[i][j-2] == 11)
						floorLayout[i][j-1] = 8;
					if (floorLayout[i][j+1] == EMPTY_TILE)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i+1][j] == 11 && floorLayout[i][j+1] < 7)
						floorLayout[i+1][j+1] = 11;
				}
				else if (floorLayout[i][j] == HORIZ_CORR_LEFT_DOOR_TILE){
					if (floorLayout[i][j-1] == 10)
						floorLayout[i][j-1] = 7;
					if (floorLayout[i][j+1] == 10)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == 13)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == EMPTY_TILE)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == 12)
						floorLayout[i][j+1] = rng.nextInt(7);
				}
				
			}
		}
		
		//these next couple lines are just to print the floor array for debugging purposes
		for (int i = 0; i < 24; i++){
			System.out.print(i + " ");
			if (i < 10)
				System.out.print(" ");
		}
		System.out.println("");
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				System.out.print(floorLayout[i][j]);
				System.out.print(" ");
				if (floorLayout[i][j] < 10)
					System.out.print(" ");
			}
			System.out.print(" <- " + i);
			System.out.println();}
		
		//this changes all the corridor tiles in the array to floor tiles to allow the character to walk on them
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				if (floorLayout[i][j] >= VERT_CORR_TILE && floorLayout[i][j] < 27)
					floorLayout[i][j] = FLOOR_TILE;
			}
		}
		
		//this should hopefully alleviate a couple glitches caused with the corridor system
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				if (j != 0 && i != 0 && i != 31 && floorLayout[i][j] == BOTTOM_CENTER_ROOM_TILE && floorLayout[i][j-1] == FLOOR_TILE){
					floorLayout[i][j] = FLOOR_TILE;
					if (floorLayout[i-1][j-1] == 10 || floorLayout[i-1][j-1] < 7)
						floorLayout[i-1][j] = 7;
					else if (floorLayout[i+1][j-1] == 10 || floorLayout[i+1][j-1] < 7)
						floorLayout[i+1][j] = 8;
				}
				if (floorLayout[i][j] == BOTTOM_CENTER_ROOM_TILE){
					if (floorLayout[i][j-1] == 10)
						floorLayout[i][j] = 7;
					else if (floorLayout[i][j-1] == 11)
						floorLayout[i][j] = 8;
				}
				if (j != 23 && floorLayout[i][j] == TOP_CENTER_ROOM_TILE && floorLayout[i][j+1] == FLOOR_TILE){
					floorLayout[i][j] = FLOOR_TILE;
				}
			}
		}
		
	}
	
	//draws the floor array to the window
	public void drawFloor(SpriteBatch batch){
		int wallSetTile;
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				if (floorLayout[i][j] == EMPTY_TILE){
					//leaves black if open space
				}
				else if (floorLayout[i][j] < 7){
					wallSetTile = floorLayout[i][j];
					batch.draw(floorTiles[wallSetTile], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == FLOOR_TILE){
					batch.draw(floorTiles[FLOOR_TILE], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 7){
					batch.draw(floorTiles[7], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 8){
					batch.draw(floorTiles[8], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 9){
					batch.draw(floorTiles[9], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 10){
					batch.draw(floorTiles[10], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 11){
					batch.draw(floorTiles[11], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 12){
					batch.draw(floorTiles[12], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 13){
					batch.draw(floorTiles[13], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 14){
					batch.draw(floorTiles[14], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == FLOOR_TILE){
					batch.draw(floorTiles[FLOOR_TILE], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == EMPTY_TILE){
					batch.draw(floorTiles[EMPTY_TILE], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == 17){
					batch.draw(floorTiles[17], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == TOP_CENTER_ROOM_TILE){
					batch.draw(floorTiles[0], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == BOTTOM_CENTER_ROOM_TILE){
					batch.draw(floorTiles[0], i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				else if (floorLayout[i][j] == STAIR_TILE){
					batch.draw(stairs, i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
				
			}
		}
	}
	
	//can be used for finding open space to place items at, returns array of [x-coordinate, y-coordinate]
	public int[] findOpenSpace(){
		int i = rng.nextInt(31);
		int j = rng.nextInt(23);
		
		while (floorLayout[i][j] != FLOOR_TILE){
			i = rng.nextInt(31);
			j = rng.nextInt(23);
		}
		
		return new int[] {i * TILE_SIZE, j * TILE_SIZE};
	}
	
	public void placeItems(){		
		for (int i = 0; i < numItemsOnFloor; i++){
			Weapon weapon = new Weapon(floorLevel, findOpenSpace());
			itemsOnFloor[i] = weapon;
		}
	}
	
	public void drawItems(SpriteBatch batch){
		for (int i = 0; i < numItemsOnFloor; i++){
			itemsOnFloor[i].itemSprite.draw(batch);
		}
	}
	
	public void placeEnemies(){
		for (int i = 0; i < numEnemiesOnFloor; i++){
			int[] coordinates = findOpenSpace();
			Enemy enemy = new Enemy(coordinates[0], coordinates[1], coordinates[0] + TILE_SIZE, coordinates[1] + 64, this);
			enemiesOnFloor[i] = enemy;
		}
	}
	
	public void drawEnemies(SpriteBatch batch){
		for (int i = 0; i < numEnemiesOnFloor; i++){
			enemiesOnFloor[i].character.draw(batch);
		}
	}
}
