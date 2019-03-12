package radar;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import radar.item.Cluster;
import radar.item.ClusterInfo;
import radar.item.Item;
import radar.item.Items;
import radar.item.Util;
import radar.net.RoundGrid;
import radar.ring.Rings;
import test.Fractal2;

public class DesktopDemo extends JFrame {
	
	static final long serialVersionUID = 0;
	
    private int lineSize = 3;
    
    private double radarLine = 0;
    
    private boolean showRadar = true;
    private boolean showClusterNames = true;
    private boolean showPolygonLines = false;
    private boolean showSquare = false;
    private boolean moveToCircle = false;
    
    private boolean changeColors = true;

    
    private static final String PLACEHOLDER = "placeholder";
    
    private boolean play = false;
    
    private String colorKey = "f3";
    private long lastColorChange = System.currentTimeMillis();
    
    private Color bg = Color.BLACK;
    private Color fg = Color.WHITE;
    
    private Image novatecWhite;
    private Image novatecBlack;
    
    private int clusterIndex = 0;
    private boolean simplifyArcs = true;
    
    private Point2D mousePressedPoint = new Point2D.Double();
    private double versatz = 0;
    
    private int optSpeed = Config.optimizationSpeed;
    
    private char lastKey = 0;
    private long lastKeyAction = 0;
    
    private boolean graphicsInitialized = false;
    private boolean printUsage = true;
    
    private String mouseInCluster = null;
    private Stack<List<Item>> itemStack = new Stack<>();
    private Stack<String> itemPath = new Stack<>();
    
    private double fps = 0;
    private double fpsCount = 0;
    private long lastFpsMeasure = 0;
    private Object fpsLock = 0;
    
    private List<String> texts = new ArrayList<>();
    
    private Object itsLock = new Object();
	private Items its = Items.getInstance();
	
	private boolean showScreenCapture = false;
	private Fractal2 fractal = null;
	private int screenCaptureScale = 4;
	
	private boolean searchTyping = false;
	private StringBuilder searchString = new StringBuilder();
    
    public static DesktopDemo instance;
    
    /**
     * Creates new form DesktopDemo
     */
    public DesktopDemo() {
    	instance = this;
    	init(DesktopDemo.class.getClassLoader().getResourceAsStream("sampleData.txt"));
        initComponents();
        initAfterGraphics(true);
        runRadarAndRampUpAndDegree();
        runRadarAndRampUpAndDegree1();
        runRadarAndRampUpAndDegree2();
        runRadarAndRampUpAndDegree3();
        //runRadarAndRampUpAndDegree4();
    }
    
    private void initAfterGraphics(boolean clusterChange) {
        Items.getInstance().initGraphics((Graphics2D) getGraphics(), lineSize); 
		Items.getInstance().setCenter(new Point2D.Double(jp.getSize().getWidth()/2, jp.getSize().getHeight()/2), lineSize);
        Items.getInstance().calculateItemPositions(clusterChange);
        if (isVisible()) {
        	Items.getInstance().setDegrees(MouseInfo.getPointerInfo().getLocation(), jp.getLocationOnScreen());
        }
        Items.getInstance().animateToSquareInitSteps();
    }

