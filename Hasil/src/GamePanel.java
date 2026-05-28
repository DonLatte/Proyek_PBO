import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    final int tileSize = 32;

    int[][] map = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,1,1,1,1},
        {1,0,1,1,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1},
        {1,0,1,1,0,0,0,0,0,0,0,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1},
        {1,0,1,1,1,1,1,1,1,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1},
        {1,0,1,1,1,1,1,1,1,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,1,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1},
        {1,1,1,1,1,1,0,0,1,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,1,1},
        {1,1,1,1,1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,1,1},
        {1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1},
        {1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1},
        {1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    final int screenWidth = 1504;
    final int screenHeight = 800;

    Thread gameThread;

    BufferedImage roadImage;

    BufferedImage playerUp;
    BufferedImage playerDown;
    BufferedImage playerLeft;
    BufferedImage playerRight;

    BufferedImage currentPlayerImage;

    int playerX = 1200;
    int playerY = 736;
    int playerSpeed = 4;

    boolean upPressed;
    boolean downPressed;
    boolean leftPressed;
    boolean rightPressed;

    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(this);
        this.setFocusable(true);

        getImages();

        startGameThread();
    }

    public void getImages() {

        try {

            roadImage = ImageIO.read(
                getClass().getResourceAsStream("/Asset/road2.jpg")
            );

            playerUp = ImageIO.read(
                getClass().getResourceAsStream("/Asset/up.png")
            );

            playerDown = ImageIO.read(
                getClass().getResourceAsStream("/Asset/down.png")
            );

            playerLeft = ImageIO.read(
                getClass().getResourceAsStream("/Asset/left.png")
            );

            playerRight = ImageIO.read(
                getClass().getResourceAsStream("/Asset/right.png")
            );

            currentPlayerImage = playerDown;

            System.out.println("Semua gambar berhasil dimuat!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        while (gameThread != null) {

            update();
            repaint();

            try {
                Thread.sleep(16);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {

        int nextX = playerX;
        int nextY = playerY;

        if (upPressed) {
            nextY -= playerSpeed;
        }

        if (downPressed) {
            nextY += playerSpeed;
        }

        if (leftPressed) {
            nextX -= playerSpeed;
        }

        if (rightPressed) {
            nextX += playerSpeed;
        }

        int leftCol = nextX / tileSize;
        int rightCol = (nextX + tileSize - 1) / tileSize;
        int topRow = nextY / tileSize;
        int bottomRow = (nextY + tileSize - 1) / tileSize;

        if (topRow >= 0 &&
            bottomRow < map.length &&
            leftCol >= 0 &&
            rightCol < map[0].length) {

            boolean collision =
                map[topRow][leftCol] == 1 ||
                map[topRow][rightCol] == 1 ||
                map[bottomRow][leftCol] == 1 ||
                map[bottomRow][rightCol] == 1;

            if (!collision) {
                playerX = nextX;
                playerY = nextY;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        for (int row = 0; row < map.length; row++) {

            for (int col = 0; col < map[row].length; col++) {

                if (map[row][col] == 1) {

                    g.setColor(Color.black);
                    g.fillRect(
                        col * tileSize,
                        row * tileSize,
                        tileSize,
                        tileSize
                    );

                } else {

                    if (roadImage != null) {

                        g.drawImage(
                            roadImage,
                            col * tileSize,
                            row * tileSize,
                            tileSize,
                            tileSize,
                            null
                        );

                    } else {

                        g.setColor(Color.gray);

                        g.fillRect(
                            col * tileSize,
                            row * tileSize,
                            tileSize,
                            tileSize
                        );
                    }
                }
            }
        }

        g.drawImage(
            currentPlayerImage,
            playerX,
            playerY,
            tileSize,
            tileSize,
            null
        );
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = true;
            currentPlayerImage = playerUp;
        }

        if (code == KeyEvent.VK_S) {
            downPressed = true;
            currentPlayerImage = playerDown;
        }

        if (code == KeyEvent.VK_A) {
            leftPressed = true;
            currentPlayerImage = playerLeft;
        }

        if (code == KeyEvent.VK_D) {
            rightPressed = true;
            currentPlayerImage = playerRight;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }

        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }

        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }

        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}