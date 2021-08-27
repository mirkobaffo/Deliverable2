package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


public class GetJsonFromUrl {
static String projName = "Bookkeeper";
static String projName2 = "libcloud";
static Integer max = 1;
static Integer index;
static HashMap<LocalDateTime, String> releaseNames;
static HashMap<LocalDateTime, String> releaseID;
static List<LocalDateTime> releases;
static String issuesString = "issues";
static String totalString = "total";

private GetJsonFromUrl() {
}


public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	InputStream is = new URL(url).openStream();
	try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));) {
		String jsonText = readAll(rd);
		return new JSONObject(jsonText);
	} finally {
		is.close();
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

public static Date parseStringToDate(String string) throws ParseException {

	String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	return new SimpleDateFormat(format).parse(string);
}

public static Date parseStringToAffectedDate(String string) throws ParseException {

	String format = "yyyy-MM-dd";
	return new SimpleDateFormat(format).parse(string);
}

public static List<Date> dateArray(String url, int i, int j, String getter) throws IOException, JSONException, ParseException {

	JSONObject json = readJsonFromUrl(url);
	JSONArray issues = json.getJSONArray(issuesString);
	ArrayList<Date> array = new ArrayList<>();
	max = json.getInt(totalString);
	for (; i < max && i < j; i++) {
		JSONObject field = issues.getJSONObject(i % 1000);
		String fieldobject = field.getJSONObject("fields").get(getter).toString();
		array.add(parseStringToDate(fieldobject));
	}
	index = i;
	return array;
}

public static List<String> keyArray(String url, int i, int j) throws IOException, JSONException, ParseException {

	JSONObject json = readJsonFromUrl(url);
	JSONArray issues = json.getJSONArray(issuesString);
	ArrayList<String> array = new ArrayList<>();
	max = json.getInt(totalString);
	for (; i < max && i < j; i++) {
		JSONObject field = issues.getJSONObject(i % 1000);
		String fieldobject = field.getString("key");
		array.add(fieldobject);
	}
	index = i;
	return array;
}


public static List<Ticket> setTicket(List<Date> cDate, List<Date> rDate, List<String> name, List<String> id){
	List<Ticket> ticketList = new ArrayList<>();
	for(int i = 0; i< cDate.size(); i++) {
		Ticket t = new Ticket(name.get(i), cDate.get(i), rDate.get(i), id.get(i));
		ticketList.add(t);
	}
	return ticketList;
}

public static Boolean setFVOV(Ticket ticket, List<Release> releases){
	for (Release r: releases) {
		if(ticket.getCreationDate().before(r.getDate()) && ticket.getOV() == null) {
			ticket.setOV(r.getInt());
		}
		if(ticket.getCommit().getDate().before(r.getDate())) {
			ticket.setFV(r.getInt());
			break;
		}
		else if (ticket.getFV() == null) {
			ticket.setFV(releases.size());
		}
	}
	return true;
}


public static void returnAffectedVersion(Ticket ticket, List<Release> releases) throws IOException, JSONException, ParseException {
	ticket.setIV(-1);
	Integer i = 0;
	String ticketName = ticket.getId();
	String url = "https://issues.apache.org/jira/rest/api/latest/issue/"+ ticketName;
    JSONObject json = readJsonFromUrl(url);
    JSONObject fields = json.getJSONObject("fields");
    JSONArray versions = fields.getJSONArray("versions");
    if(versions.length() != 0 && versions.getJSONObject(i).has("releaseDate")) {    		
    	String date = versions.getJSONObject(i).get("releaseDate").toString();
    	Date iV = parseStringToAffectedDate(date);
    	if(iV.before(ticket.getCreationDate())) {			
    		for(Release r: releases) {
    			if(iV.before(r.getDate())) {
    				ticket.setIV(r.getInt());
    				break;
    			}
    		}
    	}
    
    		
    }	
      		
   }
     
}


