/*
 * @Author Monde  Mcongwana
*/
import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.*;

public class TreeGrow {
	static long startTime = 0;
	static int frameX;
	static int frameY;
	static ForestPanel fp;

	static boolean resetbtn = false;
	static boolean pausebtn = false;
	static boolean playbtn = false;
	static boolean endbtn = false;
	static JLabel yearLabel;
	static JFrame frame;

	// start timer
	private static void tick(){
		startTime = System.currentTimeMillis();
	}

	// stop timer, return time elapsed in seconds
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f;
	}

	public static void setupGUI(int frameX,int frameY,Tree [] trees) {
		Dimension fsize = new Dimension(800, 800);
		// Frame init and dimensions
    	frame = new JFrame("Photosynthesis");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setPreferredSize(fsize);
    	frame.setSize(800, 800);

      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));
      	g.setPreferredSize(fsize);

		fp = new ForestPanel(trees);
		fp.setPreferredSize(new Dimension(frameX,frameY));
		JScrollPane scrollFrame = new JScrollPane(fp);
		fp.setAutoscrolls(true);
		scrollFrame.setPreferredSize(fsize);
	    g.add(scrollFrame);


	    JPanel buttons = new JPanel();
	    buttons.setLayout(new FlowLayout());
	    // add year label
	    yearLabel = new JLabel();
	    buttons.add(yearLabel);


			//add Play button
	    JButton play = new JButton();
	    play.setSize(400,400);
   		play.setVisible(true);
    	play.setText("Play");
    	buttons.add(play);

			play.addActionListener(new ActionListener() {
  			 public void actionPerformed(ActionEvent arg0) {
   				 playbtn = true;
  			 }
  		});

    	//add Pause button
	    JButton pause = new JButton();
	    pause.setSize(400,400);
   		pause.setVisible(true);
    	pause.setText("Pause");
    	buttons.add(pause);

    	pause.addActionListener(new ActionListener() {
  			 public void actionPerformed(ActionEvent arg0) {
   				 pausebtn = true;
  			 }
  		});

			//add reset button
	    JButton reset = new JButton();
	    reset.setSize(400,400);
   		reset.setVisible(true);
    	reset.setText("Reset");
    	buttons.add(reset);


			reset.addActionListener(new ActionListener() {
  			 public void actionPerformed(ActionEvent arg0) {
   				 resetbtn = true;
  			 }
  		});


    	//add end button
	    JButton end = new JButton();
	    end.setSize(400,400);
   		end.setVisible(true);
    	end.setText("End");
    	buttons.add(end);

    	end.addActionListener(new ActionListener() {
  			 public void actionPerformed(ActionEvent arg0) {
   				 endbtn = true;
  			 }
  		});

    	g.add(buttons);

      	frame.setLocationRelativeTo(null);  // Center window on screen.
      	frame.add(g); //add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
        Thread fpt = new Thread(fp);
        fpt.start();

	}

	static public void endProgram(){
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}


	public static void main(String[] args) {
		SunData sundata = new SunData();

		// check that number of command line arguments is correct
		if(args.length != 1)
		{
			System.out.println("Incorrect number of command line arguments. Should have form: java treeGrow.java intputfilename");
			System.exit(0);
		}

		// read in forest and landscape information from file supplied as argument
		sundata.readData(args[0]);
		System.out.println("Data loaded");

		frameX = sundata.sunmap.getDimX();
		frameY = sundata.sunmap.getDimY();
		setupGUI(frameX, frameY, sundata.trees);


		// create and start simulation loop here as separate thread
		ThreadSimulationLoop sim = new ThreadSimulationLoop(sundata);
		sim.start();
	}
}
