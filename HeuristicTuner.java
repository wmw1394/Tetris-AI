import java.util.*;

public class HeuristicTuner {

	private static final int GENERATION_COUNT = 1000;
	private static final int POPULATION_SIZE = 1000;
	private static final int TOP_K = 16;
	private static final int PIECE_LIMIT = 1000;
	private static final long RANDOM_SEED = 7777777L;
	private static final Random random = new Random(RANDOM_SEED);

	public static ArrayList<Double> generateRandomWeight() {
		ArrayList<Double> weights = new ArrayList<Double>();
		for (int i = 0; i < Constant.FEATURE_COUNT; i++) {
			weights.add(random.nextDouble());
		}
		return weights;
	}

	public static ArrayList<ArrayList<Double>> generateNextPopulation(ArrayList<ArrayList<Double>> topWeightsFromLastIteration) {
		ArrayList<ArrayList<Double>> newPopulation = new ArrayList<ArrayList<Double>>(topWeightsFromLastIteration);
		// TODO: apply crossover and mutation to generate the next generation of POPULATION_SIZE

		return newPopulation;
	}

	public static void main(String[] args) {
		
		ArrayList<ArrayList<Double>> population = new ArrayList<ArrayList<Double>>();
		
		// generate first population
		for (int i = 0; i < POPULATION_SIZE; i++) {
			ArrayList<Double> randomWegiths = generateRandomWeight();
			population.add(randomWegiths);
		}

		// evoluate by generation
		for (int generation = 0; generation < GENERATION_COUNT; generation++) {

			int pieceLimitForGeneration = (generation + 1) * PIECE_LIMIT;

			// compute the fitness scores
			ArrayList<Integer> fitnessScores = new ArrayList<Integer>();
			int maxScore = 0;
			int maxScoreWeightIndex = 0;
			for (int i = 0; i < POPULATION_SIZE; i++) {
				ArrayList<Double> weights = population.get(i);
				PlayerSkeleton player = new PlayerSkeleton(pieceLimitForGeneration, weights);
				player.frameless = true;
				player.play();
				int score = player.getScore();
				fitnessScores.add(score);
				if (score > maxScore) {
					maxScore = score;
					maxScoreWeightIndex = i;
				}
			}

			if (generation != GENERATION_COUNT - 1) {
				// get the topk-ranked fitness scores
				ArrayList<Integer> rankedScores = new ArrayList<Integer>(fitnessScores);
				Collections.sort(rankedScores);
				for (int i = 0; i < POPULATION_SIZE - TOP_K; i++) {
					rankedScores.remove(0);
				}
				// get the topk-ranked weights
				ArrayList<ArrayList<Double>> topWeights = new ArrayList<ArrayList<Double>>();
				for (int i = 0; i < POPULATION_SIZE; i++) {
					int score = fitnessScores.get(i);
					if (rankedScores.contains(score)) {
						topWeights.add(population.get(i));
					}
				}
				// generate population of POPULATION_SIZE for next generation using crossover + mutation
				population = generateNextPopulation(topWeights);
			} else {
				// TODO: output the best weight in the last generation

			}
		}
	}
}