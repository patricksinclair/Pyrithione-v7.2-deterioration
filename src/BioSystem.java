import java.util.ArrayList;
import java.util.Random;

class BioSystem {

    private Random rand = new Random();

    private double alpha, c_max; //steepness and max val of antimicrobial concn
    private double scale = 2.71760274, sigma = 0.56002833; //mic distb shape parameters
    private ArrayList<Microhabitat> microhabitats;
    private double time_elapsed, exit_time; //exit time is the time it took for the biofilm to reach the thickness limit, if it did
    private int immigration_index;

    private double deterioration_rate;
    private double immigration_rate = 0.8;
    private double tau = 0.01;
    private double delta_x = 5.;
    private int thickness_limit = 10; //this is how big the system can get before we exit. should reduce overall simulation duration todo-change back to 50
    private int n_detachments = 0, n_deaths = 0, n_replications = 0, n_immigrations = 0;


    public BioSystem(double deterioration_rate){
        //this constructor is used purely for the detachment rate determination in the biocide free environment
        this.alpha = 0.;
        this.c_max = 0.;
        this.microhabitats = new ArrayList<>();
        this.time_elapsed = 0.;
        this.exit_time = 0.;
        this.immigration_index = 0;
        this.deterioration_rate = deterioration_rate;

        microhabitats.add(new Microhabitat(calc_C_i(0, this.c_max, this.alpha, delta_x), scale, sigma));

        microhabitats.get(0).setSurface();
        microhabitats.get(0).addARandomBacterium_x_N(5);
    }


    private int getN_detachments(){return n_detachments;}
    private int getN_deaths(){return n_deaths;}
    private int getN_replications(){return n_replications;}
    private int getN_immigrations(){return n_immigrations;}

    private double getTimeElapsed(){return time_elapsed;}
    private double getExit_time(){return exit_time;}
    private int getSystemSize(){return microhabitats.size();}

    private int getTotalN(){
        int runningTotal = 0;
        for(Microhabitat m : microhabitats) {
            runningTotal += m.getN();
        }
        return runningTotal;
    }

    private int getBiofilmEdge(){
        int edgeIndex = 0;
        for(int i = 0; i < microhabitats.size(); i++){
            if(microhabitats.get(i).isBiofilm_region()) edgeIndex = i;
        }
        return edgeIndex;
    }

    private int getBiofilmThickness(){
        int thickness = 0;
        for(int i = 0; i < microhabitats.size(); i++){
            if(microhabitats.get(i).isBiofilm_region()) thickness = i+1;
        }
        return thickness;
    }


    private void immigrate(int mh_index, int n_immigrants){
        microhabitats.get(mh_index).addARandomBacterium_x_N(n_immigrants);
    }

    public void migrate(int mh_index, int bac_index){

        double migrating_bac = microhabitats.get(mh_index).getPopulation().get(bac_index);
        microhabitats.get(mh_index).removeABacterium(bac_index);

        if(microhabitats.get(mh_index).isSurface()){
            microhabitats.get(mh_index+1).addABacterium(migrating_bac);
        }else if(microhabitats.get(mh_index).isImmigration_zone()){
            microhabitats.get(mh_index-1).addABacterium(migrating_bac);
        }else{
            if(rand.nextBoolean()){
                microhabitats.get(mh_index+1).addABacterium(migrating_bac);
            }else{
                microhabitats.get(mh_index-1).addABacterium(migrating_bac);
            }
        }
    }


    private void updateBiofilmSize(){
        //once the edge microhabitat is sufficiently populated, this adds another microhabitat onto the system list
        //which is then used as the immigration zone

        if(microhabitats.get(immigration_index).atBiofilmThreshold()){

            microhabitats.get(immigration_index).setBiofilm_region();
            microhabitats.get(immigration_index).setImmigration_zone(false);

            int i = microhabitats.size();
            microhabitats.add(new Microhabitat(BioSystem.calc_C_i(i, c_max, alpha, delta_x), scale, sigma));
            immigration_index = i;
            microhabitats.get(immigration_index).setImmigration_zone(true);
        }

        //this stops sims going onn unnecessarily too long. if the biofilm reaches the thickness limit then we record the
        //time this happened at and move on
        if(getSystemSize()==thickness_limit){
            exit_time = time_elapsed;
            time_elapsed = 9e9; //this way the time elapsed is now way above the duration value, so the simulation will stop
        }
    }


    public void performAction(){

        double tau_step = tau;

    }












    private static double calc_C_i(int i, double c_max, double alpha, double delta_x){
        return c_max*Math.exp(-alpha*i*delta_x);
    }

}
