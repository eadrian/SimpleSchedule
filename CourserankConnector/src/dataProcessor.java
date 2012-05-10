
public class dataProcessor {
	private static DBConnection dbc;
	public static Object dbLock;
	
	
	
	public dataProcessor() {
		dbc = new DBConnection();
		dbLock = new Object();
		calculateAverageRatings();
	}
	
	
	
	public void calculateAverageRatings() {
		int num = dbc.getNextID("rhun_lecturers");
		for (int i=1; i<num; i++) {
			
			//dbc.getAttribute("rhun_courses", ID, attribute);
		}
	}
	
	
	public static void main(String[] args) {
		new dataProcessor();
	}
}
