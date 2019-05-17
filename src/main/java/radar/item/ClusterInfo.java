package radar.item;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import radar.Config;
import radar.ring.Rings;

public class ClusterInfo {
	
	private String name;
	
	private double count = 0;
	
	private Point2D.Double center = new Point2D.Double(0, 0);
	private Point2D.Double centerAverage = new Point2D.Double(0, 0);
	private List<Item> items = new ArrayList<>();
	private double addWinkelTmp;
	
	private List<Double> addWinkelTmps = new ArrayList<>();

    private double optimizationReduction2 = 1.0;
    
    private GeneralPath polygon = null;
    //private boolean polygonValid = false;
    
    public void copyInto(ClusterInfo ci) {
   		polygon2();
    	ci.name = name;
    	ci.count = count;
    	ci.center.setLocation(center);
    	ci.centerAverage.setLocation(centerAverage);
    	ci.items.clear();
    	ci.addWinkelTmp = addWinkelTmp;
    	ci.addWinkelTmps.clear();
    	//ci.addWinkelTmps.addAll(addWinkelTmps);
    	//ci.optimizationReduction2 = optimizationReduction2;
    	ci.polygon = polygon;
    	//ci.polygonValid = polygonValid;
    }
     
    /*
    public synchronized void polygonInvalidate() {
    	polygonValid = false;
    }
    */
    public void clearAddWinkelTmp() {
    	addWinkelTmps.clear();
    	addWinkelTmp = 0;
    	optimizationReduction2 = 1.0;
    	//polygonInvalidate();
    }
    
	public void setAddWinkelTmp(double addWinkelTmp, Point2D cp) {
		//optimizationReduction2 = Math.min(optimizationReduction2 + Config.optimizationReductionSpeed2, 10);
		double x = 0;
		addWinkelTmps.add(addWinkelTmp);
		if (addWinkelTmps.size() > 3) {
			addWinkelTmps.remove(0);
		}
		for (Double d : addWinkelTmps) {
			x += d;
		}
		if (x != addWinkelTmps.get(0) * addWinkelTmps.size()) {
			x = 0;
		}
		double w;
    	Point2D c = new Point2D.Double();
    	c.setLocation(centerAverage);
    	if (cp != null) {
    		w = Util.getAngle(cp, c);
    	} else {
    		w = 0;
    	}
		this.addWinkelTmp = w + x / addWinkelTmps.size() / optimizationReduction2;
    	//polygonInvalidate();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected void add(Item item) {
		if (! items.contains(item)) {
			count++;
			items.add(item);
			addCenter(item.getCenter());
			calculate();
		} else {
			throw new IllegalStateException("item:" + item.getName());
		}
    	//polygonInvalidate();
	}
	
	protected void addAfterCopy(Item item) {
		items.add(item);
	}

	protected void remove(Item item) {
		if (items.remove(item)) {
			count--;
			center.setLocation(
					center.getX() - item.getCenter().getX(), 
					center.getY() - item.getCenter().getY()
			);
			calculate();
		}
    	//polygonInvalidate();
	}
	
	public void removeCenter(Point2D c) {
		center.setLocation(
				center.getX() - c.getX(), 
				center.getY() - c.getY()
		);
		calculate();
    	//polygonInvalidate();
	}

	public void addCenter(Point2D c) {
		center.setLocation(
				center.getX() + c.getX(), 
				center.getY() + c.getY()
		);
		calculate();
    	//polygonInvalidate();
	}

	private void calculate() {
		centerAverage.setLocation(center.getX() / count, center.getY() / count);
	}
	
	public Point2D.Double getCenterAverage() {
		return centerAverage;
	}
	
	public double getCount() {
		return count;
	}
	
	public double getAddWinkelTmp() {
		return addWinkelTmp;
	}
	
	public boolean isAdjustedWinkel() {
		return addWinkelTmp != 0;
	}
	
