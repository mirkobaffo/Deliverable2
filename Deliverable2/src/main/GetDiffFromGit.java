package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
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

public static JSONArray getPerCommitMetrics(Repository repository, Release release, JSONArray jsonArray, HashSet<String> countDevelopers) throws IOException, JSONException {
		
		int countRev = 0;
		int linesAdded;
		JSONObject jsonDataset = new JSONObject();
		
		for(Commit commit: release.getCommitList()) {
			
			try {
				
			    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			    df.setRepository(repository);
			    df.setDiffComparator(RawTextComparator.DEFAULT);
			    df.setDetectRenames(true);
			    RevCommit rev = castToRevCommit(repository, commit);
			    if (countDevelopers.isEmpty() || !countDevelopers.contains(rev.getAuthorIdent().getEmailAddress().toString())) {
			    	countDevelopers.add(rev.getAuthorIdent().getEmailAddress().toString());
			    }
			    
		        countRev++;
			    
			    List<DiffEntry> diffs = getDiffs(repository, rev);
			    for (DiffEntry diff : diffs) {
					linesAdded = 0;
					//String path = "/Users/mirko/git/bookkeeper/" + diff.toString().substring(14).replace("]", "");
					//System.out.println("path: " + path);
			    	if(diff.toString().contains(".java") && new File("/Users/mirko/git/bookkeeper/" + diff.toString().substring(14).replace("]", "")).exists()) {
			    		for(Edit edit : df.toFileHeader(diff).toEditList()) {
				        	linesAdded += edit.getEndB() - edit.getBeginB();       
				        }
				        jsonDataset.put("FileName", diff.toString().substring(14).replace("]", ""));
				        jsonDataset.put("Release", release.getInt());
				        jsonDataset.put("LOC", countLines(diff.toString().substring(14).replace("]", "")));
				        jsonDataset.put("NAuth", countDevelopers.size());
				        jsonDataset.put("NR", countRev);
				        jsonDataset.put("LOC_added", linesAdded);
				        jsonArray.put(jsonDataset);
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
	      try (BufferedReader reader = new BufferedReader(new FileReader("/Users/mirko/git/bookkeeper/" + fileName))) {
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
	
	public static void computeReleaseJsonObject(JSONArray releaseArray, JSONObject jsonObject) throws JSONException {
		int i;
		int added;
		int nAuth;
		int nR;
		int loc;
		if ((i = getElement(releaseArray, jsonObject)) != -1) {
			added = Integer.parseInt(releaseArray.getJSONObject(i).get("LOC_added").toString());
			nAuth = Integer.parseInt(releaseArray.getJSONObject(i).get("NAuth").toString());
			nR = Integer.parseInt(releaseArray.getJSONObject(i).get("NR").toString());
			loc = Integer.parseInt(releaseArray.getJSONObject(i).get("LOC").toString());

			releaseArray.getJSONObject(i).put("LOC_added", added + Integer.parseInt(jsonObject.get("LOC_added").toString()));
			releaseArray.getJSONObject(i).put("NAuth", nAuth);
			releaseArray.getJSONObject(i).put("NR", nR);
			releaseArray.getJSONObject(i).put("LOC", loc);
		}
	}
	
	public static int computeNR(JSONArray jsonArray, JSONObject jsonObject, int start) throws JSONException {
		int nR;
		int diff = 0;
		int i = getElement(jsonArray, jsonObject);
		int release = Integer.parseInt(jsonArray.getJSONObject(i).get("Release").toString());
		String filename = jsonArray.getJSONObject(i).get("FileName").toString();
		for (int k = 0; k < jsonArray.length(); k++) {
			if (jsonArray.getJSONObject(k).get("FileName").toString().equals(filename) && Integer.parseInt(jsonArray.getJSONObject(k).get("Release").toString()) == release - 1) {
				diff = Integer.parseInt(jsonArray.getJSONObject(k).get("NR").toString());
				break;
			}
		}
		nR = start - diff;
		
		return nR;
	}
	
	public static JSONArray getMetrics(List<Release> releaseList) throws IOException, JSONException, NoHeadException, GitAPIException, ParseException{
				
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/Users/mirko/git/bookkeeper/.git/"))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
		HashSet<String> countDevelopers = null;
		JSONArray jsonArray = new JSONArray();
		JSONArray commitJsonArray = new JSONArray();
		JSONArray releaseJsonArray = new JSONArray();
        
        for(Release r: releaseList) {
        	//System.out.println("nome release: " + r.getName());
        	countDevelopers = new HashSet<String>();
        	commitJsonArray = getPerCommitMetrics(repository, r, jsonArray, countDevelopers);
        	//System.out.println("Commit json array: " + commitJsonArray);

	    }
        releaseJsonArray = generateJsonArray(commitJsonArray, countDevelopers);
        
        return releaseJsonArray;
	}
        
	
    public static JSONArray generateJsonArray(JSONArray commitJsonArray, HashSet<String> countDevelopers) throws JSONException {
		JSONArray releaseJsonArray = new JSONArray();

        for (int i = 0; i < commitJsonArray.length(); i++) {
        	int nAuth;
        	int nR = 0;
        	int loc = 0;
        	JSONObject jsonObject = commitJsonArray.getJSONObject(i);
        	if (!exists(releaseJsonArray, jsonObject)) { //exists controlla l'esistenza dell'object dentro il releasejsonarray
        		releaseJsonArray.put(jsonObject);
        	} else {
        		if(Integer.parseInt(jsonObject.get("NAuth").toString()) < countDevelopers.size()) {
        			nAuth = countDevelopers.size();
        		} else {
        			nAuth = Integer.parseInt(jsonObject.get("NAuth").toString());
        		}
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).put("NAuth", nAuth);
        		if (Integer.parseInt(releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).get("Release").toString()) == 1) {
        			if(Integer.parseInt(jsonObject.get("NR").toString()) < Integer.parseInt(releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).get("NR").toString())) {
            			nR = Integer.parseInt(releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).get("NR").toString());
            		} else {
            			nR = Integer.parseInt(jsonObject.get("NR").toString());
            		}
        			releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).put("NR", nR);
        		} else {
        			if(Integer.parseInt(jsonObject.get("NR").toString()) < Integer.parseInt(releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).get("NR").toString())) {
        				nR = computeNR(releaseJsonArray, jsonObject, Integer.parseInt(releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).get("NR").toString()));
        			} else {
            			nR = computeNR(releaseJsonArray, jsonObject, Integer.parseInt(jsonObject.get("NR").toString()));
            		}
        			releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).put("NR", nR);
        		}
        		if (releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).get("FileName").toString().equals(jsonObject.get("FileName").toString()) && Integer.parseInt(releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).get("Release").toString()) == Integer.parseInt(jsonObject.get("Release").toString())) {
        			loc = Integer.parseInt(jsonObject.get("LOC").toString());
        		}
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, jsonObject)).put("LOC", loc);
        		
        		computeReleaseJsonObject(releaseJsonArray, jsonObject);
        	}
        }
        return releaseJsonArray;
        
    }
    
    public static void setMetric(List <Class> classes, JSONArray array) throws JSONException {
		for (Class c: classes) {
			
			for(int i = 0; i < array.length(); i++) {
				//System.out.println("classname: " + c.getName() + "array: " + array.getJSONObject(i).get("FileName").toString());
				if(array.getJSONObject(i).has("FileName") && c.getName().contains(array.getJSONObject(i).get("FileName").toString())) {
					c.setLOC(Integer.parseInt(array.getJSONObject(i).get("LOC").toString()));
					c.setLOCAdded(Integer.parseInt(array.getJSONObject(i).get("LOC_added").toString()));
					c.setNauth(Integer.parseInt(array.getJSONObject(i).get("NAuth").toString()));
					c.setNR(Integer.parseInt(array.getJSONObject(i).get("NR").toString()));
			
				}
			}
		}
		computeMaxAdded(classes);
		computeAvgAdded(classes);
		
	}
    
    public static void computeMaxAdded(List<Class> classes) {
    	int temp;
    	int max = 0;
    	for (Class c: classes) {
    		temp = c.getLOCAdded();
    		if (temp > max) {
    			max = temp;
    		}
    	}
    	for (Class c: classes) {
    		c.setMAXLOCAdded(max);
    	}
    }
    
    public static void computeAvgAdded(List<Class> classes) {
    	float sum = 0;
    	float add;
    	float divide = 1;
    	for (Class c: classes) {
    		add = c.getLOCAdded();
    		sum += add;
    		divide++;
    	}
    	for (Class c : classes) {
    		c.setAVGLOCAdded(sum/divide);
    	}
    }


	
}
	