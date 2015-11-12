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
	TiledMap backgroundMap = new TiledMap();
	
	public Floor(){
	
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				floorLayout[i][j] = 1;
			}
		}
		
		this.placeRooms();
		
	}
	
	public void placeRooms() {
		
		rooms = new Vector<Room>();
		boolean failed = false;
		int newRoomCenterX, newRoomCenterY, //these are all used for corridor generation
			prevRoomCenterX = 0, prevRoomCenterY = 0;
		
		for (int i = 0; i < 30; i++){ //generally, as you increase i's limit, the density of rooms per floor will increase here
			int w = 3 + rng.nextInt(5);
			int h = 3 + rng.nextInt(5); 
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
		
		
	}
	
	//carves out room in floor array
	private void createRoom(Room room, Vector<Room> rooms){
		for (int i = room.x1/tileSize; i <= room.x2/tileSize; i++){
			for (int j = room.y1/tileSize; j <= room.y2/tileSize; j++){
				floorLayout[i][j] = 0;
			}
		}
	}
	
	//carves out corridor in floor array
	private void carveCorridor(int prevX, int newX, int prevY, int newY){
		
		if (prevX < newX && prevY < newY){
			for (int i = prevX / tileSize; i <= newX / tileSize; i++)
				floorLayout[i][prevY/tileSize] = 0;
			for (int j = prevY / tileSize; j <= newY / tileSize; j++)
				floorLayout[newX/tileSize][j] = 0;
		}
		else if (prevX < newX && prevY > newY){
			for (int i = prevX / tileSize; i <= newX / tileSize; i++)
				floorLayout[i][prevY/tileSize] = 0;
			for (int j = newY / tileSize; j <= prevY / tileSize; j++)
				floorLayout[newX/tileSize][j] = 0;
		}
		else if (prevX > newX && prevY < newY){
			for (int i = prevX / tileSize; i >= newX / tileSize; i--)
				floorLayout[i][prevY/tileSize] = 0;
			for (int j = prevY / tileSize; j <= newY / tileSize; j++)
				floorLayout[newX/tileSize][j] = 0;
		}
		else {
			for (int i = newX / tileSize; i <= prevX / tileSize; i++)
				floorLayout[i][newY/tileSize] = 0;
			for (int j = newY / tileSize; j <= prevY / tileSize; j++)
				floorLayout[prevX/tileSize][j] = 0;
		}
		
	}
	
	//draws the floor array to the window
	public void drawFloor(Batch batch){
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				if (floorLayout[i][j] == 1){
					batch.draw(brickTexture, i * tileSize, j * tileSize, tileSize, tileSize, 0, 0, 192, 192, false, false);
				}
				else if (floorLayout[i][j] == 0){
					batch.draw(dirtTexture, i * tileSize, j * tileSize, tileSize, tileSize, 0, 0, 64, 64, false, false);
				}
			}
		}
	}
	
	//can be used for finding open space to place items at, returns array of [x-coordinate, y-coordinate]
	public int[] findOpenSpace(){
		int i = rng.nextInt(31);
		int j = rng.nextInt(23);
		
		while (floorLayout[i][j] != 0){
			i = rng.nextInt(31);
			j = rng.nextInt(23);
		}
		
		return new int[] {i * tileSize, j * tileSize};
	}
	
}
