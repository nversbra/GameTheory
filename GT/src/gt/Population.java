package gt;

import java.util.*;


public class Population {

	String type;

	Hashtable<String,Double> fractions = new Hashtable<String,Double>();
	Hashtable<String,Double> demands = new Hashtable<String,Double>();
	Hashtable<String, Double> expectedFitness = new Hashtable<String,Double>();
	double popMeanFitness;

	Population(String popType){
		type = popType;
	}

	void normalSetup(double[] initFractions, double greedyDemand){

		fractions.put("modest",initFractions[0]);
		fractions.put("fair",initFractions[1]);
		fractions.put("greedy",initFractions[2]);

		demands.put("modest",1-greedyDemand);
		demands.put("fair", (double) 0.5);
		demands.put("greedy", greedyDemand);
		
		expectedFitness.put("modest", 0.0);
		expectedFitness.put("fair", 0.0);
		expectedFitness.put("greedy", 0.0);
		popMeanFitness = 0;
		
		
	}

	void falsifiableSetup(double[] initFractions, double greedyDemand){

		fractions.put("modest",initFractions[0]);
		fractions.put("fair",initFractions[1]);
		fractions.put("falsifiable",initFractions[2]);

		demands.put("modest",1-greedyDemand);
		demands.put("fair", (double) 0.5);
		demands.put("falsifiable", greedyDemand);
		
		expectedFitness.put("modest", 0.0);
		expectedFitness.put("fair", 0.0);
		expectedFitness.put("falsifiable", 0.0);
		popMeanFitness = 0;
	}

	/**
	 *  	Creates a population of the given initial fractions of strategies, level of greedyness and the type of population.
	 *
	 *	@initFraction a double array with the inital fractions (percentages) of the population (modest, fair and greedy)
	 *	@greedyDemand a double representing the demand of the greedy individuals (what level of control they can accept)
	 *	@popType a string representing the type of the population
	 *
	 */
	/*Population(double[] initFractions, double greedyDemand, String popType){

		fractions.put("modest",initFractions[0]);
		fractions.put("fair",initFractions[1]);
		fractions.put("greedy",initFractions[2]);

		demands.put("modest",1-greedyDemand);
		demands.put("fair", (double) 0.5);
		demands.put("greedy", greedyDemand);

		type = popType;
	}*/


	/**
	 *	Calculates the payoff of player type i in an interaction with another player type j.
	 *	see box 2 & 3 in paper 
	 *	! when a non-fundamentalist player (=row-player) meets a fundamentalist player, read the box as is
	 *	! when a fundamentalist player (now the row-player) meets a non-fundamentalist player, read the box as transpose
	 *	
	 *	for a "normal game", there are modest, fair and greedy players, each with a corresponding demand which is independent of the population
	 *	The fact that there are no modest players for a fundamentalist population should be reflected in the fact that the initial modest fraction is set to 0. 
	 *
	 * 	for a "falsifiable game", there are modest, fair and falsifiable players, also with corresponding demands, and the falsifiable demand is set to the greedy demand. 
	 * 	The actual payoff in an encounter (0, 0.5 or x_G) for a falsifiable player depends on the other player and this is taken care of by the calcPayOff function. 
	 *  Again, the fact that there are no modest players for a fundamentalist population should be reflected in the fact that the initial modest fraction is set to 0. 
	 * 	
	 */

	double calcPayoff(String rowPlayer, String columnPlayer){
		//box 3: extension for "falsifiable fundamentalism"
		if (rowPlayer == "falsifiable" && columnPlayer == "falsifiable"){
			return 0.5;
		}
			
		if (rowPlayer == "falsifiable" && columnPlayer == "fair") //added for clarity, but this is actually superfluent (since x_G + x_F > 1)
			return 0;

		//box 3 and box 2: "clash of cultures"
		double rowDemand = demands.get(rowPlayer);
		double columnDemand = demands.get(columnPlayer); //the demands are independent of the population type, so this is valid
		if (rowDemand + columnDemand <= 1)
			return rowDemand;
		else
			return 0;
	}


	/**
	 *	Calculate the expected fitness of a demand x_i under a population distribution of ?^k
	 *
	 *	@playerType the type of player (modest - fair - greedy - falsifiable)
	 *	@pop the other population in the total population distribution ?
	 *
	 *	@return the expected fitness represented as a double
	 */
	double expectedFitness(String playerType, Population pop) {
		double tSum = 0;
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy/falsifiable
		while(players.hasMoreElements()){
			String otherPlayer = players.nextElement();
			tSum += calcPayoff(playerType, otherPlayer) * pop.fractions.get(otherPlayer);	
		}
		//System.out.println("Expected fitness for player " + playerType + " against " + pop.type + " = " + tSum);
		return tSum;
	}

