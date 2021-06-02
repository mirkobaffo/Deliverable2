package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Class {
	
	private String name;
	private Integer IV;
	private Integer OV;
	private Integer FV;
	private Boolean buggy;
	private List<Ticket> ticket;
	
	public Class(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Integer geIV() {
		return this.IV;
	}
	
	public Integer getOV() {
		return this.OV;
	}
	
	public Integer getFV() {
		return this.FV;
	}
	
	public Boolean getBuggy() {
		return this.buggy;
	}
	
	public List<Ticket> getTicket() {
		return this.ticket;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public void setBuggy(Boolean buggy) {
		this.buggy = buggy;
	}
	
	public void setTicket(List <Ticket> ticket) {
		this.ticket = ticket;
	}
	
	public void setSingleTicket(Ticket ticket) {
		if(this.ticket == null) {
			List<Ticket> ticketList = new ArrayList();
			this.ticket = ticketList;
			this.ticket.add(ticket);
		}
		else {
		this.ticket.add(ticket);
		}
	}
}
