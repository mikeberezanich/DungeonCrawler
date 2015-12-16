package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Random;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	private AudioPlayer MGP = AudioPlayer.player;
    private static AudioStream BGM;
    public static int score;
    private BitmapFont itemStrengthFont;
	private CharSequence itemStrengthText;
	
    public void create () {

    	floorLevel = 0;
    	score = 0;
    	batch = new SpriteBatch();
        floor = new Floor(floorLevel);
        positionRng = rng.nextInt(floor.rooms.size() - 1);
        player = new Player(floor.rooms.get(positionRng).centerX, floor.rooms.get(positionRng).centerY, floor.rooms.get(positionRng).centerX+TILE_SIZE, floor.rooms.get(positionRng).centerY+TILE_SIZE);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        music();
        floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] = player;
        moveCamera();
        Gdx.graphics.setContinuousRendering(false);
        
    }

    public void render () {

    	camera.update();
    	batch.setProjectionMatrix(camera.combined);
    	batch.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        floor.drawFloor(batch);
        floor.drawItems(batch);
        floor.drawEnemies(batch);
        player.drawPlayer(batch);
        player.drawBars(batch);
        handleInput();
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
    	
    	//controls for movement
    	if (Gdx.input.isKeyJustPressed(Keys.UP)){
    		if (floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE + 1] instanceof Enemy){
    			player.changeDirection("up");
    		}
    		else{
	    		player.movePlayer("up", batch, floor);
	    		moveCamera();
	    		processTurn();
	    		checkForStairs();
    		}
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.DOWN)){
    		if (floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE - 1] instanceof Enemy){
    			player.changeDirection("down");
    		}
    		else{
	    		player.movePlayer("down", batch, floor);
	    		moveCamera();
	    		processTurn();
	    		checkForStairs();
    		}
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.RIGHT)){
    		if (floor.characterLocations[player.x1 / TILE_SIZE + 1][player.y1 / TILE_SIZE] instanceof Enemy){
    			player.changeDirection("right");
    		}
    		else{
	    		player.movePlayer("right", batch, floor);
	    		moveCamera();
	    		processTurn();
	    		checkForStairs();
    		}
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.LEFT)){
    		if (floor.characterLocations[player.x1 / TILE_SIZE - 1][player.y1 / TILE_SIZE] instanceof Enemy){
    			player.changeDirection("left");
    		}
    		else{
	    		player.movePlayer("left", batch, floor);
	    		moveCamera();
	    		processTurn();
	    		checkForStairs();
    		}
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.R)){
    		processTurn();
    	}
    	
    	//controls for item interaction
    	if (Gdx.input.isKeyJustPressed(Keys.E)){
    		if (floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] != null){
    			if (floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] instanceof Potion){
    				player.usePotion((Potion) floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE], floor, batch);
    			}
    			else{
		    		player.equipItem(floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE], floor);
		    		player.pickUpItem(floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE], floor);
    			}
    		}
    		processTurn();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.W)){
    		if (floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] != null){
    			if (floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] instanceof Weapon || floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] instanceof Armor){
    				itemStrengthFont = new BitmapFont();
    				itemStrengthText = Integer.toString(floor.itemLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE].strength);
    				itemStrengthFont.draw(batch, itemStrengthText, player.x1 + 8, player.y2 + 16);
    			}
    		}
    	}
    	
    	//controls for attacking
    	if (Gdx.input.isKeyJustPressed(Keys.D)){
    		if (player.directionFaced == "right"){
    			if (floor.characterLocations[player.x1 / TILE_SIZE + 1][player.y1 / TILE_SIZE] != null){
    				player.attack(floor.characterLocations[player.x1 / TILE_SIZE + 1][player.y1 / TILE_SIZE], batch, floor);
    			}
    		}
    		else if (player.directionFaced == "left"){
    			if (floor.characterLocations[player.x1 / TILE_SIZE - 1][player.y1 / TILE_SIZE] != null){
    				player.attack(floor.characterLocations[player.x1 / TILE_SIZE - 1][player.y1 / TILE_SIZE], batch, floor);
    			}
    		}
			else if (player.directionFaced == "up"){
    			if (floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE + 1] != null){
    				player.attack(floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE + 1], batch, floor);
    			}
			}
			else if (player.directionFaced == "down"){
    			if (floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE - 1] != null){
    				player.attack(floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE - 1], batch, floor);
    			}
			}
    		processTurn();
		}
    	
    	//controls for casting spells
    	if (Gdx.input.isKeyJustPressed(Keys.A)){
    		player.castFireball(floor, batch);
    		processTurn();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.S)){
    		player.castIceLance(floor, batch);
    		processTurn();
    	}
    	if (Gdx.input.isKeyJustPressed(Keys.Q)){
    		player.castHealingTouch(batch);
    		processTurn();
    	}

    	//this is to generate a new floor in case of a faulty floor being generated
    	//will be removed in future but this is a temporary solution
    	//won't work if enemy is nearby to prevent abuse
    	if (Gdx.input.isKeyJustPressed(Keys.P)){
    		
    		boolean enemyNearby = false;
    		
    		//this loop checks for nearby enemies
    		for (int i = 0; i < 3; i++){
    			for (int j = 0; j < 3; j++){
    				if ((player.x1 / TILE_SIZE + i < 32 && player.y1 / TILE_SIZE + j < 24 && floor.characterLocations[player.x1 / TILE_SIZE + i][player.y1 / TILE_SIZE + j] instanceof Enemy) ||
    						(player.x1 / TILE_SIZE + i < 32 && player.y1 / TILE_SIZE - j >= 0 && floor.characterLocations[player.x1 / TILE_SIZE + i][player.y1 / TILE_SIZE - j] instanceof Enemy) ||
    						(player.x1 / TILE_SIZE - i >= 0 && player.y1 / TILE_SIZE + j < 24 && floor.characterLocations[player.x1 / TILE_SIZE - i][player.y1 / TILE_SIZE + j] instanceof Enemy) || 
    						(player.x1 / TILE_SIZE - i >= 0 && player.y1 / TILE_SIZE - j >= 0 && floor.characterLocations[player.x1 / TILE_SIZE - i][player.y1 / TILE_SIZE - j] instanceof Enemy)){
    					enemyNearby = true;
    				}
    			}
    		}
    		
    		//generates new floor but doesn't boost score or increase floor level
    		if (!enemyNearby){
    			floor = new Floor(floorLevel);
        		positionRng = rng.nextInt(floor.rooms.size() - 1);
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
    }
    
    //checks if the stairs are underneath the player
    private void checkForStairs() {
    	if (floor.floorLayout[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] == 30){
    		floor = new Floor(++floorLevel);
    		positionRng = rng.nextInt(floor.rooms.size() - 1);
    		player.character.setPosition(floor.rooms.get(positionRng).centerX, floor.rooms.get(positionRng).centerY);
    		player.x1 = (int) player.character.getX();
    		player.x2 = (int) player.character.getX() + TILE_SIZE;
    		player.y1 = (int) player.character.getY();
    		player.y2 = (int) player.character.getY() + TILE_SIZE;
    		player.moveToNewFloor();
    		floor.characterLocations[player.x1 / TILE_SIZE][player.y1 / TILE_SIZE] = player;
    		moveCamera();
    		score += 200 + floorLevel * 10;
    	}
    }
    
   //this function just handles starting the music 
   private void music(){
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
   
   	//this function is used to stop the music when the player dies
   	public static void stopMusic(){
   		try {
			BGM.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
   	}
    
    //processes turns for each enemy
    private void processTurn(){
    	
    	for (int i = 0; i < floor.enemiesOnFloor.size(); i++){
    		floor.enemiesOnFloor.get(i).AI(player, floor, batch);
    	}
  
    }
}
