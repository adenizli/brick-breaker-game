import stdlib.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.util.Date;

public class Game {
    enum STATUS {INIT,PRE_START,PLAYING,GAME_OVER,PAUSED,WINNER_WINNER_CHICKEN_DINNER}
    enum DIRECTION {LEFT,RIGHT}
    
    // Canvas settings
    final String CANVAS_TITLE = "BRICK BREAKER GAME";
    final double[] CANVAS_SIZE = {800,400};
    final double[] CANVAS_SCALE = {800,400};

    // Styling
    final Font TYPOGRAPHY_TITLE = new Font("SansSerif", Font.BOLD, 36);
    final Font TYPOGRAPHY_SUBTITLE = new Font("SansSerif", Font.PLAIN, 24);
    final Font TYPOGRAPHY_BODY = new Font("SansSerif", Font.PLAIN, 18);

    // Game constant variables
    final int POINT_PER_BRICK = 10;
    final Color TEXT_COLOR = Color.decode("#232323");
    final Color GAME_COLOR_DANGER = Color.decode("#FF0000");
    final Color GAME_COLOR_SUCCESS = Color.decode("#00FF00");
    final int GAME_DELAY = 8;

    // Game helper variables
    final int SAFE_KEY_PRESS_INTERVAL = 100;
    Date latestKeyboardInput = new Date();

    // Game state variables
    int score = 0;
    STATUS status = STATUS.INIT;

    // Game text variables
    final double[] TOP_TEXT_LOCATION = {40 ,CANVAS_SIZE[1] - 20};

    // Game brick variables
    final double BRICK_HALFWIDTH = 50; // Brick half width
    final double BRICK_HALFHEIGHT = 10; // Brick half height

    final Color[] BRICK_COLOR_DEFINITIONS = { 
        new Color(255, 0, 0), 
        new Color(220, 20, 60),
        new Color(178, 34, 34), 
        new Color(139, 0, 0),
        new Color(255, 69, 0), 
        new Color(165, 42, 42),
    };

    final double[][] BRICK_COORDS = {
        {250, 320},{350, 320},{450, 320},{550, 320},
        {150,300},{250, 300},{350, 300},{450, 300},
        {550, 300},{650, 300},{50, 280},{150, 280},
        {250, 280},{350, 280},{450, 280},{550, 280},
        {650, 280},{750, 280}, {50, 260},{150, 260},
        {250, 260},{350, 260},{450, 260},{550, 260},
        {650, 260},{750, 260}, {50, 240},{150, 240},
        {250, 240},{350, 240},{450, 240},{550, 240},
        {650, 240},{750, 240}, {150, 220},{250, 220},
        {350, 220},{450, 220},{550, 220},{650, 220},
        {250, 200},{350, 200},{450, 200},{550, 200}
    };

    final double[] BRICK_STATUS = new double[BRICK_COORDS.length];

    final Color [] BRICK_COLORS = new Color[] {
        BRICK_COLOR_DEFINITIONS[0], BRICK_COLOR_DEFINITIONS[1], BRICK_COLOR_DEFINITIONS[2], BRICK_COLOR_DEFINITIONS[3],
        BRICK_COLOR_DEFINITIONS[2], BRICK_COLOR_DEFINITIONS[4], BRICK_COLOR_DEFINITIONS[3], BRICK_COLOR_DEFINITIONS[0], 
        BRICK_COLOR_DEFINITIONS[4], BRICK_COLOR_DEFINITIONS[5], BRICK_COLOR_DEFINITIONS[5], BRICK_COLOR_DEFINITIONS[0], 
        BRICK_COLOR_DEFINITIONS[1], BRICK_COLOR_DEFINITIONS[5], BRICK_COLOR_DEFINITIONS[2], BRICK_COLOR_DEFINITIONS[3], 
        BRICK_COLOR_DEFINITIONS[0], BRICK_COLOR_DEFINITIONS[4], BRICK_COLOR_DEFINITIONS[1], BRICK_COLOR_DEFINITIONS[3], 
        BRICK_COLOR_DEFINITIONS[2], BRICK_COLOR_DEFINITIONS[4], BRICK_COLOR_DEFINITIONS[0], BRICK_COLOR_DEFINITIONS[5], 
        BRICK_COLOR_DEFINITIONS[2], BRICK_COLOR_DEFINITIONS[1], BRICK_COLOR_DEFINITIONS[4], BRICK_COLOR_DEFINITIONS[0], 
        BRICK_COLOR_DEFINITIONS[5], BRICK_COLOR_DEFINITIONS[1], BRICK_COLOR_DEFINITIONS[2], BRICK_COLOR_DEFINITIONS[3], 
        BRICK_COLOR_DEFINITIONS[0], BRICK_COLOR_DEFINITIONS[5], BRICK_COLOR_DEFINITIONS[1], BRICK_COLOR_DEFINITIONS[4], 
        BRICK_COLOR_DEFINITIONS[0], BRICK_COLOR_DEFINITIONS[5], BRICK_COLOR_DEFINITIONS[1], BRICK_COLOR_DEFINITIONS[2],
        BRICK_COLOR_DEFINITIONS[3], BRICK_COLOR_DEFINITIONS[2], BRICK_COLOR_DEFINITIONS[3], BRICK_COLOR_DEFINITIONS[0]
    };
    

