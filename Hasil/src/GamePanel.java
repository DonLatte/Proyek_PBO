import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * GamePanel — panel utama game "Horror Escape"
 *
 * Sistem Camera:
 *  - Layar mengikuti posisi pemain (viewport terpusat pada karakter)
 *  - Hanya area sekitar pemain yang terlihat di layar utama
 *
 * Minimap (pojok kanan bawah):
 *  - Menampilkan seluruh layout map dalam ukuran kecil
 *  - Posisi pemain ditandai dengan titik merah
 *  - Posisi hantu ditandai dengan titik putih pucat
 *
 * Fitur Horror:
 *  - Efek flickering layar (lampu berkedip)
 *  - Bloodstain / noda darah di peta
 *  - Entitas hantu yang berpatroli
 *  - HUD: stamina bar + pesan horror
 *  - Efek fog overlay atmosferik
 *  - Jumpscare berbasis proximity
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {

    // ─── Konstanta Ukuran ────────────────────────────────────────────────────
    static final int TILE_SIZE     = 32;
    static final int SCREEN_WIDTH  = 1504;
    static final int SCREEN_HEIGHT = 800;

    // ─── Peta (0 = jalan/koridor, 1 = dinding) ───────────────────────────────
    final int[][] map = {
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

    // ─── Game Thread ─────────────────────────────────────────────────────────
    Thread gameThread;

    // ─── Aset Gambar ─────────────────────────────────────────────────────────
    BufferedImage roadImage;
    BufferedImage playerUp, playerDown, playerLeft, playerRight;
    BufferedImage currentPlayerImage;

    // ─── Posisi Pemain (koordinat dunia / world-space) ───────────────────────
    int playerX     = 1200;
    int playerY     = 736;
    int playerSpeed = 4;

    boolean upPressed, downPressed, leftPressed, rightPressed;

    // ─── Stamina (sprint) ────────────────────────────────────────────────────
    boolean  shiftPressed;
    float    stamina     = 100f;
    static final float STAMINA_MAX   = 100f;
    static final float STAMINA_DRAIN = 0.4f;
    static final float STAMINA_REGEN = 0.15f;
    boolean  exhausted   = false;

    // ─── Flickering Layar ────────────────────────────────────────────────────
    Random  rng          = new Random();
    float   flickerAlpha = 0f;
    int     flickerTimer = 0;
    boolean flickerActive = false;

    // ─── Pesan Horror ────────────────────────────────────────────────────────
    static final String[] HORROR_MESSAGES = {
        "Kamu tidak sendirian di sini...",
        "Sesuatu memperhatikanmu dari kegelapan.",
        "Jangan menoleh ke belakang.",
        "Suara langkah itu bukan milikmu.",
        "Pintu keluar semakin jauh.",
        "Ia sudah menunggumu.",
        "Darahnya masih segar.",
        "Berlarilah.",
    };
    String  currentMessage  = HORROR_MESSAGES[0];
    int     messageTimer    = 0;
    int     messageInterval = 240;
    int     messageIndex    = 0;
    float   messageAlpha    = 1f;

    // ─── Bloodstain ──────────────────────────────────────────────────────────
    static class Bloodstain {
        int x, y, size;
        float alpha;
        Bloodstain(int x, int y, int size, float alpha) {
            this.x = x; this.y = y; this.size = size; this.alpha = alpha;
        }
    }
    List<Bloodstain> bloodstains = new ArrayList<>();

    // ─── Entitas Hantu ───────────────────────────────────────────────────────
    static class Ghost {
        float x, y, speedX, speedY, alpha;
        int   patrolTimer;
        Ghost(float x, float y, float sx, float sy) {
            this.x = x; this.y = y;
            this.speedX = sx; this.speedY = sy;
            this.patrolTimer = 120;
            this.alpha = 0.55f;
        }
    }
    List<Ghost> ghosts = new ArrayList<>();

    // ─── Jumpscare ───────────────────────────────────────────────────────────
    boolean jumpscare      = false;
    int     jumpscareTimer = 0;
    float   jumpscarePulse = 0f;

    // ─── Fog overlay ─────────────────────────────────────────────────────────
    float fogPulse = 0f;

    // ─── Minimap ─────────────────────────────────────────────────────────────
    static final int MINI_TILE   = 5;   // piksel per tile di minimap
    static final int MINI_MARGIN = 12;  // jarak dari tepi layar
    static final int MINI_BORDER = 2;

    // ─── Konstruktor ─────────────────────────────────────────────────────────
    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(this);
        this.setFocusable(true);

        loadImages();
        initBloodstains();
        initGhosts();
        startGameThread();
    }

    // ─── Inisialisasi ────────────────────────────────────────────────────────

    private void loadImages() {
        try {
            roadImage   = ImageIO.read(getClass().getResourceAsStream("/Asset/road2.jpg"));
            playerUp    = ImageIO.read(getClass().getResourceAsStream("/Asset/up.png"));
            playerDown  = ImageIO.read(getClass().getResourceAsStream("/Asset/down.png"));
            playerLeft  = ImageIO.read(getClass().getResourceAsStream("/Asset/left.png"));
            playerRight = ImageIO.read(getClass().getResourceAsStream("/Asset/right.png"));
            currentPlayerImage = playerDown;
            System.out.println("[Horror Escape] Semua aset berhasil dimuat.");
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("[Horror Escape] Gagal memuat aset: " + e.getMessage());
        }
    }

    private void initBloodstains() {
        for (int i = 0; i < 18; i++) {
            int row, col;
            do {
                row = rng.nextInt(map.length);
                col = rng.nextInt(map[0].length);
            } while (map[row][col] != 0);

            int px    = col * TILE_SIZE + rng.nextInt(TILE_SIZE);
            int py    = row * TILE_SIZE + rng.nextInt(TILE_SIZE);
            int size  = 8 + rng.nextInt(16);
            float a   = 0.3f + rng.nextFloat() * 0.5f;
            bloodstains.add(new Bloodstain(px, py, size, a));
        }
    }

    private void initGhosts() {
        int[][] starts = {
            { 9 * TILE_SIZE,  2 * TILE_SIZE },
            {25 * TILE_SIZE,  4 * TILE_SIZE },
            {13 * TILE_SIZE, 11 * TILE_SIZE },
            {35 * TILE_SIZE, 14 * TILE_SIZE },
            { 3 * TILE_SIZE, 21 * TILE_SIZE },
        };
        for (int[] pt : starts) {
            float sx = (rng.nextBoolean() ? 1 : -1) * (1 + rng.nextFloat());
            float sy = (rng.nextBoolean() ? 1 : -1) * (1 + rng.nextFloat());
            ghosts.add(new Ghost(pt[0], pt[1], sx, sy));
        }
    }

    // ─── Game Loop ───────────────────────────────────────────────────────────

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        final long frameTime = 1_000_000_000L / 60;
        long lastTime = System.nanoTime();
        while (gameThread != null) {
            long now = System.nanoTime();
            if (now - lastTime >= frameTime) {
                lastTime = now;
                update();
                repaint();
            }
        }
    }

    // ─── Update ──────────────────────────────────────────────────────────────

    public void update() {
        updatePlayer();
        updateGhosts();
        updateFlicker();
        updateFog();
        updateHUD();
        checkJumpscare();
    }

    private void updatePlayer() {
        boolean moving = upPressed || downPressed || leftPressed || rightPressed;
        int speed = playerSpeed;

        if (shiftPressed && !exhausted && moving) {
            speed    = playerSpeed * 2;
            stamina -= STAMINA_DRAIN;
            if (stamina <= 0) { stamina = 0; exhausted = true; }
        } else {
            stamina += STAMINA_REGEN;
            if (stamina >= STAMINA_MAX) stamina = STAMINA_MAX;
            if (exhausted && stamina > 30f) exhausted = false;
        }

        int nextX = playerX;
        int nextY = playerY;
        if (upPressed)    nextY -= speed;
        if (downPressed)  nextY += speed;
        if (leftPressed)  nextX -= speed;
        if (rightPressed) nextX += speed;

        if (!isSolidCollision(nextX, nextY)) {
            playerX = nextX;
            playerY = nextY;
        }
    }

    private boolean isSolidCollision(int x, int y) {
        int lc = x / TILE_SIZE;
        int rc = (x + TILE_SIZE - 1) / TILE_SIZE;
        int tr = y / TILE_SIZE;
        int br = (y + TILE_SIZE - 1) / TILE_SIZE;

        if (tr < 0 || br >= map.length || lc < 0 || rc >= map[0].length) return true;
        return map[tr][lc] == 1 || map[tr][rc] == 1 || map[br][lc] == 1 || map[br][rc] == 1;
    }

    private void updateGhosts() {
        for (Ghost g : ghosts) {
            float nx = g.x + g.speedX;
            float ny = g.y + g.speedY;
            boolean hitX = isSolidCollision((int) nx, (int) g.y);
            boolean hitY = isSolidCollision((int) g.x, (int) ny);
            if (hitX) g.speedX = -g.speedX; else g.x = nx;
            if (hitY) g.speedY = -g.speedY; else g.y = ny;

            g.patrolTimer--;
            if (g.patrolTimer <= 0) {
                g.patrolTimer = 80 + rng.nextInt(160);
                if (rng.nextBoolean()) g.speedX = -g.speedX;
                if (rng.nextBoolean()) g.speedY = -g.speedY;
            }
            g.alpha = 0.3f + 0.3f * (float) Math.sin(System.nanoTime() / 400_000_000.0);
        }
    }

    private void updateFlicker() {
        if (flickerActive) {
            flickerTimer--;
            flickerAlpha = 0.15f + 0.15f * rng.nextFloat();
            if (flickerTimer <= 0) { flickerActive = false; flickerAlpha = 0f; }
        } else {
            if (rng.nextInt(300) == 0) { flickerActive = true; flickerTimer = 3 + rng.nextInt(8); }
        }
    }

    private void updateFog() {
        fogPulse = (float)(0.06 + 0.04 * Math.sin(System.nanoTime() / 1_500_000_000.0));
    }

    private void updateHUD() {
        messageTimer++;
        if (messageTimer >= messageInterval) {
            messageTimer = 0;
            messageIndex = (messageIndex + 1) % HORROR_MESSAGES.length;
            currentMessage = HORROR_MESSAGES[messageIndex];
            messageInterval = 180 + rng.nextInt(180);
        }
        float ratio = (float) messageTimer / messageInterval;
        messageAlpha = (ratio < 0.2f) ? ratio / 0.2f : (ratio > 0.8f) ? (1f - ratio) / 0.2f : 1f;
    }

    private void checkJumpscare() {
        if (jumpscare) {
            jumpscareTimer--;
            jumpscarePulse = (float) Math.sin(jumpscareTimer * 0.6) * 0.8f;
            if (jumpscareTimer <= 0) { jumpscare = false; jumpscarePulse = 0f; }
            return;
        }
        for (Ghost g : ghosts) {
            if (Math.hypot(playerX - g.x, playerY - g.y) < TILE_SIZE * 1.5) {
                jumpscare = true; jumpscareTimer = 40;
                System.out.println("[Horror Escape] JUMPSCARE!");
                break;
            }
        }
    }

    // ─── Kamera: hitung offset agar pemain selalu di tengah layar ────────────

    /** Offset kamera dalam piksel (world → screen: screenX = worldX - camX). */
    private int cameraX() {
        int cx = playerX + TILE_SIZE / 2 - SCREEN_WIDTH  / 2;
        int maxCX = map[0].length * TILE_SIZE - SCREEN_WIDTH;
        return Math.max(0, Math.min(cx, maxCX));
    }

    private int cameraY() {
        int cy = playerY + TILE_SIZE / 2 - SCREEN_HEIGHT / 2;
        int maxCY = map.length * TILE_SIZE - SCREEN_HEIGHT;
        return Math.max(0, Math.min(cy, maxCY));
    }

    // ─── Rendering ───────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int camX = cameraX();
        int camY = cameraY();

        // Terapkan translasi kamera sekali untuk semua elemen dunia
        g.translate(-camX, -camY);

        drawMap(g, camX, camY);
        drawBloodstains(g);
        drawGhosts(g);
        drawPlayer(g);

        // Kembalikan translasi sebelum menggambar HUD (koordinat layar)
        g.translate(camX, camY);

        drawFogOverlay(g);
        drawFlicker(g);
        drawHUD(g);
        drawMinimap(g, camX, camY);
        drawJumpscare(g);
    }

    /** Gambar hanya tile yang terlihat di viewport + satu tile buffer di setiap sisi. */
    private void drawMap(Graphics2D g, int camX, int camY) {
        int startCol = Math.max(0, camX / TILE_SIZE - 1);
        int endCol   = Math.min(map[0].length - 1, (camX + SCREEN_WIDTH)  / TILE_SIZE + 1);
        int startRow = Math.max(0, camY / TILE_SIZE - 1);
        int endRow   = Math.min(map.length - 1,    (camY + SCREEN_HEIGHT) / TILE_SIZE + 1);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int px = col * TILE_SIZE;
                int py = row * TILE_SIZE;

                if (map[row][col] == 1) {
                    GradientPaint wallGrad = new GradientPaint(
                        px, py, new Color(25, 20, 20),
                        px + TILE_SIZE, py + TILE_SIZE, new Color(8, 5, 5)
                    );
                    g.setPaint(wallGrad);
                    g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                    g.setColor(new Color(40, 30, 30, 60));
                    g.drawLine(px, py, px + TILE_SIZE, py + TILE_SIZE);
                } else {
                    if (roadImage != null) {
                        g.drawImage(roadImage, px, py, TILE_SIZE, TILE_SIZE, null);
                        g.setColor(new Color(0, 0, 0, 55));
                        g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                    } else {
                        g.setColor(new Color(40, 38, 35));
                        g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
    }

    private void drawBloodstains(Graphics2D g) {
        for (Bloodstain b : bloodstains) {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, b.alpha));
            g.setColor(new Color(139, 0, 0));
            g.fillOval(b.x - b.size / 2, b.y - b.size / 2, b.size, (int)(b.size * 0.6));
            g.setColor(new Color(100, 0, 0));
            for (int i = 0; i < 3; i++) g.fillOval(b.x + b.size / 2 + i * 3, b.y - 2 + i, 3, 2);
            g.setComposite(old);
        }
    }

    private void drawGhosts(Graphics2D g) {
        for (Ghost ghost : ghosts) {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ghost.alpha));
            int gx = (int) ghost.x, gy = (int) ghost.y;
            g.setColor(new Color(200, 220, 200));
            g.fillOval(gx + 8, gy, 16, 16);
            g.fillRect(gx + 6, gy + 14, 20, 18);
            for (int i = 0; i < 4; i++) {
                int wave = (int)(3 * Math.sin(System.nanoTime() / 200_000_000.0 + i));
                g.fillOval(gx + 4 + i * 7, gy + 30 + wave, 8, 6);
            }
            g.setColor(new Color(220, 30, 30));
            g.fillOval(gx + 10, gy + 5, 4, 5);
            g.fillOval(gx + 18, gy + 5, 4, 5);
            g.setComposite(old);
        }
    }

    private void drawPlayer(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(playerX + 4, playerY + TILE_SIZE - 6, TILE_SIZE - 8, 8);
        if (currentPlayerImage != null) {
            g.drawImage(currentPlayerImage, playerX, playerY, TILE_SIZE, TILE_SIZE, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(playerX, playerY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void drawFogOverlay(Graphics2D g) {
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fogPulse));
        g.setColor(new Color(10, 8, 6));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setComposite(old);
    }

    private void drawFlicker(Graphics2D g) {
        if (!flickerActive) return;
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flickerAlpha));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setComposite(old);
    }

    private void drawHUD(Graphics2D g) {
        // ── Stamina bar ──────────────────────────────────────────────────────
        int barW = 160, barH = 12, barX = 20, barY = SCREEN_HEIGHT - 36;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(barX - 2, barY - 2, barW + 4, barH + 4, 6, 6);
        g.setColor(exhausted ? new Color(180, 30, 30) : new Color(180, 20, 20));
        g.fillRoundRect(barX, barY, (int)(barW * stamina / STAMINA_MAX), barH, 4, 4);
        g.setColor(new Color(120, 0, 0));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(barX, barY, barW, barH, 4, 4);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(new Color(200, 160, 160));
        g.drawString("STAMINA", barX, barY - 4);

        // ── Pesan horror ─────────────────────────────────────────────────────
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, messageAlpha));
        g.setFont(new Font("Serif", Font.ITALIC, 18));
        FontMetrics fm = g.getFontMetrics();
        int msgX = (SCREEN_WIDTH - fm.stringWidth(currentMessage)) / 2;
        int msgY = SCREEN_HEIGHT - 20;
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(currentMessage, msgX + 1, msgY + 1);
        g.setColor(new Color(200, 50, 50));
        g.drawString(currentMessage, msgX, msgY);
        g.setComposite(old);

        // ── Kontrol ──────────────────────────────────────────────────────────
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.setColor(new Color(100, 80, 80, 180));
        g.drawString("WASD: Gerak  SHIFT: Sprint", SCREEN_WIDTH - 200, 18);
    }

    /**
     * Minimap di pojok kanan bawah.
     *
     * Cara kerja:
     *  - Setiap tile di map digambar sebagai persegi MINI_TILE × MINI_TILE piksel.
     *  - Warna: hitam = dinding, abu gelap = koridor.
     *  - Kotak putih transparan = viewport pemain saat ini.
     *  - Titik merah terang = posisi pemain.
     *  - Titik putih kecil = posisi hantu.
     */
    private void drawMinimap(Graphics2D g, int camX, int camY) {
        int cols    = map[0].length;
        int rows    = map.length;
        int miniW   = cols * MINI_TILE;
        int miniH   = rows * MINI_TILE;

        // Posisi pojok kiri atas minimap di layar
        int ox = SCREEN_WIDTH  - miniW - MINI_MARGIN;
        int oy = SCREEN_HEIGHT - miniH - MINI_MARGIN;

        // ── Latar & border ───────────────────────────────────────────────────
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.82f));
        g.setColor(new Color(5, 3, 3));
        g.fillRect(ox - MINI_BORDER, oy - MINI_BORDER,
                   miniW + MINI_BORDER * 2, miniH + MINI_BORDER * 2);

        // ── Tile map ─────────────────────────────────────────────────────────
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (map[row][col] == 1) {
                    g.setColor(new Color(18, 12, 12));
                } else {
                    g.setColor(new Color(65, 55, 50));
                }
                g.fillRect(ox + col * MINI_TILE, oy + row * MINI_TILE,
                           MINI_TILE, MINI_TILE);
            }
        }

        // ── Viewport rect (area yang terlihat di layar utama) ────────────────
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
        g.setColor(new Color(255, 220, 180));
        int vx = (int)((float) camX / (map[0].length * TILE_SIZE) * miniW);
        int vy = (int)((float) camY / (map.length  * TILE_SIZE) * miniH);
        int vw = (int)((float) SCREEN_WIDTH  / (map[0].length * TILE_SIZE) * miniW);
        int vh = (int)((float) SCREEN_HEIGHT / (map.length   * TILE_SIZE) * miniH);
        g.fillRect(ox + vx, oy + vy, vw, vh);

        // ── Hantu (titik putih kecil) ─────────────────────────────────────────
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        for (Ghost ghost : ghosts) {
            int gx = (int)((ghost.x / (map[0].length * TILE_SIZE)) * miniW);
            int gy = (int)((ghost.y / (map.length   * TILE_SIZE)) * miniH);
            g.setColor(new Color(180, 200, 180));
            g.fillOval(ox + gx - 1, oy + gy - 1, 3, 3);
        }

        // ── Pemain (titik merah) ──────────────────────────────────────────────
        int px = (int)((float)(playerX + TILE_SIZE / 2) / (map[0].length * TILE_SIZE) * miniW);
        int py = (int)((float)(playerY + TILE_SIZE / 2) / (map.length   * TILE_SIZE) * miniH);
        g.setColor(new Color(230, 30, 30));
        g.fillOval(ox + px - 2, oy + py - 2, 5, 5);

        // ── Border tepi minimap ───────────────────────────────────────────────
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        g.setColor(new Color(90, 40, 40));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(ox - MINI_BORDER, oy - MINI_BORDER,
                   miniW + MINI_BORDER * 2 - 1, miniH + MINI_BORDER * 2 - 1);

        // ── Label ─────────────────────────────────────────────────────────────
        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.setColor(new Color(140, 80, 80));
        g.drawString("MAP", ox, oy - MINI_BORDER - 3);

        g.setComposite(old);
    }

    private void drawJumpscare(Graphics2D g) {
        if (!jumpscare) return;
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            Math.min(Math.abs(jumpscarePulse), 0.75f)));
        g.setColor(new Color(180, 0, 0));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setComposite(old);

        g.setFont(new Font("Serif", Font.BOLD, 72));
        g.setColor(new Color(255, 255, 255, (int)(200 * Math.abs(jumpscarePulse))));
        String txt = "!!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(txt, (SCREEN_WIDTH - fm.stringWidth(txt)) / 2, SCREEN_HEIGHT / 2 + 24);
    }

    // ─── Key Listener ────────────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> { upPressed    = true; currentPlayerImage = playerUp;    }
            case KeyEvent.VK_S -> { downPressed  = true; currentPlayerImage = playerDown;  }
            case KeyEvent.VK_A -> { leftPressed  = true; currentPlayerImage = playerLeft;  }
            case KeyEvent.VK_D -> { rightPressed = true; currentPlayerImage = playerRight; }
            case KeyEvent.VK_SHIFT -> shiftPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W     -> upPressed    = false;
            case KeyEvent.VK_S     -> downPressed  = false;
            case KeyEvent.VK_A     -> leftPressed  = false;
            case KeyEvent.VK_D     -> rightPressed = false;
            case KeyEvent.VK_SHIFT -> shiftPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}