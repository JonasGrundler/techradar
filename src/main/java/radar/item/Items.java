package radar.item;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import radar.Config;
import radar.net.RoundGrid;
import radar.ring.Rings;

public class Items {
	
    private int itemCount = 0;
    private List<Item> items = new ArrayList<>();
	private List<List<Item>> itemsByRing = new ArrayList<>();
	private Map<String, List<List<Item>>> itemsByClusterAndRing = new HashMap<>();
	private List<List<SortedMap<Integer, Item>>> itemsByRingAndSubringSortedByWinkel = new ArrayList<>();
	private List<SortedMap<Double, Item>> itemsByRingSortedByWinkel = new ArrayList<>();
	
    private boolean showText = false;
    private boolean showItems = false;
    private boolean followMouse = true;
    private boolean showPolygons = false;
    private boolean optimize = false;
    private boolean showNovaPointsAnimation = false;
    private boolean showNovaPointsAnimation2 = false;
    private boolean showItemsStateBeforeAnimation;
    private boolean showBigBangAnimation = false;
    private boolean showBigBangAnimation2 = false;
    private boolean showCloserAnimation = false;
    private boolean showCloserAnimation2 = false;
    private Cluster clusterBy = null;
    private int optSpeed = 0;
    
    private boolean showResetItemLocationAnimation = false;
    private boolean showClusterAnimation = false;
    private boolean changeAnimationPhase2 = false;
    private Cluster clusterByChange;
    private double oldNovaDimm;
    private double oldPolygonDimm;
    private boolean oldShowItems;
    
    private double closerPercentage;
    
    private double bigBangVersatz;
    private double bigBangVersatzTotal;
    
    private double globalVersatz = 0;
    
    private Point2D mouseLocation;

    private double novaTextDimm = 0.0;
    private double novaPolygonDimm = 0.0;
    
    private double novaDimm = 1.0;
    private double novaAnimateVersatz = 0;
    
    private Random random = new Random();
    
    private Point2D center = new Point2D.Double(500, 500);
    private Point2D centerSquare = new Point2D.Double();
    
    private int[] ringItemLines = {2, 4, 1, 1, 1};
    
    private static Items instance = new Items();
    private static Items tmpInstance = new Items();
    
    private int lineSize;
    
    private boolean showSquare = false;
    private boolean animateToSquareEnded = false;
    private boolean showSquareIsAnimating = false;
    private final double squareMaxAdditionalRadius = 5000000;
    private int squareAdditionalRadiusIdx = 0;
    private double squareRingRadiusBase = 0;
    private double squareRingRadiusBaseOffset = 0;
    private double squareMaxWinkelDiff = 0;
    double squareStretchX = 1.0;
    double squareStretchY = 1.0;
    
    private int maximumItemsPerSubring = -1;
    private double minimumWinkelDiffAcrossRings = 0;
    
    private double optimizationReduction = 1.0;
	
	private boolean showStarWarsAnimation = false;
	
	private ClusterInfoMap cim = new ClusterInfoMap();
	
    private Map<String, Long> lastClusterMove = new HashMap<>();
    private Map<Item, Long> lastItemMove = new HashMap<>();

    private List<Double> squareAdditionRadiusSteps = new ArrayList<>();	
    
    private boolean roundBack = true;
    
    public synchronized Items getCopy() {
    	Items items = new Items();
    	
    	items.itemCount = this.itemCount;
    	    	
    	items.showText = showText;
    	items.showItems = showItems;
    	items.followMouse = followMouse;
    	items.showPolygons = showPolygons;
    	items.optimize = optimize;
    	items.showNovaPointsAnimation = showNovaPointsAnimation;
    	items.showNovaPointsAnimation2 = showNovaPointsAnimation2;
    	items.showItemsStateBeforeAnimation = showItemsStateBeforeAnimation;
    	items.showBigBangAnimation = showBigBangAnimation;
    	items.showBigBangAnimation2 = showBigBangAnimation2;
    	items.showCloserAnimation = showCloserAnimation;
    	items.showCloserAnimation2 = showCloserAnimation2;
    	items.clusterBy = clusterBy;
    	items.optSpeed = optSpeed;
        
    	items.showResetItemLocationAnimation = showResetItemLocationAnimation;
    	items.showClusterAnimation = showClusterAnimation;
    	items.changeAnimationPhase2 = changeAnimationPhase2;
    	items.clusterByChange = clusterByChange;
    	items.oldNovaDimm = oldNovaDimm;
    	items.oldPolygonDimm = oldPolygonDimm;
    	items.oldShowItems = oldShowItems;
        
    	items.closerPercentage = closerPercentage;
        
    	items.bigBangVersatz =  bigBangVersatz;
    	items.bigBangVersatzTotal = bigBangVersatzTotal;
        
    	items.globalVersatz = globalVersatz;
        
    	items.mouseLocation = mouseLocation;

    	items.novaTextDimm = novaTextDimm;
    	items.novaPolygonDimm = novaPolygonDimm;
        
    	items.novaDimm = novaDimm;
    	items.novaAnimateVersatz = novaAnimateVersatz;
        
    	items.center.setLocation(center);
    	items.centerSquare.setLocation(centerSquare);
        
    	System.arraycopy(ringItemLines, 0, items.ringItemLines, 0, ringItemLines.length);
        
    	items.lineSize = lineSize;
        
    	items.showSquare = showSquare;
    	items.animateToSquareEnded = animateToSquareEnded;
    	items.showSquareIsAnimating = showSquareIsAnimating;
    	items.squareAdditionalRadiusIdx = squareAdditionalRadiusIdx;
    	items.squareRingRadiusBase = squareRingRadiusBase;
    	items.squareRingRadiusBaseOffset = squareRingRadiusBaseOffset;
    	items.squareMaxWinkelDiff = squareMaxWinkelDiff;
    	items.squareStretchX = squareStretchX;
    	items.squareStretchY = squareStretchY;
        
    	items.maximumItemsPerSubring = maximumItemsPerSubring;
    	items.minimumWinkelDiffAcrossRings = minimumWinkelDiffAcrossRings;
        
    	items.optimizationReduction = optimizationReduction;
    	
    	items.showStarWarsAnimation = showStarWarsAnimation;
    	
    	items.lastClusterMove.clear();
    	items.lastItemMove.clear();
    	
    	items.squareAdditionRadiusSteps = new ArrayList<>(squareAdditionRadiusSteps);
    	
    	items.roundBack = roundBack;

    	cim.copyInto(items.cim);
    	
    	items.itemsByRing.clear();
    	items.itemsByClusterAndRing.clear();
    	items.itemsByRingAndSubringSortedByWinkel.clear();
    	items.itemsByRingSortedByWinkel.clear();

    	while (this.items.size() > items.items.size()) {
    		items.items.add(new Item(0, "", 0, 0, null));
    	}
    	while (items.items.size() > this.items.size()) {
    		items.items.remove(items.items.size() - 1 );
    	}
    	while (items.itemsByRing.size() < itemsByRing.size()) {
   			items.itemsByRing.add(new ArrayList<>());
    	}
    	for (String cvs : itemsByClusterAndRing.keySet()) {
    		if (! items.itemsByClusterAndRing.containsKey(cvs)) {
    			items.itemsByClusterAndRing.put(cvs, new ArrayList<>());
    		}
    		while (items.itemsByClusterAndRing.get(cvs).size() < itemsByClusterAndRing.get(cvs).size()) {
    			items.itemsByClusterAndRing.get(cvs).add(new ArrayList<>());
    		}
    	}
    	while (items.itemsByRingSortedByWinkel.size() < itemsByRingSortedByWinkel.size()) {
    		items.itemsByRingSortedByWinkel.add(new TreeMap<>());
    	}
    	while (items.itemsByRingAndSubringSortedByWinkel.size() < itemsByRingAndSubringSortedByWinkel.size()) {
    		items.itemsByRingAndSubringSortedByWinkel.add(new ArrayList<>());
    	}
    	for (int i = 0; i < itemsByRingAndSubringSortedByWinkel.size(); i++) {
    		while (items.itemsByRingAndSubringSortedByWinkel.get(i).size() < itemsByRingAndSubringSortedByWinkel.get(i).size()) {
    			items.itemsByRingAndSubringSortedByWinkel.get(i).add(new TreeMap<>());
    		}
    	}
    	for (int i = 0; i < this.items.size(); i++) {
    		Item it = this.items.get(i);
    		Item itCopy = items.items.get(i);
    		it.copyInto(itCopy, items.cim.get(it.getClusterValue()));
    		//items.itemsByRing.get(it.getRing()).add(itCopy);
    		//items.itemsByClusterAndRing.get(it.getClusterValue()).get(it.getRing()).add(itCopy);
    		//items.itemsByRingSortedByWinkel.get(it.getRing()).put(it.getWinkel(), itCopy);
    		//items.itemsByRingAndSubringSortedByWinkel.get(it.getRing()).get(it.getSubRing()).put(it.getWinkelNr(), itCopy);
    		//items.getCim().get(it.getClusterValue()).addAfterCopy(itCopy);
    	}
    	items.itemsByRing.get(items.itemsByRing.size() - 1).add(itemsByRing.get(itemsByRing.size() - 1).get(0));
    	
    	return items;
    }
    
	
	public ClusterInfoMap getCim() {
		return cim;
	}
	
