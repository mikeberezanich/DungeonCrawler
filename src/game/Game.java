package game;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

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
	
	private static final int TILE_SIZE = 32;
	private SpriteBatch batch;
	private Floor floor;
	private OrthographicCamera camera;
	private Matrix4 projection = new Matrix4();
	public Player player;
	private int positionRng;
	private Random rng = new Random();
	public Connection connection;
	
    public void create () {

    	batch = new SpriteBatch();
        floor = new Floor();
        positionRng = rng.nextInt(floor.rooms.size());
        player = new Player(floor.rooms.get(positionRng).centerX, floor.rooms.get(positionRng).centerY, floor.rooms.get(positionRng).centerX+TILE_SIZE, floor.rooms.get(positionRng).centerY+TILE_SIZE);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        try {
			connection = new DatabaseConnection().connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public void render () {

    	handleInput();
    	checkForStairs();
    	moveCamera();
    	camera.update();
    	batch.setProjectionMatrix(camera.combined); //comment this line out for testing
    	batch.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        floor.drawFloor(batch);
        player.drawPlayer(batch);
        batch.end();
    	
    }
    
    public void moveCamera(){
    	camera.position.set(player.x1, player.y1, 0);
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
    
    private void handleInput() {
    	if (Gdx.input.isKeyJustPressed(Keys.UP)){
    		player.movePlayer("up", batch, floor);
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.DOWN)){
    		player.movePlayer("down", batch, floor);
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.RIGHT)){
    		player.movePlayer("right", batch, floor);
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.LEFT)){
    		player.movePlayer("left", batch, floor);
    	}
    }
    
    private void checkForStairs() {
    	if (player.getPositionTile(floor) == 30){
    		floor = new Floor();
    		player.character.setPosition(floor.rooms.get(positionRng).centerX, floor.rooms.get(positionRng).centerY);
    		player.x1 = (int) player.character.getX();
    		player.x2 = (int) player.character.getX() + TILE_SIZE;
    		player.y1 = (int) player.character.getY();
    		player.y2 = (int) player.character.getY() + TILE_SIZE;
    		player.moveToNewFloor();
    	}
    }
}
