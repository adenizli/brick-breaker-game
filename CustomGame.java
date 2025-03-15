/*
 * @author: Ahmet Sait Denizli
 * @version: 1.0
 * @description: DX-Ball Game Implementation | A simple brick breaker game implementation
 * @since: 2025-03-14
 */

import stdlib.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Random;

public class CustomGame {
    enum DIRECTION {LEFT,RIGHT}
    enum KEY_MAP {SPACE, LEFT_ARROW, RIGHT_ARROW}
    enum TYPOGRAPHY {TITLE, SUBTITLE, BODY}
    enum TYPOGRAPHY_ALIGNMENT {LEFT, RIGHT, CENTER}

    /*
     * @param args
     * @return void
     * @since 2025-03-14
     */
    public static void main(String[] args){
        // ===================================
        // ======== CANVAS SETTINGS ==========
        // ===================================
        final String CANVAS_TITLE = "BRICK BREAKER GAME";
        final int[] CANVAS_SIZE = {800,400};

        
        // ===================================
        // ======== BRICK VARIABLES ==========
        // ===================================
        final double BRICK_HALFWIDTH = 50;
        final double BRICK_HALFHEIGHT = 10;

        final Color[] BRICK_COLOR_DEFINITIONS = {
            new Color(46, 204, 113),
            new Color(52, 152, 219),
            new Color(255, 215, 0),
            new Color(72, 61, 139),
            new Color(199, 21, 133),
            new Color(255, 105, 180)
        };
        
        
        final double[][] brick_coordinates = {
            {150, 350}, {250, 350}, {550, 350}, 
            {150, 330}, {250, 330}, {550, 330}, {650, 330}, {750, 330}, 
            {50,310}, {150, 310}, {250, 310}, {350, 310}, {450, 310}, {550, 310}, {650, 310}, 
            {50,290}, {150, 290}, {250, 290}, {350, 290}, {450, 290}, {550, 290}, {650, 290}, 
            {50,270}, {150, 270}, {250, 270}, {350, 270}, {450, 270}, {550, 270},
            {50,250}, {350, 250}, {450, 250}, {550, 250},
            {150, 230}, {250, 230}, {350, 230}, {650, 230}, {750, 230},
            {50,210}, {150, 210}, {250, 210},  {450, 210}, {550, 210}, {650, 210}, {750, 210},
            {50,190}, {150, 190}, {250, 190}, {350, 190}, {450, 190}, {550, 190}, {650, 190}, {750, 190},
            {50,170}, {150, 170}, {250, 170}, {350, 170}, {450, 170}, {550, 170}, {650, 170},
            {150, 150}, {250, 150}, {450, 150}, {550, 150},
            
        };
        
        final Color[] brick_colors = new Color[brick_coordinates.length];
        
        

        // Brick status contains 0,1 values to indicate if the brick is destroyed or not
        final double[] BRICK_STATUS = new double[brick_coordinates.length];
        

        // ===================================
        // ======== PADDLE VARIABLES =========
        // ===================================
        final double PADDLE_HALFWIDTH = 60;
        final double PADDLE_HALFHEIGHT = 5;
        final double PADDLE_SPEED = 20;
        final double[] PADDLE_INITIAL_COORDS = {400, 5};
        

        // ===================================
        // ======== BALL VARIABLES ===========
        // ===================================
        final double BALL_RADIUS = 8;
        final double[] BALL_INITIAL_COORDS = {400,18};
        final double DIRECTION_ROTATION_SPEED = 1;
        final int DIRECTION_LENGTH = 150;
        // Ball magnitude of velocity
        final double BALL_SPEED = 5;
        final double INITIAL_ANGLE = 0;

        // ===================================
        // =======  HELPER VARIABLES =========
        // ===================================
        double latestKeyboardInput = new Date().getTime();

        // ===================================
        // ========  GAME VARIABLES ==========
        // ===================================
        double[] ballCoords = {BALL_INITIAL_COORDS[0], BALL_INITIAL_COORDS[1]};
        double[] ballVelocity = {Math.sin(Math.toRadians(INITIAL_ANGLE)) * BALL_SPEED, Math.cos(Math.toRadians(INITIAL_ANGLE)) * BALL_SPEED};
        double[] paddleCoords = {PADDLE_INITIAL_COORDS[0], PADDLE_INITIAL_COORDS[1]};

        // ===================================
        // ======  GAME STATE VARIABLE =======
        // ===================================
        // Game state variable is used as a cache mechanism 
        // to store the common changed values of the game
        // It also stores game status.
        // Order of the variables:
        // 0->Score, 
        // 1->Game State: 0->Initial, 1->Pre-Start, 2->Playing, 3->Game Over, 4->Paused, 5->Winner
        // 2->Angle,
        // 3->Latest Keyboard Input in Milliseconds
        double[] GAME_STATES = {0,0,INITIAL_ANGLE,latestKeyboardInput};
        
        // ===================================
        // ========  INFRASTRUCTURE ==========
        // ===================================
        canvasSetup(CANVAS_TITLE, CANVAS_SIZE);
        resetGameParams(GAME_STATES, ballCoords, ballVelocity, paddleCoords, brick_coordinates, brick_colors, BRICK_COLOR_DEFINITIONS, BRICK_STATUS, PADDLE_INITIAL_COORDS, BALL_SPEED, BALL_INITIAL_COORDS, INITIAL_ANGLE);

        // ===================================
        // ==========  LIFE CYCLE  ===========
        // ===================================
        while(true) {
            controller(GAME_STATES, ballCoords, ballVelocity, paddleCoords, brick_coordinates, brick_colors, BRICK_COLOR_DEFINITIONS, BRICK_STATUS, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT, CANVAS_SIZE, PADDLE_INITIAL_COORDS, BALL_SPEED, BALL_INITIAL_COORDS, INITIAL_ANGLE, DIRECTION_ROTATION_SPEED, PADDLE_SPEED);
            // Initialization
            if (GAME_STATES[1] == 0) init(CANVAS_SIZE);
            // Pre-Start
            else if (GAME_STATES[1] == 1) preStart(GAME_STATES, CANVAS_SIZE, ballCoords, BALL_RADIUS, brick_coordinates, BRICK_STATUS, brick_colors, BRICK_HALFWIDTH, BRICK_HALFHEIGHT, paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT, DIRECTION_LENGTH);
            // Playing
            else if (GAME_STATES[1] == 2) playing(GAME_STATES, CANVAS_SIZE, ballCoords, BALL_RADIUS, BALL_SPEED, brick_coordinates, BRICK_STATUS, brick_colors, BRICK_HALFWIDTH, BRICK_HALFHEIGHT, paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT, ballVelocity);
            // Game Over
            else if (GAME_STATES[1] == 3) gameOver(GAME_STATES, CANVAS_SIZE, ballCoords, BALL_RADIUS, brick_coordinates, BRICK_STATUS, brick_colors, BRICK_HALFWIDTH, BRICK_HALFHEIGHT, paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT);
            // Paused
            else if (GAME_STATES[1] == 4) paused(CANVAS_SIZE);
            // Winner
            else if (GAME_STATES[1] == 5) winnerWinnerChickenDinner(GAME_STATES, CANVAS_SIZE);
        }
    }