    public static Items getInstance() {
    	return instance;
    }
    
    public static void resetTmpInstance() {
    	tmpInstance = new Items();
    }
    public static Items getTmpInstance() {
    	return tmpInstance;
    }
    public static void switchInstance() {
    	synchronized (instance) {
    		synchronized (tmpInstance) {
		    	tmpInstance.showText = instance.showText;
		    	tmpInstance.showItems = instance.showItems;
		    	tmpInstance.followMouse = instance.followMouse;
		    	tmpInstance.showPolygons = instance.showPolygons;
		    	tmpInstance.optimize = instance.optimize;
		    	tmpInstance.showNovaPointsAnimation = instance.showNovaPointsAnimation;
		    	tmpInstance.showNovaPointsAnimation2 = instance.showNovaPointsAnimation2;
		    	tmpInstance.showItemsStateBeforeAnimation = instance.showItemsStateBeforeAnimation;
		    	tmpInstance.showBigBangAnimation = instance.showBigBangAnimation;
		    	tmpInstance.showBigBangAnimation2 = instance.showBigBangAnimation2;
		    	tmpInstance.showCloserAnimation = instance.showCloserAnimation;
		    	tmpInstance.showCloserAnimation2 = instance.showCloserAnimation2;
		    	tmpInstance.clusterBy = instance.clusterBy;
		    	tmpInstance.optSpeed = instance.optSpeed;
		        
		    	tmpInstance.showResetItemLocationAnimation = instance.showResetItemLocationAnimation;
		    	tmpInstance.showClusterAnimation = instance.showClusterAnimation;
		    	tmpInstance.changeAnimationPhase2 = instance.changeAnimationPhase2;
		    	tmpInstance.clusterByChange = instance.clusterByChange;
		    	tmpInstance.oldNovaDimm = instance.oldNovaDimm;
		    	tmpInstance.oldPolygonDimm = instance.oldPolygonDimm;
		    	tmpInstance.oldShowItems = instance.oldShowItems;
		        
		    	tmpInstance.closerPercentage = instance.closerPercentage;
		        
		    	tmpInstance.bigBangVersatz = instance.bigBangVersatz;
		    	tmpInstance.bigBangVersatzTotal = instance.bigBangVersatzTotal;
		        
		    	tmpInstance.globalVersatz = instance.globalVersatz;
		        
		        tmpInstance.novaTextDimm = instance.novaTextDimm;
		        tmpInstance.novaPolygonDimm = instance.novaPolygonDimm;
		        
		        tmpInstance.novaDimm = instance.novaDimm;
		        tmpInstance.novaAnimateVersatz = instance.novaAnimateVersatz;
		        
		        tmpInstance.optimizationReduction = 1.0;
		        tmpInstance.showSquare = instance.showSquare;
		        
		        tmpInstance.minimumWinkelDiffAcrossRings = instance.minimumWinkelDiffAcrossRings;
		        
		    	instance = tmpInstance;
		    	
		    	tmpInstance = null;
    		}
    	}
    }
    
    private Item itemCreated(Item item) {
		addItem(item);
		create(item.getRing());
		if (random.nextBoolean()) {
			create(item.getRing());
		}
		//create(item.getRing());
		/*if (itemsByRing.get(item.getRing()).size() % (5 - item.getRing()) == 0) {
			create(item.getRing());
			create(item.getRing());
		}*/
		return item;
    }

    public synchronized Item create (Item item) {
    	return itemCreated(new Item(item));
    }
	
	public synchronized Item create (int ring, String text, int size, int percentage, Map<Cluster, String> values) {
		Item item = new Item(ring, text, size, percentage, values);
		return itemCreated(item);
	}

	public synchronized Item create (int ring) {
		Item item = new Item (ring);
		addItem(item);
		return item;
	}
	
	public synchronized void addGlobalVersatz(double versatz) {
		this.globalVersatz = (globalVersatz + versatz) % (Math.PI * 2);
		for (Item item : items) {
			double nw = item.getWinkel() + versatz;
			Point2D nc = new Point2D.Double();
			nc.setLocation(
					center.getX() + Math.cos(nw) * item.getLength(),
					center.getY() + Math.sin(nw) * item.getLength()
			);
			removeItem(item);
			setWinkelAndLengthAndSubringAndCenter(item, nw, nw, item.getLength(), item.getLength(), item.getSubRing(), nc, item.getWinkelNr(), 0);
		}
		cim.clearAddWinkelTmp();
	}
	
	public synchronized boolean animateChangeInit(Cluster cluster, boolean resetItemsLocations) {
		if (
				!showClusterAnimation && !showResetItemLocationAnimation && 
				(cluster == null && resetItemsLocations || !cluster.equals(clusterBy))) {
			clusterByChange = cluster;
			oldNovaDimm = novaDimm;
			oldPolygonDimm = novaPolygonDimm;
			changeAnimationPhase2 = false;
			if (cluster != null) {
				showClusterAnimation = true;
			} else
			if (resetItemsLocations) {
				showResetItemLocationAnimation = true;
			}
			oldShowItems = showItems;
			if (showItems) {
				toggleShowItems();
			}
			return true;
		}
		return false;
	}
	
	public synchronized void calculatePolygons() {
		for (ClusterInfo ci : cim.getClusterInfos().values()) {
			ci.polygon2();
		}
	}
	
	public boolean isShowChangeAnimation() {
		return showClusterAnimation || showResetItemLocationAnimation;
	}

	public synchronized boolean animateChange() {
		if (showClusterAnimation || showResetItemLocationAnimation) {
			if (! changeAnimationPhase2) {
				novaDimm *= 0.90;
				novaPolygonDimm *= 0.90;
				//System.out.println("nd=" + novaDimm + "; npd=" + novaPolygonDimm);
				if (novaDimm < 0.01 && novaPolygonDimm < 0.01) {
					if (showClusterAnimation) {
						setClusterBy(clusterByChange);
					} else
					if (showResetItemLocationAnimation) {
						randomizeItemLocations();
					}
					novaDimm = 0.01;
					novaPolygonDimm = 0.01;
					if (oldShowItems) {
						toggleShowItems();
					}
					changeAnimationPhase2 = true;
				}
			} else {
				//System.out.println("nd:" + novaDimm + ", npd:" + novaPolygonDimm);
				novaDimm = Math.min(oldNovaDimm, novaDimm / 0.90);
				novaPolygonDimm = Math.min(oldPolygonDimm, novaPolygonDimm / 0.90);
				//System.out.println("nd=" + novaDimm + "; npd=" + novaPolygonDimm);
				if (novaDimm >= oldNovaDimm) {
					novaDimm = oldNovaDimm;
					if (novaPolygonDimm >= oldPolygonDimm) {
						novaPolygonDimm = oldPolygonDimm;
						clusterByChange = null;
						showClusterAnimation = false;
						showResetItemLocationAnimation = false;
					}
				}
			}
		}
		return showClusterAnimation;
	}
	
	public synchronized void setClusterBy(Cluster c) {
		clusterBy = c;
		itemsByClusterAndRing = new HashMap<>();
		cim = new ClusterInfoMap();
		
		for (Item item : items) {
			item.setClusterBy(clusterBy, cim);
			
			// itemsByClusterAndRing
	    	List<List<Item>> cluster = itemsByClusterAndRing.get(item.getClusterValue());
	    	if (cluster == null) {
	    		cluster = new ArrayList<>();
	    		for (int i = 0; i < Rings.getInstance().getSizes().length; i++) {
	    			cluster.add(null);
	    		}
	    		itemsByClusterAndRing.put(item.getClusterValue(), cluster);
	    	}
			List<Item> l = cluster.get(item.getRing());
			if (l == null) {
				l = new ArrayList<>();
				cluster.set(item.getRing(), l);
			}
			l.add(item);
		}
	}

	private void addItem(Item item) {
		if (item.isPlaceholder()) {
			//itemCountPlaceholder++;
		} else {
			itemCount++;
		}
		items.add(item);
		
		while (itemsByRing.size() <= item.getRing()) {
			itemsByRing.add(new ArrayList<>());
		}
		itemsByRing.get(item.getRing()).add(item);
	}
	
