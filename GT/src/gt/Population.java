package gt;

import java.util.*;

public class Population {

	String type;

	Hashtable<String, Double> fractions = new Hashtable<String, Double>();
	Hashtable<String, Double> demands = new Hashtable<String, Double>();
	Hashtable<String, Double> expectedFitness = new Hashtable<String, Double>();
	Hashtable<String, Double> mutation_terms = new Hashtable<String, Double>();
	double popMeanFitness;

	// mutation frequencies: W_ij is the transition probability per generation for players of type j to mutate to type i
	Hashtable<String, Hashtable<String, Double>> mut_freq = new Hashtable<String, Hashtable<String, Double>>();

	Population(String popType) {
		type = popType;
	}

	void normalSetup(double[] initFractions, double greedyDemand,
			Hashtable<String, Hashtable<String, Double>> mutation_frequencies) {

		fractions.put("petrified", initFractions[0]);
		fractions.put("modest", initFractions[1]);
		fractions.put("fair", initFractions[2]);
		fractions.put("greedy", initFractions[3]);
		fractions.put("terrorist", initFractions[4]);

		demands.put("petrified", 0.0);
		demands.put("modest", 1 - greedyDemand);
		demands.put("fair", (double) 0.5);
		demands.put("greedy", greedyDemand);
		demands.put("terrorist", 1.0);

		expectedFitness.put("petrified", 0.0);
		expectedFitness.put("modest", 0.0);
		expectedFitness.put("fair", 0.0);
		expectedFitness.put("greedy", 0.0);
		expectedFitness.put("terrorist", 0.0);
		popMeanFitness = 0;

		mutation_terms.put("petrified", 0.0);
		mutation_terms.put("modest", 0.0);
		mutation_terms.put("fair", 0.0);
		mutation_terms.put("greedy", 0.0);
		mutation_terms.put("terrorist", 0.0);

		mut_freq = mutation_frequencies;

	}

	/*
	 * void falsifiableSetup(double[] initFractions, double greedyDemand){
	 * 
	 * fractions.put("modest",initFractions[0]); fractions.put("fair",initFractions[1]);
	 * fractions.put("falsifiable",initFractions[2]);
	 * 
	 * demands.put("modest",1-greedyDemand); demands.put("fair", (double) 0.5); demands.put("falsifiable",
	 * greedyDemand);
	 * 
	 * expectedFitness.put("modest", 0.0); expectedFitness.put("fair", 0.0); expectedFitness.put("falsifiable", 0.0);
	 * popMeanFitness = 0; }
	 */

	/**
	 * Calculates the payoff of player type i in an interaction with another player type j. see box 2 & 3 in paper !
	 * when a non-fundamentalist player (=row-player) meets a fundamentalist player, read the box as is ! when a
	 * fundamentalist player (now the row-player) meets a non-fundamentalist player, read the box as transpose
	 * 
	 * for a "normal game", there are modest, fair and greedy players, each with a corresponding demand which is
	 * independent of the population The fact that there are no modest players for a fundamentalist population should be
	 * reflected in the fact that the initial modest fraction is set to 0.
	 *
	 * for a "falsifiable game", there are modest, fair and falsifiable players, also with corresponding demands, and
	 * the falsifiable demand is set to the greedy demand. The actual payoff in an encounter (0, 0.5 or x_G) for a
	 * falsifiable player depends on the other player and this is taken care of by the calcPayOff function. Again, the
	 * fact that there are no modest players for a fundamentalist population should be reflected in the fact that the
	 * initial modest fraction is set to 0.
	 * 
	 */

	double calcPayoff(String rowPlayer, String columnPlayer) {
		// box 3: extension for "falsifiable fundamentalism"
		if (rowPlayer == "falsifiable" && columnPlayer == "falsifiable") {
			return 0.5;
		}

		if (rowPlayer == "falsifiable" && columnPlayer == "fair") // added for clarity, but this is actually superfluent
																	// (since x_G + x_F > 1)
			return 0;

		// box 3 and box 2: "clash of cultures"
		double rowDemand = demands.get(rowPlayer);
		double columnDemand = demands.get(columnPlayer); // the demands are independent of the population type, so this
															// is valid
		if (rowDemand + columnDemand <= 1)
			return rowDemand;
		else
			return 0;
	}

