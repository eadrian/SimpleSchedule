package web;
import java.util.Comparator;

public class CourseSorter {
	public static Comparator<Course> COMPARATOR = new Comparator<Course>()
    {
        public int compare(Course c1, Course c2)
        {
            return (int) (c2.totalScore-c1.totalScore);		
        }
    };
}