package game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Game implements ApplicationListener {
	
	private SpriteBatch batch;
	
    public void create () {
    	
    	batch = new SpriteBatch();
    	batch.begin();
        Floor floor = new Floor(batch);
        batch.end();
    	
    }

    public void render () {
    	
    	if (Gdx.input.isKeyJustPressed(Keys.ANY_KEY)){
    		//add animation code here
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
