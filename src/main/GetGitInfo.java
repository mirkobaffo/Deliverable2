package main;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetGitInfo {
	static String projName = "bookkeeper/";
	static String projName2 = "libcloud/";
	static String commitString = "commit";
	public static final String PROGRAM = "git log --date=iso-strict --name-status --stat HEAD --date-order --reverse";
	static boolean done = false;
	
		private GetGitInfo() {
			
		}
	
	//Con questa funzione prendo una lista di commit (Oggetto Commit con id e data )dalla directory del progetto
	
	public static List<Commit> commitList() throws IOException, ParseException{
		BufferedReader is;  // reader for output of process
	    String line;
	    List<Commit> commitList = new ArrayList<>();
	    List<String> idList = new ArrayList<>();
	    List<Date> dateList = new ArrayList<>();
	    List<String> classNameList = new ArrayList<>();
	    File dir = new File("/Users/mirko/git/" + projName);
	    final Process p = Runtime.getRuntime().exec(PROGRAM, null, dir);
	    is = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    int countLines = 0;
	    while (!done && ((line = is.readLine()) != null)) {
	    	if (line.startsWith(commitString)) {
	    		String s = line.substring(7);
	    		idList.add(s);
	    	}
	    	else if (line.startsWith("Date:")) {
	    		
	    		String d = line.substring(8, 17) + " " + line.substring(19, 27);
	    
	    		Date commitDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(d);  
	    		dateList.add(commitDate);
	    	}
	
	    	countLines++;
	    	
	    }
	    
	    if (idList.size() == dateList.size()) {
	    	for (int i = 0; i < idList.size(); i++) {
			    Commit commit = new Commit();
			    commit.setSequenceNumber(i);
			   	commit.setId(idList.get(i));
			   	commit.setDate(dateList.get(i));
			   	commitList.add(commit);
			    }	    
	    }
	    
	    
	    final Process p2 = Runtime.getRuntime().exec(PROGRAM, null, dir);
	    BufferedReader is2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));

	    for (int i = 0; i < countLines; i++) {
	    	line = is2.readLine();

	    	if (line != null && line.endsWith(".java")) {
	    		classNameList.add(line.substring(2));
	    	} else if (line != null && line.startsWith(commitString) && i != 0 && !classNameList.isEmpty()) {
	    		commitList.get(idList.indexOf(line.substring(7)) - 1).setClassName(classNameList);
	    		classNameList = new ArrayList<>();	
	    	}
	    }
	    
	    if (!commitList.isEmpty()) {
	    	for (Commit commit: commitList) {
	    		List<Class> cList = new ArrayList<>();
	    		if (commit.getClassName() != null) {

		    		List<String> sList = commit.getClassName();
		    		for (String s: sList) {

		    			Class c = new Class(s);
		    			c.setDate(commit.getDate());
		    			c.setChg(commit.getClassName().size());
		    			cList.add(c);
		    		}
	    		}
	    		commit.setClassList(cList);
	    	}	
	    }
	    
	    return commitList;

	}
	
	
	public static List<Ticket> setClassVersion(List <Ticket> ticket, List <Commit> commitList, List<Release> releases) throws IOException{
		List <Ticket> ticketList = new ArrayList<>();
		for(Ticket t : ticket) {
			BufferedReader is;  // reader for output of process
		    String line;
		    List<String> idList = new ArrayList<>();
			File dir = new File("/Users/mirko/git/" + projName);
			String ticketId = t.getId();

		    final Process p = Runtime.getRuntime().exec("git log --grep=" + ticketId + " --date=iso-strict --name-status --stat HEAD  --date-order --reverse", null, dir);
		    is = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    while (!done && ((line = is.readLine()) != null)) {

		    	if (line.startsWith(commitString)) {
		    		String s = line.substring(7);
		    		idList.add(s);
		    	}
		    	
		    }
		    for(String e: idList) {
		    	for(Commit c: commitList) {
		    		if(c.getId().equals(e)) {
		    			t.setCommit(c);
		    			GetJsonFromUrl.setFVOV(t, releases);
		    			if(t.getOV()!= null && t.getFV() >= t.getOV() && !ticketList.contains(t)) {
		    					ticketList.add(t);
		    			}
		    			List <Class> classes = c.getClassList();
		    			for(Class cl: classes) {
		    				cl.setSingleTicket(t);
		    			}
		    			//setto la fixed version nelle classi della commit
		    		}
		    	}
		    }
		}
	    return ticketList;
	}
	
	
	
	
	
}

//--pretty=format:%H --grep " + param + "--date=iso-strict --name-status  --stat HEAD --abbrev-commit --date-order