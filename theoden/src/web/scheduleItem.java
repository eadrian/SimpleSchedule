package web;


public class scheduleItem {
	public String name;
	public String days;
	public int start;
	public int end;
	public scheduleItem(String name, String days, int start, int end) {
		this.name = name;
		this.days = days;
		this.start = start;
		this.end = end;
		
	}
	public boolean conflicts(String d, int s, int e) {
		
		
		if (d.length()<this.days.length())
			return true;
		for (int i=0; i<this.days.length(); i++) {
			if (d.charAt(i)=='1'&& this.days.charAt(i)=='1') {
				if ((start<=s && end>=s) || (start<=e && end>=e) || (start>=s && end<=e))
					return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof scheduleItem))return false;
	    scheduleItem otherItem = (scheduleItem)other;
	    return otherItem.name.equals(this.name);
	}
}