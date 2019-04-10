package radar.item;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import radar.Config;
import radar.ring.Rings;

public class Item {
	
	private String name;
	private String description;
	private boolean placeholder;
	private int size;
	private String assignee;

	// any...
	private Map<Cluster, String> values; // = new HashMap<>();
	private int percentage;
	private int ring;
	private int subRing = -1;
	private Rectangle textBounds;
	private Point2D center = new Point2D.Double();
	private int radarSizeIncrease;
	private int winkelNr = -1;
	private double winkel;
	private double length;
	private double tmpRampUp = 1.0;
	private double tmpRampUpSpeed = 1.0;
	private int novaAnimateIdx = -1;
	private int arcDegree = 0;
	private Point2D novaAnimationPoint = new Point2D.Double();
	private Point2D bigBangPoint = new Point2D.Double();
	private int bigBangAlpha = 255;
	private double bigBangZ;
	private double closerW = 0;
	private Point2D closerPoint = new Point2D.Double();
	private String clusterValue = "default";
	private Point2D targetLocationDiff = new Point2D.Double();
	private double targetLocationReached = 0;
	private double winkelCircle;
	private double lengthCircle;
	private double ydiff = 0;
	private double ydiffdiff = 0;
	
	private ClusterInfo clusterInfo;
	
	public void copyInto(Item item, ClusterInfo ci, boolean deep) {
		item.name = name;
		item.description = description;
		item.placeholder = placeholder;
		item.size = size;
		item.assignee = assignee;
		/*
		if (item.values != null) {
			item.values.clear();
		}
		if (values != null) {
			if (item.values == null) {
				item.values = new HashMap<>();
			}
			item.values.putAll(values);
		}*/
		item.percentage = percentage;
		if (deep) {
			item.ring = ring;
			item.subRing = subRing;
			item.winkelNr = winkelNr;
			item.winkel = winkel;
			item.length = length;
		}
		item.textBounds = textBounds;
		item.center.setLocation(center);
		item.radarSizeIncrease = radarSizeIncrease;
		item.tmpRampUp = tmpRampUp;
		item.tmpRampUpSpeed = tmpRampUpSpeed;
		item.novaAnimateIdx = novaAnimateIdx;
		item.arcDegree =arcDegree;
		item.novaAnimationPoint.setLocation(novaAnimationPoint);
		item.bigBangPoint.setLocation(bigBangPoint);
		item.bigBangAlpha = bigBangAlpha;
		item.bigBangZ = bigBangZ;
		item.closerW = closerW;
		item.closerPoint.setLocation(closerPoint);
		item.clusterValue = clusterValue;
		item.targetLocationDiff.setLocation(targetLocationDiff);
		item.targetLocationReached = targetLocationReached;
		item.winkelCircle = winkelCircle;
		item.lengthCircle = lengthCircle;
		item.ydiff = ydiff;
		item.ydiffdiff = ydiffdiff;
		
		item.clusterInfo = ci;
		//item.clusterInfo.add(item);
	}
	
	protected Item (Item item) {
		this (item.ring, item.name, item.description, item.size, item.percentage, item.values, item.assignee);
		this.arcDegree = item.arcDegree;
		this.center.setLocation(item.center);
		this.length = item.length;
		this.lengthCircle = item.lengthCircle;
		this.winkel = item.winkel;
		this.winkelCircle = item.winkelCircle;
		this.winkelNr = item.winkelNr;
	}
	
	protected Item (int ring, String text, String description, int size, int percentage, Map<Cluster, String> values, String assignee) {
		this.ring = ring;
		this.name = text;
		this.description = description;
		this.size = size;
		this.percentage = percentage;
		this.values = values;
		this.assignee = assignee;
	}
	
	protected Item (int ring) {
		this.ring = ring;
		placeholder = true;
		textBounds = new Rectangle(0, 0, 0, 0);
	}
	
	public void stepToTargetLocation(double limit, double speed) {
		if (targetLocationReached < limit) {
			targetLocationReached -= (limit - speed * limit);
		} else {
			targetLocationReached *= speed;
		}
		if (targetLocationReached < 0.01) {
			targetLocationReached = 0;
		}
		/*
		if (clusterInfo != null) {
			clusterInfo.polygonInvalidate();
		}
		*/
	}

	public void stepToTargetLocation() {
		stepToTargetLocation(0.3, Config.stepToTargetLocationSpeed);
	}
	
	public double getYdiff() {
		return ydiff;
	}
	
	public void setYdiffdiff(double ydiffdiff) {
		this.ydiffdiff = ydiffdiff;
	}
	
	public void applyYdiff(double max, double min) {
		ydiff += ydiffdiff;
		if (getArcCenter().getY() > max) {
			ydiff = 0;
			ydiff = - getCenter().getY() + min;
		}
	}
	
	public void resetYdiff() {
		if (ydiff != 0) {
			this.targetLocationReached = 1.0;
			this.targetLocationDiff = new Point2D.Double(0, ydiff);
			ydiff = 0;
			ydiffdiff = 0;
		}
	}
	
	public double getTargetLocationReached() {
		return targetLocationReached;
	}
	
