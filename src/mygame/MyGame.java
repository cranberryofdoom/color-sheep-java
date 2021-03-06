package mygame;

import jgame.*;
import jgame.platform.*;

public class MyGame extends StdGame {

	int key_shoot = 70;
	int key_blue = 68;
	int key_green = 83;
	int key_red = 65;

	String woolColor = "black";

	String[] colors = {
			"red", "green", "blue",
			"darkred", "darkgreen", "darkblue",
			"yellow", "cyan", "magenta",
			"darkyellow", "darkcyan", "darkmagenta",
			"white", "darkwhite", "black"
	};

	public static void main(String[]args) {
		new MyGame(parseSizeArgs(args,0));
	}

	public MyGame() {
		initEngineApplet();
	}

	public MyGame(JGPoint size) {
		initEngine(size.x,size.y);
	}

	public void initCanvas() {
		setCanvasSettings(60, 20, 8, 8, null, new JGColor(100, 100, 100), null);
	}

	public void initGame() {
		defineMedia("mygame.tbl");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		initial_lives = 1;
		startgame_ingame = true;
		setGameState("Title");
	}

	public void startTitle(){
		removeObjects(null,0);
	}

	public void paintFrameTitle(){
		drawString("Color Sheep", pfWidth()/2, 30, 0);
		drawString("Help Sir Woolson protect himself from the Wolves!", pfWidth()/2, 50, 0);
		drawString("Press Space to begin", pfWidth()/2, 70, 0);
	}

	public void initNewLife() {
		removeObjects(null,0);
		new Player(10, pfHeight() - 16, 5);
	}

	public void paintFrameStartGame() {
		drawString("Level " + level, viewWidth()/2, 80, 0);
	}

	public void paintFrameStartLevel() {
		drawString("Level " + level, viewWidth()/2, 80, 0);
	}

	public void paintFrameLevelDone() {
		drawString("Yay! You did it!", viewWidth()/2, 50, 0);
		drawString("Ready to start the next level?", viewWidth()/2, 80, 0);
		drawString("Press space to begin.", viewWidth()/2, 110, 0);
	}

	public void incrementLevel() {
		level += 1;
		woolColor = "black";
		if (level > 5){
			gameOver();
		}
	}

	public void startGameOver() {
		removeObjects(null,0);
	}

	public void doFrameInGame() {
		moveObjects();
		checkCollision(2,1);
		checkCollision(3,1);

		// cheat to finish the game
		if (getKey('Q')) {															
			gameOver();
		}

		// cheat to skip the level
		if (getKey('T')){
			removeObjects(null,2);											
			levelDone();
		}

		// key_down is selecting light and key_down is selecting dark
		if (getKey(key_down)){			
			woolColor = getKeyColor(getKey(key_red), getKey(key_green), getKey(key_blue));
		} else if (getKey(key_up)){ 	
			if (getKeyColor(getKey(key_red), getKey(key_green), getKey(key_blue)) != "black"){
				woolColor = "dark" + getKeyColor(getKey(key_red), getKey(key_green), getKey(key_blue));
			} else {
				woolColor = getKeyColor(getKey(key_red), getKey(key_green), getKey(key_blue));
			}
		}

		if (level == 5 && checkTime(0,6,5)) {
			new Boss();
		} else if (level != 5 && checkTime(0, 300, (int) random(25, 50))){
			new Enemy();										
		}

		if (gametime >= 300 && (countObjects("wolf", 0) == 0)) {
			levelDone();
		}

		if (level == 5 && gametime <= 1000 && checkTime(0, 300, (int) random(25, 50))){
			new Fireball();
		}

		if (level == 5 && gametime >= 1000 && (countObjects("megaWolf", 0) == 0)){
			gameOver();
		}
	}

