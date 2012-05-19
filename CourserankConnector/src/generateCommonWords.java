import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class generateCommonWords {

	private static DBConnection dbc;
	public static Object dbLock;
	
	public Map<String, Integer> tags;
	public Map<String, Integer> sortedTags;
	
	public int TOP_WORD_COUNT=10;
	
	
	public generateCommonWords() {
		List<String> commonWords = new ArrayList<String>();
		dbc = new DBConnection();
		dbLock = new Object();
		tags = new HashMap<String, Integer>();
		ValueComparator bvc =  new ValueComparator(tags, false);
		sortedTags = new TreeMap<String, Integer>(bvc);
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses;" );
			while(rs.next()){
				String tagStr = rs.getString("tags");
				StringTokenizer st = new StringTokenizer(tagStr, " ;,.)/-:(", false);
			    while (st.hasMoreTokens()) {
			    	 String s = st.nextToken().trim();
			    	 if (!tags.containsKey(s)/* && !isCommon(s)*/) {
			    		 tags.put(s, 1);
			    	 } else {
			    		 tags.put(s, tags.get(s)+1);
			    	 }
			    }
				
			}				
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to create new id.");			
		}
		
		for (Map.Entry<String, Integer> e : tags.entrySet()) {
			sortedTags.put(e.getKey(), e.getValue());
		}
		int i=0;
		for (Map.Entry<String, Integer> e : sortedTags.entrySet()) {
			if (i>TOP_WORD_COUNT)
				break;
			commonWords.add(e.getKey());
			System.out.println(e.getKey()+" : "+e.getValue());
			i++;
		}
		enterWords(commonWords);
	}
	
	//Enters top common words into DB of common words, as well as hand entered words (hardcoded by me).
	private void enterWords(List<String> commonWords) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) throws Exception {
		
		new generateCommonWords();
    }
}
