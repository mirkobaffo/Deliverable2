package main;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetGitInfo {
	
	
	public static final String PROGRAM = "git log --date=iso-strict --name-status --stat HEAD --abbrev-commit --date-order --reverse";
	static boolean done = false;
	
		
	
	//Con questa funzione prendo una lista di commit (Oggetto Commit con id e data )dalla directory del progetto
	
	public static List<Commit> commitList(List<Date> date, String param) throws IOException, ParseException{
		BufferedReader is;  // reader for output of process
	    String line;
	    List<Commit> CommitList = new ArrayList<>();
	    List<String> idList = new ArrayList<>();
	    List<Date> dateList = new ArrayList<>();
	    List<String> Classnamelist = new ArrayList<>();
	    File dir = new File("/Users/mirko/git/bookkeeper/");
	    final Process p = Runtime.getRuntime().exec(PROGRAM, null, dir);
	    is = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    while (!done && ((line = is.readLine()) != null)) {
	    	
	    	if (line.startsWith("commit")) {
	    		String s = line.substring(6, 14);
	    		Classnamelist.clear();
	    		idList.add(s);
	    	}
	    	else if (line.startsWith("Date:")) {
	    		
	    		String d = line.substring(8, 17) + " " + line.substring(19, 27);
	    
	    		Date CommitDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(d);  
	    		dateList.add(CommitDate);
	    	}
	
	    	else if (line.endsWith(".java")) {
	    		String Classname = line.substring(2);
	    		Classnamelist.add(Classname);	
	    	}
	    	
	    }
	    if (idList.size() == dateList.size()) {
	    	for (int i = 0; i < idList.size(); i++) {
			    Commit commit = new Commit();
			   	Commit.setId(commit,idList.get(i));
			   	Commit.setDate(commit,dateList.get(i));
			   	Commit.setClassName(commit, Classnamelist);
			   	CommitList.add(commit);
			    }	    
	    }
	    //System.out.println(CommitList.get(0).getDate());
	    return CommitList;

	}
}

//--pretty=format:%H --grep " + param + "--date=iso-strict --name-status  --stat HEAD --abbrev-commit --date-order