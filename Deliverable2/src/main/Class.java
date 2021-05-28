package main;

import java.util.Date;

public class Class {
	
	private String name;
	private Date IV;
	private Date OV;
	private Date FV;
	
	public Class(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Date geIV() {
		return this.IV;
	}
	
	public Date getOV() {
		return this.OV;
	}
	
	public Date getFV() {
		return this.FV;
	}
	
	public void setName(String name) {
		this.name = name;
	}
 
	public void setIV(Date IV) {
		this.IV = IV;
	}
	
	public void setOV(Date OV) {
		this.OV = OV;
	}
	
	public void setFV(Date FV) {
		this.FV = FV;
	}
}
