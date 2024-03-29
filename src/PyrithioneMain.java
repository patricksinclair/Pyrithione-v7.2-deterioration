import org.apache.commons.math3.distribution.PoissonDistribution;

public class PyrithioneMain {
    public static void main(String[] args){


        double scale_99 = 2.71760274, sigma_99 = 0.56002833;
        double scale_95 = 1.9246899, sigma_95 = 1.00179994;
        double scale_90 = 1.01115312, sigma_90 = 1.51378016;

        double tau_val = 0.02;

        //BioSystem.varyingDeteriorationAndThreshold(tau_val);
        BioSystem.varyingTauStep(scale_99, sigma_99);
    }
}
