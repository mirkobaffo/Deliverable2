package main;

import java.util.Date;
import java.util.List;

public class Commit {

	private String id;
	private Date date;
	private List<String> classname;
	private List<Class> classes;
  	private Integer sequenceNumber;

	public Commit(String id, Date date, List<String> classname)  {
		    this.id = id; 
		    this.date = date;
		    this.classname = classname;
		  }
	
	Commit() {
		
	}
	
	public List<Class> getClassList() {
		return this.classes;
	}
	
	public String getId() {
		return this.id;
	}
	
	public Integer getSequenceNumber() {
		return this.sequenceNumber;
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
	
	
	public void setClassList(List<Class> classes) {
		this.classes = classes;
	}
	
	public void setSequenceNumber(Integer sqn) {
		this.sequenceNumber = sqn;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	public void setClassName(List<String> classname) {
		this.classname = classname;
	}
	
}
