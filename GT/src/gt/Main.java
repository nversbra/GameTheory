package gt;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Hashtable;

public class Main {
	static int iterations = 50;

	public static void main(String[] args) {
		double greedyDemand = 0.6;
		double greedyOrFalsiFundInit = 0.50; // greedy (normal game) or falsifiable (game with falsifiable players)
												// initial fundamentalist fraction
		double greedyOrFalsiNonFundInit = 0.00; // greedy (normal game) or falsifiable (game with falsifiable players)
												// initial non-fundamentalist fraction
		double fairNonFundInit = 0.50;

		double petrifiedNonFundInit = 0.0;
		double terroristFundInit = 0.0;

		double[] fundaIniFractions = { 0, 0, 1 - greedyOrFalsiFundInit - terroristFundInit, greedyOrFalsiFundInit,
				terroristFundInit }; // ! modest initial value = 0 for all fundamentalist populations (since there are
										// no modest players)
		Population funda = new Population("fundamentalist");
		double[] nfundaIniFractions = { petrifiedNonFundInit,
				1 - (fairNonFundInit + greedyOrFalsiNonFundInit + petrifiedNonFundInit), fairNonFundInit,
				greedyOrFalsiNonFundInit, 0 }; // first values do not influence sim, but three values should add up to
												// one
		Population nfunda = new Population("non-fundamentalist");

		double zero = 0.0; // just a variable that is used at places where the value should always be, logically
							// speaking, zero : )

		double omega = 0.3; // probability for mutations into terrorist

		/***************************************************
		 * mutation chances for non-fundamentalist population
		 ***************************************************/
		Hashtable<String, Hashtable<String, Double>> W = new Hashtable<String, Hashtable<String, Double>>();

		Hashtable<String, Double> mutations_to_petrified = new Hashtable<String, Double>();
		mutations_to_petrified.put("petrified", zero);
		mutations_to_petrified.put("modest", terroristFundInit);
		mutations_to_petrified.put("fair", terroristFundInit);
		mutations_to_petrified.put("greedy", zero);
		mutations_to_petrified.put("terrorist", zero);
		W.put("petrified", mutations_to_petrified);

		Hashtable<String, Double> mutations_to_modest = new Hashtable<String, Double>();
		mutations_to_modest.put("petrified", zero);
		mutations_to_modest.put("modest", zero);
		mutations_to_modest.put("fair", zero);
		mutations_to_modest.put("greedy", zero);
		mutations_to_modest.put("terrorist", zero);
		W.put("modest", mutations_to_modest);

		Hashtable<String, Double> mutations_to_fair = new Hashtable<String, Double>();
		mutations_to_fair.put("petrified", zero);
		mutations_to_fair.put("modest", zero);
		mutations_to_fair.put("fair", zero);
		mutations_to_fair.put("greedy", zero);
		mutations_to_fair.put("terrorist", zero);
		W.put("fair", mutations_to_fair);

		Hashtable<String, Double> mutations_to_greedy = new Hashtable<String, Double>();
		mutations_to_greedy.put("petrified", zero);
		mutations_to_greedy.put("modest", zero);
		mutations_to_greedy.put("fair", zero);
		mutations_to_greedy.put("greedy", zero);
		mutations_to_greedy.put("terrorist", zero);
		W.put("greedy", mutations_to_greedy);

		Hashtable<String, Double> empty_terrorist = new Hashtable<String, Double>();
		empty_terrorist.put("petrified", zero);
		empty_terrorist.put("modest", zero);
		empty_terrorist.put("fair", zero);
		empty_terrorist.put("greedy", zero);
		empty_terrorist.put("terrorist", zero);
		W.put("terrorist", empty_terrorist);

		/***************************************************
		 * mutation chances for fundamentalist population **
		 ***************************************************/
		Hashtable<String, Hashtable<String, Double>> Omega = new Hashtable<String, Hashtable<String, Double>>();

		Hashtable<String, Double> empty_petrified = new Hashtable<String, Double>();
		empty_petrified.put("petrified", zero);
		empty_petrified.put("modest", zero);
		empty_petrified.put("fair", zero);
		empty_petrified.put("greedy", zero);
		empty_petrified.put("terrorist", zero);
		Omega.put("petrified", empty_petrified);

		Hashtable<String, Double> empty_modest = new Hashtable<String, Double>();
		empty_modest.put("petrified", zero);
		empty_modest.put("modest", zero);
		empty_modest.put("fair", zero);
		empty_modest.put("greedy", zero);
		empty_modest.put("terrorist", zero);
		Omega.put("modest", empty_modest);

		Hashtable<String, Double> mutations_to_fair_fund = new Hashtable<String, Double>();
		mutations_to_fair_fund.put("petrified", zero);
		mutations_to_fair_fund.put("modest", zero);
		mutations_to_fair_fund.put("fair", zero);
		mutations_to_fair_fund.put("greedy", zero);
		mutations_to_fair_fund.put("terrorist", zero);
		Omega.put("fair", mutations_to_fair_fund);

		Hashtable<String, Double> mutations_to_greedy_f = new Hashtable<String, Double>();
		mutations_to_greedy_f.put("petrified", zero);
		mutations_to_greedy_f.put("modest", zero);
		mutations_to_greedy_f.put("fair", zero);
		mutations_to_greedy_f.put("greedy", zero);
		mutations_to_greedy_f.put("terrorist", zero);
		Omega.put("greedy", mutations_to_greedy_f);

		Hashtable<String, Double> mutations_to_terrorist = new Hashtable<String, Double>();
		mutations_to_terrorist.put("petrified", zero);
		mutations_to_terrorist.put("modest", zero);
		mutations_to_terrorist.put("fair", omega);
		mutations_to_terrorist.put("greedy", omega);
		mutations_to_terrorist.put("terrorist", zero);
		Omega.put("terrorist", mutations_to_terrorist);

		// first type of game (box 2) with extension
		funda.normalSetup(fundaIniFractions, greedyDemand, Omega);
		nfunda.normalSetup(nfundaIniFractions, greedyDemand, W);

		// game with preference falsification (box 3)
		// funda.falsifiableSetup(fundaIniFractions, greedyDemand);
		// nfunda.falsifiableSetup(nfundaIniFractions, greedyDemand);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					// new FileOutputStream("results_28_12/1d_rhoG0/1d_sigmaG9_rhoG0.txt"), "utf-8"));
					new FileOutputStream("results_extensions/test.txt"), "utf-8"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * System.out.println("initial values"); funda.print(); nfunda.print(); System.out.println("____________");
		 */

		for (int i = 0; i < iterations; i++) {
			// if(i % 100 == 0){
			String out = "";
			out = out + Integer.toString(i);
			out += ",";
			// out = out + funda.filePrint();
			out = out + nfunda.filePrint();
			out += ",";
			out = out + funda.filePrint();
			out += "\n";
			funda.print();
			nfunda.print();

			// make terrorist attacks (thus the corresponding mutations) dependent on greedy fundamentalist fraction at
			// time step t: W_TG (probability for greedy fundamentalist to mutate into terrorist) is already set to
			// omega

			Hashtable<String, Double> mut_to_petrified = new Hashtable<String, Double>();
			mut_to_petrified.put("petrified", zero);
			mut_to_petrified.put("modest", funda.fractions.get("terrorist"));
			mut_to_petrified.put("fair", funda.fractions.get("terrorist"));
			// mut_to_petrified.put("modest", (nfunda.fractions.get("modest") + funda.fractions.get("terrorist"))/2);
			// mut_to_petrified.put("fair", (nfunda.fractions.get("fair") + funda.fractions.get("terrorist"))/2);
			// mut_to_petrified.put("fair", 0.0);
			mut_to_petrified.put("greedy", zero);
			mut_to_petrified.put("terrorist", zero);
			nfunda.mut_freq.put("petrified", mut_to_petrified);

			nfunda.prepareUpdate(funda);
			funda.prepareUpdate(nfunda);
			nfunda.update(funda);
			funda.update(nfunda);

			try {
				writer.write(out);
				// [H] first row is read as a header by ipython (?) and not as data; so by writing it twice the second
				// line will be used and plotted
				if (i == 0)
					writer.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("____________");
			// }
		}

		double petrified = nfunda.fractions.get("petrified");
		double modest = nfunda.fractions.get("modest");
		double fair = nfunda.fractions.get("fair");
		double greedy = nfunda.fractions.get("greedy");
		double terrorist = nfunda.fractions.get("greedy");

		System.out.println(petrified);
		System.out.println(modest);
		System.out.println(fair);
		System.out.println(greedy);
		System.out.println(terrorist);

		double sum = petrified + modest + fair + greedy + terrorist;
		System.out.println("totfrac: " + sum);
		System.out.println("getTotfrac: " + nfunda.getTot());

		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