    private synchronized void novaAnimateInit() {
		for (Item item : items) {
			item.setNovaAnimateIdx(-1);
		}
    	
		for (Item item : items) {
			if (!item.isPlaceholder()) {
				double diff = Double.MAX_VALUE;
				for (int u = 0; u < itemCount; u++) {
					Point2D p = novaAnimateGet(u);
					double diff2 = item.getArcCenter().distance(p);
					if (diff2 < diff) {
						boolean used = false;
						for (Item item2 : items) {
							if (!item2.isPlaceholder()) {
								if (item2.getNovaAnimateIdx() == u) {
									used = true;
									break;
								}
							}
						}
						if (! used) {
							item.setNovaAnimateIdx(u);
							diff = diff2;
						}
					}
				}
			}
		}
    }
    

    private Point2D novaAnimateGet(int c) {
    	double cos = Math.cos(Math.PI / (itemCount / 2.0) * (c + novaAnimateVersatz));
		double dx = cos * center.getX() / 2;
		dx = dx * (2 + (dx + center.getX())/center.getX())/3;
		double sin = Math.sin(Math.PI / (itemCount / 4.0) * (c + novaAnimateVersatz) );
		double dy = sin * center.getY() / 2;
		if (dy > 0) {
			dy = dy * (1 + dy/center.getY()) * 2;
		}
		double y = center.getY() + dy * (1.5 + dx/center.getX())/2 * 0.8;
		double x = center.getX() + dx * (dy + center.getY()*5)/center.getY()/5*(2 - Math.pow(Math.abs(cos), 0.25)) * 0.8;
		return new Point2D.Double(x - center.getX() / 8, y - center.getX() / 7);
    }
        
    public void novaAnimateVersatz() {
		if (showNovaPointsAnimation || showNovaPointsAnimation2) {
			novaAnimateVersatz = novaAnimateVersatz + items.size()/2500.0;
		}
    }
    
    public synchronized boolean novaAnimate() {
    	if (showNovaPointsAnimation || showNovaPointsAnimation2) {
	    	boolean changed = false;
	    	for (Item item : items) {
				if (! item.isPlaceholder()) {
					Point2D ipGoal = novaAnimateGet(item.getNovaAnimateIdx()) ;
					Point2D ac = item.getArcCenter();
					double diffx = ipGoal.getX() - ac.getX();
					double diffy = ipGoal.getY() - ac.getY();
					if (showNovaPointsAnimation) {
						item.addToNovaAnimationPoint(diffx / 100, diffy / 100);
						changed = Math.abs(diffx) >= 0.25 || Math.abs(diffy) >= 0.25;
					} else {
						item.addToNovaAnimationPoint(- item.getNovaAnimationPoint().getX() / 50, - item.getNovaAnimationPoint().getY() / 50);
						changed = Math.abs(item.getNovaAnimationPoint().getX()) >= 1 || Math.abs(item.getNovaAnimationPoint().getY()) >= 1;
					}
				}
			}
			if (showNovaPointsAnimation) {
				if (changed) {
					novaDimm *= 0.99;
				} else {
					novaDimm = 0;
				}
			} else {
				novaDimm = Math.min(1, novaDimm / 0.98);
				if (! changed) {
					novaDimm = 1;
					showNovaPointsAnimation2 = false;
					for (Item item : items) {
						item.resetNovaAnimationPoint();
					}
				}
			}
			return true;
    	} 
    	return false;
    }
    
    public void novaAnimateStop() {
		novaDimm = Math.max(0.01, novaDimm);
		showNovaPointsAnimation = false;
		showNovaPointsAnimation2 = true;
		if (showItems != showItemsStateBeforeAnimation) {
			toggleShowItems();
		}
    }
    
    public void novaAnimateStart() {
		if (! showNovaPointsAnimation) {
			showItemsStateBeforeAnimation = showItems;
			if (! showItems) {
				toggleShowItems();
			}
			novaDimm = 1;
			novaAnimateVersatz = 0.0;
			novaAnimateInit();
		}
		showNovaPointsAnimation = true;
    }
    
    public boolean isFollowMouse() {
		return followMouse;
	}
    
    private double ndistance (Item x1, Item x2, SortedMap<Double, Item> sm, double max) {
    	
    	double dd = 0;
    	
    	if (! x1.isPlaceholder()) {
    		// cluster
    		double wd = 0;
    		double sd = 0;
    		double cd = 0;
    		ClusterInfo ci = x1.getClusterInfo();
    		if (ci.getCount() > 1) {
    			Point2D theCenter;
    			if (showSquare) {
    				theCenter = centerSquare;
    			} else {
    				theCenter = center;
    			}
   				wd = ci.getWinkelDistanceX1(theCenter, x1, x2, max, showSquare);
    			//wd = winkelDistanceX1(x1, x2);
   				if (! showSquare) {
   					//sd = simpleDistance(x1, x2, sm.values()) / 100000;
   				}
    		}
       		//cd = clusterDistance(x1.getClusterValue(), null, x1, x2)/1000;
    		//System.out.println("wd:" + wd + ", sd:" + sd + ", cd:" + cd);
    		dd += wd + cd + sd;
    	}
    	return dd;
    }
    
   
    private double clusterDistance(String clusterValue) {
		double count = 0;
		int gc1 = 0;
		int gc2 = 0;
		int nipsr = 0;
    	for (int ring = 1; ring < ringItemLines.length && ring < itemsByRing.size(); ring++) {
    		List<Item> lli = itemsByRing.get(ring);
    		if (lli != null) {
    			if (itemsByClusterAndRing.get(clusterValue) == null) {
    				return Double.MAX_VALUE;
    			}
				List<Item> lis = itemsByClusterAndRing.get(clusterValue).get(ring);
				if (lis != null && lis.size() > 0) {
					for (int subring = 0; subring < ringItemLines[ring]; subring++) {
						int count1 = 0;
						int count2 = 0;
						for (Item item : lis) {
							if (item.getSubRing() == subring) {
								double winknr = item.getWinkel();
								if (! item.isPlaceholder()) {
									nipsr++;
									boolean broke = false;
									double bw = Double.MAX_VALUE;
			    					double w;
				    				for (Item i : itemsByRingSortedByWinkel.get(ring).tailMap(winknr + Double.MIN_VALUE).values()) {
				    					w = i.getWinkel();
										if (showSquare && bw != Double.MAX_VALUE && Math.abs(Util.getWDiff(bw, w)) > squareMaxWinkelDiff / 4) {
											broke = true;
											break;
										}
				    					if (i.isPlaceholder()) {
			    							count1++;
			    							gc1++;
			    						} else
			    						if (! i.getClusterValue().equals(clusterValue)) {
			    							broke = true;
				    						break;
				    					}
				    					bw = w;
				    				}
				    				bw = Double.MAX_VALUE;
				    				if (! broke) {
					    				for (Item i : itemsByRingSortedByWinkel.get(ring).headMap(winknr).values()) {
					    					w = i.getWinkel();
											if (showSquare && bw != Double.MAX_VALUE && Math.abs(Util.getWDiff(bw, w)) > squareMaxWinkelDiff / 4) {
												broke = true;
												break;
											}
					    					if (i.isPlaceholder()) {
				    							count1++;
				    							gc1++;
				    						} else
				    						if (! i.getClusterValue().equals(clusterValue) ) {
					    						break;
					    					}
					    					bw = w;
					    				}
				    				}
				    				bw = Double.MAX_VALUE;
				    				broke = false;
				    				List<Item> headItems = new ArrayList<>(itemsByRingSortedByWinkel.get(ring).headMap(winknr).values());
				    				for (int idx = headItems.size() - 1; idx >= 0; idx--) {
				    					Item i = headItems.get(idx);
				    					w = i.getWinkel();
										if (showSquare && bw != Double.MAX_VALUE && Math.abs(Util.getWDiff(bw, w)) > squareMaxWinkelDiff / 4) {
											broke = true;
											break;
										}
				    					if (i.isPlaceholder()) {
			    							count2++;
			    							gc2++;
				    					} else
				    					if (! i.getClusterValue().equals(clusterValue)){
				    						broke = true;
				    						break;
				    					}
				    					bw = w;
				    				}
				    				bw = Double.MAX_VALUE;
				    				if (! broke) {
					    				headItems = new ArrayList<>(itemsByRingSortedByWinkel.get(ring).tailMap(winknr + Double.MIN_VALUE).values());
					    				for (int idx = headItems.size() - 1; idx >= 0; idx--) {
					    					Item i = headItems.get(idx);
					    					w = i.getWinkel();
											if (showSquare && bw != Double.MAX_VALUE && Math.abs(Util.getWDiff(bw, w)) > squareMaxWinkelDiff / 4) {
												broke = true;
												break;
											}
					    					if (i.isPlaceholder()) {
				    							count2++;
				    							gc2++;
					    					} else
					    					if (! i.getClusterValue().equals(clusterValue)){
					    						break;
					    					}
					    					bw = w;
					    				}
				    				}
				    				count += count1 - count2;
				    				break;
								}
							}
			    		}
					}
				}
    		}
    	}
    	//
    	if (gc1 == 0 && gc2 > 0) {
    		return - Double.MAX_VALUE / 2;
    	} else
    	if (gc2 == 0 && gc1 > 0) {
    		return Double.MAX_VALUE / 2;
    	} else
    	if (gc1 == 0 && gc2 == 0) {
    		return Double.MAX_VALUE - random.nextDouble() * Double.MAX_VALUE / 2;
    	}
    	if (nipsr > 0) {
    		return count / (double) nipsr;
    	}
    	return 0;
    }
   
