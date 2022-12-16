import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Main class: обрабатывает всю начальную графику (главное меню) и загружает
 * всё перед игрой, включая ActionListener, который считывает нажатия кнопок
 * "Play", "Help", Settings", "Load","Exit"
 */
public class Main implements ActionListener {

    // Экземпляр игры
    public static Level content;

    public String savefileName;

    // Правила игры
    String helpString = "\n -There are 7 solid, and 7 striped balls, "
            + "a black 8-ball, and a white cue-ball \n "
            + " -Player 1 plays solid balls and Player 2 plays striped balls"
            + " \n -A player is randomly chosen to break\n -If a ball is sunk, the player keeps playing until they miss "
            + "\n -Once they miss, it's the next player's turn \n -Sink all of the designated balls, and then shoot"
            + " at the 8-ball last to win \n-The 8-ball must be sunk last-sinking it before then will result"
            + " in an automatic loss \n -If the cue ball is sunk, the next player gets their turn with the ball in hand \n"
            + " -The cue ball must touch that player's type of ball (striped or solid), and the coloured ball that was hit"
            + " or the cue ball must touch a side of the table\n\nUse the left and right arrow keys or \"A\" and \"D\" to rotate the cue\n"
            + "Click and hold the mouse to power up the cue. The longer you hold, the more power in your shot\nLet go to release the cue";

    // Размеры экрана
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int WIDTH = (int) screenSize.getWidth();
    public static final int HEIGHT = (int) screenSize.getHeight();

    // Фреймы
    private final JFrame splashScreen = new JFrame();
    private final JFrame mainScreen = new JFrame();
    private JFrame playScreen = new JFrame();

    private Input input = new Input(playScreen);

