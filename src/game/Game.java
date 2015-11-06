package game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Game implements ApplicationListener {
	
	private SpriteBatch batch;
	private Floor floor;
	private OrthographicCamera camera;
	private Matrix4 projection = new Matrix4();
	public int tileSize = 32;
	
    public void create () {

        floor = new Floor(batch);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

    }

    public void render () {
    	
    	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    	projection.setToOrtho(0, Gdx.graphics.getWidth()/2, 0, Gdx.graphics.getHeight()/2, -1, 1);
    	batch = new SpriteBatch();
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        floor.drawFloor(batch);
        batch.end();
        
    	if (Gdx.input.isKeyJustPressed(Keys.UP)){
    		camera.position.y += tileSize;
    		camera.update();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.DOWN)){
    		camera.position.y -= tileSize;
    		camera.update();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.RIGHT)){
    		camera.position.x += tileSize;
    		camera.update();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.LEFT)){
    		camera.position.x -= tileSize;
    		camera.update();
    	}
    	
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
