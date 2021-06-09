package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Class {
	
	private String name;
	private Date date;
	private Boolean buggy;
	private List<Ticket> ticket;
	private int chg; //numero di file committati insieme a lui
	private int maxChg; //massimo numero di file committati insieme a lui nella release
	private int sumChg; //somma dei valori totali di chg utile per ottenere la media
	private int recurrence; //quante volte una classe appare in una commit di una release, utile per la media
	
	public Class(String name) {
		this.name = name;
		this.maxChg = 0;
		this.recurrence = 1;
		this.sumChg = 0;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public Boolean getBuggy() {
		return this.buggy;
	}
	
	public List<Ticket> getTicket() {
		return this.ticket;
	}
	
	
	public int getChg() {
		return this.chg;
	}
	
	public int getMaxChg() {
		return this.maxChg;
	}
	
	
	public int getSumChg() {
		return this.sumChg;
	}
	
	
	public int getRecurrence() {
		return this.recurrence;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
 
	
	public void setBuggy(Boolean buggy) {
		this.buggy = buggy;
	}
	
	public void setTicket(List <Ticket> ticket) {
		this.ticket = ticket;
	}
	
	public void setDate(Date date) {
		this.date = date;
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
	
	
	public void setChg(int chg) {
		this.chg = chg;
	}
	
	
	public void setMaxChg(int maxChg) {
		this.maxChg = maxChg;
	}
	
	
	public void setSumChg(int sumChg) {
		this.sumChg = sumChg;
	}
	
	public void setRecurrence(int recurrence) {
		this.recurrence = recurrence;
	}
	
	
	
}
