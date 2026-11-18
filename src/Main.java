import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
public class Main extends JFrame {
    public BufferedImage bg;
    public BufferedImage button_start;
    public BufferedImage button_setting;
    public BufferedImage button_exit;
    public BufferedImage gameBackgroundImage;
    public BufferedImage playerImage;
    public BufferedImage enemyImage;
    public BufferedImage npcImage;
    public BufferedImage platformImage;
    private static final String MENU_BG_PATH = "C:/My game/images/1 (1).jpg";      
    private static final String GAME_BG_PATH = "C:/My game/images/3 (1).jpg";      
    private static final String START_BUTTON_PATH = "C:/My game/images/pixel-game-button-collection-colorful-260nw-2675896493(6).jpg";   
    private static final String SETTINGS_BUTTON_PATH = "C:/My game/images/pixel-game-button-collection-colorful-260nw-2675896493 (4).jpg";
    private static final String EXIT_BUTTON_PATH = "C:/My game/images/pixel-game-button-collection-colorful-260nw-2675896493 (5).jpg";   
    private static final String PLAYER_PATH = "C:/My game/images/главный персонаж.png";         
    private static final String ENEMY_PATH = "C:/My game/images/Без названия29.png";        
    private static final String PLATFORM_PATH = "images/platform.png";    
    private static final boolean REMOVE_WHITE_SPRITE_BACKGROUND = true; 
    public int gameState = 0;
    public int windowWidth = 1200;
    public int windowHeight = 900;
    public Player player;
    public Enemy enemy;
    public ArrayList<Platform> platforms = new ArrayList<Platform>();
    public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    public ArrayList<Particle> particles = new ArrayList<Particle>();
    public Timer timer;
    private JPanel gamePanel;
    public Random random = new Random();
    public boolean left = false;
    public boolean right = false;
    public boolean crouching = false;
    public boolean attackPressed = false;
    public boolean dialogue = false;
    public boolean paused = false;
    public int score = 0;
    public int frameCounter = 0;
    public int menuSelection = 0;
    public int dialogueIndex = 0;
    public int settingsVolume = 5;
    public int settingsDifficulty = 1;
    public String[] dialogueLines = {
        "Hello! Welcome to this place.",
        "There are platforms ahead of you.",
        "Be careful when you meet an enemy.",
        "You can attack by pressing J.",
        "Good luck on your journey!"
    };
    MouseListener ml = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            if (gameState == 0) {
                if (inside(mouseX, mouseY, 880, 350, 150, 70)) {
                    startGame();
                }
                if (inside(mouseX, mouseY, 880, 550, 150, 70)) {
                    gameState = 2;
                }
                if (inside(mouseX, mouseY, 880, 750, 150, 70)) {
                    System.exit(0);
                }
            } else if (gameState == 2) {
                if (inside(mouseX, mouseY, 450, 650, 300, 70)) {
                    gameState = 0;
                }
            } else if (gameState == 1 && paused) {
                if (inside(mouseX, mouseY, 450, 400, 300, 65)) {
                    paused = false;
                }
                if (inside(mouseX, mouseY, 450, 490, 300, 65)) {
                    gameState = 0;
                    paused = false;
                }
            } else if (gameState == 3 || gameState == 4) {
                if (inside(mouseX, mouseY, 450, 550, 300, 70)) {
                    startGame();
                }
                if (inside(mouseX, mouseY, 450, 650, 300, 70)) {
                    gameState = 0;
                }
            }
            gamePanel.repaint();
        }
        @Override
        public void mousePressed(MouseEvent e) {
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        }
        @Override
        public void mouseEntered(MouseEvent e) {
        }
        @Override
        public void mouseExited(MouseEvent e) {
        }
    };
    KeyListener kl = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if (gameState == 0) {
                if (code == KeyEvent.VK_DOWN) {
                    menuSelection = (menuSelection + 1) % 3;
                }
                if (code == KeyEvent.VK_UP) {
                    menuSelection = (menuSelection + 2) % 3;
                }
                if (code == KeyEvent.VK_ENTER) {
                    selectMenuItem();
                }
            } else if (gameState == 1) {
                handleGameKeyPressed(code);
            } else if (gameState == 2) {
                handleSettingsKeyPressed(code);
            } else if (gameState == 3 || gameState == 4) {
                if (code == KeyEvent.VK_ENTER) {
                    startGame();
                }
                if (code == KeyEvent.VK_ESCAPE) {
                    gameState = 0;
                }
            }
            gamePanel.repaint();
        }
        @Override
        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                left = false;
            }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                right = false;
            }
            if (code == KeyEvent.VK_S) {
                crouching = false;
            }
            if (code == KeyEvent.VK_J) {
                attackPressed = false;
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
        }
    };
    public Main() {
        loadImages();
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    renderGame(g2);
                } finally {
                    g2.dispose();
                }
            }
        };
        gamePanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        gamePanel.setDoubleBuffered(true);
        gamePanel.addMouseListener(ml);
        gamePanel.addKeyListener(kl);
        gamePanel.setFocusable(true);
        setContentPane(gamePanel);
        pack(); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Pixel Adventure");
        setLocationRelativeTo(null);
        setVisible(true);
        gamePanel.requestFocusInWindow();
        timer = new Timer(16, e -> gameLoop());
        timer.start();
    }
    public void loadImages() {
        bg = loadImage(MENU_BG_PATH);
        gameBackgroundImage = loadImage(GAME_BG_PATH);
        button_start = loadImage(START_BUTTON_PATH);
        button_setting = loadImage(SETTINGS_BUTTON_PATH);
        button_exit = loadImage(EXIT_BUTTON_PATH);
        playerImage = loadSprite(PLAYER_PATH);
        enemyImage = loadSprite(ENEMY_PATH);
        platformImage = loadImage(PLATFORM_PATH);
    }
    private BufferedImage loadImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        String source = path.trim();
        if (source.startsWith("https://github.com/") && source.contains("/blob/")) {
            source = source.replace("https://github.com/", "https://raw.githubusercontent.com/")
                           .replaceFirst("/blob/", "/");
        }
        try {
            BufferedImage image;
            if (source.startsWith("https://") || source.startsWith("http://")) {
                URLConnection connection = URI.create(source).toURL().openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                try (InputStream input = connection.getInputStream()) {
                    image = ImageIO.read(input);
                }
            } else {
                File file = new File(source);
                if (!file.isFile()) {
                    System.err.println("[Картинки] Файл не найден: " + file.getAbsolutePath());
                    return null;
                }
                image = ImageIO.read(file);
            }
            if (image == null) {
                System.err.println("[Картинки] Это не PNG/JPG или файл повреждён: " + source);
                return null;
            }
            System.out.println("[Картинки] Загружено: " + source + " (" + image.getWidth() + "x"
                + image.getHeight() + ", "
                + (image.getColorModel().hasAlpha() ? "есть прозрачность" : "нет прозрачности") + ")");
            return image;
        } catch (Exception e) {
            System.err.println("[Картинки] Не удалось загрузить: " + source);
            System.err.println("[Картинки] Причина: " + e.getMessage());
            return null; // Если нет картинки, игра продолжает работать.
        }
    }
    private BufferedImage loadSprite(String path) {
        BufferedImage image = loadImage(path);
        if (image == null || !REMOVE_WHITE_SPRITE_BACKGROUND) {
            return image;
        }
        return removeEdgeWhiteBackground(image);
    }
    private BufferedImage removeEdgeWhiteBackground(BufferedImage original) {
        int w = original.getWidth();
        int h = original.getHeight();
        long count = (long) w * h;
        if (count > 16000000L) {
            return original;
        }
        int[] pixels = original.getRGB(0, 0, w, h, null, 0, w);
        // Если в PNG уже есть прозрачность, ничего менять не нужно.
        for (int pixel : pixels) {
            if ((pixel >>> 24) != 255) {
                return original;
            }
        }
        int whiteBorder = 0;
        int borderSize = 2 * w + 2 * Math.max(0, h - 2);
        for (int x = 0; x < w; x++) {
            if (isNearWhite(pixels[x])) whiteBorder++;
            if (h > 1 && isNearWhite(pixels[(h - 1) * w + x])) whiteBorder++;
        }
        for (int y = 1; y < h - 1; y++) {
            if (isNearWhite(pixels[y * w])) whiteBorder++;
            if (w > 1 && isNearWhite(pixels[y * w + w - 1])) whiteBorder++;
        }
        if (whiteBorder < Math.max(1, borderSize / 4)) {
            return original;
        }
        boolean[] visited = new boolean[pixels.length];
        int[] queue = new int[pixels.length];
        int head = 0;
        int tail = 0;
        for (int x = 0; x < w; x++) {
            int top = x;
            int bottom = (h - 1) * w + x;
            if (!visited[top] && isNearWhite(pixels[top])) {
                visited[top] = true;
                queue[tail++] = top;
            }
            if (!visited[bottom] && isNearWhite(pixels[bottom])) {
                visited[bottom] = true;
                queue[tail++] = bottom;
            }
        }
        for (int y = 1; y < h - 1; y++) {
            int left = y * w;
            int right = left + w - 1;
            if (!visited[left] && isNearWhite(pixels[left])) {
                visited[left] = true;
                queue[tail++] = left;
            }
            if (!visited[right] && isNearWhite(pixels[right])) {
                visited[right] = true;
                queue[tail++] = right;
            }
        }
        while (head < tail) {
            int p = queue[head++];
            // Удаляем цвет только у фона, который связан с границей.
            pixels[p] = 0x00000000;
            int x = p % w;
            int y = p / w;
            int[] neighbors = {
                x > 0 ? p - 1 : -1,
                x + 1 < w ? p + 1 : -1,
                y > 0 ? p - w : -1,
                y + 1 < h ? p + w : -1
            };
            for (int n : neighbors) {
                if (n >= 0 && !visited[n] && isNearWhite(pixels[n])) {
                    visited[n] = true;
                    queue[tail++] = n;
                }
            }
        }
        if (tail > 0) {
            BufferedImage cleaned = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            cleaned.setRGB(0, 0, w, h, pixels, 0, w);
            System.out.println("[Картинки] Удалён белый фон по краям: " + tail + " пикселей");
            return cleaned;
        }
        return original;
    }
    private static boolean isNearWhite(int pixel) {
        int r = (pixel >>> 16) & 255;
        int g = (pixel >>> 8) & 255;
        int b = pixel & 255;
        return r >= 235 && g >= 235 && b >= 235;
    }
    public boolean inside(int x, int y, int rx, int ry, int rw, int rh) {
        return x >= rx && x <= rx + rw && y >= ry && y <= ry + rh;
    }
    public void selectMenuItem() {
        if (menuSelection == 0) {
            startGame();
        } else if (menuSelection == 1) {
            gameState = 2;
        } else {
            System.exit(0);
        }
    }
    public void handleGameKeyPressed(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            paused = !paused;
            return;
        }
        if (paused) {
            return;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            left = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            right = true;
        }
        if (code == KeyEvent.VK_S) {
            crouching = true;
        }
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_UP) {
            player.jump();
        }
        if (code == KeyEvent.VK_J && !attackPressed) {
            player.attack(enemy, particles);
            attackPressed = true;
        }
    }
    public void handleSettingsKeyPressed(int code) {
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_ENTER) {
            gameState = 0;
        }
        if (code == KeyEvent.VK_LEFT) {
            settingsVolume = Math.max(0, settingsVolume - 1);
        }
        if (code == KeyEvent.VK_RIGHT) {
            settingsVolume = Math.min(10, settingsVolume + 1);
        }
        if (code == KeyEvent.VK_UP) {
            settingsDifficulty = Math.min(2, settingsDifficulty + 1);
        }
        if (code == KeyEvent.VK_DOWN) {
            settingsDifficulty = Math.max(0, settingsDifficulty - 1);
        }
    }
    public void startGame() {
        player = new Player(100, 510);
        enemy = new Enemy(850, 510);
        player.sprite = playerImage;
        enemy.sprite = enemyImage;
        platforms.clear();
        projectiles.clear();
        particles.clear();
        createLevel();
        score = 0;
        frameCounter = 0;
        dialogue = false;
        dialogueIndex = 0;
        paused = false;
        left = false;
        right = false;
        crouching = false;
        attackPressed = false;
        gameState = 1;
        gamePanel.requestFocusInWindow();
    }
    public void createLevel() {
        platforms.add(new Platform(0, 750, 1200, 100));
        platforms.add(new Platform(180, 560, 220, 25));
        platforms.add(new Platform(650, 430, 200, 25));
        platforms.add(new Platform(350, 300, 240, 25));
        platforms.add(new Platform(900, 540, 200, 25));
        platforms.add(new Platform(750, 180, 220, 25));
        platforms.add(new Platform(100, 390, 200, 25));
        platforms.add(new Platform(480, 620, 180, 25));
        platforms.add(new Platform(950, 320, 180, 25));
        for (Platform platform : platforms) {
            platform.sprite = platformImage;
        }
    }
    public void gameLoop() {
        if (gameState != 1 || paused) {
            gamePanel.repaint();
            return;
        }
        frameCounter++;
        updatePlayer();
        updateEnemy();
        updateProjectiles();
        updateParticles();
        updateWorldChecks();
        gamePanel.repaint();
    }
    public void updatePlayer() {
        player.left = left;
        player.right = right;
        player.crouching = crouching;
        player.update(platforms, windowWidth, windowHeight);
        if (player.health <= 0) {
            gameState = 4;
        }
    }
    public void updateEnemy() {
        if (enemy == null || enemy.health <= 0) {
            return;
        }
        enemy.update(player, platforms, projectiles, frameCounter);
        if (enemy.touches(player.getBounds()) && enemy.attackReady()) {
            player.takeDamage(enemy.damage);
            enemy.markAttack();
            createDamageParticles(player.x + player.width / 2, player.y + 20, Color.RED);
        }
    }
    public void updateProjectiles() {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update();
            if (projectile.isOutside(windowWidth, windowHeight)) {
                projectiles.remove(i);
            } else if (projectile.getBounds().intersects(player.getBounds()) && projectile.fromEnemy) {
                player.takeDamage(projectile.damage);
                createDamageParticles(player.x, player.y, Color.ORANGE);
                projectiles.remove(i);
            }
        }
    }
    public void updateParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.update();
            if (particle.life <= 0) {
                particles.remove(i);
            }
        }
    }
    public void updateWorldChecks() {
        if (enemy != null && enemy.health <= 0 && !enemy.rewardGiven) {
            score += 100;
            enemy.rewardGiven = true;
            createDamageParticles(enemy.x + 20, enemy.y + 20, Color.YELLOW);
        }
        if (player.y > windowHeight + 100) {
            player.takeDamage(1000);
        }
        if (score >= 100 && enemy.health <= 0) {
            gameState = 3;
        }
    }
    public void createDamageParticles(int x, int y, Color color) {
        for (int i = 0; i < 12; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 1 + random.nextDouble() * 4;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            particles.add(new Particle(x, y, vx, vy, color));
        }
    }
    private void renderGame(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (gameState == 0) {
            drawMenu(g2);
        } else if (gameState == 1) {
            drawGame(g2);
        } else if (gameState == 2) {
            drawSettings(g2);
        } else if (gameState == 3) {
            drawEndScreen(g2, true);
        } else if (gameState == 4) {
            drawEndScreen(g2, false);
        }
    }
    public void drawMenu(Graphics2D g) {
        drawBackground(g);
        g.setColor(new Color(15, 20, 35, 170));
        g.fillRoundRect(760, 170, 390, 660, 35, 35);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 58));
        g.drawString("PIXEL ADVENTURE", 120, 170);
        drawMenuButton(g, button_start, "START", 880, 350, menuSelection == 0);
        drawMenuButton(g, button_setting, "SETTINGS", 880, 550, menuSelection == 1);
        drawMenuButton(g, button_exit, "EXIT", 880, 750, menuSelection == 2);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Use mouse or arrows and Enter", 780, 250);
        g.drawString("A small platform adventure", 780, 290);
    }
    public void drawBackground(Graphics2D g) {
        if (bg != null) {
            g.drawImage(bg, 0, 0, windowWidth, windowHeight, this);
        } else {
            g.setPaint(new java.awt.GradientPaint(0, 0, new Color(20, 35, 75), 0, windowHeight, new Color(90, 150, 190)));
            g.fillRect(0, 0, windowWidth, windowHeight);
            g.setColor(new Color(255, 255, 255, 100));
            for (int i = 0; i < 35; i++) {
                int x = (i * 137) % windowWidth;
                int y = (i * 83) % 500;
                g.fillOval(x, y, 3, 3);
            }
        }
    }
    public void drawMenuButton(Graphics2D g, BufferedImage image, String text, int x, int y, boolean selected) {
        if (selected) {
            g.setColor(new Color(255, 220, 90, 100));
            g.fillRoundRect(x - 12, y - 10, 175, 85, 18, 18);
        }
        if (image != null) {
            g.drawImage(image, x, y, 150, 65, this);
        } else {
            g.setColor(new Color(50, 80, 130));
            g.fillRoundRect(x, y, 150, 65, 18, 18);
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, 150, 65, 18, 18);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(text, x + 30, y + 40);
        }
    }
    public void drawGame(Graphics2D g) {
        drawGameBackground(g);
        drawPlatforms(g);
        drawEnemy(g);
        drawPlayer(g);
        drawProjectiles(g);
        drawParticles(g);
        drawInterface(g);
    }
    public void drawGameBackground(Graphics2D g) {
        if (gameBackgroundImage != null) {
            g.drawImage(gameBackgroundImage, 0, 0, windowWidth, windowHeight, this);
            return;
        }
        g.setPaint(new java.awt.GradientPaint(0, 0, new Color(115, 190, 235), 0, windowHeight, new Color(35, 95, 130)));
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(930, 80, 100, 100);
        g.setColor(new Color(255, 255, 255, 45));
        for (int i = 0; i < 8; i++) {
            int x = i * 180 - 30;
            g.fillOval(x, 560 + (i % 2) * 35, 230, 110);
        }
    }
    public void drawPlatforms(Graphics2D g) {
        for (Platform platform : platforms) {
            platform.draw(g);
        }
    }
    public void drawEnemy(Graphics2D g) {
        if (enemy != null) {
            enemy.draw(g);
        }
    }
    public void drawPlayer(Graphics2D g) {
        if (player != null) {
            player.draw(g);
        }
    }
    public void drawProjectiles(Graphics2D g) {
        for (Projectile projectile : projectiles) {
            projectile.draw(g);
        }
    }
    public void drawParticles(Graphics2D g) {
        for (Particle particle : particles) {
            particle.draw(g);
        }
    }
    public void drawInterface(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(20, 20, 290, 145, 18, 18);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Health", 40, 50);
        drawHealthBar(g, 110, 34, 170, 20, player.health, player.maxHealth);
        g.drawString("Potions: " + player.potions, 40, 85);
        g.drawString("Score: " + score, 40, 115);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("A/D move   W/Space jump   J attack", 40, 145);
    }
    public void drawHealthBar(Graphics2D g, int x, int y, int width, int height, int value, int max) {
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(x, y, width, height, 8, 8);
        int current = Math.max(0, Math.min(width, width * value / Math.max(1, max)));
        g.setColor(value > max / 2 ? new Color(60, 210, 90) : new Color(230, 65, 55));
        g.fillRoundRect(x, y, current, height, 8, 8);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, width, height, 8, 8);
    }
    public void drawPause(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 55));
        g.drawString("PAUSED", 475, 280);
        drawSimpleButton(g, "CONTINUE", 450, 400, 300, 65);
        drawSimpleButton(g, "MAIN MENU", 450, 490, 300, 65);
    }
    public void drawSettings(Graphics2D g) {
        drawBackground(g);
        g.setColor(new Color(0, 0, 0, 175));
        g.fillRoundRect(300, 150, 600, 600, 30, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("SETTINGS", 455, 240);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Volume: " + settingsVolume + " / 10", 420, 360);
        g.drawString("Use LEFT and RIGHT arrows", 420, 405);
        g.drawString("Difficulty: " + difficultyName(), 420, 485);
        g.drawString("Use UP and DOWN arrows", 420, 530);
        drawSimpleButton(g, "BACK", 450, 650, 300, 65);
    }
    public String difficultyName() {
        if (settingsDifficulty == 0) {
            return "Easy";
        }
        if (settingsDifficulty == 1) {
            return "Normal";
        }
        return "Hard";
    }
    public void drawEndScreen(Graphics2D g, boolean won) {
        g.setColor(new Color(10, 15, 35));
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(won ? new Color(100, 230, 130) : new Color(240, 90, 80));
        g.setFont(new Font("Arial", Font.BOLD, 58));
        g.drawString(won ? "YOU WIN!" : "GAME OVER", 420, 300);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 25));
        g.drawString("Final score: " + score, 490, 390);
        drawSimpleButton(g, "PLAY AGAIN", 450, 550, 300, 70);
        drawSimpleButton(g, "MAIN MENU", 450, 650, 300, 70);
    }
    public void drawSimpleButton(Graphics2D g, String text, int x, int y, int width, int height) {
        g.setColor(new Color(55, 95, 150));
        g.fillRoundRect(x, y, width, height, 18, 18);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, width, height, 18, 18);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString(text, x + 35, y + height / 2 + 8);
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new Main());
    }
}
class Player {
    public int x;
    public int y;
    public int width = 220;
    public int height = 190;
    public int standingHeight = 190;
    public int crouchingHeight = 140;
    public int health = 100;
    public int maxHealth = 100;
    public int potions = 2;
    public int attackDamage = 25;
    public int invulnerability = 0;
    public int attackCooldown = 0;
    public int animationFrame = 0;
    public int animationCounter = 0;
    public double speed = 5.0;
    public double jumpPower = -16.0;
    public double gravity = 0.75;
    public double velocityX = 0;
    public double velocityY = 0;
    public boolean onGround = false;
    public boolean left = false;
    public boolean right = false;
    public boolean crouching = false;
    public boolean facingRight = true;
    public boolean attacking = false;
    public boolean damaged = false;
    public BufferedImage sprite; 
    public Player(int startX, int startY) {
        x = startX;
        y = startY;
    }
    public void update(ArrayList<Platform> platforms, int screenWidth, int screenHeight) {
        updateTimers();
        updateMovement();
        updateCrouch();
        updateHorizontalPosition(screenWidth);
        updateVerticalPosition(platforms);
        updateAnimation();
        checkScreenBounds(screenHeight);
    }
    public void updateTimers() {
        if (invulnerability > 0) {
            invulnerability--;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (invulnerability == 0) {
            damaged = false;
        }
    }
    public void updateMovement() {
        velocityX = 0;
        if (left && !right) {
            velocityX = -speed;
            facingRight = false;
        }
        if (right && !left) {
            velocityX = speed;
            facingRight = true;
        }
        if (crouching) {
            velocityX *= 0.45;
        }
    }
    public void updateCrouch() {
        int oldHeight = height;
        if (crouching && onGround) {
            height = crouchingHeight;
        } else {
            height = standingHeight;
        }
        if (height < oldHeight) {
            y += oldHeight - height;
        }
        if (height > oldHeight) {
            y -= height - oldHeight;
        }
    }
    public void updateHorizontalPosition(int screenWidth) {
        x += (int) velocityX;
        if (x < 0) {
            x = 0;
        }
        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }
    public void updateVerticalPosition(ArrayList<Platform> platforms) {
        int oldY = y;
        velocityY += gravity;
        if (velocityY > 18) {
            velocityY = 18;
        }
        y += (int) velocityY;
        onGround = false;
        for (Platform platform : platforms) {
            Rectangle playerBounds = getBounds();
            Rectangle platformBounds = platform.getBounds();
            boolean horizontal = playerBounds.x < platformBounds.x + platformBounds.width
                && playerBounds.x + playerBounds.width > platformBounds.x;
            boolean fallingThroughTop = oldY + height <= platform.y
                && y + height >= platform.y;
            if (horizontal && fallingThroughTop && velocityY >= 0) {
                y = platform.y - height;
                velocityY = 0;
                onGround = true;
            }
            boolean hittingBottom = oldY >= platform.y + platform.height
                && y <= platform.y + platform.height
                && horizontal
                && velocityY < 0;
            if (hittingBottom) {
                y = platform.y + platform.height;
                velocityY = 0;
            }
        }
    }
    public void checkScreenBounds(int screenHeight) {
        if (y > screenHeight + 150) {
            health = 0;
        }
    }
    public void updateAnimation() {
        animationCounter++;
        if (animationCounter >= 8) {
            animationCounter = 0;
            animationFrame++;
            if (animationFrame > 3) {
                animationFrame = 0;
            }
        }
    }
    public void jump() {
        if (onGround) {
            velocityY = jumpPower;
            onGround = false;
        }
    }
    public void attack(Enemy enemy, ArrayList<Particle> particles) {
        if (attackCooldown > 0 || enemy == null || enemy.health <= 0) {
            return;
        }
        attacking = true;
        attackCooldown = 28;
        Rectangle area = getAttackBounds();
        if (area.intersects(enemy.getBounds())) {
            enemy.takeDamage(attackDamage);
            enemy.knockBack(facingRight ? 8 : -8);
            addAttackParticles(particles);
        }
    }
    public Rectangle getAttackBounds() {
        int attackX = facingRight ? x + width : x - 65;
        return new Rectangle(attackX, y + 12, 65, height - 18);
    }
    public void addAttackParticles(ArrayList<Particle> particles) {
        int centerX = facingRight ? x + width + 10 : x - 10;
        int centerY = y + height / 2;
        for (int i = 0; i < 8; i++) {
            double vx = (Math.random() - 0.5) * 6;
            double vy = (Math.random() - 0.5) * 6;
            particles.add(new Particle(centerX, centerY, vx, vy, new Color(255, 230, 100)));
        }
    }
    public void takeDamage(int amount) {
        if (invulnerability > 0 || health <= 0) {
            return;
        }
        health -= amount;
        if (health < 0) {
            health = 0;
        }
        invulnerability = 55;
        damaged = true;
    }
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
    public void usePotion() {
        if (potions > 0 && health < maxHealth) {
            potions--;
            heal(35);
        }
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    public boolean isMoving() {
        return left || right;
    }
    public void draw(Graphics2D g) {
        drawShadow(g);
        if (sprite != null) {
            if (facingRight) {
                g.drawImage(sprite, x, y, width, height, null);
            } else {
                g.drawImage(sprite, x + width, y, x, y + height,
                    0, 0, sprite.getWidth(), sprite.getHeight(), null);
            }
        } else {
            drawBody(g);
            drawHead(g);
            drawFace(g);
            drawLegs(g);
        }
        if (attacking && attackCooldown > 18) {
            drawAttackEffect(g);
        }
    }
    public void drawShadow(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 55));
        g.fillOval(x - 5, y + height - 3, width + 10, 12);
    }
    public void drawBody(Graphics2D g) {
        g.setColor(new Color(45, 65, 175));
        g.fillRoundRect(x + 4, y + 20, width - 8, height - 22, 10, 10);
        g.setColor(new Color(80, 110, 220));
        g.fillRect(x + 8, y + 27, width - 16, 8);
    }
    public void drawHead(Graphics2D g) {
        g.setColor(new Color(235, 185, 145));
        g.fillOval(x + 6, y - 4, width - 12, 30);
        g.setColor(new Color(75, 45, 35));
        g.fillArc(x + 5, y - 8, width - 10, 22, 0, 180);
    }
    public void drawFace(Graphics2D g) {
        g.setColor(Color.BLACK);
        int eyeX = facingRight ? x + width - 15 : x + 10;
        g.fillOval(eyeX, y + 8, 4, 4);
    }
    public void drawLegs(Graphics2D g) {
        g.setColor(new Color(35, 40, 100));
        int legOffset = isMoving() ? animationFrame % 2 * 3 : 0;
        g.fillRect(x + 8, y + height - 12, 10, 14 + legOffset);
        g.fillRect(x + width - 18, y + height - 12, 10, 14 - legOffset);
    }
    public void drawAttackEffect(Graphics2D g) {
        g.setColor(new Color(255, 245, 170, 180));
        Rectangle area = getAttackBounds();
        g.drawArc(area.x, area.y, area.width, area.height, facingRight ? 270 : 90, 180);
    }
}
class Platform {
    public int x;
    public int y;
    public int width;
    public int height;
    public Color surfaceColor = new Color(65, 165, 75);
    public Color sideColor = new Color(115, 75, 45);
    public boolean moving = false;
    public int movementRange = 0;
    public int movementSpeed = 1;
    public int startX;
    public int direction = 1;
    public BufferedImage sprite; 
    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        startX = x;
    }
    public void update() {
        if (!moving || movementRange <= 0) {
            return;
        }
        x += movementSpeed * direction;
        if (x > startX + movementRange) {
            direction = -1;
        }
        if (x < startX - movementRange) {
            direction = 1;
        }
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    public void draw(Graphics2D g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
            return;
        }
        g.setColor(sideColor);
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(surfaceColor);
        g.fillRoundRect(x, y, width, 9, 8, 8);
        g.setColor(new Color(255, 255, 255, 55));
        g.drawLine(x + 8, y + 3, x + width - 8, y + 3);
        g.setColor(new Color(40, 35, 30, 80));
        for (int i = 0; i < width / 35; i++) {
            g.fillOval(x + 12 + i * 35, y + 15, 5, 4);
        }
    }
}
class Enemy {
    public int x;
    public int y;
    public int width = 220;
    public int height = 190;
    public int health = 190;
    public int maxHealth = 140;
    public int damage = 12;
    public int attackTimer = 0;
    public int attackDelay = 75;
    public int animationCounter = 0;
    public int animationFrame = 0;
    public int direction = -1;
    public int patrolLeft;
    public int patrolRight;
    public double velocityY = 0;
    public double gravity = 0.7;
    public double knockback = 0;
    public boolean onGround = false;
    public boolean rewardGiven = false;
    public boolean alert = false;
    public BufferedImage sprite; 
    public Enemy(int startX, int startY) {
        x = startX;
        y = startY;
        patrolLeft = Math.max(0, startX - 150);
        patrolRight = startX + 150;
    }
    public void update(Player player, ArrayList<Platform> platforms, ArrayList<Projectile> projectiles, int frame) {
        updateTimers();
        updateDetection(player);
        updateMovement(player);
        updateGravity(platforms);
        updateAnimation();
        tryRangedAttack(player, projectiles, frame);
    }
    public void updateTimers() {
        if (attackTimer > 0) {
            attackTimer--;
        }
    }
    public void updateDetection(Player player) {
        int distance = Math.abs(player.x - x);
        alert = distance < 360 && Math.abs(player.y - y) < 160;
    }
    public void updateMovement(Player player) {
        if (health <= 0) {
            return;
        }
        if (alert) {
            direction = player.x < x ? -1 : 1;
            if (Math.abs(player.x - x) > 65) {
                x += direction * 2;
            }
        } else {
            x += direction;
            if (x < patrolLeft) {
                direction = 1;
            }
            if (x > patrolRight) {
                direction = -1;
            }
        }
        if (knockback != 0) {
            x += (int) knockback;
            knockback *= 0.75;
            if (Math.abs(knockback) < 0.5) {
                knockback = 0;
            }
        }
    }
    public void updateGravity(ArrayList<Platform> platforms) {
    int oldY = y;
    velocityY += gravity;
    if (velocityY > 15) {
        velocityY = 15;
    }
    y += (int) velocityY;
    onGround = false;
    for (Platform platform : platforms) {
        boolean horizontal =
            x < platform.x + platform.width &&
            x + width > platform.x;
        boolean falling =
            oldY + height <= platform.y &&
            y + height >= platform.y;
        if (horizontal && falling && velocityY >= 0) {
            y = platform.y - height;
            velocityY = 0;
            onGround = true;
        }
    }
    if (y + height > 800) {
        y = 800 - height;
        velocityY = 0;
        onGround = true;
    }
    if (x < 0) {
        x = 0;
    }
    if (x + width > 1200) {
        x = 1200 - width;
    }
}
    public void updateAnimation() {
        animationCounter++;
        if (animationCounter >= 10) {
            animationCounter = 0;
            animationFrame = (animationFrame + 1) % 4;
        }
    }
    public void tryRangedAttack(Player player, ArrayList<Projectile> projectiles, int frame) {
        if (!alert || attackTimer > 0 || health <= 0) {
            return;
        }
        if (Math.abs(player.x - x) > 130 && Math.abs(player.x - x) < 330) {
            int directionToPlayer = player.x < x ? -1 : 1;
            projectiles.add(new Projectile(x + width / 2, y + 28, directionToPlayer * 5, 0, true, damage));
            attackTimer = attackDelay;
        }
    }
    public boolean touches(Rectangle rectangle) {
        return getBounds().intersects(rectangle);
    }
    public boolean attackReady() {
        return attackTimer <= 0;
    }
    public void markAttack() {
        attackTimer = attackDelay;
    }
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }
    public void knockBack(double amount) {
        knockback = amount;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    public void draw(Graphics2D g) {
        if (health <= 0) {
            drawDefeated(g);
            return;
        }
        drawShadow(g);
        if (sprite != null) {
            if (direction >= 0) {
                g.drawImage(sprite, x, y, width, height, null);
            } else {
                g.drawImage(sprite, x + width, y, x, y + height,
                    0, 0, sprite.getWidth(), sprite.getHeight(), null);
            }
        } else {
            drawBody(g);
            drawHead(g);
            drawEyes(g);
        }
        drawHealth(g);
        if (alert) {
            drawAlert(g);
        }
    }
    public void drawShadow(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 55));
        g.fillOval(x - 4, y + height - 3, width + 8, 11);
    }
    public void drawBody(Graphics2D g) {
        g.setColor(new Color(155, 40, 45));
        g.fillRoundRect(x + 3, y + 20, width - 6, height - 20, 10, 10);
        g.setColor(new Color(210, 65, 60));
        g.fillRect(x + 8, y + 28, width - 16, 8);
    }
    public void drawHead(Graphics2D g) {
        g.setColor(new Color(180, 105, 80));
        g.fillOval(x + 5, y - 5, width - 10, 31);
        g.setColor(new Color(70, 35, 30));
        g.fillArc(x + 4, y - 9, width - 8, 23, 0, 180);
    }
    public void drawEyes(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x + 11, y + 8, 6, 5);
        g.fillOval(x + width - 17, y + 8, 6, 5);
    }
    public void drawHealth(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(x - 4, y - 23, width + 8, 8);
        g.setColor(Color.RED);
        g.fillRect(x - 4, y - 23, (width + 8) * health / maxHealth, 8);
        g.setColor(Color.WHITE);
        g.drawRect(x - 4, y - 23, width + 8, 8);
    }
    public void drawAlert(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("!", x + 15, y - 32);
    }
    public void drawDefeated(Graphics2D g) {
        g.setColor(new Color(80, 80, 80));
        g.fillOval(x, y + height - 18, width, 18);
    }
}
class Projectile {
    public double x;
    public double y;
    public double velocityX;
    public double velocityY;
    public int size = 14;
    public int damage;
    public boolean fromEnemy;
    public int life = 180;
    public Projectile(double x, double y, double velocityX, double velocityY, boolean fromEnemy, int damage) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.fromEnemy = fromEnemy;
        this.damage = damage;
    }
    public void update() {
        x += velocityX;
        y += velocityY;
        life--;
    }
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, size, size);
    }
    public boolean isOutside(int width, int height) {
        return life <= 0 || x < -size || x > width + size || y < -size || y > height + size;
    }
    public void draw(Graphics2D g) {
        g.setColor(fromEnemy ? new Color(255, 110, 60) : new Color(255, 235, 90));
        g.fillOval((int) x, (int) y, size, size);
        g.setColor(new Color(255, 255, 255, 150));
        g.drawOval((int) x, (int) y, size, size);
    }
}
class Particle {
    public double x;
    public double y;
    public double velocityX;
    public double velocityY;
    public int life = 35;
    public int maxLife = 35;
    public int size = 5;
    public Color color;
    public Particle(double x, double y, double velocityX, double velocityY, Color color) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.color = color;
    }
    public void update() {
        x += velocityX;
        y += velocityY;
        velocityY += 0.12;
        life--;
        if (size > 1 && life % 5 == 0) {
            size--;
        }
    }
    public void draw(Graphics2D g) {
        if (life <= 0) {
            return;
        }
        int alpha = Math.max(0, Math.min(255, life * 255 / maxLife));
        Color drawColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        g.setColor(drawColor);
        g.fillOval((int) x, (int) y, size, size);
    }
}
