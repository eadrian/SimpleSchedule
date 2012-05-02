
/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * A simple example that uses HttpClient to execute an HTTP request against
 * a target site that requires user authentication.
 */
public class CourserankConnector {

    public static void main(String[] args) throws Exception {
    	///////////////////////////////////////
    	//Tagger init
    	
    	//MaxentTagger tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
    	///
    	ImportData importCourse = new ImportData();
        HttpClient httpclient = new DefaultHttpClient();
        httpclient = WebClientDevWrapper.wrapClient(httpclient);
        try {
        	/*
            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(null, -1),
                    new UsernamePasswordCredentials("eadrian", "eactresp1"));
*/
        	//////////////////////////////////////////////////
        	//Get Course Bulletin Departments page
        	List<Course> courses = new ArrayList<Course>();

        	
        	HttpGet httpget = new HttpGet("http://explorecourses.stanford.edu");

            System.out.println("executing request" + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            String bulletinpage = "";
            if (entity != null) {
                //System.out.println("Response content length: " + entity.getContentLength());
                InputStream i = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	bulletinpage +=line;
                	//System.out.println(line);
                }
                br.close();
                i.close();
            }
            EntityUtils.consume(entity);
            
            ///////////////////////////////////////////////////////////////////////////////
            //Login to Courserank
            
            
            httpget = new HttpGet("https://courserank.com/stanford/main");

            System.out.println("executing request" + httpget.getRequestLine());
            response = httpclient.execute(httpget);
            entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            String page = "";
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                InputStream i = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	page +=line;
                	//System.out.println(line);
                }
                br.close();
                i.close();
            }
            EntityUtils.consume(entity);
            ////////////////////////////////////////////////////
            //POST REQUEST LOGIN
            
            
            HttpPost post = new HttpPost("https://www.courserank.com/stanford/main");
            
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
            
            
            pairs.add(new BasicNameValuePair("RT", ""));
            pairs.add(new BasicNameValuePair("action", "login"));
            pairs.add(new BasicNameValuePair("password", "trespass"));
            pairs.add(new BasicNameValuePair("username", "eaconte@stanford.edu"));
            post.setEntity(new UrlEncodedFormEntity(pairs));
            System.out.println("executing request" + post.getRequestLine());
            HttpResponse resp = httpclient.execute(post);
            HttpEntity ent = resp.getEntity();
            