    // ===========================
    // ===== INFRASTRUCTURE ======
    // ===========================
    /*
     * @param CANVAS_TITLE: Title of the canvas
     * @param CANVAS_SIZE: Size of the canvas
     * @return void
     * @since 2025-03-14
     */
    private static void canvasSetup(String CANVAS_TITLE, int[] CANVAS_SIZE) {
        // Represents the number of units in the x-axis
        final int x_scale = 800;
        // Represents the number of units in the y-axis
        final int y_scale = 400;

        StdDraw.setCanvasSize(CANVAS_SIZE[0], CANVAS_SIZE[1]);
        StdDraw.setTitle(CANVAS_TITLE);
        StdDraw.setXscale(0.0, x_scale);
        StdDraw.setYscale(0.0, y_scale);
        StdDraw.enableDoubleBuffering();
    }

    /*
     * This method is used to get keyboard inputs and update the game stat
     * 
     * @param GAME_STATES: Game state variables
     * @param ballCoords: Coordinates of the ball
     * @param ballVelocity: Velocity of the ball
     * @param paddleCoords: Coordinates of the paddle
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_COLORS: Colors of the bricks
     * @param BRICK_COLOR_DEFINITIONS: Color definitions for the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * @param CANVAS_SIZE: Size of the canvas
     * @param PADDLE_INITIAL_COORDS: Initial coordinates of the paddle
     * @param BALL_SPEED: Speed of the ball
     * @param BALL_INITIAL_COORDS: Initial coordinates of the ball
     * @param INITIAL_ANGLE: Initial angle of the ball
     * @param DIRECTION_ROTATION_SPEED: Rotation speed of the ball
     * @param PADDLE_SPEED: Speed of the paddle
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void controller(
        double[] GAME_STATES,
        double[] ballCoords,
        double[] ballVelocity,
        double[] paddleCoords,
        double[][] BRICK_COORDS,
        Color[] BRICK_COLORS,
        Color[] BRICK_COLOR_DEFINITIONS,
        double[] BRICK_STATUS,
        double PADDLE_HALFWIDTH,
        double PADDLE_HALFHEIGHT,
        int[] CANVAS_SIZE,
        double[] PADDLE_INITIAL_COORDS,
        double BALL_SPEED,
        double[] BALL_INITIAL_COORDS,
        double INITIAL_ANGLE,
        double DIRECTION_ROTATION_SPEED,
        double PADDLE_SPEED
    ) {
        // Get keyboard inputs
        final boolean isSpacePressed = StdDraw.isKeyPressed(KeyEvent.VK_SPACE);
        final boolean isLeftArrowPressed = StdDraw.isKeyPressed(KeyEvent.VK_LEFT);
        final boolean isRightArrowPressed = StdDraw.isKeyPressed(KeyEvent.VK_RIGHT);

        // Handle space key
        // Is Safely Pressed is used to check 
        // if the space key is pressed and is not 
        // pressed in the last 100 milliseconds to
        // prevent the space key from executing multiple times

        if (isSpacePressed && isSafelyPressed(GAME_STATES, KEY_MAP.SPACE)) {
            // From Initial to Pre-Start   
            if (GAME_STATES[1] == 0) GAME_STATES[1] = 1;
            // From Pre-Start to Playing
            else if (GAME_STATES[1] ==  1) GAME_STATES[1] = 2;
            // From Playing to Paused
            else if (GAME_STATES[1] ==  2) GAME_STATES[1] = 4;
            // From Game Over or Winner to Pre-Start
            // From Winner to Pre-Start
            else if (GAME_STATES[1] == 3 || GAME_STATES[1] == 5) {
                GAME_STATES[1] = 1;
                resetGameParams(GAME_STATES, ballCoords, ballVelocity, paddleCoords, BRICK_COORDS, BRICK_COLORS, BRICK_COLOR_DEFINITIONS, BRICK_STATUS, PADDLE_INITIAL_COORDS, BALL_SPEED, BALL_INITIAL_COORDS, INITIAL_ANGLE);
            } 
            // From Paused to Playing
            else if (GAME_STATES[1] == 4) GAME_STATES[1] = 2;
        }

        // Handle left arrow key
        if (isLeftArrowPressed) {
            // From Pre-Start to Rotate Angle Left
            if (GAME_STATES[1] == 1) rotateDirection(GAME_STATES, ballVelocity, BALL_SPEED, DIRECTION_ROTATION_SPEED, DIRECTION.LEFT);
            // From Playing to Move Paddle Left
            else if (GAME_STATES[1] == 2) movePaddle(paddleCoords, PADDLE_HALFWIDTH, PADDLE_SPEED, CANVAS_SIZE, DIRECTION.LEFT);
        }

        // Handle right arrow key
        else if (isRightArrowPressed) {
            // From Pre-Start to Rotate Angle Right
            if (GAME_STATES[1] == 1) rotateDirection(GAME_STATES, ballVelocity, BALL_SPEED, DIRECTION_ROTATION_SPEED, DIRECTION.RIGHT);
            // From Playing to Move Paddle Right
            else if (GAME_STATES[1] == 2) movePaddle(paddleCoords, PADDLE_HALFWIDTH, PADDLE_SPEED, CANVAS_SIZE, DIRECTION.RIGHT);
        }
    }

    // ===========================
    // ===== GAME ENGINE =========
    // ===========================

    /*
     * This method is used to run the game engine
     * 
     * @param GAME_STATES: Game state variables
     * @param ballCoords: Coordinates of the ball
     * @param ballVelocity: Velocity of the ball
     * @param BALL_SPEED: Speed of the ball
     * @param BALL_RADIUS: Radius of the ball
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param BRICK_HALFWIDTH: Half width of the bricks
     * @param BRICK_HALFHEIGHT: Half height of the bricks
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * @param CANVAS_SIZE: Size of the canvas
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void runGameEngine(
        double[] GAME_STATES,
        double[] ballCoords,
        double[] ballVelocity,
        double BALL_SPEED,
        double BALL_RADIUS,
        double[][] BRICK_COORDS, 
        double[] BRICK_STATUS, 
        double BRICK_HALFWIDTH,
        double BRICK_HALFHEIGHT,
        double[] paddleCoords, 
        double PADDLE_HALFWIDTH,
        double PADDLE_HALFHEIGHT,
        int[] CANVAS_SIZE
    ) {
        moveBall(ballCoords, ballVelocity);
        checkCollision(GAME_STATES, ballCoords, ballVelocity, BALL_SPEED, BALL_RADIUS, BRICK_COORDS, BRICK_STATUS, BRICK_HALFWIDTH, BRICK_HALFHEIGHT, paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT, CANVAS_SIZE);
    }

    /*
     * This method is used to reset the game parameters to initial state
     * 
     * @param GAME_STATES: Game state variables
     * @param ballCoords: Coordinates of the ball
     * @param ballVelocity: Velocity of the ball
     * @param paddleCoords: Coordinates of the paddle
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_COLORS: Colors of the bricks
     * @param BRICK_COLOR_DEFINITIONS: Color definitions for the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param PADDLE_INITIAL_COORDS: Initial coordinates of the paddle
     * @param BALL_SPEED: Speed of the ball
     * @param BALL_INITIAL_COORDS: Initial coordinates of the ball
     * @param INITIAL_ANGLE: Initial angle of the ball
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void resetGameParams(
        double[] GAME_STATES,
        double[] ballCoords,
        double[] ballVelocity,
        double[] paddleCoords,
        double[][] BRICK_COORDS,
        Color[] BRICK_COLORS,
        Color[] BRICK_COLOR_DEFINITIONS,
        double[] BRICK_STATUS,
        double[] PADDLE_INITIAL_COORDS,
        double BALL_SPEED,
        double[] BALL_INITIAL_COORDS,
        double INITIAL_ANGLE
    ) {
        // Reset game state to initial
        GAME_STATES[1] = 0;
        // Reset ball coordinates to initial
        ballCoords[0] = BALL_INITIAL_COORDS[0];
        ballCoords[1] = BALL_INITIAL_COORDS[1];
        // Reset paddle coordinates to initial
        paddleCoords[0] = PADDLE_INITIAL_COORDS[0];
        paddleCoords[1] = PADDLE_INITIAL_COORDS[1];
        // Reset ball velocity to initial
        ballVelocity[0] = Math.sin(Math.toRadians(INITIAL_ANGLE)) * BALL_SPEED;
        ballVelocity[1] = Math.cos(Math.toRadians(INITIAL_ANGLE)) * BALL_SPEED;
        // Reset score to 0
        GAME_STATES[0] = 0;
        // Reset brick status to initial
        for (int i = 0; i < BRICK_COORDS.length; i++) BRICK_STATUS[i] = 1;    

        Random rand = new Random();
        for (int i = 0; i < BRICK_COORDS.length; i++) {
            int colorIndex = rand.nextInt(BRICK_COLOR_DEFINITIONS.length);
            BRICK_COLORS[i] = BRICK_COLOR_DEFINITIONS[colorIndex];
        }
    }

    /*
     * This method is used to check if all the bricks are destroyed
     * 
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param GAME_STATES: Game state variables
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void checkAllBricksDestroyed(double[][] BRICK_COORDS, double[] BRICK_STATUS, double[] GAME_STATES) {
        for (int i = 0; i < BRICK_COORDS.length; i++) {
            if (BRICK_STATUS[i] == 1) return;
        }
        // If all the bricks are destroyed, set the game state to winner
        GAME_STATES[1] = 5;
    }

    /*
     * This method is used to move the ball
     * 
     * @param ballCoords: Coordinates of the ball
     * @param ballVelocity: Velocity of the ball
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void moveBall(double[] ballCoords, double[] ballVelocity) {
        // Move the ball in the x direction
        ballCoords[0] += ballVelocity[0];
        // Move the ball in the y direction
        ballCoords[1] += ballVelocity[1];
    }

    /*
     * This method is used to move the paddle
     * 
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_SPEED: Speed of the paddle
     * @param CANVAS_SIZE: Size of the canvas
     * @param direction: Direction of the paddle
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void movePaddle(double[] paddleCoords, double PADDLE_HALFWIDTH, double PADDLE_SPEED, int[] CANVAS_SIZE, DIRECTION direction) {
        // Check if the paddle can move left, if not, do nothing
        boolean isLeftPaddleMoveValid = paddleCoords[0] - PADDLE_HALFWIDTH > 0;
        // Check if the paddle can move right, if not, do nothing
        boolean isRightPaddleMoveValid = paddleCoords[0] + PADDLE_HALFWIDTH < CANVAS_SIZE[0];
        
        // Move the paddle left
        if (direction == DIRECTION.LEFT && isLeftPaddleMoveValid) paddleCoords[0] -= PADDLE_SPEED;
        // Move the paddle right
        else if (direction == DIRECTION.RIGHT && isRightPaddleMoveValid) paddleCoords[0] += PADDLE_SPEED;
    }

    /*
     * This method is used to rotate the direction of the ball
     * 
     * @param GAME_STATES: Game state variables
     * @param ballVelocity: Velocity of the ball
     * @param BALL_SPEED: Speed of the ball
     * @param DIRECTION_ROTATION_SPEED: Rotation speed of the ball
     * @param rotationDirection: Direction of the rotation
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void rotateDirection(double[] GAME_STATES, double[] ballVelocity, double BALL_SPEED, double DIRECTION_ROTATION_SPEED, DIRECTION rotationDirection) {
        // Check if the ball can rotate left, if not, do nothing
        boolean isLeftRotationValid = GAME_STATES[2] >= -89;
        // Check if the ball can rotate right, if not, do nothing
        boolean isRightRotationValid = GAME_STATES[2] <= 89;

        // Rotate the ball left
        if (rotationDirection == DIRECTION.LEFT && isLeftRotationValid) GAME_STATES[2] -= DIRECTION_ROTATION_SPEED;
        // Rotate the ball right
        else if (rotationDirection == DIRECTION.RIGHT && isRightRotationValid) GAME_STATES[2] += DIRECTION_ROTATION_SPEED;
        // Update the ball velocity
        updateBallVelocity(GAME_STATES, ballVelocity, BALL_SPEED);
    }

    /*
     * This method is used to update the ball velocity
     * on pre-start angle selection.
     * 
     * @param GAME_STATES: Game state variables
     * @param ballVelocity: Velocity of the ball
     * @param BALL_SPEED: Speed of the ball
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void updateBallVelocity(double[] GAME_STATES, double[] ballVelocity, double BALL_SPEED) {
        // Update the ball velocity by using the pre-start angle
        ballVelocity[0] = Math.sin(Math.toRadians(GAME_STATES[2])) * BALL_SPEED;
        ballVelocity[1] = Math.cos(Math.toRadians(GAME_STATES[2])) * BALL_SPEED;
    }

    /*
     * This method is used to check if the ball collided with any surface
     * This method also handles the logic for the changing velocity of the ball
     * Depending on the various conditions, the velocity of the ball will be updated
     * This method also handles the logic for the scoring of the game
     * # of point per brick is also defined in this method
     * 
     * @param GAME_STATES: Game state variables
     * @param ballCoords: Coordinates of the ball
     * @param ballVelocity: Velocity of the ball
     * @param BALL_SPEED: Speed of the ball
     * @param BALL_RADIUS: Radius of the ball
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param BRICK_HALFWIDTH: Half width of the bricks
     * @param BRICK_HALFHEIGHT: Half height of the bricks
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * @param CANVAS_SIZE: Size of the canvas
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void checkCollision(
        double[] GAME_STATES,
        double[] ballCoords,
        double[] ballVelocity,
        double BALL_SPEED,
        double BALL_RADIUS, 
        double[][] BRICK_COORDS,
        double[] BRICK_STATUS,
        double BRICK_HALFWIDTH,
        double BRICK_HALFHEIGHT,
        double[] paddleCoords,
        double PADDLE_HALFWIDTH,
        double PADDLE_HALFHEIGHT,
        int[] CANVAS_SIZE
    ) {
        final int POINT_PER_BRICK = 10;

        // ===========================
        // ===== Canvas & Ball =======
        // ===========================
        boolean isBallCollidedWithLeftCanvas = ballCoords[0] - BALL_RADIUS <= 0;
        boolean isBallCollidedWithRightCanvas = ballCoords[0] + BALL_RADIUS >= CANVAS_SIZE[0];
        boolean isBallCollidedWithTopCanvas = ballCoords[1] + BALL_RADIUS >= CANVAS_SIZE[1];
        boolean isBallCollidedWithBottomCanvas = ballCoords[1] - BALL_RADIUS <= 0;
        
        if(isBallCollidedWithLeftCanvas || isBallCollidedWithRightCanvas) ballVelocity[0] = -ballVelocity[0];
        if(isBallCollidedWithTopCanvas) ballVelocity[1] = -ballVelocity[1];
        if(isBallCollidedWithBottomCanvas) GAME_STATES[1] = 3;
        
        // ===========================
        // ==== Paddle Collision =====
        // ===========================

        // Check if the ball is aligned with the paddle
        boolean isPaddleXaligned = ballCoords[0] >= paddleCoords[0] - PADDLE_HALFWIDTH && ballCoords[0] <= paddleCoords[0] + PADDLE_HALFWIDTH;
        boolean isPaddleYaligned = ballCoords[1] >= paddleCoords[1] - PADDLE_HALFHEIGHT && ballCoords[1] <= paddleCoords[1] + PADDLE_HALFHEIGHT;

        // Check if the ball is collided with the paddle
        boolean isPaddleYcollided = Math.abs(paddleCoords[1] - ballCoords[1]) <= PADDLE_HALFHEIGHT + BALL_RADIUS;
        boolean isPaddleXcollided = Math.abs(paddleCoords[0] - ballCoords[0]) <= PADDLE_HALFWIDTH + BALL_RADIUS;

        // If and only if both x and y direction of the ball are collided with the paddle
        // The ball is considered to be collided with the paddle
        if (isPaddleXcollided && isPaddleYcollided) {
            // Check if the center of the ball is not aligned with the paddle
            // If it is not aligned, then the ball is hit at a corner of the paddle
            // This case is described by visualizing on a coordinate plane at report
            if (!isPaddleXaligned && !isPaddleYaligned) {
                // Calculate the corner point of the paddle that was hit
                double paddleCornerX = paddleCoords[0] + (ballCoords[0] > paddleCoords[0] ? PADDLE_HALFWIDTH : -PADDLE_HALFWIDTH);
                double paddleCornerY = paddleCoords[1] + (ballCoords[1] > paddleCoords[1] ? PADDLE_HALFHEIGHT : -PADDLE_HALFHEIGHT);

                // Calculate the normal vector of the paddle
                double nx = ballCoords[0] - paddleCornerX;
                double ny = ballCoords[1] - paddleCornerY;
                double length = Math.sqrt(nx * nx + ny * ny);
                nx /= length;
                ny /= length;

                // Calculate the dot product of the ball velocity and the normal vector
                double dotProduct = ballVelocity[0] * nx + ballVelocity[1] * ny;

                // Reflect the ball velocity by using the normal vector
                ballVelocity[0] -= 2 * dotProduct * nx;
                ballVelocity[1] -= 2 * dotProduct * ny;
            } 
            // If the ball is aligned with the paddle in the x direction
            // The ball will bounce off the paddle in the y direction
            else if (isPaddleXaligned) ballVelocity[1] = -ballVelocity[1];

            // If the ball is aligned with the paddle in the y direction
            // The ball will bounce off the paddle in the x direction
            else if (isPaddleYaligned) ballVelocity[0] = -ballVelocity[0];
        }

        // ===========================
        // ===== Brick Collision =====
        // ===========================

        // When the ball collides with multiple bricks at the same time,
        // we will calculate the ball's reflection by adding all normal vectors
        // from all collisions and using the average normal.
        // In brick collision detection (for example, when in contact with multiple bricks simultaneously)
        // To sum up, we are just executing a superposition principle to normal vectors of all collisions.
        double sumNx = 0;
        double sumNy = 0;
        int collisionCount = 0;

        // Processing the all bricks
        for (int i = 0; i < BRICK_COORDS.length; i++) {
            if (BRICK_STATUS[i] == 0) continue;

            // Check if the ball is aligned with the brick
            boolean isXaligned = ballCoords[0] >= BRICK_COORDS[i][0] - BRICK_HALFWIDTH &&
                                ballCoords[0] <= BRICK_COORDS[i][0] + BRICK_HALFWIDTH;
            boolean isYaligned = ballCoords[1] >= BRICK_COORDS[i][1] - BRICK_HALFHEIGHT &&
                                ballCoords[1] <= BRICK_COORDS[i][1] + BRICK_HALFHEIGHT;

            // Check if the ball is collided with the brick
            boolean isXcollided = Math.abs(BRICK_COORDS[i][0] - ballCoords[0]) <= BRICK_HALFWIDTH + BALL_RADIUS;
            boolean isYcollided = Math.abs(BRICK_COORDS[i][1] - ballCoords[1]) <= BRICK_HALFHEIGHT + BALL_RADIUS;

            // If the ball is collided with the brick
            if (isXcollided && isYcollided) {
                double nx = 0, ny = 0;

                // If the ball is not aligned with the brick
                // The ball is hit at a corner of the brick
                if (!isXaligned && !isYaligned) {
                    double cornerX = BRICK_COORDS[i][0] + (ballCoords[0] > BRICK_COORDS[i][0] ? BRICK_HALFWIDTH : -BRICK_HALFWIDTH);
                    double cornerY = BRICK_COORDS[i][1] + (ballCoords[1] > BRICK_COORDS[i][1] ? BRICK_HALFHEIGHT : -BRICK_HALFHEIGHT);
                    nx = ballCoords[0] - cornerX;
                    ny = ballCoords[1] - cornerY;
                } else if (isXaligned) {
                    // The ball is hit in the y direction
                    ny = ballCoords[1] > BRICK_COORDS[i][1] ? 1 : -1;
                } else if (isYaligned) {
                    // The ball is hit in the x direction
                    nx = ballCoords[0] > BRICK_COORDS[i][0] ? 1 : -1;
                }

                // Convert the normal vector to a unit vector
                double length = Math.sqrt(nx * nx + ny * ny);
                if (length != 0) {
                    nx /= length;
                    ny /= length;
                }

                // Add the normal vector to the sum
                sumNx += nx;
                sumNy += ny;
                collisionCount++;

                GAME_STATES[0] += POINT_PER_BRICK;
                BRICK_STATUS[i] = 0;
            }
        }

        if (collisionCount > 0) {
            // Calculate the average normal vector
            double avgNx = sumNx / collisionCount;
            double avgNy = sumNy / collisionCount;
            double avgLength = Math.sqrt(avgNx * avgNx + avgNy * avgNy);
            
            // Calculate the unit vector of the average normal vector
            if (avgLength != 0) {
                avgNx /= avgLength;
                avgNy /= avgLength;
            }
            
            // Calculate the incidence angle of the ball (the angle between the horizontal and the ball's velocity)
            double incidenceAngle = Math.toDegrees(Math.atan2(Math.abs(ballVelocity[1]), Math.abs(ballVelocity[0])));
            
            // Adjustable threshold value
            double thresholdAngle = Math.toDegrees(Math.atan2(Math.abs(BRICK_HALFHEIGHT), Math.abs(BRICK_HALFWIDTH))); 

            // If the ball is moving almost horizontally, normal reflection can produce very sharp results in collisions.
            if (incidenceAngle < thresholdAngle) {
                // In this case, we apply a simpler reflection:
                // Which axis has more deviation, we reverse it.
                double deltaX = Math.abs(ballCoords[0] - (ballCoords[0] + avgNx));
                double deltaY = Math.abs(ballCoords[1] - (ballCoords[1] + avgNy));
                if (deltaX > deltaY) {
                    ballVelocity[0] = -ballVelocity[0];
                } else {
                    ballVelocity[1] = -ballVelocity[1];
                }
            } else {
                // Normal reflection: the classical reflection formula is applied.
                double dotProduct = ballVelocity[0] * avgNx + ballVelocity[1] * avgNy;
                ballVelocity[0] -= 2 * dotProduct * avgNx;
                ballVelocity[1] -= 2 * dotProduct * avgNy;
            }
            
            checkAllBricksDestroyed(BRICK_COORDS, BRICK_STATUS, GAME_STATES);
        }
    }

    // ===========================
    // ======= RENDERING =========
    // ===========================

    // This part mainly contains the common rendering methods and components

    /*
     * This method is used to render the current state of the game
     * This method must be called on every rendering method
     * It is also used to set the game frame rate
     * Game frame rate is set by using show() method of StdDraw
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void render() {
        // Set the game frame rate to 120
        final int GAME_FPS = 120;
        // Render the game at the frame rate
        StdDraw.show(1000/GAME_FPS);
    }

    /*
     * This method is used to render the ball
     * Depending on the coordinates of the ball
     * the ball will be rendered at the specified coordinates
     * 
     * @param ballCoords: Coordinates of the ball
     * @param BALL_RADIUS: Radius of the ball
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderBall(double[] ballCoords, double BALL_RADIUS) {
        final Color BALL_COLOR = new Color(15, 82, 186);

        StdDraw.setPenColor(BALL_COLOR);
        StdDraw.filledCircle(ballCoords[0], ballCoords[1], BALL_RADIUS);
    }

    /*
     * This method is used to render the bricks
     * Depending on the coordinates of the bricks
     * the bricks will be rendered at the specified coordinates
     * 
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param BRICK_COLORS: Colors of the bricks
     * @param BRICK_HALFWIDTH: Half width of the bricks
     * @param BRICK_HALFHEIGHT: Half height of the bricks
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderBricks(double[][] BRICK_COORDS, double[] BRICK_STATUS, Color[] BRICK_COLORS, double BRICK_HALFWIDTH, double BRICK_HALFHEIGHT) {
        for (int i = 0; i < BRICK_COORDS.length; i++) {
            // If the brick is already destroyed, skip it
            if (BRICK_STATUS[i] == 0) continue;
            // Select a random color for each brick from the available colors
            // Render the brick
            StdDraw.setPenColor(BRICK_COLORS[i]);
            StdDraw.filledRectangle(BRICK_COORDS[i][0], BRICK_COORDS[i][1], BRICK_HALFWIDTH, BRICK_HALFHEIGHT);
        }
    }

    /*
     * This method is used to render the paddle
     * Depending on the coordinates of the paddle
     * the paddle will be rendered at the specified coordinates
     * 
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderPaddle(double[] paddleCoords, double PADDLE_HALFWIDTH, double PADDLE_HALFHEIGHT) {
        final Color PADDLE_COLOR = new Color(128, 128, 128);

        StdDraw.setPenColor(PADDLE_COLOR);
        StdDraw.filledRectangle(paddleCoords[0], paddleCoords[1], PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT);
    }

    // ===========================
    // ===========Init============
    // ===========================

    // This part mainly contains the initialization page

    /*
     * This method is used to set initalization logic of the game
     * It is called when the application is started
     * Mainly used to refer to the welcome text
     * 
     * @param CANVAS_SIZE: Size of the canvas
     * 
     * @return void
     * @since 2025-03-14
     */