	public int getSubRing() {
		return subRing;
	}
	
	public int getBigBangAlpha() {
		return bigBangAlpha;
	}
	
	public void resetTmpRampUp() {
		resetTmpRampUp(1.0);
	}

	public void resetTmpRampUp(double value) {
		this.tmpRampUp = value;
	}
	
	public boolean applyTmpRampUp(boolean forward) {
		boolean change = false;
		if (! forward) {
			if (tmpRampUp > 0) {
				change = true;
				tmpRampUp = Math.max(0, tmpRampUp - tmpRampUpSpeed);
			} 
		} else {
			if (tmpRampUp < 1) {
				change = true;
				tmpRampUp = Math.min(1, tmpRampUp + tmpRampUpSpeed);
			}
		}
		return change;
	}
	
	public void setTmpRampUpSpeed(double speed) {
		tmpRampUpSpeed = speed;
	}
	
	public int getArcDegree() {
		return arcDegree;
	}
	
	public void setArcDegree(int arcDegree) {
		this.arcDegree = arcDegree;
	}
	
	public double getWinkel() {
		return winkel;
	}
	
	public double getWinkelCircle() {
		return winkelCircle;
	}
	
	public int getWinkelNr() {
		return winkelNr;
	}
	
	public double getLength() {
		return length;
	}
	
	public double getLengthCircle() {
		return lengthCircle;
	}
	
	public void setWinkelAndLengthAndSubringAndCenterX(double winkel, double winkelCircle, double length, double lengthCircle, int subring, Point2D center, int winkelNr) {
		setWinkelAndLengthAndSubringAndCenterX(winkel, winkelCircle, length, lengthCircle, subring, center, winkelNr, 0.0);
	}
	
