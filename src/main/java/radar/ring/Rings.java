package radar.ring;

import java.awt.geom.Point2D;

import radar.item.Util;

public class Rings {
	
    private int[] sizesOrg = {150, 650, 750, 850, 950};
    private int[] offsetsOrg = {850, 350, 250, 150, 50};
	private int[] sizes = {150, 650, 750, 850, 950};
	private int[] offsets = {850, 350, 250, 150, 50};
    private double[][] bigBangMultis = new double[5][1];
    private Point2D[] bigBangPoint1 = new Point2D.Double[5];
    private Point2D[] bigBangPoint2 = new Point2D.Double[5];
    private int[] bigBangAlpha = new int[5];
    private double[] bigBangZ = new double[5];
    
    private static final Rings instance = new Rings();
    
    public static Rings getInstance() {
    	return instance;
    }
	
	private Rings() {
	}

    public void calculateBasics(double width, double height) {
    	double rad = (int) Math.round(Math.min(width, height));
    	//if (! square) {
	    	for (int i = 0; i < sizesOrg.length; i++) {
				sizes[i] = (int) Math.round(rad * (double) sizesOrg[i]/(offsetsOrg[i] + sizesOrg[i]));
	        	offsets[i] = (int) Math.round((double)sizes[i]/(double)sizesOrg[i]*offsetsOrg[i]);
	        	//System.out.println("size[" + i + "]=" + sizes[i] + ", width=" + width + ", height=" + height);
	    		//sizes[i] = (int) Math.round((sizes[i]) * f) - Config.yOffset / 2;
	    		//offsets[i] = (int) Math.round(offsets[i] * f);
	    	}
    	/*} else {
	    	for (int i = 0; i < 5; i++) {
	        	sizes[i] = (int) Math.round(Items.getInstance().getSquareRadius(rad - offsets[i]));
	    	}
    	}*/
    }

    public int[] getSizes() {
		return sizes;
	}
    
    public int[] getOffsets() {
		return offsets;
	}
   
    public int getSize() {
    	return sizes[sizes.length - 1];
    }
    
    public double[][] getBigBangMultis() {
		return bigBangMultis;
	}
    
    public void resetBigBangMultis() {
		for (int i = 0; i < Rings.getInstance().getBigBangMultis().length; i++) {
			for (int o = 0; o < Rings.getInstance().getBigBangMultis()[i].length; o++) {
				Rings.getInstance().getBigBangMultis()[i][o] = 1;
			}
		}
    }
    
    public void bigBangAnimate(double percentage, Point2D center) {
    	for (int i = 0; i < sizes.length; i++) {
	    	double radius = sizes[i] / 2 * bigBangMultis[i][0];
	    	Point2D goal2D1 = new Point2D.Double(center.getX(), center.getY() - radius);
	    	Point2D goal2D2 = new Point2D.Double(center.getX(), center.getY() + radius);
	
			bigBangPoint1[i] = new Point2D.Double();
			bigBangPoint2[i] = new Point2D.Double();
			Point2D p1 = Util.moveBigBang2(radius, percentage, percentage, goal2D1, center, goal2D1, 1, bigBangPoint1[i], false);
			Util.moveBigBang2(radius, percentage, percentage, goal2D2, center, goal2D2, 0.65, bigBangPoint2[i], false);
			bigBangAlpha[i] = (int) Math.round(255 - 255 * (1 - p1.getX()) * (1 - percentage));
			bigBangZ[i] = p1.getY();
    	}
    }
    
    public int[] getBigBangAlpha() {
		return bigBangAlpha;
	}
    
    public Point2D[] getBigBangPoint1() {
		return bigBangPoint1;
	}
    
    public Point2D[] getBigBangPoint2() {
		return bigBangPoint2;
	}

    public double[] getBigBangZ() {
		return bigBangZ;
	}
    
}
