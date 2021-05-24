package main;

import java.util.Date;
import java.util.List;

public class Commit {

	private String id;
	private Date date;
	private List<String> classname;

	public Commit(String id, Date date, List<String> classname) throws Exception {
		    this.id = id; 
		    this.date = date;
		    this.classname = classname;
		  }
	
	Commit() {
		
	}
	
	
	public String getId() {
		return id;
	}
	
	
	public Date getDate() {
		return date;
	}
	
	
	public List<String> getClassName(){
		return classname;
	}
	
	
	public static void setId(Commit commit, String id) {
		commit.id = id;
	}
	
	
	public static void setDate(Commit commit, Date date) {
		commit.date = date;
	}
	
	
	public static void setClassName(Commit commit, List<String> classname) {
		commit.classname = classname;
	}
	
}
