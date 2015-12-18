package gt;

import java.util.*;


public class Population {

	String type;

	Hashtable<String,Double> fractions = new Hashtable<String,Double>();
	Hashtable<String,Double> demands = new Hashtable<String,Double>();

	/**
	*  	Creates a population of the given initial fractions of strategies, level of greedyness and the type of population.
	*
	*	@initFraction a double array with the inital fractions (percentages) of the population (modest, fair and greedy)
	*	@greedyDemand a double representing the demand of the greedy individuals (what level of control they can accept)
	*	@popType a string representing the type of the population
	*
	*/
	Population(double[] initFractions, double greedyDemand, String popType){

		fractions.put("modest",initFractions[0]);
		fractions.put("fair",initFractions[1]);
		fractions.put("greedy",initFractions[2]);

		demands.put("modest",1-greedyDemand);
		demands.put("fair", (double) 0.5);
		demands.put("greedy",1-greedyDemand);

		type = popType;
	}
	
	/**
	*	Calculates the payoff of x_i in an interaction with another individual x_j. The payoff is x_j:s demand
	*	if the sum of both players demand are less than or equal to 1, otherwise 0.
	*
	*	@iDemand the demand if the player x_i
	*	@jDemand the demand of the player x_j
	*
	*	@return iDemand if iDemand + jDemand <= 1, otherwise 0
	*/
	double calcPayoff(double iDemand,double jDemand) {
		if ((iDemand + jDemand) <= 1) {
			return iDemand;
		} else return 0;
	}

	/**
	*	Calculate the expected fitness of a demand x_i under a population distribution of ?^k
	*
	*	@iDemand the demand x_i
	*	@pop the other population in the total population distribution ?
	*
	*	@return the expecnted fitness represented as a double
	*/
	double expectedFitness(double iDemand, Population pop) {
		double tSum = 0;
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy
		while(players.hasMoreElements()){
			String otherPlayer = players.nextElement();
			tSum += calcPayoff(iDemand, pop.demands.get(otherPlayer)) * pop.fractions.get(otherPlayer);	
		}
		return tSum;
	}

	/**
	*	Calculates the mean population fitness of the population distribution ?
	*
	*	@otherPop the other population in the total population distribution ?
	*
	*	@return the mean population fitness represented as a double
	*/
	double populationMeanFitness(Population otherPop){
		double tSum = 0;
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy
		while(players.hasMoreElements()){
			String currentPlayer = players.nextElement();
			tSum += fractions.get(currentPlayer) * expectedFitness(demands.get(currentPlayer), otherPop);
		}
		return tSum;
	}
	
	/**
	*	Calculates the next proportion of individuals choosing the action corresponding to the given fractionKey in the population
	*	distribution ?.
	*
	*	@fractionKey the key corresponding to the studied action
	*	@otherPop the other population in the total population distribution ?
	*
	*	@return the updated fraction p^(k+1)
	*/
	double updateFraction(String fractionKey, Population otherPop){
		return fractions.get(fractionKey) * expectedFitness(demands.get(fractionKey), otherPop) * (1./populationMeanFitness(otherPop));
	}
	
	
	/**
	*	:)
	*/
	Hashtable<String, Double> copyFractions(){
		Hashtable<String,Double> copiedFractions = new Hashtable<String,Double>();
		copiedFractions.put("greedy", fractions.get("greedy"));
		copiedFractions.put("modest", fractions.get("modest"));
		copiedFractions.put("fair", fractions.get("fair"));
		return copiedFractions;	
	}


	/**
	*	Updates the distributions for all individuals in this population with interactions with the other given population.
	*
	*	@otherPop the other population in the population distribution ?
	*/
	void update(Population otherPop){
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy
		while(players.hasMoreElements()){
			String currentPlayer = players.nextElement();
			double updatedFraction = updateFraction(currentPlayer, otherPop);
			fractions.put(currentPlayer, updatedFraction);
		}
	}


	/**
	*	Prints relevant data to the console.
	*/
	void print(){
		System.out.println("Current values of the " + type + " population are:");
		System.out.println("Modest fraction: " + fractions.get("modest") + " with demand: " + demands.get("modest"));
		System.out.println("Fair fraction: " + fractions.get("fair") + " with demand: " + demands.get("fair"));
		System.out.println("Greedy fraction: " + fractions.get("greedy") + " with demand: " + demands.get("greedy"));	
	}

	/**
	*	Formats a string for used for printing to a file, can be seen as an alternative to the standard toString() method.
	*
	*	@return the formatted string
	*/
	public String filePrint() {
		String out= "";
		out += fractions.get("modest") +", " + fractions.get("fair")+ ", " + fractions.get("greedy");
		return  out;
	}
}