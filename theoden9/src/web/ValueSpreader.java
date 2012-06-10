package web;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class ValueSpreader {
	public static int MAX_SCORE = 100;
	public ValueSpreader() {
		
	}
	public ValueSpreader(int max) {
		MAX_SCORE = max;
	}
	public Map<String, Float> spreadValuesF(Map<String, Float> m, float sFactor, float rFactor) {
		
		Map<String, Float> mprime = new HashMap<String, Float>();
		FloatComparator comp =  new FloatComparator(m, true);
		Map<String, Float> tree = new TreeMap<String, Float>(comp);
		Set<Float> valuesSeen = new HashSet<Float>();
		Float max = (float) 0;
		Float min=(float) 100000000;
		for (Entry<String, Float> e : m.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
			if (e.getValue()>max)
				max = e.getValue();
			if (e.getValue()<min)
				min = e.getValue();
            tree.put(e.getKey(), new Float(e.getValue()));
            if (!valuesSeen.contains(e.getValue()))
            	valuesSeen.add(e.getValue());
        }
		
		int size = m.size();
		System.err.println("Size: "+size);
		int count = 0;
		int numValues = valuesSeen.size();
		float smoothingFactor = sFactor;
		float incFactor = 1 - smoothingFactor; 
		//1.8 70->60     2 60->50 2.3 50->40 2.8 40->30 4 30->20
		//float movingInc = MAX_SCORE / size;
		float movingInc = ((float)MAX_SCORE)/numValues;
		float nextInc = movingInc;
		
		//Integer prevValue = null;
		Float prevValue = min;
		Float value = (float) 0;
		int repeatCount = 1;
		boolean other = false;
		int prevCount=0;
		//Float factor = (max)/((float)(size));
		
		
		for (Map.Entry<String, Float> e : tree.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
			count++;
			if (prevValue!=null && e.getValue().equals(prevValue) && count>1) {
				//repeatCount++;
				//nextInc+=(movingInc/divisor);
						///repeatCount;
			} else {
				/*float remainder = MAX_SCORE-value;
				//Float inc = (remainder / (size-count));
				Float expinc = (remainder / (size-count));
				if (prevValue==null)
					prevValue = 0;
				
				Float factor = (max-prevValue)/((float)(size-count));
				
				Float inc = (e.getValue()-prevValue)*(incFactor*expinc/factor+smoothingFactor*expinc);
				//Float inc = (remainder / (max-e.getValue()))*(e.getValue()-prevValue);*/
				if (prevValue==null)
					prevValue = (float) 0;
				//Float factor = (max-prevValue)/((float)(size-count));
				Float rankFactor = ((MAX_SCORE)*((float)count-prevCount)/size );
				//System.out.println("numValues: "+numValues);
				Float factor = (max-min)/((float)(numValues));
				Float inc = smoothingFactor*nextInc+incFactor*((e.getValue()-prevValue)*(nextInc)/factor);
				//System.out.println("SFactor: "+(nextInc)+" IFactor "+(((e.getValue()-prevValue)*(nextInc)/factor))+" RankFactor: "+rankFactor);
				inc = (float) (inc*(float)(1-rFactor)+rankFactor*(float)(rFactor));
				value = value+inc;
				prevValue = e.getValue();
				//movingInc = (MAX_SCORE-value)/(size-count-1);
				nextInc = movingInc;
				repeatCount=1;
				prevCount=count;
			}
			
			if (other) {
				System.out.println("Name: "+e.getKey()+" Old Value: "+e.getValue()+" Spread Value: "+value);
				other = false;
			} else {
				other = true;
			}
			
			mprime.put(e.getKey(), value);
			/*if (count==(size/2))
				System.err.println("MEDIAN REACHED");*/
            //count++;
        }
		return mprime;
	}
	
	public Map<String, Float> spreadValuesI(Map<String, Integer> m, float sFactor, float rFactor) {
		Map<String, Float> mprime = new HashMap<String, Float>();
		ValueComparator comp =  new ValueComparator(m, true);
		Map<String, Integer> tree = new TreeMap<String, Integer>(comp);
		Set<Integer> valuesSeen = new HashSet<Integer>();
		int max = 0;
		int min=100000000;
		for (Map.Entry<String, Integer> e : m.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
			if (e.getValue()>max)
				max = e.getValue();
			if (e.getValue()<min)
				min = e.getValue();
            tree.put(e.getKey(), new Integer(e.getValue()));
            if (!valuesSeen.contains(e.getValue()))
            	valuesSeen.add(e.getValue());
        }
		
		int size = m.size();
		System.err.println("Size: "+size);
		int count = 0;
		int numValues = valuesSeen.size();
		float smoothingFactor = sFactor;
		float incFactor = 1 - smoothingFactor; 
		//1.8 70->60     2 60->50 2.3 50->40 2.8 40->30 4 30->20
		//float movingInc = MAX_SCORE / size;
		float movingInc = ((float)MAX_SCORE)/numValues;
		float nextInc = movingInc;
		
		//Integer prevValue = null;
		Integer prevValue = min;
		Float value = (float) 0;
		int repeatCount = 1;
		boolean other = false;
		int prevCount=0;
		//Float factor = (max)/((float)(size));
		
		
		for (Map.Entry<String, Integer> e : tree.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
			count++;
			if (prevValue!=null && e.getValue().equals(prevValue) && count>1) {
				//repeatCount++;
				//nextInc+=(movingInc/divisor);
						///repeatCount;
			} else {
				/*float remainder = MAX_SCORE-value;
				//Float inc = (remainder / (size-count));
				Float expinc = (remainder / (size-count));
				if (prevValue==null)
					prevValue = 0;
				
				Float factor = (max-prevValue)/((float)(size-count));
				
				Float inc = (e.getValue()-prevValue)*(incFactor*expinc/factor+smoothingFactor*expinc);
				//Float inc = (remainder / (max-e.getValue()))*(e.getValue()-prevValue);*/
				if (prevValue==null)
					prevValue = 0;
				//Float factor = (max-prevValue)/((float)(size-count));
				Float rankFactor = ((MAX_SCORE)*((float)count-prevCount)/size );
				//System.out.println("numValues: "+numValues);
				Float factor = (max-min)/((float)(numValues));
				Float inc = smoothingFactor*nextInc+incFactor*((e.getValue()-prevValue)*(nextInc)/factor);
				//System.out.println("SFactor: "+(nextInc)+" IFactor "+(((e.getValue()-prevValue)*(nextInc)/factor))+" RankFactor: "+rankFactor);
				inc = (float) (inc*(float)(1-rFactor)+rankFactor*(float)(rFactor));
				value = value+inc;
				prevValue = e.getValue();
				//movingInc = (MAX_SCORE-value)/(size-count-1);
				nextInc = movingInc;
				repeatCount=1;
				prevCount=count;
			}
			
			if (other) {
				System.out.println("Name: "+e.getKey()+" Old Value: "+e.getValue()+" Spread Value: "+value);
				other = false;
			} else {
				other = true;
			}
			
			mprime.put(e.getKey(), value);
			/*if (count==(size/2))
				System.err.println("MEDIAN REACHED");*/
            //count++;
        }
		return mprime;
	}

class ValueComparator implements Comparator {

	  Map base;
	  boolean invert = false;
	  public ValueComparator(Map base, boolean b) {
	      this.base = base;
	      invert = b;
	  }
	  public ValueComparator(Map base) {
	      this.base = base;
	  }

	  public int compare(Object a, Object b) {

	    if((Integer)base.get(a) < (Integer)base.get(b)) {
	      return invert? -1:1;
	      
	    } else if((Integer)base.get(a) == (Integer)base.get(b)) {
	      return ((String)a).compareTo((String)b);
	    } else {
	    	return invert? 1:-1;
	    }
	  }
	}



	public static void main(String[] args) {
		ValueSpreader v = new ValueSpreader();
		
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("1", 1);
		m.put("2", 2);
		m.put("3a", 3);
		m.put("3b", 3);
		m.put("3c", 3);
		m.put("4a", 4);
		m.put("4b", 4);
		m.put("5a", 5);
		m.put("5b", 5);
		m.put("5c", 5);
		m.put("6", 6);
		m.put("7", 7);
		m.put("20a", 20);
		m.put("20b", 20);
		m.put("21", 21);
		m.put("22", 22);
		//v.spreadValuesI(m, (float) .2, 2);
		//v.spreadValuesI(m, (float) .8, 2);
		System.err.println("1");
		v.spreadValuesI(m, (float) .2, (float) .2);
		System.err.println("2");
		v.spreadValuesI(m, (float) .4, (float) .2);
		System.err.println("3");
		v.spreadValuesI(m, (float) .2, (float) .3);
		//v.spreadValuesI(m, (float) 1, 3);
		
	}
	
}