	/**
	 * Calculate the expected fitness of a demand x_i under a population distribution of ?^k
	 *
	 * @playerType the type of player (modest - fair - greedy - falsifiable)
	 * @pop the other population in the total population distribution ?
	 *
	 * @return the expected fitness represented as a double
	 */
	double expectedFitness(String playerType, Population pop) {
		double tSum = 0;
		Enumeration<String> players = fractions.keys(); // [H] modest, fair, greedy/falsifiable
		while (players.hasMoreElements()) {
			String otherPlayer = players.nextElement();
			tSum += calcPayoff(playerType, otherPlayer) * pop.fractions.get(otherPlayer);
		}
		// System.out.println("Expected fitness for player " + playerType + " against " + pop.type + " = " + tSum);
		return tSum;
	}

	/**
	 * Calculates the mutation terms according to the mutation part of Eq. (3) in
	 * https://www.math.uu.nl/publications/preprints/1330.pdf. Updates mutation_terms with the new values.
	 */
	void calc_mutation_terms() {
		System.out.println("mutation terms for " + type + " population");
		Enumeration<String> players_i = fractions.keys();
		while (players_i.hasMoreElements()) {
			String currentPlayer = players_i.nextElement();
			double total_mutation_effect = 0;
			Enumeration<String> players_j = fractions.keys();
			while (players_j.hasMoreElements()) {
				String player_j = players_j.nextElement();
				// System.out.println("i: " + currentPlayer + ", j: " + player_j + ", W_ij: " +
				// mut_freq.get(currentPlayer).get(player_j));
				// System.out.println("fraction of j : " + fractions.get(player_j));

				if (total_mutation_effect < 0.00000000000000001)
					total_mutation_effect = 0;

				total_mutation_effect += mut_freq.get(currentPlayer).get(player_j) * fractions.get(player_j)
						- mut_freq.get(player_j).get(currentPlayer) * fractions.get(currentPlayer);

			}
			mutation_terms.put(currentPlayer, total_mutation_effect);
		}
	}

	/**
	 * Updates the expected fitness and the population mean fitness for an interaction between this population and
	 * another.
	 * 
	 * @param otherPop
	 *            the other population
	 */
	void prepareUpdate(Population otherPop) {
		Enumeration<String> players = expectedFitness.keys(); // [H] modest, fair, greedy/falsifiable
		double meanFitnessSum = 0;
		while (players.hasMoreElements()) {
			String currentPlayer = players.nextElement();
			double expecFit = expectedFitness(currentPlayer, otherPop);
			expectedFitness.put(currentPlayer, expecFit);
			// System.out.println("expected fitness " + expecFit );
			meanFitnessSum += fractions.get(currentPlayer) * expecFit;
		}
		popMeanFitness = meanFitnessSum;
		// calc_mutation_terms();
		// System.out.println("population mean fitness ( " + type + ", " + otherPop.type + " ) = " + popMeanFitness);
	}

	/**
	 * Calculates the next proportion of individuals choosing the action corresponding to the given fractionKey in the
	 * population distribution ?.
	 *
	 * @fractionKey the key corresponding to the studied action
	 * @otherPop the other population in the total population distribution ?
	 *
	 * @return the updated fraction p^(k+1)
	 */
	double updateFraction(String fractionKey) {
		return fractions.get(fractionKey) * expectedFitness.get(fractionKey) * (1. / popMeanFitness);
	}

