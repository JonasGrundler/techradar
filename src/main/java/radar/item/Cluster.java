package radar.item;

public enum Cluster {
	
	STRATEGIC_TOPIC("Strategic Topic", "tag 1 (e.g. category)"), 
	CATEGORY("Category", "tag 2 (e.g. type)"),
	//CA("CA", "tag 3 (e.g.business area)"),
	TOPIC("Topic", "tag 4")
	//,TOPIC_LEAD("Topic Lead", "tag 5")
	;
	
	private String column;
	private String anonymizedColumn;
	
	private static boolean anonymize = false;
	
	public static void setAnonymize(boolean anonymize) {
		Cluster.anonymize = anonymize;
	}
	
	private Cluster(String column, String anonymizedColumn) {
		this.column = column;
		this.anonymizedColumn = anonymizedColumn;
	}
	
	public String getColumn() {
		if (anonymize) {
			return anonymizedColumn;
		} else {
			return column;
		}
	}
	
	public String getRawColumn() {
		return column;
	}

}
