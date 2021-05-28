package main;

import java.util.Date;

public class Ticket {

	private String name;
	private Date creationDate;
	private Date resolutionDate;
	private String id;
	private Commit commit;
	
	
	public Ticket (String name, Date creationDate, Date resolutionDate, String id) {
		this.name = name;
		this.creationDate = creationDate;
		this.resolutionDate = resolutionDate;
		this.id = id;
	}
	
	Ticket(){
		
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	
	public Date getResolutionDate() {
		return resolutionDate;
	}
	
	
	public String getId() {
		return id;
	}
	
	
	public Commit getCommit() {
		return commit;
	}
	

	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setCreationDate(Date cDate) {
		this.creationDate = cDate;
	}
	
	
	public void setResolutionDate(Date rDate ) {
		this.resolutionDate = rDate;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public void setCommit(Commit commit) {
		this.commit = commit;
	}
}



