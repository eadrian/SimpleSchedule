package web;

public class searchFactor {
	public static String[] factors = {"Work","GERs","Schedule","Quality","Popularity","Necessity","Interest"};
	
	
	public boolean enabled=true;
	public float factor = 1;
	public String name="";
	public searchFactor(String name) {
		this.name = name;
	}
}
