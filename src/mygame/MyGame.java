package mygame;
import jgame.*;
import jgame.platform.*;

public class MyGame extends StdGame {

	int key_shoot = 70;
	int key_blue = 68;
	int key_green = 83;
	int key_red = 65;
	
	int red = 0;
	int green = 0;
	int blue = 0;
	
	double playerX = 10;
	double playerY = pfHeight() - 10;
	
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
		setCanvasSettings(40, 10, 8, 8, null, new JGColor(100, 100, 100), null);
	}

	public void initGame() {
		defineMedia("mygame.tbl");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		startgame_ingame = true;
	}

	public void initNewLife() {
		removeObjects(null,0);
		new Player(10, pfHeight() - 10, 5);
	}

	public void startGameOver() {
		removeObjects(null,0);
	}

	public void doFrameInGame() {
		moveObjects();
		checkCollision(2,1);
		if (getKey(key_up) || getKey(key_down)){									// make sure you have dark (up) or light (down)
			if (getKey(key_red) || getKey(key_green) || getKey(key_blue)){			// make sure you're hitting either red, green or blue
				
				int boolRed = getKey(key_red) ? 1 : 0;								// get the integer values of the booleans for color rendering
				int boolGreen = getKey(key_blue) ? 1 : 0;
				int boolBlue = getKey(key_green) ? 1 : 0;
				int boolDark = getKey(key_up) ? 1 : 0;
				
				red = (boolRed * 255) - (boolRed * boolDark * 100); 				// if the dark key is hit then woolson turns into a darker color
				green = (boolGreen * 255) - (boolGreen * boolDark * 100);
				blue = (boolBlue * 255) - (boolBlue * boolDark * 100);
				
				System.out.println("red " + red + " green " + green + " blue " + blue);
				new Player(playerX, playerY, 5);
			}
			
		}
		if (checkTime(0, 800, (int) random(25, 75)))						// randomly generates a wolf within the level
			new Enemy(); 													// wolves get produced faster as levels increase
		if (gametime >= 800 && countObjects("wolf", 0) == 0) {				// gametimes goes for 800
			levelDone();
		}
	}

	public void incrementLevel() {
	}

	JGFont scoring_font = new JGFont("Arial",0,8);

	public class Enemy extends JGObject {
		int[] color = {							// initialize colors
				255 * (int)random(0,1,1),
				255 * (int)random(0,1,1),
				255 * (int)random(0,1,1)
				};
		
		public Enemy() {
			super("wolf", true, pfWidth() - 10, pfHeight() - 10, 2, stage%2==1 ? "block" : "ball", random(-1,1), 0, -2 );
			for (int i = 0; x < color.length; i++){		// initlalize darker colors
				if (color[i] == 255) {
					color[i] = color[i] - (100 * (int)random(0,1,1));
				}
			}
		}

		public void paint() {
			setColor(new JGColor(color[0], color[1], color[2]));
			drawRect(x, y, 16, 16, true, true);
		}

		public void move() {
			timer += gamespeed;
			if (getKey(key_shoot)) {
				hit(this);
			}
			x -= 1;
		}

		public void hit(JGObject o) {
			remove();
			o.remove();
			score += 5;
		}
	}

	public class Player extends JGObject {
		public Player(double x, double y, double speed) {
			super("woolson", true, x, y, 1, "shipu", 0, 0, speed, speed, -1);
		}
		
		public void paint() {			
			setColor(new JGColor(red, blue, green));
			drawOval(x, y, 16, 16, true, true);
		}

		public void move() {
			setDir(0,0);
			if (getKey(key_left) && x > xspeed){
				xdir = -1;
			}
			if (getKey(key_right) && x < pfWidth()-32-yspeed){
				xdir = 1;
			}
		}

		public void hit(JGObject o) {
			System.out.print("AJKSLD");
			if (and(o.colid, 2)){
				gameOver();
			}
		}
	}
}
