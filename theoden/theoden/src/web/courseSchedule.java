package web;
import java.util.ArrayList;
import java.util.List;

public class courseSchedule {
		public List<String> quarters;
		public List<String> times;
		public List <String> numbers;
		public List<List <String>> profs;
		public List<Integer> courseTimes;
		public List<String> days;
		public int units;
		public String grading;
		public String type;
		public courseSchedule() {
			quarters = new ArrayList<String>();
			times = new ArrayList<String>();
			numbers = new ArrayList<String>();
			profs = new ArrayList<List<String>>();
			days = new ArrayList<String>();
			courseTimes = new ArrayList<Integer>();
		}
	}