	public double getWinkelDistanceX1(Point2D center, Item x1, Item x2, double max, boolean showSquare) {
		double w;
		double d = 0;
		
    	Point2D c = new Point2D.Double();
    	c.setLocation(centerAverage);
    	boolean sw = x1 != x2;
    	
    	if (addWinkelTmp != 0) {
    		w = addWinkelTmp;
    	} else {
	    	if (sw) {
		    	c.setLocation(c.getX() - x1.getCenter().getX() / count, c.getY() - x1.getCenter().getY() / count);
		    	c.setLocation(c.getX() + x2.getCenter().getX() / count, c.getY() + x2.getCenter().getY() / count);
	    	}
			w = Util.getAngle(center, c);
    	}    	
		
		/*d = Math.abs (
				- Util.getWDiff(w, addWinkelTmp)  
				+ Util.getWDiff(2 * Math.PI - w, 2 * Math.PI - addWinkelTmp)
		) * items.size() ;*/
		
		for (Item item : items) {
			if (sw) {
				if (item == x1) {
					item = x2;
				} /*else
				if (item == x2) {
					item = x1;
				}*/
			}
			double closerW1 = Math.abs (
					- Util.getWDiff(w, item.getWinkel())  
					+ Util.getWDiff(2 * Math.PI - w, 2 * Math.PI - item.getWinkel()) 
					)
					; 
			if (closerW1 > Math.PI) {
				closerW1 = 2 * Math.PI - closerW1;
			}
			if (closerW1 > Math.PI) {
				System.out.println("ok");
			}
			double ad;
			if (showSquare) {
				ad = 0.01;
			} else {
				ad = 0.5;
			}
			d += (ad + closerW1) * (ad + closerW1) ;
			if (d > max) {
				return Double.MAX_VALUE;
			}
		}
		return d;
	}
	
	public List<Item> getItems() {
		return items;
	}	

	protected synchronized void polygon2() {
		//if (! polygonValid) {
			polygon = polygon2Internal();
		//	polygonValid = true;
		//}
	}
	