    /*
    private Point2D getListCenter(List<Item> l) {
		Point2D c = new Point2D.Double();
		for (Item li : l) {
			c.setLocation(c.getX() + li.getCenter().getX(), c.getY() + li.getCenter().getY());
		}
		c.setLocation(c.getX() / l.size(), c.getY() / l.size());
		return c;
    }
    */
    
    
    /*
    private double winkelDistanceX1(Item x1, Item x2) {
     	double d = 0;
     	ClusterInfo ci = x1.getClusterInfo();
    	Point2D c = new Point2D.Double();
    	
    	c.setLocation(ci.getCenterAverage());
    	c.setLocation(c.getX() - x1.getCenter().getX() / ci.getCount(), c.getY() - x1.getCenter().getY() / ci.getCount());
    	c.setLocation(c.getX() + x2.getCenter().getX() / ci.getCount(), c.getY() + x2.getCenter().getY() / ci.getCount());

 		double w;
		if (! showSquare) {
			w = Util.getAngle(center, c);
		} else {
			w = Util.getAngle(centerSquare, c);
		}
		for (Item item : ci.getItems()) {
			if (item == x1) {
				item = x2;
			}
			double closerW1 = Math.abs (
					- Util.getWDiff(w, item.getWinkel()) / 2 
					+ Util.getWDiff(2 * Math.PI - w, 2 * Math.PI - item.getWinkel()) / 2
					)
					; 
			if (closerW1 > Math.PI) {
				closerW1 = 2 * Math.PI - closerW1;
			}
			d += closerW1;
		}
		return d;
    }*/
    
/*
	private double simpleDistance(Item x1, Item x2, List<Item> l) {
		Point2D c = getListCenter(l);
		double d = 0;
		if (x1 != x2) {
			c.setLocation(c.getX() - x1.getCenter().getX()/l.size(), c.getY() - x1.getCenter().getY()/l.size());
			c.setLocation(c.getX() + x2.getCenter().getX()/l.size(), c.getY() + x2.getCenter().getY()/l.size());
		}
		
		for (Item li : l) {
			d += c.distance(li.getCenter());
		}
		if (x1 != x2) {
			d -= c.distance(x1.getCenter());
			d += c.distance(x2.getCenter());
		}
		return d;
	} 
	*/
    
    public synchronized void setCenter(Point2D p, int lineSize) {
    	
    	for (int i = 0; i < Rings.getInstance().getSizes().length; i++) {
    		int r1 = 0;
    		if (i > 0) {
    			r1 = Rings.getInstance().getSizes()[i - 1];
    		}
    		int r2 = Rings.getInstance().getSizes()[i];
    		ringItemLines[i] = Math.max(1, (r2 - r1) / (Config.itemSizes[Config.itemSizes.length - 1] * 2 + 5));
    		Rings.getInstance().getBigBangMultis()[i] = new double[ringItemLines[i]];
    	}

    	center = p;
    	for (int i = 0; i < Config.ringsCount && i < itemsByRing.size(); i++) {
    		if (itemsByRing.get(i).size() > 0) {
	        	int totalRingsLength = 0;
	        	int[] ringLength = new int[Rings.getInstance().getSizes()[i]];
	        	for (int r = 0; r < ringItemLines[i]; r++) {
	        		int minclength = 0;
	        		if (i > 0) {
	        			minclength = Rings.getInstance().getSizes()[i - 1] / 2 + lineSize;
	        		}
	        		int maxclength = Rings.getInstance().getSizes()[i] / 2 - lineSize;
	        		int clength = minclength + (maxclength - minclength) / (1 + ringItemLines[i]) * r;
	        		ringLength[r] = clength;
	        		totalRingsLength += clength;
	        	}
	        	int speed = totalRingsLength / itemsByRing.get(i).size();
	        	int ring = 0;
	        	int[] ringCount = new int[ringItemLines[i]];
	        	int[] ringLength2 = new int[ringLength.length];
	        	System.arraycopy(ringLength, 0, ringLength2, 0, ringLength.length);
	        	for (int o = 0; o < itemsByRing.get(i).size(); o++) {
	        		ringLength2[ring] -= speed;
	        		if (ringLength2[ring] < -speed/2) {
	        			ringLength2[ring + 1] += ringLength2[ring];
	        			ring++;
	        		}
	        		ringCount[ring]++;
	        	}
    		}
    	}

    }
    
    public boolean isShowSquareIsAnimating() {
		return showSquareIsAnimating;
	}
    
	public synchronized boolean optimize(int optSpeed) {
		if (
				showBigBangAnimation || showBigBangAnimation2 ||
				showCloserAnimation || showCloserAnimation2 ||
				showNovaPointsAnimation || showNovaPointsAnimation2 ||
				showStarWarsAnimation ||
				showClusterAnimation || showResetItemLocationAnimation ||
				showSquareIsAnimating //|| changeAnimationPhase2
		) {
			return false;
		}
		
    	this.optSpeed = optSpeed;
    	boolean changed = false;
    	boolean itemsMoved = false;
    	int unchanged = 0;
		if (optimize) {
			optimizationReduction = Math.min(optimizationReduction + Config.optimizationReductionSpeed / Math.PI * minimumWinkelDiffAcrossRings * maximumItemsPerSubring, 1.02);
			SortedMap<Double, Item> sm = new TreeMap<>();
			for (String sc : cim.getClusterInfos().keySet()) {
				if (! isClusterPlaceholder(sc)) {
		    		if (random.nextDouble() < 0.1) {
		    			Long lcm = lastClusterMove.get(sc);
		    			if (lcm == null || System.currentTimeMillis() - lcm > 50) {
		    				if (optimizeCompleteCluster(sc)) {
		    					changed = true;
		    					lastClusterMove.put(sc, System.currentTimeMillis());
		    				}
		    			}
		    		}
				}
			}
	    	for (int i = 0; i < optSpeed && unchanged < optSpeed / 4; i++) {
				{
					List<Item> ringItems = itemsByRing.get(random.nextInt(itemsByRing.size()));
					Item a1 = null;
					Item a2 = null;
					int i1;
					int i2;
					
					if (ringItems.size() > 0) {
						i1 = random.nextInt(ringItems.size());
						i2 = random.nextInt(ringItems.size());
			    		a1 = ringItems.get(i1);
			    		a2 = ringItems.get(i2);
			    		
				    	if (a1 != a2 && (! a1.getClusterValue().equals(a2.getClusterValue())) &&
				    		(a1.isPlaceholder() || a2.isPlaceholder()) &&
				    		(lastItemMove.get(a1) == null || System.currentTimeMillis() - lastItemMove.get(a1) > 150) &&
				    		(lastItemMove.get(a2) == null || System.currentTimeMillis() - lastItemMove.get(a2) > 150)
				    	) {
					    	double d1 = ndistance(a1, a1, sm, Double.MAX_VALUE) + ndistance(a2, a2, sm, Double.MAX_VALUE);
					    	double d2 = ndistance(a1, a2, sm, Double.MAX_VALUE) + ndistance(a2, a1, sm, Double.MAX_VALUE);
					    	if (d2 * optimizationReduction < d1) {
						    	lastItemMove.put(a1, System.currentTimeMillis());
						    	lastItemMove.put(a2, System.currentTimeMillis());
					    		switchItemsInRing(a1, a2);
					    		if (showItems) {
						    		a1.resetTmpRampUp();
						    		a1.setTmpRampUpSpeed(0.02);
						    		a2.resetTmpRampUp();
						    		a2.setTmpRampUpSpeed(0.02);
						    		itemsMoved = true;
					    		}
						    }
						}
				   	}
				}
				if (! itemsMoved) {
					unchanged++;
				} else {
					unchanged = 0;
				}
	    	}
	    }
    	return changed || itemsMoved;
	}
    
    public void resetOptimizationReduction() {
    	optimizationReduction = 1.0;
    }
    
