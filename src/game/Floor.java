package game;

import java.util.Random;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Floor{

	private int[][] floorLayout = new int[32][24];
	private int tileSize = 32;
	private Texture brickTexture = new Texture("assets/brick.png");
	private Texture dirtTexture = new Texture("assets/dirt.png");
	
//	private Map map = new Map();
//	private MapLayer layer = map.getLayers().get(0);
//	private TiledMapTileLayer tiledLayer = (TiledMapTileLayer)map.getLayers().get(0);
	
	public Floor(Batch batch){
		//temporary floor generation here, just fills the floor in with all walls. Will be replaced with random floor generation in future
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				floorLayout[i][j] = 1;
			}
		}
		
		this.placeRooms();
		this.drawRooms(batch);
	}
	
	public void placeRooms() {
		
		Vector<Room> rooms = new Vector<Room>(50);
		Random rng = new Random();
		boolean failed = false;
		
		
		for (int i = 0; i < 20; i++){
			int w = 3 + rng.nextInt(5);
			int h = 3 + rng.nextInt(5); 
			int x = rng.nextInt(32 - w - 1) + 1;
			int y = rng.nextInt(24 - h - 1) + 1;
			Room room = new Room(x, y, w, h);
			failed = false;
			for (int j = 0; j < rooms.size(); j++){
				if (room.intersects(room, rooms.get(j)))
					failed = true;
				}
			if (!failed){
				createRoom(room, rooms);
				rooms.add(room);
			}
		}
		
		
	}
	
	private void createRoom(Room room, Vector<Room> rooms){
		for (int i = room.x1/tileSize; i <= room.x2/tileSize; i++){
			for (int j = room.y1/tileSize; j <= room.y2/tileSize; j++){
				floorLayout[i][j] = 0;
			}
		}
	}
	
	public void drawRooms(Batch batch){
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
	
}
