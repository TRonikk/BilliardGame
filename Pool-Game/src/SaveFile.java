import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.RandomAccessFile;

// Сохранение в файл

public class SaveFile {
    private RandomAccessFile raf;
    private String path;

    public SaveFile(Level game, String path) {
        this.path = path;

        try {
            raf = new RandomAccessFile(path, "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }

        write(game);
    }

    // Запись игровых данных в файл
    public void write(Level game) {
        try {
            // Кол-во очков
            raf.writeInt(game.player1.getPoints());
            raf.writeInt(game.player1.getPoints());

            // Чей ход
            raf.writeBoolean(game.player1.isTurn());
            raf.writeBoolean(game.player1.isTurn());

            for (int i = 0; i < 16; i++) {
                raf.writeDouble(game.getBall(i).getX());
                raf.writeDouble(game.getBall(i).getY());
                System.out.println("X(" + i + ") = " + game.getBall(i).getX() + " Y(" + i + ") = " + game.getBall(i).getY());
            }
        } catch (IOException e) {
            System.out.println("Error writing to binary file");
            e.printStackTrace();
        }
    }

    // Считывание игровых данных игроков
    public static Character[] readCharacterInfo(String path) {
        Character[] players = new Character[2];
        int[] pointAmount = new int[2];
        boolean[] characterTurn = new boolean[2];

        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            // Очки
            pointAmount[0] = raf.readInt();
            pointAmount[1] = raf.readInt();

            // Чей ход
            characterTurn[0] = raf.readBoolean();
            characterTurn[1] = raf.readBoolean();
        } catch (IOException e) {
            System.err.println("Error reading in save file");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading game file!");
        }

        players[0] = new Character("Player 1", characterTurn[0], pointAmount[0], true);
        players[1] = new Character("Player 2", characterTurn[1], pointAmount[1], true);

        return players;
    }

    // Считывание игровых данных шаров
    public static Ball[] readBallInfo(String path) {
        Ball[] ball = new Ball[16];
        double[] xPos = new double[16];
        double[] yPos = new double[16];

        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");

            // Очки
            raf.readInt();
            raf.readInt();

            // Чей ход
            raf.readBoolean();
            raf.readBoolean();

            for (int i = 0; i < 16; i++) {
                xPos[i] = raf.readDouble();
                yPos[i] = raf.readDouble();
                System.out.println("New X(" + i + ") = " + xPos[i] + " New Y(" + i + ") = " + yPos[i]);
            }
        } catch (IOException e) {
            System.err.println("Error reading in save file");
            e.printStackTrace();
        }

        ball[0] = new Ball(xPos[0], yPos[0], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.WHITE, true, 0);
        ball[1] = new Ball(xPos[1], yPos[1], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.YELLOW, false, 9);
        ball[2] = new Ball(xPos[2], yPos[2], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.RED, true, 7);
        ball[3] = new Ball(xPos[3], yPos[3], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.PURPLE, false, 12);
        ball[4] = new Ball(xPos[4], yPos[4], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.RED, false, 15);
        ball[5] = new Ball(xPos[5], yPos[5], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.BLACK, true, 8);
        ball[6] = new Ball(xPos[6], yPos[6], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.YELLOW, true, 1);
        ball[7] = new Ball(xPos[7], yPos[7], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.GREEN, true, 6);
        ball[8] = new Ball(xPos[8], yPos[8], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.BLUE, false, 10);
        ball[9] = new Ball(xPos[9], yPos[9], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.BROWN, true, 3);
        ball[10] = new Ball(xPos[10], yPos[10], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.GREEN, false, 14);
        ball[11] = new Ball(xPos[11], yPos[11], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.BROWN, false, 11);
        ball[12] = new Ball(xPos[12], yPos[12], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.BLUE, true, 2);
        ball[13] = new Ball(xPos[13], yPos[13], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.ORANGE, false, 13);
        ball[14] = new Ball(xPos[14], yPos[14], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.PURPLE, true, 4);
        ball[15] = new Ball(xPos[15], yPos[15], Level.INIT_RADIUS, Level.INIT_MASS, new Speed(0, 0), Level.ORANGE, true, 5);

        return ball;
    }
}