    private boolean optimizeCompleteCluster(String clusterValue) {
    	double distance = clusterDistance(clusterValue);
    	
		Point2D sc;
		if (showSquare) {
			sc = centerSquare;
		} else {
			sc = center;
		}
		double minDiff;
		if (showSquare) {
			minDiff = minimumWinkelDiffAcrossRings / 2;
		} else {
			minDiff = minimumWinkelDiffAcrossRings;
		}
    	if (distance < -2) {
			cim.getClusterInfos().get(clusterValue).setAddWinkelTmp(-minDiff, sc);
    	} else
    	if (distance > 2) {
			cim.getClusterInfos().get(clusterValue).setAddWinkelTmp(+minDiff, sc);
    	}
    	//System.out.println("dist=" + distance);
    	
    	
    	return distance != 0;
    }
    
    private void switchItemsInRing(Item item1, Item item2) {
    	double wTmp = item1.getWinkel();
    	double wTmpw = item1.getWinkelCircle();
    	double lTmp = item1.getLength();
    	double lTmpw = item1.getLengthCircle();
    	int sTmp = item1.getSubRing();
    	int iTmp = item1.getWinkelNr();
    	Point2D pTmp = new Point2D.Double(item1.getCenter().getX(), item1.getCenter().getY());
    	removeItem(item1);
    	removeItem(item2);
    	setWinkelAndLengthAndSubringAndCenter (item1, item2.getWinkel(), item2.getWinkelCircle(), item2.getLength(), item2.getLengthCircle(), item2.getSubRing(), item2.getCenter(), item2.getWinkelNr());
    	setWinkelAndLengthAndSubringAndCenter (item2, wTmp, wTmpw, lTmp, lTmpw, sTmp, pTmp, iTmp);
    }
    
    private void validate() {
    	/*
    	int c = 0;
    	for (int i = 0; i < itemsByRingAndSubringSortedByWinkel.size(); i++) {
    		for (int o = 0; o < itemsByRingAndSubringSortedByWinkel.get(i).size(); o++) {
    			c += itemsByRingAndSubringSortedByWinkel.get(i).get(o).size();
    			for (Entry<Integer, Item> entry : itemsByRingAndSubringSortedByWinkel.get(i).get(o).entrySet()) {
    				if (!entry.getKey().equals(entry.getValue().getWinkelNr())) {
    					System.out.println("[" + i + "][" + o + "] " + entry.getKey() + " vs " + entry.getValue().getWinkelNr());
    					throw new IllegalStateException();
    				}
    			}
    		}
    	}
    	if (c > items.size()) {
    		int c2 = 0;
    		for (int i = 0; i < itemsByRing.size(); i++) {
    			c2 += itemsByRing.get(i).size();
    		}
    		System.out.println("prob:" + c + " vs " + items.size() + " vs " + c2);
    	}
    	*/
    }
    
    private void removeItem(Item item) {
    	itemsByRingSortedByWinkel.get(item.getRing()).remove(item.getWinkel());
    	if (item.getSubRing() >= 0) {
    		itemsByRingAndSubringSortedByWinkel.get(item.getRing()).get(item.getSubRing()).remove(item.getWinkelNr());
    	}
    }
    
    private void setWinkelAndLengthAndSubringAndCenter(Item item, double winkel, double winkelCircle, double length, double lengthCircle, int subring, Point2D center, int winkelNr) {
    	setWinkelAndLengthAndSubringAndCenter(item, winkel, winkelCircle, length, lengthCircle, subring, center, winkelNr, 0.0);
    }
    
    private void setWinkelAndLengthAndSubringAndCenter(Item item, double winkel, double winkelCircle, double length, double lengthCircle, int subring, Point2D center, int winkelNr, double tlr) {
    	validate();
    	item.setWinkelAndLengthAndSubringAndCenterX(winkel, winkelCircle, length, lengthCircle, subring, center, winkelNr, tlr);
    	itemsByRingSortedByWinkel.get(item.getRing()).put(item.getWinkel(), item);
    	itemsByRingAndSubringSortedByWinkel.get(item.getRing()).get(item.getSubRing()).put(item.getWinkelNr(), item);
    	validate();
    }
    
    public synchronized void fastForwardToTargets() {
    	for (Item item : items) {
    		item.fastForwardToTarget();
    	}
    }

    public boolean dimmText() {
		if (showText) {
			novaTextDimm = Math.min(1, novaTextDimm + 0.01);
		} else {
			novaTextDimm = Math.max(0, novaTextDimm - 0.01);
		}
		return Math.abs(novaTextDimm - 0.5) != 0.5;
    }
    
    public synchronized boolean rampUpArcs() {
    	boolean change = false;
		for (Item item : items) {
			if (item.applyTmpRampUp(showItems)) {
				change = true;
			}
		}
		return change;
    }
    
    public boolean dimmPolygons() {
    	if (! isShowChangeAnimation()) {
			if (showPolygons) {
				novaPolygonDimm = Math.min(1, novaPolygonDimm + 0.01);
			} else {
				novaPolygonDimm = Math.max(0, novaPolygonDimm - 0.01);
			}
    	}
		return Math.abs(novaPolygonDimm) != 0.5;
    }
    
    public boolean isShowNovaPointsAnimation1Or2OrBigBangAnimation1Or2() {
		return showNovaPointsAnimation || showNovaPointsAnimation2 || showBigBangAnimation || showBigBangAnimation2;
	}
    
    public double getNovaDimm() {
		return novaDimm;
	}
    
    public double getNovaTextDimm() {
		return novaTextDimm;
	}
    
    public double getNovaPolygonDimm() {
		return novaPolygonDimm;
	}
    
    public synchronized void calculateItemPositions(boolean clusterChange) {
    	itemsByRingAndSubringSortedByWinkel.clear();
    	itemsByRingSortedByWinkel.clear();
    	minimumWinkelDiffAcrossRings = Double.MAX_VALUE;
    	for (int i = 0; i < Config.ringsCount && i < itemsByRing.size(); i++) {
    		if (itemsByRing.get(i).size() > 0) {
	        	int totalRingsLength = 0;
	        	double[] ringLength = new double[ringItemLines[i]];
	        	for (int r = 0; r < ringItemLines[i]; r++) {
	        		double minclength = 0;
	        		if (i > 0) {
	        			minclength = Rings.getInstance().getSizes()[i - 1] / 2.0 + lineSize;
	        		}
	        		double maxclength = Rings.getInstance().getSizes()[i] / 2.0 - lineSize;
	        		double clength = minclength + (maxclength - minclength) / (1.0 + ringItemLines[i]) * r;
	        		ringLength[r] = (int) clength;
	        		totalRingsLength += (int) clength;
	        	}
	        	double speed = totalRingsLength / (double) (itemsByRing.get(i).size());
	        	int ring = 0;
	        	int[] ringCount = new int[ringItemLines[i]];
	        	double[] ringLength2 = new double[ringLength.length];
	        	System.arraycopy(ringLength, 0, ringLength2, 0, ringLength.length);
	        	for (int o = 0; o < itemsByRing.get(i).size(); o++) {
	        		ringLength2[ring] -= speed;
	        		if (ringLength2[ring] < -speed/2.0) {
	        			ringLength2[ring + 1] += ringLength2[ring];
	        			ring++;
	        		}
	        		ringCount[ring]++;
	        		maximumItemsPerSubring = Math.max(maximumItemsPerSubring, ringCount[ring]);
	        	}
        		minimumWinkelDiffAcrossRings = Math.PI * 2 / maximumItemsPerSubring;
	        	int subring = 0;
	        	for (int o = 0; o < itemsByRing.get(i).size(); o++) {
	        		
	        		Item item = itemsByRing.get(i).get(o);
	        		
	        		int minclength = 0;
	        		if (i > 0) {
	        			minclength = Rings.getInstance().getSizes()[i - 1] / 2 + lineSize;
	        		}
	        		int maxclength = Rings.getInstance().getSizes()[i] / 2 - lineSize;
	        		//int ring = ((o * rings[i]) / items[i].length) + 1;
	        		ringLength[subring] -= speed;
	        		if (ringLength[subring] < -speed/2) {
	        			ringLength[subring + 1] += ringLength[subring];
	        			subring++;
	        		}
	        		int clength = minclength + (maxclength - minclength) / (1 + ringItemLines[i]) * (subring + 1);
	        		double versatz = Math.PI*2/(itemsByRing.get(i).size()/ringItemLines[i])/2 * subring/* + 0.01*/;
	        		double winkel = globalVersatz + versatz + Math.PI*2/(ringCount[subring])*(o  % (ringCount[subring]));
	        		double baseX = center.getX() + Math.cos(winkel) * clength;
	        		double baseY = center.getY() + Math.sin(winkel) * clength;
	        		
	        		while (itemsByRingSortedByWinkel.size() <= i) {
	        			itemsByRingSortedByWinkel.add(new TreeMap<>());
	        		}
	        		while (itemsByRingAndSubringSortedByWinkel.size() <= i) {
	        			itemsByRingAndSubringSortedByWinkel.add(new ArrayList<>());
	        		}
	        		while (itemsByRingAndSubringSortedByWinkel.get(i).size() <= subring) {
	        			itemsByRingAndSubringSortedByWinkel.get(i).add(new TreeMap<>());
	        		}
	        		while (itemsByRingAndSubringSortedByWinkel.get(i).size() <= item.getSubRing()) {
	        			itemsByRingAndSubringSortedByWinkel.get(i).add(new TreeMap<>());
	        		}
	        		
	        		removeItem(item);
	        		setWinkelAndLengthAndSubringAndCenter (item, winkel, winkel, clength, clength, subring, new Point2D.Double(baseX, baseY), o);
	        	}
    		}
    	}
    	if (clusterChange) 
    	{
    		fastForwardToTargets();
    	}
    	if (showSquare) {
    		animateToSquareInit();
    		while (! squareReached()) {
    			animateToSquare(false);
    		}
    	}
    }
        
