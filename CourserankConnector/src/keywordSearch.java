import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

//import dataProcessor.ValueComparator;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class keywordSearch {
	private static DBConnection dbc;
	public static Object dbLock;
	public keywordSearch (String search, List<String> courses) {
		dbc = new DBConnection();
		dbLock = new Object();
		
		List<String> keywordList = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(search, " ;,.)/-:(", false);
		List<AttrVal> match = new ArrayList<AttrVal>();
	    while (st.hasMoreTokens()) {
	    	Stemmer stemmer = new Stemmer();
	    	String token = st.nextToken().trim().toLowerCase();
	    	keywordList.add(stemmer.stemString(token));
	    	AttrVal a = new AttrVal();
	    	a.like = true;
	    	a.type = "String";
	    	a.attr = "tags";
	    	a.val = "%"+stemmer.stemString(token)+"%";
	    	match.add(a);
	    }
	    List<AttrVal> request = new ArrayList<AttrVal>();
	    AttrVal r = new AttrVal();
	    r.type="String";
	    r.attr = "code";
	    
	    AttrVal r2 = new AttrVal();
	    r2.type="float";
	    r2.attr = "qScore";
	    
	    AttrVal r3 = new AttrVal();
	    r3.type="float";
	    r3.attr = "wScore";
	    
	    AttrVal r4 = new AttrVal();
	    r4.type="float";
	    r4.attr = "nScore";
	    
	    AttrVal r5 = new AttrVal();
	    r5.type="String";
	    r5.attr = "tags";
	    
	    request.add(r);
	    request.add(r2);
	    request.add(r3);
	    request.add(r4);
	    request.add(r5);
	    List<List<AttrVal>> results = dbc.getAttributesThatMatch("rhun_courses", request, match);
	    
	    
	    Map<String, Float> rateMap = new TreeMap<String, Float>();
		FloatComparator bvc =  new FloatComparator(rateMap);
		Map<String, Float> sortedResults = new TreeMap<String, Float>(bvc);
	    
	    for (int i=0; i<results.size(); i++) {
	    	
	    	String code = results.get(i).get(0).val;
			System.out.println("Code: "+code);
			float q = Float.parseFloat(results.get(i).get(1).val);
			float w = Float.parseFloat(results.get(i).get(2).val);
			float n = Float.parseFloat(results.get(i).get(3).val);
			if (!rateMap.containsKey(code)) {
				rateMap.put(code, q+w+n);
				sortedResults.put(code, q+w+n);
			}
	    }
	    
	    for (Map.Entry<String, Float> e : sortedResults.entrySet()) {
            System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
            
        }
		String codeNum = "";
	     /*for (int j=0; j<c.code.length(); j++) {
	    	 if (Character.isDigit(c.code.charAt(j))) {
	    		 codeNum = codeNum+c.code.charAt(j);
	    	 }
	     }*/
	}
	
	class FloatComparator implements Comparator {

		  Map base;
		  public FloatComparator(Map base) {
		      this.base = base;
		  }

		  public int compare(Object a, Object b) {

		    if((Float)base.get(a) < (Float)base.get(b)) {
		      return 1;
		    } else if((Float)base.get(a) == (Float)base.get(b)) {
		      return ((String)a).compareTo((String)b);
		    } else {
		      return -1;
		    }
		  }
		}
}
