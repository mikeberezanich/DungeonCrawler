package game;

import java.util.Random;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
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

	private int[][] floorLayout = new int[32][24];
	private int tileSize = 32;
	private Texture brickTexture = new Texture("assets/brick.png");
	private TextureRegion brickTextureRegion = new TextureRegion(brickTexture, tileSize, tileSize);
	private Texture dirtTexture = new Texture("assets/dirt.png");
	TiledMap backgroundMap = new TiledMap();
	public TiledMapTileLayer backgroundLayer = new TiledMapTileLayer(1024, 768, tileSize, tileSize);//(TiledMapTileLayer)backgroundTiledMap.getLayers().get(this);
//	public TiledMapTileLayer backgroundLayer = (TiledMapTileLayer)backgroundMap.getLayers().get();
//	private TiledMapTileLayer tiledLayer = (TiledMapTileLayer)map.getLayers().get(0);
	
	public Floor(Batch batch){
		//instantiates the floors array with all 1's, representing walls
		//I think we should try to convert the floor to a tiled map but I'm having trouble doing it
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				floorLayout[i][j] = 1;
//				brickTextureRegion.setRegion(i * tileSize, j * tileSize, tileSize, tileSize);
//				StaticTiledMapTile tile = new StaticTiledMapTile(brickTextureRegion);
//				Cell cell = new Cell();
//				backgroundLayer.setCell(i * tileSize, j * tileSize, cell);
//				cell.setTile(tile);
				
			}
		}
		
//		backgroundLayer = backgroundTiledMap.getLayers());
		this.placeRooms();
		
		this.drawFloor(batch);
	}
	
	public void placeRooms() {
		
		Vector<Room> rooms = new Vector<Room>(50);
		Random rng = new Random();
		boolean failed = false;
		int newRoomCenterX, newRoomCenterY, 
			prevRoomCenterX = 0, prevRoomCenterY = 0;
		
		
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
				if (rooms.size() > 0){
					prevRoomCenterX = rooms.lastElement().centerX;//rooms.get(rooms.size() - 2).centerX;
					prevRoomCenterY = rooms.lastElement().centerY;//rooms.get(rooms.size() - 2).centerY;
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
	
	private void createRoom(Room room, Vector<Room> rooms){
		for (int i = room.x1/tileSize; i <= room.x2/tileSize; i++){
			for (int j = room.y1/tileSize; j <= room.y2/tileSize; j++){
				floorLayout[i][j] = 0;
			}
		}
	}
	
//	private void horizontalCorridor(int x1, int x2, int y1, int y2){
//		int j;
//		if (Math.min(x1, x2) == x1)
//			j = y1/tileSize;
//		else
//			j = y2/tileSize;
//		
//		for (int i = Math.min(x1, x2)/tileSize; i < Math.max(x1, x2)/tileSize; i++){
//			floorLayout[i][j] = 0;
//		}
//	}
//	
//	private void verticalCorridor(int x1, int x2, int y1, int y2){
//		int i;
//		if (Math.min(y1, y2) == y1)
//			i = x1/tileSize;
//		else
//			i = x2/tileSize;
//		
//		for (int j = Math.max(y1, y2)/tileSize; j > Math.min(y1, y2)/tileSize; j--){
//			floorLayout[i][j] = 0;
//		}
//	}
	
	private void carveCorridor(int prevX, int newX, int prevY, int newY){
		
		if (prevX < newX && prevY < newY){
			for (int i = prevX / tileSize; i < newX / tileSize; i++)
				floorLayout[i][prevY/tileSize] = 0;
			for (int j = prevY / tileSize; j < newY / tileSize; j++)
				floorLayout[newX/tileSize][j] = 0;
		}
		else if (prevX < newX && prevY > newY){
			for (int i = prevX / tileSize; i < newX / tileSize; i++)
				floorLayout[i][prevY/tileSize] = 0;
			for (int j = newY / tileSize; j < prevY / tileSize; j++)
				floorLayout[newX/tileSize][j] = 0;
		}
		else if (prevX > newX && prevY < newY){
			for (int i = prevX / tileSize; i > newX / tileSize; i--)
				floorLayout[i][prevY/tileSize] = 0;
			for (int j = prevY / tileSize; j < newY / tileSize; j++)
				floorLayout[newX/tileSize][j] = 0;
		}
		else {
			for (int i = newX / tileSize; i < prevX / tileSize; i++)
				floorLayout[i][newY/tileSize] = 0;
			for (int j = newY / tileSize; j < prevY / tileSize; j++)
				floorLayout[prevX/tileSize][j] = 0;
		}
		
	}
	
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
	
}