    public synchronized void calculateItemPositions2(boolean showRadar, double radarLine) {
    	double w;
    	if (showSquare) {
    		w = getSquaredWinkel(radarLine);
    	} else {
    		w = radarLine;
    	}
    	for (Item item : items) {
    		item.calculatePositions2(showRadar, w, showItems, novaDimm, showSquare, squareMaxWinkelDiff);
    	}
    }
        
    public boolean toggleShowText() {
    	showText = ! showText;
    	return showText;
    }
    
    public boolean togglePolygons() {
		showPolygons = ! showPolygons;
		return showPolygons;
    }
    
    public boolean toggleOptimize() {
    	optimize = ! optimize;
    	return optimize;
    }
    
    public synchronized boolean toggleShowItems() {
		int count = 0;
		for (ClusterInfo ci : cim.getClusterInfos().values()) {
			count++;
			for (Item item : ci.getItems()) {
				if (!item.isPlaceholder()) {
					item.setTmpRampUpSpeed(Math.max(0.005, count * 0.05 / cim.getClusterInfos().size()));
				}
			}
		}
		showItems = ! showItems;
		return showItems;
    }
    
    public synchronized List<Item> getItems() {
		return items;
	}
    
    public synchronized boolean toggleFollowMouse(Point2D p, Point2D sl) {
    	followMouse = !followMouse;
		if (! followMouse) {
			for (Item item : items) {
				item.setArcDegree(0);
			}
		} else {
			setDegrees(p, sl);
		}
		return followMouse;
    }
    
    public boolean isShowText() {
		return showText;
	}
    
    public boolean isShowNovaPointsAnimation() {
		return showNovaPointsAnimation;
	}
    
    public boolean isShowNovaPointsAnimation2() {
		return showNovaPointsAnimation2;
	}
    
    private synchronized void randomizeItemLocations() {
		for (List<Item> itemList : itemsByRing) {
			for (int i = 0; i < itemList.size() * 10; i++) {
				int r1 = random.nextInt(itemList.size());
				int r2 = random.nextInt(itemList.size());
				if (r1 != r2) {
					Item i1 = itemList.get(r1);
					Item i2 = itemList.get(r2);
					switchItemsInRing(i1, i2);
				}
			}
		}
    }
    
    public void setDegrees(Point p, Point sl) {
		setDegrees(new Point2D.Double((p.getX() - sl.getX()), (p.getY() - sl.getY())));
    }

    public void setDegrees(Point2D p, Point2D sl) {
		setDegrees(new Point2D.Double((p.getX() - sl.getX()), (p.getY() - sl.getY())));
    }
  
    public void setDegrees(Point2D p) {
    	mouseLocation = p;
    }
    
    public synchronized boolean setDegrees() {
    	if (followMouse && mouseLocation != null) {
	    	for (Item item : items) {
	    		Point2D pi = item.getArcCenter();
	        	double w = Math.PI - Util.getAngle(pi, mouseLocation);
	        	double d = (w/Math.PI * 180);
	        	
	        	item.setArcDegree((int)Math.round(d - ((item.getPercentage() * 360 / 100.0 / 2))));
			}
	    	return true;
    	}
    	return false;
    }

    public synchronized String setTooltipText(Point2D p) {
    	for (Item item : items) {
        	if (item.containsForTooltip(p, showText, showItems)) {
        		return item.getText();
    		}
    	}
    	return null;
    }

    public synchronized void initGraphics(Graphics2D g, int lineSize) {
    	this.lineSize = lineSize;
    	for (Item item : items) {
    		item.initGraphics(g);
    	}
    }

    /*
    public synchronized GeneralPath polygon2(String name, boolean roundBack, double edgeDistance) {
    	
    	// create polygon
    	List<Item> ibc = cim.get(name).getItems();
    	if (ibc == null || ibc.size() == 0) {
    		return null;
    	}
    	//System.out.println("name " + name);
    	List<Item> pol = polygon(ibc);
    	
    	
    	GeneralPath polygon = null;
    	
    	List<Item> l = new ArrayList<>();
		Point2D p = new Point2D.Double();

    	for (Item item : pol) {
    		if (!roundBack || p.distance(center) + Config.itemSizes[item.getSize()] / 2 <= Rings.getInstance().getSize() / 2) {
    			l.add(item);
    			p.setLocation(p.getX() + item.getArcCenter().getX(), p.getY() + item.getArcCenter().getY());
    		}
    	}
    	    	
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
    			double r = edgeDistance/lx;
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
    	
    }*/
        
    public Point2D getCenter() {
		return center;
	}
    
	public synchronized boolean stepToTargetLocation(double limit, double speed) {
		boolean change = false;
		for (Item item : items) {
			if (item.getTargetLocationReached() > 0) {
				item.stepToTargetLocation(limit, speed);
				change = true;
			}
		}
		return change;
	}
	
	public synchronized boolean stepToTargetLocation() {
		return stepToTargetLocation(0.3, Config.stepToTargetLocationSpeed);
	}

    
    public synchronized Set<String> getClusterValues() {
    	return cim.getClusterInfos().keySet();
    }
    
    public synchronized void animateStarWarsStop() {
		for (Item item : items) {
			item.resetYdiff();
		}
		showStarWarsAnimation = false;
		if (!showItemsStateBeforeAnimation) {
			toggleShowItems();
		}
    }
    
    public synchronized void animateStarWarsStart() {
    	if (!showStarWarsAnimation) {
    		//SquareGrid.init(center.getX() * 2, center.getY() * 2, center);
    		//novaDimm = 1;
			showItemsStateBeforeAnimation = showItems;
			if (! showItems) {
				toggleShowItems();
			}
	    	for (Entry<String, ClusterInfo> e : cim.getClusterInfos().entrySet()) {
	    		if (! isClusterPlaceholder(e.getKey())) {
	    			double rc = random.nextDouble() * 2 + 1;
	    			for (Item item : e.getValue().getItems()) {
	    				double r = random.nextDouble();
	   					item.setYdiffdiff(r * rc);
	    			}
	    		}
	    	}
    	}
    	showStarWarsAnimation = true;
    }

    public synchronized boolean animateStarWars() {
    	if (showStarWarsAnimation) {
    		//SquareGrid.move();
			for (Item item : items) {
				item.applyYdiff(
						center.getY() * 2 - Config.yOffset, 
						Config.yOffset
				);
			}
			return true;
    	}
    	return false;
    }
    
    public boolean isShowStarWarsAnimation() {
		return showStarWarsAnimation;
	}
 
    public synchronized void animateBigBangStart() {
    	if (!showBigBangAnimation) {
    		bigBangVersatzTotal = 0;
    		bigBangVersatz = 0.01;
    		Rings.getInstance().resetBigBangMultis();
    		novaDimm = 1;
			showItemsStateBeforeAnimation = showItems;
			if (! showItems) {
				toggleShowItems();
			}
    	}
    	showBigBangAnimation = true;
    }
    
