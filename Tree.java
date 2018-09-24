/*
 * @Author Monde  Mcongwana
*/

// Trees define a canopy which covers a square area of the landscape
import java.lang.Math;

public class Tree{

private
	int xpos;	// x-coordinate of center of tree canopy
	int ypos;	// y-coorindate of center of tree canopy
	float ext;	// extent of canopy out in vertical and horizontal from center
	int numBlockThatCount;

	static float growfactor = 1000.0f; // divide average sun exposure by this amount to get growth in extent

public
	Tree(int x, int y, float e){
		xpos=x; ypos=y; ext=e;
	}

	int getX() {
		return xpos;
	}

	int getY() {
		return ypos;
	}

	synchronized float getExt() {
		return ext;
	}

	synchronized void setExt(float e) {
		ext = e;
	}

	// is the tree extent within the provided range [minr, maxr)
	boolean inrange(float minr, float maxr) {
		return (ext >= minr && ext < maxr);
	}

	// grow a tree according to its sun exposure
	void sungrow(Land land) {

		float sun = 0;
		int xlimit = land.getDimX();
		int ylimit = land.getDimY();
		int size = Math.round(ext);
		numBlockThatCount=0;

		//determine the LandChunks that the 4 corners are in and block those 4 chunks
		//corner 1: position <xpos-size,  ypos-size> corner 2: position <xpos-size,  ypos+size> corner 3: position <xpos+size,  ypos-size> corner 4: position <xpos+size,  ypos+size>

		int c1x = xpos-size; int c1y = ypos-size;
		int c2x = xpos-size; int c2y = ypos+size;
		int c3x = xpos+size; int c3y = ypos-size;
		int c4x = xpos+size; int c4y = ypos+size;

		if (xpos-size<0){
			c1x = 0;
			c2x = 0;
		}
		if (ypos-size<0){
			c1y =0;
			c3y = 0;
		}
		if (xpos+size>=xlimit){
			c3x = xlimit-1;
			c4x = xlimit-1;
		}
		if (ypos+size>=ylimit){
			c2y = ylimit-1;
			c4y = ylimit-1;
		}

		int [] corner1 = land.getPortionPositions(c1x,  c1y);
		int [] corner2 = land.getPortionPositions(c2x,  c2y);
		int [] corner3 = land.getPortionPositions(c3x,  c3y);
		int [] corner4 = land.getPortionPositions(c4x,  c4y);


		// all 4 possible blocks that the tree could be in are blocked. These are the blocks where the trees corners are in.
		synchronized(land.segSunArray[corner1[0]][corner1[1]]){
			synchronized(land.segSunArray[corner2[0]][corner2[1]]){
				synchronized(land.segSunArray[corner3[0]][corner3[1]]){
					synchronized(land.segSunArray[corner4[0]][corner4[1]]) {

						for(int x = xpos-size; x<=xpos+size; x++){
			 				for(int y = ypos-size; y<=ypos+size; y++){
								if(0<=x && x<xlimit && 0<=y && y<ylimit){
									sun+=land.getShade(x,y);	//does a READ from SunData Land map. must NOT be allowed to do this if another tree with some of the same blocks are being processed.
									numBlockThatCount++;
								}
							}
						}
						float averageSun = sun/numBlockThatCount;
						land.shadow(this);//update sunmap by passing it the tree
						float tempExt = getExt()+(averageSun/growfactor); //does a READ of this tree (should be fine if re-entrant lock)
						setExt(tempExt); //sets a new extent for this tree. Performs a WRITE. This is fine becuase no other threads will use this particular trees extent.
					}
				}
			}
		}
	}
}
