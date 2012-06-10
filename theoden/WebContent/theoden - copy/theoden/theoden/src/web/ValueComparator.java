package web;
import java.util.Comparator;
import java.util.Map;

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