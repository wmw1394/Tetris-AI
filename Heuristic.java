public class Heuristic {

	private static final double WEIGHT_ELIMATED_ROWS = 1;
	private static final double WEIGHT_LANDING_HEIGHT = 1;
	private static final double WEIGHT_ROWS_TRANSITION = 1;
	private static final double WEIGHT_COLUMN_TRANSITION = 1;
	private static final double WEIGHT_NUMBER_HOLES = 1;
	private static final double WEIGHT_WELL_SUM = 1;
	private static final double WEIGHT_ROUGHNESS = 1;
	private static final double WEIGHT_AGGREGATE_HEIGHT = 1;
	private static final double WEIGHT_MAX_HOLE_HEIGHT = 1;
	private static final double WEIGHT_MAX_COLUMN_HEIGHT = 1;
	private static final double WEIGHT_COLUMNS_WITH_HOLES = 1;
	private static final double WEIGHT_ROWS_WITH_HOLES = 1;
	private static final double WEIGHT_LOWEST_PLAYABLE_ROW = 1;
	private static final double WEIGHT_MAX_PIT_DEPTH = 1;
	private static final double WEIGHT_SLOPE = 1;
	private static final double WEIGHT_CONCAVITY = 1;
	private static final double WEIGHT_BLOCKADES = 1;

	/* heuristic function, given a state and one (potential) move, 
	return the weighted heurtisticValue combining each feature */
	public static double heuristicValue(State s, int moveIndex) {
		int[] move = s.legalMoves()[moveIndex];
		int[][] futureFields = s.getFutureFieldForMove(move);

		double eliminatedRows = getRowsClearedForMove(move);
		double landingHeight = getLandingHeight();
		double rowsTransition = getRowsTransition(futureFields);
		double columnsTransition = getColumnsTransition(futureFields);
		double numHoles = getNumberOfHoles(futureFields);
		double wellsum = getWellSums(futureFields);
		double roughness = getRoughness(futureFields);
		double aggregateHeight = getAggregateHeight(futureFields);
		double maxHoleHeight;
		double maxColumnHeight;
		double columnsWithHoles;
		double rowsWithHoles;
		double lowestPlayableRow = getLowestPlayableRow(futureFields);
		double maxPitDepeth = getMaximumPitDepth(futureFields);
		double slope = getSlope(futureFields);
		double concavity = getConcavity(futureFields);
		double blockades = getBlockades(futureFields);

		double value = 0;
		value += eliminatedRows * WEIGHT_ELIMATED_ROWS;
		value += landingHeight * WEIGHT_LANDING_HEIGHT;
		value += rowsTransition * WEIGHT_ROWS_TRANSITION;
		value += columnsTransition * WEIGHT_COLUMN_TRANSITION;
		value += numHoles * WEIGHT_NUMBER_HOLES;
		value += wellsum * WEIGHT_WELL_SUM;
		value += roughness * WEIGHT_ROUGHNESS;
		value += aggregateHeight * WEIGHT_AGGREGATE_HEIGHT;
		value += maxHoleHeight * WEIGHT_MAX_HOLE_HEIGHT;
		value += maxColumnHeight * WEIGHT_MAX_COLUMN_HEIGHT;
		value += columnsWithHoles * WEIGHT_COLUMNS_WITH_HOLES;
		value += rowsWithHoles * WEIGHT_ROWS_WITH_HOLES;
		value += lowestPlayableRow * WEIGHT_LOWEST_PLAYABLE_ROW;
		value += maxPitDepeth * WEIGHT_MAX_PIT_DEPTH;
		value += slope * WEIGHT_SLOPE;
		value += concavity * WEIGHT_CONCAVITY;
		value += blockades * WEIGHT_BLOCKADES;

		return value;
	}

	static int[] heights;

	public static void traverse(int[][] fieldCopy, int i, int j) {
		if (fieldCopy[i][j] == 0) {
			fieldCopy[i][j] = -1;
		}
		if (i+1 >= 0 && i+1 < 21) {
			if (fieldCopy[i+1][j] == 0) {
				traverse(fieldCopy, i+1, j);
			}
		}
		if (i-1 >= 0 && i-1 < 21) {
			if (fieldCopy[i-1][j] == 0) {
				traverse(fieldCopy, i-1, j);
			}
		}
		if (j+1 >= 0 && j+1 < 10) {
			if (fieldCopy[i][j+1] == 0) {
				traverse(fieldCopy, i, j+1);
			}
		}
		if (j-1 >= 0 && j-1 < 10) {
			if (fieldCopy[i][j-1] == 0) {
				traverse(fieldCopy, i, j-1);
			}
		}
	}

	private static void calculateHeights(int[][] futureFields) {
		for (int j = 0; j < 10; j++) {
			boolean flag = true;
			for (int i = 20; j >= 0 && flag; i--) {
				if (futureFields[i][j] != 0) {
					heights[j] = i + 1;
					flag = false;
				}
			}
		}
	}

	private static int[] getHeights(int[][] futureFields) {
		return heights;
	}

	private static double getLandingHeight() {

	}

	private static double getMaximumPitDepth(int[][] futureFields) {
		int deepestPit = 0;
		int[] heights = getHeights(futureFields);
		for (int j = 1; j <= 8; j++) {
			int temp = Math.min(heights[j-1] - heights[j], heights[j+1] - heights[j]);
			if (temp > 0) {
				if (temp > deepestPit) {
					deepestPit = temp;
				}
			}
		}
		return deepestPit;
	}

	private static double getRowsTransition(int[][] futureFields) {
		int rowsTransition = 0;
		for (int i = 21; i >= 0; i--) {
			for (int j = 1; j < 10; j++) {
				if (futureFields[i][j] != futureFields[i][j-1]) {
					rowsTransition++;
				}
			}
		}
		return rowsTransition;
	}

	private static double getColumnsTransition(int[][] futureFields) {
		int columnsTransition = 0;
		for (int j = 0; j < 10; j++) {
			for (int i = 21; i > 0; i--) {
				if (futureFields[i][j] != futureFields[i-1][j]) {
					columnsTransition++;
				}
			}
		}
		return columnsTransition;
	}

	private static double getSlope(int[][] futureFields) {
		int[] heights = getHeights(futureFields);
		int slope = 0;
		for (int j = 1; j < 10; j++) {
			slope += heights[j] - heights[j-1];
		}
		return slope;
	}

	private static double getConcavity(int[][] futureFields) {
		int concavity = 0;
		int[] heights = getHeights(futureFields);
		for (int j = 0; j <= 3; j++) {
			concavity += heights[4] - heights[j];
		}
		for (int j = 6; j < 10; j++) {
			concavity += heights[5] - heights[j];
		}
		return concavity;
	}

	private static double getNumberOfHoles(int[][] futureFields) {
		int num_holes = 0;
		for (int j = 0; j < 10; j++) {
			boolean flag = false;
			for (int i = 20; i >= 1; i--) {
				if (futureFields[i][j] != 0 && !flag) {
					flag = true;
				}
				if (flag == true) {
					if (futureFields[i][j] == 0) {
						num_holes++;
					}
				}
			}
		}
		return num_holes;
	}

	public static double getWellSums(int[][] futureFields) {
		int[][] fieldCopy = new int[21][10];
		int wellsum = 0;
		for (int i = 0; i < 21; i++) {
			for (int j = 0; j < 10; j++) {
				fieldCopy[i][j] = futureFields[i][j];
			}
		}
		for (int i = 20; i >= 0; i--) {
			for (int j = 0; j < 10; j++) {
				if (fieldCopy[i][j] == 0) {
					wellsum++;
					traverse(fieldCopy, i, j);
				}
			}
		}
		wellsum--;
		return wellsum;
	}

	public static double getRoughness(int[][] futureFields) {
		int[] heights = getHeights(futureFields);
		int roughness = 0;
		for (int j = 1; j < 10; j++) {
			roughness += Math.abs(heights[j] - heights[j-1]);
		}
		return roughness;
	}

	public static double getAggregateHeight(int[][] futureFields) {
		int[] heights = getHeights(futureFields);
		int aggregateHeight = 0;
		for (int j = 0; j < 10; j++) {
			aggregateHeight += heights[j];
		}
		return aggregateHeight;
	}

	public static double getLowestPlayableRow(int[][] futureFields) {
		int[][] fieldCopy = new int[21][10];
		for (int i = 0; i < 21; i++) {
			for (int j = 0; j < 10; j++) {
				fieldCopy[i][j] = futureFields[i][j];
			}
		}
		for (int j = 0; j < 10; j++) {
			if (fieldCopy[20][j] == 0) {
				traverse(fieldCopy, 20, j);
			} 
		}
		for (int i = 0; i < 21; i++) {
			for (int j = 0; j < 10; j++) {
				if (fieldCopy[i][j] == -1) {
					return i;
				}
			}
		}
		return 20;
	}

	public static double getBlockades(int[][] futureFields) {
		int[] heights = getHeights(futureFields);
		int blockades = 0;
		for (int j = 0; j < 10; j++) {
			for (int i = heights[j]; i > 0; i--) {
				if (futureFields[i][j] != 0 && futureFields[i - 1][j] == 0) {
					blockades += 1;
				}
			}
		}
		return blockades;
	}
}