
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
 *Connects to explorecourses and Courserank to gather course data and write to DB.
 */
public class CourserankConnector {

    public static void main(String[] args) throws Exception {
    	///////////////////////////////////////
    	//Tagger init
    	
    	//MaxentTagger tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
    	///
    	//CLIENT INITIALIZATION
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
            
            //STORE RETURNED HTML TO BULLETINPAGE
            
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
            
            //SPLIT FOR EACH DEPARTMENT LINK, ITERATE
            boolean ready = false;
            for (int i=1; i<depts.length; i++) {
            	//EXTRACT LINK, DEPARTMENT NAME AND ABBREVIATION
            	String dept = new String(depts[i]);
            	String abbr = getToken(dept, "(", ")");
            	String name = getToken(dept, ">", "(");
            	name.trim();
            	//System.out.println(tagger.tagString(name));
            	String link = getToken(dept, "=\"", "\">");
            	System.out.println(name + " : "+abbr+" : "+link);
            	
            	System.out.println("======================================================================");
            	
            	if (i<=10 || i >=127)   //values to keep it to undergraduate courses. Excludes law, med, business, overseas
            		continue;
            	/*if (i<=46)
            		continue; */        //Start at BIOHOP
            	/*if (abbr.equals("INTNLREL"))
            		ready = true;
            	if (!ready)
            		continue;*/
            	//Construct department course search URL
            	//Then request page
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
            	
                
                
                //Process results page
                List <Course> deptCourses = new ArrayList<Course>();
                List<Course> result = processResultPage(rpage);
                deptCourses.addAll(result);
                
                //While there are more result pages, keep going
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
                
                //Get course ratings for all department courses via courserank
                deptCourses = getRatings(httpclient, abbr, deptCourses);
                for (int j = 0; j<deptCourses.size(); j++) {
                	Course c = deptCourses.get(j);
                	System.out.println(""+c.title+" : "+c.rating);
                	c.tags = name;
                	c.code = c.code.trim();
                	c.department = name;
                	c.deptAB = abbr;
                	c.writeToDatabase();
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
    
    
    //Uses courserank to get course information.  Takes a list of department courses, the string abbreviation for the department, eg CS
    //And the httpClient object.
    private static List<Course> getRatings(HttpClient httpclient, String abbr, List<Course> deptCourses) {
////////////////////////////////////////
//GETS FIRST PAGE OF RESULTS
		try {
			boolean next = true;
			int p = 0;
			while (next) {
				next = false;
				//Post a search request for courserank
				HttpPost post = new HttpPost("https://www.courserank.com/stanford/search_results");
				
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
				
				//Only filter for current year
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
				
				//Check if there is a next page
				next = rpage.contains("pageNavNext");
				
				////////////////////////////////////////////////////
				//PARSE FIRST PAGE OF RESULTS
				
				//int index = rpage.indexOf("div class=\"searchItem");
				String []classSplit = rpage.split("div class=\"searchItem");
				p++;
				for (int i=1; i<classSplit.length; i++) {
					
					//Extract many useful bits of information
					String str = new String(classSplit[i]);
					
					//ID
					String CID = getToken(str, "course?id=","\">");
					
					// CODE 
					String CODE = getToken(str,"class=\"code\">" ,":</").trim();
					
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
					String GER = getToken(str, "GERs:", "</d").trim();
					
					//RATING
					//Construct rating based on number of star images
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
					//Use either official or unofficial grade whichever is present
					String GRADE = getToken(str, "div class=\"unofficialGrade\">", "</");
					if (GRADE.equals("NOT FOUND")) {
					GRADE = getToken(str, "div class=\"officialGrade\">", "</");
					}
					
					//REVIEWS
					String REVIEWS = getToken(str, "class=\"ratings\">", " rating");
				
					//Copy information for each quarter instance of a course in the list of courses.
					for (int j=0; j<4;j++) {
						Course c = new Course();
						c.code = CODE;
						if (j==0) {
							c.quarter = "Autumn";
						} else if (j==1) {
							c.quarter = "Winter";
						} else if (j==2) {
							c.quarter = "Spring";
						} else if (j==3) {
							c.quarter = "Summer";
						} 
						
						//If course is present in department course list from exploreCourses.  Then update info.
						if (deptCourses.contains(c)) {
							c=deptCourses.get(deptCourses.indexOf(c));
							if (c==null)
								System.out.println("EMERGENCY");
							c.avgGrade = GRADE;
							c.rating = rating;
							c.numReviews = Integer.parseInt(REVIEWS);
							//c.ID = Integer.parseInt(CID);
							c.rating = rating;
							c.numReviews = Integer.parseInt(REVIEWS);
							/*
							try {
								c.numUnits = Integer.parseInt(UNITS);
							} catch (NumberFormatException e) {
								System.out.print(UNITS);
								int in = UNITS.indexOf("-");
								c.numUnits = Integer.parseInt(UNITS.substring(in+1,in+2));
							}*/
							
							//Parse out workload and just use the average if two numbers are present, otherwise just use number.
							WLOAD = WLOAD.trim();
							if (WLOAD.contains("&")) {
								WLOAD = (WLOAD.substring(WLOAD.indexOf(" "))).trim();
							}
							int wload = -1;
							if (WLOAD.contains("-")) {
								wload = (Integer.parseInt(WLOAD.split("-")[0]) + Integer.parseInt(WLOAD.split("-")[1].substring(0,WLOAD.split("-")[1].indexOf(" "))))/2;
							} else if (!WLOAD.contains("NA")){
								wload = Integer.parseInt(WLOAD.substring(0, WLOAD.indexOf(" ")));
							}
							//System.out.println("Workload: "+wload);
							c.workload = wload;
							//System.out.println(GER);
							c.universityReqs=GER;
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
    //Just checks if another page exists in the exploreCourses results.
	private static boolean anotherPage(String page) {
    	
    	boolean next = (page.contains(">next") || page.contains("> next"));
		return next;
	}
	//Given a page url for exploreCourses, gets the next page of results url.
	private static String nextURL(String url) {
		String pageToken = "page=";
		int index = url.indexOf(pageToken);
		int pageno = Integer.parseInt(url.substring(index+pageToken.length(),url.indexOf("&q=") ));
		int newpageno = pageno+1;
		String newurl = url.replace("page="+pageno, "page="+newpageno);
		return newurl;
	}
	//Given a source string, string (start substring), and an end string substring
	//Returns the string between the start string and endstring int he source string
	//Returns string "NOT FOUND" if one of the start or end tokens are not found or out of order.
	public static String getToken(String str, String start, String end) {
    	String Patt = start;
    	int index = str.indexOf(Patt);
    	if (index == -1)
    		return "NOT FOUND";
    	index += Patt.length();
    	String token = new String(str.substring(index, index+str.substring(index).indexOf(end)));
    	return token.trim();
    }
	
	//Processes a page of exploreCourses results.
    
    public static List<Course> processResultPage(String page) {
    	
    	String[] cresults = page.split("class=\"searchResult");
    	//Splits along each course result.
    	List<Course> results = new ArrayList<Course>();
    	for (int i=1; i<cresults.length; i++) {
    		String cresult = new String(cresults[i]);
    		//System.out.println(cresult);
    		
    		String cname = getToken(cresult, "courseNumber\">", ":<");
    		cname.trim();
    		//IF COURSE NUMBER IS OVER 299, JUST END.  I CUT OFF RESULTS AT THAT POINT.
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
    	    boolean sem = cresult.contains("SEM");
    		List<String> profs = new ArrayList<String>();
    		List<Boolean> starred = new ArrayList();
    		int sindex  = 0;
    		//This iterates through the list of instructors for a course.
    		//Searches to see if they got a star award.  Saves that data in the starred list.
    		//Also extracts professor names.
    		while (true) {
    			boolean star = false;
    			String Patt = "instructorLink\">";
    	    	int index = cresult.indexOf(Patt, sindex);
    	    	
    	    	
    	    	if (index == -1)
    	    		break;
    	    	
    	    	String starFile = "gold-star.jpg";
    	    	
    	    	
    	    	sindex = index+1;
    	    	index += Patt.length();
    	    	String token = new String(cresult.substring(index, index+cresult.substring(index).indexOf("</")));
    	    	//System.out.println("Token name: "+token);
    	    	token = token.trim();
    	    	if (token.contains(starFile)) {
    	    		token = getToken(token, "16px;\" />", ")")+")";
    	    		star = true;
    	    	}
    	    	if (star)
    	    		System.out.println(token);
    	    	profs.add(token);
    	    	starred.add(star);
    	    	
    		}
    		
    		//Create a new course object to add to the list.
    		//Here get the schedule
    		courseSchedule cs = getSchedule(cresult);
    		Course c = new Course();
    		c.code = cname;
    		c.courseNumber = num;
    		c.title = ctitle;
    		c.description = cdesc;
    		c.type = sem ? "Seminar" : "Lecture";
    		if (sem)
    			System.out.println("SEMINAR");
    		if (cs.quarters.size() == 0)
    			continue;
    		for (int x=0; x<cs.quarters.size(); x++) {
    			
    			/*System.out.println(cs.quarters.get(x)+" "+cs.times.get(x));
    			System.out.println(cs.numbers.get(x));
    			System.out.println(cs.profs.get(x));*/
    			if (cs.quarters.size()>1)
    				System.out.println("Adding more than one");
    			Course c1 = new Course(c);
    			c1.lectureDays = cs.days.get(x);
    			c1.lecturers = cs.profs.get(x);
    			for (int j =0; j<c1.lecturers.size(); j++) {
    				String prof = c1.lecturers.get(j).trim();
    				if (profs.contains(prof)) {
    					//System.out.println("Good found.");
    					int index = profs.indexOf(prof);
    					if (starred.get(index)) {
    						System.out.println("Found Star Match:  "+prof);
    						prof = "*"+prof;
    						c1.lecturers.set(j, prof);
    					}
    				}
    			}
    			c1.timeBegin = cs.courseTimes.get(2*x);
    			c1.timeEnd = cs.courseTimes.get(2*x+1);
    			c1.quarter = cs.quarters.get(x);
    			System.out.println("Course "+c1.code+" Added as: "+c1.quarter);
    			c1.ID = Integer.parseInt(cs.numbers.get(x));
    			c1.numUnits = cs.units;
    			c1.grading = cs.grading;
    			c1.type = cs.type;
    			results.add(c1);
    		}
    		
    		
    	
    		//c.title = ctitle;
    		
    		//System.out.println(cname + " : "+ ctitle);
    		//System.out.println(cdesc);
    	}
    	return results;
    	
    	
    	
    	
    }
    
    static int YEAR = 2012;
    //Given a individual string of all the explorecourses info for a given course
    //Extracts a courseSchedule object for that course.
	private static courseSchedule getSchedule(String cresult) {
		courseSchedule cs = new courseSchedule();
		String[] termInfo = cresult.split("class=\"sectionContainerTerm");
		for (int i=1; i < termInfo.length; i++) {
			String info = new String(termInfo[i]);
			String term = "";
			String year = "";
			//Get term and calculate year.
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
			//System.out.println(info);
			//Get course number
			String number = getToken(info, "Class #", "|");
			System.out.println("Num: "+number);
			String[] times = info.split("/"+year+" - (\\d){2}/(\\d){2}/"+year);
			if (times.length == 1) {
				continue;
			}
			if (info.contains("CANCELLED"))
				continue;
			
			
			//Get integers representing start time and end time.
			//Start time for 9:00 AM would be 900.  10:50 AM would be 1050.
			int endindex = (times[1].indexOf("AM") < times[1].indexOf("PM") && times[1].indexOf("AM")>-1 || times[1].indexOf("PM")==-1) ? times[1].indexOf("AM") : times[1].indexOf("PM");
			endindex += 2;
			
			if (endindex == 1) {
				//System.out.println(cresult);
				System.out.println(times[1]);
			}
			
			
			
			String time = new String(times[1].substring(0, endindex));
			
			int startTime = -1;
			for (int x = 0; x<time.length(); x++) {
				if (Character.isDigit(time.charAt(x))) {
					int colonIndex = time.indexOf(":");
					
					boolean am = time.contains("AM");
					startTime = am? 0 : 1200;
					int hoursTime = Integer.parseInt(time.substring(x, colonIndex))*100;
					
					if (hoursTime==1200) {
						hoursTime = 0;
					}
					int minTime = Integer.parseInt(time.substring(colonIndex+1, colonIndex+3));
					startTime+= hoursTime+minTime;
					//System.out.println("START TIME " + startTime);
					break;
				}
			}
			
			String endTimeS = new String(times[1].substring(times[1].indexOf("-")));
			int AMINDEX = endTimeS.indexOf("AM");
			int PMINDEX = endTimeS.indexOf("PM");
			if (AMINDEX==-1) {
				endindex = 2+PMINDEX;
			} else if (PMINDEX==-1) {
				endindex = 2+AMINDEX;
			} else {
				endindex = AMINDEX < PMINDEX ? AMINDEX: PMINDEX;
				endindex +=2;
			}
			if (endindex ==1) {
				System.out.println("ERROR IN TIME CALCULATION");
				
			}
			endTimeS = new String(endTimeS.substring(1, endindex));
			
			int endTime = -1;
			
			for (int x = 0; x<endTimeS.length(); x++) {
				if (Character.isDigit(endTimeS.charAt(x))) {
					int colonIndex = endTimeS.indexOf(":");
					
					boolean am = endTimeS.contains("AM");
					endTime = am? 0 : 1200;
					int hoursTime = Integer.parseInt(endTimeS.substring(x, colonIndex))*100;
					if (hoursTime==1200) {
						hoursTime = 0;
					}
					int minTime = Integer.parseInt(endTimeS.substring(colonIndex+1, colonIndex+3));
					endTime+= hoursTime+minTime;
					//System.out.println("END TIME " + endTime);
					break;
				}
			}
			
			
			//System.out.println("TIME: "+time);
			
			/////////////////////////////////////////////////////
			//Parse Days
			
			//Construct a days of the week string of the form 01010
			//Where each character refers to a day of the week.  zero means no class that day, one means class.
			String days = "";
			String dayString = new String(times[1].substring(0, times[1].indexOf("-")));
			if (dayString.contains("Mon")) {
				days+="1";
			} else {
				days+="0";
			}
			if (dayString.contains("Tue")) {
				days+="1";
			} else {
				days+="0";
			}
			if (dayString.contains("Wed")) {
				days+="1";
			} else {
				days+="0";
			}
			if (dayString.contains("Thu")) {
				days+="1";
			} else {
				days+="0";
			}
			if (dayString.contains("Fri")) {
				days+="1";
			} else {
				days+="0";
			}
			
			//System.out.println("Sched: "+days);
			String professors = getToken(info, "Instructors: </span>", "<");
			String grading = "";
			if (info.contains("SEM"))
				System.out.println("SEMINAR FOUND");
			if (info.contains("Letter")) {
				grading+="Letter";
			}
			if (info.contains("No Credit")) {
				if (info.contains("Letter")) {
					grading+=" or ";
				}
				grading+="Credit/No Credit";
			}
			String[] profs = professors.split(";");
			for (int j=0; j<profs.length; j++) {
				profs[j] = profs[j].trim();
			}
			List<String> teachers = Arrays.asList(profs);
			if (info.indexOf(" unit")==-1)
				continue;
			cs.quarters.add(term);
			cs.numbers.add(number);
			cs.times.add(time);
			cs.profs.add(teachers);
			cs.courseTimes.add(startTime);
			cs.courseTimes.add(endTime);
			cs.days.add(days);
			
			cs.units = Integer.parseInt(info.substring(info.indexOf(" unit")-1,info.indexOf(" unit")));
			cs.grading = grading;
			cs.type = "Lecture";
			if (info.contains("SEM"))
				cs.type="Seminar";
			
		}
		return cs;
	}
    
    
}

