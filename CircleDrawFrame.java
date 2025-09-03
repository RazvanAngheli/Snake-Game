package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Point;

public class CircleDrawFrame {

    private class Animation implements Runnable {
        private Random random = new Random();
        private boolean shouldGenerateNewFruit = true;

        @Override
        public void run() {
            while (true) {
                if (x + dx < 0 || x + dx + size > drawingArea.getWidth() ||
                        y + dy < 0 || y + dy + size > drawingArea.getHeight()) {
                    dx = 0;
                    dy = 0;
                    gameOver = true;
                }

                moveSnake();
                checkCollisions();
                drawingArea.repaint();

                try {
                    Thread.sleep(pause);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void checkCollisions() {
            // verifica dca bilutele sunt atinse
            for (int i = 0; i < bilute.size(); i++) {
                Point biluta = bilute.get(i);
                if (x >= biluta.x && x <= biluta.x + size &&
                        y >= biluta.y && y <= biluta.y + size) {
                    bilute.remove(i);
                    growSnake();
                    shouldGenerateNewFruit = true;
                }
            }

            // bilutele se genereaza in alte locuri
            if (shouldGenerateNewFruit) {
                int randomX = random.nextInt(drawingArea.getWidth() - size);
                int randomY = random.nextInt(drawingArea.getHeight() - size);
                bilute.add(new Point(randomX, randomY));
                shouldGenerateNewFruit = false;
            }
        }
    }

    private class CircleDrawListener implements MouseListener, KeyListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            // click
            System.out.println("Click");
        }

        @Override
        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
            drawingArea.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // dupa ce dai click
            System.out.println("Release");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // mouse-ul intra in chenar
            System.out.println("Entered");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // cand scoti mouse-ul din chenar
            System.out.println("Exited");
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mousePressed(e); //apasam mouse-ul
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // miscarea mouse-ului
            System.out.println("Moved(x=" + e.getX() + ",y=" + e.getY() + ")");
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            switch (c){
                case 'y' -> snakeColor = Color.YELLOW;
                case 'r' -> snakeColor = Color.RED;
                case 'b' -> snakeColor = Color.BLUE;
                case 'g' -> snakeColor = Color.GREEN;
                case 'd' -> snakeColor = Color.BLACK;
                case 's' -> {dx = 0; dy = 0;} // opreste sarpele
                case '+' -> size += 8; // creste marimea sarpelui
                case '-' -> size = (size > 8) ? size - 8 : size; // reduce marimea sarpelui
            }

            drawingArea.repaint();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            switch(code){
                case KeyEvent.VK_LEFT -> {dx = - speed; dy = 0;}
                case KeyEvent.VK_UP -> {dx = 0; dy = - speed;}
                case KeyEvent.VK_RIGHT -> {dx = speed; dy = 0;}
                case KeyEvent.VK_DOWN -> {dx = 0; dy = speed;}
            }

            drawingArea.repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // scrie cand dai release la o tasta
            System.out.println("Key Released");
        }
    }

    int x, y, size = 10, speed = 3, dx = 0, dy = 0, pause = 50;
    boolean gameOver = false;
    Color backgroundColor = Color.GREEN;
    Color snakeColor = Color.BLACK;
    private ArrayList<Point> snakeSegments = new ArrayList<>();
    private ArrayList<Point> bilute = new ArrayList<>();

    JFrame frame = new JFrame("Circle Draw");

    JPanel drawingArea = new JPanel() {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(backgroundColor);
            // sterge tot si scrie game over
            g.clearRect(0, 0, getWidth(), getHeight());
            if (gameOver) {
                g.setFont(new Font("Comic Sans MS", Font.ITALIC, 16));
                g.drawString("GAME OVER!", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            g.setColor(snakeColor);
            for (Point segment : snakeSegments) {
                g.fillOval(segment.x, segment.y, size, size);
            }

            g.setColor(Color.RED);
            for (Point biluta : bilute) {
                g.fillOval(biluta.x, biluta.y, size, size);
            }
        }
    };

    public CircleDrawFrame() {
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 1));
        frame.add(drawingArea);
        CircleDrawListener listener = new CircleDrawListener();
        drawingArea.addMouseListener(listener);
        drawingArea.addMouseMotionListener(listener);
        frame.addKeyListener(listener);
        frame.setVisible(true);
        new Thread(new Animation()).start();
    }

    private void growSnake() {
        // marim sarpele
        Point tail = snakeSegments.get(snakeSegments.size() - 1);
        for (int i = 0; i < 3; i++) {
            snakeSegments.add(new Point(tail.x, tail.y));
        }
    }

    private void moveSnake() {
        // mutam capul șarpelui in directia specificata
        x += dx;
        y += dy;
        snakeSegments.add(0, new Point(x, y));
        if (snakeSegments.size() > 1) {
            snakeSegments.remove(snakeSegments.size() - 1); // sterge ultimul segment
        }
    }

    public static void main(String[] args) {
        new CircleDrawFrame();
    }
}
