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
				int boolGreen = getKey(key_green) ? 1 : 0;
				int boolBlue = getKey(key_blue) ? 1 : 0;
				int boolDark = getKey(key_up) ? 1 : 0;

				red = (boolRed * 255) - (boolRed * boolDark * 100); 				// if the dark key is hit then woolson turns into a darker color
				green = (boolGreen * 255) - (boolGreen * boolDark * 100);
				blue = (boolBlue * 255) - (boolBlue * boolDark * 100);

				new Player(playerX, playerY, 5);
			}

		}

		if (checkTime(0, 200, (int) random(25, 75)))						// randomly generates a wolf within the level
			new Enemy(); 													// wolves get produced faster as levels increase

		if (gametime >= 200 && countObjects("wolf", 0) == 0) {				// gametime goes for 500
			levelDone();
		}
	}

	public void incrementLevel() {
		level += 1;
		red = 0;
		green = 0;
		blue = 0;
	}

	JGFont scoring_font = new JGFont("Arial",0,4);

	public class Enemy extends JGObject {
		int[] color = {0,0,0};							// initialize colors

		public Enemy() {
			super(
					"wolf",
					true,
					pfWidth() - 10,
					pfHeight() - 10,
					2,
					stage%2==1 ? "block" : "ball",
							random(-1,1),
							0,
							-2
					);

			if (level == 0) {													// only light r, g, b
				color[(int)random(0,2,1)] = 255;								// only one of either r, b, g needs to be 255
			} else if (level == 1) {											// only light r, g, b and dark r, g, b
				color[(int)random(0,2,1)] = 255 - (100 * (int)random(0,1,1));
			} else if (level == 2) {											// add on light magenta, cyan, yellow
				color[(int)random(0,1,1)] = 255;								// pick two from r, g, b
				color[(int)random(0,1,1)*2] = 255;
			} else if (level == 3) {											// dark magenta, cyan, yellow
				int dark = 100 * (int)random(0,1,1);
				color[(int)random(0,1,1)] = 255 - dark;					
				color[(int)random(0,1,1)*2] = 255 - dark;
			}
		}

		public void paint() {
			setColor(new JGColor(color[0], color[1], color[2]));
			drawRect(x, y, 16, 16, true, true);
		}

		public void move() {
			timer += gamespeed;
			if (getKey(key_shoot)) {

				if (red == color[0] && green == color[1] && blue == color[2]){
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

	public class Player extends JGObject {
		public Player(double x, double y, double speed) {
			super(
					"woolson",
					true,
					x,
					y,
					1,
					"shipu",
					0,
					0,
					speed,
					speed,
					-1
					);
		}

		public void paint() {			
			setColor(new JGColor(red, green, blue));
			drawOval(x, y, 16, 16, true, true);
		}

		public void move() {
			setDir(0,0);
			if (getKey(key_shoot)) {
				red = 0;
				green = 0;
				blue = 0;
				paint();
			}
			if (getKey(key_left) && x > xspeed){
				xdir = -1;
			}
			if (getKey(key_right) && x < pfWidth()-32-yspeed){
				xdir = 1;
			}
		}

		public void hit(JGObject o) {
			if (and(o.colid, 2)){
				gameOver();
			}
		}
	}
}
