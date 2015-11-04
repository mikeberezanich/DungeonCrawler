package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class Floor{

	private int[][] floorLayout = new int[32][24];
	private int tileSize = 32;
	private Texture brickTexture = new Texture("assets/brick.png");
//	private Map map = new Map();
//	private MapLayer layer = map.getLayers().get(0);
//	private TiledMapTileLayer tiledLayer = (TiledMapTileLayer)map.getLayers().get(0);
	
	Floor(SpriteBatch batch){
		//temporary floor generation here, just fills the floor in with all walls. Will be replaced with random floor generation in future
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				floorLayout[i][j] = 1;
			}
		}
		
		
		
		for (int i = 0; i < 32; i++){
			for (int j = 0; j < 24; j++){
				if (floorLayout[i][j] == 1)
				{
					batch.draw(brickTexture, i * tileSize, j * tileSize, tileSize, tileSize, 0, 0, 192, 192, false, false);
					//tiledLayer.setCell(i * tileSize, j * tileSize, new Cell());
				}
			}
		}
	}
	
}
