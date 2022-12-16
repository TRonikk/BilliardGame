public class Character {
	String name;
	boolean turn; // Игрока ли ход?
	int points;
	boolean striped; // ?полосатые или полые шары
	
	public Character(String name, boolean turn, int points, boolean striped) {
		super();
		this.name = name;
		this.turn = turn;
		this.points = points;
		this.striped=striped;
	}

	public void incrementScore()
	{
		System.out.println("+1!");
		points += 1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTurn() {
		return turn;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public boolean isStriped() {
		return striped;
	}

	public void setStriped(boolean striped) {
		this.striped = striped;
	}
}
