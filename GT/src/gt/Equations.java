package gt;

/**
 * Created by Nassim on 10/12/15.
 */



public class Equations {
  //  public float replicatorDynamic(float prevDynamic){
   // return (prevDynamic * expectedFitness()) / meanPopFitness;
    //}


float calcPayoff(float iDemand,float jDemand) {
    if ((iDemand + jDemand) <= 1) {
        return iDemand;
    } else return 0;
}

float expectedFitness(float iDemand, distribution p) {
    float tSum = 0;
    for (int i = 0; i < p.length(); i++) {
    tSum += calcPayoff(iDemand, p.demand()[i]) * p.proportions()[i];
    }
    return tSum;
}

float populationMeanFitness(distribution p1, distribution p2){
    float tSum = 0;
    for (int i = 0; i < p1.length(); i++) {
        tSum += p1.proportions()[i] * expectedFitness(p1.demand()[i], p2);
    }
    return tSum;
}

float populationFraction(float popFraction, float iDemand, distribution p, distribution sigma){
 return (popFraction * expectedFitness(iDemand, sigma)) / populationMeanFitness(p, sigma);
}

float populationFraction(int i, distribution p, distribution sigma){
    return (p.proportions()[i] * expectedFitness(p.demand()[i], sigma)) / populationMeanFitness(p, sigma);
}

}