	private String getKeyColor(boolean keyRed, boolean keyGreen, boolean keyBlue) {
		int red = keyRed ? 1 : 0;
		int green = keyGreen ? 1 : 0;
		int blue = keyBlue ? 1 : 0;
		String color = "black";
		if (red == 1){ 
			if (green == 1) {
				if (blue == 1) {
					color = "white";		// (255, 255, 255)
				} else {
					color = "yellow";		// (255, 255, 0)
				}
			} else {
				if (blue == 1) {
					color = "magenta";		// (255, 0, 255)
				} else {
					color = "red";			// (255, 0, 0)
				}
			}
		} else {
			if (green == 1) {
				if (blue == 1) {
					color = "cyan";			// (0, 255, 255)
				} else {
					color = "green";		// (0, 255, 0)
				}
			} else {
				if (blue == 1) {
					color = "blue";			// (0, 0, 255)
				} else {
					color = "black";		// (0, 0, 0)
				}
			}
		}
		return color;
	}

	public class Enemy extends JGObject {
		int furColor;
		public Enemy() {
			super(
					"wolf",
					true,
					pfWidth() - 10,
					pfHeight() - 16,
					2,
					"blackFur",
					random(-2, 0),
					0,
					-2
					);
			switch(level){
			case 0:
				furColor = random(0,2,1);
				break;
			case 1:
				furColor = random(0,5,1);
				break;
			case 2:
				furColor = random(0,8,1);
				break;
			case 3:
				furColor = random(0,11,1);
				break;
			case 4:
				furColor = random(0,14,1);
				break;
			}
		}

		public void paint() {
			drawImage(x, y, colors[furColor] + "Fur", true);
		}

		public void move() {
			if (getKey(key_shoot)) {
				if (colors[furColor].equals(woolColor)){
					hit(this);
				}
			}
			x -= 1;
		}

		public void hit(JGObject o) {
			remove();
			o.remove();
			score += 5;
		}
	}

	public class Boss extends JGObject {
		int hitCount = 0;
		String megaFurColor;

		public Boss(){
			super(
					"megaWolf",
					true,
					pfWidth() - 10,
					pfHeight() - 32,
					2,
					"redMegaFur",
					-0.5,
					0,
					-2
					);
		}

		public void paint() {
			megaFurColor = colors[hitCount];
			drawImage(x, y, megaFurColor + "MegaFur", true);
		}

		public void move() {
			timer += gamespeed;
			if (getKey(key_shoot)) {
				if (megaFurColor.equals(woolColor)){
					hit(this);
				}	
			}
			x -= 1;
		}

		public void hit(JGObject o) {
			if (hitCount == 15){
				gameOver();
			} else {
				hitCount += 1;
				score += 10;
			}
		}
	}

	public class Player extends JGObject {
		public Player(double x, double y, double speed) {
			super(
					"woolson",
					true,
					x,
					y,
					1,
					"blackWool",
					0,
					0,
					speed,
					speed,
					-1
					);
		}

		public void paint() {
			drawImage(x, y, woolColor + "Wool", true);
		}

		public void move() {
			setDir(0,0);
			if (getKey(key_shoot)) {
				woolColor = "blackWool";
				paint();
			}
			if (getKey(key_left) && x > xspeed){
				xdir = -1;
			}
			if (getKey(key_right) && x < pfWidth() - 32 - yspeed){
				xdir = 1;
			}
		}

		public void hit(JGObject o) {
			if (and(o.colid, 2) || and(o.colid, 3)){
				lives -= 1;
				gameOver();
			}
		}
	}

	public class Fireball extends JGObject {
		public Fireball() {
			super(
					"fireball",
					true,
					random(0, 300, 20),
					0,
					3,
					"bary",
					random(-1,1,1),
					random(-1,1,1),
					random(-1,1,1),
					random(-1,1,1),
					-3
					);
		}
		
		public void move() {
			y += 1;
		}
		
		public void paint(){
			setColor(JGColor.yellow);
			drawOval(x+4,y+4,8,8,true,true);
			setColor(JGColor.orange);
			drawOval(x+4,y+4,4,4,true,true);	
		}
	}
}
