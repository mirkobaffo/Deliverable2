package main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.json.JSONArray;
import org.json.JSONException;



public class Main {
	
	static String projName = "BOOKKEEPER";
	static String projName2 = "LIBCLOUD";
	static Integer max = 1;
	static Integer index;
	static int releaseCounter = 1;
	
	
	public static void csvVersionArray(List <Release> releases) throws IOException {
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
	
	// metodo che assegna la commit relativa alla release
	public static void dateComparator(List<Release> release, List<Commit> commit) {
		for (int i = 0; i < release.size()/2; i++) {
			for(int k = 1; k< commit.size(); k++) {		
				if(!release.get(i).getDate().after(commit.get(k).getDate()))  {
					release.get(i).setCommit(commit.get(k-1));
					break;
				}
			}
		}
		//in caso di commit mancanti all'inizio per motivi quali il cambio di repo, qui faccio un'approssimazione
		if(release.get(2).getCommit().getSequenceNumber()==0) {
			int counter = 0;
			for(Release r: release) {
				r.setCommit(commit.get((commit.size()/release.size())*counter));
				counter = counter +1;
			}
		}
	}
	
	public static boolean computeBuggynessCheck(Ticket t, Release r) {
		return (t.getIV() <= r.getInt() && t.getFV() > r.getInt());

	}
	
	public  static void computeBuggyness(List <Release> releases) {
		for (Release r : releases) {
			r.setNumOfBuggyClass(0);
			for (Class c : r.getClasses()) {
				c.setBuggy(false);
				if(c.getTicket()!= null) {
					for(Ticket t: c.getTicket()) {						
						if(computeBuggynessCheck(t,r)) {
							c.setBuggy(true);
							r.setNumOfBuggyClass(r.getNumOfBuggyClass() + 1);
							break;
						}
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	public static int getPercentageDefectiveInTraining(List<Release> releases, int z) {
		int counter = 0;
		int releaseCounter = 1;
		for(int i = 0; i < z; i ++) {
			counter = counter + releases.get(i).getNumOfBuggyClass();
			releaseCounter = releaseCounter + releases.get(i).getClasses().size();
		}
		return (counter*100)/releaseCounter;
	}
	
	public static int getDefectiveInTraining(List<Release> releases, int z) {
		int counter = 0;
		for(int i = 0; i < z; i ++) {
			counter = counter + releases.get(i).getNumOfBuggyClass();
			}
		
		
		return counter;
	}

	
	public static String csvFinal(List <Release> releases, int counter) throws IOException {
		String name = "/Users/mirko/git/Deliverable2/doc/release" + counter + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Release");
			stringBuilder.append(",");
			stringBuilder.append("className");
			stringBuilder.append(",");
			stringBuilder.append("LOC");
			stringBuilder.append(",");
			stringBuilder.append("Age");
			stringBuilder.append(",");
			stringBuilder.append("CHG");
			stringBuilder.append(",");
			stringBuilder.append("MAX_CHG");
			stringBuilder.append(",");
			stringBuilder.append("AVG_CHG");
			stringBuilder.append(",");
			stringBuilder.append("Bug Fixed");
			stringBuilder.append(",");
			stringBuilder.append("NAuth");
			stringBuilder.append(",");
			stringBuilder.append("Number of Commit");
			stringBuilder.append(",");
			stringBuilder.append("LOC Added");
			stringBuilder.append(",");
			stringBuilder.append("AVG_LOC Added");
			stringBuilder.append(",");
			stringBuilder.append("MAX_LOC Added");
			stringBuilder.append(",");
			stringBuilder.append("Buggy");
			stringBuilder.append("\n");
			br.write(stringBuilder.toString());
			int size = releases.size();
			for (int i = 0 ; i < size; i++) {
				for (Class c : releases.get(i).getClasses()) {
					StringBuilder stringBuilder2 = new StringBuilder();
					stringBuilder2.append(releases.get(i).getInt());
					stringBuilder2.append(",");
					stringBuilder2.append(c.getName());
					stringBuilder2.append(",");
					stringBuilder2.append(c.getLOC());
					stringBuilder2.append(",");
					stringBuilder2.append(Metrics.classAge(c));
					stringBuilder2.append(",");
					stringBuilder2.append(c.getChg());
					stringBuilder2.append(",");
					stringBuilder2.append(c.getMaxChg());
					stringBuilder2.append(",");
					stringBuilder2.append(Metrics.getAVGChg(c));
					stringBuilder2.append(",");
					stringBuilder2.append(Metrics.numberOfBugFixedForRelease(releases.get(i), c));
					stringBuilder2.append(",");
					stringBuilder2.append(c.getNauth());
					stringBuilder2.append(",");
					stringBuilder2.append(c.getNR());
					stringBuilder2.append(",");
					stringBuilder2.append(c.getLOCAdded());
					stringBuilder2.append(",");
					stringBuilder2.append(c.getAVGLOCAdded());
					stringBuilder2.append(",");
					stringBuilder2.append(c.getMAXLOCAdded());
					stringBuilder2.append(",");
					stringBuilder2.append(c.getBuggy());
					stringBuilder2.append("\n");
					br.write(stringBuilder2.toString());
				}
			}	
		}
		return name;
	}
	
	
	public static String getCostName(WekaData w) {
		String s = "";
		if(w.getCostSensitive() == 0) {
			s = "no Cost Sensitive";
		}
		else if(w.getCostSensitive() == 1) {
			s = "Sensitive Treshold";
		}
		else {
			s = "Sensitive learning";
		}
		return s;
	}
	
	
	public static void main(String[] args) throws Exception {
		Integer i = 0;
		Integer j = 0;
		j = i + 1000;
		
		
		String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	               + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	               + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,affectedVersion,versions,created&startAt="
				+ i.toString() + "&maxResults=" + j.toString();
		List<Date> createdarray = main.GetJsonFromUrl.dateArray(url, i , j , "created");
		List<Date> resolutionarray = main.GetJsonFromUrl.dateArray(url, i , j , "resolutiondate");
		List <String> keyArray = main.GetJsonFromUrl.keyArray(url, i, j);
		List <String> version = main.GetReleaseInfo.versionArray(url, i ,1000, "name");
		List <Commit> commit = GetGitInfo.commitList();
		int size = GetReleaseInfo.getReleaseList().size();
		List<Release> releases = GetReleaseInfo.getReleaseList();
		List<Ticket> ticket = GetJsonFromUrl.setTicket(createdarray,resolutionarray,version,keyArray);
		dateComparator(releases,commit);
		for(Ticket t : ticket) {
			GetJsonFromUrl.returnAffectedVersion(t, releases);
		}
		List<Ticket> ticketConCommit = GetGitInfo.setClassVersion(ticket,commit,releases);
		Proportion.checkIV(ticketConCommit);
		GetReleaseInfo.setClassToRelease(releases, commit);		
		List<Release> halfReleases = releases.subList(0, size/2);
		GetReleaseInfo.assignCommitListToRelease(halfReleases, commit);
		computeBuggyness(halfReleases);
		JSONArray jsonArray = GetDiffFromGit.getMetrics(halfReleases);
		for (Release r: halfReleases) {
			GetDiffFromGit.setMetric(r, jsonArray);
		}
		csvFinal(halfReleases, 0);
		List<WekaData> wekaList = new ArrayList<>();
		List<String> trainingSet = TestWekaEasy.makeTrainingSet(halfReleases);
		List<String> testingSet = TestWekaEasy.makeTestingSet(halfReleases);
		for(int z = 1; z< testingSet.size()+1; z ++) {
			wekaList.addAll(TestWekaEasy.wekaAction(testingSet.get(z-1), trainingSet.get(z-1), z, getDefectiveInTraining(halfReleases, z)));
		}
		
		CsvMaker.csvByWeka(wekaList, halfReleases);
		}
}