    private static void init(int[] CANVAS_SIZE) {
        StdDraw.clear();
        renderInit(CANVAS_SIZE);
        render();
    }

    /*
     * This method is used to render the initialization page
     * It is called when the application is started
     * Mainly used to refer to the initialization screen
     * 
     * @param CANVAS_SIZE: Size of the canvas
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderInit(int[] CANVAS_SIZE) {
        final String WELCOME_TEXT = "Press space to start";
        final double[] WELCOME_LOCATION = {CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2};
        final Color TEXT_COLOR = Color.decode("#232323");
        renderText(WELCOME_LOCATION, WELCOME_TEXT, TEXT_COLOR, TYPOGRAPHY.BODY, TYPOGRAPHY_ALIGNMENT.CENTER);
    }

    // ===========================
    // =========Pre-Start=========
    // ===========================

    /*
     * This method is used to set pre-start logic of the game
     * It is called when the game is started
     * Mainly used to refer to the pre-start screen
     * 
     * @param GAME_STATES: Game state variables
     * @param CANVAS_SIZE: Size of the canvas
     * @param ballCoords: Coordinates of the ball
     * @param BALL_RADIUS: Radius of the ball
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param BRICK_COLORS: Colors of the bricks
     * @param BRICK_HALFWIDTH: Half width of the bricks
     * @param BRICK_HALFHEIGHT: Half height of the bricks
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * @param DIRECTION_LENGTH: Length of the direction
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void preStart(
        double[] GAME_STATES,
        int[] CANVAS_SIZE,
        double[] ballCoords, 
        double BALL_RADIUS, 
        double[][] BRICK_COORDS, 
        double[] BRICK_STATUS, 
        Color[] BRICK_COLORS, 
        double BRICK_HALFWIDTH, 
        double BRICK_HALFHEIGHT,
        double[] paddleCoords, 
        double PADDLE_HALFWIDTH, 
        double PADDLE_HALFHEIGHT, 
        double DIRECTION_LENGTH
    ) {
        StdDraw.clear();
        renderPreStart(GAME_STATES, CANVAS_SIZE, ballCoords, BALL_RADIUS, BRICK_COORDS, BRICK_STATUS, BRICK_COLORS, BRICK_HALFWIDTH, BRICK_HALFHEIGHT, paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT, DIRECTION_LENGTH);
        render();
    }

    /*
     * This method is used to render the pre-start page
     * It is called when the game is started
     * Mainly used to refer to the pre-start screen 
     * 
     * @param GAME_STATES: Game state variables
     * @param CANVAS_SIZE: Size of the canvas
     * @param ballCoords: Coordinates of the ball
     * @param BALL_RADIUS: Radius of the ball
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param BRICK_COLORS: Colors of the bricks
     * @param BRICK_HALFWIDTH: Half width of the bricks
     * @param BRICK_HALFHEIGHT: Half height of the bricks
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * @param DIRECTION_LENGTH: Length of the direction
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderPreStart(
        double[] GAME_STATES,
        int[] CANVAS_SIZE,
        double[] ballCoords, 
        double BALL_RADIUS, 
        double[][] BRICK_COORDS, 
        double[] BRICK_STATUS, 
        Color[] BRICK_COLORS, 
        double BRICK_HALFWIDTH, 
        double BRICK_HALFHEIGHT, 
        double[] paddleCoords, 
        double PADDLE_HALFWIDTH, 
        double PADDLE_HALFHEIGHT, 
        double DIRECTION_LENGTH
    ) {
        renderTopText("Angle:" + (90 - Math.abs(GAME_STATES[2])), CANVAS_SIZE, GAME_STATES);
        renderBall(ballCoords, BALL_RADIUS);
        renderPaddle(paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT);
        renderBricks(BRICK_COORDS, BRICK_STATUS, BRICK_COLORS, BRICK_HALFWIDTH, BRICK_HALFHEIGHT);
        renderAngleLine(ballCoords, GAME_STATES[2], DIRECTION_LENGTH);
        render();
    }

    /*
     * This method is used to render the angle line
     * It is called when the game is started
     * Used to refer to the angle line
     * 
     * @param ballCoords: Coordinates of the ball
     * @param angle: Angle of the ball
     * @param DIRECTION_LENGTH: Length of the direction
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderAngleLine(double[] ballCoords, double angle, double DIRECTION_LENGTH) {
        final Color LINE_COLOR = Color.decode("#232323");
        StdDraw.setPenColor(LINE_COLOR);
        StdDraw.line(ballCoords[0], ballCoords[1], ballCoords[0] + Math.sin(Math.toRadians(angle))*DIRECTION_LENGTH, ballCoords[1] + Math.cos(Math.toRadians(angle))*DIRECTION_LENGTH);
    }
    
    // ===========================
    // ==========Playing==========
    // ===========================

    /*
     * This method is used to set playing logic of the game
     * It is called when the game is started
     * Mainly used to refer to the playing screen
     * 
     * @param GAME_STATES: Game state variables
     * @param CANVAS_SIZE: Size of the canvas
     * @param ballCoords: Coordinates of the ball
     * @param BALL_RADIUS: Radius of the ball
     * @param BALL_SPEED: Speed of the ball
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param BRICK_COLORS: Colors of the bricks
     * @param BRICK_HALFWIDTH: Half width of the bricks
     * @param BRICK_HALFHEIGHT: Half height of the bricks
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * @param ballVelocity: Velocity of the ball
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void playing(
        double[] GAME_STATES,
        int[] CANVAS_SIZE,
        double[] ballCoords, 
        double BALL_RADIUS, 
        double BALL_SPEED,
        double[][] BRICK_COORDS, 
        double[] BRICK_STATUS, 
        Color[] BRICK_COLORS,
        double BRICK_HALFWIDTH, 
        double BRICK_HALFHEIGHT,
        double[] paddleCoords,
        double PADDLE_HALFWIDTH, 
        double PADDLE_HALFHEIGHT,
        double[] ballVelocity
    ) {
        StdDraw.clear();
        renderPlaying(GAME_STATES, CANVAS_SIZE, ballCoords, BALL_RADIUS, BRICK_COORDS, BRICK_STATUS, BRICK_COLORS, BRICK_HALFWIDTH, BRICK_HALFHEIGHT, paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT, ballVelocity);
        render();
        runGameEngine(GAME_STATES, ballCoords, ballVelocity, BALL_SPEED, BALL_RADIUS, BRICK_COORDS, BRICK_STATUS, BRICK_HALFWIDTH, BRICK_HALFHEIGHT, paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT, CANVAS_SIZE);
    }

    /*
     * This method is used to render the playing page
     * It is called when the game is started
     * Mainly used to refer to the playing screen
     * 
     * @param GAME_STATES: Game state variables
     * @param CANVAS_SIZE: Size of the canvas
     * @param ballCoords: Coordinates of the ball
     * @param BALL_RADIUS: Radius of the ball
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param BRICK_COLORS: Colors of the bricks
     * @param BRICK_HALFWIDTH: Half width of the bricks
     * @param BRICK_HALFHEIGHT: Half height of the bricks
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * @param ballVelocity: Velocity of the ball
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderPlaying(
        double[] GAME_STATES,
        int[] CANVAS_SIZE,
        double[] ballCoords, 
        double BALL_RADIUS, 
        double[][] BRICK_COORDS, 
        double[] BRICK_STATUS, 
        Color[] BRICK_COLORS, 
        double BRICK_HALFWIDTH, 
        double BRICK_HALFHEIGHT,
        double[] paddleCoords, 
        double PADDLE_HALFWIDTH, 
        double PADDLE_HALFHEIGHT, 
        double[] ballVelocity
    ) {
        final double[] TOP_TEXT_LOCATION = {40 ,CANVAS_SIZE[1] - 20};
        final Color TEXT_COLOR = Color.decode("#232323");
        renderText(TOP_TEXT_LOCATION, "Score:" + GAME_STATES[0], TEXT_COLOR, TYPOGRAPHY.BODY, TYPOGRAPHY_ALIGNMENT.LEFT);
        renderBall(ballCoords, BALL_RADIUS);
        renderPaddle(paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT);
        renderBricks(BRICK_COORDS, BRICK_STATUS, BRICK_COLORS, BRICK_HALFWIDTH, BRICK_HALFHEIGHT);
        render();
    }

    // ===========================
    // =========Game Over=========
    // ===========================

    /*
     * This method is used to set game over logic of the game
     * It is called when the game is over
     * Mainly used to refer to the game over screen
     * 
     * @param GAME_STATES: Game state variables
     * @param CANVAS_SIZE: Size of the canvas
     * @param ballCoords: Coordinates of the ball
     * @param BALL_RADIUS: Radius of the ball
     * @param BRICK_COORDS: Coordinates of the bricks
     * @param BRICK_STATUS: Status of the bricks
     * @param BRICK_COLORS: Colors of the bricks
     * @param BRICK_HALFWIDTH: Half width of the bricks
     * @param BRICK_HALFHEIGHT: Half height of the bricks
     * @param paddleCoords: Coordinates of the paddle
     * @param PADDLE_HALFWIDTH: Half width of the paddle
     * @param PADDLE_HALFHEIGHT: Half height of the paddle
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void gameOver(
        double[] GAME_STATES,
        int[] CANVAS_SIZE,
        double[] ballCoords, 
        double BALL_RADIUS, 
        double[][] BRICK_COORDS, 
        double[] BRICK_STATUS, 
        Color[] BRICK_COLORS, 
        double BRICK_HALFWIDTH, 
        double BRICK_HALFHEIGHT,
        double[] paddleCoords, 
        double PADDLE_HALFWIDTH, 
        double PADDLE_HALFHEIGHT
    ) {
        StdDraw.clear();
        renderBall(ballCoords, BALL_RADIUS);
        renderBricks(BRICK_COORDS, BRICK_STATUS, BRICK_COLORS, BRICK_HALFWIDTH, BRICK_HALFHEIGHT);
        renderPaddle(paddleCoords, PADDLE_HALFWIDTH, PADDLE_HALFHEIGHT);
        renderGameOverText(CANVAS_SIZE, GAME_STATES);
        render();
    }

    /*
     * This method is used to render the game over text
     * It is called when the game is over
     * Mainly used to refer to the game over screen
     * 
     * @param CANVAS_SIZE: Size of the canvas
     * @param GAME_STATES: Game state variables
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderGameOverText(int[] CANVAS_SIZE, double[] GAME_STATES) {
        final String GAME_OVER_TEXT = "GAME OVER";
        final Color GAME_OVER_COLOR = Color.decode("#232323");
        final double[] GAME_OVER_LOCATION = {CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2};
        renderText(GAME_OVER_LOCATION, GAME_OVER_TEXT, GAME_OVER_COLOR, TYPOGRAPHY.TITLE, TYPOGRAPHY_ALIGNMENT.CENTER);

        final String SCORE_TEXT = "Your score is " + GAME_STATES[0];
        final Color SCORE_COLOR = Color.decode("#232323");  
        final double[] SCORE_LOCATION = {CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2 - 40};
        renderText(SCORE_LOCATION, SCORE_TEXT, SCORE_COLOR, TYPOGRAPHY.SUBTITLE, TYPOGRAPHY_ALIGNMENT.CENTER);
    }

    // ===========================
    // =========Paused============
    // ===========================

    /*
     * This method is used to set paused logic of the game
     * It is called when the game is paused
     * Mainly used to refer to the paused screen
     * 
     * @param CANVAS_SIZE: Size of the canvas
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void paused(int[] CANVAS_SIZE) {
        StdDraw.clear();
        renderPaused(CANVAS_SIZE);
        render();
    }

    /*
     * This method is used to render the paused text
     * It is called when the game is paused
     * Mainly used to refer to the paused screen
     * 
     * @param CANVAS_SIZE: Size of the canvas
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderPaused(int[] CANVAS_SIZE) {
        final String PAUSED_TEXT = "Paused, press space to continue";
        final Color PAUSED_COLOR = Color.decode("#232323");
        final double[] PAUSED_LOCATION = {CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2};
        renderText(PAUSED_LOCATION, PAUSED_TEXT, PAUSED_COLOR, TYPOGRAPHY.BODY, TYPOGRAPHY_ALIGNMENT.CENTER);
    }

    // ===========================
    // =========Winner============
    // ===========================

    /*      
     * This method is used to set winner logic of the game
     * It is called when the game is over
     * Mainly used to refer to the winner screen
     * 
     * @param GAME_STATES: Game state variables
     * @param CANVAS_SIZE: Size of the canvas
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void winnerWinnerChickenDinner(double[] GAME_STATES, int[] CANVAS_SIZE) {
        StdDraw.clear();
        renderWinner(CANVAS_SIZE, GAME_STATES);
        render();
    }

    /*
     * This method is used to render the winner text
     * It is called when the game is over
     * Mainly used to refer to the winner screen
     * 
     * @param CANVAS_SIZE: Size of the canvas
     * @param GAME_STATES: Game state variables
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderWinner(int[] CANVAS_SIZE, double[] GAME_STATES) {
        final String VICTORY_TEXT = "VICTORY!";
        final Color VICTORY_COLOR = Color.decode("#00FF00");
        final double[] VICTORY_LOCATION = {CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2};
        renderText(VICTORY_LOCATION, VICTORY_TEXT, VICTORY_COLOR, TYPOGRAPHY.TITLE, TYPOGRAPHY_ALIGNMENT.CENTER);
        
        final double[] SCORE_LOCATION = {CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2 - 40};
        final String SCORE_TEXT = "Your score is " + GAME_STATES[0];
        final Color SCORE_COLOR = Color.decode("#232323");;
        renderText(SCORE_LOCATION, SCORE_TEXT, SCORE_COLOR, TYPOGRAPHY.SUBTITLE, TYPOGRAPHY_ALIGNMENT.CENTER);
    }
    

    // ===========================
    // =========Helper============
    // ===========================

    /*
     * This method is used to check if a key is safely pressed
     * It is called when a key is pressed
     * 
     * @param GAME_STATES: Game state variables
     * @param keyMap: Key map
     * 
     * @return boolean
     * @since 2025-03-14
     */
    private static boolean isSafelyPressed(double[] GAME_STATES, KEY_MAP keyMap) {
        final long KEY_COOLDOWN_MS = 200; // 200ms cooldown between key presses

        switch (keyMap) {
            case SPACE -> {
                if (new Date().getTime() - GAME_STATES[3] >= KEY_COOLDOWN_MS) {
                    GAME_STATES[3] = new Date().getTime();
                    return true;
                }
                return false;
            }

            default -> {
                return false;
            }
        }
    }