    public synchronized boolean animateBigBang() {
    	if (showBigBangAnimation || showBigBangAnimation2) {
	    	if (showBigBangAnimation) {
		    	for (int i = 0; i < Rings.getInstance().getBigBangMultis().length; i++) {
		    		for (int o = 0; o < Rings.getInstance().getBigBangMultis()[i].length; o++) {
		    			Rings.getInstance().getBigBangMultis()[i][o] *= Config.bigBangMoveSpeed;
		    		}
		    	}
		    	if (novaDimm > 0.002) {
		    		novaDimm *= 0.95;
		    	} else {
		    		novaDimm = 0.0;
		    	}
    			RoundGrid.move(Config.bigBangMoveSpeed, bigBangVersatzTotal);
			} else {
		    	for (int i = 0; i < Rings.getInstance().getBigBangMultis().length; i++) {
		    		for (int o = 0; o < Rings.getInstance().getBigBangMultis()[i].length; o++) {
		    			Rings.getInstance().getBigBangMultis()[i][o] = Math.min(
		    					Rings.getInstance().getBigBangMultis()[i][o] + (1-Rings.getInstance().getBigBangMultis()[i][o])*(novaDimm)/5,
		    					1
		    			);
		    		}
		    	}
				novaDimm = Math.min(1, novaDimm / 0.98);
				if (novaDimm >= 0.999 && Rings.getInstance().getBigBangMultis()[Rings.getInstance().getBigBangMultis().length - 1][0] > 0.99) {
					novaDimm = 1;
					bigBangVersatz = 0;
					for (Item item : items) {
			    		if (! item.isPlaceholder()) {
		    				item.animateBigBang(center, novaDimm, -bigBangVersatzTotal);
							item.resetBigBangPoint();
			    		}
					}
					showBigBangAnimation2 = false;
				}
			}
    		if (showBigBangAnimation) {
    			bigBangVersatzTotal = (bigBangVersatzTotal + bigBangVersatz * (1 - novaDimm)) % (Math.PI * 2);
    		} else {
    			//bigBangVersatz = 0; //-bigBangVersatzTotal * novaDimm / 1000;
    			//bigBangVersatzTotal = (bigBangVersatzTotal + bigBangVersatz) % (Math.PI * 2);
    		}
	    	for (Item item : items) {
	    		if (! item.isPlaceholder()) {
    				item.animateBigBang(center, novaDimm, bigBangVersatzTotal * (1 - novaDimm));
	    			if (Math.abs(item.getBigBangZ()) > 100 && item.getBigBangAlpha() == 0) {
	    				Rings.getInstance().getBigBangMultis()[item.getRing()][item.getSubRing()] = 2;
	    				for (int i = 0; i < itemsByRing.get(item.getRing()).size(); i++) {
	    					if (itemsByRing.get(item.getRing()).get(i).getSubRing() == item.getSubRing()) {
	    						itemsByRing.get(item.getRing()).get(i).resetTmpRampUp(0);
	    						itemsByRing.get(item.getRing()).get(i).setTmpRampUpSpeed(0.02);
	    					}
	    				}
	    			} else {
	    			}
	    		}
	    	}

	    	return true;
    	}
    	return false;
    }
    
    public boolean isShowBigBangAnimation() {
		return showBigBangAnimation;
	}
    
    public boolean isShowBigBangAnimation2() {
		return showBigBangAnimation2;
	}

    public synchronized void animateBigBangStop() {
		novaDimm = Math.max(0.01, novaDimm);
    	showBigBangAnimation = false;
    	showBigBangAnimation2 = true;
		//bigBangVersatz = -bigBangVersatzTotal * novaDimm / 100;
		if (showItems != showItemsStateBeforeAnimation) {
			toggleShowItems();
		}
    }
    
    public synchronized void animateCloserStart(double edgeDistance) {
    	if (!showCloserAnimation) {
    		Point2D center;
    		if (!showSquare) {
    			center = this.center;
    		} else {
    			center = this.centerSquare;
    		}
	    	closerPercentage = 0.01;
	    	showCloserAnimation2 = false;
	    	for (String cat : getClusterValues()) {
	    		if (! isClusterPlaceholder(cat)) {
		   			GeneralPath p = cim.get(cat).getPolygon();
		   			if (p != null) {
			   			double w = Util.getAngle(center, new Point2D.Double(p.getBounds2D().getCenterX(), p.getBounds2D().getCenterY()));
				    	for (Item item : cim.get(cat).getItems()) {
			    			item.animateCloserInit(center, w);
				    	}
		   			}
	    		}
	    	}
    	}
    	showCloserAnimation = true;
    }

    public synchronized boolean animateCloser() {
    	boolean changed = showCloserAnimation || showCloserAnimation2;
    	if (showCloserAnimation) {
    		closerPercentage = closerPercentage/0.98 + (1 - closerPercentage) / 25;
    		if (closerPercentage > 0.99) {
    			closerPercentage = 1.0;
    		}
    	} else
    	if (showCloserAnimation2) {
    		closerPercentage *= 0.98 - (1 - closerPercentage) / 25;
    		if (closerPercentage < 0.01) {
    			closerPercentage = 0.0;
    			showCloserAnimation2 = false;
    			showCloserAnimation = false;
    			for (Item item : items) {
    				item.animateCloserStop();
    			}
    		}
    	}
    	if (showCloserAnimation || showCloserAnimation2) {
	    	for (String cat : getClusterValues()) {
		    	for (Item item : cim.get(cat).getItems()) {
		    		if (! showSquare) {
		    			item.animateCloser(center, closerPercentage);
		    		} else {
		    			item.animateCloser(centerSquare, closerPercentage);
		    		}
		    	}
	    	}
    	}
    	return changed;
    }
    
    public synchronized void animateCloserStop() {
    	showCloserAnimation = false;
    	showCloserAnimation2 = true;
    }
    
    public boolean isClusterPlaceholder(String name) {
    	return name.equals("default");
    }
    
    public boolean isShowItems() {
		return showItems;
	}
    
    public boolean isShowPolygons() {
		return showPolygons;
	}
    
    public boolean isShowCloserAnimation() {
		return showCloserAnimation;
	}
    
    public boolean isShowCloserAnimation2() {
		return showCloserAnimation2;
	}
    
    public boolean isShowResetItemLocationAnimation() {
		return showResetItemLocationAnimation;
	}
    
    public int getOptimizationSpeed() {
    	return optSpeed;
    }
    
    public boolean isOptimize() {
		return optimize;
	}
    
    public void animateToSquareInit() {
    	animateToSquareInitCalcValues();
		showSquare = true;
		animateToSquareEnded = false;
    }
    
    public void animateToSquareInitCalcValues() {
    	squareAdditionalRadiusIdx = 0;
    	squareRingRadiusBase = Rings.getInstance().getSize() / 2.0;
    	squareRingRadiusBaseOffset = Config.yOffset;
    	squareMaxWinkelDiff = 0;
    	squareStretchX = 1.0;
		squareStretchY = 1.0;
    }

    public double getSquareMaxWinkelDiff() {
		return squareMaxWinkelDiff;
	}
    
    public boolean isShowSquare() {
		return showSquare;
	}
    
    /*public double getSquareStretch() {
		return squareStretchX;
	}*/
    
    public boolean squareReached() {
    	return animateToSquareEnded && showSquare;
    }
        
    public synchronized void animateToSquareInitSteps() {
    	if (center.getY() > 30000) {
    		return;
    	}
    	animateToSquareInitCalcValues();
    	squareAdditionRadiusSteps.clear();
    	double lastR = 0;
    	Point2D lastP1 = new Point2D.Double(0, 0);
    	Point2D lastP2 = new Point2D.Double(0, 0);
    	Point2D lastP3 = new Point2D.Double(0, 0);
    	while (lastR < squareMaxAdditionalRadius) {
    		Point2D nextP1;
    		Point2D nextP2;
    		Point2D nextP3;
    		do {
    			lastR = Math.min(lastR + Math.min(100000, Math.max(1, lastR * 0.0001)), squareMaxAdditionalRadius);
    			
    			calculateSquareStretchesInternal(false, lastR, false);

    			nextP1 = getItemCenterSquaredInternal(getSquaredWinkelInternal(Math.PI * 1.5, false, lastR), 1 * Rings.getInstance().getSizes()[0] / 2, lastR, true, false);
    			nextP2 = getItemCenterSquaredInternal(getSquaredWinkelInternal(Math.PI * 0.5, false, lastR), 1 * Rings.getInstance().getSize() / 2, lastR, true, false);
    			nextP3 = getItemCenterSquaredInternal(getSquaredWinkelInternal(Math.PI * 0.5, false, lastR), 1 * Rings.getInstance().getSizes()[0] / 2, lastR, true, false);
    			
    		} while (
    				nextP1.distance(lastP1) < Config.stepToSquareSpeed &&
    				nextP2.distance(lastP2) < Config.stepToSquareSpeed &&
    				nextP3.distance(lastP3) < Config.stepToSquareSpeed &&
    				lastR < squareMaxAdditionalRadius);
    		lastP1 = nextP1;
    		lastP2 = nextP2;
    		lastP3 = nextP3;
    		squareAdditionRadiusSteps.add(lastR);
    	}
    	//System.out.println("squareSteps:" + squareAdditionRadiusSteps.size());
    }
    
    public int getSquareAdditionalRadiusIdx() {
		return squareAdditionalRadiusIdx;
	}
    
    public int getSquareAdditionRadiusStepsSize() {
		return squareAdditionRadiusSteps.size();
	}
    
    private void calculateSquareStretches(boolean stretchX, boolean stretchXandY) {
    	calculateSquareStretchesInternal(stretchX, squareAdditionRadiusSteps.get(squareAdditionalRadiusIdx), stretchXandY);
    }

