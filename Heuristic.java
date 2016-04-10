import java.util.stream.IntStream;

public class Heuristic {
	/* heuristic function, given a state and one (potential) move, 
	return the weighted heurtisticValue combining each feature */
	private static int num_holes = 0;
	private static int max_height_holes = 0;
	private static int max_column_height = 0;
	private static int num_column_holes = 0;
	private static int num_row_holes = 0;
	private static int[] colWithHoles = new int[10];
	private static int[] rowWithHoles = new int[21];
	private static int[] colHeights = new int[10];


	public static double heuristicValue(State s, int moveIndex) {
		int[] move = s.legalMoves()[moveIndex];
		int[][] futureField = s.getFutureFieldForMove(move);
		int rowsCleared = s.getRowsClearedForMove(move);
		searchHoles(futureField);
		int firstCol = move[1];
		int length = State.pWidth[s.getNextPiece()][move[0]];

		int landingHeight = (int) getLandingHeight(futureField, firstCol, length, rowsCleared);
		System.out.println("num_holes:"+num_holes);
		System.out.println(max_height_holes);
		System.out.println(max_column_height);
		System.out.println(num_column_holes);
		System.out.println(num_row_holes);
		System.out.println(colHeights);
		System.out.println(landingHeight);
		return 0;		
	}



	private static double getLandingHeight(int[][] futureFields, int firstCol, int length, int rowsCleared) {
		int height = 0;
		for(int i = firstCol; i < firstCol + length; i++) {
			if(height < colHeights[i]) {
				height = colHeights[i];
				System.out.println("column_height:"+colHeights[i]);
				System.out.println("i:"+i);

			}
		}
		System.out.println("height"+height);
		System.out.println(rowsCleared);
		height -= rowsCleared;
		return height;
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
	
	//just use search holes
	private static void searchHoles(int[][] futureFields) {
		 num_holes = 0;
		 max_height_holes = 0;
		 max_column_height = 0;
		 num_column_holes = 0;
		 num_row_holes = 0;
		 colWithHoles = new int[10];
		 rowWithHoles = new int[21];

		 for (int j = 0; j < 10; j++) {
				boolean flag = false;
				for (int i = 20; i >= 1; i--) {
					if (futureFields[i][j] != 0) {
						if (max_column_height < i+1) {
							max_column_height = i+1;
						}
						if (!flag) {
							System.out.println("max_column_height:"+i);
							colHeights[j] = i+1;
							flag = true;
						}
					}

					if (flag == true) {
						if (futureFields[i][j] == 0) {
							if (max_height_holes < i) {
								max_height_holes = i;
							}
							num_holes++;
							colWithHoles[j] = 1;
							rowWithHoles[i] = 1;
						}
					}
				}
			}
			num_row_holes = IntStream.of(rowWithHoles).sum();
			num_column_holes = IntStream.of(colWithHoles).sum();
	}

	private static double getNumberOfHoles(int[][] futureFields) {
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


	private static int[] getHeights(int[][] futureFields) {
		return colHeights;
	}

	public static int getRoughness(int[][] futureFields) {
		int[] heights = getHeights(futureFields);
		int roughness = 0;

		for (int j = 1; j < 10; j++) {
			roughness += Math.abs(heights[j] - heights[j-1]);
		}
		
		return roughness;
	}

	public static int getAggregateHeight(int[][] futureFields) {
		int[] heights = getHeights(futureFields);
		int aggregateHeight = 0;

		for (int j = 0; j < 10; j++) {
			aggregateHeight += heights[j];
		}
		
		return aggregateHeight;
	}

	// public static int getLowestPlayableRow(int[][] futureFields) {

	// }


	public static int getBlockades(int[][] futureFields) {
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


	// gonna to decide the weights for each feature and set them as constant values
	// one example below
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
	private static final double WEIGHT_NUMBER_BLOCKADES = 1;


	// gonna to write a separate heuristic function for every feature
	// one example below
	private static double valueForFeatureEliminatedRows(State s, int[] move) {
		return 0;
	}

	// public static void main(String[] args) {
	// 	int[][] field = new int[21][10];

	// 	for (int i = 10; i >= 0; i--) {
	// 		for (int j = 0; j < 10; j++) {
	// 			field[i][j] = 1;
	// 		}
	// 	}

	// 	field[5][3] = 0;
	// 	field[5][4] = 0;

	// 	field[6][7] = 0;
	// 	field[7][7] = 0;
		

	// 	System.out.println(getWellSums(field));
	// }


}