            System.out.println("----------------------------------------");
            if (ent != null) {
                System.out.println("Response content length: " + ent.getContentLength());
                InputStream i = ent.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	//System.out.println(line);
                }
                br.close();
                i.close();
            }
            EntityUtils.consume(ent);
            ///////////////////////////////////////////////////
            //THIS STEP MAY NOT BE NEEDED BUT GETS MAIN PROFILE PAGE
            
            HttpGet gethome = new HttpGet("https://www.courserank.com/stanford/home");
            
            
            System.out.println("executing request" + gethome.getRequestLine());
            HttpResponse gresp = httpclient.execute(gethome);
            HttpEntity gent = gresp.getEntity();
            
            System.out.println("----------------------------------------");
            if (ent != null) {
                System.out.println("Response content length: " + gent.getContentLength());
                InputStream i = gent.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	//System.out.println(line);
                }
                br.close();
                i.close();
            }
            
            
            
            /////////////////////////////////////////////////////////////////////////////////
            //Parse Bulletin
            
            String results = getToken(bulletinpage, "RESULTS HEADER", "Additional Searches");
            String[] depts = results.split("href");
            
            for (int i=1; i<depts.length; i++) {
            	String dept = depts[i];
            	String abbr = getToken(dept, "(", ")");
            	String name = getToken(dept, ">", "(");
            	name.trim();
            	//System.out.println(tagger.tagString(name));
            	String link = getToken(dept, "=\"", "\">");
            	System.out.println(name + " : "+abbr+" : "+link);
            	
            	System.out.println("======================================================================");
            	
            	if (i<=10 || i >127)   //values to keep it to undergraduate courses. Excludes law, med, business, overseas
            		continue;
            	
            	String URL = "http://explorecourses.stanford.edu/"+link+"&filter-term-Autumn=on&filter-term-Winter=on&filter-term-Spring=on";
            	httpget = new HttpGet(URL);

                //System.out.println("executing request" + httpget.getRequestLine());
                response = httpclient.execute(httpget);
                entity = response.getEntity();

                //ystem.out.println("----------------------------------------");
                //System.out.println(response.getStatusLine());
                String rpage = "";
                if (entity != null) {
                    //System.out.println("Response content length: " + entity.getContentLength());
                    InputStream in = entity.getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = br.readLine()) != null) {
                    	rpage +=line;
                    	//System.out.println(line);
                    }
                    br.close();
                    in.close();
                }
                EntityUtils.consume(entity);
            	
                List <Course> deptCourses = new ArrayList<Course>();
                List<Course> result = processResultPage(rpage);
                deptCourses.addAll(result);
                boolean more =(!(result.size()==0) && ( result.get((result.size()-1)).courseNumber < 299));
                boolean morepages = anotherPage(rpage);
                while (morepages && more) {
                	URL = nextURL(URL);
                	httpget = new HttpGet(URL);

                    //System.out.println("executing request" + httpget.getRequestLine());
                    response = httpclient.execute(httpget);
                    entity = response.getEntity();

                    //System.out.println("----------------------------------------");
                    //System.out.println(response.getStatusLine());
                    rpage = "";
                    if (entity != null) {
                        //System.out.println("Response content length: " + entity.getContentLength());
                        InputStream in = entity.getContent();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = br.readLine()) != null) {
                        	rpage +=line;
                        	//System.out.println(line);
                        }
                        br.close();
                        in.close();
                    }
                    EntityUtils.consume(entity);
                    morepages = anotherPage(rpage);
                    result = processResultPage(rpage);
                    deptCourses.addAll(result);
                    more = (!(result.size()==0) && ( result.get((result.size()-1)).courseNumber < 299));
                    /*String mores = more? "yes": "no";
                    String pagess = morepages?"yes":"no";
                    System.out.println("more: "+mores+" morepages: "+pagess);
                    System.out.println("more");*/
                }
                deptCourses = getRatings(httpclient, abbr, deptCourses);
                for (int j = 0; j<deptCourses.size(); j++) {
                	Course c = deptCourses.get(j);
                	System.out.println(""+c.courseTitle+" : "+c.rating);
                	importCourse.writeCourse("A-", c.courseTitle, "descr", "CS", "Programming Abstractions",
							 "Zelenski", "Gates 100", "10", "5", ""+c.rating, "11:00 AM - 12:15 PM", "Mon, Wed, Fri",
							 "Aut, Spr", "GER:DBHum, GER:ECGender", "tag1, tag2, tag3");
                	//System.out.println(tagger.tagString(c.title));
                }
            	
            	
            }
            
            if (!page.equals(""))
            	return;
            
        	///////////////////////////////////////////////////
        	//Get Course Bulletin Department courses 
        	
        	
        	/*
        	
            httpget = new HttpGet("https://courserank.com/stanford/main");

            System.out.println("executing request" + httpget.getRequestLine());
            response = httpclient.execute(httpget);
            entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            page = "";
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                InputStream i = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	page +=line;
                	//System.out.println(line);
                }
                br.close();
                i.close();
            }
            EntityUtils.consume(entity);
            ////////////////////////////////////////////////////
            //POST REQUEST LOGIN
            
            
            HttpPost post = new HttpPost("https://www.courserank.com/stanford/main");
            
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
            
            
            pairs.add(new BasicNameValuePair("RT", ""));
            pairs.add(new BasicNameValuePair("action", "login"));
            pairs.add(new BasicNameValuePair("password", "trespass"));
            pairs.add(new BasicNameValuePair("username", "eaconte@stanford.edu"));
            post.setEntity(new UrlEncodedFormEntity(pairs));
            System.out.println("executing request" + post.getRequestLine());
            HttpResponse resp = httpclient.execute(post);
            HttpEntity ent = resp.getEntity();
            
            System.out.println("----------------------------------------");
            if (ent != null) {
                System.out.println("Response content length: " + ent.getContentLength());
                InputStream i = ent.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	//System.out.println(line);
                }
                br.close();
                i.close();
            }
            EntityUtils.consume(ent);
            ///////////////////////////////////////////////////
            //THIS STEP MAY NOT BE NEEDED BUT GETS MAIN PROFILE PAGE
            
            HttpGet gethome = new HttpGet("https://www.courserank.com/stanford/home");
            
            
            System.out.println("executing request" + gethome.getRequestLine());
            HttpResponse gresp = httpclient.execute(gethome);
            HttpEntity gent = gresp.getEntity();
            
            System.out.println("----------------------------------------");
            if (ent != null) {
                System.out.println("Response content length: " + gent.getContentLength());
                InputStream i = gent.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	//System.out.println(line);
                }
                br.close();
                i.close();
            }
            
            
            ////////////////////////////////////////
            //GETS FIRST PAGE OF RESULTS
            EntityUtils.consume(gent);
            
            post = new HttpPost("https://www.courserank.com/stanford/search_results");
            
            pairs = new ArrayList<NameValuePair>(2);
            
            
            pairs.add(new BasicNameValuePair("filter_term_currentYear", "on"));
            pairs.add(new BasicNameValuePair("query", ""));
            post.setEntity(new UrlEncodedFormEntity(pairs));
            System.out.println("executing request" + post.getRequestLine());
            resp = httpclient.execute(post);
            ent = resp.getEntity();
            
            System.out.println("----------------------------------------");
            
            String rpage = "";
            if (ent != null) {
                System.out.println("Response content length: " + ent.getContentLength());
                InputStream i = ent.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	rpage += line;
                	System.out.println(line);
                }
                br.close();
                i.close();
            }
            EntityUtils.consume(ent);
            
            ////////////////////////////////////////////////////
            //PARSE FIRST PAGE OF RESULTS
            
            //int index = rpage.indexOf("div class=\"searchItem");
            String []classSplit = rpage.split("div class=\"searchItem");
            for (int i=1; i<classSplit.length; i++) {
            	String str = classSplit[i];
            	
            	//ID
            	String CID = getToken(str, "course?id=","\">");
            	
            	// CODE 
            	String CODE = getToken(str,"class=\"code\">" ,":</");
            	
            	//TITLE 
            	String NAME = getToken(str, "class=\"title\">","</");
            	
            	//DESCRIP
            	String DES = getToken(str, "class=\"description\">","</");
            	
            	//TERM
            	String TERM = getToken(str, "Terms:", "|");
            	
            	//UNITS
            	String UNITS = getToken(str, "Units:", "<br/>");

            	//WORKLOAD
            	
            	String WLOAD = getToken(str, "Workload:", "|");
            	
            	//GER
            	String GER = getToken(str, "GERs:", "</d");
            	
            	//RATING
            	int searchIndex = 0;
            	float rating = 0;
            	while (true) {
            		int ratingIndex = str.indexOf("large_Full", searchIndex);
            		if (ratingIndex ==-1) {
            			int halfratingIndex = str.indexOf("large_Half", searchIndex);
            			if (halfratingIndex == -1)
            				break;
            			else
            				rating += .5;
            			break;
            		}
            		searchIndex = ratingIndex+1;
            		rating++;
            			
            	}
            	String RATING = ""+rating;
            	
            	//GRADE
            	String GRADE = getToken(str, "div class=\"unofficialGrade\">", "</");
            	if (GRADE.equals("NOT FOUND")) {
            		GRADE = getToken(str, "div class=\"officialGrade\">", "</");
            	}
            	
            	//REVIEWS
            	String REVIEWS = getToken(str, "class=\"ratings\">", " ratings");
            	
            	
            	System.out.println(""+CODE+" : "+NAME + " : "+CID);
            	System.out.println("----------------------------------------");
            	System.out.println("Term: "+TERM+" Units: "+UNITS+ " Workload: "+WLOAD + " Grade: "+ GRADE);
            	System.out.println("Rating: "+RATING+ " Reviews: "+REVIEWS);
            	System.out.println("==========================================");
            	System.out.println(DES);
            	System.out.println("==========================================");
            	
            	
            	
            }
            
            
            ///////////////////////////////////////////////////
            //GETS SECOND PAGE OF RESULTS
            post = new HttpPost("https://www.courserank.com/stanford/search_results");
            
            pairs = new ArrayList<NameValuePair>(2);
            
            
            pairs.add(new BasicNameValuePair("filter_term_currentYear", "on"));
            pairs.add(new BasicNameValuePair("page", "2"));
            pairs.add(new BasicNameValuePair("query", ""));
            post.setEntity(new UrlEncodedFormEntity(pairs));
            System.out.println("executing request" + post.getRequestLine());
            resp = httpclient.execute(post);
            ent = resp.getEntity();
            
            System.out.println("----------------------------------------");
            if (ent != null) {
                System.out.println("Response content length: " + ent.getContentLength());
                InputStream i = ent.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	//System.out.println(line);
                }
                br.close();
                i.close();
            }
            EntityUtils.consume(ent);
            
            /*
            httpget = new HttpGet("https://github.com/");

            System.out.println("executing request" + httpget.getRequestLine());
            response = httpclient.execute(httpget);
            entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            page = "";
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                InputStream i = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(i));
                String line;
                while ((line = br.readLine()) != null) {
                	page +=line;
                	System.out.println(line);
                }
                br.close();
                i.close();
            }*/
            EntityUtils.consume(entity);
            
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
    
    private static List<Course> getRatings(HttpClient httpclient, String abbr, List<Course> deptCourses) {
////////////////////////////////////////
//GETS FIRST PAGE OF RESULTS
		try {
			boolean next = true;
			int p = 0;
			while (next) {
				next = false;
				
				HttpPost post = new HttpPost("https://www.courserank.com/stanford/search_results");
				
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
				
				
				pairs.add(new BasicNameValuePair("filter_term_currentYear", "on"));
				pairs.add(new BasicNameValuePair("page", ""+p));
				pairs.add(new BasicNameValuePair("query", abbr));
				
				post.setEntity(new UrlEncodedFormEntity(pairs));
				System.out.println("executing request" + post.getRequestLine());
				HttpResponse resp = httpclient.execute(post);
				HttpEntity ent = resp.getEntity();
				
				System.out.println("----------------------------------------");
				
				String rpage = "";
				if (ent != null) {
					System.out.println("Response content length: " + ent.getContentLength());
					InputStream i = ent.getContent();
					BufferedReader br = new BufferedReader(new InputStreamReader(i));
					String line;
					while ((line = br.readLine()) != null) {
						rpage += line;
						//System.out.println(line);
					}
					br.close();
					i.close();
				}
				EntityUtils.consume(ent);
				
				next = rpage.contains("pageNavNext");
				
				////////////////////////////////////////////////////
				//PARSE FIRST PAGE OF RESULTS
				
				//int index = rpage.indexOf("div class=\"searchItem");
				String []classSplit = rpage.split("div class=\"searchItem");
				p++;
				for (int i=1; i<classSplit.length; i++) {
					String str = classSplit[i];
					
					//ID
					String CID = getToken(str, "course?id=","\">");
					
					// CODE 
					String CODE = getToken(str,"class=\"code\">" ,":</");
					
					//TITLE 
					String NAME = getToken(str, "class=\"title\">","</");
					
					//DESCRIP
					String DES = getToken(str, "class=\"description\">","</");
					
					//TERM
					String TERM = getToken(str, "Terms:", "|");
					
					//UNITS
					String UNITS = getToken(str, "Units:", "<br/>");
					
					//WORKLOAD
					
					String WLOAD = getToken(str, "Workload:", "|");
					
					//GER
					String GER = getToken(str, "GERs:", "</d");
					
					//RATING
					int searchIndex = 0;
					double rating = 0;
					while (true) {
						int ratingIndex = str.indexOf("large_Full", searchIndex);
						if (ratingIndex ==-1) {
							int halfratingIndex = str.indexOf("large_Half", searchIndex);
							if (halfratingIndex == -1)
								break;
							else
								rating += .5;
							break;
						}
						searchIndex = ratingIndex+1;
						rating++;
					
					}
					String RATING = ""+rating;
					
					//GRADE
					String GRADE = getToken(str, "div class=\"unofficialGrade\">", "</");
					if (GRADE.equals("NOT FOUND")) {
					GRADE = getToken(str, "div class=\"officialGrade\">", "</");
					}
					
					//REVIEWS
					String REVIEWS = getToken(str, "class=\"ratings\">", " rating");
				
				
					
					Course c = new Course();
					c.courseTitle = CODE;
				
					if (deptCourses.contains(c)) {
						c=deptCourses.get(deptCourses.indexOf(c));
						c.rating = rating;
						deptCourses.set(deptCourses.indexOf(c), c);
					} else {
						try {
							String[] nameParts = CODE.split(" ");
			    			int num = Integer.parseInt(nameParts[1]);
			    			/*if (num > 299) {
			        			next = false;
			        		}*/
			    		} catch (NumberFormatException e) {
			    			
			    		}
					}
					/*System.out.println(""+CODE+" : "+NAME + " : "+CID);
					System.out.println("----------------------------------------");
					System.out.println("Term: "+TERM+" Units: "+UNITS+ " Workload: "+WLOAD + " Grade: "+ GRADE);
					System.out.println("Rating: "+RATING+ " Reviews: "+REVIEWS);
					System.out.println("==========================================");
					System.out.println(DES);
					System.out.println("==========================================");*/
					
		
				}
				EntityUtils.consume(ent);
			}
			return deptCourses;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		
		}
		return null;
		
	}

	private static boolean anotherPage(String page) {
    	
    	boolean next = (page.contains(">next") || page.contains("> next"));
		return next;
	}

	private static String nextURL(String url) {
		String pageToken = "page=";
		int index = url.indexOf(pageToken);
		int pageno = Integer.parseInt(url.substring(index+pageToken.length(),url.indexOf("&q=") ));
		int newpageno = pageno+1;
		String newurl = url.replace("page="+pageno, "page="+newpageno);
		return newurl;
	}

	public static String getToken(String str, String start, String end) {
    	String Patt = start;
    	int index = str.indexOf(Patt);
    	if (index == -1)
    		return "NOT FOUND";
    	index += Patt.length();
    	String token = str.substring(index, index+str.substring(index).indexOf(end));
    	return token.trim();
    }
	
	
    
    public static List<Course> processResultPage(String page) {
    	
    	String[] cresults = page.split("class=\"searchResult");
    	List<Course> results = new ArrayList<Course>();
    	for (int i=1; i<cresults.length; i++) {
    		String cresult = cresults[i];
    		//System.out.println(cresult);
    		
    		String cname = getToken(cresult, "courseNumber\">", ":<");
    		cname.trim();
    		
    		String[] nameParts = cname.split(" ");
    		int num=-1;
    		try {
    			num = Integer.parseInt(nameParts[1]);
    			if (num > 299) {
        			return results;
        		}
    		} catch (NumberFormatException e) {
    			
    		}
    		
    		String ctitle = getToken(cresult, "courseTitle\">", "</");
    		
    		String cdesc = getToken(cresult, "courseDescription\">", "</div");
    		boolean FreshSem = cdesc.contains("Preference to freshmen");
    	    boolean SophSem = cdesc.contains("Preference to sophomores");
    	    /*
    	    if (cdesc.contains("Prerequisites:")) {
    	    	String prereqs = cdesc.substring(cdesc.indexOf("Prerequisites:"));
    	    	StringTokenizer st = new StringTokenizer(prereqs," ,;",false);
    	    	List<String> tokens = new ArrayList<String>();
    	        while (st.hasMoreTokens()) {
    	        	String token = st.nextToken();
    	            tokens.add(token);
    	            char c = token.charAt(0);
    	            if (Character.isDigit(c)) {
    	            	
    	            }
    	        }
    	        
    	    }
    	    */
    	    boolean sem = cresult.contains("| SEM");
    		List<String> profs = new ArrayList<String>();
    		List<Boolean> starred = new ArrayList();
    		int sindex  = 0;
    		while (true) {
    			boolean star = false;
    			String Patt = "instructorLink\">";
    	    	int index = cresult.indexOf(Patt, sindex);
    	    	
    	    	
    	    	if (index == -1)
    	    		break;
    	    	
    	    	String starFile = "gold-star.jpg";
    	    	
    	    	
    	    	sindex = index+1;
    	    	index += Patt.length();
    	    	String token = cresult.substring(index, index+cresult.substring(index).indexOf("</"));
    	    	if (token.contains(starFile)) {
    	    		token = getToken(token, "16px;\" />", ")")+")";
    	    		star = true;
    	    	}
    	    	if (star)
    	    		System.out.println(token);
    	    	profs.add(token);
    	    	starred.add(star);
    	    	
    		}
    		courseSchedule cs = getSchedule(cresult);
    		if (cs.quarters.size() == 0)
    			continue;
    		for (int x=0; x<cs.quarters.size(); x++) {
    			/*System.out.println(cs.quarters.get(x)+" "+cs.times.get(x));
    			System.out.println(cs.numbers.get(x));
    			System.out.println(cs.profs.get(x));*/
    		}
    		Course c = new Course();
    		c.courseTitle = cname;
    		c.courseNumber = num;
    		//c.title = ctitle;
    		results.add(c);
    		//System.out.println(cname + " : "+ ctitle);
    		//System.out.println(cdesc);
    	}
    	return results;
    	
    	
    	
    	
    }
    
    static int YEAR = 2012;

	private static courseSchedule getSchedule(String cresult) {
		courseSchedule cs = new courseSchedule();
		String[] termInfo = cresult.split("class=\"sectionContainerTerm");
		for (int i=1; i < termInfo.length; i++) {
			String info = termInfo[i];
			String term = "";
			String year = "";
			if (info.contains(YEAR+" Autumn")) {
				term = "Autumn";
				year = ""+(YEAR-1);
			} else if (info.contains(YEAR+" Winter")) {
				term = "Winter";
				year = ""+YEAR;
			} else if (info.contains(YEAR+" Spring")) {
				term = "Spring";
				year = ""+YEAR;
			} else if (info.contains(YEAR+" Summer")) {
				term = "Summer";
				year = ""+YEAR;
			}
			
			String number = getToken(info, "Class #", "|");
			String[] times = info.split("/"+year+" - (\\d){2}/(\\d){2}/"+year);
			if (times.length == 1) {
				continue;
			}
			int endindex = (times[1].indexOf("AM") < times[1].indexOf("PM") && times[1].indexOf("AM")>-1) ? times[1].indexOf("AM") : times[1].indexOf("PM");
			endindex += 2;
			
			if (endindex == -1) {
				//System.out.println(cresult);
				System.out.println(times[1]);
			}
			String time = times[1].substring(0, endindex);
			
			
			
			String professors = getToken(info, "Instructors: </span>", "<");
			String[] profs = professors.split(";");
			List<String> teachers = Arrays.asList(profs);
			cs.quarters.add(term);
			cs.numbers.add(number);
			cs.times.add(time);
			cs.profs.add(teachers);
			
		}
		return cs;
	}
    
    
}