    private void calculateSquareStretchesInternal(boolean stretchX, double addR, boolean stretchXandY) {
		squareMaxWinkelDiff = 0;
    	
		Point2D ip1 = getItemCenterSquaredInternal(getSquaredWinkelInternal(Math.PI/2, stretchX, addR), Rings.getInstance().getSize() / 2 , addR, stretchXandY, false);
		Point2D ip2 = getItemCenterSquaredInternal(getSquaredWinkelInternal(Math.PI/2, stretchX, addR), 0, addR, stretchXandY, false);

		double maxy = Math.max(ip1.getY(), ip2.getY());
		double minx = ip1.getX();
		
		double maxStretchY = Rings.getInstance().getSize()/(maxy - Config.yOffset/2);
		double maxStretchX = Rings.getInstance().getSize() / (center.getX() - minx) * 3.5/4.0;
		
		/*if (
				Math.abs(ip1.getX() - this.center.getX()) < Rings.getInstance().getSize() / 4.0
		) {
			maxStretchY = 1;
		}*/

		squareStretchY = Math.min(maxStretchY, maxStretchX);
		if (ip1.getY() <= ip2.getY()) {
			squareStretchX = maxStretchX;
		} else {
			squareStretchX = squareStretchY;
		}
		
    }
    
    public synchronized boolean animateToSquare(boolean moveToCircle) {
    	showSquareIsAnimating = squareAdditionalRadiusIdx < squareAdditionRadiusSteps.size() - 1 && showSquare || moveToCircle;
    	if (showSquareIsAnimating) {
    		animateToSquareEnded = false;
    		
    		double mf = -1;
    		double f = -1;

			if (! moveToCircle) {
    			squareAdditionalRadiusIdx++;
    		} else {
    			squareAdditionalRadiusIdx--;
    		}
			
			//
    		mf = squareAdditionRadiusSteps.get(squareAdditionalRadiusIdx) / squareMaxAdditionalRadius;
			f = Math.PI * 2;

			//
			calculateSquareStretches(false, false);

				
	    	for (Item item : items) {
	    		double bw;
	    		bw = getSquaredWinkel(item.getWinkelCircle());
	    		double sr = getSquareRadius(item.getLengthCircle());
	    		squareMaxWinkelDiff = Math.max(squareMaxWinkelDiff, (bw - Math.PI * 1.5) * 2);
				Point2D center = getItemCenterSquared(getSquaredWinkel(item.getWinkelCircle(), false), item.getLengthCircle(), squareAdditionalRadiusIdx, true);
				removeItem(item);
	    		setWinkelAndLengthAndSubringAndCenter(
	    				item,
	    				bw, item.getWinkelCircle(),
	    				sr, item.getLengthCircle() /*- squareRingRadiusBaseOffset * mf / 2*/, 
	    				item.getSubRing(), center, item.getWinkelNr());
	    	}
	    	
	    	for (int i = 0; i < 5; i++) {
	    		stepToTargetLocation(0.3 + mf * 0.5, 1 - (1 - Config.stepToTargetLocationSpeed) * Math.max(1, mf * 50));
	    	}
    		minimumWinkelDiffAcrossRings = squareMaxWinkelDiff / maximumItemsPerSubring * squareStretchX;
    		centerSquare.setLocation(
    				this.center.getX(),
    				this.center.getY() + squareAdditionRadiusSteps.get(squareAdditionalRadiusIdx)/f
    				+ squareRingRadiusBaseOffset * mf / 2
    				);
    	}
    	
    	//
		if (showSquare && squareAdditionalRadiusIdx == squareAdditionRadiusSteps.size() - 1) {
			animateToSquareEnded = true;
			cim.clearAddWinkelTmp();
		} else
		if (showSquare && moveToCircle && squareAdditionalRadiusIdx == 0) {
			double nmwdar = Math.PI * 2 / maximumItemsPerSubring;
			cim.clearAddWinkelTmp();
			for (Item item : items) {
				removeItem(item);	
				setWinkelAndLengthAndSubringAndCenter(
						item,
						item.getWinkelCircle(), item.getWinkelCircle(),
						item.getLengthCircle(), item.getLengthCircle(), 
						item.getSubRing(), item.getCenter(), 
						item.getWinkelNr(), 0.1);
			}
    		minimumWinkelDiffAcrossRings = nmwdar;
			showSquare = false;
			moveToCircle = false;
		}

    	return showSquare;
    }

    public Point2D getItemCenterSquared(double winkel, double lengthCircle) {
    	return getItemCenterSquared(winkel, lengthCircle, squareAdditionalRadiusIdx, true);
    }
    
    private Point2D getItemCenterSquared(double winkel, double lengthCircle, int squareAdditionalRadiusIdx, boolean stretch) {
    	return getItemCenterSquaredInternal(winkel, lengthCircle, squareAdditionRadiusSteps.get(squareAdditionalRadiusIdx), stretch, true);
    }

    private Point2D getItemCenterSquaredInternal(double winkel, double lengthCircle, double squareAdditionalRadius, boolean stretch, boolean restretch) {
    	
    	double mf = squareAdditionalRadius / squareMaxAdditionalRadius;
    	double sr = getSquareRadius(lengthCircle, squareAdditionalRadius);
		Point2D center = new Point2D.Double();
		double x = this.center.getX() + Math.cos(winkel) * sr;
		double y = this.center.getY() + Math.sin(winkel) * sr +
				squareAdditionalRadius/2.0/Math.PI
				+ squareRingRadiusBaseOffset * mf / 2;
		center.setLocation(x, y);
		
		if (stretch) {
			center.setLocation(
					(center.getX() - this.center.getX()) * squareStretchX + this.center.getX(), 
					Config.yOffset / 2 + (center.getY() - Config.yOffset / 2) * squareStretchY
			);
			
			if (restretch) {
				Point2D ip1 = getItemCenterSquaredInternal(getSquaredWinkelInternal(Math.PI/2, false, squareAdditionalRadius), Rings.getInstance().getSize() / 2 , squareAdditionalRadius, true, false);
		    	double d = ip1.distance(this.center);
				double f = Rings.getInstance().getSize() / 2.0 / d * (1 + (double) squareAdditionalRadiusIdx / squareAdditionRadiusSteps.size());

				center.setLocation(
						(center.getX() - this.center.getX()) * f + this.center.getX(), 
						(center.getY() - this.center.getY()) * f + this.center.getY() - (1 - f) * Rings.getInstance().getSize()/2);
			}

		}
		return center;
    }

    public double getSquaredWinkel (double winkelCircle) {
    	return getSquaredWinkel(winkelCircle, true);
    }
    
    public double getSquaredWinkel (double winkelCircle, boolean stretchX) {
    	return getSquaredWinkelInternal(winkelCircle, stretchX, squareAdditionRadiusSteps.get(squareAdditionalRadiusIdx));
    }
    
    private double getSquaredWinkelInternal (double winkelCircle, boolean stretchX, double addR) {
    	double diffw2;
		if (winkelCircle > Math.PI * 3.0 / 2.0 || winkelCircle < Math.PI / 2.0) {
			if (winkelCircle < Math.PI / 2) {
				diffw2 = (winkelCircle + Math.PI / 2.0) * squareRingRadiusBase / (squareRingRadiusBase + addR);
			} else {
				diffw2 = (winkelCircle - Math.PI * 1.5) * squareRingRadiusBase / (squareRingRadiusBase + addR);
			}
		} else {
			diffw2 = - (Math.PI * 1.5 - winkelCircle) * squareRingRadiusBase / (squareRingRadiusBase + addR);
		}
		//System.out.println("winkelCircle=" + winkelCircle + ", diffw2=" + diffw2);
		if (stretchX) {
			return Math.PI * 1.5 + diffw2 * squareStretchX;
		} else {
			return Math.PI * 1.5 + diffw2;
		}
    }

    
    public Point2D getCenterSquare() {
    	return centerSquare;
    }
    
    private double getSquareRadius(double posCircle) {
    	return getSquareRadius(posCircle, squareAdditionRadiusSteps.get(squareAdditionalRadiusIdx));
	}
    
    private double getSquareRadius(double posCircle, double additionalRadius) {
    	return 
    			posCircle 
    			+ additionalRadius/Math.PI/2.0
    			- (
    					itemsByRing.get(itemsByRing.size() - 1).get(0).getLengthCircle() 
    					//(Rings.getInstance().getSizes()[0] - Rings.getInstance().getSizes()[1]) / 2
    					- posCircle
				) 
    			* additionalRadius / squareMaxAdditionalRadius
    			//+ squareAdd
    			//+ squareRingRadiusBaseOffset * squareAdditionalRadius / squareMaxAdditionalRadius / 2
				;
				
    	/*
		return squareAdditionalRadius/(Math.PI * 2) + squareRingRadiusBase + squareRingRadiusBaseOffset/2 +
				squareAdditionalRadius / squareMaxAdditionalRadius * squareMinRingRadius / 2
				;
				*/
	}
}
