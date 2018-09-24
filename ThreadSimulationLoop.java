/*
 * @Author Monde  Mcongwana
*/


import java.util.concurrent.ForkJoinPool;
import java.util.*;
import java.io.*;

public class ThreadSimulationLoop extends java.lang.Thread{
	static SunData sundataLocal;
	int year;
	static long startTime = 0;

	ThreadSimulationLoop(SunData sundata){
		sundataLocal=sundata;
	}

	static final ForkJoinPool fjPool = new ForkJoinPool();

	public void reset(){
		//reset all ext to 0.4:. this always runs only after the tree growth calculations are done and so no protection is needed
		for(Tree t : sundataLocal.trees){
			t.setExt(0.4f);
		}

		//reset year counter to 0
		year = 0;

		TreeGrow.resetbtn = false;
	}

	private static void tick(){
        startTime = System.currentTimeMillis();
    }
    private static float tock(){
        return (System.currentTimeMillis()-startTime)/1000.0f;
    }

	public void nextYear(){
		year ++;
		//seqeuntially go through layers from top to bottom
		for(int i =18; i>=0;i-=2){
			int startLayer =i;
			int endLayer = i+2;

			//start a thread to handle the calculations for the current layer
			fjPool.invoke(new SunaValueThread(startLayer,endLayer,0, sundataLocal.trees.length));

		}
	}

	public void run(){
		System.out.println("sim is running");

		//set year counter to 0
		year = 0;
    reset();
		while(true){ //while the program is running

			//tick();
			nextYear(); //calls nextYear() method to calculate new sun values for a new year which then grows all the trees
			//System.out.println(tock());

			//update year counter text field
			TreeGrow.yearLabel.setText("year "+year);

			//check buttons

			if(TreeGrow.resetbtn){ //if reset button pressed, reset extents to 0.4
				reset();
			}

			if(TreeGrow.pausebtn){ //if pause button pressed then wait for play button to be pressed
				TreeGrow.pausebtn = false;

				while(!TreeGrow.playbtn){ //wait until play button is pressed to continue
					try{
						Thread.sleep(1);
					}
					catch(InterruptedException ex){
						Thread.currentThread().interrupt();
					}
				}
				TreeGrow.playbtn = false;
			}

			else if(TreeGrow.endbtn){ //end program if End button is pressed.
				System.exit(0);
			}

			//reset the sun for the new year
			sundataLocal.sunmap.resetShade();

			//this pause is longer than the rendering pause of 20ms. Thus allowing the rendering to catch up with this thread.
			try{
				Thread.sleep(50);
			}
			catch(InterruptedException ex){
				Thread.currentThread().interrupt();
			}


		}


	}
}