    // ===========================
    // ========Components=========
    // ===========================

    /*
     * This method is used to allow developers to
     * render text in a standard approach
     * 
     * @param location: Location of the text (x, y)
     * @param text: Text to render
     * @param color: Color of the text
     * @param typography: Typography of the text
     * @param alignment: Alignment of the text
     * 
     * @return void
     * @since 2025-03-14
     */
    private static void renderText(double[] location, String text, Color color, TYPOGRAPHY typography, TYPOGRAPHY_ALIGNMENT alignment) {
        // Typography Settings
        final Font TYPOGRAPHY_TITLE = new Font("SansSerif", Font.BOLD, 36);
        final Font TYPOGRAPHY_SUBTITLE = new Font("SansSerif", Font.PLAIN, 24);
        final Font TYPOGRAPHY_BODY = new Font("SansSerif", Font.PLAIN, 18);

        // Set the font based on the typography
        switch (typography) {
            case TITLE:
                StdDraw.setFont(TYPOGRAPHY_TITLE);
                break;
            case SUBTITLE:
                StdDraw.setFont(TYPOGRAPHY_SUBTITLE);
                break;
            case BODY:
                StdDraw.setFont(TYPOGRAPHY_BODY);
                break;
        }

        // Set the color of the text
        StdDraw.setPenColor(color);

        // Set the alignment of the text
        switch (alignment) {
            case LEFT:
                StdDraw.textLeft(location[0], location[1], text);
                break;
            case RIGHT:
                StdDraw.textRight(location[0], location[1], text);
                break;
            case CENTER:
                StdDraw.text(location[0], location[1], text);
                break;
        }
    }

    /*
     * This method is used to render the top text
     * It is called when the game is started
     * Mainly used to refer to the top text
     * 
     * @param text: Text to render
     * 
     */
    private static void renderTopText(String text, int[] CANVAS_SIZE, double[] GAME_STATES) {
        final double[] TOP_TEXT_LOCATION = {40 ,CANVAS_SIZE[1] - 20};
        final Color TEXT_COLOR = Color.decode("#232323");
        renderText(TOP_TEXT_LOCATION, text, TEXT_COLOR, TYPOGRAPHY.BODY, TYPOGRAPHY_ALIGNMENT.LEFT);
    }
}