import stdlib.StdDraw;
import java.awt.Color;
import java.util.Date;

public class Game {
    enum STATUS {
        INIT,
        PRE_START,
        PLAYING,
        GAME_OVER,
        PAUSED,
    }

    enum PADDLE_DIRECTION {
        LEFT,
        RIGHT
    }
    
    final double[] CANVAS_SIZE = {800,400};
    final double[] CANVAS_SCALE = {800,400};

    int score = 0;
    Date latestSpaceClick = new Date();
    STATUS status = STATUS.INIT;
    final double[] SCORE_LOCATION = {40 ,CANVAS_SIZE[1] - 20};
    final Color TEXT_COLOR = Color.decode("#232323");


    final int POINT_PER_BRICK = 10;

    final Color[] COLOR_DEFINITONS = { 
        new Color(255, 0, 0), 
        new Color(220, 20, 60),
        new Color(178, 34, 34), 
        new Color(139, 0, 0),
        new Color(255, 69, 0), 
        new Color(165, 42, 42),
    };

    final double[][] BRICK_COORDS = {{250, 320, 1},{350, 320, 1},{450, 320, 1},{550, 320, 1},
    {150,300, 1},{250, 300, 1},{350, 300, 1},{450, 300, 1},{550, 300, 1},{650, 300, 1},
    {50, 280, 1},{150, 280, 1},{250, 280, 1},{350, 280, 1},{450, 280, 1},{550, 280, 1},{650, 280, 1},{750, 280, 1},
    {50, 260, 1},{150, 260, 1},{250, 260, 1},{350, 260, 1},{450, 260, 1},{550, 260, 1},{650, 260, 1},{750, 260, 1},
    {50, 240, 1},{150, 240, 1},{250, 240, 1},{350, 240, 1},{450, 240, 1},{550, 240, 1},{650, 240, 1},{750, 240, 1},
    {150, 220, 1},{250, 220, 1},{350, 220, 1},{450, 220, 1},{550, 220, 1},{650, 220, 1},
    {250, 200, 1},{350, 200, 1},{450, 200, 1},{550, 200, 1}};
    // Brick colors
    final Color [] BRICK_COLORS = new Color[] {
        COLOR_DEFINITONS[0], COLOR_DEFINITONS[1], COLOR_DEFINITONS[2], COLOR_DEFINITONS[3],
        COLOR_DEFINITONS[2], COLOR_DEFINITONS[4], COLOR_DEFINITONS[3], COLOR_DEFINITONS[0], COLOR_DEFINITONS[4], COLOR_DEFINITONS[5],
        COLOR_DEFINITONS[5], COLOR_DEFINITONS[0], COLOR_DEFINITONS[1], COLOR_DEFINITONS[5], COLOR_DEFINITONS[2], COLOR_DEFINITONS[3], COLOR_DEFINITONS[0], COLOR_DEFINITONS[4],
        COLOR_DEFINITONS[1], COLOR_DEFINITONS[3], COLOR_DEFINITONS[2], COLOR_DEFINITONS[4], COLOR_DEFINITONS[0], COLOR_DEFINITONS[5], COLOR_DEFINITONS[2], COLOR_DEFINITONS[1],
        COLOR_DEFINITONS[4], COLOR_DEFINITONS[0], COLOR_DEFINITONS[5], COLOR_DEFINITONS[1], COLOR_DEFINITONS[2], COLOR_DEFINITONS[3], COLOR_DEFINITONS[0], COLOR_DEFINITONS[5],
        COLOR_DEFINITONS[1], COLOR_DEFINITONS[4], COLOR_DEFINITONS[0], COLOR_DEFINITONS[5], COLOR_DEFINITONS[1], COLOR_DEFINITONS[2],
        COLOR_DEFINITONS[3], COLOR_DEFINITONS[2], COLOR_DEFINITONS[3], COLOR_DEFINITONS[0]
    };
    
    final double BRICK_HALFWIDTH = 50; // Brick half width
    final double BRICK_HALFHEIGHT = 10; // Brick half height

