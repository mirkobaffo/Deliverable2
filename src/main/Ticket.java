package main;

import java.util.Date;

public class Ticket {

	private String name;
	private Date creationDate;
	private Date resolutionDate;
	private String id;
	private Commit commit;
	private int sequenceNumber;
	private Integer IV;
	private Integer OV;
	private Integer FV;
	private Integer P;
	
	
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
		return IV;
	}
	
	public Integer getOV() {
		return OV;
	}
	
	public Integer getFV() {
		return FV;
	}
	
	
	public Integer getP() {
		return P;
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
	
	
	public void setIV(Integer IV) {
		this.IV = IV;
	}
	
	public void setOV(Integer OV) {
		this.OV = OV;
	}
	
	public void setFV(Integer FV) {
		this.FV = FV;
	}
	
	public void setP(Integer P) {
		this.P = P;
	}
	
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}



