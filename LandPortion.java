/*
 * @Author Monde  Mcongwana
*/


public class LandPortion{

			float[][] lp;

			public LandPortion(int x, int y){
				lp =  new float[x][y];
			}

			public float getSunValue(int x, int y){
				return lp[x][y];
			}
			public void setSunValue(int x,int y, float val){
				lp[x][y]=val;
			}


}