    final double PADDLE_HALFWIDTH = 60; // Paddle half width
    final double PADDLE_HALFHEIGHT = 5; // Paddle half height
    final double PADDLE_SPEED = 20; // Paddle speed
    final Color PADDLE_COLOR = new Color(128, 128, 128); // Paddle color
    double[] paddleCoords = {400, 5}; // Initial position of the center of the paddle
    
    final Color BALL_COLOR = new Color(15, 82, 186);
    final double BALL_RADIUS = 8;
    double shootingAngle = 0;
    double[] ballVelocity = {1,2};
    double[] ballCoords = {400,18};

    public void main() {
        StdDraw.setCanvasSize(800, 400);
        StdDraw.setXscale(0.0, CANVAS_SCALE[0]);
        StdDraw.setYscale(0.0, CANVAS_SCALE[1]);
        StdDraw.enableDoubleBuffering();
        while(true) {
            this.controller();
            switch (status) {
                case STATUS.INIT:
                    this.init();
                    break;
                case STATUS.PRE_START:
                    this.preStart();
                    break;
                case STATUS.PLAYING:
                    this.playing();
                    this.engine();
                    break;
                case STATUS.GAME_OVER:
                    this.gameOver();
                    break;
                case STATUS.PAUSED:
                    this.paused();
                    break;
                default:
                    break;
            }
        }
        
    }

    private void init() {
        StdDraw.clear();
        this.drawInit();
        StdDraw.show(16);
    }

