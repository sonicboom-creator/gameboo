import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public class Main extends JFrame {
    public BufferedImage bg, button_start, button_setting, button_exit;
    MouseListener ml = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getPoint().x > 1000 && e.getPoint().x < 1100 &&
                    e.getPoint().y > 300 && e.getPoint().y < 400) {
                new Main();
                System.out.println("play");
            }
            if (e.getPoint().x > 1000 && e.getPoint().x < 1100 &&
                    e.getPoint().y > 500 && e.getPoint().y < 600) {
                new Main();
                System.out.println("menu");
            }
            if (e.getPoint().x > 1000 && e.getPoint().x < 1100 &&
                    e.getPoint().y > 700 && e.getPoint().y < 800) {
                new Main();
                System.out.println("exit");
            }
            if (e.getPoint().x > 1000 && e.getPoint().x < 1100 &&
                    e.getPoint().y > 700 && e.getPoint().y < 800) {
                new Main();
                System.exit(0);
            }
        }
        @Override
        public void mousePressed(MouseEvent e) { }
        @Override
        public void mouseReleased(MouseEvent e) { }
        @Override
        public void mouseEntered(MouseEvent e) { }
        @Override
        public void mouseExited(MouseEvent e) { }
    };
    Main() {
        try {
            bg = ImageIO.read(new File("src/img/1.jpg"));
            button_start = ImageIO.read(new File("src/img/pixel-game-button-collection-colorful-260nw-2675896493(6).jpg"));
            button_setting = ImageIO.read(new File("src/img/pixel-game-button-collection-colorful-260nw-2675896493 (4).jpg"));
            button_exit = ImageIO.read(new File("src/img/pixel-game-button-collection-colorful-260nw-2675896493 (5).jpg"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setSize(200,500);
        setVisible(true);
        setTitle("game");
        addMouseListener(ml);
    }
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(bg, 0, 0, this);
        g.drawImage(button_start, 1000, 350, this);
        g.drawImage(button_setting, 1000, 550, this);
        g.drawImage(button_exit, 1000, 750, this);
    }
    public static void main(String[] args) {
        new Main();
    }
}