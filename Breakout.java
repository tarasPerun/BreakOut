/*
 * File: Breakout.java
 * ------------------------
 * The program implements the classic arcade game "Breakout".
 * The game consists of three attempts. At the beginning of each attemp
 * the ball is launched at a random angle from the racket. This ball bounces
 * off the edges of the world and from the racket, according to the laws
 * of physics: the angle of incidence is equal to the angle of reflection.
 *
 * The game continues until one of two conditions is met:
 * 1. The ball hits the bottom wall, which means that the player hit him
 * with a racket. In this case, the attempt ends and the next ball is
 * launched  as soon as the player clicks the mouse. If all 3 attempts
 * are exhausted - the player is considered to have lost.
 * 2. The last brick is broken - the game ends with the victory of the player.
 */

package com.shpp.p2p.cs.tperun.assignment4;

import acm.graphics.*;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * The class contains variables and methods that make the game "Breakout".
 */
public class Breakout extends WindowProgram {
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 30;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static final int NBRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static final int NBRICK_ROWS = 10;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 3;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 10;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static final int NUMBER_TURNS = 3;

    // The initial vertical velocity of ball.
    private static final double VERTICAL_VELOCITY = 5.0;

    // The animation delay time in milliseconds.
    private static final double DELAY_TIME = 15;

    // class variables containing links to graphic objects.
    GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
    GOval ball = createBall();
    GLabel label = new GLabel("Click to start");
    GObject collider = null;

    // runs the program
    @Override
    public void run() {
        // Registering for mouse events on specified area
        addMouseListeners();
        // Creating the graphic environment
        createGameWorld();
        // Ball movement and collision handling
        moveBallAndCollisionsControl();
    }

    /**
     * Creates the environment for the game and waits
     * for a mouse click to continue
     */
    private void createGameWorld() {
        // Builds an environment for the game
        createPaddle();
        buildBrickWall();
        // outputs the inviting to game start
        putLabel();
        // Stops program and waits for a mouse click to continue
        waitForClick();
    }

    /**
     * Outputs the label with specified content, color and location
     */
    private void putLabel() {
        label.setFont("Default-20");
        label.setColor(Color.BLACK);
        add(label, getWidth() / 2. - label.getWidth() / 2,
                getHeight() / 2. - label.getHeight() / 2);
    }

    /**
     * Creates a ball with the specified parameters and put it into
     * the center of window.
     *
     * @return The graphic object jf class GOval.
     */
    private GOval createBall() {
        GOval b = new GOval(getWidth() / 2. - BALL_RADIUS,
                getHeight() / 2. - BALL_RADIUS / 2.,
                2 * BALL_RADIUS, 2 * BALL_RADIUS);
        b.setFilled(true);
        b.setFillColor(Color.BLACK);
        return b;
    }

    /**
     * Sets a random initial ball speed.
     *
     * @param yVelocity The vertical The vertical ball velocity.
     * @return Sets a random initial ball speed
     */
    private double setRandomHorizontalVelocity(double yVelocity) {
        RandomGenerator rgen = RandomGenerator.getInstance();
        // Sets a random ball horizontal velocity.
        double xVelocity = rgen.nextDouble(1.0, yVelocity);
        // Changes the sign of horizontal velocity randomly.
        if (rgen.nextBoolean(0.5)) {
            xVelocity = -xVelocity;
        }
        return xVelocity;
    }

    /**
     * Describes ball's movement around the world and collision handling.
     */
    private void moveBallAndCollisionsControl() {
        // The horizontal and vertical ball velocity, respectively.
        double vx, vy;
        // The remaining bricks counters and the number of ball losses, respectively.
        int bricksCounter = NBRICK_ROWS * NBRICKS_PER_ROW;
        int crashCounter = 0;

        remove(label);
        add(ball);
        // launches the ball in a random direction
        vx = setRandomHorizontalVelocity(VERTICAL_VELOCITY);
        vy = VERTICAL_VELOCITY;

        while (true) {
            ball.move(vx, vy);
            pause(DELAY_TIME);

            /*
             * Check collision. Determination of the collision fact
             * only by the bottom points of the ball when it moves down
             * and only by the upper points when it moves up.
             */
            if (vy < 0) {
                collider = getCollidingObject(ball.getX(),
                        ball.getY());
            } else {
                collider = getCollidingObject(ball.getX() + 2 * BALL_RADIUS,
                        ball.getY() + 2 * BALL_RADIUS);
            }
            if (collider != null) {
                // Checking if the collider is a paddle
                if (collider == paddle) {
                    vy = -vy;
                } else // collider is brick
                {
                    remove(collider);
                    bricksCounter--;
                    // Game end check
                    if (bricksCounter <= 0) {
                        changeLabelAndWaitToContinue("You won!!!");
                        break;
                    }
                    vy = -vy;
                }
            }
            // Checking the bottom line
            if ((ball.getY() + 2 * BALL_RADIUS) > (getHeight() - 2 * BALL_RADIUS)) {
                if (crashCounter < NUMBER_TURNS - 1) {
                    changeLabelAndWaitToContinue("Try again!");
                    crashCounter++;
                    vx = setRandomHorizontalVelocity(VERTICAL_VELOCITY);
                    vy = VERTICAL_VELOCITY;
                    continue;
                } else {
                    changeLabelAndWaitToContinue("You lost!");
                    break;
                }
            }
            // checking the top and side walls
            if (ball.getY() < 0) {
                vy = -vy;
            }
            if ((ball.getX() > getWidth() - 2 * BALL_RADIUS) || (ball.getX() < 0)) {
                vx = -vx;
            }
        }
        label.setLabel("Game over!");
        putLabel();
    }

