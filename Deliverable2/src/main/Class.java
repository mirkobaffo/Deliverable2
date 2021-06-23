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
	private int Nauth; //numero autori
	private int NR; //numero di commit che riguardano questa classe
	private int LOC; //righe di codice
	private int LOCAdded; //righe di codice aggiunte
	private int MAXLOCAdded; // massimo numero di righe di codice aggiunte per release
	private int sumLOCAdded; //somma totale righe di codice aggiunte per ottenere la media
	private float AVGLOCAdded;
	private Release release; 
	
	public Class(String name) {
		this.name = name;
		this.maxChg = 0;
		this.recurrence = 1;
		this.sumChg = 0;
		this.MAXLOCAdded = 0;
		this.sumLOCAdded = 0;
		this.LOC = 0;
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
	
	
	public int getNR() {
		return this.NR;
	}
	
	
	public int getNauth() {
		return this.Nauth;
	}
	
	
	public int getLOC() {
		return this.LOC;
	}
	
	
	public int getMAXLOCAdded() {
		return this.MAXLOCAdded;
	}
	
	
	public int getLOCAdded() {
		return this.LOCAdded;
	}
	
	
	public int getSumMAXLOCAdded() {
		return this.sumLOCAdded;
	}
	
	
	public int getSumChg() {
		return this.sumChg;
	}
	
	
	
	public int getRecurrence() {
		return this.recurrence;
	}
	
	
	public Release getRelease() {
		return this.release;
	}
	
	
	public float getAVGLOCAdded() {
		return this.AVGLOCAdded;
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
	
	
	public void setLOC(int LOC) {
		this.LOC = LOC;
	}
	
	
	public void setNR(int NR) {
		this.NR = NR;
	}
	
	
	public void setNauth(int Nauth) {
		this.Nauth = Nauth;
	}
	
	
	public void setLOCAdded(int LOCAdded) {
		this.LOCAdded = LOCAdded;
	}
	
	
	public void setMAXLOCAdded(int MAXLOCAdded) {
		this.MAXLOCAdded = MAXLOCAdded;
	}
	
	
	public void setSumLOCAdded(int sumLOCAdded) {
		this.sumLOCAdded = sumLOCAdded;
	}
	
	
	public void setRelease(Release release) {
		this.release = release;
	}
	
	
	public void setAVGLOCAdded(float AVGLOCAdded) {
		this.AVGLOCAdded = AVGLOCAdded;
	}
	
	
}
