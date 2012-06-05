import java.util.ArrayList;
import java.util.List;


public class Schedule {
	public List<scheduleItem> items;
	public Schedule() {
		items = new ArrayList<scheduleItem>();
	}
	public Schedule(Schedule sched) {
		items = new ArrayList<scheduleItem>(sched.items);
	}
	public boolean addItem(String name, String day, int start, int end) {
		if (!checkFit(name,day, start, end)) {
			return false;
		} else {
			scheduleItem i = new scheduleItem(name, day, start, end);
			items.add(i);
			return true;
		}
	}
	public void removeItem(String name) {
		scheduleItem i = new scheduleItem(name,"",0,0);
		while (items.contains(i)) {
			items.remove(i);
		}
	}
	public boolean checkFit(String name, String days, int start, int end) {
		for (int i=0; i<items.size(); i++) {
			if (items.get(i).conflicts(days, start, end)) {
				//System.err.println(name+" schedule conflict with: "+items.get(i).name);
				return false;
			}
		}
		//System.out.println("No Schedule Conflict");
		return true;
	}
	
	
	
	private class scheduleItem {
		public String name;
		private String days;
		private int start;
		private int end;
		private scheduleItem(String name, String days, int start, int end) {
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
}