    private GeneralPath polygon2Internal() {
    	
    	// create polygon
    	List<Item> ibc = items;
    	if (ibc == null || ibc.size() == 0) {
    		return null;
    	}
    	//System.out.println("name " + name);
    	List<Item> pol = polygon(ibc);
    	
    	List<Item> l = new ArrayList<>();
		Point2D p = new Point2D.Double();

    	for (Item item : pol) {
    		if (!Config.roundBack || p.distance(center) + Config.itemSizes[item.getSize()] / 2 <= Rings.getInstance().getSize() / 2) {
    			l.add(item);
    			p.setLocation(p.getX() + item.getArcCenter().getX(), p.getY() + item.getArcCenter().getY());
    		}
    	}
    	
		GeneralPath polygon;
    	    	
    	if (l.size() > 1) {
    		p.setLocation(p.getX() / l.size(), p.getY() / l.size());
    		List<Point2D> points = new ArrayList<>();
    		for (int i = 0; i < l.size(); i++) {
    			Point2D p1 = l.get((i - 1 + l.size()) % l.size()).getArcCenter();
    			Point2D p2 = l.get((i + 1) % l.size()).getArcCenter();
    			Point2D ac = l.get(i).getArcCenter();
    			double w1 = Util.getAngle(p1, ac);
    			double w2 = Util.getAngle(p2, ac);
    			double wx = Math.cos(w1) + Math.cos(w2);
    			double wy = Math.sin(w1) + Math.sin(w2);
    			double lx = Math.sqrt(Math.pow(wx, 2) + Math.pow(wy, 2));
    			double r = Config.edgeDistance/lx;
    			points.add(new Point2D.Double(ac.getX() + r * wx, ac.getY() + r * wy));
    		}
    		List<Point2D> points2 = new ArrayList<>();
    		for (int i = 1; i < points.size() + 1; i++) {
    			int idx = i%points.size();
    			int idx2 = (i + 1)%points.size();
    			double dx = points.get(idx).getX() - points.get(i - 1).getX();
    			double dy = points.get(idx).getY() - points.get(i - 1).getY();
    			double dist = points.get(idx).distance(points.get(i - 1));
    			points2.add(new Point2D.Double(
    					points.get(idx).getX() - Math.min(25, dist/2) * dx/dist,
    					points.get(idx).getY() - Math.min(25, dist/2) * dy/dist
    			));
    			points2.add(points.get(idx));
    			dx = points.get(idx).getX() - points.get(idx2).getX();
    			dy = points.get(idx).getY() - points.get(idx2).getY();
    			dist = points.get(idx).distance(points.get(idx2));
    			points2.add(new Point2D.Double(
    					points.get(idx).getX() - Math.min(25, dist/2)  * dx/dist,
    					points.get(idx).getY() - Math.min(25, dist/2)  * dy/dist
    			));
    		}
    		
	    	polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, l.size());  
	    	polygon.moveTo(points2.get(0).getX(), points2.get(0).getY());
	    	for (int i = 2; i < points2.size() + 2; i+=3) {
	    		int idx2 = (i + 1) % points2.size();
	    		int idx = i % points2.size();
	    		
	    		polygon.curveTo(
	    				points2.get(i - 1).getX(), points2.get(i - 1).getY(), 
	    				points2.get(i - 1).getX(), points2.get(i - 1).getY(), 
	    				points2.get(idx).getX(), points2.get(idx).getY()
	    		);
	    		
    			polygon.lineTo(points2.get(idx2).getX(), points2.get(idx2).getY());
	    	}
    	} else {
			int x = (int) Math.round(ibc.get(0).getCenter().getX());
			int y = (int) Math.round(ibc.get(0).getCenter().getY());
	    	polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, l.size());  
			polygon.moveTo(x, y - 20);
		}
    	
    	return polygon;
 
    }

    private List<Item> polygon(List<Item> l) {
    	
    	List<Item> pol = new ArrayList<>();
    	//synchronized (Items.getInstance()) 
    	{
	    	while (l.size() < 3) {
	    		l = new ArrayList<>(l);
	    		Item i = new Item(0, "", "", 0, 0, null, null);
	    		Point2D p = new Point2D.Double(
						(l.get(0).getArcCenter().getX() + l.get(l.size() - 1).getArcCenter().getX()) / 2,
						(l.get(0).getArcCenter().getY() + l.get(l.size() - 1).getArcCenter().getY()) / 2);
	    		//while (p.distance(l.get(0)))
	    		for (int j = 0; j < 2; j++)
	    		i.setWinkelAndLengthAndSubringAndCenterX(
	    				0, 0, (l.get(0).getLength() + l.get(l.size() - 1).getLength()) / 2, 
	    				(l.get(0).getLengthCircle() + l.get(l.size() - 1).getLengthCircle()) / 2, 0, 
	    				p,
	    		0, 0.1);
	    		for (int j = 0; j < 10; j++)
	    			i.stepToTargetLocation();
	    		l.add(1, i);
	    		
	    	}
	    	
	    	if (l.size() >= 3) {
		    	Item first = l.get(0);
		    	for (Item item : l) {
		    		if (item.getArcCenter().getY() > first.getArcCenter().getY()) {
		    			first = item;
		    		} else
		    		if (item.getArcCenter().getY() == first.getArcCenter().getY()) {
		    			if (item.getArcCenter().getX() > first.getArcCenter().getX()) {
		    				first = item;
		    			}
		    		}
		    	}
		    	
		    	pol.add(first);
		    	
		    	boolean cont;
		    	Item next = null;
		    	do {
		    		cont = false;
		    		double wd = Math.PI * 2;
		    		Item last = pol.get(pol.size() - 1);
	    			Item x;
	    			if (pol.size() > 1) {
	    				x = pol.get(pol.size() - 2);
	    			} else {
	    				x = new Item(1);
		    			x.getCenter().setLocation(-1000, -1000);
	    			}
		        	for (Item item : l) {
		        		if (item != last) {
		        			double w1 = Util.getAngle(x, last);
		        			double w2 = Util.getAngle(last, item);
		        			
		        			double diff = w2 - w1;
		        			
		        			while (diff < 0) {
		        				diff += Math.PI * 2;
		        			}
		        			
		        			if (diff <= wd) {
		        				if (
		        						diff < wd || 
		        						next != null && last != null &&
		        						last.getArcCenter().distance(item.getArcCenter()) > last.getArcCenter().distance(next.getArcCenter())) {
			        				next = item;
			        				wd = diff;
		        				}
		        			}
		        		}
		        	}
		        	//System.out.println("pol:" + pol);
		        	//System.out.println("wd=" + wd);
		        	if (! pol.contains(next)) {
		        		pol.add(next);
		        		cont = true;
		        	} else {
		        		//System.out.println("already:" + next.text);
		        	}
		    		next = null;
		    	} while (cont);
	    	} else {
	    		pol.addAll(l);
	    	}
    	}
    	
    	return pol;
    }
    
    public GeneralPath getPolygon() {
    	return polygon;
    }

    /*
    public synchronized boolean isPolygonValid() {
		return polygonValid;
	}
	*/

}
