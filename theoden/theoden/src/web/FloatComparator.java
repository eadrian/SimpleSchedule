package web;
import java.util.Comparator;
import java.util.Map;

class FloatComparator implements Comparator {

	  Map base;
	  boolean invert = false;
	  
	  public FloatComparator(Map base, boolean b) {
		  invert = b;
	      this.base = base;
	  }
	  
	  
	  public FloatComparator(Map base) {
	      this.base = base;
	  }

	  public int compare(Object a, Object b) {

	    if((Float)base.get(a) < (Float)base.get(b)) {
	    	return invert? -1:1;
	    } else if((Float)base.get(a) == (Float)base.get(b)) {
	      return ((String)a).compareTo((String)b);
	    } else {
	    	return invert? 1:-1;
	    }
	  }
	}