    // Game paddle variables
    final double PADDLE_HALFWIDTH = 60; // Paddle half width
    final double PADDLE_HALFHEIGHT = 5; // Paddle half height
    final double PADDLE_SPEED = 3; // Paddle speed
    final Color PADDLE_COLOR = new Color(128, 128, 128); // Paddle color
    final double[] PADDLE_INITIAL_COORDS = {400, 5}; // Initial position of the center of the paddle

    double[] paddleCoords = {PADDLE_INITIAL_COORDS[0], PADDLE_INITIAL_COORDS[1]};

    // Game ball variables
    final Color BALL_COLOR = new Color(15, 82, 186);
    final double BALL_RADIUS = 8;
    final double[] BALL_INITIAL_COORDS = {400,18};
    final double DIRECTION_ROTATION_SPEED = 1;
    final int DIRECTION_LENGTH = 150;
    final double BALL_SPEED = 2;
    final double INITIAL_ANGLE = 74;

    double angle = INITIAL_ANGLE;
    double[] ballVelocity = {Math.sin(degreeToRadian(angle)) * BALL_SPEED, Math.cos(degreeToRadian(angle)) * BALL_SPEED};
    double[] ballCoords = {BALL_INITIAL_COORDS[0], BALL_INITIAL_COORDS[1]};

    public void main() {
        StdDraw.setCanvasSize(800, 400);
        StdDraw.setTitle(CANVAS_TITLE);
        StdDraw.setXscale(0.0, CANVAS_SCALE[0]);
        StdDraw.setYscale(0.0, CANVAS_SCALE[1]);
        StdDraw.enableDoubleBuffering();

        for (int i = 0; i < BRICK_COORDS.length; i++) {
            BRICK_STATUS[i] = 1;
        }

        while(true) {
            this.controller();
            if (status == STATUS.INIT) this.init();
            else if (status == STATUS.PRE_START) this.preStart();
            else if (status == STATUS.PLAYING) this.playing();
            else if (status == STATUS.GAME_OVER) this.gameOver();
            else if (status == STATUS.PAUSED) this.paused();
            else if (status == STATUS.WINNER_WINNER_CHICKEN_DINNER) this.winnerWinnerChickenDinner();
        }
        
    }

    // Game methods
    private void runGameEngine() {
        this.moveBall();
        this.checkCollision();
        this.checkAllBricksDestroyed();
    }

    private void resetGameParams() {
        this.status = STATUS.INIT;
        this.latestKeyboardInput = new Date();
        this.ballCoords[0] = BALL_INITIAL_COORDS[0];
        this.ballCoords[1] = BALL_INITIAL_COORDS[1];
        this.paddleCoords[0] = PADDLE_INITIAL_COORDS[0];
        this.paddleCoords[1] = PADDLE_INITIAL_COORDS[1];
        this.angle = INITIAL_ANGLE;
        this.ballVelocity[0] = Math.sin(degreeToRadian(angle)) * BALL_SPEED;
        this.ballVelocity[1] = Math.cos(degreeToRadian(angle)) * BALL_SPEED;
        this.score = 0;

        for (int i = 0; i < BRICK_COORDS.length; i++) {
            BRICK_STATUS[i] = 1;
        }
    }

