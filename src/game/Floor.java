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
	public static int tileSize = 32;
	private Random rng = new Random();
	private Texture brickTexture = new Texture("assets/brick.png");
	private Texture dirtTexture = new Texture("assets/dirt.png");
	private Texture floorTileset = new Texture("assets/WallSet.png");
	private TextureRegion[] floorTiles = new TextureRegion[20];
	
	public Floor(){
	
		//floor array is now represented by numbers 0-28 where 16 equals empty space between rooms
		//20-28 represent different corridor pieces that are used later for rendering
		//0-17 refer to tile numbers, reference WallSet (with tile mapping).xcf to find tile numbers
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				floorLayout[i][j] = 16;
			}
		}
		
		int k = 0;
		//for instantiating our tiles array with each tile of the tileset
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 4; j++){
				floorTiles[k] = new TextureRegion(floorTileset, j * 16, i * 16, 16, 16);
				k++;
			}
		}
		
		this.placeRooms();
		
	}
	
	//places rooms on floor
	public void placeRooms() {
		
		rooms = new Vector<Room>();
		boolean failed = false;
		int newRoomCenterX, newRoomCenterY, //these are all used for corridor generation
			prevRoomCenterX = 0, prevRoomCenterY = 0;
		
		for (int i = 0; i < 50; i++){ //generally, as you increase i's limit, the density of rooms per floor will increase here
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
		for (int i = room.x1/tileSize; i <= room.x2/tileSize; i++){
			for (int j = room.y1/tileSize; j <= room.y2/tileSize; j++){
				if (i == room.x1/tileSize && j == room.y1/tileSize && floorLayout[i][j] == 16)
					floorLayout[i][j] = 13; //creates bottom left corner
				else if (i == room.x2/tileSize && j == room.y1/tileSize && floorLayout[i][j] == 16)
					floorLayout[i][j] = 14; //creates bottom right corner
				else if (i == room.x1/tileSize && floorLayout[i][j] == 16)
					floorLayout[i][j] = 10; //creates left wall
				else if (i == room.x2/tileSize && floorLayout[i][j] == 16)
					floorLayout[i][j] = 11; //creates right wall
				else if (j == room.y2/tileSize && i == room.centerX/tileSize && (floorLayout[i][j] == 16 || floorLayout[i][j] == 20))
					floorLayout[i][j] = 27; //center of top wall, used for corridors later
				else if (j == room.y1/tileSize && i == room.centerX/tileSize && (floorLayout[i][j] == 16 || floorLayout[i][j] == 20))
					floorLayout[i][j] = 28; //center of bottom wall, used for corridors later
				else if ((j == room.y1/tileSize || j == room.y2/tileSize) && floorLayout[i][j] == 16)
					floorLayout[i][j] = rng.nextInt(7); //creates bottom and top walls
				else if (floorLayout[i][j] == 23 || floorLayout[i][j] == 20 || floorLayout[i][j] == 26){
					//Do nothing
				}
				else
					floorLayout[i][j] = 15;
			}
		}
	}
	
	//carves out corridor in floor array
	private void carveCorridor(int prevX, int newX, int prevY, int newY){
		
		if (prevX < newX && prevY < newY){
			int k = prevY / tileSize;
			for (int i = prevX / tileSize; i <= newX / tileSize; i++){
				if (floorLayout[i][k] == 16)
					floorLayout[i][k] = 23;
				else if (floorLayout[i][k] == 11)
					floorLayout[i][k] = 24;
				else if (floorLayout[i][k] == 10)
					floorLayout[i][k] = 25;
				else if (floorLayout[i][k] == 20 || floorLayout[i][k] == 21 || floorLayout[i][k] == 22 || floorLayout[i][k] == 26 || i == newX / tileSize) 
					//if overlapping with any verical corridor or at end of carving, make a both corridor tile 
					floorLayout[i][k] = 26;
				else if (floorLayout[i][k] == 24)
					floorLayout[i][k] = 26;
				else if (floorLayout[i][k] == 25)
					floorLayout[i][k] = 25;
				else
					floorLayout[i][k] = 23;
			}
			k = newX / tileSize;
			for (int j = prevY / tileSize; j <= newY / tileSize; j++){
				if (floorLayout[k][j] == 16)
					floorLayout[k][j] = 20;
				else if (floorLayout[k][j] == 27)
					floorLayout[k][j] = 21;
				else if (floorLayout[k][j] == 28)
					floorLayout[k][j] = 22;
				else if (floorLayout[k][j] == 23 || floorLayout[k][j] == 24 || floorLayout[k][j] == 25 || floorLayout[k][j] == 26 || j == newY / tileSize) 
					//if overlapping with any horizontal corridor or at end of carving, make a both corridor tile 
					floorLayout[k][j] = 26;
				else if (floorLayout[k][j] == 21)
					floorLayout[k][j] = 21;
				else if (floorLayout[k][j] == 22)
					floorLayout[k][j] = 22;
				else
					floorLayout[k][j] = 20;
			}
		}
		else if (prevX < newX && prevY > newY){
			int k = prevY / tileSize;
			for (int i = prevX / tileSize; i <= newX / tileSize; i++){
				if (floorLayout[i][k] == 16)
					floorLayout[i][k] = 23;
				else if (floorLayout[i][k] == 11)
					floorLayout[i][k] = 24;
				else if (floorLayout[i][k] == 10)
					floorLayout[i][k] = 25;
				else if (floorLayout[i][k] == 20 || floorLayout[i][k] == 21 || floorLayout[i][k] == 22 || floorLayout[i][k] == 26 || i == newX / tileSize) 
					//if overlapping with any verical corridor or at end of carving, make a both corridor tile 
					floorLayout[i][k] = 26;
				else if (floorLayout[i][k] == 24)
					floorLayout[i][k] = 26;
				else if (floorLayout[i][k] == 25)
					floorLayout[i][k] = 25;
				else
					floorLayout[i][k] = 23;
			}
			k = newX / tileSize;
			for (int j = newY / tileSize; j <= prevY / tileSize; j++){
				if (floorLayout[k][j] == 16)
					floorLayout[k][j] = 20;
				else if (floorLayout[k][j] == 27)
					floorLayout[k][j] = 21;
				else if (floorLayout[k][j] == 28)
					floorLayout[k][j] = 22;
				else if (floorLayout[k][j] == 23 || floorLayout[k][j] == 24 || floorLayout[k][j] == 25 || floorLayout[k][j] == 26 || j == newY / tileSize) 
					//if overlapping with any horizontal corridor or at end of carving, make a both corridor tile 
					floorLayout[k][j] = 26;
				else if (floorLayout[k][j] == 21)
					floorLayout[k][j] = 21;
				else if (floorLayout[k][j] == 22)
					floorLayout[k][j] = 22;
				else
					floorLayout[k][j] = 20;
			}
		}
		else if (prevX > newX && prevY < newY){
			int k = prevY / tileSize;
			for (int i = prevX / tileSize; i >= newX / tileSize; i--){
				if (floorLayout[i][k] == 16)
					floorLayout[i][k] = 23;
				else if (floorLayout[i][k] == 11)
					floorLayout[i][k] = 24;
				else if (floorLayout[i][k] == 10)
					floorLayout[i][k] = 25;
				else if (floorLayout[i][k] == 20 || floorLayout[i][k] == 21 || floorLayout[i][k] == 22 || floorLayout[i][k] == 26 || i == newX / tileSize) 
					//if overlapping with any verical corridor or at end of carving, make a both corridor tile 
					floorLayout[i][k] = 26;
				else if (floorLayout[i][k] == 24)
					floorLayout[i][k] = 26;
				else if (floorLayout[i][k] == 25)
					floorLayout[i][k] = 25;
				else
					floorLayout[i][k] = 23;
			}
			k = newX / tileSize;
			for (int j = prevY / tileSize; j <= newY / tileSize; j++){
				if (floorLayout[k][j] == 16)
					floorLayout[k][j] = 20;
				else if (floorLayout[k][j] == 27)
					floorLayout[k][j] = 21;
				else if (floorLayout[k][j] == 28)
					floorLayout[k][j] = 22;
				else if (floorLayout[k][j] == 23 || floorLayout[k][j] == 24 || floorLayout[k][j] == 25 || floorLayout[k][j] == 26 || j == newY / tileSize) 
					//if overlapping with any horizontal corridor or at end of carving, make a both corridor tile 
					floorLayout[k][j] = 26;
				else if (floorLayout[k][j] == 21)
					floorLayout[k][j] = 21;
				else if (floorLayout[k][j] == 22)
					floorLayout[k][j] = 22;
				else
					floorLayout[k][j] = 20;
			}
		}
		else {
			int k = newY / tileSize;
			for (int i = newX / tileSize; i <= prevX / tileSize; i++){
				if (floorLayout[i][k] == 16)
					floorLayout[i][k] = 23;
				else if (floorLayout[i][k] == 11)
					floorLayout[i][k] = 24;
				else if (floorLayout[i][k] == 10)
					floorLayout[i][k] = 25;
				else if (floorLayout[i][k] == 20 || floorLayout[i][k] == 21 || floorLayout[i][k] == 22 || floorLayout[i][k] == 26 || i == newX / tileSize) 
					//if overlapping with any verical corridor or at end of carving, make a both corridor tile 
					floorLayout[i][k] = 26;
				else if (floorLayout[i][k] == 24)
					floorLayout[i][k] = 26;
				else if (floorLayout[i][k] == 25)
					floorLayout[i][k] = 25;
				else
					floorLayout[i][k] = 23;
			}
			k = prevX / tileSize;
			for (int j = newY / tileSize; j <= prevY / tileSize; j++){
				if (floorLayout[k][j] == 16)
					floorLayout[k][j] = 20;
				else if (floorLayout[k][j] == 27)
					floorLayout[k][j] = 21;
				else if (floorLayout[k][j] == 28)
					floorLayout[k][j] = 22;
				else if (floorLayout[k][j] == 23 || floorLayout[k][j] == 24 || floorLayout[k][j] == 25 || floorLayout[k][j] == 26 || j == newY / tileSize) 
					//if overlapping with any horizontal corridor or at end of carving, make a both corridor tile 
					floorLayout[k][j] = 26;
				else if (floorLayout[k][j] == 21)
					floorLayout[k][j] = 21;
				else if (floorLayout[k][j] == 22)
					floorLayout[k][j] = 22;
				else
					floorLayout[k][j] = 20;
			}
		}
	}
	
	//to fix up the array for rendering
	public void fixUpArray(){
		
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				
				//don't even bother trying to make sense of these, these are just all the different situations being accounted for
				if (floorLayout[i][j] == 26){
					if (floorLayout[i][j+1] == 16)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == 16)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i+1][j] == 16)
						floorLayout[i+1][j] = 11;
					if (floorLayout[i-1][j] == 16)
						floorLayout[i-1][j] = 10;
					if (floorLayout[i+1][j] == 11 && floorLayout[i][j+1] < 7)
						floorLayout[i+1][j+1] = 11;
					if (floorLayout[i+1][j] == 11 && floorLayout[i][j-1] < 7 && floorLayout[i+1][j-1] != 11)
						floorLayout[i+1][j-1] = 14;
					if (floorLayout[i-1][j] == 10 && floorLayout[i][j-1] < 8)
						floorLayout[i-1][j-1] = 13;
					if (floorLayout[i][j-1] == 10)
						floorLayout[i][j-1] = 7;
					if (floorLayout[i-1][j] == 10 && floorLayout[i][j-1] < 7)
						floorLayout[i-1][j-1] = 13;
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
			
				}
				else if (floorLayout[i][j] == 20){
					if (floorLayout[i-1][j] == 16)
						floorLayout[i-1][j] = 10;
					if (floorLayout[i+1][j] == 16)
						floorLayout[i+1][j] = 11;
					if (floorLayout[i-1][j] == 11)
						floorLayout[i-1][j] = 12;
					if (floorLayout[i+1][j] == 10)
						floorLayout[i+1][j] = 12;
					if (floorLayout[i-1][j] == 13)
						floorLayout[i-1][j] = 10;
					if (floorLayout[i+1][j] == 14)
						floorLayout[i+1][j] = 11;
					if (floorLayout[i-1][j] == 14)
						floorLayout[i-1][j] = 16;
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
					if (floorLayout[i+1][j] == 22 && floorLayout[i+1][j-1] == 11)
						floorLayout[i+1][j] = 8;
					if (floorLayout[i-1][j] == 22 && floorLayout[i-1][j-1] == 10)
						floorLayout[i-1][j] = 7;
				}
				else if (floorLayout[i][j] == 21){
					
				}
				else if (floorLayout[i][j] == 22){
					if (floorLayout[i-1][j] < 7 && floorLayout[i-2][j] == 15 )
						floorLayout[i-1][j] = 9;
					if (floorLayout[i-1][j] < 7)
						floorLayout[i-1][j] = 7;
					if (floorLayout[i+1][j] < 7)
						floorLayout[i+1][j] = 8;
				}
				else if (floorLayout[i][j] == 23){
					if (floorLayout[i][j-1] == 16)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == 16)
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
					if (floorLayout[i][j+1] == 8)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] < 7 && floorLayout[i][j-2] == 11)
						floorLayout[i][j-1] = 8;
				}
				else if (floorLayout[i][j] == 24){
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
					if (floorLayout[i][j+1] == 16)
						floorLayout[i][j+1] = rng.nextInt(7);
				}
				else if (floorLayout[i][j] == 25){
					if (floorLayout[i][j-1] == 10)
						floorLayout[i][j-1] = 7;
					if (floorLayout[i][j+1] == 10)
						floorLayout[i][j+1] = rng.nextInt(7);
					if (floorLayout[i][j-1] == 13)
						floorLayout[i][j-1] = rng.nextInt(7);
					if (floorLayout[i][j+1] == 16)
						floorLayout[i][j+1] = rng.nextInt(7);
				}
				
			}
		}
		
		//these next 15 lines are just to print the floor array for debugging purposes
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
		
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				if (floorLayout[i][j] >= 20 && floorLayout[i][j] < 27)
					floorLayout[i][j] = 15;
			}
		}
	}
	
	//draws the floor array to the window
	public void drawFloor(SpriteBatch batch){
		int wallSetTile;
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				if (floorLayout[i][j] == 16){
					//leaves black if open space
				}
				else if (floorLayout[i][j] < 7){
					wallSetTile = floorLayout[i][j];
					batch.draw(floorTiles[wallSetTile], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 15){
					batch.draw(floorTiles[15], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 7){
					batch.draw(floorTiles[7], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 8){
					batch.draw(floorTiles[8], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 10){
					batch.draw(floorTiles[10], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 11){
					batch.draw(floorTiles[11], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 12){
					batch.draw(floorTiles[12], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 13){
					batch.draw(floorTiles[13], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 14){
					batch.draw(floorTiles[14], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 15){
					batch.draw(floorTiles[15], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 16){
					batch.draw(floorTiles[16], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 17){
					batch.draw(floorTiles[17], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 27){
					batch.draw(floorTiles[0], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else if (floorLayout[i][j] == 28){
					batch.draw(floorTiles[0], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				else{
					batch.draw(floorTiles[15], i * tileSize, j * tileSize, tileSize, tileSize);
				}
				
			}
		}
	}
	
	//can be used for finding open space to place items at, returns array of [x-coordinate, y-coordinate]
	public int[] findOpenSpace(){
		int i = rng.nextInt(31);
		int j = rng.nextInt(23);
		
		while (floorLayout[i][j] != 15){
			i = rng.nextInt(31);
			j = rng.nextInt(23);
		}
		
		return new int[] {i * tileSize, j * tileSize};
	}
	
}
