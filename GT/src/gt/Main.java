package gt;

public class Main {
    static int iterations = 2000;

    public static void main(String[] args) {

       Equations e =new Equations();
       distribution funda = new distribution();
       distribution sigma = new distribution();

        funda.fundaDistri(funda);
        sigma.nonFundaDistri(sigma);

        for (int i = 0; i < iterations ; i++) {
            for (int j = 0; j < funda.length(); j++) {
                funda.proportions()[j]= e.populationFraction(j, funda, sigma);
            }
            for (int j = 0; j < sigma.length(); j++) {
                sigma.proportions()[j]= e.populationFraction(j, sigma, funda);
            }
        }

    }
}
