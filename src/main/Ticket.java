package main;

import java.util.Date;

public class Ticket {

	private String name;
	private Date creationDate;
	private Date resolutionDate;
	private String id;
	private Commit commit;
	private int sequenceNumber;
	private Integer injectedVersion;
	private Integer openingVersion;
	private Integer fixedVersion;
	private Integer p;
	
	
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
	
	public Integer getSequenceNumber() {
		return sequenceNumber;
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
	
	public Integer getIV() {
		return injectedVersion;
	}
	
	public Integer getOV() {
		return openingVersion;
	}
	
	public Integer getFV() {
		return fixedVersion;
	}
	
	
	public Integer getP() {
		return p;
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
	
	
	public void setIV(Integer injectedVersion) {
		this.injectedVersion = injectedVersion;
	}
	
	public void setOV(Integer openingVersion) {
		this.openingVersion = openingVersion;
	}
	
	public void setFV(Integer fixedVersion) {
		this.fixedVersion = fixedVersion;
	}
	
	public void setP(Integer p) {
		this.p = p;
	}
	
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}