    private void drawInit() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2, "Press space to start");
    }

    private void preStart() {
        StdDraw.clear();
        this.drawPreStart();
        StdDraw.show(16);
    }

    private void drawPreStart() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2, "Press space to start");
    }
    
    private void playing() {
        StdDraw.clear();
        this.drawBall();
        this.drawBricks();
        this.drawScore();
        this.drawPaddle();
        // this.drawPaddle();
        StdDraw.show(16);
    }

    private void gameOver() {
        StdDraw.clear();
        this.drawGameOver();
        StdDraw.show(16);
    }

    private void drawGameOver() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2, "Game Over");
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2 - 20, "Your score is " + score);
    }

    private void paused() {
        StdDraw.clear();
        this.drawPaused();
        StdDraw.show(16);
    }

    private void drawPaused() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.text(CANVAS_SIZE[0]/2, CANVAS_SIZE[1]/2, "Paused, press space to continue");
    }
    
    private void controller() {
        if (status == STATUS.INIT) {
            if (StdDraw.isKeyPressed(32)) this.status = STATUS.PRE_START;
        }
        else if (status == STATUS.PRE_START) {
            if (StdDraw.isKeyPressed(32) && new Date().getTime() - this.latestSpaceClick.getTime() > 1000) {
                this.status = STATUS.PLAYING;
                this.latestSpaceClick = new Date();
            }
        }
        else if (status == STATUS.PLAYING) {
            if (StdDraw.isKeyPressed(37)) this.movePaddle(PADDLE_DIRECTION.LEFT);
            if (StdDraw.isKeyPressed(39)) this.movePaddle(PADDLE_DIRECTION.RIGHT);
            if (StdDraw.isKeyPressed(32) && new Date().getTime() - this.latestSpaceClick.getTime() > 200) {
                this.status = STATUS.PAUSED;
                this.latestSpaceClick = new Date();
            }
        }
        else if (status == STATUS.PAUSED) {
            if (StdDraw.isKeyPressed(32) && new Date().getTime() - this.latestSpaceClick.getTime() > 200) {
                this.status = STATUS.PLAYING;
                this.latestSpaceClick = new Date();
            }
        }
    }

    private void drawBall() {
        StdDraw.setPenColor(BALL_COLOR);
        StdDraw.filledCircle(ballCoords[0], ballCoords[1], BALL_RADIUS);
    }

    private void drawBricks() {
        for (int i = 0; i < BRICK_COORDS.length; i++) {
            if (BRICK_COORDS[i][2] == 0) continue;
            StdDraw.setPenColor(BRICK_COLORS[i]);
            StdDraw.filledRectangle(BRICK_COORDS[i][0], BRICK_COORDS[i][1], BRICK_HALFWIDTH, BRICK_HALFHEIGHT);
        }
    }

    private void drawScore() {
        StdDraw.setPenColor(TEXT_COLOR);
        StdDraw.textLeft(SCORE_LOCATION[0], SCORE_LOCATION[1], "SCORE: "+ score);
    }

    private void drawPaddle() {
        StdDraw.setPenColor(PADDLE_COLOR);
        StdDraw.filledRectangle(paddleCoords[0], paddleCoords[1], BRICK_HALFWIDTH, BRICK_HALFHEIGHT);
    }

    private void engine() {
        this.moveBall();
        this.checkCollision();
    }

    private void moveBall() {
        ballCoords[0] += ballVelocity[0];
        ballCoords[1] += ballVelocity[1];
    }

    private void movePaddle(PADDLE_DIRECTION direction) {
        switch (direction) {
            case PADDLE_DIRECTION.LEFT:
                if (paddleCoords[0] - PADDLE_HALFWIDTH <= 0) break;
                paddleCoords[0] -= PADDLE_SPEED;
                break;
            case PADDLE_DIRECTION.RIGHT:
                if (paddleCoords[0] + PADDLE_HALFWIDTH >= CANVAS_SIZE[0]) break;
                paddleCoords[0] += PADDLE_SPEED;
                break;
            default:
                break;
        }
    }

    private void checkCollision() {
        if(CANVAS_SIZE[0] - ballCoords[0] <= BALL_RADIUS) ballVelocity[0] = -ballVelocity[0];

        if(ballCoords[0] - BALL_RADIUS <= 0) ballVelocity[0] = -ballVelocity[0];

        if(CANVAS_SIZE[1] - ballCoords[1] <= BALL_RADIUS) ballVelocity[1] = -ballVelocity[1];

        if(ballCoords[1] - BALL_RADIUS <= 0) status = STATUS.GAME_OVER;
        

        boolean isPaddleXaligned = ballCoords[0] >= paddleCoords[0] - PADDLE_HALFWIDTH && ballCoords[0] <= paddleCoords[0] + PADDLE_HALFWIDTH;
        boolean isPaddleYaligned = ballCoords[1] >= paddleCoords[1] - PADDLE_HALFHEIGHT && ballCoords[1] <= paddleCoords[1] + PADDLE_HALFHEIGHT;

        boolean isPaddleYcollided = Math.abs(paddleCoords[1] - ballCoords[1]) <= PADDLE_HALFHEIGHT + BALL_RADIUS;
        boolean isPaddleXcollided = Math.abs(paddleCoords[0] - ballCoords[0]) <= PADDLE_HALFWIDTH + BALL_RADIUS;

        if(isPaddleXaligned & isPaddleYcollided) ballVelocity[1] = -ballVelocity[1]; 
        
        if(isPaddleYaligned & isPaddleXcollided) ballVelocity[0] = -ballVelocity[0];
        

        for (double[] brick: BRICK_COORDS) {
            if (brick[2] == 0) continue;

            boolean isXaligned = ballCoords[0] >= brick[0] - BRICK_HALFWIDTH && ballCoords[0] <= brick[0] + BRICK_HALFWIDTH;
            boolean isYaligned = ballCoords[1] >= brick[1] - BRICK_HALFHEIGHT && ballCoords[1] <= brick[1] + BRICK_HALFHEIGHT;
            boolean isYcollided = Math.abs(brick[1] - ballCoords[1]) <= BRICK_HALFHEIGHT + BALL_RADIUS;
            boolean isXcollided = Math.abs(brick[0] - ballCoords[0]) <= BRICK_HALFWIDTH + BALL_RADIUS;
            
            if(isXaligned & isYcollided) {
                ballVelocity[1] = -ballVelocity[1];
                score = score + POINT_PER_BRICK;
                brick[2] = 0;
            };
            
            if(isYaligned & isXcollided) {
                ballVelocity[0] = -ballVelocity[0];
                score = score + POINT_PER_BRICK;
                brick[2] = 0;
            };
        }
    }
}