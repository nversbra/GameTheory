package gt;

/**
 * Created by Nassim on 10/12/15.
 */



public class distribution{
    float[][] p;

    distribution(){
                p = new float[2][];
            }

    float[] demand(){
        return p[0];
    }

    float[] proportions(){
        return p[1];
    }

    int length(){
        return this.p[0].length;
    }

    //fill a distribution with demands and it's fraction
void fundaDistri(distribution p) {


}

void nonFundaDistri(distribution p) {


}
}