	/**
	 * Normalizes the fractions so that the total sum is 1. Each new fraction is updates to the value of that fraction
	 * divided by the total value of all fractions.
	 */
	public void normalizeFractions() {
		// calculate tot
		Enumeration<String> fracs = fractions.keys();
		double tot = 0;
		while (fracs.hasMoreElements()) {
			String current = fracs.nextElement();
			tot += fractions.get(current);
		}

		// put the new values in the fractions hashtable
		fracs = fractions.keys();
		while (fracs.hasMoreElements()) {
			String current = fracs.nextElement();
			fractions.put(current, fractions.get(current) / tot);
		}
	}

	/**
	 * Calculates the sum of all fractions, used for testing purposes.
	 * 
	 * @return the sum of all fractions in this population, should be 1
	 */
	public double getTot() {
		Enumeration<String> fracs = fractions.keys();
		double tot = 0;
		while (fracs.hasMoreElements()) {
			String current = fracs.nextElement();
			tot += fractions.get(current);
		}
		return tot;
	}

	/**
	 * Updates the distributions for all individuals in this population with interactions with the other given
	 * population.
	 *
	 * @param otherPop
	 *            the other population in the population distribution ?
	 */
	void update(Population otherPop) {
		Enumeration<String> players = fractions.keys();
		while (players.hasMoreElements()) {
			String currentPlayer = players.nextElement();
			double updatedFraction = updateFraction(currentPlayer);
			fractions.put(currentPlayer, updatedFraction);
		}
		calc_mutation_terms(); // this update order keeps all fractions normalized (i.e. adding up to one within one
								// update: this order does not appear to keep the fractions normalized... Maybe caused
								// caused by a bug somewhere?

		// population)
		Enumeration<String> players2 = fractions.keys();
		while (players2.hasMoreElements()) {
			String currentPlayer = players2.nextElement();
			double added_mutation = fractions.get(currentPlayer) + mutation_terms.get(currentPlayer);
			fractions.put(currentPlayer, added_mutation);
		}
		normalizeFractions(); // normalize the values so the fractions add up to 1
	}

	/**
	 * [H]
	 * 
	 * @param d
	 * @return double rounded to 4 significant digits (to be used when displaying as a string, not during calculation
	 *         itself)
	 */

	double round(double d) {
		return Math.round(d * 10000.0) / 10000.0;
	}

	/**
	 * Prints relevant data to the console.
	 */
	void print() {
		System.out.println("------------Current values of the " + type + " population are:");
		System.out.println("Petrified fraction: " + round(fractions.get("petrified")) + " with demand: "
				+ demands.get("petrified"));
		System.out.println(
				"Modest fraction: " + round(fractions.get("modest")) + " with demand: " + demands.get("modest"));
		System.out.println("Fair fraction: " + round(fractions.get("fair")) + " with demand: " + demands.get("fair"));
		if (fractions.containsKey("greedy"))
			System.out.println(
					"Greedy fraction: " + round(fractions.get("greedy")) + " with demand: " + demands.get("greedy"));
		if (fractions.containsKey("falsifiable"))
			System.out.println("Falsifiable fraction: " + round(fractions.get("falsifiable")) + " with demand: "
					+ demands.get("falsifiable"));
		System.out.println("Terrorist fraction: " + round(fractions.get("terrorist")) + " with demand: "
				+ demands.get("terrorist"));
	}

	/**
	 * Formats a string for used for printing to a file, can be seen as an alternative to the standard toString()
	 * method.
	 *
	 * @return the formatted string
	 */
	public String filePrint() {
		String out = "";
		if (fractions.containsKey("greedy"))
			out += round(fractions.get("petrified")) + ", " + round(fractions.get("modest")) + ", "
					+ round(fractions.get("fair")) + ", " + round(fractions.get("greedy")) + ", "
					+ round(fractions.get("terrorist"));
		if (fractions.containsKey("falsifiable"))
			out += round(fractions.get("petrified")) + ", " + round(fractions.get("modest")) + ", "
					+ round(fractions.get("fair")) + ", " + round(fractions.get("falsifiable"));
		return out;
	}
}