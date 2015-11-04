package game;

public class Player {

	private int lvl;
	private int exp;
	private int health;
	private int atk;
	private int def;
	
	public int getLvl(){
		return lvl;
	}
	public void setLvl(int l){
		lvl = l;
	}
	
	public int getExp(){
		return exp;
	}
	public void setExp(int e){
		exp = e;
	}
	
	public int getHealth(){
		return health;
	}
	public void setHealth(int h){
		health = h;
	}
	
	public int getAtk(){
		return atk;
	}
	public void setAtk(int a){
		atk = a;
	}
	
	public int getDef(){
		return def;
	}
	public void setDef(int d){
		def = d;
	}
	
}
