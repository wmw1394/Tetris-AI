import java.util.*;
import java.util.stream.*;

public class Heuristic {

	private static double WEIGHT_ELIMATED_ROWS = 2.4;
	private static double WEIGHT_LANDING_HEIGHT = -3.71;
	private static double WEIGHT_ROWS_TRANSITION = 0;
	private static double WEIGHT_COLUMN_TRANSITION = 0;
	private static double WEIGHT_NUMBER_HOLES = -4.79;
	private static double WEIGHT_WELL_SUM = 0;
	private static double WEIGHT_ROUGHNESS = -1.7;
	private static double WEIGHT_AGGREGATE_HEIGHT = 0;
	private static double WEIGHT_MAX_HOLE_HEIGHT = 0;
	private static double WEIGHT_MAX_COLUMN_HEIGHT = -1.3;
	private static double WEIGHT_COLUMNS_WITH_HOLES = 0;
	private static double WEIGHT_ROWS_WITH_HOLES = 0;
	private static double WEIGHT_LOWEST_PLAYABLE_ROW = 0;
	private static double WEIGHT_MAX_PIT_DEPTH = 0;
	private static double WEIGHT_SLOPE = 0;
	private static double WEIGHT_CONCAVITY = 0;
	private static double WEIGHT_BLOCKADES = -1.87;

	private static double maxHoleHeight;
	private static double maxColumnHeight;
	private static double columnsWithHoles;
	private static double rowsWithHoles;

	public static void assignWeights(ArrayList<Double> weights) {
		WEIGHT_ELIMATED_ROWS = weights.get(0);
		WEIGHT_LANDING_HEIGHT = weights.get(1);
		// WEIGHT_ROWS_TRANSITION = weights.get(2);
		// WEIGHT_COLUMN_TRANSITION = weights.get(3);
		WEIGHT_NUMBER_HOLES = weights.get(4);
		// WEIGHT_WELL_SUM = weights.get(5);
		WEIGHT_ROUGHNESS = weights.get(6);
		// WEIGHT_AGGREGATE_HEIGHT = weights.get(7);
		// WEIGHT_MAX_HOLE_HEIGHT = weights.get(8);
		WEIGHT_MAX_COLUMN_HEIGHT = weights.get(9);
		// WEIGHT_COLUMNS_WITH_HOLES = weights.get(10);
		// WEIGHT_ROWS_WITH_HOLES = weights.get(11);
		// WEIGHT_LOWEST_PLAYABLE_ROW = weights.get(12);
		// WEIGHT_MAX_PIT_DEPTH = weights.get(13);
		// WEIGHT_SLOPE = weights.get(14);
		// WEIGHT_CONCAVITY = weights.get(15);
		WEIGHT_BLOCKADES = weights.get(16);
	}

	public static void printWeights() {
		String s = String.format("rows: %f  heigth: %f  holes: %f  roughness: %f  max_column_height: %f  blockades: %f",
			WEIGHT_ELIMATED_ROWS, WEIGHT_LANDING_HEIGHT, WEIGHT_NUMBER_HOLES, WEIGHT_ROUGHNESS, WEIGHT_MAX_COLUMN_HEIGHT, WEIGHT_BLOCKADES);
		System.out.println(s);
	}

	/* heuristic function, given a state and one (potential) move, 
	return the weighted heurtisticValue combining each feature */
	public static double heuristicValue(State s, int moveIndex) {		
		int[] move = s.legalMoves()[moveIndex];
		int[][] futureFields = s.getFutureFieldForMove(move);

		calculateHeights(futureFields);

		int rowsCleared = s.getRowsClearedForMove(move);
		int firstCol = move[1];
		int length = State.pWidth[s.getNextPiece()][move[0]];
		double landingHeight = (int) getLandingHeight(futureFields, firstCol, length, rowsCleared);
		double eliminatedRows = s.getRowsClearedForMove(move);
		double rowsTransition = getRowsTransition(futureFields);
		double columnsTransition = getColumnsTransition(futureFields);
		double numHoles = getNumberOfHoles(futureFields);
		double wellsum = getWellSums(futureFields);
		double roughness = getRoughness(futureFields);
		double aggregateHeight = getAggregateHeight(futureFields);
		double maxHoleHeight = 0;
		double maxColumnHeight = 0;
		double columnsWithHoles = 0;
		double rowsWithHoles = 0;
		double lowestPlayableRow = getLowestPlayableRow(futureFields);
		double maxPitDepeth = getMaximumPitDepth(futureFields);
		double slope = getSlope(futureFields);
		double concavity = getConcavity(futureFields);
		double blockades = getBlockades(futureFields);

		double value = 0;

		if (s.getFutureLoss(move)) {
			value = Double.NEGATIVE_INFINITY;
		} else {
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
		}

		return value;
	}

	static int[] heights = new int[Constant.COLS];

