import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Level.java используется для отображения внешнего вида игрового стола.
 * Проверяет наличие столкновений и нужным образом их обрабатывает.
 */

public class Level extends JPanel {
	
	// BufferedImages содержат изображения для отображения бильярдного стола
	private final BufferedImage wooden_tile;
	private final BufferedImage wooden_tile_rotated90;
	private final BufferedImage black_dot;
	private final BufferedImage table_grass;
	private final BufferedImage graphic_cue;
	
	// LinkList содержит шары
	public LinkList ballList;

	// Для проверки на столкновения используется массив объектов Ball
	public Ball[] ball = new Ball[BALLS];

	private Cue cue = new Cue(this);

	// Игроки
	Character player1 = new Character("Player 1", true, 0, true);
	Character player2 = new Character("Player 2", false, 0, false);


	// Установка границ для шаров (стены, в которые попадают шары)
	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int WIDTH = (int) screenSize.getWidth();
	public static final int HEIGHT = (int) screenSize.getHeight();

	public static final int BALLS = 22;

	// Переменная для сохранения файла
	SaveFile saveFile;

	// Переменные столкновения
	private double next_collision;
	private Ball first;
	private Ball second;

	JLabel p1;
	JLabel p2;

	private static boolean paused = false;
	private static boolean queued_collision_update = false;

	public static final double INIT_RADIUS = 15;
	public static final double INIT_MASS = 5;

	public static final Color YELLOW = new Color(225, 175, 0);
	public static final Color BLUE = new Color(1, 78, 146);
	public static final Color RED = new Color(247, 0, 55);
	public static final Color PURPLE = new Color(77, 30, 110);
	public static final Color ORANGE = new Color(255, 97, 36);
	public static final Color GREEN = new Color(16, 109, 62);
	public static final Color BROWN = new Color(129, 30, 33);
	public static final Color BLACK = new Color(20, 20, 20);
	public static final Color WHITE = new Color(255, 255, 255);

	boolean collisionOccured = false;

	double METER_TO_PIXEL = (800 / 2.84);
	int TABLE_WIDTH = (int) (1.624 * METER_TO_PIXEL);
	int TABLE_HEIGHT = (int) (3.048 * METER_TO_PIXEL);
	int PLAY_WIDTH = (int) (1.42 * METER_TO_PIXEL);
	int PLAY_HEIGHT = (int) (2.84 * METER_TO_PIXEL);
	int WIDTH_GAP = (TABLE_WIDTH - PLAY_WIDTH);
	int HEIGHT_GAP = (TABLE_HEIGHT - PLAY_HEIGHT);

	double dx = WIDTH_GAP / 6 + INIT_RADIUS + 4;
	double dy = HEIGHT_GAP / 6 + INIT_RADIUS + 4;

	double centerX = WIDTH_GAP / 2 + PLAY_WIDTH / 2;
	double centerY = HEIGHT_GAP / 2 + PLAY_HEIGHT / 2;

	double initialPosX = Main.WIDTH - 400;
	double initialPosY = Main.HEIGHT / 2;

