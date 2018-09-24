/*
 * @Author Monde  Mcongwana
*/


import java.awt.Color;
import java.util.Random;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.*;


public class ForestPanel extends JPanel implements Runnable {
	Tree[] forest;	// trees to render
	List<Integer> rndorder; // permutation of tree indices so that rendering is less structured


	ForestPanel(Tree[] trees) {
		forest=trees;
	}

	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		g.clearRect(0,0,width,height);

		// draw the forest in different canopy passes
		Random rnd = new Random(0); // providing the same seed gives trees consistent colouring

		// start from small trees of [0, 2) extent
		float minh = 0.0f;
		float maxh = 2.0f;

		for(int layer = 0; layer <= 10; layer++) {
			for(int t = 0; t < forest.length; t++){
				int rt = rndorder.get(t);
				float extent = forest[rt].getExt(); //local copy of extent variable
				if(extent >= minh && extent < maxh) { // only render trees in current band
					// draw trees as rectangles centered on getX, getY with random greenish colour
					g.setColor(new Color(rnd.nextInt(100), 150+rnd.nextInt(100), rnd.nextInt(100)));
					g.fillRect(forest[rt].getY() - (int) extent, forest[rt].getX() - (int) extent,
						   2*(int) extent+1,2*(int) extent+1);
				}
			}
			minh = maxh;  // next band of trees
			maxh += 2.0f;
		}
	}

	public void run() {

		// reordering so that trees are rendered in a more random fashion
		rndorder = new ArrayList<Integer>();
		for(int l = 0; l < forest.length; l++)
			rndorder.add(l);
		java.util.Collections.shuffle(rndorder);

		int counter = 0;
		// render loop
		while(true) {
			repaint();
			//paintComponent(getGraphics());
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
		}
	}

}
