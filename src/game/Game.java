package game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Game implements ApplicationListener {
	
	private SpriteBatch batch;
	private Floor floor;
	private Camera camera;
	
    public void create () {
    	
    	batch = new SpriteBatch();
//      float unitScale = 1 / 32f;
        batch.begin();
        floor = new Floor(batch);
//      OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(floor.backgroundMap, unitScale, batch);
//   	renderer.renderTileLayer(floor.backgroundLayer);
    	batch.end();
    	
    }

    public void render () {
    	
    	if (Gdx.input.isKeyJustPressed(Keys.UP)){
    		//add animation code here

    	}
    	
//    	float unitScale = 1 / 32f;
//    	batch.begin();
//    	OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(floor.backgroundTiledMap, unitScale, batch);
//    	renderer.renderTileLayer(floor.backgroundLayer);
//    	batch.end();
    	
    }

    public void resize (int width, int height) {
    }

    public void pause () {
    }

    public void resume () {
    }

    public void dispose () {
    }
    
    public static void main(String[] args){
    	new LwjglApplication(new Game(), "Dungeon Crawler", 1024, 768);
    }
}
