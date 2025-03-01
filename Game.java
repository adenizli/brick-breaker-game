import stdlib.StdDraw;
import java.awt.Color;

public class Game {
    enum ESTATUS {
        INIT,
        PLAYING,
        GAME_OVER,
        PAUSED,
    }

    enum EPADDLE_DIRECTIONS {
        LEFT,
        RIGHT
    }
    
    int score = 0;
    
    final double[] CANVAS_SIZE = {800,400};
    final double[] CANVAS_SCALE = {800,400};
    
    final double[][] BRICK_COORDS = {{0,10}};
    final Color[] BRICK_COLORS = { 
        new Color(255, 0, 0), 
        new Color(220, 20, 60),
        new Color(178, 34, 34), 
        new Color(139, 0, 0),
        new Color(255, 69, 0), 
        new Color(165, 42, 42)
    };
    final double BRICK_HALFWIDTH = 50; // Brick half width
    final double BRICK_HALFHEIGHT = 10; // Brick half height

    final double PADDLE_HALFWIDTH = 60; // Paddle half width
    final double PADDLE_HALFHEIGHT = 5; // Paddle half height
    final double PADDLE_SPEED = 20; // Paddle speed
    final Color PADDLE_COLOR = new Color(128, 128, 128); // Paddle color
    double[] paddle_pos = {400, 5}; // Initial position of the center of the paddle
    
    final Color BALL_COLOR = new Color(15, 82, 186);
    final double BALL_RADIUS = 8;
    double shootingAngle = 0;
    double[] ballVelocity = {10,7};
    double[] ballCoords = {400,18};

    // Game Components (These can be changed for custom scenarios)
    // 2D array to store center coordinates of bricks in the format {x, y}
    // double[][] brick_coordinates = new double[][]{
    // {250, 320},{350, 320},{450, 320},{550, 320},
    // {150,300},{250, 300},{350, 300},{450, 300},{550, 300},{650, 300},
    // {50, 280},{150, 280},{250, 280},{350, 280},{450, 280},{550, 280},{650, 280},{750, 280},
    // {50, 260},{150, 260},{250, 260},{350, 260},{450, 260},{550, 260},{650, 260},{750, 260},
    // {50, 240},{150, 240},{250, 240},{350, 240},{450, 240},{550, 240},{650, 240},{750, 240},
    // {150, 220},{250, 220},{350, 220},{450, 220},{550, 220},{650, 220},
    // {250, 200},{350, 200},{450, 200},{550, 200}};
    // Brick colors
    Color [] brick_colors = new Color[] {
    BRICK_COLORS[0], BRICK_COLORS[1], BRICK_COLORS[2], BRICK_COLORS[3],
    BRICK_COLORS[2], BRICK_COLORS[4], BRICK_COLORS[3], BRICK_COLORS[0], BRICK_COLORS[4], BRICK_COLORS[5],
    BRICK_COLORS[5], BRICK_COLORS[0], BRICK_COLORS[1], BRICK_COLORS[5], BRICK_COLORS[2], BRICK_COLORS[3], BRICK_COLORS[0], BRICK_COLORS[4],
    BRICK_COLORS[1], BRICK_COLORS[3], BRICK_COLORS[2], BRICK_COLORS[4], BRICK_COLORS[0], BRICK_COLORS[5], BRICK_COLORS[2], BRICK_COLORS[1],
    BRICK_COLORS[4], BRICK_COLORS[0], BRICK_COLORS[5], BRICK_COLORS[1], BRICK_COLORS[2], BRICK_COLORS[3], BRICK_COLORS[0], BRICK_COLORS[5],
    BRICK_COLORS[1], BRICK_COLORS[4], BRICK_COLORS[0], BRICK_COLORS[5], BRICK_COLORS[1], BRICK_COLORS[2],
    BRICK_COLORS[3], BRICK_COLORS[2], BRICK_COLORS[3], BRICK_COLORS[0]};

    public void main() {
        StdDraw.setCanvasSize(800, 400);
        StdDraw.setXscale(0.0, CANVAS_SCALE[0]);
        StdDraw.setYscale(0.0, CANVAS_SCALE[1]);
        StdDraw.enableDoubleBuffering();
        while(true) {
            this.render();
            this.engine();
        }
    }

    private void render() {
        StdDraw.clear(StdDraw.AQUA);
        this.drawBall();
        // this.drawBricks();
        // this.drawPaddle();
        StdDraw.show(16);
    }

    private void drawBall() {
        StdDraw.circle(ballCoords[0], ballCoords[1], BALL_RADIUS);
    }

    private void engine() {
        this.moveBall();
    }

    private void moveBall() {
        ballCoords[0] += ballVelocity[0];
        ballCoords[1] += ballVelocity[1];

        // Right wall hit check
        if(CANVAS_SIZE[0] - ballCoords[0] <= BALL_RADIUS ) {
            ballVelocity[0] = -ballVelocity[0];
        }

        // Upper wall hit check
        if(CANVAS_SIZE[1] - ballCoords[1] <= BALL_RADIUS ) {
            ballVelocity[1] = -ballVelocity[1];
        }

        // Left wall hit check
        if(ballCoords[0] - BALL_RADIUS <= 0 ) {
            ballVelocity[0] = -ballVelocity[0];
        }

        // Bottom wall hit check
        if(ballCoords[1] - BALL_RADIUS <= 0 ) {
            ballVelocity[1] = -ballVelocity[1];
        }
    }
}