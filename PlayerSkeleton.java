import java.util.*;

public class PlayerSkeleton {

	private int pieceLimit;
	private ArrayList<Double> weights;
	private State s;

	public PlayerSkeleton(int pieceLimit, ArrayList<Double> weights) {
		this.pieceLimit = pieceLimit;
		this.weights = weights;
		s = new State();
	}

	public static PlayerSkeleton LimitlessPlayerSkeleton() {
		int pieceLimit = Integer.MAX_VALUE;
		ArrayList<Double> weights = new ArrayList<Double>();
		for (int i = 0; i < Constant.FEATURE_COUNT; i++) {
			weights.add(1.0);
		}		
		return new PlayerSkeleton(pieceLimit, weights);
	}

	public void play() {
		new TFrame(s);
		int pieceCount = 0;
		while (!s.hasLost() && pieceCount < pieceLimit) {
			s.makeMove(pickMove(s, s.legalMoves()));
			s.draw();
			s.drawNext(0, 0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed " + s.getRowsCleared() + " rows.");
	}

	//implement this function to have a working system
	private int pickMove(State s, int[][] legalMoves) {
		double maxHValue = Double.NEGATIVE_INFINITY;
		int moveWithMaxHValue = 0;
		for (int i = 0; i < legalMoves.length; i++) {
			double hvalue = Heuristic.heuristicValue(s, i);
			if (hvalue > maxHValue) {
				maxHValue = hvalue;
				moveWithMaxHValue = i;
			}
		}
		return moveWithMaxHValue;
	}

	public int getScore() {
		return s.getRowsCleared();
	}
	
	public static void main(String[] args) {
		PlayerSkeleton p = PlayerSkeleton.LimitlessPlayerSkeleton();
		p.play();
	}
}