	public static void traverse(int[][] fieldCopy, int i, int j) {
		if (fieldCopy[i][j] == 0) {
			fieldCopy[i][j] = -1;
		}
		if (i+1 >= 0 && i+1 < Constant.ROWS) {
			if (fieldCopy[i+1][j] == 0) {
				traverse(fieldCopy, i+1, j);
			}
		}
		if (i-1 >= 0 && i-1 < Constant.ROWS) {
			if (fieldCopy[i-1][j] == 0) {
				traverse(fieldCopy, i-1, j);
			}
		}
		if (j+1 >= 0 && j+1 < Constant.COLS) {
			if (fieldCopy[i][j+1] == 0) {
				traverse(fieldCopy, i, j+1);
			}
		}
		if (j-1 >= 0 && j-1 < Constant.COLS) {
			if (fieldCopy[i][j-1] == 0) {
				traverse(fieldCopy, i, j-1);
			}
		}
	}

	private static void calculateHeights(int[][] futureFields) {
		for (int j = 0; j < Constant.COLS; j++) {
			boolean flag = true;
			for (int i = 20; i >= 0 && flag; i--) {
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

	private static double getLandingHeight(int[][] futureFields, int firstCol, int length, int rowsCleared) {
		int height = 0;
		for(int i = firstCol; i < firstCol + length; i++) {
			if(height < heights[i]) {
				height = heights[i];
			}
		}
		height -= rowsCleared;
		return height;
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
		for (int i = 20; i >= 0; i--) {
			for (int j = 1; j < Constant.COLS; j++) {
				if (futureFields[i][j] != futureFields[i][j-1]) {
					rowsTransition++;
				}
			}
		}
		return rowsTransition;
	}

	private static double getColumnsTransition(int[][] futureFields) {
		int columnsTransition = 0;
		for (int j = 0; j < Constant.COLS; j++) {
			for (int i = 20; i > 0; i--) {
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
		for (int j = 1; j < Constant.COLS; j++) {
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
		for (int j = 6; j < Constant.COLS; j++) {
			concavity += heights[5] - heights[j];
		}
		return concavity;
	}

	private static double getNumberOfHoles(int[][] futureFields) {
		int num_holes = 0;
		for (int j = 0; j < Constant.COLS; j++) {
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
	
	//just use search holes
	private static void searchHoles(int[][] futureFields) {
		int num_holes = 0;
		maxHoleHeight = 0;
		maxColumnHeight = 0;
		columnsWithHoles = 0;
		rowsWithHoles = 0;
		int[] colWithHoles = new int[Constant.COLS];
		int[] rowWithHoles = new int[Constant.ROWS];

		for (int j = 0; j < Constant.COLS; j++) {
			boolean flag = false;
			for (int i = 20; i >= 1; i--) {
				if (futureFields[i][j] != 0) {
					if (maxColumnHeight < i+1) {
						maxColumnHeight = i+1;
					}
					if (!flag) {
						flag = true;
					}
				}

				if (flag == true) {
					if (futureFields[i][j] == 0) {
						if (maxHoleHeight < i) {
							maxHoleHeight = i;
						}
						num_holes++;
						colWithHoles[j] = 1;
						rowWithHoles[i] = 1;
					}
				}
			}
		}
		rowsWithHoles = IntStream.of(rowWithHoles).sum();
		columnsWithHoles = IntStream.of(colWithHoles).sum();
	}

	public static double getWellSums(int[][] futureFields) {
		int[][] fieldCopy = new int[Constant.ROWS][Constant.COLS];
		int wellsum = 0;
		for (int i = 0; i < Constant.ROWS; i++) {
			for (int j = 0; j < Constant.COLS; j++) {
				fieldCopy[i][j] = futureFields[i][j];
			}
		}
		for (int i = 20; i >= 0; i--) {
			for (int j = 0; j < Constant.COLS; j++) {
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
		for (int j = 1; j < Constant.COLS; j++) {
			roughness += Math.abs(heights[j] - heights[j-1]);
		}
		return roughness;
	}

	public static double getAggregateHeight(int[][] futureFields) {
		int[] heights = getHeights(futureFields);
		int aggregateHeight = 0;
		for (int j = 0; j < Constant.COLS; j++) {
			aggregateHeight += heights[j];
		}
		return aggregateHeight;
	}

	public static double getLowestPlayableRow(int[][] futureFields) {
		int[][] fieldCopy = new int[Constant.ROWS][Constant.COLS];
		for (int i = 0; i < Constant.ROWS; i++) {
			for (int j = 0; j < Constant.COLS; j++) {
				fieldCopy[i][j] = futureFields[i][j];
			}
		}
		for (int j = 0; j < Constant.COLS; j++) {
			if (fieldCopy[20][j] == 0) {
				traverse(fieldCopy, 20, j);
			} 
		}
		for (int i = 0; i < Constant.ROWS; i++) {
			for (int j = 0; j < Constant.COLS; j++) {
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
		for (int j = 0; j < Constant.COLS; j++) {
			for (int i = heights[j]; i > 0; i--) {
				if (futureFields[i][j] != 0 && futureFields[i - 1][j] == 0) {
					blockades += 1;
				}
			}
		}
		return blockades;
	}
}