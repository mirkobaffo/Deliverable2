package main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONException;



public class CsvWriter {
	
	static String projName = "BOOKKEEPER";
	static Integer max = 1;
	static Integer index;
	
	public static void CsvWriteArray(List <Date> createdarray, List <Date> resolutionarray, List <String> key, List <String> Idarray, List <Commit> commit) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter("/Users/mirko/Desktop/output.csv"))) {
			// Write header of the csv file produced in output
			
			StringBuilder sb = new StringBuilder();
			sb.append("creation date: ");
			sb.append(",");
			sb.append("Resolution date: ");
			sb.append(",");
			sb.append("Name: ");
			sb.append(",");
			sb.append("Id: ");
			sb.append(",");
			sb.append("Commit: ");
			sb.append("\n");
			br.write(sb.toString());
			int size = createdarray.size();
			for (int i = 0 ; i < size; i++) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append(createdarray.get(i));
				sb2.append(",");
				sb2.append(resolutionarray.get(i));
				sb2.append(",");
				sb2.append(key.get(i));
				sb2.append(",");
				sb2.append(Idarray.get(i));
				sb2.append(",");
				sb2.append(commit.get(i).getId());
				sb2.append("\n");
				br.write(sb2.toString());
			}
		}

	}
	
	
	public static void CsvVersionArray(List <Release> releases) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter("/Users/mirko/Desktop/Releases.csv"))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Number: ");
			sb.append(",");
			sb.append("Id");
			sb.append(",");
			sb.append("Name");
			sb.append(",");
			sb.append("Date");
			sb.append(",");
			sb.append("Commit");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size()/2;
			for (int i = 0 ; i < size; i++) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append(releases.get(i).getInt());
				sb2.append(",");
				sb2.append(releases.get(i).getId());
				sb2.append(",");
				sb2.append(releases.get(i).getName());
				sb2.append(",");
				sb2.append(releases.get(i).getDate().toString());
				sb2.append(",");
				sb2.append(releases.get(i).getCommit().toString());
				sb2.append("\n");
				br.write(sb2.toString());
			}	
		}
	}
	
	public static void DateComparator(List<Release> release, List<Commit> commit) {
		for (int i = 0; i < release.size()/2; i++) {
			for(int k = 0; k< commit.size(); k++) {		
				//System.out.println("commit: " + release.get(i).getDate());
				if(release.get(i).getDate().after(commit.get(k).getDate()))  {
					//System.out.println("sono dopo");
					continue;
				}
				else {
					//System.out.println("sono prima");
					release.get(i).setCommit(commit.get(k-1));
					break;
				}
			}
		}
	}
	
	
	public  static void computeBuggyness(List <Release> releases) {
		for (Release r : releases) {
			for (Class c : r.getClasses()) {
				if(c.geIV() <= r.getInt() && c.getTicket().getFV() >= r.getInt()) {
					c.setBuggy(true);
				}
				else {
					c.setBuggy(false);
				}
			}
		}
	}
	
	
	public static void csvFinal(List <Release> releases) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter("/Users/mirko/Desktop/Releases.csv"))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Release");
			sb.append(",");
			sb.append("Name");
			sb.append(",");
			sb.append("Buggy");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size()/2;
			for (int i = 0 ; i < size; i++) {
				for (Class c : releases.get(i).getClasses()) {
					StringBuilder sb2 = new StringBuilder();
					sb2.append(releases.get(i).getId());
					sb2.append(",");
					sb2.append(c.getName());
					sb2.append(",");
					sb2.append(c.getBuggy());
					sb2.append("\n");
					br.write(sb2.toString());
				}
			}	
		}
	}
	
	public static void main(String[] args) throws IOException, JSONException, ParseException {
		Integer i = 0;
		Integer j = 0;
		j = i + 1000;
		
		
		String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	               + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	               + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,affectedVersion,versions,created&startAt="
				+ i.toString() + "&maxResults=" + j.toString();
		List<Date> createdarray = main.GetJsonFromUrl.DateArray(url, i , j , "created");
		List<Date> resolutionarray = main.GetJsonFromUrl.DateArray(url, i , j , "resolutiondate");
		List <String> keyArray = main.GetJsonFromUrl.keyArray(url, i, j, "key");
		List <String> version = main.getReleaseInfo.VersionArray(url, i ,1000, "name");
		List<String> id = main.GetJsonFromUrl.IdArray(url, i , j );
		List<Ticket> ticket = new ArrayList<>();
		List<Ticket> ticketConCommit = new ArrayList<>();
		List <Commit> commit = GetGitInfo.commitList();
		int size = getReleaseInfo.getReleaseList().size();
		List<Release> releases = getReleaseInfo.getReleaseList();//.subList(0, size/2);
		ticket = GetJsonFromUrl.setTicket(createdarray,resolutionarray,version,keyArray);
		DateComparator(releases,commit);
		System.out.println("sistemati i ticket");
		for(Ticket t : ticket) {
			GetJsonFromUrl.returnAffectedVersion(t, releases);
		}
		ticketConCommit = GetGitInfo.setClassVersion(ticket,commit,releases);
		System.out.println("Assegnate le Commit e le conseguenti FV, OV");
		Proportion.checkIV(ticketConCommit);
		System.out.println("ticket con commit:" + ticketConCommit);
		for (int k = 0; k < ticketConCommit.size(); k++) {
			System.out.println("ticket: " + ticketConCommit.get(k).getId() + "ticket FV: " + ticketConCommit.get(k).getFV() + "ticket IV: " + ticketConCommit.get(k).getIV()+ "Ticket OV: " +ticketConCommit.get(k).getOV());
		}
		System.out.println("a qui");
	
		getReleaseInfo.setClassToRelease(releases, commit);
		computeBuggyness(releases.subList(size, releases.size()/2));
		//CsvWriteArray(createdarray,resolutionarray,keyArray, id, commit);
		//CsvVersionArray(releases);
		csvFinal(releases.subList(size, releases.size()/2));
		
		
		
	}
}
