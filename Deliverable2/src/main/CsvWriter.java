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



public class CsvWriter {
	
	static String projName = "BOOKKEEPER";
	static Integer max = 1;
	static Integer index;
	static int releaseCounter = 1;
	
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
			r.setNumOfBuggyClass(0);
			for (Class c : r.getClasses()) {
				c.setBuggy(false);
				if(c.getTicket()!= null) {
					for(Ticket t: c.getTicket())
						if(t.getIV() <= r.getInt() && t.getFV() > r.getInt()) {
						c.setBuggy(true);
						r.setNumOfBuggyClass(r.getNumOfBuggyClass() + 1);
						break;
						}
					}
			}
		}
	}
	
	
	public static void csvByWeka(List <WekaData> wList, List<Release> releases) throws IOException {
		String name = "/Users/mirko/Desktop/weka_data" + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Dataset");
			sb.append(",");
			sb.append("#trainingRelease");
			sb.append(",");
			sb.append("%training");
			sb.append(",");
			sb.append("%DefectiveInTraining");
			sb.append(",");
			sb.append("%DefectiveInTesting");
			sb.append(",");
			sb.append("Classifier");
			sb.append(",");
			sb.append("Balancing");
			sb.append(",");
			sb.append("FeatureSelection");
			sb.append(",");
			sb.append("Cost Sensitive");
			sb.append(",");
			sb.append("Sensitivity");
			sb.append(",");
			sb.append("TP");
			sb.append(",");
			sb.append("FP");
			sb.append(",");
			sb.append("TN");
			sb.append(",");
			sb.append("FN");
			sb.append(",");
			sb.append("Recall");
			sb.append(",");
			sb.append("Precision");
			sb.append(",");
			sb.append("AUC");
			sb.append(",");
			sb.append("Kappa");
			sb.append("\n");
			br.write(sb.toString());
			for (WekaData w : wList) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append(w.getTrainingStep());
				sb2.append(",");
				sb2.append(w.getTrainingStep());
				sb2.append(",");
				sb2.append(((w.getTrainingStep())*100/7));
				sb2.append(",");
				sb2.append(getPercentageDefectiveInTraining(releases, w.getTrainingStep()));
				sb2.append(",");
				sb2.append((releases.get(w.getTrainingStep()).getNumOfBuggyClass()*100)/releases.get(w.getTrainingStep()).getClasses().size());
				sb2.append(",");
				sb2.append(w.getClassifier());
				sb2.append(",");
				sb2.append(w.getSampling());
				sb2.append(",");
				sb2.append(w.getFeatureSelection());
				sb2.append(",");
				sb2.append(getCostName(w));
				sb2.append(",");
				sb2.append(w.getTreshold());
				sb2.append(",");
				sb2.append(w.getEval().truePositiveRate(1));
				sb2.append(",");
				sb2.append(w.getEval().falsePositiveRate(1));
				sb2.append(",");
				sb2.append(w.getEval().falsePositiveRate(1));
				sb2.append(",");
				sb2.append(w.getEval().falseNegativeRate(1));
				sb2.append(",");
				sb2.append(w.getEval().recall(1));
				sb2.append(",");
				sb2.append(w.getEval().precision(1));
				sb2.append(",");
				sb2.append(w.getEval().areaUnderROC(1));
				sb2.append(",");
				sb2.append(w.getEval().kappa());
				sb2.append("\n");
				br.write(sb2.toString());
				}
			}	
		
	}
	
	
	public static String csvForWeka(List <Release> releases, int counter) throws IOException {
		String name = "/Users/mirko/Desktop/Releases" + counter + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder sb = new StringBuilder();
			//sb.append("Name");
			//sb.append(",");
			sb.append("LOC");
			sb.append(",");
			sb.append("Age");
			sb.append(",");
			sb.append("CHG");
			sb.append(",");
			sb.append("MAX_CHG");
			sb.append(",");
			sb.append("AVG_CHG");
			sb.append(",");
			sb.append("Bug Fixed");
			sb.append(",");
			sb.append("NAuth");
			sb.append(",");
			sb.append("Number of Commit");
			sb.append(",");
			sb.append("LOC Added");
			sb.append(",");
			sb.append("AVG_LOC Added");
			sb.append(",");
			sb.append("MAX_LOC Added");
			sb.append(",");
			sb.append("Buggy");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size();
			for (int i = 0 ; i < size; i++) {
				for (Class c : releases.get(i).getClasses()) {
					if(c.getLOC()!=0) {
						StringBuilder sb2 = new StringBuilder();
						//sb2.append(c.getName());
						//sb2.append(",");
						sb2.append(c.getLOC());
						sb2.append(",");
						sb2.append(Metrics.classAge(c));
						sb2.append(",");
						sb2.append(c.getChg());
						sb2.append(",");
						sb2.append(c.getMaxChg());
						sb2.append(",");
						sb2.append(Metrics.getAVGChg(c));
						sb2.append(",");
						sb2.append(Metrics.numberOfBugFixedForRelease(releases.get(i), c));
						sb2.append(",");
						sb2.append(c.getNauth());
						sb2.append(",");
						sb2.append(c.getNR());
						sb2.append(",");
						sb2.append(c.getLOCAdded());
						sb2.append(",");
						sb2.append(c.getMAXLOCAdded());
						sb2.append(",");
						sb2.append(c.getAVGLOCAdded());
						sb2.append(",");
						sb2.append(c.getBuggy());
						sb2.append("\n");
						br.write(sb2.toString());
					}
				}
			}	
		}
		return name;
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
			//counter = counter + releases.get(i).getNumOfBuggyClass();
			for(Class c: releases.get(i).getClasses()) {
				if(c.getLOC()!=0 && c.getBuggy() == true) {
					counter = counter + 1;
				}
			}
		}
		
		return counter;
	}

	
	public static String csvFinal(List <Release> releases, int counter) throws IOException {
		String name = "/Users/mirko/Desktop/Releases" + counter + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Release");
			sb.append(",");
			sb.append("className");
			sb.append(",");
			sb.append("LOC");
			sb.append(",");
			sb.append("Age");
			sb.append(",");
			sb.append("CHG");
			sb.append(",");
			sb.append("MAX_CHG");
			sb.append(",");
			sb.append("AVG_CHG");
			sb.append(",");
			sb.append("Bug Fixed");
			sb.append(",");
			sb.append("NAuth");
			sb.append(",");
			sb.append("Number of Commit");
			sb.append(",");
			sb.append("LOC Added");
			sb.append(",");
			sb.append("AVG_LOC Added");
			sb.append(",");
			sb.append("MAX_LOC Added");
			sb.append(",");
			sb.append("Buggy");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size();
			for (int i = 0 ; i < size; i++) {
				for (Class c : releases.get(i).getClasses()) {
					StringBuilder sb2 = new StringBuilder();
					sb2.append(releases.get(i).getInt());
					sb2.append(",");
					sb2.append(c.getName());
					sb2.append(",");
					sb2.append(c.getLOC());
					sb2.append(",");
					sb2.append(Metrics.classAge(c));
					sb2.append(",");
					sb2.append(c.getChg());
					sb2.append(",");
					sb2.append(c.getMaxChg());
					sb2.append(",");
					sb2.append(Metrics.getAVGChg(c));
					sb2.append(",");
					sb2.append(Metrics.numberOfBugFixedForRelease(releases.get(i), c));
					sb2.append(",");
					sb2.append(c.getNauth());
					sb2.append(",");
					sb2.append(c.getNR());
					sb2.append(",");
					sb2.append(c.getLOCAdded());
					sb2.append(",");
					sb2.append(c.getAVGLOCAdded());
					sb2.append(",");
					sb2.append(c.getMAXLOCAdded());
					sb2.append(",");
					sb2.append(c.getBuggy());
					sb2.append("\n");
					br.write(sb2.toString());
				}
			}	
		}
		return name;
	}
	
	
	public static String getCostName(WekaData w) {
		String s = new String();
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
		long inizio = System.currentTimeMillis();
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
		List<String> testingSet = new ArrayList<>();
		List<String> trainingSet = new ArrayList<>();
		int size = getReleaseInfo.getReleaseList().size();
		List<Release> releases = getReleaseInfo.getReleaseList();//.subList(0, size/2);
		ticket = GetJsonFromUrl.setTicket(createdarray,resolutionarray,version,keyArray);
		DateComparator(releases,commit);
		for(Ticket t : ticket) {
			GetJsonFromUrl.returnAffectedVersion(t, releases);
		}
		ticketConCommit = GetGitInfo.setClassVersion(ticket,commit,releases);
		Proportion.checkIV(ticketConCommit);
		//for (int k = 0; k < ticketConCommit.size(); k++) {
			//System.out.println("ticket: " + ticketConCommit.get(k).getId() + "ticket FV: " + ticketConCommit.get(k).getFV() + "ticket IV: " + ticketConCommit.get(k).getIV()+ "Ticket OV: " +ticketConCommit.get(k).getOV());
		//}	
		getReleaseInfo.setClassToRelease(releases, commit);
		List<Release> halfReleases = releases.subList(0, size/2);
		getReleaseInfo.assignCommitListToRelease(halfReleases, commit);
		computeBuggyness(halfReleases);
		JSONArray jsonArray = GetDiffFromGit.getMetrics(halfReleases);
		/*for(Release r : releases.subList(0, size/2)) {
			for(Class c: r.getClasses()) {
				System.out.println("Release: " + r.getInt() + " numberOfBugFixedForRelease: " + Metrics.numberOfBugFixedForRelease(r,c) + " classAge: " + Metrics.classAge(c) + " Chg: " + c.getChg() + " maxChg: " + c.getMaxChg() + " avgChg: " + Metrics.getAVGChg(c));
			}
		}*/
		for (Release r: halfReleases) {
			//System.out.println("RID: " + r.getId() + "CLASSES:" + r.getClasses());
			GetDiffFromGit.setMetric(r, jsonArray);
		}
		csvFinal(halfReleases, 0);
		List<WekaData> wekaList = new ArrayList<>();
		trainingSet = TestWekaEasy.makeTrainingSet(halfReleases);
		testingSet = TestWekaEasy.makeTestingSet(halfReleases);
		for(int z = 1; z< testingSet.size()+1; z ++) {
			wekaList.addAll(TestWekaEasy.wekaAction(testingSet.get(z-1), trainingSet.get(z-1), z, getDefectiveInTraining(halfReleases, z)));
		}
		csvByWeka(wekaList, halfReleases);
		long fine = System.currentTimeMillis();
		System.out.println((fine-inizio)/1000);	
	}
}
