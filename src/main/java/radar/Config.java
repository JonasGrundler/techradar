package radar;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Config {

	public static final int ringsCount = 5;
    public static final String[] texts = {"reduce", "work", "build-up", "evaluate", "observe"};
    public static final int[] itemSizes = {5, 10, 20, 30, 40};
    public static final double bigBangMoveSpeed = 0.995;
    public static final int optimizationSpeed = 50;
    public static final int optimizationSpeed2 = optimizationSpeed * 50;
    public static final double edgeDistance = 20;
    public static final boolean roundBack = false;
    public static final double optimizationReductionSpeed = 0.00000001;
    public static final double optimizationReductionSpeed2 = 0.00000001;
    public static final double stepToTargetLocationSpeed = 0.980;
    public static final boolean anonymized = false;
    public static final double stepToSquareSpeed = 12;
    public static final int polygonAlpha = 128;
    public static final int paintWait = 1000/60; // 1s/framesPerSecond
    public static final int yOffset = 50;
    public static final Dimension baseDimension = new Dimension (1920, 1080);

    public static final Map<String, String> continuousMaps = new HashMap<>();
    public static final Map<String, Map<String, Color>> categoryToColorMap = new HashMap<>();
    static {
    	Map<String, Color> categoryToColor = new HashMap<>();
    	categoryToColor.putAll(farbverlauf(new Color (95, 71, 147), new Color(227, 69, 81), 4));
    	categoryToColorMap.put("f1", categoryToColor);

    	categoryToColor = new HashMap<>();
    	categoryToColor.putAll(farbverlauf(new Color (95, 188, 163), new Color(55, 188, 236), 4));
    	categoryToColorMap.put("f2", categoryToColor);

    	categoryToColor = new HashMap<>();
    	categoryToColor.putAll(farbverlauf(new Color (244, 148, 3), new Color(255, 234, 119), 4));
    	categoryToColorMap.put("f3", categoryToColor);
    	
    	Map<String, Color> f1 = categoryToColorMap.get("f1");
    	Map<String, Color> f2 = categoryToColorMap.get("f2");
    	Map<String, Color> f3 = categoryToColorMap.get("f3");
    	
    	continuousMaps.put("f3-f1-10", "f1");
    	categoryToColorMap.putAll(farbverlauf ("f1", f1, "f2", f2, 10));
    	continuousMaps.put("f1-f2-10", "f2");
    	categoryToColorMap.putAll(farbverlauf ("f2", f2, "f3", f3, 10));
    	continuousMaps.put("f2-f3-10", "f3");
    	categoryToColorMap.putAll(farbverlauf ("f3", f3, "f1", f1, 10));
    	
    	for (Entry<String, Map<String, Color>> entry : categoryToColorMap.entrySet()) {
    		entry.getValue().put("default", Color.BLACK);
    	}
	}
    
    private static Map<String, String> nameToColorKey = new HashMap<>();
    private static int colorIdx = -1;

	private static Map<String, Map<String, Color>> farbverlauf (String fk1, Map<String, Color> f1, String fk2, Map<String, Color> f2, int count) {
		Map<String, Map<String, Color>> f = new HashMap<>();
		List<Map<String, Color>> l = new ArrayList<>();
		for (int o = 0; o < f1.size(); o++) {
			Map<String, Color> m = farbverlauf(f1.get("k" + (o + 1)), f2.get("k" + (o + 1)), count);
			l.add(m);
		}
    	continuousMaps.put(fk1, fk1 + "-" + fk2 + "-" + (1));
		for (int i = 0; i < count; i++) {
	    	continuousMaps.put(fk1 + "-" + fk2 + "-" + i, fk1 + "-" + fk2 + "-" + (i + 1));
			Map<String, Color> map = new HashMap<>();
			for (int o = 0; o < f1.size(); o++) {
				map.put("k" + (o + 1), l.get(o).get("k" + (i + 1)));
			}
			f.put(fk1 + "-" + fk2 + "-" + (i + 1), map);
		}
		return f;
	}
    

    private static Map<String, Color> farbverlauf (Color c1, Color c2, int count) {
    	if (c1 == null || c2 == null) {
    		System.out.println("c1=" + c1 + ", c2=" + c2);
    	}
    	Map<String, Color> l = new HashMap<>();
    	for (int i = 0; i < count + 2; i++) {
    		l.put("k" + (i + 1), new Color(
    				c1.getRed() + (int) ((c2.getRed() - c1.getRed())/(double) (count + 1) * i),
    				c1.getGreen() + (int) ((c2.getGreen() - c1.getGreen())/(double) (count + 1) * i),
    				c1.getBlue() + (int) ((c2.getBlue() - c1.getBlue())/(double) (count + 1) * i)
    		));
    	}
    	return l;
    }
    
    public static Color getCurrent(String key, String cat) {
    	Map<String, Color> map = categoryToColorMap.get(key);
    	String ck = nameToColorKey.get(cat);
    	if (ck == null) {
    		colorIdx = (colorIdx + 1) % 6;
    		ck = "k" + (colorIdx + 1);
    		nameToColorKey.put(cat, ck);
    	}
    	return map.get(ck);
    }

    public static String getNextKey(String key) {
    	return continuousMaps.get(key);
    }

}
