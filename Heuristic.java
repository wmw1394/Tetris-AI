public class Heuristic {

	/* heuristic function, given a state and one (potential) move, 
	return the weighted heurtisticValue combining each feature */
	public static double heuristicValue(State s, int moveIndex) {
		int[] move = s.legalMoves()[moveIndex];
		return 0;		
	}

	// gonna to decide the weights for each feature and set them as constant values
	// one example below
	private static final double WEIGHT_ELIMATED_ROWS = 1;

	// gonna to write a separate heuristic function for every feature
	// one example below
	private static double valueForFeatureEliminatedRows(State s, int[] move) {
		return 0;
	}
}