/*
 * File: Breakout.java
 * -------------------
 *
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

//import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60; 
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/* Instance variables */
	private GRect paddle; 
	private GOval ball;
	private int numberOfBricks = 0;
	private int numberOfTurns = NTURNS;
	private int paddle_position_y = APPLICATION_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setupGame();
		playGame();
	}
	
	private void setupGame(){
		buildBricks();
		createPaddle();
	}
	
	private void buildBricks(){
		int brick_position_y = BRICK_Y_OFFSET;
		int color_id = 0;
		for(int i = 0; i < NBRICK_ROWS; i++){
			layRow(brick_position_y, color_id);
			brick_position_y = brick_position_y + BRICK_SEP + BRICK_HEIGHT;
			color_id += 1;
		}
	}
	
	private void layBrick(int brick_position_x, int brick_position_y, int color_id){
		Color color = getBrickColor(color_id);
		GRect brick = new GRect(brick_position_x, brick_position_y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(color);
		brick.setFillColor(color);
		brick.setFilled(true);
		add(brick);
		numberOfBricks++;
	}
	
	private Color getBrickColor(int color_id){
		Color color = null;
		switch (color_id) {
			case 0:
			case 1:
				color = Color.RED;
				break;
			case 2:
			case 3:
				color = Color.ORANGE;
				break;
			case 4:
			case 5:
				color = Color.YELLOW;
				break;
			case 6:
			case 7:
				color = Color.GREEN;
				break;
			case 8:
			case 9:
				color = Color.CYAN;
				break;
		}
		return color;
	}
	
	private void layRow(int row_position_y, int color_id){
		int x_margin = ( (WIDTH - ( (BRICK_WIDTH * NBRICKS_PER_ROW ) + ( (NBRICKS_PER_ROW -1) * BRICK_SEP ) ) ) /2 );
		int brick_position_x = x_margin;
		int brick_position_y = row_position_y;
		for(int i = 0; i < NBRICKS_PER_ROW; i++){
			layBrick(brick_position_x, brick_position_y, color_id);
			brick_position_x = brick_position_x + BRICK_SEP + BRICK_WIDTH;
		}
	}
	
	private void createPaddle(){
		paddle = new GRect((WIDTH-PADDLE_WIDTH)/2, paddle_position_y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	
	
	/** Called on mouse move to reposition the object */
	public void mouseMoved(MouseEvent e) {
		if (e.getX() >= PADDLE_WIDTH/2 && e.getX() <= WIDTH - PADDLE_WIDTH/2 ) {
				paddle.setLocation(e.getX() - PADDLE_WIDTH/2, paddle_position_y);
		}
	}
	
	private void playGame(){
		createBall();
		moveBall();
	}
	
	private void createBall(){
		ball = new GOval(WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	private void moveBall(){
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = +3.0;
		while(ball.getY() < HEIGHT-2*BALL_RADIUS){
			checkforWall();
			ball.move(vx, vy);
			pause(15);
			checkCollision();
		} 
		if(numberOfBricks > 0){
			newTurn();
		}
	}
	
	private void checkforWall(){
		if(ball.getY() <= 0.0){
			vy = -vy;
		} else if(ball.getX() >= WIDTH-2*BALL_RADIUS || ball.getX() <= 0.0){
			vx = -vx;
		}
	}

	private void newTurn(){
		--numberOfTurns;
		if(numberOfTurns == 0){
			endGame("You lost!");
		} else {
			remove(ball);
			newBall();
		}
	}
	
	private void checkCollision(){
		GObject collider = getCollidingObject(ball.getX(), ball.getY());
		GObject collider2 = getCollidingObject(ball.getX()+(2*BALL_RADIUS), ball.getY());
		GObject collider3 = getCollidingObject(ball.getX(), ball.getY()+(2*BALL_RADIUS));
		GObject collider4 = getCollidingObject(ball.getX()+(2*BALL_RADIUS), ball.getY()+(2*BALL_RADIUS));
		if(collider != null){
			handleCollision(collider);
		} else if (collider2 !=null) {
			handleCollision(collider2);
		} else if (collider3 !=null) {
			handleCollision(collider3);
		} else if (collider4 !=null) {
			handleCollision(collider4);
		}	
	}
	
	private void handleCollision(GObject collider){
		if(collider==paddle){
			if(ball.getY()>(HEIGHT-PADDLE_Y_OFFSET-PADDLE_HEIGHT-2*BALL_RADIUS)){
				ball.setLocation(ball.getX(), HEIGHT-PADDLE_Y_OFFSET-PADDLE_HEIGHT-2*BALL_RADIUS);
			}
			vy = -vy;
		} else {
			remove(collider);
			vy = -vy;
			checkBricks();
		}
	}
	
	private GObject getCollidingObject(double x, double y){
		GObject element = getElementAt(x, y);
		return element;
	}
	
	private void checkBricks(){
		--numberOfBricks;
		if(numberOfBricks==0){
			remove(ball);
			endGame("You won!");
		}
	}
	
	private void newBall(){
		createBall();
		moveBall();
	}
	
	private void endGame(String message){
		remove(ball);
		remove(paddle);
		add(new GLabel(message), WIDTH/2, HEIGHT/2);
	}
}