    private void checkAllBricksDestroyed() {
        for (int i = 0; i < BRICK_COORDS.length; i++) {
            if (BRICK_STATUS[i] == 1) return;
        }
        this.status = STATUS.WINNER_WINNER_CHICKEN_DINNER;
    }

    private void moveBall() {
        this.ballCoords[0] += this.ballVelocity[0];
        this.ballCoords[1] += this.ballVelocity[1];
    }

    private void movePaddle(DIRECTION direction) {
        boolean isLeftPaddleMoveValid = paddleCoords[0] - PADDLE_HALFWIDTH > 0;
        boolean isRightPaddleMoveValid = paddleCoords[0] + PADDLE_HALFWIDTH < CANVAS_SIZE[0];
        System.out.println(paddleCoords[0] + " " + PADDLE_HALFWIDTH + " " + CANVAS_SIZE[0]);
        if (direction == DIRECTION.LEFT && isLeftPaddleMoveValid) {
            paddleCoords[0] -= PADDLE_SPEED;
        } else if (direction == DIRECTION.RIGHT && isRightPaddleMoveValid) {
            paddleCoords[0] += PADDLE_SPEED;
        }
    }

    private void rotateDirection(DIRECTION rotationDirection) {
        boolean isLeftRotationValid = angle >= -89;
        boolean isRightRotationValid = angle <= 89;

        if (rotationDirection == DIRECTION.LEFT && isLeftRotationValid) angle -= DIRECTION_ROTATION_SPEED;
        else if (rotationDirection == DIRECTION.RIGHT && isRightRotationValid) angle += DIRECTION_ROTATION_SPEED;

        this.updateBallVelocity();
    }

    private void updateBallVelocity() {
        ballVelocity[0] = Math.sin(degreeToRadian(angle)) * BALL_SPEED;
        ballVelocity[1] = Math.cos(degreeToRadian(angle)) * BALL_SPEED;
    }