    // Вызывается при первом запуске программы, инициирует главный конструктор

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Main start = new Main();
            }
        });
    }

    public static JButton button(String name) {
        JButton newButton = new JButton(name);
        newButton.setFocusPainted(false);
        newButton.setFont(new Font("Cooper Black", Font.ITALIC, 26));
        newButton.setFocusable(false);
        newButton.setBackground(new Color(30, 180, 30));
        newButton.setMaximumSize(new Dimension(225, 50));
        return newButton;
    }

    public Main() {

        // Настройка фреймов
        splashScreen.setSize(WIDTH, HEIGHT);
        splashScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainScreen.setSize(WIDTH, HEIGHT);
        playScreen.setVisible(false);

        mainScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainScreen.setUndecorated(true);
        mainScreen.setVisible(false);

        try {
            splashScreen.setContentPane(new JLabel(
                    new ImageIcon(ImageIO.read(new File("D:/Java programming/Pool-Game/resource/Images/Pool-Game SplashScreen.jpg"))
                            .getScaledInstance((int) WIDTH, (int) HEIGHT, Image.SCALE_SMOOTH))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        splashScreen.getContentPane().setLayout(new BorderLayout(5, 5));
        splashScreen.setUndecorated(true);
        splashScreen.setVisible(true);

        // Анимация - мигание метки на заставке
        BlinkLabel anyKeyLabel = new BlinkLabel("PRESS ANY KEY TO CONTINUE...");
        anyKeyLabel.setForeground(Color.WHITE);
        anyKeyLabel.setFont(new Font("Cooper Black", Font.PLAIN, 35));
        splashScreen.getContentPane().add(anyKeyLabel, BorderLayout.PAGE_END);
        anyKeyLabel.startBlinking();

        // Фоновая музыка
        final Sound bgMusic = new Sound();
        bgMusic.setVolume(-10);

        try {
            bgMusic.loadSound("D:/Java programming/Pool-Game/resource/Music/Main Music.wav");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        splashScreen.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                splashScreen.dispose();
                mainScreen.setVisible(true);

                try {
                    bgMusic.playSound();
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        });

        // Настройка главного меню
        try {
            mainScreen.setContentPane(new JLabel(
                    new ImageIcon(ImageIO.read(new File("D:/Java programming/Pool-Game/resource/Images/Main menu background.jpg"))
                            .getScaledInstance((int) WIDTH, (int) HEIGHT, Image.SCALE_SMOOTH))));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading the image!");
        }

        mainScreen.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Панелька для всех кнопок меню
        JPanel menuPane = new JPanel();
        menuPane.setBackground(new Color(0, 0, 0, 100));
        menuPane.setPreferredSize(new Dimension((int) WIDTH / 3, (int) HEIGHT - (int) HEIGHT / 5));
        menuPane.setLayout(new BoxLayout(menuPane, BoxLayout.Y_AXIS));
        menuPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Панельки для правил игры и настроек
        final JPanel helpPane = new JPanel();
        helpPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        helpPane.setLayout(new BoxLayout(helpPane, BoxLayout.Y_AXIS));
        helpPane.setPreferredSize(new Dimension((int) WIDTH / 3, (int) HEIGHT - (int) HEIGHT / 5));
        helpPane.setBackground(new Color(0, 0, 0, 125));
        helpPane.setOpaque(false);

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 125);
        mainScreen.getContentPane().add(menuPane, c);
        c.insets = new Insets(0, 125, 0, 0);
        c.gridx = 1;
        mainScreen.getContentPane().add(helpPane, c);

        JButton playButton = button("Play");
        playButton.setAlignmentX(menuPane.CENTER_ALIGNMENT);
        menuPane.add(Box.createRigidArea(new Dimension(0, 50)));
        menuPane.add(playButton);

        // Сделать клавишу Enter по умолчанию для запуска игры
        menuPane.getRootPane().setDefaultButton(playButton);

        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startBalls();
            }
        });

        menuPane.add(Box.createRigidArea(new Dimension(0, 75)));

        JButton helpButton = button("Help");
        helpButton.setAlignmentX(menuPane.CENTER_ALIGNMENT);
        menuPane.add(helpButton);

        menuPane.add(Box.createRigidArea(new Dimension(0, 75)));

        JButton settingsButton = button("Settings");
        settingsButton.setAlignmentX(menuPane.CENTER_ALIGNMENT);
        menuPane.add(settingsButton);

        menuPane.add(Box.createRigidArea(new Dimension(0, 75)));

        JButton loadButton = button("Load");
        loadButton.setAlignmentX(menuPane.CENTER_ALIGNMENT);
        menuPane.add(loadButton);
        menuPane.add(Box.createRigidArea(new Dimension(0, 75)));

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser("D:/Java programming/Pool-Game/resource/games");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(mainScreen);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    savefileName = fileChooser.getSelectedFile().getName();
                    startBalls();
                    content.loadGame("D:/Java programming/Pool-Game/resource/games/" + savefileName);
                }
            }
        });

        JButton exitButton = button("Exit");
        exitButton.setAlignmentX(menuPane.CENTER_ALIGNMENT);
        menuPane.add(exitButton);

        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpPane.removeAll();
                helpPane.setOpaque(true);
                JTextArea helpText = new JTextArea();
                JLabel helpTitle = new JLabel("How to Play classic 8-Ball Pool");
                helpTitle.setFont(new Font("High Tower Text", Font.BOLD, 24));
                helpTitle.setForeground(Color.white);
                helpTitle.setAlignmentX(helpPane.CENTER_ALIGNMENT);
                helpText.setText(helpString);
                helpText.setEditable(false);
                helpText.setFont(new Font("High Tower Text", Font.PLAIN, 20));
                helpText.setForeground(Color.WHITE);
                helpText.setHighlighter(null);
                helpText.setBackground(Color.BLACK);
                helpText.setLineWrap(true);
                helpText.setWrapStyleWord(true);
                JScrollPane helpScroll = new JScrollPane(helpText);
                helpScroll.setOpaque(false);
                helpPane.add(helpTitle);
                helpPane.add(helpScroll);

                mainScreen.revalidate();
                mainScreen.repaint();
            }
        });


        final JCheckBox checkMusic = new JCheckBox("Music");
        checkMusic.setSelected(true);

        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpPane.removeAll();
                helpPane.setOpaque(true);

                checkMusic.setBackground(Color.black);
                checkMusic.setForeground(Color.white);
                checkMusic.setFont(new Font("Cooper Black", Font.PLAIN, 28));
                checkMusic.setAlignmentX(helpPane.CENTER_ALIGNMENT);
                checkMusic.setFocusPainted(false);
                checkMusic.setMargin(new Insets(0, 20, 0, 20));

                helpPane.add(Box.createRigidArea(new Dimension(0, 75)));
                helpPane.add(checkMusic);
                mainScreen.revalidate();
                mainScreen.repaint();
            }
        });


        // Галочка для включения/выключения музыки
        checkMusic.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (checkMusic.isSelected() == true) {
                    try {
                        bgMusic.playSound();
                    } catch (Throwable e1) {
                        e1.printStackTrace();
                    }
                } else if (checkMusic.isSelected() == false) {
                    bgMusic.stopSound();
                }
            }
        });


        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    System.exit(0);
                }
            }
        });
    }

    public void startBalls() {
        // Инициализируем playScreen
        playScreen.setUndecorated(true);
        playScreen.setVisible(true);
        playScreen.setSize((int) WIDTH, (int) HEIGHT);

        // Создание экземпляра игры
        content = new Level();

        // Установка экземпляра игры на игровой фрейм (playScreen)
        playScreen.setContentPane(content);
        playScreen.getGlassPane().setVisible(true);

        Timer timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        playScreen.repaint();
    }
}