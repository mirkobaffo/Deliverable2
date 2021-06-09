package main;

import java.util.ArrayList;
import java.util.List;

public class Proportion {
	
	public static void checkIV(List<Ticket> ticketList) {
		int p;
		int iV;
		for (int i = 0; i < ticketList.size(); i++) {
			if(ticketList.get(i).getCommit() == null) {
				continue;
			}
			if (ticketList.get(i).getFV() != null && ticketList.get(i).getOV() != null) {
				//se IV � null viene settato a -1
				if (ticketList.get(i).getIV() == -1) {
					System.out.println("Sto analizzando il ticket inconsistente: " + ticketList.get(i).getId());

					iV = computeInconsistentIV(i, ticketList);
					ticketList.get(i).setIV(iV);
					p = ticketList.get(i).getP();
					System.out.println("questa è la p alla fine della fiera: " + p);
				} else {
					System.out.println("Sto analizzando il ticket consistente: " + ticketList.get(i).getId());

					p = computeP(ticketList.get(i));
				}
				//System.out.println("Proportion: " + p);
				
			}
			
		}
	}
	
	public static int computeP(Ticket ticket) {
		int top = ticket.getFV() - ticket.getIV();
		System.out.println("numeratore di p: " + top);
		//System.out.println("ticket: " + ticket.getId() + " ticket FV: " + ticket.getFV() + " ticket IV: " + ticket.getIV()+ " Ticket OV: " +ticket.getOV());
		int bottom = ticket.getFV() - ticket.getOV();
		if (bottom == 0) {
			bottom = 1;
		}
		int p = top/bottom;
		if (p == 0) {
			p = 1;
		}
		ticket.setP(p);
		return p;
		
	}
	
	public static int computeInconsistentIV(int index, List<Ticket> ticketList) {
		int iV;
		int p;
		List<Integer> pList = new ArrayList<>();
		for (int i = 0; i < index; i++) {
			pList.add(ticketList.get(i).getP());
		}
		p = getP(pList);
		if (p == 0) {
			p = 1;
		}
		System.out.println("Questo è il valore di p dopo aver fatto get p: " + p + " lo sto settando nel ticket " + index);
		ticketList.get(index).setP(p);
		//System.out.println("inconsistente: " + ticketList.get(index).getId());
		iV = computeIV(ticketList.get(index).getOV(), ticketList.get(index).getFV(), p);
		//System.out.println(iV);
		return iV;
	}
	
	public static int getP(List<Integer> l) {
		int size = l.size(); 
		System.out.println("questa è la size della lista: " + size);
		int sum = 0;
		int divide = 0;
		if (!l.isEmpty()) {
			for (int i = size-(size/100); i < size; i++) {
				if(l.get(i) != null)  {
					System.out.println("questo è il valore " + i + " della lista: " + l.get(i));
					sum += l.get(i);
				}
				divide++;
			}
		}
		if (divide == 0) {
			divide = 1;
		}
		System.out.println("questo è il valore sum/divide: "+ (sum/divide));
		return sum/divide;
	}
	
	public static int computeIV(int OV, int FV, int p) {
		int IV = FV - ((FV - OV)*p);
		System.out.println("FV: " + FV + " OV: " + OV + " IV: " + IV + " P: " + p );
		if(IV < 0) {
			IV=1;
		}
		return IV;
	}
	
	/*public static void main(String[] args) {
		List<Integer> array = new ArrayList<Integer>();
		for(int i = 0; i < 2000; i++) {
			array.add(i);
		}
		System.out.println(getP(array));
	}*/
}