	public void setWinkelAndLengthAndSubringAndCenterX(
			double winkel, double winkelCircle, 
			double length, double lengthCircle, 
			int subring, Point2D center, int winkelNr, double targetLocationReached) {
		while (winkel < 0) {
			winkel = (winkel + Math.PI * 2) % (Math.PI * 2);
		}
		winkel = winkel % (Math.PI * 2);
		while (winkelCircle < 0) {
			winkelCircle = (winkelCircle + Math.PI * 2) % (Math.PI * 2);
		}
		winkelCircle = winkelCircle % (Math.PI * 2);
		if (clusterInfo != null) {
			clusterInfo.removeCenter(this.center);
		}
		this.winkel = winkel;
		this.length = length;
		this.subRing = subring;
		if (targetLocationReached == 0) {
			this.center.setLocation(
					this.center.getX() + this.targetLocationReached * targetLocationDiff.getX(),
					this.center.getY() + this.targetLocationReached * targetLocationDiff.getY());
		}
		this.targetLocationDiff.setLocation(
				this.center.getX() + targetLocationReached * targetLocationDiff.getX() - center.getX(),
				this.center.getY() + targetLocationReached * targetLocationDiff.getY() - center.getY());
		this.targetLocationReached = 1.0;
		//this.targetLocationReached = targetLocationReached;
		//this.targetLocationForward = false;
		this.center.setLocation(center);
		if (clusterInfo != null) {
			clusterInfo.addCenter(this.center);
		}
		this.winkelNr = winkelNr;
		this.winkelCircle = winkelCircle;
		this.lengthCircle = lengthCircle;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isPlaceholder() {
		return placeholder;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getRing() {
		return ring;
	}
	
	public int getPercentage() {
		return percentage;
	}
	
	public void initGraphics(Graphics2D g) {
		if (! placeholder) {
			textBounds = g.getFontMetrics().getStringBounds(name, g).getBounds();
		}
	}
	
	public boolean containsForTooltip(Point2D p, boolean showText, boolean showItems) {
		if (showText) {
			Point2D tc = getTextCenter();
			if (p.getX() - (tc.getX() + textBounds.getX() - textBounds.getWidth() / 2) > 0) {
				if (p.getY() - (tc.getY() + textBounds.getY() - textBounds.getHeight() / 2) > 0) {
					if (tc.getX() + textBounds.getX() + textBounds.getWidth() / 2 - p.getX() > 0) {
						if (tc.getY() + textBounds.getY() + textBounds.getHeight() / 2 - p.getY() > 0) {
							return true;
						}
					}
				}
			}
		}
		if (showItems) {
			Point2D ac = getArcCenter();
			int size2 = (int) Math.round(getArcSize()/2 + 1);
			if (p.getX() - (ac.getX() - size2) > 0) {
				if (p.getY() - (ac.getY() - size2) > 0) {
					if (ac.getX() + size2 - p.getX() > 0) {
						if (ac.getY() + size2 - p.getY() > 0) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public Rectangle2D getTextBounds() {
		return textBounds;
	}
	
	public Point2D getArcCenter() {
		return new Point2D.Double(
				center.getX() + novaAnimationPoint.getX() + bigBangPoint.getX() + 
				closerPoint.getX() + targetLocationDiff.getX() * targetLocationReached,
				center.getY() + novaAnimationPoint.getY() + bigBangPoint.getY() + 
				closerPoint.getY() + targetLocationDiff.getY() * targetLocationReached +
				ydiff
		);
	}

	public void animateCloserStop() {
		closerPoint.setLocation(0, 0);
		closerW = 0;
	}	
	
	public void animateCloserInit(Point2D itemsCenter, double w) {
		closerW = 
				- Util.getWDiff(w, winkel) / 2 
				+ Util.getWDiff(2 * Math.PI - w, 2 * Math.PI - winkel) / 2
				; 
	}

	public void animateCloser(Point2D itemsCenter, double percentage) {
    	Point2D center = new Point2D.Double();
		center.setLocation(
				itemsCenter.getX() + Math.cos(winkel - closerW) * length,
				itemsCenter.getY() + Math.sin(winkel - closerW) * length
		);
		closerPoint.setLocation(
				(percentage) * (center.getX() - this.center.getX()), 
				(percentage) * (center.getY() - this.center.getY())
		);
	}
	
	public void animateBigBang(Point2D itemsCenter, double percentage, double versatz) {
    	Point2D goal2D = new Point2D.Double();
    	double radius = length * Rings.getInstance().getBigBangMultis()[ring][subRing];
		goal2D.setLocation(
				itemsCenter.getX() + Math.cos(winkel + versatz) * radius,
				itemsCenter.getY() + Math.sin(winkel + versatz) * radius
		);
		Point2D p = Util.moveBigBang2(radius, percentage, percentage, goal2D, itemsCenter, this.center, 1.0, bigBangPoint, true);
		bigBangAlpha = (int) Math.round(255 - (255 - 255 * p.getX()) * (1 - percentage));
		bigBangZ = p.getY();
	}
	
	public double getBigBangZ() {
		return bigBangZ;
	}
	

	public void resetBigBangPoint() {
		bigBangPoint.setLocation(0, 0);
		bigBangAlpha = 255;
		bigBangZ = 0;
	}

	public void resetNovaAnimationPoint() {
		novaAnimationPoint.setLocation(0, 0);
	}

	public void addToNovaAnimationPoint(double x, double y) {
		novaAnimationPoint.setLocation(novaAnimationPoint.getX() + x, novaAnimationPoint.getY() + y);
	}
	
	public Point2D getNovaAnimationPoint() {
		return novaAnimationPoint;
	}
	
	public Point2D getCenter() {
		return center;
	}
	
	public int getArcDegrees() {
		return (int) Math.round(360 * percentage / 100.0);
	}
	
	public double getArcSize() {
		return (Config.itemSizes[size] + radarSizeIncrease * 2) * tmpRampUp;
	}
	
	public int getArcStart() {
		return arcDegree;
	}
	
	public Point2D getTextCenter() {
		Point2D p = new Point2D.Double();
		Point2D pi = getArcCenter();
		if (textBounds == null) {
			textBounds = new Rectangle(0, 0, 0, 0);
		}
		p.setLocation(
				pi.getX(),
				pi.getY() - textBounds.getY() - textBounds.getHeight()
		);
		return p;
	}
	
	public int getNovaAnimateIdx() {
		return novaAnimateIdx;
	}
	
	public void setNovaAnimateIdx(int novaAnimateIdx) {
		this.novaAnimateIdx = novaAnimateIdx;
	}
	
	public void calculatePositions2(boolean showRadar, double radarLine, boolean showItems, double novaDimm, boolean showSquare, double squareMaxWinkelDiff) {
		double winkel = this.winkel - closerW;
		double limit;
		if (! showSquare) {
			limit = Math.PI / 2;
		} else {
			limit = squareMaxWinkelDiff / 4;
		}
    	if (showRadar && showItems) {
    		double corr2 = 0;
			double nrl = radarLine % (Math.PI * 2);
			double wdiff;
			if (nrl > winkel) {
				wdiff = nrl - winkel;
			} else {
				wdiff = nrl + (Math.PI * 2 - winkel);
			}
			if (wdiff > 0 && wdiff < limit) {
        		corr2 = (Math.pow(8, (limit - wdiff) * (Math.PI / 2 / limit)) * novaDimm);
			}
    		radarSizeIncrease = (int) Math.round(corr2);
    	} else {
			if (radarSizeIncrease > 0) {
				radarSizeIncrease = ((int) Math.round(Math.max(0, radarSizeIncrease - 1)));
			}
	
		}
	}
	
	public void fastForwardToTarget() {
		targetLocationDiff.setLocation(0, 0);
		targetLocationReached = 0;
	}
	
	public void setClusterBy(Cluster cluster, ClusterInfoMap cim) {
		if (clusterInfo != null) {
			clusterInfo.remove(this);
		}
		cim.remove(this);
		if (values != null) {
			clusterValue = values.get(cluster);
		}
   		if (clusterValue == null) {
    		clusterValue = "default";
    	}
   		clusterInfo = cim.add(this);
	}
	
	public String getClusterValue() {
		return clusterValue;
	}
	
	@Override
	public String toString() {
		return "Item: {" + name + ", " + values + ", " + ring + "}";
	}
	
	public ClusterInfo getClusterInfo() {
		return clusterInfo;
	}

	public String getAssignee() {
		return assignee;
	}
}
