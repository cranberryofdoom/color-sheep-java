package mygame;
import jgame.*;
import jgame.platform.*;

public class MyGame extends StdGame {

	int key_shoot = 70;
	int key_blue = 68;
	int key_green = 83;
	int key_red = 65;

	String woolColor;

	String[] colors = {
			"red", "green", "blue",
			"darkRed", "darkGreen", "darkBlue",
			"yellow", "cyan", "magenta",
			"darkYellow", "darkCyan", "darkMagenta",
			"white", "grey", "black"
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
		woolColor = "blackWool";
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

		// cheat to finish the game
		if (getKey('Q')) {															
			gameOver();
		}

		// cheat to skip the level
		if (getKey('T')){
			removeObjects(null,2);											
			levelDone();
		}
		
//		if (getKey(key_down)){			// light
//			if (getKey(key_red) && ){
//				
//				
//			}
//		} else if (getKey(key_up)){ 	// dark
//			
//		}

		if (level == 5 && checkTime(0,6,5)) {
			new Boss();
		} else if (level != 5 && checkTime(0, 200, (int) random(25, 50))){			// randomly generates a wolf within the level
			new Enemy();										
		}

		if (gametime >= 200 && (countObjects("wolf", 0) == 0)) {
			levelDone();
		}

		if (level == 5 && gametime >= 200 && (countObjects("megaWolf", 0) == 0)){
			gameOver();
		}
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
			case 1:
				furColor = random(0,5,1);
			case 2:
				furColor = random(0,8,1);
			case 3:
				furColor = random(0,11,1);
			case 4:
				furColor = random(0,14,1);
			default:
				furColor = random(0,14,1);
			}
		}

		public void paint() {
			drawImage(x, y, colors[furColor] + "Fur", true);
		}

		public void move() {
			timer += gamespeed;
			if (getKey(key_shoot)) {
				if (colors[furColor] == woolColor){
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
					0,
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
				if (megaFurColor == woolColor){
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
			drawImage(x, y, woolColor, true);
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
			if (and(o.colid, 2)){
				lives -= 1;
				gameOver();
			}
		}
	}
}