    private void init(InputStream is) {
    	BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(is));
	        initItems(br);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void init(String filename) {
    	BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(filename)));
	        initItems(br);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    private void runRadarAndRampUpAndDegree3() {
    	Thread thread = new Thread() {
    		public void run() {
    			long t = 0;
	    		while (true) {
	    			try {
	    				if (System.currentTimeMillis() - lastFpsMeasure >= 1000) {
	    					fps = fpsCount;
	    					lastFpsMeasure = System.currentTimeMillis();
	    					fpsCount = 0;
	    				}
	    				long diff = Config.paintWait - (System.currentTimeMillis() - t);
	    				if (diff > 0) {
	    					//System.out.println("diff=" + diff);
	    					Thread.sleep(Math.max(0, Config.paintWait - (System.currentTimeMillis() - t)));
	    				}
	    				t = System.currentTimeMillis();
	    				if (!showScreenCapture) {
		    				Items its2 = Items.getInstance().getCopy();
		    				synchronized (itsLock) {
		    					its = its2;
			    				}
		    				repaint();
		    				/*synchronized (fpsLock) {
		    					try {
		    						fpsLock.wait(Config.paintWait);
		    					} catch (InterruptedException e) {}
		    				}*/
	    				} else {
	    					if (fractal != null && fractal.isEnded()) {
	    						showScreenCapture = false;
	    						while (! fractal.isEnd()) {
	    							Thread.sleep(10);
	    						}
	    						fractal = null;
	    					}
	    				}
	    			} catch (Exception e) {
	    				e.printStackTrace(System.out);
	    			}
	    		}
    		}
    	};
    	thread.setDaemon(true);
    	thread.start();
    }

    
    private void runRadarAndRampUpAndDegree() {
    	Thread thread = new Thread() {
    		public void run() {
	    		while (true) {
	    			try {
	    				Thread.sleep(5);
	    				Items.getInstance().rampUpArcs();
	    				if (changeColors) {
	    					changeColors();
	    				}
	    				keyPaint();
	    				Items.getInstance().dimmText();
	    				Items.getInstance().dimmPolygons();
	    				Items.getInstance().setDegrees();
	    			} catch (Exception e) {
	    				e.printStackTrace(System.out);
	    			}
	    		}
    		}
    	};
    	thread.setDaemon(true);
    	thread.start();
    }
    
    private void runRadarAndRampUpAndDegree1() {
    	Thread thread = new Thread() {
    		public void run() {
	    		while (true) {
	    			try {
	    				Thread.sleep(10);
	    				if (!showScreenCapture) {
		    				if (showRadar) {
		    					//if (!Items.getInstance().isShowSquareIsAnimating()) {
		    						radarLine = (radarLine + 0.01) % (2 * Math.PI);
		    					/*} else {
		    						radarLine = (radarLine + 0.001) % (2 * Math.PI);
		    					}*/
		    				}
	    		        	Items.getInstance().calculateItemPositions2(showRadar, radarLine);
	    				}
	    			} catch (Exception e) {
	    				e.printStackTrace(System.out);
	    			}
	    		}
    		}
    	};
    	thread.setDaemon(true);
    	thread.start();
    }

    private void runRadarAndRampUpAndDegree2() {
    	Thread thread = new Thread() {
    		public void run() {
	    		while (true) {
	    			try {
	    				Thread.sleep(5);
	    				if (!showScreenCapture) {
		    				Items.getInstance().novaAnimateVersatz();
		    				if (Items.getInstance().animateToSquare(moveToCircle)) {
		    				} else {
		    					showSquare = false;
		    					moveToCircle = false;
		    				}
		    				Items.getInstance().animateStarWars();
		    				Items.getInstance().stepToTargetLocation();
		    				Items.getInstance().animateChange();
		    				Items.getInstance().animateCloser();
		    				if (Items.getInstance().animateBigBang()) {
		    					Rings.getInstance().bigBangAnimate(
		    							Items.getInstance().getNovaDimm(),
		    							Items.getInstance().getCenter()
		    					);
		    				}
		    				Items.getInstance().novaAnimate();
		    				Items.getInstance().optimize(optSpeed);
	    				}
	    			} catch (Exception e) {
	    				e.printStackTrace(System.out);
	    			}
	    		}
    		}
    	};
    	thread.setDaemon(true);
    	thread.start();
    }
    /*
    private void runRadarAndRampUpAndDegree4() {
    	Thread thread = new Thread() {
    		public void run() {
	    		while (true) {
	    			try {
	    				Thread.sleep(10);
	    				if (
	    						Items.getInstance().getNovaDimm() > 0 || 
	    						Items.getInstance().isShowCloserAnimation() ||
	    						Items.getInstance().isShowCloserAnimation2()
	    				) {
	    					//Items.getInstance().calculatePolygons();
	    				}
	    			} catch (Exception e) {
	    				e.printStackTrace(System.out);
	    			}
	    		}
    		}
    	};
    	thread.setDaemon(true);
    	thread.start();
    }
    */


    
    private void changeColors() {
    	long t = System.currentTimeMillis();
    	if (t - lastColorChange > 500) {
        	colorKey = Config.getNextKey(colorKey);
        	lastColorChange = t;
    	}
    }
    /*
	if (name.equals("Größe (0-4)") || name.equals("Percentage")) {
		return value;
	}*/
    
    private boolean anonymizeHeader(String name) {
    	if (
    			name.equals("Größe (0-4)") || 
    			name.equals("Percentage") ||
    			name.equals("Ring")
    	) {
    		return false;
    	}
    	return true;
    }

    
    /*
    private Map<String, String> anonymizedHeaders = new HashMap<>();
    private String getAnonymizedHeader(String name) {
    	if (! anonymizeHeader(name)) {
    		return name;
    	}
		String anonymizedHeader = anonymizedHeaders.get(name);
		if (anonymizedHeader == null) {
    		for (Cluster cluster : Cluster.values()) {
    			if (cluster.getRawColumn().equals(name)) {
    				anonymizedHeader = cluster.getRawColumn();
    				anonymizedHeaders.put(name, anonymizedHeader);
    				break;
    			}
    		}
    		// create one
    		if (anonymizedHeader == null) {
    			anonymizedHeader = "tag " + Cluster.values().length;
    			anonymizedHeaders.put(name, anonymizedHeader);
    		}
		}
		return anonymizedHeader;
    }*/
    
    private String getValue(String name, String defaultStr, String[] tokens, String[] headers) {
    	for (int i = 0; i < headers.length; i++) {
    		if (
    				name.equals(headers[i])
    		) {
    			if (i >= tokens.length) {
    				return defaultStr;
    			} else {
    				return tokens[i];
    			}
    		}
    	}
    	return defaultStr;
    }
    
    private Map<String, Map<String, String>> anonymizedValues = new HashMap<>();
    private String getAnonymizedValue(String name, String value, String pfx) {
    	if (! anonymizeHeader(name)) {
    		return value;
    	}
    	Map<String, String> m = anonymizedValues.get(name);
    	if (m == null) {
    		m = new HashMap<>();
    		anonymizedValues.put(name, m);
    	}
    	String anonymizedValue = m.get(value);
    	if (anonymizedValue == null) {
    		anonymizedValue = pfx + "-" + (m.size() + 1);
    		m.put(value, anonymizedValue);
    	}
    	return anonymizedValue;
    }
    
    private synchronized void initItems(BufferedReader r) {
    	String line = null;
        Cluster.setAnonymize(false);
        try {
        	Items.resetTmpInstance();
        	line = r.readLine();
        	String[] headers = line.split("\t");
        	while ((line = r.readLine()) != null) {
        		System.out.println(line);
        		// Name	Größe (0-4)	Percentage	Moving	Ring	Kategorie	Topic	CA	Kommentar
        		
        		String[] p = line.split("\t");
        		int ring = -1;
        		
        		String deleted = getValue("Deleted", "", p, headers).trim();
    			String dup = getValue("Dup", "", p, headers);

        		if (deleted.length() == 0 && dup.length() == 0) {
	        		String ringText = getValue("Ring", null, p, headers);
	    			for (int i = 0; i < Config.texts.length; i++) {
						if (ringText.equals(Config.texts[i])) {
							ring = i;
						}
	    			}
	    			
	    			Map<Cluster, String> values = new HashMap<>();
	    			
	    			for (Cluster cluster : Cluster.values()) {
	    				values.put(cluster, getValue(cluster.getColumn(), "default", p, headers));
	    			}
	        		
	    			String category = getValue(Cluster.CATEGORY.getColumn(), null, p, headers);
	    			if (category == null) {
	    				System.out.println("oops");
	    			}
	        		if (category.equals(PLACEHOLDER)) {
	        			category = PLACEHOLDER;
	        			Items.getTmpInstance().create (ring);
	        		} else {
	        			String name = getValue("Name", null, p, headers);
	        			int groesse = Integer.parseInt(getValue("Größe (0-4)", "2", p, headers));
	        			int percentage = Integer.parseInt(getValue("Percentage", "100", p, headers));
	        			if (Config.anonymized) {
	        				name = getAnonymizedValue("Itemname", name, "item");
	        				Map<Cluster, String> values2 = new HashMap<>();
	        				for (Entry<Cluster, String> e : values.entrySet()) {
	        					values2.put(e.getKey(), getAnonymizedValue(e.getKey().getRawColumn(), e.getValue(), "group"));
	        				}
	        				values = values2;
	        			}
	        			Items.getTmpInstance().create(
		        				ring, 
		        				name, 
		        				groesse,
		        				percentage,
		        				values
		        		);
	        		}
        		}
        	}
        	r.close();
        } catch (Exception e) {
        	System.out.println("line:" + line);
        	e.printStackTrace(System.out);
        }
        Cluster.setAnonymize(Config.anonymized);
        switchTmpItems();
        System.out.println("items read");
    }
    
    private void switchTmpItems() {
        Items.switchInstance();
        Items.getInstance().setClusterBy(Cluster.values()[clusterIndex]);
        if (graphicsInitialized) {
        	initAfterGraphics(true);
        }
    }

    // "C:\\Work7\\New Technologies\\workspace\\TR\\src\\Items.txt"
    public static void main(String args[]) {

    	/*
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        UIManager.put("swing.boldMetal", Boolean.FALSE);
		*/
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	String filename = args != null && args.length > 0 ? args[0] : null;
            	DesktopDemo dd = new DesktopDemo();
            	dd.setVisible(true);
            	/*if (filename == null) {
            		dd.fileDialog();
            	}*/
            	if (filename != null) {
            		dd.init(filename);
            	}
            }
        });
    }
    
    public void fileDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	String filename = null;
        		FileDialog fd = new FileDialog(DesktopDemo.this, "Choose a file", FileDialog.LOAD);
        		fd.setMultipleMode(false);
        		fd.setVisible(true);
        		if (fd.getFiles() != null && fd.getFiles().length == 1) {
            		filename = fd.getFiles()[0].getAbsolutePath();
        		}
        		if (filename == null) {
        		  System.out.println("You cancelled the choice. Exiting.");
        		} else {
        		  System.out.println("You chose " + filename);
        		}
            	if (filename != null) {
            		init(filename);
            	}
            }
        });
    }
    
    private void drillIn() {
		if (mouseInCluster != null) {
			if (itemPath.size() == 0 || !mouseInCluster.equals(itemPath.lastElement())) {
				Items.resetTmpInstance();
				List<Item> itemBase = new ArrayList<>();
				itemStack.push(itemBase);
				itemPath.push(mouseInCluster);
				for (Item item : Items.getInstance().getItems()) {
					if (! item.isPlaceholder()) {
						itemBase.add(item);
					}
				}
				List<Item> l = Items.getInstance().getCim().get(mouseInCluster).getItems();
				for (Item item : l) {
					Items.getTmpInstance().create(item);
				}
				switchTmpItems();
			}
		}
    }
    
    private void drillOut() {
		if (itemStack.size() > 0) {
			Items.resetTmpInstance();
			List<Item> l = itemStack.pop();
			itemPath.pop();
			for (Item item : l) {
				Items.getTmpInstance().create(item);
			}
			switchTmpItems();
		}
    }
    
    /** Create and show components
     */
    private void initComponents() {
        
    	setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Technology Radar");
        setResizable(true);
        //setPreferredSize(new Dimension(getMaximumSize().width, getMaximumSize().height));


        novatecWhite = Toolkit.getDefaultToolkit().createImage(DesktopDemo.class.getClassLoader().getResource("images/NOVATEC-rgb-weiss-violett-rot_schutz.png"));
        novatecBlack = Toolkit.getDefaultToolkit().createImage(DesktopDemo.class.getClassLoader().getResource("images/NOVATEC-rgb-schwarz-violett-rot_schutz.jpg"));

        Container conFrame = this.getContentPane();

        conFrame.add(jp);
        
        //GroupLayout layout = new GroupLayout(jp);
        //conFrame.setLayout(layout);
        //layout.setAutoCreateContainerGaps(true);
        //layout.setAutoCreateGaps(true);
        
        jp.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("event:" + e.getWheelRotation() + ", " + e.getScrollType() + ", " + e.getScrollAmount());
			}
		});
        
        jp.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!showScreenCapture) {
					if (e.getClickCount() > 1 && e.getButton() == 1) {
						key('>');
						drillIn();
					} else
					if (e.getClickCount() > 1 && e.getButton() == 3) {
						key('<');
						drillOut();
					}
				}
			}
		});
        
        jp.addMouseMotionListener(new MouseMotionListener() {
        	
        	public void mouseDragged(MouseEvent e) {
        		if (!showSquare && !showScreenCapture) {
	        		double w1 = Util.getAngle(Items.getInstance().getCenter(), mousePressedPoint);
	        		double w2 = Util.getAngle(Items.getInstance().getCenter(), e.getPoint());
	        		//System.out.println("w1=" + w1 + ", w2=" + w2);
	        		double w = 
					- Util.getWDiff(w1, w2) 
					+ Util.getWDiff(2 * Math.PI - w1, 2 * Math.PI - w2);
	
	        		Items.getInstance().addGlobalVersatz(w - versatz);
	        		versatz = w;
        		}
        	};
        	
            @Override
            public void mouseMoved(MouseEvent e) {
            	if (!showScreenCapture) {
	            	mouseInCluster = null;
	            	versatz = 0;
	            	mousePressedPoint.setLocation(e.getPoint());
		            Items.getInstance().setDegrees(e.getPoint());
	            	
		            String text = Items.getInstance().setTooltipText(e.getPoint());
		            if (text != null) {
		            	jp.setToolTipText(text);
		            	ToolTipManager.sharedInstance().setEnabled(true);
	            	} else {
	            		ToolTipManager.sharedInstance().setEnabled(false);
	            	}
	                ToolTipManager.sharedInstance().mouseMoved(e);
	                //repaint();
            	}
            }
        });
        
        jp.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				//Items.getInstance().setCenter(new Point2D.Double(jp.getSize().getWidth()/2, jp.getSize().getHeight()/2), lineSize);
				Rings.getInstance().calculateBasics((int) jp.getSize().getWidth(), (int) jp.getSize().getHeight()); 
				RoundGrid.init(Config.bigBangMoveSpeed);
		        //Items.getInstance().calculateItemPositions();
				initAfterGraphics(false);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
        
        addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				//System.out.println("key:" + e.getKeyChar() + " vs " + KeyEvent.VK_RIGHT);
				if (!searchTyping) {
					Items.getInstance().resetOptimizationReduction();
					if (c == 'r') {
						showRadar = ! showRadar;
					} else
					if (c == 'x') {
						showClusterNames = !showClusterNames;
					} else
					if (c == 'l') {
						showPolygonLines = !showPolygonLines;
					} else
					if (c == 'k') {
						toggleCluster(true);
					} else
					if (c == 'K') {
						toggleCluster(false);
					}
					if (c == 'j') {
						toggleSimplifyArcs();
					} else
					if (c == 'o') {
						optSpeed = Config.optimizationSpeed;
						Items.getInstance().toggleOptimize();
					} else
					if (c == 'O') {
						optSpeed = Config.optimizationSpeed2;
						Items.getInstance().toggleOptimize();
					} else
					if (c == ' ') {
						Items.getInstance().animateChangeInit(null, true);
					} else
					if (c == 'c') {
						changeColors = ! changeColors;
					} else
					if (c == 'y') {
						fileDialog();
					} else
					if (c == '?') {
						printUsage = !printUsage;
					} else
					if (c == '>' || c == '+') {
						if (mouseInCluster != null) {
							c = '>';
							drillIn();
						} else
						if (mouseInCluster == null) {
							c = '<';
							drillOut();
						}
					} else
					if (c == '<' || c == '-') {
						c = '<';
						drillOut();
					} else
					if (c == 'q' || c == 'Q') {
						c = 'q';
						mouseInCluster = nextCat(false);
					} else
					if (c == 6 && e.isControlDown()) {
						c = 'F';
						searchTyping = true;
					} else
					if (c == 'f' && ! e.isControlDown()) {
						Items.getInstance().toggleFollowMouse(MouseInfo.getPointerInfo().getLocation(), jp.getLocationOnScreen());
					} else
					if (c == 'w' || c == 'W') {
						c = 'w';
						mouseInCluster = nextCat(true);
					} else
					if (c == 'z') {
						if (! showSquare) {
							Items.getInstance().animateToSquareInit();
							showSquare = true;
							moveToCircle = false;
						} else {
							moveToCircle = !moveToCircle;
						}
					} else
					if (c == 's') {
						if (!showScreenCapture && fractal == null) {
							animateScreenCapture();
						} else 
						if (showScreenCapture && fractal != null && !fractal.isEnded() && !fractal.isEnding()){
							int sc;
							if (!showSquare) {
								sc = Math.min(jp.getWidth(), jp.getHeight());
							} else {
								sc = Math.max(jp.getWidth(), jp.getHeight());
							}
							fractal.removeListenersAndThreads(sc);
						}
					}
	
					key(c);
				} else {
					if (c == KeyEvent.VK_ESCAPE) {
						searchTyping = false;
					} else {
						if (c == KeyEvent.VK_BACK_SPACE) {
							if (searchString.length() > 0) {
								searchString.deleteCharAt(searchString.length() - 1);
							}
						} else {
							searchString.append(c);
						}
					}
				}
				//repaint();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (searchTyping) {
					return;
				}
				char c = e.getKeyChar();
				key(c);
				if (c == 'n') {
					Items.getInstance().novaAnimateStop();
				} else
				if (c == 'b') {
					if (showSquare) {
						Items.getInstance().animateStarWarsStop();
					} else {
						Items.getInstance().animateBigBangStop();
					}
				} else
				if (c == 'm') {
					Items.getInstance().animateCloserStop();
				}
				//repaint();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (searchTyping) {
					return;
				}
				char c = e.getKeyChar();
				key(c);
				if (c == 't') {
					Items.getInstance().toggleShowText();
				} else
				if (c == 'i') {
					Items.getInstance().toggleShowItems();
				} else
				if (c == 'p') {
					Items.getInstance().togglePolygons();
				} else
				if (c == 'n' && !(Items.getInstance().isShowBigBangAnimation() || Items.getInstance().isShowBigBangAnimation2())) {
					Items.getInstance().novaAnimateStart();
				} else
				if (c == '.') {
					play= ! play;
					if (play) {
						play();
					}
				} else
				if (c == 'b') {
					if (Items.getInstance().isShowSquare()) {
						if (! (Items.getInstance().isShowStarWarsAnimation())) {
							Items.getInstance().animateStarWarsStart();
						}
					} else {
						if (!(Items.getInstance().isShowNovaPointsAnimation() || Items.getInstance().isShowNovaPointsAnimation2())) {
							Items.getInstance().animateBigBangStart();
						}
					}
				} else
				if (c == 'm') {
					Items.getInstance().animateCloserStart(Config.edgeDistance);
				}
				//repaint();
			}
		});
        
        //setPreferredSize(new Dimension(1000, 1000));
        pack();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		graphicsInitialized = true;
		RoundGrid.init(Config.bigBangMoveSpeed);
    }
    
    private void animateScreenCapture() {
    	int currentScs = 1;
    	Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    	try {
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
			int width = screenRect.width;
			int height = screenRect.height;
			//if (capture.getRGB(width/2, height/2) == 0) {
				capture.setRGB(width/2, height/2, Color.WHITE.getRGB());
			//}
			while (currentScs < screenCaptureScale) {
				currentScs *= 2;
				for (int i = 0; i < width; i+=2) {
					for (int o = 0; o < height; o+=2) {
						int c1 = capture.getRGB(i, o);
						int c2 = capture.getRGB(i+1, o);
						int c3 = capture.getRGB(i+1, o+1);
						int c4 = capture.getRGB(i, o+1);
						int r = (int) Math.min(255, Math.round((
								((16777216 + c1) / 65536) + 
								((16777216 + c2) / 65536) +
								((16777216 + c3) / 65536) +
								((16777216 + c4) / 65536)
						)/3.0));
						int g = (int) Math.min(255, Math.round((
								(((16777216 + c1) / 256 ) % 256) + 
								(((16777216 + c2) / 256 ) % 256) +
								(((16777216 + c3) / 256 ) % 256) + 
								(((16777216 + c4) / 256 ) % 256)
						)/3.0));
						int b = (int) Math.min(255, Math.round((
								((16777216 + c1) % 256) + 
								((16777216 + c2) % 256) + 
								((16777216 + c3) % 256) + 
								((16777216 + c4) % 256)
						)/3.0));
						int rgb = r;
						rgb = (rgb << 8) + g;
						rgb = (rgb << 8) + b;
						capture.setRGB(i/2, o/2, rgb);
					}
				}
				width /= 2;
				height /= 2;
			}
			Color[][] c = new Color[width][height];
			for (int i = 0; i < width; i++) {
				for (int o = 0; o < height; o++) {
					int dist = (int) Math.sqrt(Math.pow(width/2-i, 2) + Math.pow(height/2-o, 2));
					if (dist <= height/2 + 1 || showSquare) {
						c[i][o] = new Color(capture.getRGB(i, o));
					} else {
						c[i][o] = Color.BLACK;
					}
				}
			}
			fractal = new Fractal2(jp);
			System.out.println("bg:" + jp.getBackground());
			fractal.processImage(c, Color.BLACK, 0);
			fractal.initFandSC(currentScs);
			fractal.runFractal();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		showScreenCapture = true;
    }
    
    private String nextCat(boolean forward) {
		SortedMap<Double, String> sm = new TreeMap<>();
		for (Item item : Items.getInstance().getItems()) {
			if (! sm.containsValue(item.getClusterValue()) && !item.isPlaceholder()) {
				sm.put(item.getWinkel(), item.getClusterValue());
			}
		}
		List<String> sl = new ArrayList<>(sm.values());
		int idx;
		if (mouseInCluster == null) {
			if (forward) {
				idx = 0;
			} else {
				idx = sl.size() - 1;
			}
		} else {
			int add;
			if (forward) {
				add = 1;
			} else {
				add = -1;
			}
			idx = sl.indexOf(mouseInCluster) + add;
		}
		if (idx < sl.size() && idx >= 0) {
			return sl.get(idx);
		}
		// go back with '>' key
		return null;
    }
    
    private void key(char c) {
    	lastKey = c;
    	lastKeyAction = System.currentTimeMillis();
    }
    
    private boolean keyPaint() {
    	boolean change = false;
    	if (lastKeyAction != 0) {
    		change = true;
	    	if (System.currentTimeMillis() - lastKeyAction > 1000) {
	    		lastKeyAction = 0;
	    		lastKey = 0;
	    	}
    	}
    	return change;
    }
    
    private void fillRadarArea(Graphics2D g) {
    	g.fillOval(
    			jp.getWidth() / 2 - Rings.getInstance().getSize() / 2 - lineSize * 2, 
    			jp.getHeight() / 2 - Rings.getInstance().getSize() / 2 - lineSize * 2,
    			Rings.getInstance().getSize() + lineSize * 4,
    			Rings.getInstance().getSize() + lineSize * 4
    	);
    }
    
    private void drawLogo(Graphics2D g2) {
    	Image image;
    	if (bg == Color.WHITE) {
    		image = novatecBlack;
    	} else {
    		image = novatecWhite;
    	}
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		
		int rh;
		int off = (int) Math.round(
				h/8 + 
				((Rings.getInstance().getSizes()[1] - Rings.getInstance().getSizes()[0])/2 - h/8) *
				(double) Items.getInstance().getSquareAdditionalRadiusIdx() / (double) Items.getInstance().getSquareAdditionRadiusStepsSize()
		);
		if (! showSquare) {
			rh = (int) Math.round(Rings.getInstance().getSizes()[0]/2.0) + off;
		} else {
			Point2D ip = Items.getInstance().getItemCenterSquared(Items.getInstance().getSquaredWinkel(Math.PI/360.0*(540.0), false), Rings.getInstance().getSizes()[0]/2);
	    	rh = - (int) Math.round(ip.getY()) + jp.getHeight() / 2 + off;
		}
		g2.drawImage(image,
				jp.getWidth() / 2 - w / 6,
				jp.getHeight() / 2 - rh - h / 6, 
				jp.getWidth() / 2 + w / 6,
				jp.getHeight() / 2 - rh + h / 6, 0, 0, w, h, null);
    }
    
    
    
    JPanel jp = new JPanel() {
    	
		Map<String, Rectangle> luc = new HashMap<>();
		Map<String, Point> w = new HashMap<>();
    	
    	static final long serialVersionUID = 0;
    	
        @Override
        public void paint(Graphics g) {
        	
        	if (! showScreenCapture) {
        	
	        	long t = System.currentTimeMillis();
	        	
	        	//System.out.println("1:" + (System.currentTimeMillis() - t));
	        	
	        	Graphics2D g2 = (Graphics2D) g;
	        	
	        	//g2.setClip(800, 0, 1600, 800);
	        	//AffineTransform at = AffineTransform.getScaleInstance(0.5, 0.5);
	        	//AffineTransform at = AffineTransform.getShearInstance(0.15, 0.15);
	        	//at.translate(100, 100);
	        	//g2.setTransform(at);
	        	
	        	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        	g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	        	g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        	g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	        	
	        	//super.paint(g);
	        	g2.setColor(fg);
	        	g2.setBackground(bg);
	        	if (Config.roundBack) {
	            	g2.fillRect(0, 0, jp.getWidth(), jp.getHeight());
	            	g2.setColor(bg);
	            	fillRadarArea(g2);
	        		g2.setColor(fg);
	        	} else {
	            	g2.clearRect(0, 0, jp.getWidth(), jp.getHeight());
	        	}
	
	        	//System.out.println("2:" + (System.currentTimeMillis() - t));
	        	synchronized (itsLock) {
	            	//System.out.println("3:" + (System.currentTimeMillis() - t));
	       		//its = Items.getInstance();
	        	
		        	// Ringe
		    		if (
		    				!its.isShowBigBangAnimation() && 
		    				!its.isShowBigBangAnimation2())
		        	{
			        	g2.setColor(fg);
			        	double rld = Math.abs(radarLine - Math.PI * 1.5);
			        	if (showRadar && (rld <= 0.1 && ! its.isShowSquare() || rld <= 0.0001)) {
			        		g2.setFont(new Font("default", Font.BOLD, (int) Math.round(16 + (0.1 - rld) * 50)));
			        	} else {
			        		g2.setFont(new Font("default", Font.BOLD, 16));
			        	}
			        	for (int i = 0; i < Rings.getInstance().getSizes().length; i++) {
			        		if (i > 0) {
			                	g2.setStroke(new BasicStroke(lineSize));
			        		} else {
			                	g2.setStroke(new BasicStroke(lineSize / 2));
			        		}
			        		Rectangle2D r1 = g2.getFontMetrics().getStringBounds(Config.texts[i], g2);
			        		if (its.isShowSquare()) {
			        			//synchronized (its) 
			        			{
				        			int dx;
				        			if (its.squareReached()) {
				        				dx = 719;
				        			} else {
				        				dx = 1;
				        			}
				        			GeneralPath p = new GeneralPath();
				        			for (int px = 0; px < 720; px+=dx) {
				        				Point2D ip = its.getItemCenterSquared(its.getSquaredWinkel(Math.PI/360*(px + 180), false), Rings.getInstance().getSizes()[i]/2);
				        				if (px > 0) {
				        					p.lineTo(ip.getX(), ip.getY());
				        				} else {
				        					p.moveTo(ip.getX(), ip.getY());
				        				}
				        			}
				        			g2.draw(p);
				        			int px = 360;
			        				Point2D ip = its.getItemCenterSquared(its.getSquaredWinkel(Math.PI/360*(px + 180), false), Rings.getInstance().getSizes()[i]/2);
					            	g2.drawString(
					            			Config.texts[i], 
					            			(int) Math.round(ip.getX()) - g2.getFontMetrics().stringWidth(Config.texts[i]) / 2, 
					            			(int) Math.round(Math.max(5, ip.getY() + (int) r1.getHeight())));
			        			}
			        		} else {
			        			//System.out.println("size[i]=" + Rings.getInstance().getSizes()[i]);
				            	g2.drawOval(
				            			jp.getWidth()/2 - Rings.getInstance().getSizes()[i]/2,
				            			Rings.getInstance().getOffsets()[i] / 2, 
				            			Rings.getInstance().getSizes()[i], Rings.getInstance().getSizes()[i]);
				            	g2.drawString(Config.texts[i], jp.getWidth()/2 - g2.getFontMetrics().stringWidth(Config.texts[i]) / 2, Math.max(5, jp.getHeight()/2 - Rings.getInstance().getSizes()[i]/2 + (int) r1.getHeight()));
			        		}
			        	}
		        	}
		        	
		        	//System.out.println("4:" + (System.currentTimeMillis() - t));
		        	// NT Logo
		        	drawLogo(g2);
		        	//System.out.println("5:" + (System.currentTimeMillis() - t) + "::" + its.getNovaPolygonDimm());
		
		        	// Polygone
		        	if (
		        			showPolygonLines ||
		        			its.getNovaDimm() > 0 &&
		        			its.getNovaPolygonDimm() > 0
		        	) {
			    		if (showClusterNames) {
			    			luc.clear();
			    			w.clear();
			    		}
		    			Set<String> cvs = its.getClusterValues();
			        	for (String cat : cvs) {
			        		if (! its.isClusterPlaceholder(cat)) {
			        			int stroke;
					    		Color c = Config.getCurrent(colorKey, cat);
					    		ClusterInfo ci = its.getCim().get(cat);
				    			GeneralPath p = ci.getPolygon();
				    			int alpha = Config.polygonAlpha;
					    		if (p != null && (mouseInCluster != null && mouseInCluster.equals(cat) || p.contains(
					    				MouseInfo.getPointerInfo().getLocation().getX() - jp.getLocationOnScreen().getX(),
					    				MouseInfo.getPointerInfo().getLocation().getY() - jp.getLocationOnScreen().getY()
					    		))) {
					    			alpha = Math.min(255, Config.polygonAlpha + 64);
					    			//p = its.polygon2(cat, roundBack, (int) Math.round(Config.edgeDistance * 1.2));
					    			mouseInCluster = cat;
					    			stroke = 5;
					    		} else {
					    			stroke = 3;
					    		}
					    		g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.min(255, Math.round(alpha * its.getNovaPolygonDimm()))));
				    			if (p != null) {
				    	    		g2.setStroke(new BasicStroke(stroke));
				    	    		Point2D ca = ci.getCenterAverage();
				    	    		double f = (its.getSquareAdditionRadiusStepsSize() - its.getSquareAdditionalRadiusIdx()) / (double) its.getSquareAdditionRadiusStepsSize();
				    	    		Point2D from = new Point2D.Double(
				    	    				its.getCenter().getX() * f + (1 - f) * its.getCenter().getX(),
				    	    				its.getCenter().getY() * f + (1 - f) * jp.getHeight()
				    	    		);
				    	    		//System.out.println("f=" + f);
				    	    		/*
				    	    		if (showSquare && its.getSquareAdditionalRadiusIdx() > its.getSquareAdditionRadiusStepsSize() / 1.2) {
				    	    			from = new Point2D.Double(ca.getX(), jp.getHeight());
				    	    		} else {
				    	    			from = its.getCenter();
				    	    		}*/
				    	    		
				    	    		if (its.getNovaDimm() > 0 && its.getNovaPolygonDimm() > 0) 
				    	    		{
					    	        	GradientPaint gp = new GradientPaint(
					    	        			from, new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.min(255, Math.round(0.1 * alpha * its.getNovaPolygonDimm()))), 
					    	        			ca, new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.min(255, Math.round(alpha * its.getNovaPolygonDimm())))); 
					    	        	g2.setPaint(gp); 
					    				g2.fill(p);
				    	    		}
					    			if (showPolygonLines) {
							    		g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue()/*, (int) Math.round(255 * its.getNovaPolygonDimm())*/));
						    			g2.draw(p);
					    			}
				    			}
				    			
				    			if (showClusterNames && p != null) {
					    			g2.setFont(new Font("default", Font.BOLD, 20));
						    		//g2.setStroke(new BasicStroke(1));
					    			Rectangle2D pb = p.getBounds2D();
					    			Rectangle2D sb = g2.getFontMetrics().getStringBounds(cat, g2);
					    			luc.put(cat, new java.awt.Rectangle(
					    					(int) Math.round(pb.getCenterX() - sb.getWidth() / 2 - 4),
					    					(int) Math.round(pb.getCenterY() - sb.getHeight() / 2 - 4),
					    					(int) Math.round(sb.getWidth()) + 8,
					    					(int) Math.round(sb.getHeight()) + 8
					    			));
					    			w.put(cat, new Point(
					    					(int) Math.round(pb.getCenterX() - sb.getCenterX()),
					    					(int) Math.round(pb.getCenterY() - sb.getCenterY())
					    			));
				    			}
			        		}
			        	}
		    		}
		    		
		        	//System.out.println("6:" + (System.currentTimeMillis() - t));
		        	if (its.isShowNovaPointsAnimation1Or2OrBigBangAnimation1Or2()) {
		        		if (Config.roundBack) {
			        		g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int) Math.round(255 * (1 - its.getNovaDimm() * its.getNovaDimm()))));
			            	fillRadarArea(g2);
		        		} else {
			        		g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int) Math.round(255 * (1 - its.getNovaDimm() * its.getNovaDimm()))));
			        		g2.fillRect(0, 0, jp.getWidth(),  jp.getHeight());
		        		}
		        	}
		        	
		        	//System.out.println("7:" + (System.currentTimeMillis() - t));
		        	// Ringe big bang
		    		if (its.isShowBigBangAnimation() || its.isShowBigBangAnimation2())
		        	{
			        	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			        	for (int i = 0; i < Rings.getInstance().getSizes().length; i++) {
			        		double mult = Rings.getInstance().getBigBangMultis()[i][0];
			        		double size = Rings.getInstance().getSizes()[i];
			        		g2.setFont(new Font("default", Font.BOLD, (int) Math.round(12 + 4 * mult)));
				        	g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), (int) Math.round(Rings.getInstance().getBigBangAlpha()[i])));
			        		if (i > 0) {
			                	g2.setStroke(new BasicStroke(lineSize));
			        		} else {
			                	g2.setStroke(new BasicStroke(lineSize / 2));
			        		}
			        		Rectangle2D r1 = g2.getFontMetrics().getStringBounds(Config.texts[i], g2);
			            	g2.drawOval(
			            			(int) Math.round(Rings.getInstance().getBigBangPoint1()[i].getX() - size / 2 * mult),
			            			(int) Math.round(Rings.getInstance().getBigBangPoint1()[i].getY()), 
			            			(int) Math.round(size * mult), 
			            			(int) Math.round(Rings.getInstance().getBigBangPoint2()[i].getY() - Rings.getInstance().getBigBangPoint1()[i].getY())
			            	);
			            	g2.drawString(
			            			Config.texts[i], 
			            			jp.getWidth()/2 - g2.getFontMetrics().stringWidth(Config.texts[i]) / 2, 
			            			Math.max(5, (int) Math.round(Rings.getInstance().getBigBangPoint1()[i].getY() + (int) r1.getHeight())));
			        	}
		        	}
		
		        	
		        	//System.out.println("8:" + (System.currentTimeMillis() - t));
		        	// grid
		    		if (! its.isShowSquare()) {
			        	if (its.isShowBigBangAnimation() || its.isShowBigBangAnimation2()) {
			        		RoundGrid.drawGrid(
			        				its.getCenter(), 
			        				its.getNovaDimm(), g2, its.getNovaDimm(), 
			        		fg);
			        	} 
		    		}
		        	
		        	//System.out.println("9:" + (System.currentTimeMillis() - t));
		        	// its
		        	//if (showItems) 
		        	//synchronized (its) 
		        	{
			        	g2.setFont(new Font("default", Font.PLAIN, 12));
			        	String searchStringString = searchString.toString().toLowerCase();
			        	for (Item item : its.getItems()) {
			        		if (
			        				item.isPlaceholder() ||
			        				(searchString != null && searchString.length() > 0 && item.getText().toLowerCase().indexOf(searchStringString) < 0)
			        			) {
			        			/*g2.setColor(new Color(32, 32, 32));
			            		g2.fillRect(
			            				(int) Math.round(item.getCenter().getX() - 1), 
			            				(int) Math.round(item.getCenter().getY() - 1),
			            				2,
			            				2
			            		);*/
			        		} else {
			        			Point2D ac = item.getArcCenter();
			            		if (
			            				//!item.isPlaceholder() && 
			            				(!Config.roundBack || ac.distance(its.getCenter()) + Config.itemSizes[item.getSize()] / 2 <= Rings.getInstance().getSize() / 2))
			            		{
			            			if (its.isShowText() && ! its.isShowNovaPointsAnimation1Or2OrBigBangAnimation1Or2() && ! its.isShowChangeAnimation()) {
					            		g2.setColor(g2.getBackground());
					            		Point2D tc = item.getTextCenter();
					            		if (item.getClusterValue().equals(mouseInCluster)) {
					        	        	g2.setFont(new Font("default", Font.BOLD, 12));		            			
					            		} else {
					        	        	g2.setFont(new Font("default", Font.PLAIN, 12));
					            		}
					            		g2.drawString(item.getText(), 
					            				(int) (tc.getX() - item.getTextBounds().getWidth()/2 - 1),
					            				(int) (tc.getY() - item.getTextBounds().getHeight()/2 - 1)
					            		);
			            			}
			            			if (item.getArcSize() > 0) {
			            				if (! (its.isShowBigBangAnimation() || its.isShowBigBangAnimation2())) {
			            					g2.setColor(Config.getCurrent(colorKey, item.getClusterValue()));
			            				} else {
			            					Color c = Config.getCurrent(colorKey, item.getClusterValue());
			            					g2.setColor(new Color (c.getRed(), c.getGreen(), c.getBlue(), item.getBigBangAlpha()));
			            				}
					            		g2.setStroke(new BasicStroke(1));
					            		int addToSize = 0;
					            		if (item.getClusterValue().equals(mouseInCluster)) {
					            			addToSize = 2;
					            		}
					            		if (simplifyArcs) {
						            		g2.fillArc(
						            				(int) Math.round(ac.getX() - Config.itemSizes[0] / 2), 
						            				(int) Math.round(ac.getY() - Config.itemSizes[0] / 2),
						            				Config.itemSizes[0] + addToSize,
						            				Config.itemSizes[0] + addToSize,
						            				0,
						            				360
						            		);
					            		} else {
						            		g2.fillArc(
						            				(int) Math.round(ac.getX() - item.getArcSize() / 2 - addToSize/2), 
						            				(int) Math.round(ac.getY() - item.getArcSize() / 2 - addToSize/2),
						            				(int) Math.round(item.getArcSize() + addToSize),
						            				(int) Math.round(item.getArcSize() + addToSize),
						            				item.getArcStart(),
						            				item.getArcDegrees()
						            		);
					            		}
			            			}
			            			
			            			
			            			//if (showText) 
			            			{
			            				if (its.isShowNovaPointsAnimation1Or2OrBigBangAnimation1Or2()) {
			            					g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), (int) Math.round(255 * its.getNovaDimm() * its.getNovaTextDimm())));
			            				} else {
			            					g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), (int) Math.round(255 * its.getNovaTextDimm())));
			            				}
					            		if (item.getClusterValue().equals(mouseInCluster)) {
					        	        	g2.setFont(new Font("default", Font.BOLD, 12));		            			
					            		} else {
					        	        	g2.setFont(new Font("default", Font.PLAIN, 12));
					            		}
			            				Point2D tc = item.getTextCenter();
					            		g2.drawString(item.getText() /*+ "_" + item.getWinkelNr() + "." +item.getSubRing()*/, 
					            				(int) (tc.getX() - item.getTextBounds().getWidth()/2),
					            				(int) (tc.getY() - item.getTextBounds().getHeight()/2)
					            		);
			            			}
			            			
				            	}
			            	}
			        	}
		        	}
		        	
		        	//System.out.println("10:" + (System.currentTimeMillis() - t));
		        	if (
		        			showClusterNames && 
		        			(
		        				showPolygonLines ||
			        			its.getNovaDimm() > 0 && 
			        			its.getNovaPolygonDimm() > 0 
			        		) &&
		        			luc.size() > 0
		        	) {
			    		g2.setStroke(new BasicStroke(1));
			        	for (String cat : its.getClusterValues()) {
			        		if (! its.isClusterPlaceholder(cat)) {
					    		Color c = Config.getCurrent(colorKey, cat);
				    			
				    			Rectangle r = luc.get(cat);
				    			if (r != null && r.getX() > 0 && r.getY() > 0) {
					    			g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int) Math.round(192 * its.getNovaPolygonDimm() * its.getNovaDimm())));
					    			g.fillRoundRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight(), 15, 15);
						    		g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.round(255 * /*items.getNovaPoygonDimm() * */ its.getNovaDimm())));
					    			g2.setFont(new Font("default", Font.BOLD, 20));
					    			Point s = w.get(cat);
					    			g2.drawString(
					    					cat, 
					    					(int) s.getX(),
					    					(int) s.getY()
					    			);
				    			}
			        		}
			        	}
		        	}
		
		        	//System.out.println("11:" + (System.currentTimeMillis() - t));
		        	// radar
		    		if (showRadar) {
		        		g2.setStroke(new BasicStroke(3));
		        		if (its.isShowNovaPointsAnimation1Or2OrBigBangAnimation1Or2()) {
		        			g2.setColor(new Color(57, 255, 20, (int) Math.round(255 * its.getNovaDimm())));
		        		} else {
		        			g2.setColor(new Color(57, 255, 20));
		        		}
		        		if (!its.isShowSquare()) {
			        		g2.drawLine(
			        				(int) Math.round(its.getCenter().getX()), 
			        				(int) Math.round(its.getCenter().getY()), 
			        				(int) Math.round(its.getCenter().getX() + Math.cos(radarLine) * (Rings.getInstance().getSize() / 2 * 1.01)), 
			        				(int) Math.round(its.getCenter().getY() + Math.sin(radarLine) * (Rings.getInstance().getSize() / 2 * 1.01))
			        		);
		        		} else {
		        			Point2D ip1 = its.getItemCenterSquared(its.getSquaredWinkel(radarLine, false), Rings.getInstance().getSize() / 2 + 5);
		        			Point2D ip2 = its.getItemCenterSquared(its.getSquaredWinkel(radarLine, false), 0);
		        			g2.drawLine((int) Math.round(ip2.getX()), (int) Math.round(ip2.getY()), (int) Math.round(ip1.getX()), (int) Math.round(ip1.getY()));
		        		}
		    		}
		    		
		        	//System.out.println("12:" + (System.currentTimeMillis() - t));
		    		if (! its.isShowSquare()) {
			    		printUsageOnScreen(its, g2);
		    		}
			    	printInfoOnScreen(its, g2);
		    		
		        	//System.out.println("13:" + (System.currentTimeMillis() - t));
		    		/*
		    		g2.setStroke(new BasicStroke(5));
		    		for (ClusterInfo ci : its.getCim().getClusterInfos().values()) {
		    			double winkel = ci.getAddWinkelTmp();
		    			if (ci.isAdjustedWinkel()) {
		    	    		g2.setColor(Color.WHITE);
		    			} else {
		    				g2.setColor(Color.BLACK);
		    			}
			    		g2.drawLine(
			    				(int) Math.round(its.getCenter().getX()), 
			    				(int) Math.round(its.getCenter().getY()), 
			    				(int) Math.round(its.getCenter().getX() + Math.cos(winkel) * 500),
			    				(int) Math.round(its.getCenter().getY() + Math.sin(winkel) * 500)
						);
			    		g2.drawString(ci.getName(), (int) Math.round(its.getCenter().getX() + Math.cos(winkel) * 500),
			    				(int) Math.round(its.getCenter().getY() + Math.sin(winkel) * 500));
		    		}
		    		*/
			    	
			        synchronized (fpsLock) {
		        		fpsCount++;
		        		//fpsLock.notify();
		        	}
		        }
        	} else  {
        		fractal.doPaint((Graphics2D) g);
        	}
        	
        	if (searchTyping) {
        		g.setColor(fg);
        		g.setFont(new Font("default", Font.BOLD, 48));
        		FontMetrics fm = g.getFontMetrics();
        		Rectangle2D w = fm.getStringBounds(searchString.toString(), g);
        		g.drawString(
        			searchString.toString(),
        			(int) (jp.getWidth() / 2 - w.getCenterX()), 
        			(int) (jp.getHeight() / 2 - w.getHeight() / 2)
        		);
        	}
        	
        }
        
    };
    
    private void play() {
    	new Thread() {
    		@Override
    		public void run() {

    	    	try {
		    	    if (simplifyArcs) {
		    	    	toggleSimplifyArcs();
			    		wait2 (3);
		    	    }
		    	    if (Items.getInstance().isOptimize()) {
		    	    	Items.getInstance().toggleOptimize();
			    		wait2 (3);
		    	    }
		    	    if (Items.getInstance().isShowItems()) {
		    	    	Items.getInstance().toggleShowItems();
			    		wait2 (3);
		    	    }
		    	    if (Items.getInstance().isShowPolygons()) {
		    	    	Items.getInstance().togglePolygons();
			    		wait2 (3);
		    	    }
		    	    if (Items.getInstance().isShowText()) {
		    	    	Items.getInstance().toggleShowText();
		    	    }
		    	    if (!showRadar) {
		    	    	showRadar = true;
		    	    }

		    	    Robot robot = new Robot();

		    	    while (play) {
		    			// r, t, i, f, ' ', p, o, n
		    	    	
		    	    	int cl = Cluster.values().length - Cluster.values().length / 2;
		
			    	    pressSecondsW(robot, 'P', 0);	// show polygons
			    	    pressSecondsW(robot, 'I', 0);	// show items
			    	    pressSecondsW(robot, 'O', 4, true);	// slow optimize on
			    	    pressSeconds(robot, 'M', 2);	// squeeze
	    	    		wait2 (1);
	    	    		pressSeconds(robot, 'N', 4);	// animate logo
	    	    		wait2 (4);
	    	    		pressSeconds(robot, 'B', 5);	// animate big bang
	    	    		wait2 (4);
			    	    for (int i = 0; i < Cluster.values().length / 2; i++) {
		    	    		pressSecondsW(robot, 'K', 2);
				    	    pressSeconds(robot, 'M', 2);
		    	    		wait2 (2);
			    	    }
			    	    
			    	    pressSeconds(robot, 'Z', 5);

			    	    pressSeconds(robot, 'M', 2);	// squeeze
	    	    		wait2 (1);
	    	    		pressSeconds(robot, 'N', 4);	// animate logo
	    	    		wait2 (4);
	    	    		pressSeconds(robot, 'B', 5);	// animate big bang
	    	    		wait2 (4);

			    	    for (int i = 0; i < cl; i++) {
		    	    		pressSecondsW(robot, 'K', 2);
				    	    pressSeconds(robot, 'M', 1);
		    	    		wait2 (2);
			    	    }
			    	    pressSecondsW(robot, 'Z', 5);
	
			    	    pressSecondsW(robot, 'I', 2);	// hide items
			    	    pressSecondsW(robot, 'P', 2);	// hide polygons
	
			    	    pressSecondsW(robot, 'O', 1, true);	// optimize off
	    			}		    	    
    	    	} catch (Exception e) { e.printStackTrace(System.out); }
    		}
    	}.start();
    }
    
    private void playShow() throws AWTException, InterruptedException {
	    Robot robot = new Robot();

	    while (play) {
			// r, t, i, f, ' ', p, o, n
    	    
    	    pressSecondsW(robot, 'P', 1);	// show polygons
    	    pressSecondsW(robot, 'I', 1);	// show items
    	    pressSecondsW(robot, 'T', 2);	// show texts
    	    pressSecondsW(robot, 'T', 1);	// hide texts	(original)
    	    pressSecondsW(robot, 'O', 4);	// slow optimize on
    	    pressSeconds(robot, 'M', 2);	// squeeze
    		wait2 (3);
    		pressSeconds(robot, 'N', 8);	// animate logo
    		wait2 (2);
    		pressSeconds(robot, 'B', 8);	// animate big bang
    		wait2 (5);
	    	toggleSimplifyArcs();			// simplify arcs
    	    pressSecondsW(robot, 'O', 0);	// slow optimize off
    	    pressSecondsW(robot, 'O', 0, true);	// fast optimize on
    	    for (int i = 0; i < Cluster.values().length; i++) {
	    		pressSecondsW(robot, 'K', 2);
	    	    pressSeconds(robot, 'M', 2);
	    		wait2 (5);
    	    }
    	    
    	    pressSecondsW(robot, 'Z', 5);
    		pressSeconds(robot, 'N', 8);	// animate logo
    		wait2 (2);
    	    pressSeconds(robot, 'B', 8);
    		wait2 (2);
    	    for (int i = 0; i < Cluster.values().length; i++) {
	    		pressSecondsW(robot, 'K', 3);
	    		wait2 (5);
    	    }
    	    pressSecondsW(robot, 'Z', 5);

    	    pressSecondsW(robot, 'I', 2);	// hide items
    	    pressSecondsW(robot, 'P', 2);	// hide polygons

    	    pressSecondsW(robot, 'O', 1, true);	// optimize off
		}		    	    
    }
    
    private void pressSeconds(Robot robot, int keyEvent, int seconds) throws InterruptedException {
		try {
			System.out.println(System.currentTimeMillis() + " pressing..." + keyEvent);
			robot.keyPress(keyEvent);
			if (play)
			Thread.sleep(seconds * 1000);
			robot.keyRelease(keyEvent);
			System.out.println(System.currentTimeMillis() + " releasing..." + keyEvent);
		} catch (Exception e) {}
    }

    private void wait2(int seconds) throws InterruptedException {
		try {
			if (play)
			Thread.sleep(seconds * 1000);
		} catch (Exception e) {}
    }

    private void pressSecondsW(Robot robot, int keyEvent, int seconds) throws InterruptedException {
    	pressSecondsW(robot, keyEvent, seconds, false);
    }
    private void pressSecondsW(Robot robot, int keyEvent, int seconds, boolean shift) throws InterruptedException {
		try {
			if (shift) {
				robot.keyPress(KeyEvent.VK_SHIFT);
			}
			robot.keyPress(keyEvent);
			robot.keyRelease(keyEvent);
			if (shift) {
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
			if (play)
			Thread.sleep(seconds * 1000);
		} catch (Exception e) {}
    }
    
    private void toggleCluster(boolean forward) {
    	int tmpCI;
    	if (forward) {
    		tmpCI = (clusterIndex + 1) % Cluster.values().length;
    	} else {
    		tmpCI = (clusterIndex - 1 + Cluster.values().length) % Cluster.values().length;
    	}
    	if (Items.getInstance().animateChangeInit(Cluster.values()[tmpCI], false)) {
    		clusterIndex = tmpCI;
    	}
    }

    private void toggleSimplifyArcs() {
    	simplifyArcs = ! simplifyArcs;
    }
        
    private SortedMap<String, String> usageTexts = new TreeMap<>();
    private SortedMap<String, String> usageTexts2 = new TreeMap<>();
    private void addUsageText(String key, String text, String pfx) {
    	usageTexts.put(key, text);
    	usageTexts2.put(key, pfx);
    }
    private void updateUsageTexts(Items its) {
    	addUsageText("m", "play animation : shrink cluster areas (animated when pressed)", onOff(its.isShowCloserAnimation()||Items.getInstance().isShowCloserAnimation2()));
    	addUsageText("c", "toggle animation : color change", onOff(changeColors));
    	addUsageText("b", "play animation : black whole (animated when pressed)", onOff(its.isShowBigBangAnimation()||Items.getInstance().isShowBigBangAnimation2()));
    	addUsageText(".", "toggle animation : auto animation loop", onOff(play));
    	addUsageText("n", "play animation : Novatec logo (animated when pressed)", onOff(its.isShowNovaPointsAnimation()||Items.getInstance().isShowNovaPointsAnimation2()));
    	addUsageText("p", "toggle visibility : cluster areas", onOff(its.isShowPolygons()));
    	addUsageText("f", "toggle animation : follow mouse", onOff(its.isFollowMouse()));
    	addUsageText("i", "toggle visibility : show items", onOff(its.isShowItems()));
    	addUsageText("t", "toggle visibility : show item texts", onOff(play));
    	addUsageText("r", "toggle animation : radar ", onOff(showRadar));
    	addUsageText(" ", "redistribute items", onOff(its.isShowResetItemLocationAnimation()));
    	addUsageText("o", "toggle animation : sort items by cluster (slow)", onOff(its.isOptimize() && Items.getInstance().getOptimizationSpeed() == Config.optimizationSpeed));
    	addUsageText("O", "toggle animation : sort items by cluster (fast)", onOff(its.isOptimize() && Items.getInstance().getOptimizationSpeed() == Config.optimizationSpeed2));
    	addUsageText("j", "toggle item display : simple/complex", onOff(simplifyArcs));
    	addUsageText("k", "switch to next cluster (current:" + Cluster.values()[clusterIndex].getColumn() + ", next:" + Cluster.values()[(clusterIndex + 1)%Cluster.values().length].getColumn() + ")", "[" + clusterIndex + "]");
    	addUsageText("K", "switch to previous cluster (current:" + Cluster.values()[clusterIndex].getColumn() + ", previous:" + Cluster.values()[(Cluster.values().length + clusterIndex - 1)%Cluster.values().length].getColumn() + ")", "[" + clusterIndex + "]");
    	addUsageText("l", "toggle visibility : cluster area lines", onOff(showPolygonLines));
    	addUsageText("x", "toggle visibility : cluster area names", onOff(showClusterNames));
    	addUsageText("y", "load file", "");
    	addUsageText("z", "switch square/circle", "");
    	addUsageText("s", "capture and animate screen", "");
    	addUsageText("F", "toggle enter search on/off (search string:" + searchString + ")", onOff(searchTyping));
    	String mic;
    	if (mouseInCluster != null) {
    		mic = mouseInCluster;
    	} else {
    		mic = "<none selected>";
    	}
    	addUsageText(">", "mouse double click left : drilldown cluster " + mic, "");
    	String path = null;
    	if (itemPath.size() > 0) {
    		path = "";
    		for (String p : itemPath) {
    			path = path + " > " + p;
    		}
    	} else {
    		path = "<none>";
    	}
    	addUsageText("<", "mouse double click right: go to previous level; path: " + path, "");
    	addUsageText("?", "show/do not show this help", "");
    }
    private String onOff(boolean onOff) {
    	if (onOff) {
    		return "[on]";
    	} else {
    		return "[off]";
    	}
    }
    private void printUsageOnScreen(Items its, Graphics2D g) {
    	if (printUsage) {
	    	// dynamic usage text
	    	updateUsageTexts(its);
	    	if (Config.roundBack) {
	    		g.setColor(bg);
	    	} else {
	    		g.setColor(fg);
	    	}
	    	g.setFont(new Font("default", Font.PLAIN, 13));
			Rectangle2D rpfx1 = g.getFontMetrics().getStringBounds("O ", g);
			Rectangle2D rpfx2 = g.getFontMetrics().getStringBounds(" [off]  ", g);
			int line = 0;
			int fontStyle;
	    	for (Entry<String, String> entry : usageTexts.entrySet()) {
	    		double dy = rpfx1.getHeight() * line;
	    		if (entry.getKey().charAt(0) == lastKey) {
	    			fontStyle = Font.BOLD;
	    		} else {
	    			fontStyle = Font.PLAIN;
	    		}
	        	g.setFont(new Font("default", fontStyle, 13));
	        	int y = (int) Math.round(-rpfx1.getY() + dy);
	    		g.drawString(" " + entry.getKey(), (int) Math.round(-rpfx1.getX()), y);
	    		g.drawString(" " + usageTexts2.get(entry.getKey()), (int) Math.round(-rpfx2.getX() - rpfx1.getX() + rpfx1.getWidth()), y);
	    		g.drawString(":" + entry.getValue(), (int) Math.round(-rpfx2.getX() - rpfx1.getX() + rpfx1.getWidth() + rpfx2.getWidth()), y);
	    		line++;
	    	}
    	}
    }


    private void printInfoOnScreen(Items its, Graphics2D g) {
    	//if (printUsage) 
    	{
    		texts.clear();
	    	updateUsageTexts(its);
	    	if (Config.roundBack) {
	    		g.setColor(bg);
	    	} else {
	    		g.setColor(fg);
	    	}
	    	g.setFont(new Font("default", Font.BOLD, 16));
			Rectangle2D rpfx1 = g.getFontMetrics().getStringBounds("O ", g);
			texts.add("Current clustering : by " + Cluster.values()[clusterIndex].getColumn());
	    	String path = null;
	    	if (itemPath.size() > 0) {
	    		path = "";
	    		for (String p : itemPath) {
	    			path = path + "\n > " + p;
	    		}
	    	} else {
	    		path = "<none>";
	    	}
			texts.add("Current filtering path : " + path);
			texts.add(Math.round(fps) + " fps");
			double maxWidth = 0;
			double maxHeight = rpfx1.getHeight();
	    	for (String s : texts) {
	    		maxWidth = Math.max(maxWidth, g.getFontMetrics().getStringBounds(s, g).getWidth());
	    	}
	    	maxWidth += rpfx1.getWidth();
			double height = 0.5 * maxHeight;
			if (showSquare) {
				height += Config.yOffset / 2;
			}
	    	for (String s : texts) {
	        	int y = (int) Math.round(-rpfx1.getY() + height);
	    		g.drawString(s, (int) Math.round(jp.getWidth() -maxWidth), y);
	    		height += maxHeight;
	    	}
    	}
    }
}

