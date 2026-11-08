import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
public class Main extends JFrame {
    enum State { MENU, GAME }
    private State currentState = State.MENU;
    public BufferedImage bg, button_start, button_setting, button_exit;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    public Main() {
        try {
            bg = ImageIO.read(new File("src/img/1.jpg"));
            button_start = ImageIO.read(new File("src/img/pixel-game-button-collection-colorful-260nw-2675896493(6).jpg"));
            button_setting = ImageIO.read(new File("src/img/pixel-game-button-collection-colorful-260nw-2675896493 (4).jpg"));
            button_exit = ImageIO.read(new File("src/img/pixel-game-button-collection-colorful-260nw-2675896493 (5).jpg"));
        } catch (Exception e) {
            System.out.println("Ресурсы не найдены, активирован графический режим заглушек.");
        }
        setSize(1300, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Platformer Game");
        setResizable(false);
        menuPanel = new MenuPanel();
        gamePanel = new GamePanel();
        add(menuPanel);
        setVisible(true);
    }
    public void changeState(State newState) {
        this.currentState = newState;
        getContentPane().removeAll();
        if (currentState == State.GAME) {
            add(gamePanel);
            gamePanel.requestFocusInWindow();
            gamePanel.loadLevel(1); // Старт с первого уровня
        } else {
            add(menuPanel);
        }
        revalidate();
        repaint();
    }
    private class MenuPanel extends JPanel {
        public MenuPanel() {
            setFocusable(true);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int mx = e.getX();
                    int my = e.getY();
                    if (mx > 1000 && mx < 1200 && my > 350 && my < 450) {
                        System.out.println("play");
                        changeState(State.GAME);
                    }
                    if (mx > 1000 && mx < 1200 && my > 750 && my < 850) {
                        System.out.println("exit");
                        System.exit(0);
                    }
                }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            drawBtn(g, button_start, 1000, 350, Color.GREEN, "ИГРАТЬ");
            drawBtn(g, button_setting, 1000, 550, Color.ORANGE, "ОПЦИИ");
            drawBtn(g, button_exit, 1000, 750, Color.RED, "ВЫХОД");
        }
        private void drawBtn(Graphics g, BufferedImage img, int x, int y, Color c, String txt) {
            if (img != null) {
                g.drawImage(img, x, y, this);
            } else {
                g.setColor(c);
                g.fillRect(x, y, 200, 60);
                g.setColor(Color.WHITE);
                g.drawString(txt, x + 70, y + 35);
            }
        }
    }
    private class GamePanel extends JPanel implements ActionListener, KeyListener {
        private Timer gameTimer;
        private int currentLevel = 1;
        private Entity player;
        private ArrayList<Rectangle> platforms;
        private ArrayList<Entity> enemies;
        private boolean left = false, right = false;
        public GamePanel() {
            setBackground(Color.BLACK);
            setFocusable(true);
            addKeyListener(this);
            gameTimer = new Timer(16, this); // ~60 FPS
            gameTimer.start();
        }
        public void loadLevel(int level) {
            this.currentLevel = level;
            platforms = new ArrayList<>();
            enemies = new ArrayList<>();
            platforms.add(new Rectangle(0, 800, 1300, 100));
            if (level == 1) {
                player = new Entity(100, 600, 40, 60, Color.BLUE, 100); // Игрок
                platforms.add(new Rectangle(400, 620, 200, 20));
                platforms.add(new Rectangle(700, 480, 200, 20));
                enemies.add(new Entity(800, 400, 40, 60, Color.RED, 50)); // Враг 1
            } else if (level == 2) {
                player = new Entity(100, 600, 40, 60, Color.BLUE, 100);
                platforms.add(new Rectangle(300, 650, 150, 20));
                platforms.add(new Rectangle(550, 520, 150, 20));
                platforms.add(new Rectangle(800, 400, 200, 20));
                enemies.add(new Entity(600, 450, 40, 60, Color.RED, 60));
                enemies.add(new Entity(900, 320, 40, 60, Color.RED, 60));
            } else if (level == 3) {
                player = new Entity(100, 600, 40, 60, Color.BLUE, 100);
                platforms.add(new Rectangle(250, 600, 200, 20));
                platforms.add(new Rectangle(550, 600, 200, 20));
                platforms.add(new Rectangle(850, 450, 200, 20));
                enemies.add(new Entity(300, 500, 40, 60, Color.RED, 80));
                enemies.add(new Entity(600, 500, 40, 60, Color.RED, 80));
            } else if (level == 4) { 
                player = new Entity(100, 600, 40, 60, Color.BLUE, 120);
                platforms.add(new Rectangle(500, 550, 300, 20));
                enemies.add(new Entity(600, 450, 60, 90, Color.MAGENTA, 250)); // БОСС (больше здоровья)
            }
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentState != State.GAME) return;
            if (left) player.move(-6);
            if (right) player.move(6);
            player.updatePhysics(platforms);
            for (Entity enemy : enemies) {
                enemy.updatePhysics(platforms);
                enemy.aiBehavior(player);
            }
            enemies.removeIf(enemy -> enemy.hp <= 0);
            if (enemies.isEmpty()) {
                if (currentLevel < 4) {
                    System.out.println("Уровень пройден! Загрузка уровня " + (currentLevel + 1));
                    loadLevel(currentLevel + 1);
                } else {
                    System.out.println("Вы прошли всю игру!");
                    changeState(State.MENU);
                }
            }
            if (player.hp <= 0) {
                System.out.println("Игра окончена. Возврат в меню.");
                changeState(State.MENU);
            }
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Отрисовка интерфейса (Уровень и Здоровье)
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("УРОВЕНЬ: " + currentLevel + " / 4", 30, 40);
            g.drawString("ВАШЕ HP: " + player.hp, 30, 70);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("[F] или [L-SHIFT] — Атаковать врага вблизи", 30, 100);

            // Рисуем платформы
            g.setColor(Color.GRAY);
            for (Rectangle plat : platforms) {
                g.fillRect(plat.x, plat.y, plat.width, plat.height);
            }

            // Рисуем врагов
            for (Entity enemy : enemies) {
                enemy.draw(g);
            }

            // Рисуем игрока
            player.draw(g);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) left = true;
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) right = true;
            if ((key == KeyEvent.VK_SPACE || key == KeyEvent.VK_W) && !player.isJumping) {
                player.velY = -15; 
                player.isJumping = true;
            }
            if (key == KeyEvent.VK_F || key == KeyEvent.VK_SHIFT) {
                player.attack(enemies);
            }
            if (key == KeyEvent.VK_ESCAPE) {
                changeState(State.MENU);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) left = false;
            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) right = false;
        }
        @Override
        public void keyTyped(KeyEvent e) {}
    }
