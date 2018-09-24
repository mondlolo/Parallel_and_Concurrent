/*
 * @Author Monde  Mcongwana
*/


import java.util.concurrent.RecursiveAction;

public class SunaValueThread  extends RecursiveAction{
    int low;
    int high;
    int layerStart;
    int layerStop;

    static final int SEQUENTIAL_CUTOFF =250001; //for a dataset of 1 million, this SC will result in 4 threads

    SunaValueThread(int lstart, int lstop, int l, int h){
        layerStop = lstop;
        layerStart = lstart;
        low = l;
        high = h;
    }

    protected void compute(){
        if((high-low)<=SEQUENTIAL_CUTOFF){ // this is run sequentially
            //sequentially goes through the entire tree array to find and compute the trees in the layer
             for(int i=low;i<high;i++){
                 Tree t = ThreadSimulationLoop.sundataLocal.trees[i]; //gets a tree from the array of trees.

                 if(t.inrange(layerStart,layerStop)){ //if this tree is in the current level
                        t.sungrow(ThreadSimulationLoop.sundataLocal.sunmap);//call method in Tree class to grow this tree
                 }

             }
        }
        else{
            SunaValueThread left = new SunaValueThread(layerStart, layerStop, low, (high+low)/2);
            SunaValueThread right = new SunaValueThread(layerStart, layerStop, (high+low)/2, high);
            left.fork(); //left thread starts (.compute())
            right.compute();
            left.join(); //wait for left thread to complete
        }

    }//end compute

}//end class
