package main;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;




public class GetReleaseInfo {
	
	  static Integer max = 1;
	  static Integer index;
	  static HashMap<LocalDateTime, String> releaseNames;
	  static HashMap<LocalDateTime, String> releaseID;
	  static List<LocalDateTime> releases;
	  static Integer numVersions;
	  static String releaseDateString = "releaseDate";
	  static String versionString = "versions";
	  static String projName ="BOOKKEEPER";
	  static String projName2 = "LIBCLOUD";
	  static List<Class> auxList = new ArrayList<>();

	  
	  
	  private GetReleaseInfo() {
		  
	  }
	   
	   
	   

      public static List<Release> getReleaseList() throws ParseException, JSONException, IOException {

		   //Fills the arraylist with releases dates and orders them
		   //Ignores releases with missing dates
		   releases = new ArrayList<>();
		   Integer i;
		   String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
		   JSONObject json = readJsonFromUrl(url);
		   List<Release> releaseList = new ArrayList<>();
		   JSONArray versions = json.getJSONArray(versionString);
		   releaseNames = new HashMap<>();
		   releaseID = new HashMap<> ();
		   for (i = 0; i < versions.length(); i++ ) {
			   String name = "";
		       String id = "";
		       if(versions.getJSONObject(i).has(releaseDateString)) {
		    	   if (versions.getJSONObject(i).has("name"))
		    		   name = versions.getJSONObject(i).get("name").toString();
		           if (versions.getJSONObject(i).has("id")) {
		               id = versions.getJSONObject(i).get("id").toString();
		               addRelease(versions.getJSONObject(i).get(releaseDateString).toString(), name,id);
		           	}
		            }
		         }
		   // order releases by date
		   Collections.sort(releases, (LocalDateTime o1, LocalDateTime o2) -> o1.compareTo(o2));
		   for ( i = 0; i < releases.size(); i++) {
			   Integer index = i + 1;
			   
		       releaseList.add(customRelease(index, releases.get(i).toString(),
		       releaseNames.get(releases.get(i)),releaseID.get(releases.get(i))));
		       
		   }
		   return releaseList;
      }
      
      
      
	  public static void addRelease(String strDate, String name, String id) {
		      LocalDate date = LocalDate.parse(strDate);
		      LocalDateTime dateTime = date.atStartOfDay();
		      if (!releases.contains(dateTime))
		         releases.add(dateTime);
		      releaseNames.put(dateTime, name);
		      releaseID.put(dateTime, id);
		   }
	   

	   
	   
	   public static Release customRelease(Integer index, String strDate, String name, String id) throws ParseException {
		   	  Release release = new Release();
		   	  Release.setNumber(release, index);
		      Release.setName(release, name);
		      Release.setId(release,id);
		      Date releaseDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(strDate);  
		      Release.setDate(release, releaseDate);
		      return release;
		   }

	   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	      
	      try(InputStream is = new URL(url).openStream();) {
	         BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	         String jsonText = readAll(rd);
	         return new JSONObject(jsonText);
	       }
	   }
	   
	   private static String readAll(Reader rd) throws IOException {
		      StringBuilder sb = new StringBuilder();
		      int cp;
		      while ((cp = rd.read()) != -1) {
		         sb.append((char) cp);
		      }
		      return sb.toString();
		   }

	   
	   public static List<String> versionArray(String url, int i, int j, String getter) throws IOException, JSONException, ParseException {

			JSONObject json = readJsonFromUrl(url);
			JSONArray issues = json.getJSONArray("issues");
			int counter = 0;
			ArrayList<String> array = new ArrayList<>();
			max = json.getInt("total");
			for (; i < max && i < j; i++) {
				JSONArray versions = issues.getJSONObject(i % 1000).getJSONObject("fields").getJSONArray(versionString);
				if(versions.length() != 0 ) {
			          if (versions.getJSONObject(0).has(getter)) {
			          		 array.add(versions.getJSONObject(0).get(getter).toString());
			          		 counter = counter +1;
			          }
			       }
				else {
			      	 array.add("null");
			       }
			}
			index = i;
			return array;
		}
	   
	   
	   public static boolean containsName(List<Class> list, Class c){
		   	  boolean q = false;
		   	  
		   	  int counter = 0;
		   	  for (Class e: list) {
		   		  if (e.getName().equals(c.getName())) {
		   			  q = true;
		   			  e.setRecurrence(e.getRecurrence() + 1);
	   				  e.setSumChg(e.getSumChg() + c.getChg());
		   			  if(e.getMaxChg() < c.getChg()) {
		   				  e.setMaxChg(c.getChg());
		   				  
		   			  }
		   			  
		   		  }
		   		  else 
		   		  {
		   			  c.setMaxChg(c.getChg());
		   			  c.setRecurrence(c.getRecurrence() + 1);
	   				  c.setSumChg(c.getSumChg() + c.getChg());
		   		  }
		   		  counter = counter +1;
		   	  }
		   	  return q;
		   }
	   
	   	   public static void auxListFiller(List<Commit> commitList, int firstRef, int lastRef){
	   		for (Commit commit: commitList) {
  			  if (commit.getSequenceNumber() > firstRef && commit.getSequenceNumber() <= lastRef && commit.getClassList() != null) {
  				  for (Class c: commit.getClassList()) {
  						 if (auxList.isEmpty() || !containsName(auxList, c)) {
  							 auxList.add(c);
  					  }
  				  }
  			  }  
	   		}
	   	  }
			   
		   public static void setClassToRelease(List<Release> releaseList, List<Commit> commitList) {
		    	  int firstRef = 0;
		    	  List<Class> releaseClassList = new ArrayList<>();
		    	  for (int i = 0; i<releaseList.size()/2; i++) {
		    		  int lastRef = releaseList.get(i).getCommit().getSequenceNumber();
		    		  auxListFiller(commitList, firstRef, lastRef);
		    		  firstRef = lastRef;
		    		  for (Class e: auxList) {
		    			  releaseClassList.add(e);
		    		  }
		    		  releaseList.get(i).setClasses(releaseClassList);
		    		  releaseClassList = new ArrayList<>();
		    	  }
		   }
		   
		   public static void assignCommitListToRelease(List<Release> releaseList, List<Commit> commitList)  {
		    	  int firstRef = 0;
		    	  int lastRef = 0;
		    	  List<Commit> temp = new ArrayList<>();
		    	  for (Release release: releaseList) {
		    		  lastRef = release.getCommit().getSequenceNumber();
		    		  for(Commit c: commitList) {
		    			  if (c.getSequenceNumber() > firstRef && c.getSequenceNumber() <= lastRef) {
		    				  temp.add(c);
		    			  }  
		    		  }
		    		  firstRef = lastRef;
		    		  release.setCommitList(temp);
		    		  temp = new ArrayList<>();
		    	  }
	    		  
		   }

	      
		
 }

	
