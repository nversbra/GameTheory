package gt;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Main {
	static int iterations = 40;

	public static void main(String[] args) {

		/*
		 * 
		 * [H] notes on the parameters
		 * - there are at max. three fractions: modest, fair & greedy, which get a constant demand value per simulation
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
		 * - the initial greedy non-fundamentalist fraction 
		 * (- the initial fair non-fundamelist fraction can be read from the plots of the paper)
		 */


		double greedyDemand = 0.9; 
		double greedyFundInit = 0.9;
		double greedyNonFundInit = 0;
		double fairNonFundInit = 0.5;

		double[] fundaIniFractions = {0, 1-greedyFundInit, greedyFundInit}; //! fair initial value = 0
		Population funda = new Population(fundaIniFractions, greedyDemand, "fundamentalist");

		double[] nfundaIniFractions = {1-(fairNonFundInit + greedyNonFundInit), fairNonFundInit , greedyNonFundInit}; //first values do not influence sim, but three values should add up to one
		Population nfunda = new Population(nfundaIniFractions, greedyDemand, "non-fundamentalist");

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					// filename : figure_initialFundamelistGreedyFraction_initialNonFundamentalistGreedyFraction.txt
					new FileOutputStream("1d_sigmaG9_rhoG0.txt"), "utf-8"));


		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("initial values");
		funda.print();
		nfunda.print();
		System.out.println("____________");

		

		for (int i = 0; i < iterations ; i++) {
			//if(i % 100 == 0){
			String out = "";
			out = out + Integer.toString(i);
			out += ",";
			//out = out + funda.filePrint();
			out = out + nfunda.filePrint();
			out += ",";
			out = out + funda.filePrint();
			out += "\n";
			//funda.print();
			//nfunda.print();
			
			Hashtable<String, Double> saved_fractions = funda.copyFractions();
			funda.update(nfunda);
			Hashtable<String,Double> updated_fractions = funda.copyFractions();
			funda.fractions = saved_fractions;
			nfunda.update(funda);
			funda.fractions = updated_fractions;

			try {
				writer.write(out);
				//[H] first row is read as a header by ipython (?) and not as data; so by writing it twice the second line will be used and plotted
				if (i==0) 
					writer.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("____________");
			//}
		}
		try{
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		



	}
}
