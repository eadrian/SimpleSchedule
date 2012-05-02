import java.util.ArrayList;
import java.util.List;

public class courseSchedule {
		public List<String> quarters;
		public List<String> times;
		public List <String> numbers;
		public List<List <String>> profs;
		public courseSchedule() {
			quarters = new ArrayList<String>();
			times = new ArrayList<String>();
			numbers = new ArrayList<String>();
			profs = new ArrayList<List<String>>();
		}
	}