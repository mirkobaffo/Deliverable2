package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetDiffFromGit {
	
	static String projName = "bookkeeper";
	static String projName2 = "libcloud";

public static JSONArray getPerCommitMetrics(Repository repository, Release release, HashSet<String> countDevelopers) throws IOException, JSONException {
		
	
		int countRev = 0;
		int linesAdded;
		
		JSONObject jsonDataset = new JSONObject();
		JSONArray jsonArray = new JSONArray();		
		
		for(Commit commit: release.getCommitList()) {
			countDevelopers = new HashSet<String>();
			try {
			    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			    df.setRepository(repository);
			    df.setDiffComparator(RawTextComparator.DEFAULT);
			    df.setDetectRenames(true);
			    RevCommit rev = castToRevCommit(repository, commit);

		    	//System.out.println(rev.getAuthorIdent().getName());
			    if (countDevelopers.isEmpty() || !countDevelopers.contains(rev.getAuthorIdent().getEmailAddress().toString())) {
			    	countDevelopers.add(rev.getAuthorIdent().getEmailAddress().toString());
			    }
			    
		        countRev++;

		        List<DiffEntry> diffs = getDiffs(repository, rev);
			    
			    for (DiffEntry diff : diffs) {
			    	//System.out.println("diff: " + diff);
					linesAdded = 0;
			    	if(diff.toString().contains(".java") && new File("/Users/mirko/git/" + projName + "/" + diff.toString().substring(14).replace("]", "")).exists()) {
			    		//System.out.println("ho trovato una diff: " + diff.toString().substring(14).replace("]", ""));
			    		for(Edit edit : df.toFileHeader(diff).toEditList()) {
				        	linesAdded += edit.getEndB() - edit.getBeginB();       
				        }
				        jsonDataset.put("FileName", diff.toString().substring(14).replace("]", ""));
				        jsonDataset.put("Release", release.getInt());
				        jsonDataset.put("LOC", countLines(diff.toString().substring(14).replace("]", "")));
				        jsonDataset.put("NAuth", countDevelopers.size());
				        jsonDataset.put("autori", countDevelopers);
				        //System.out.println("sviluppatori: " + countDevelopers.size());
				        jsonDataset.put("NR", countRev);
				        jsonDataset.put("LOC_added", linesAdded);
				        jsonArray.put(jsonDataset);
				        //System.out.println("dataset: " + jsonDataset);
				        jsonDataset = new JSONObject();
				       		
					}
	        	}
				        	
			} catch (IOException e1) {
			    throw new RuntimeException(e1);
			}
		}
		return jsonArray;
	}

	
	public static long countLines(String fileName) {

	      long lines = 0;
	      try (BufferedReader reader = new BufferedReader(new FileReader("/Users/mirko/git/" + projName+ "/" + fileName))) {
	          while (reader.readLine() != null) lines++;
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      return lines;

	 }
	
	
	public static RevCommit castToRevCommit(Repository repository, Commit commit) throws IOException {
		RevCommit rev = null;
		ObjectId commitId = ObjectId.fromString(commit.getId());
		try (RevWalk revWalk = new RevWalk(repository)) {
		  rev = revWalk.parseCommit(commitId);
		}
		return rev;
	}
	
	public static List<DiffEntry> getDiffs(Repository repository, RevCommit rev) throws IOException {
		List<DiffEntry> diffs;
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repository);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setContext(0);
		df.setDetectRenames(true);
		if (rev.getParentCount() != 0) {
			RevCommit parent = (RevCommit) rev.getParent(0).getId();
			diffs = df.scan(parent.getTree(), rev.getTree());
		} else {
			RevWalk rw = new RevWalk(repository);
			ObjectReader reader = rw.getObjectReader();
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, reader, rev.getTree()));
		}
		
		return diffs;
 }
	
	public static boolean exists(JSONArray releaseArray, JSONObject obj) throws JSONException {
		if (releaseArray.length() == 0) {
			return false;
		} else {
			if (getElement(releaseArray, obj) != -1) {
				return true;
			}
			return false;
		}
		
	}
	
	public static int getElement(JSONArray releaseArray, JSONObject obj) throws JSONException {
		for (int i = 0; i < releaseArray.length(); i++) {
			if(releaseArray.getJSONObject(i).get("FileName").toString().equals(obj.get("FileName").toString()) 
					&& releaseArray.getJSONObject(i).get("Release").toString().equals(obj.get("Release").toString())) {
				return i;
			}
		}
		return -1;
	}
	

	
	public static JSONArray getMetrics(List<Release> releaseList) throws IOException, JSONException, NoHeadException, GitAPIException, ParseException{
				
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/Users/mirko/git/" + projName + "/.git/"))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();

		HashSet<String> countDevelopers = null;
		JSONArray commitJsonArray = new JSONArray();
		JSONArray releaseJsonArray = new JSONArray();
        
        for(Release r: releaseList) {
        	countDevelopers = new HashSet<String>();
        	commitJsonArray = getPerCommitMetrics(repository, r, countDevelopers);
	    }
        releaseJsonArray = generateJsonArray(commitJsonArray, countDevelopers);
        
        return releaseJsonArray;
	}
        
	
	public static String getLoc(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
			
			String loc;
			
			if (commitJsonArray.getJSONObject(i).get("LOC").equals(releaseObject.get("LOC"))) {
				loc = commitJsonArray.getJSONObject(i).get("LOC").toString();
			} else {
				loc = ((Integer) Math.max(Integer.parseInt(commitJsonArray.getJSONObject(i).get("LOC").toString()), Integer.parseInt(releaseObject.get("LOC").toString()))).toString();
			}
			
			return loc;
		}
	
	public static String getNAuth(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
		Integer nAuth = Integer.parseInt(releaseObject.get("NAuth").toString());
		String s = new String();
		if(!releaseObject.has("autori")) {
			releaseObject.put("autori", commitJsonArray.getJSONObject(i).get("autori"));
			releaseObject.put("NAuth", 1);
		}
		s = releaseObject.get("autori").toString();
		if(!s.contains(commitJsonArray.getJSONObject(i).get("autori").toString())) {
			s = s + " " + commitJsonArray.getJSONObject(i).get("autori").toString();
			releaseObject.put("autori", s);
			nAuth = Integer.parseInt(releaseObject.get("NAuth").toString()) + 1;
		}
		
		return nAuth.toString();
	}

	public static String getNR(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
	
		String nR;
		
		if (commitJsonArray.getJSONObject(i).get("NR").equals(releaseObject.get("NR"))) {
			nR = commitJsonArray.getJSONObject(i).get("NR").toString();
		} else {
			nR = ((Integer) Math.max(Integer.parseInt(commitJsonArray.getJSONObject(i).get("NR").toString()), Integer.parseInt(releaseObject.get("NR").toString()))).toString();
		}
		
		return nR;
	}
	
	
	public static String getLocAdded(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
		
		Integer locAdded = Integer.parseInt(commitJsonArray.getJSONObject(i).get("LOC").toString()) + Integer.parseInt(releaseObject.get("LOC").toString());
		
		return locAdded.toString();
	}
	

    public static JSONArray generateJsonArray(JSONArray commitJsonArray, HashSet<String> countDevelopers) throws JSONException {
    	
    	String nAuth = "";
    	String nR = "";
    	String loc = "";
    	String locAdded = "";
    	
		JSONArray releaseJsonArray = new JSONArray();
		//System.out.println("commitsize: " + commitJsonArray.length());
        for (int i = 0; i < commitJsonArray.length(); i++) {
        	
        	if (!exists(releaseJsonArray, commitJsonArray.getJSONObject(i))) { //exists controlla l'esistenza dell'object dentro il releasejsonarray
        		releaseJsonArray.put(commitJsonArray.get(i));
        	} else {
        		
        		nAuth = getNAuth(commitJsonArray, i, releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))));
        		nR = getNR(commitJsonArray, i, releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))));
        		loc = getLoc(commitJsonArray, i, releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))));
        		locAdded = getLocAdded(commitJsonArray, i, releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))));
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put("NAuth", nAuth);
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put("NR", nR);
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put("LOC", loc);
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put("LOC_added", loc);
        		
        	}
        }
        return releaseJsonArray;
        
    }
    
    public static void setMetric(Release release, JSONArray array) throws JSONException {
		//System.out.println("release_clsses: " + release.getClasses().size() + "arraylenght: " + array.length());
    	for (Class c: release.getClasses()) {
			for(int i = 0; i < array.length(); i++) {
				//System.out.println("json: " + array.getJSONObject(i).get("FileName") + "class: " + c.getName());
				if(array.getJSONObject(i).has("FileName") && c.getName().contains(array.getJSONObject(i).get("FileName").toString())) {
					c.setLOC(Integer.parseInt(array.getJSONObject(i).get("LOC").toString()));
					c.setLOCAdded(Integer.parseInt(array.getJSONObject(i).get("LOC_added").toString()));
					c.setNauth(Integer.parseInt(array.getJSONObject(i).get("NAuth").toString()));
					c.setNR(Integer.parseInt(array.getJSONObject(i).get("NR").toString()));
			
				}
			}
		}
		computeMaxAdded(release);
		computeAvgAdded(release);
		
	}
    
    public static void computeMaxAdded(Release r) {
    	int temp = 0;
    	int max = 0;
    	for (Class c: r.getClasses()) {
    		temp = c.getLOCAdded();
    		//System.out.println("temp: " + temp);
    		if (temp > max) {
    			max = temp;
    		}
    		//System.out.println("max: " + max);

    	}
    	for (Class c: r.getClasses()) {
    		c.setMAXLOCAdded(max);
    	}
    }
    
    public static void computeAvgAdded(Release r) {
    	float sum = 0;
    	float add = 0;
    	float divide = 1;
    	for (Class c: r.getClasses()) {
    		add = c.getLOCAdded();
    		sum += add;
    		divide++;

    	}
		List <Class> newClasses = new ArrayList<>();
    	for (Class c : r.getClasses()) {
    		Class newClass = new Class(c.getName());
    		newClass.setAVGLOCAdded(sum/divide);
    		newClass.setLOCAdded(c.getLOCAdded());
    		newClass.setLOC(c.getLOC());
    		newClass.setBuggy(c.getBuggy());
    		newClass.setChg(c.getChg());
    		newClass.setMaxChg(c.getMaxChg());
    		newClass.setSumChg(c.getSumChg());
    		newClass.setRecurrence(c.getRecurrence());
    		newClass.setMAXLOCAdded(c.getMAXLOCAdded());
    		newClass.setNauth(c.getNauth());
    		newClass.setDate(c.getDate());
    		newClass.setNR(c.getNR());
    		newClass.setTicket(c.getTicket());
    		newClasses.add(newClass);
    		r.setClasses(newClasses);
    	}
    }
    
}
	