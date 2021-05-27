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
		return this.id;
	}
	
	
	public Date getDate() {
		return this.date;
	}
	
	
	public List<String> getClassName(){
		return this.classname;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	public void setClassName(List<String> classname) {
		this.classname = classname;
	}
	
}
