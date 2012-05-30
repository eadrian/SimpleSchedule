import java.util.HashMap;
import java.util.Map;


public class searchFactors {
	public static String[] factors = {"WORK","GERS","SCHEDULE","QUALITY","POPULARITY","NEED","INTEREST", "RELEVANCE", "LEVEL", "INDEPENDENT", "PROJECT", "TOTAL", "PREREQS"};
	public Map<String, Float> factorWeight;
	
	
	public searchFactors() {
		factorWeight = new HashMap<String, Float>();
		for (int i=0; i<factors.length; i++) {
			factorWeight.put(factors[i], (float) 0);
		}
	}
	public void setFactor(String name, float weight) {
		if (factorWeight.containsKey(name)) {
			factorWeight.put(name, weight);
		}
	}
}
