package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Fractal2 {

	private static final long serialVersionUID = 1L;
	
	List<List<FPoint>> basePoints = new ArrayList<>();
	int basePointsIdx = -1;
	List<Double> pointsSf = new ArrayList<>();
	List<SortedMap<Integer, FPoint>> pointsPattern = new ArrayList<>();
	FPoint[][] pointsPattern2 = new FPoint[0][0];
	int pointsPatternSize1 = 0;
	int[] pointsPatternSize2 = new int[]{};
	
	Double d1 = new Double(1);
	Color background = null;
	FPoint p0 = new FPoint(0, 0, 0, 0, 0, 1);
	
	double sf = 1.0;
	
	double turn = 0.0;
	double turnf = 0.0;
	int jpw = 1;
	int jph = 1;
	double jpw2 = 0.5;
	double jph2 = 0.5;
	
	boolean initialized = false;
	
	List<Double> bpMaxList = new ArrayList<>();
	
	Object repaintSync = new Object();
	
	long pms = 16;
	int reduceImage = 0;
	int nthreads2 = 8;
	BufferedImage bi;
	int[] imageData;
	int[] imageDataClear;

	
	List<Boolean> copyPart = new ArrayList<>();
	List<Thread> threads3 = new ArrayList<>();
	
	List<Thread> threads2 = new ArrayList<>();
	List<Work> work2 = new ArrayList<>();
	int workIdx2;
	int workTBD2;
	
	int wsize2 = 0;
	
	long timeRestriction = 0;
	
    private double fps = 0;
    private double fpsCount = 0;
    private long lastFpsMeasure = 0;
    private Object fpsLock = 0;
    
    private Point2D center = null;
    
    Point2D tp = new Point2D.Double();
    Point2D vp = new Point2D.Double();
    
    KeyListener kl;
    MouseMotionListener mml;
	JPanel jp;
	
	boolean syncFPS = false;
	boolean ending = false;
	boolean ended = false;
	boolean end = false;
	double endSF;
	
	double addBpMax = 0;
	
	Point lastMouseLoc = new Point();
	
	public void doPaint(Graphics2D g2) {
		
		//long t0 = System.currentTimeMillis();
		
		if (!initialized) {
			return;
		}

   		boolean result = g2.drawImage(bi, 0,  0, null);
   		if (syncFPS) {
	   		g2.setColor(new Color(255 - background.getRed(), 255 - background.getGreen(), 255 - background.getBlue()));
	   		g2.drawString("drawing at " + (int) fps + " fps", 20, 20);
   		}
   		
   		
   		g2.setStroke(new BasicStroke(1));
   		g2.setColor(Color.DARK_GRAY);
   		g2.drawOval((int) Math.round(jpw2 + tp.getX()) - 100, (int) Math.round(jph2 + tp.getY()) - 100, 200, 200);
   		
   		if (syncFPS)
        synchronized (fpsLock) {
    		fpsCount++;
    		//fpsLock.notify();
    	}
	}
	
	private void draw(FPoint center) {
		//long tp = System.currentTimeMillis();
		System.arraycopy(imageDataClear, 0, imageData, 0, imageData.length);
		//System.out.println("1:" + (System.currentTimeMillis() - tp));
		//System.out.println("work.size=" + work.size() + "; work2.size=" + work2.size());
		wsize2 = 0;
		workIdx2 = 0;
		timeRestriction = System.currentTimeMillis();
		draw (center, pointsSf.size() - 1, new FPoint());
		//System.out.println("2:" + (System.currentTimeMillis() - tp));
		int workSize2 = 0;
		//long t=System.currentTimeMillis();
		do {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {}
			synchronized (work2) {
				workSize2 =  workTBD2;
			}
			//System.out.println(",work.size=" + workSize2);
		} while (workSize2 > 0);
		//System.out.println("took:" + (System.currentTimeMillis() - t) );
		//t = System.currentTimeMillis();
		synchronized (threads3) {
			for (int i = 0; i < nthreads2; i++) {
				copyPart.set(i, true);
			}
		}
		boolean copying;
		do {
			copying = false;
			for (int i = 0; i < nthreads2 && (!copying); i++) {
				synchronized (threads3) {
					if (copyPart.get(i)) {
						copying = true;
					}
				}
			}
		} while (copying);
   		//bi.setRGB(0, 0, jpw, jph, imageData, 0, jpw);
		//System.out.println("3:" + (System.currentTimeMillis() - t));
	}
	
	private void draw(FPoint center, int idx, FPoint np1) {
		double bpMax = bpMaxList.get((basePointsIdx + idx) % basePoints.size());
		if (idx > 0 || pointsSf.get(idx) > bpMax) {
			double range = pointsSf.get(idx) / bpMax;
    		for (int i = pointsPatternSize2[idx] - 1; i >= 0; i--) {
    			FPoint p = pointsPattern2[idx][i];
    			np1.x = center.x + p.x;
    			double r = jpw + range;
    			if (np1.x < r && np1.x > -r) {
    				np1.y = center.y + p.y;
    				r = jph + range;
    				if (np1.y < r && np1.y > -r) {
	        			/*np1.cCount = center.cCount + p.cCount;
	        			np1.rSum = center.rSum + p.rSum;
	        			np1.gSum = center.gSum + p.gSum;
	        			np1.bSum = center.bSum + p.bSum;*/
    					np1.cCount = center.cCount + 1;
    					np1.rSum = center.rSum + p.rSum/p.cCount;
    					np1.gSum = center.gSum + p.gSum/p.cCount;
    					np1.bSum = center.bSum + p.bSum/p.cCount;
	    				if (idx > 1) {
							synchronized (work2) {
			    				if (wsize2 >= work2.size()) {
			   						work2.add(new Work());
			    				}
			    				work2.get(wsize2++).copyValues(np1, idx - 1);
								workTBD2++;
							}
	    				} else {
	    					drawOnScreen(np1, Math.max(0, idx - 1));
	    				}
    				}
    			}
    		}
		}
	}
		
	private void drawOnScreen(FPoint wnp, int idx) {
		FPoint[] pointsPattern2idx = pointsPattern2[idx];
		for (int i = pointsPatternSize2[idx] - 1; i >= 0; i--) {
			FPoint p = pointsPattern2idx[i];
			int x = (int) Math.round(jpw2 + wnp.x + p.x + tp.getX());
			int y = (int) Math.round(jph2 + wnp.y + p.y + tp.getY());
			int loc = x + y * jpw;
			if (y < jph && x < jpw && x >= 0 && y >= 0 /*&& imageData[loc] == 0*/) {
				double f = wnp.cCount + 1;
				int r = (int) Math.round((wnp.rSum + p.rSum/p.cCount) / f);
				int g = (int) Math.round((wnp.gSum + p.gSum/p.cCount) / f);
				int b = (int) Math.round((wnp.bSum + p.bSum/p.cCount) / f);
				int rgb = r;
				rgb = (rgb << 8) + g;
				rgb = (rgb << 8) + b;
				boolean replace = true;
				if (imageData[loc] != background.getRGB()) {
					/*						int idr = ((16777216 + imageData[loc]) / 65536);
						int idg = (((16777216 + imageData[loc]) / 256 ) % 256);
						int idb = ((16777216 + imageData[loc]) % 256);
*/
					int idr = imageData[loc] % 256;
					int idg = (imageData[loc] / 256) % 256;
					int idb = (imageData[loc] / (256 * 256));
					int sn = (r + g + b) + r * g + r * b + b * g;
					int so = idr + idg + idb + idr * idg + idr * idb + idb * idg;
					if (background == Color.BLACK) {
						replace = sn > so;
					} else
					if (background == Color.WHITE) {
						replace = sn < so;
					} else
					{
						replace = false;
					}
				}
				if (replace) {
					imageData[loc] = rgb;
				}
			}
		}
	}

	public void initFandSC(double initialSF) {
		while (! jp.isVisible()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		sf = 1.01;
		pointsSf.add(new Double(bpMaxList.get(0) * initialSF));
		pointsPattern.add(new TreeMap<>());
		jpw = jp.getWidth();
		jph = jp.getHeight();
		jpw2 = jpw / 2.0;
		jph2 = jph / 2.0;
		center = new Point2D.Double(jpw2, jph2);
		imageData = new int[jpw * jph];
		imageDataClear = new int[jpw * jph];
		int rgb = background.getRed();
		rgb = (rgb << 8) + background.getGreen();
		rgb = (rgb << 8) + background.getBlue();
		for (int i = 0; i < jpw; i++) {
			for (int o = 0; o < jph; o++) {
				imageDataClear[i + o * jpw] = rgb;
			}
		}
		bi = //g.getDeviceConfiguration().createCompatibleVolatileImage((int) Math.round(turnb), (int) Math.round(turnb));
		new BufferedImage(jpw, jph, BufferedImage.TYPE_INT_RGB);
		
		for (int i = 0; i < nthreads2; i++) {
			final int part = i;
			copyPart.add(false);
			Thread thread = new Thread() {
				int i = part;
				@Override
				public void run() {
					while (!ended) {
						boolean copy;
						try {
							Thread.sleep(2);
							synchronized (threads3) {
								copy = copyPart.get(i);
							}
							if (copy) {
								bi.setRGB(0, (jph/nthreads2)*i, jpw, (jph/nthreads2), imageData, (jph/nthreads2)*i*jpw, jpw);
								synchronized (threads3) {
									copyPart.set(i, false);
								}
							}
						} catch (InterruptedException e) {}
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
			threads3.add(thread);
		}
		for (int i = 0; i < nthreads2; i++) {
			Thread thread = new Thread() {
				public void run() {
					int oldWorkIdx = 0;
					int newWorkIdx = 0;
					FPoint np1 = new FPoint();
					while (!ended) {
						{
							int d;
							synchronized (work2) {
								workTBD2 -= (newWorkIdx - oldWorkIdx);
								d = wsize2 - workIdx2;
								if (d > 0) {
									oldWorkIdx = workIdx2;
									workIdx2 += Math.min(25, d);
									newWorkIdx = workIdx2;
								}
							}
							if (d <= 0) {
								newWorkIdx = 0;
								oldWorkIdx = 0;
								try {
									//System.out.println("not working:" + this);
									Thread.sleep(0, 5000);
								} catch (InterruptedException e) {}
							} else {
								//System.out.println("working:" + this);
								for (int o = oldWorkIdx; o < newWorkIdx; o++) {
									Work w = work2.get(o);
									draw (w.np, w.idx, np1);
								}
							}
						} 
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
			threads2.add(thread);
		}

		mml = new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent me) {
				lastMouseLoc.setLocation(me.getPoint());
				Point e = new Point();
				e.setLocation(me.getX() - tp.getX(), me.getY() - tp.getY());
				double r = e.distance(new Point2D.Double(jpw2, jph2));
				if (
						r > 100 && 
						e.getX() >= 0 &&
						e.getY() >= 0 &&
						e.getX() <= jpw &&
						e.getY() <= jph
				) {
					double angle = getAngle(center, e);
					sf = 1 + (e.getY() - jph2 - Math.sin(angle) * 100) / jph2 * 0.5;
					turnf = (e.getX() - jpw2 - Math.cos(angle) * 100) / jpw2 * 0.3;
				} else {
					sf = 1;
					turnf = 0;
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				tp.setLocation(
						Math.max(Math.min(tp.getX() + e.getX() - lastMouseLoc.getX(), jpw2), -jpw2),
						Math.max(Math.min(tp.getY() + e.getY() - lastMouseLoc.getY(), jph2), -jph2)
				);
				//lastMouseLoc.setLocation(e.getPoint());
				mouseMoved(e);
			}
		};
		jp.addMouseMotionListener(mml);
		
		kl = new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				double step = 0.001;
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					vp.setLocation(Math.min(vp.getX() + step, 1), vp.getY());
				} else
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					vp.setLocation(Math.max(vp.getX() - step, 0), vp.getY());
				} else
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					vp.setLocation(vp.getX(),  Math.max(vp.getY() - step, 0));
				} else
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					vp.setLocation(vp.getX(), Math.min(vp.getY() + step,  1));
				}
			}
		};
		jp.getTopLevelAncestor().addKeyListener(kl);
		
		initialized = true;
		
		try {
			Robot r = new Robot();
			for (int i = 0; i < 10; i++) {
				r.mouseMove((int) (jpw2 + 120 + i), (int) (jph2 + 70 + i));
				Thread.sleep(10);
			}
			//System.out.println("x=" + jpw2 + ", y=" + jph2);
			//new Robot().mouseMove(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void removeListenersAndThreads(double endSF) {
		jp.removeMouseMotionListener(mml);
		jp.getTopLevelAncestor().removeKeyListener(kl);
		this.endSF = endSF;
		if (turnf == 0) {
			turnf = Double.MIN_VALUE;
		}
		if (sf == 0) {
			sf = 0.1;
		}
		ending = true;
		ended = false;
	}
	
	public void setAddBpMax(double addBpMax) {
		this.addBpMax = addBpMax;
	}

	public Fractal2(JPanel jp) {

		JFrame frame;
		if (jp == null) {
			syncFPS = true;
			frame = new JFrame();
			frame.setUndecorated(true);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setTitle("Technology Radar");
			frame.setResizable(true);
			frame.setPreferredSize(new Dimension(frame.getMaximumSize().width, frame.getMaximumSize().height));
			Container conFrame = frame.getContentPane();
			jp = new JPanel() {

				static final long serialVersionUID = 0;

				@Override
				public void paint(Graphics g) {
					doPaint((Graphics2D) g);
				}
			};
			conFrame.add(jp);
			frame.pack();
			frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			frame.setVisible(true);
		}
		
		this.jp = jp;

	}
	
	public void runFractal() {
		end = false;
		ended = false;
		ending = false;
		Thread thread = new Thread() {
			public void run() {
				long t = 0;
				while (!ended) {
					try {
	    				if (System.currentTimeMillis() - lastFpsMeasure >= 1000) {
	    					fps = fpsCount;
	    					lastFpsMeasure = System.currentTimeMillis();
	    					fpsCount = 0;
	    				}
						long diff = pms - (System.currentTimeMillis() - t);
						if (diff > 0) {
							Thread.sleep(diff);
						}
						t = System.currentTimeMillis();
						if (! ending || turn != 0) {
							turn = (turn + turnf + Math.PI * 2) % (Math.PI * 2);
							if (ending) {
								turnf = Math.max(Math.abs(turnf) * 1.15, Math.PI / 30) * Math.signum(turnf);
								if (
										turn <= Math.abs(turnf) ||
										(2 * Math.PI - turn) <= Math.abs(turnf)
								) {
									turn = 0;
								}
							}
						}

						diveIn();
						//System.out.println("a:" + (System.currentTimeMillis() - t));
			       		draw(p0);
						//System.out.println("b:" + (System.currentTimeMillis() - t));
						jp.repaint();
						//System.out.println("c:" + (System.currentTimeMillis() - t));
	    				/*synchronized (fpsLock) {
	    					try {
	    						fpsLock.wait(pms / 2);
	    					} catch (InterruptedException e) {}
	    				}*/
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
					if (ending) {
						if (turn == 0) {
							if (pointsSf.get(pointsSf.size() - 1) <= endSF) {
								ended = true;
								for (Thread thread : threads2) {
									synchronized (thread) {
										try {
											thread.join();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
								end = true;
							}
						}
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	public boolean isEnd() {
		return end;
	}
	
	
	private void diveIn() {
		if (ending) {
			sf *= 0.98;
		}
		if (!ending || pointsSf.get(pointsSf.size() - 1) > endSF) {
			for (int i = pointsSf.size() - 1; i >= 0; i--) {
				pointsSf.set(i, pointsSf.get(i) * sf);
			}
			double ub = 2 * Math.max(jpw, jph) * (bpMaxList.get((basePointsIdx + pointsSf.size() - 1) % basePoints.size()) + addBpMax);
			if (sf >= 1) {
				if (pointsSf.get(0) > bpMaxList.get(basePointsIdx) + addBpMax) {
					pointsSf.add(0, new Double(pointsSf.get(0) / (bpMaxList.get(basePointsIdx) + addBpMax)));
					basePointsIdx = (basePoints.size() + basePointsIdx - 1) % basePoints.size();
				}
				if (pointsSf.get(pointsSf.size() - 1) > ub) {
					pointsSf.remove(pointsSf.size() - 1);
				}
			} else {
				if (pointsSf.get(0) < 1) {
					pointsSf.remove(0);
					basePointsIdx = (basePointsIdx + 1) % basePoints.size();
				}
				if (! ending) {
					if (pointsSf.get(pointsSf.size() - 1) < ub) {
						pointsSf.add(new Double(pointsSf.get(pointsSf.size() - 1) * (bpMaxList.get((basePointsIdx + pointsSf.size()) % basePoints.size()) + addBpMax)));
					}
				}
			}
			if (pointsSf.size() > pointsPattern.size()) {
				pointsPattern.add(new TreeMap<>());
			}
		}
		
		
		double cos = Math.cos(turn);
		double sin = Math.sin(turn);

		int searchFP = 0;
    	for (int i = pointsSf.size() - 1; i >= 0; i--) {
    		Double pf = pointsSf.get(i);
    		SortedMap<Integer, FPoint> ss = pointsPattern.get(i);
    		List<FPoint> l = new ArrayList<>(ss.values());
    		ss.clear();
        	for (FPoint p : basePoints.get((basePointsIdx + i) % basePoints.size())) {
        		double px = ((p.x + vp.getX()) * cos + (p.y + vp.getY()) * sin) * pf;
        		double py = (-(p.x + vp.getX()) * sin + (p.y + vp.getY()) * cos) * pf;
        		searchFP = (int) Math.round(px + 0.5) + (int) Math.round(py + 0.5) * jpw;
        		FPoint fpoint2 = ss.get(searchFP);
        		if (fpoint2 == null) {
        			if (l.size() > 0) {
        				fpoint2 = l.remove(l.size() - 1);
        				fpoint2.cCount = 0;
        				fpoint2.rSum = 0;
        				fpoint2.gSum = 0;
        				fpoint2.bSum = 0;
        			} else {
        				fpoint2 = new FPoint();
        			}
        			fpoint2.x = px;
        			fpoint2.y = py;
            		ss.put(searchFP, fpoint2);
        		}
        		fpoint2.cCount += p.cCount;
        		fpoint2.rSum += p.rSum;
        		fpoint2.gSum += p.gSum;
        		fpoint2.bSum += p.bSum;
        	}
    	}
    	
    	if (pointsPattern2.length < pointsSf.size()) {
    		pointsPattern2 = new FPoint[pointsSf.size()][0];
    	}
    	
    	for (int i = 0; i < pointsSf.size(); i++) {
    		if (pointsPattern.get(i).size() > pointsPattern2.length) {
    			pointsPattern2[i] = new FPoint[pointsPattern.get(i).size()];
    		}
    	}
    	
    	//pointsPattern2 = new double[pointsSf.size()][][];
    	pointsPatternSize1 = pointsSf.size();
    	pointsPatternSize2 = new int[pointsSf.size()];
    	for (int i = 0; i < pointsSf.size(); i++) {
    		pointsPattern2[i] = new FPoint[pointsPattern.get(i).size()];
    		SortedMap<Integer, FPoint> sm = pointsPattern.get(i);
    		int o = 0;
    		for (FPoint p : sm.values()) {
    			pointsPattern2[i][o++] = new FPoint(p.x, p.y, p.cCount, p.rSum, p.gSum, p.bSum);
    		}
    		pointsPatternSize2[i] = sm.size();
    	}
	}
	
	private void loadCharacters(String string) {
		background = Color.BLACK;
		for (char ch : string.toCharArray()) {
			BufferedImage b = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) b.getGraphics();
			g.setBackground(Color.BLACK);
			g.setColor(Color.WHITE);
			g.setFont(new Font(b.getGraphics().getFont().getFontName(), Font.PLAIN, 12));
			g.drawString(String.valueOf(ch), 50, 50);
			Color[][] c = ImageUtil.loadPixelsFromImage(b);
			processImage(c, g.getBackground(), 0);
		}
	}
	
	private void loadStrings(String[] strings) {
		background = Color.BLACK;
		for (String s : strings) {
			BufferedImage b = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) b.getGraphics();
			g.setBackground(Color.BLACK);
			g.setColor(Color.WHITE);
			g.setFont(new Font(b.getGraphics().getFont().getFontName(), Font.PLAIN, 12));
			g.drawString(s, 50, 50);
			Color[][] c = ImageUtil.loadPixelsFromImage(b);
			processImage(c, g.getBackground(), 0);
		}
	}

	private void loadImage(String s, Color bg) throws IOException {
		loadImage(s, bg, false);
	}

	private void loadImage(String s, Color bg, boolean invertBg) throws IOException {
		loadImage(s, bg, 10, invertBg);
	}

	private void loadImage(String s, Color bg, int tolerance, boolean invertBg) throws IOException {
		background = bg;
		Color[][] c = ImageUtil.loadPixelsFromImage(new File(s));
		System.out.println("image " + s + ", size=[" + c.length + "][" + c[0].length + "]");
		processImage(c, bg, tolerance);
		if (invertBg) {
			if (background == Color.WHITE) {
				background = Color.BLACK;
			} else
			if (background == Color.BLACK) {
				background = Color.WHITE;
			}
		}
	}

	public void processImage(Color[][] c, Color bg, int rgbTolerance) {
		if (background == null) {
			background = bg;
		}
		basePointsIdx++;
		basePoints.add(new ArrayList<>());
		pointsSf.clear();
		double found = 0;
		double total = 0;
		double xmin = Double.MAX_VALUE;
		double xmax = -Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double ymax = -Double.MAX_VALUE;
		for (int i = 0; i < c.length; i++) {
			for (int o = 0; o < c[i].length; o++) {
				if (
						!closeColor(c[i][o], bg, rgbTolerance)
				) {
					xmin = Math.min(i, xmin);
					xmax = Math.max(i, xmax);
					ymin = Math.min(o, ymin);
					ymax = Math.max(o, ymax);
					boolean skip = false;
					if (i > 1 && i < c.length - 2 && o > 1 && o < c[i].length - 2 && reduceImage > 0) {
						skip = 
								!closeColor(c[i-1][o-1], bg, rgbTolerance) &&
								!closeColor(c[i-1][o+0], bg, rgbTolerance) &&
								!closeColor(c[i-1][o+1], bg, rgbTolerance) &&
								!closeColor(c[i-0][o-1], bg, rgbTolerance) &&
								!closeColor(c[i-0][o+1], bg, rgbTolerance) &&
								!closeColor(c[i+1][o-1], bg, rgbTolerance) &&
								!closeColor(c[i+1][o+0], bg, rgbTolerance) &&
								!closeColor(c[i+1][o+1], bg, rgbTolerance);
						if (skip && reduceImage > 1) {
							skip = skip &&
								!closeColor(c[i-2][o-2], bg, rgbTolerance) &&
								!closeColor(c[i-2][o-1], bg, rgbTolerance) &&
								!closeColor(c[i-2][o+0], bg, rgbTolerance) &&
								!closeColor(c[i-2][o+1], bg, rgbTolerance) &&
								!closeColor(c[i-2][o+2], bg, rgbTolerance) &&
								!closeColor(c[i-1][o+2], bg, rgbTolerance) &&
								!closeColor(c[i+0][o+2], bg, rgbTolerance) &&
								!closeColor(c[i+1][o+2], bg, rgbTolerance) &&
								!closeColor(c[i+2][o+2], bg, rgbTolerance) &&
								!closeColor(c[i+2][o+1], bg, rgbTolerance) &&
								!closeColor(c[i+2][o+0], bg, rgbTolerance) &&
								!closeColor(c[i+2][o-1], bg, rgbTolerance) &&
								!closeColor(c[i+2][o-2], bg, rgbTolerance) &&
								!closeColor(c[i+1][o-2], bg, rgbTolerance) &&
								!closeColor(c[i+0][o-2], bg, rgbTolerance) &&
								!closeColor(c[i-1][o-2], bg, rgbTolerance);
						}
					}
					if (!skip) {
						FPoint p = new FPoint(i, o, 0, 0, 0, 0);
						basePoints.get(basePointsIdx).add(p);
						p.cCount++;
						p.rSum += c[i][o].getRed();
						p.gSum += c[i][o].getGreen();
						p.bSum += c[i][o].getBlue();
						found++;
					}
				}
				total++;
			}
		}
		bpMaxList.add(Math.max(xmax - xmin, ymax - ymin));
		
		for (FPoint p : basePoints.get(basePointsIdx)) {
			p.x = (p.x - xmin - (xmax - xmin) / 2) / bpMaxList.get(bpMaxList.size() - 1);
			p.y = (p.y - ymin - (ymax - ymin) / 2) / bpMaxList.get(bpMaxList.size() - 1);
		}

		// set a point in the middle
		FPoint minDiffCenter = new FPoint(0, 0, 0, 0, 0, 0);
		double diff = Double.MAX_VALUE;
		FPoint center = new FPoint(0, 0, 0, 0, 0, 0);
		for (FPoint p : basePoints.get(basePointsIdx)) {
			double d = Math.sqrt(Math.pow(center.x - p.x, 2) + Math.pow(center.y - p.y, 2));
			if (d < diff) {
				minDiffCenter.x = p.x;
				minDiffCenter.y = p.y;
				diff = d;
			}
		}
		for (FPoint p : basePoints.get(basePointsIdx)) {
			p.x = p.x - minDiffCenter.x;
			p.y = p.y - minDiffCenter.y;
		}
		
		System.out.println("ratio = " + found + " / " + total + ", bpMax=" + bpMaxList.get(bpMaxList.size() - 1));
	}
	
	private boolean closeColor(Color c1, Color c2, int tolerance) {
		return Math.sqrt(Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2) + Math.pow(c1.getBlue() - c2.getBlue(), 2)) <= tolerance;
	}
	
	public static double getAngle(Point2D p1, Point2D p2)
	{       
	    double dx = p2.getX() - p1.getX();
	    double dy = p2.getY() - p1.getY();
	    double adx = Math.abs(dx);
	    double ady = Math.abs(dy);
	    
	    if (dy == 0 && dx == 0)
	    {
	        return 0;
	    }
	    else if (dy == 0 && dx > 0)
	    {
	        return 0;      
	    }
	    else if (dy == 0 && dx < 0)
	    {
	        return Math.PI;
	    }
	    else if (dy > 0 && dx == 0)
	    {
	        return Math.PI * 0.5;
	    }
	    else if (dy < 0 && dx == 0)
	    {
	        return Math.PI * 1.5;
	    }
	    
	    double rwinkel = Math.atan(ady /adx);  
	        
	    if (dx>0 && dy>0) 
	    {
	        //rwinkel = rwinkel;
	    }
	    else if (dx<0 && dy>0)  
	    {
	    	rwinkel = Math.PI - rwinkel;
	    }
	    else if (dx>0 && dy<0) 
	    {
	    	rwinkel = -rwinkel;
	    }
	    else if (dx<0 && dy<0)  
	    {
	    	rwinkel = Math.PI + rwinkel;
	    }
	    
	    return rwinkel;
	}
	
	public boolean isEnded() {
		return ended;
	}
	
	public boolean isEnding() {
		return ending;
	}


	public static void main(String[] args) {
		Fractal2 f = new Fractal2(null);
		f.setAddBpMax(1);
		//f.setVisible(true);
		try {
			//f.loadImage("C:\\Users\\jg\\Downloads\\p5.bmp", Color.BLACK, 30, false);
			//f.loadImage("C:\\Users\\jg\\Downloads\\p4.bmp", Color.BLACK);
			//f.loadImage("C:\\Users\\jg\\Downloads\\p3.bmp", Color.BLACK);
			//f.loadImage("C:\\Users\\jg\\Downloads\\p2.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\p1.bmp");
			//f.loadCharacters("0123456789");
			//f.loadCharacters("+-+-");
			//f.loadStrings(new String[]{"Novatec", "Beratung", "Software Entwicklung", "Cloud", "IoT", "Blockchain"});
			//f.loadImage("C:\\Users\\jg\\Downloads\\complex.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\kotlin_250x250_3.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\kotlin_250x250_2.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\kotlin_250x250.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\squareOutline2.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\biggerSquareOutline.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\biggerSquareFilled.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\biggerSquare.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\smallSquare.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\hex.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\Leonie2.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\Lilian.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\Novatec3.bmp", Color.WHITE, true);
			
			//f.loadImage("C:\\Users\\jg\\Downloads\\pflanze.bmp", Color.WHITE, true);
			//f.loadStrings(new String[]{"Innovation"});

			/*
			f.loadStrings(new String[]{"Beratung"});
			f.loadImage("C:\\Users\\jg\\Downloads\\Novatec3.bmp", Color.WHITE, true);
			f.loadStrings(new String[]{"Entwicklung"});
			f.loadImage("C:\\Users\\jg\\Downloads\\csquare.bmp", Color.WHITE, true);
			f.loadStrings(new String[]{"Machine Learning"});
			f.loadImage("C:\\Users\\jg\\Downloads\\Novatec.bmp", Color.WHITE, true);
			f.loadStrings(new String[]{"Cloud"});
			f.loadImage("C:\\Users\\jg\\Downloads\\Novatec2.bmp", Color.WHITE, true);
			*/
			/*
			f.loadImage("C:\\Users\\jg\\Downloads\\csquare.bmp", Color.WHITE, true);
			f.loadImage("C:\\Users\\jg\\Downloads\\csquare.bmp", Color.WHITE, true);
			f.loadImage("C:\\Users\\jg\\Downloads\\csquare.bmp", Color.WHITE, true);
			f.loadImage("C:\\Users\\jg\\Downloads\\csquare.bmp", Color.WHITE, true);
			f.loadImage("C:\\Users\\jg\\Downloads\\csquare.bmp", Color.WHITE, true);
			*/
			//f.loadImage("C:\\Users\\jg\\Downloads\\cross.bmp", Color.WHITE, false);
			//f.loadImage("C:\\Users\\jg\\Downloads\\flag3.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\flag2.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\flag.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\flag4.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\flag5.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\flag6.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\square3.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\square4.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\Novatec3.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\Novatec2.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\Novatec.bmp", Color.WHITE, true);
			//f.loadImage("C:\\Users\\jg\\Downloads\\Smiley.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\triangle.bmp");
			//f.loadImage("C:\\Users\\jg\\Downloads\\heart.bmp", Color.WHITE);
			f.initFandSC(1.0);
			f.runFractal();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		if (args != null) {
			if (args[0].equals("special")) {
				try {
					final Robot r = new Robot();
					while (true) {
						Thread.sleep(1000);
						Rectangle sr = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
						BufferedImage sfi = r.createScreenCapture(sr);
						Color c = new Color(sfi.getRGB(sr.width - 1, sr.height - 1));
						if (c.getRed() == 0 && c.getRed() == 0 && c.getGreen() == 255) {
							System.out.println("animate...");
							for (int i = 1; i < 50; i++) {
								for (int o = 1; o < 50; o++) {
									sfi.setRGB(sr.width - i, sr.height - o, Color.WHITE.getRGB());
								}
							}
							f.processImage(ImageUtil.loadPixelsFromImage(sfi), Color.WHITE, 10);
							f.initFandSC(1.0);
							f.runFractal();
							((Window) f.jp.getTopLevelAncestor()).toFront();
							break;
						} else {
							System.out.println("Color (" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")");
						}
					}
					f.jp.getTopLevelAncestor().addKeyListener(new KeyListener() {
						
						@Override
						public void keyTyped(KeyEvent e) {
							//((Window) f.jp.getTopLevelAncestor()).toBack();
							r.keyPress(KeyEvent.VK_ALT);
							r.keyPress(KeyEvent.VK_TAB);
							r.delay(500);
							r.keyRelease(KeyEvent.VK_TAB);
							r.keyRelease(KeyEvent.VK_ALT);
							System.exit(0);
						}
						
						@Override
						public void keyReleased(KeyEvent e) {
						}
						
						@Override
						public void keyPressed(KeyEvent e) {
						}
					});
				} catch (Exception e) {
				}
			}
		}
	}

}