	// Конструктор загружает графические файлы, задает действия кнопок, создает игровые шары
	public Level() {

		setLayout(new FlowLayout(FlowLayout.RIGHT));

		wooden_tile = loadTextures("D:/Java programming/Pool-Game/resource/Images/wooden_tile.png");
		wooden_tile_rotated90 = loadTextures("D:/Java programming/Pool-Game/resource/Images/wooden_tile_rotated90.png");
		black_dot = loadTextures("D:/Java programming/Pool-Game/resource/Images/black_dot.png");
		table_grass = loadTextures("D:/Java programming/Pool-Game/resource/Images/table_grass.png");
		graphic_cue = loadTextures("D:/Java programming/Pool-Game/resource/Images/cue.png");

		p1 = new JLabel("Player 1: " + player1.points);
		p2 = new JLabel("Player 2: " + player2.points);
		p1.setFont(new Font("Cooper Black", Font.PLAIN, 26));
		p2.setFont(new Font("Cooper Black", Font.PLAIN, 26));

		add(p1);
		add(Box.createRigidArea(new Dimension(25, 0)));
		add(p2);
		add(Box.createRigidArea(new Dimension(25, 0)));

		JButton exit = Main.button("Exit");
		JButton save = Main.button("Save");

		add(save);
		add(exit);

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

					System.exit(0);
				}
			}
		});

		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					String fileName = JOptionPane.showInputDialog("Enter filename to save:");
					saveFile = new SaveFile(Main.content, "D:/Java programming/Pool-Game/resource/games/" + fileName + ".txt");
					System.out.println("Created");
			}
		});

		ballList = new LinkList();

		// Шар-биток
		ballList.insert(
				new Ball(initialPosX / 2.7, initialPosY, INIT_RADIUS, INIT_MASS, new Speed(0, 0), WHITE, true, 0));

		// Первый шар в треугольнике
		ballList.insert(new Ball(initialPosX, initialPosY, INIT_RADIUS, INIT_MASS, new Speed(0, 0), YELLOW, false, 9));

		ballList.insert(
				new Ball(initialPosX + dx, initialPosY + dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0), RED, true, 7));
		ballList.insert(new Ball(initialPosX + dx, initialPosY - dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0), PURPLE,
				false, 12));

		ballList.insert(new Ball(initialPosX + 2 * dx, initialPosY + 2 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				RED, false, 15));
		ballList.insert(
				new Ball(initialPosX + 2 * dx, initialPosY, INIT_RADIUS, INIT_MASS, new Speed(0, 0), BLACK, true, 8));
		ballList.insert(new Ball(initialPosX + 2 * dx, initialPosY - 2 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				YELLOW, true, 1));

		ballList.insert(new Ball(initialPosX + 3 * dx, initialPosY + 3 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				GREEN, true, 6));
		ballList.insert(new Ball(initialPosX + 3 * dx, initialPosY + 1 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				BLUE, false, 10));
		ballList.insert(new Ball(initialPosX + 3 * dx, initialPosY - 1 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				BROWN, true, 3));
		ballList.insert(new Ball(initialPosX + 3 * dx, initialPosY - 3 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				GREEN, false, 14));

		ballList.insert(new Ball(initialPosX + 4 * dx, initialPosY + 4 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				BROWN, false, 11));
		ballList.insert(new Ball(initialPosX + 4 * dx, initialPosY + 2 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				BLUE, true, 2));
		ballList.insert(new Ball(initialPosX + 4 * dy, initialPosY, INIT_RADIUS, INIT_MASS, new Speed(0, 0), ORANGE,
				false, 13));
		ballList.insert(new Ball(initialPosX + 4 * dx, initialPosY - 2 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				PURPLE, true, 4));
		ballList.insert(new Ball(initialPosX + 4 * dx, initialPosY - 4 * dy, INIT_RADIUS, INIT_MASS, new Speed(0, 0),
				ORANGE, true, 5));

		addPocketBalls();

		repaint();
	}

	// Загрузка предыдущего сохранения
	public void loadGame(String path)
	{
		// Считываем существующие переменные
		ball = SaveFile.readBallInfo(path);
		Character[] characters = SaveFile.readCharacterInfo(path);

		player1 = characters[0];
		player2 = characters[1];

		ballList = new LinkList();
		for (int i = 0; i < 16; i++)
			ballList.insert(ball[i]);

		addPocketBalls();

		System.out.println("Loaded");
	}

	/**
	 * Добавляет невидмые шары в лузы; если эти шары поражены,столкновение обнаружено, и можно предположить,
	 * что движущийся шар достаточно соприкасается с лузой, чтобы считаться забитым
	 * */
	public void addPocketBalls() {
		// Верхние лузы
		ballList.insert(new Ball(100, 100, 5, 0, new Speed(0, 0), ORANGE, true, 77));
		ballList.insert(new Ball((Main.WIDTH / 2), 100, 5, 0, new Speed(0, 0), ORANGE, true, 77));
		ballList.insert(new Ball(Main.WIDTH - 110, 110, 5, 0, new Speed(0, 0), ORANGE, true, 77));

		// Нижние лузы
		ballList.insert(new Ball(100, Main.HEIGHT - 110, 5, 0, new Speed(0, 0), ORANGE, true, 77));
		ballList.insert(new Ball((Main.WIDTH / 2), Main.HEIGHT - 110, 5, 0, new Speed(0, 0), ORANGE, true, 77));
		ballList.insert(new Ball(Main.WIDTH - 110, Main.HEIGHT - 110, 5, 0, new Speed(0, 0), ORANGE, true, 77));
	}

	// Загрузка текстур
	public BufferedImage loadTextures(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException ex) {
			System.err.println("Error! Cannot read the file");
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error loading textures!.");
		}

		return image;
	}

	public void paintComponent(Graphics g) {
		ball = ballList.getElements();

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);

		super.paintComponent(g2d);

		// Рендеринг бильярдного стола
		// Границы
		g.drawImage(wooden_tile, 0, 0, Main.WIDTH, 100, this);
		g.drawImage(wooden_tile, 0, Main.HEIGHT - 100, Main.WIDTH, 100, this);
		g.drawImage(wooden_tile_rotated90, 0, 0, 100, Main.HEIGHT, this);
		g.drawImage(wooden_tile_rotated90, Main.WIDTH - 100, 0, 100, Main.HEIGHT, this);

		// Зеленое поерытие
		g.drawImage(table_grass, 100, 100, Main.WIDTH - 200, Main.HEIGHT - 200, this);

		// Верхние лузы
		g.drawImage(black_dot, 50, 50, 100, 100, this);
		g.drawImage(black_dot, (Main.WIDTH / 2) - 50, 50, 100, 100, this);
		g.drawImage(black_dot, Main.WIDTH - 150, 50, 100, 100, this);

		// Нижние лузы
		g.drawImage(black_dot, 50, Main.HEIGHT - 150, 100, 100, this);
		g.drawImage(black_dot, (Main.WIDTH / 2) - 50, Main.HEIGHT - 150, 100, 100, this);
		g.drawImage(black_dot, Main.WIDTH - 150, Main.HEIGHT - 150, 100, 100, this);

		// Рендеринг кия
		cue.updatePosition((int) ball[0].getX(), (int) ball[0].getY());
		cue.drawBack();
		cue.render(g2d, graphic_cue, this);

		// Определяем, когда шар сталкивается с лузой, и что делаем, когда
		// шар попадает в лузу
		if (!paused) {
			double passed = 0.0;
			while (passed + next_collision < 1.0) {
				for (int i = 0; i < BALLS; i++) {
					if (ball[i] == first) {
						if (second.getBallNumber() == 77) {
							first.setX(10000000.0);
							first.setY(10000000.0);

							if (first.getBallNumber() == 0) {
								ball[0].setX((double) Main.WIDTH / 2);
								ball[0].setY((double) Main.HEIGHT / 2);

								swapPlayerTurn();
							} else if (first.getBallNumber() == 8) {
								if (player1.turn) {
									if (player1.points == 7) {
										// победа
										JFrame win = new JFrame("WINNER");
										win.setSize(500, 500);
										win.setLayout(new BorderLayout());
										JLabel winnerLabel = new JLabel("Player 1 WINS!");
										winnerLabel.setFont(new Font("High Tower Text", Font.BOLD, 30));
										win.getContentPane().setBackground(Color.GREEN);
										winnerLabel.setBackground(Color.GREEN);
										win.add(winnerLabel, BorderLayout.CENTER);
										win.setVisible(true);
										SwingUtilities.getWindowAncestor(Level.this).dispose();

									} else {
										// поражение
										JFrame win = new JFrame("WINNER");
										win.setSize(500, 500);
										win.setLayout(new BorderLayout());
										JLabel winnerLabel = new JLabel("Player 2 WINS!");
										winnerLabel.setFont(new Font("High Tower Text", Font.BOLD, 30));
										win.getContentPane().setBackground(Color.GREEN);
										winnerLabel.setBackground(Color.GREEN);
										win.add(winnerLabel, BorderLayout.CENTER);
										win.setVisible(true);
										SwingUtilities.getWindowAncestor(Level.this).dispose();
									}
								} else if (player2.turn) {
									if (player2.points == 7) {
										JFrame win = new JFrame("WINNER");
										win.setSize(500, 500);
										win.setLayout(new BorderLayout());
										JLabel winnerLabel = new JLabel("Player 2 WINS!");
										winnerLabel.setFont(new Font("High Tower Text", Font.BOLD, 30));
										win.getContentPane().setBackground(Color.GREEN);
										winnerLabel.setBackground(Color.GREEN);
										win.add(winnerLabel, BorderLayout.CENTER);
										win.setVisible(true);
										SwingUtilities.getWindowAncestor(Level.this).dispose();

									} else {
										JFrame win = new JFrame("WINNER");
										win.setSize(500, 500);
										win.setLayout(new BorderLayout());
										JLabel winnerLabel = new JLabel("Player 1 WINS!");
										winnerLabel.setFont(new Font("High Tower Text", Font.BOLD, 30));
										win.getContentPane().setBackground(Color.GREEN);
										winnerLabel.setBackground(Color.GREEN);
										win.add(winnerLabel, BorderLayout.CENTER);
										win.setVisible(true);
										SwingUtilities.getWindowAncestor(Level.this).dispose();
									}
								}
							} else if (first.getSolid() == true) {
								player1.incrementScore();

								if (player1.turn != true)
									swapPlayerTurn();
							} else {
								player2.incrementScore();

								if (player2.turn != true)
									swapPlayerTurn();
							}

							first.setSpeedZero();
							first.pocketed();
						} else{
							ball[i].collide(second, next_collision);

							collisionOccured = true;
						}

					} else if (ball[i] != second) {
						ball[i].move(next_collision);
					}
				}
				passed += next_collision;
				collision_update();
			}
			next_collision += passed;
			next_collision -= 1.0;
			for (int i = 0; i < BALLS; i++) {
				ball[i].move(1.0 - passed);
			}
		}

		// Рендеринг шаров
		int a = 0;
		for (int i = 0; i < BALLS; i++) {
			ball[i].paint(g2d);

		}


		// Проверяем, не двигаются ли шары
		// Если да, то нужно поменять местами ходы (если мяч не был забит в лузу)
		int keys = 0;
		for (int i = 0; i < 16; i++) {
			if (ball[i].moving() == false) {
				keys += 1;
			}
		}

		if (keys == 16 && collisionOccured == true) {
			System.out.println("Swapped");
			collisionOccured = false;
			swapPlayerTurn();
		}

		if (queued_collision_update) {
			collision_update();
		}

		// Смена игрока
		// Ставит индикатор, чей сейчас ход
		// Выделение зеленым цветом означает ход игрока
		if (player1.turn == true) {
			p1.setForeground(Color.GREEN);
			p2.setForeground(Color.RED);
		} else {
			p1.setForeground(Color.RED);
			p2.setForeground(Color.GREEN);
		}

		// Отображение очков
		p1.setText("Player 1: " + player1.points);
		p2.setText("Player 2: " + player2.points);
	}

	public static void queue_collision_update() {
		queued_collision_update = true;
	}

	public void collision_update() {
		next_collision = Double.POSITIVE_INFINITY;
		int a = 0;
		for (int i = 0; i < BALLS - 1; i++) {
			for (int j = i + 1; j < BALLS; j++) {
				double minimo = ball[i].next_collision(ball[j]);
				if (minimo < next_collision) {
					next_collision = minimo;
					first = ball[i];
					second = ball[j];
				}
			}

		}
		queued_collision_update = false;
	}

	// Меняет ход игрока
	public void swapPlayerTurn() {
		boolean turn;

		turn = !player1.isTurn();
		player1.setTurn(turn);

		turn = !player2.isTurn();
		player2.setTurn(turn);
	}

	public Ball getBall(int key) {
		return ball[key];
	}
}