	/**
	 *	Calculates the mean population fitness of the population distribution ?
	 *
	 *	@otherPop the other population in the total population distribution ?
	 *
	 *	@return the mean population fitness represented as a double
	 */
	/*double populationMeanFitness(Population otherPop){
		double tSum = 0;
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy/falsifiable
		while(players.hasMoreElements()){
			String currentPlayer = players.nextElement();
			//System.out.println("Current player: " + currentPlayer);
			//System.out.println(demands.get(currentPlayer));
			tSum += fractions.get(currentPlayer) * expectedFitness(currentPlayer, otherPop);
		}
		System.out.println("population mean fitness ( " + type + ", " + otherPop.type + " ) = " + tSum);
		return tSum;
	}*/

	/**
	 *	Calculates the next proportion of individuals choosing the action corresponding to the given fractionKey in the population
	 *	distribution ?.
	 *
	 *	@fractionKey the key corresponding to the studied action
	 *	@otherPop the other population in the total population distribution ?
	 *
	 *	@return the updated fraction p^(k+1)
	 */
	double updateFraction(String fractionKey){
		return fractions.get(fractionKey) * expectedFitness.get(fractionKey) * (1./popMeanFitness);
	}


	/**
	 *	:)
	 */
	/*Hashtable<String, Double> copyFractions(){
		Hashtable<String,Double> copiedFractions = new Hashtable<String,Double>();
		if (fractions.containsKey("greedy"))
			copiedFractions.put("greedy", fractions.get("greedy"));
		if (fractions.containsKey("falsifiable"))
			copiedFractions.put("falsifiable", fractions.get("falsifiable"));
		copiedFractions.put("modest", fractions.get("modest"));
		copiedFractions.put("fair", fractions.get("fair"));
		return copiedFractions;	
	}*/
	
	/**
	 * 
	 */
	
	void prepareUpdate(Population otherPop){
		Enumeration<String> players = expectedFitness.keys(); //[H] modest, fair, greedy/falsifiable
		double meanFitnessSum = 0;
		while(players.hasMoreElements()){
			String currentPlayer = players.nextElement();
			double expecFit = expectedFitness(currentPlayer, otherPop);
			expectedFitness.put(currentPlayer, expecFit);
			//System.out.println("expected fitness " + expecFit );
			meanFitnessSum += fractions.get(currentPlayer) * expecFit;
		}
		popMeanFitness = meanFitnessSum;
		//System.out.println("population mean fitness ( " + type + ", " + otherPop.type + " ) = " + popMeanFitness);
	}


	/**
	 *	Updates the distributions for all individuals in this population with interactions with the other given population.
	 *
	 *	@otherPop the other population in the population distribution ?
	 */
	void update(Population otherPop){
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy/falsifiable
		while(players.hasMoreElements()){
			String currentPlayer = players.nextElement();
			double updatedFraction = updateFraction(currentPlayer);
			fractions.put(currentPlayer, updatedFraction);
		}
	}


	/**
	 * [H]
	 * @param d
	 * @return double rounded to 4 significant digits (to be used when displaying as a string, not during calculation itself)
	 */

	double round(double d){
		return Math.round(d * 10000.0) / 10000.0 ;
	}


	/**
	 *	Prints relevant data to the console.
	 */
	void print(){
		System.out.println("Current values of the " + type + " population are:");
		System.out.println("Modest fraction: " + round(fractions.get("modest")) + " with demand: " + demands.get("modest"));
		System.out.println("Fair fraction: " + round(fractions.get("fair")) + " with demand: " + demands.get("fair"));
		if (fractions.containsKey("greedy"))
			System.out.println("Greedy fraction: " + round(fractions.get("greedy")) + " with demand: " + demands.get("greedy"));	
		if (fractions.containsKey("falsifiable"))
			System.out.println("Falsifiable fraction: " + round(fractions.get("falsifiable")) + " with demand: " + demands.get("falsifiable"));	
			
	}

	/**
	 *	Formats a string for used for printing to a file, can be seen as an alternative to the standard toString() method.
	 *
	 *	@return the formatted string
	 */
	public String filePrint() {
		String out= "";
		if (fractions.containsKey("greedy"))
			out += round(fractions.get("modest")) +", " + round(fractions.get("fair"))+ ", " + round(fractions.get("greedy"));
		if (fractions.containsKey("falsifiable"))
			out += round(fractions.get("modest")) +", " + round(fractions.get("fair"))+ ", " + round(fractions.get("falsifiable"));
		return  out;
	}
}