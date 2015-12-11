package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import sun.audio.*;

public class Game implements ApplicationListener {
	
	private static final int TILE_SIZE = 32;
	private SpriteBatch batch;
	private Floor floor;
	private OrthographicCamera camera;
	public Player player;
	private int positionRng;
	private Random rng = new Random();
	public Connection connection;
	public int floorLevel;
	
    public void create () {

    	floorLevel = 0;
    	batch = new SpriteBatch();
        floor = new Floor(floorLevel);
        positionRng = rng.nextInt(floor.rooms.size() - 1);
        player = new Player(floor.rooms.get(positionRng).centerX, floor.rooms.get(positionRng).centerY, floor.rooms.get(positionRng).centerX+TILE_SIZE, floor.rooms.get(positionRng).centerY+TILE_SIZE);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        music();
        floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] = player;
        moveCamera();
        try {
			connection = new DatabaseConnection().connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public void render () {

    	handleInput();
    	camera.update();
    	batch.setProjectionMatrix(camera.combined); //comment this line out for testing
    	batch.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        floor.drawFloor(batch);
        floor.drawItems(batch);
        floor.drawEnemies(batch);
        player.drawPlayer(batch);
        batch.end();
    	
    }
    
    public void moveCamera(){
    	camera.position.set(player.x1, player.y1, 0);
    }

    public void resize (int width, int height) {
    	camera.update(); //I don't think this is correct but it seems to work better than having nothing there
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
    		moveCamera();
    		processTurn();
    		checkForStairs();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.DOWN)){
    		player.movePlayer("down", batch, floor);
    		moveCamera();
    		processTurn();
    		checkForStairs();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.RIGHT)){
    		player.movePlayer("right", batch, floor);
    		moveCamera();
    		processTurn();
    		checkForStairs();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.LEFT)){
    		player.movePlayer("left", batch, floor);
    		moveCamera();
    		processTurn();
    		checkForStairs();
    	}

    }
    
    //checks if the stairs are underneath the player
    //should update this to ask for confirmation of whether to move a new floor or not
    private void checkForStairs() {
    	if (player.getPositionTile(floor) == 30){
    		floor = new Floor(++floorLevel);
    		player.character.setPosition(floor.rooms.get(positionRng).centerX, floor.rooms.get(positionRng).centerY);
    		player.x1 = (int) player.character.getX();
    		player.x2 = (int) player.character.getX() + TILE_SIZE;
    		player.y1 = (int) player.character.getY();
    		player.y2 = (int) player.character.getY() + TILE_SIZE;
    		player.moveToNewFloor();
    		floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] = player;
    		moveCamera();
    	}
    }
    
   //this function just handles starting the music 
   private void music() 
    {       
        AudioPlayer MGP = AudioPlayer.player;
        AudioStream BGM;

        ContinuousAudioDataStream loop = null;

        try
        {
            BGM = new AudioStream(new FileInputStream("src/assets/magical_theme.wav"));
            AudioPlayer.player.start(BGM);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        MGP.start(loop);
    }
    
    //processes turns for each enemy
    private void processTurn(){
    	
    	for (int i = 0; i < floor.numEnemiesOnFloor; i++){
    		floor.enemiesOnFloor[i].AI(player, floor, batch);
    	}
    	
    }
}
