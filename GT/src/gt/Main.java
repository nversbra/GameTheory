package gt;


public class Main {
	static int iterations = 1000;

	public static void main(String[] args) {
		
		
		/*
		 * 
		 * [H] notes on the parameters
		 * - there are at max. three fractions: modest, fair & greedy, which get a constant value per simulation
		 * 		e.g. there are no multiple modest fractions (with e.g. x_M = 0.1 and x_M = 0.2) in the same simulation
		 * - fundamentalist population is assumed not to have a fair fraction, so always set initial value to 0
		 * - fractions should add up to one for each population separately
		 * - greedy demand is assumed to be the same for both populations
		 * - modest demand is always 1-greedy demand (automatically assigned for given greedy demand)
		 * - fair demand is always 0.5 (automatically assigned)
		 * 
		 * The parameters that determine the outcome of the simulation are:
		 * - the greedy demand
		 * - the initial greedy fundamentalist fraction (since there are only two fractions, the fair one is then also set)
		 * - the initial greedy non-fundamentalist fraction (as I understand from the paper, the values of the other fractions *do not matter*)
		 * 
		 */


		double greedyDemand = 0.6; 
		double greedyFundInit = 0.5;
		double greedyNonFundInit = 0.5;
		
		double[] fundaIniFractions = {0, 1-greedyFundInit, greedyFundInit}; //! fair initial value = 0
		Population funda = new Population(fundaIniFractions, greedyDemand, "fundamentalist");

		double[] nfundaIniFractions = {0.1, 0.4, greedyNonFundInit}; //first values do not influence sim, but three values should add up to one
		Population nfunda = new Population(nfundaIniFractions, greedyDemand, "non-fundamentalist");
		


		System.out.println("initial values");
		funda.print();
		nfunda.print();
		System.out.println("____________");


		for (int i = 0; i < iterations ; i++) {
			funda.update(nfunda);
			nfunda.update(funda);
			if(i % 100 == 0){
				System.out.println("timestep: " + (i+1));
				funda.print();
				nfunda.print();
				System.out.println("____________");
			}
		}

	}
}
