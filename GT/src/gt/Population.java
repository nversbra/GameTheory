package gt;

import java.util.*;


public class Population {

	String type;

	Hashtable<String,Double> fractions = new Hashtable<String,Double>();
	Hashtable<String,Double> demands = new Hashtable<String,Double>();

	Population(double[] initFractions, double greedyDemand, String popType){

		fractions.put("modest",initFractions[0]);
		fractions.put("fair",initFractions[1]);
		fractions.put("greedy",initFractions[2]);

		demands.put("modest",1-greedyDemand);
		demands.put("fair", (double) 0.5);
		demands.put("greedy",1-greedyDemand);

		type = popType;
	}
	
	double calcPayoff(double iDemand,double jDemand) {
		if ((iDemand + jDemand) <= 1) {
			return iDemand;
		} else return 0;
	}

	double expectedFitness(double iDemand, Population pop) {
		double tSum = 0;
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy
		while(players.hasMoreElements()){
			String otherPlayer = players.nextElement();
			tSum += calcPayoff(iDemand, pop.demands.get(otherPlayer)) * pop.fractions.get(otherPlayer);	
		}
		return tSum;
	}

	double populationMeanFitness(Population otherPop){
		double tSum = 0;
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy
		while(players.hasMoreElements()){
			String currentPlayer = players.nextElement();
			tSum += fractions.get(currentPlayer) * expectedFitness(demands.get(currentPlayer), otherPop);
		}
		return tSum;
	}
	
	double updateFraction(String fractionKey, Population otherPop){
		return fractions.get(fractionKey) * expectedFitness(demands.get(fractionKey), otherPop) * (1./populationMeanFitness(otherPop));
	}


	void update(Population otherPop){
		Enumeration<String> players = fractions.keys(); //[H] modest, fair, greedy
		while(players.hasMoreElements()){
			String currentPlayer = players.nextElement();
			double updatedFraction = updateFraction(currentPlayer, otherPop);
			fractions.put(currentPlayer, updatedFraction);
		}
	}



	void print(){
		System.out.println("Current values of the " + type + " population are:");
		System.out.println("Modest fraction: " + fractions.get("modest") + " with demand: " + demands.get("modest"));
		System.out.println("Fair fraction: " + fractions.get("fair") + " with demand: " + demands.get("fair"));
		System.out.println("Greedy fraction: " + fractions.get("greedy") + " with demand: " + demands.get("greedy"));	
	}


}