    private void checkCollision() {
        // Canvas & Ball collisions
        boolean isBallCollidedWithLeftCanvas = ballCoords[0] - BALL_RADIUS <= 0;
        boolean isBallCollidedWithRightCanvas = ballCoords[0] + BALL_RADIUS >= CANVAS_SIZE[0];
        boolean isBallCollidedWithTopCanvas = ballCoords[1] + BALL_RADIUS >= CANVAS_SIZE[1];
        boolean isBallCollidedWithBottomCanvas = ballCoords[1] - BALL_RADIUS <= 0;
        
        if(isBallCollidedWithLeftCanvas || isBallCollidedWithRightCanvas) ballVelocity[0] = -ballVelocity[0];
        if(isBallCollidedWithTopCanvas) ballVelocity[1] = -ballVelocity[1];
        if(isBallCollidedWithBottomCanvas) status = STATUS.GAME_OVER;
        
        // Paddle Collision Logic
        boolean isPaddleXaligned = ballCoords[0] >= paddleCoords[0] - PADDLE_HALFWIDTH && ballCoords[0] <= paddleCoords[0] + PADDLE_HALFWIDTH;
        boolean isPaddleYaligned = ballCoords[1] >= paddleCoords[1] - PADDLE_HALFHEIGHT && ballCoords[1] <= paddleCoords[1] + PADDLE_HALFHEIGHT;

        boolean isPaddleYcollided = Math.abs(paddleCoords[1] - ballCoords[1]) <= PADDLE_HALFHEIGHT + BALL_RADIUS;
        boolean isPaddleXcollided = Math.abs(paddleCoords[0] - ballCoords[0]) <= PADDLE_HALFWIDTH + BALL_RADIUS;

        if (isPaddleXcollided && isPaddleYcollided) {
            if (!isPaddleXaligned && !isPaddleYaligned) {
                double paddleCornerX = paddleCoords[0] + (ballCoords[0] > paddleCoords[0] ? PADDLE_HALFWIDTH : -PADDLE_HALFWIDTH);
                double paddleCornerY = paddleCoords[1] + (ballCoords[1] > paddleCoords[1] ? PADDLE_HALFHEIGHT : -PADDLE_HALFHEIGHT);

                double nx = ballCoords[0] - paddleCornerX;
                double ny = ballCoords[1] - paddleCornerY;
                double length = Math.sqrt(nx * nx + ny * ny);
                nx /= length;
                ny /= length;

                double dotProduct = ballVelocity[0] * nx + ballVelocity[1] * ny;

                ballVelocity[0] -= 2 * dotProduct * nx;
                ballVelocity[1] -= 2 * dotProduct * ny;
            } 
            else if (isPaddleXaligned) ballVelocity[1] = -ballVelocity[1];
            else if (isPaddleYaligned) ballVelocity[0] = -ballVelocity[0];
        }

        // Brick Collision Logic
        for (int i = 0; i < BRICK_COORDS.length; i++) {
            if (BRICK_STATUS[i] == 0) continue;

            boolean isXaligned = ballCoords[0] >= BRICK_COORDS[i][0] - BRICK_HALFWIDTH && ballCoords[0] <= BRICK_COORDS[i][0] + BRICK_HALFWIDTH;
            boolean isYaligned = ballCoords[1] >= BRICK_COORDS[i][1] - BRICK_HALFHEIGHT && ballCoords[1] <= BRICK_COORDS[i][1] + BRICK_HALFHEIGHT;

            boolean isYcollided = Math.abs(BRICK_COORDS[i][1] - ballCoords[1]) <= BRICK_HALFHEIGHT + BALL_RADIUS;
            boolean isXcollided = Math.abs(BRICK_COORDS[i][0] - ballCoords[0]) <= BRICK_HALFWIDTH + BALL_RADIUS;

            if (isXcollided && isYcollided) {
                if (!isXaligned && !isYaligned) { 
                    double cornerX = BRICK_COORDS[i][0] + (ballCoords[0] > BRICK_COORDS[i][0] ? BRICK_HALFWIDTH : -BRICK_HALFWIDTH);
                    double cornerY = BRICK_COORDS[i][1] + (ballCoords[1] > BRICK_COORDS[i][1] ? BRICK_HALFHEIGHT : -BRICK_HALFHEIGHT);

                    double nx = ballCoords[0] - cornerX;
                    double ny = ballCoords[1] - cornerY;
                    double length = Math.sqrt(nx * nx + ny * ny);
                    nx /= length;
                    ny /= length;

                    double dotProduct = ballVelocity[0] * nx + ballVelocity[1] * ny;

                    ballVelocity[0] -= 2 * dotProduct * nx;
                    ballVelocity[1] -= 2 * dotProduct * ny;
                } 
                else if (isXaligned) ballVelocity[1] = -ballVelocity[1]; 
                else if (isYaligned) ballVelocity[0] = -ballVelocity[0];
                

                score += POINT_PER_BRICK;
                BRICK_STATUS[i] = 0;
            }
        }
    }

    private void controller() {
        final boolean isSpacePressed = StdDraw.isKeyPressed(32);
        final boolean isLeftArrowPressed = StdDraw.isKeyPressed(37);
        final boolean isRightArrowPressed = StdDraw.isKeyPressed(39);
        final boolean isSafelyPressed = this.isSafelyPressed();

        if (isSpacePressed & isSafelyPressed) {
            this.latestKeyboardInput = new Date();
            if (status == STATUS.INIT) this.status = STATUS.PRE_START;
            else if (status == STATUS.PRE_START) this.status = STATUS.PLAYING;
            else if (status == STATUS.PLAYING) this.status = STATUS.PAUSED;
            else if (status == STATUS.PAUSED) this.status = STATUS.PLAYING;
            else if (status == STATUS.GAME_OVER) this.resetGameParams();
        }

        if (isLeftArrowPressed) {
            this.latestKeyboardInput = new Date();
            if (status == STATUS.PRE_START) this.rotateDirection(DIRECTION.LEFT);
            else if (status == STATUS.PLAYING) this.movePaddle(DIRECTION.LEFT);
        }

        else if (isRightArrowPressed) {
            this.latestKeyboardInput = new Date();
            if (status == STATUS.PRE_START) this.rotateDirection(DIRECTION.RIGHT);
            else if (status == STATUS.PLAYING) this.movePaddle(DIRECTION.RIGHT);
        }
    }