    /**
     * Changes the content of previous label, puts it into the same
     * place and waits for mouse click to continue.
     *
     * @param string The content of label.
     */
    private void changeLabelAndWaitToContinue(String string) {
        remove(ball);
        label.setLabel(string);
        putLabel();
        waitForClick();
        remove(label);
    }

    /**
     * Creates a paddle in the form of a black rectangular object
     * with the specified parameters
     */
    private void createPaddle() {
        paddle.setFilled(true);
        paddle.setFillColor(Color.BLACK);
        add(paddle, getWidth() / 2. - PADDLE_WIDTH / 2.,
                getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
    }

    /**
     * Returns an existing graphic object at a point with
     * coordinates x and y. If there is nothing ,return null.
     *
     * @param x The x-coordinate of graphic object.
     * @param y The y-coordinate of graphic object.
     * @return The graphic object or null.
     */
    private GObject getCollidingObject(double x, double y) {

        /* Check several selected points of the ball for collisions with objects. */
        // The upper left point.
        if (getElementAt(x, y) != null) {
            return getElementAt(x, y);
        }
        // The upper right point.
        if (getElementAt(x + 2 * BALL_RADIUS, y) != null) {
            return getElementAt(x, y);
        }
        // The bottom left point.
        if (getElementAt(x, y + 2 * BALL_RADIUS) != null) {
            return getElementAt(x, y);
        }
        // The bottom right point.
        if (getElementAt(x + 2 * BALL_RADIUS,
                y + 2 * BALL_RADIUS) != null) {
            return getElementAt(x, y);
        }
        return null;
    }

    /**
     * Creates the brick wall with different colors of bricks
     */
    private void buildBrickWall() {
        // colors of brick rows set in the task
        Color[] colors = {Color.RED, Color.RED, Color.ORANGE, Color.ORANGE, Color.YELLOW,
                Color.YELLOW, Color.GREEN, Color.GREEN, Color.CYAN, Color.CYAN};

        for (int i = 0; i < NBRICK_ROWS; i++) {
            // If NBRICK_ROWS > 10 then the colours repeat
            buildBricksRow(colors[i % 10], BRICK_Y_OFFSET +
                    i * (BRICK_HEIGHT + BRICK_SEP));
        }
    }

    /**
     * Builds a brick row with specified color and
     * vertical position.
     *
     * @param color      the  bricks color in a row.
     * @param vertOffset the row offset from upper side of window.
     */
    private void buildBricksRow(Color color, double vertOffset) {
        for (int i = 0; i < NBRICKS_PER_ROW; i++) {
            createBrick(color, BRICK_SEP / 4. + i * (BRICK_WIDTH + BRICK_SEP),
                    vertOffset);
        }
    }

    /**
     * Creates brick with specified color, width and height
     * at a specified location (x, y).
     *
     * @param color The brick color.
     * @param x     The x-coordinate of brick upper left corner.
     * @param y     The y-coordinate of brick upper left corner.
     */
    private void createBrick(Color color, double x, double y) {
        GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        brick.setColor(color);
        brick.setFilled(true);
        brick.setFillColor(color);
        add(brick);
    }


    /**
     * Defines the actions to be taken with a mouse click
     *
     * @param mouseEvent An event which indicates that a mouse action occurred
     *                   in a component.
     */
    public void mouseClicked(MouseEvent mouseEvent) {
        //removes visible graphic object ball
        remove(ball);

        // places the ball in the center of the window
        add(ball, getWidth() / 2. - BALL_RADIUS,
                getHeight() / 2. - BALL_RADIUS / 2.);
    }

    /**
     * Controls the paddle with the mouse. As soon as user moves the mouse,
     * the paddle must move with the cursor and it is centered relative to the
     * cursor. The paddle always has the same coordinate on the Y axis, it never
     * moves up and down. When the user removes the mouse outside the screen -
     * the paddle remains completely on the screen.
     *
     * @param mouseEvent An event which indicates that a mouse action occurred
     *                   in a component.
     */
    public void mouseMoved(MouseEvent mouseEvent) {
        if ((mouseEvent.getX() > PADDLE_WIDTH / 2 - 5) &&
                (mouseEvent.getX() < getWidth() - PADDLE_WIDTH / 2 + 5)) {

            paddle.setLocation(mouseEvent.getX() - PADDLE_WIDTH / 2.,
                    getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
        }
    }
}