    // Canvas rendering methods
    private void render() {
        StdDraw.show(GAME_DELAY);
    }

    private void renderTopText(String text, double value) {
        StdDraw.setFont(TYPOGRAPHY_BODY);
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.textLeft(TOP_TEXT_LOCATION[0], TOP_TEXT_LOCATION[1], text + " " + value);
    }

    private void renderBall() {
        StdDraw.setPenColor(BALL_COLOR);
        StdDraw.filledCircle(ballCoords[0], ballCoords[1], BALL_RADIUS);
    }

    private void renderBricks() {
        for (int i = 0; i < BRICK_COORDS.length; i++) {
            if (BRICK_STATUS[i] == 0) continue;
            StdDraw.setPenColor(BRICK_COLORS[i]);
            StdDraw.filledRectangle(BRICK_COORDS[i][0], BRICK_COORDS[i][1], BRICK_HALFWIDTH, BRICK_HALFHEIGHT);
        }
    }

    private void renderPaddle() {
        StdDraw.setPenColor(PADDLE_COLOR);
        StdDraw.filledRectangle(paddleCoords[0], paddleCoords[1], BRICK_HALFWIDTH, BRICK_HALFHEIGHT);
    }

    private void init() {
        StdDraw.clear();
        this.renderWelcomeText();
        this.render();
    }

    private void renderWelcomeText() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2, "Press space to start");
    }



    // ===========================
    // =========Pre-Start=========
    // ===========================
    private void preStart() {
        StdDraw.clear();
        this.renderPreStart();
        this.render();
    }

    private void renderPreStart() {
        StdDraw.clear();
        this.renderBall();
        this.renderTopText("Angle:", 90 - Math.abs(angle));
        this.renderPaddle();
        this.renderBricks();
        this.renderAngleLine();
        this.render();
    }

    private void renderAngleLine() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.line(ballCoords[0], ballCoords[1], ballCoords[0] + Math.sin(degreeToRadian(angle))*DIRECTION_LENGTH, ballCoords[1] + Math.cos(degreeToRadian(angle))*DIRECTION_LENGTH);
    }
    
    // ===========================
    // ==========Playing==========
    // ===========================
    private void playing() {
        StdDraw.clear();
        this.renderPlaying();
        this.render();
        this.runGameEngine();
    }

    private void renderPlaying() {
        StdDraw.clear();
        this.renderBall();
        this.renderBricks();
        this.renderTopText("Score:", score);
        this.renderPaddle();
        this.render();
        this.runGameEngine();
    }

    // ===========================
    // =========Game Over=========
    // ===========================
    private void gameOver() {
        StdDraw.clear();
        this.renderBall();
        this.renderBricks();
        this.renderPaddle();
        this.renderGameOverText();
        this.render();
    }

    private void renderGameOverText() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.setFont(TYPOGRAPHY_TITLE);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2, "GAME OVER");
        StdDraw.setFont(TYPOGRAPHY_SUBTITLE);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2 - 40, "Your score is " + score);
    }

    // ===========================
    // =========Paused============
    // ===========================
    private void paused() {
        StdDraw.clear();
        this.drawPaused();
        this.render();
    }

    private void drawPaused() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2, "Paused, press space to continue");
    }

    // ===========================
    // =========Winner============
    // ===========================
    private void winnerWinnerChickenDinner() {
        StdDraw.clear();
        this.drawWinner();
        this.render();
    }

    private void drawWinner() {
        StdDraw.setPenColor(GAME_COLOR_DANGER);
        StdDraw.setFont(TYPOGRAPHY_TITLE);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2, "VICTORY!");
        StdDraw.setFont(TYPOGRAPHY_SUBTITLE);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2 - 40, "Your score is " + score);
    }
    

    // ===========================
    // =========Helper============
    // ===========================
    private double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    private boolean isSafelyPressed() {
        return new Date().getTime() - this.latestKeyboardInput.getTime() > SAFE_KEY_PRESS_INTERVAL;
    }
}