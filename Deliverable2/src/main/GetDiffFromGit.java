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

public static void getAddedDeleted(Repository repository, Release release, JSONObject jsonDataset) throws IOException, JSONException{
		//metodo per contare le righe aggiunte e cancellate
		int linesAdded;
		int linesDeleted;
		int linesReplaced;
		int filesChanged = 0;		
		
		for(Commit commit: release.getCommitList()) {
			try {
				linesAdded = 0;
				linesDeleted = 0;
				linesReplaced = 0;
			    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			    df.setRepository(repository);
			    df.setDiffComparator(RawTextComparator.DEFAULT);
			    df.setDetectRenames(true);
			    List<DiffEntry> diffs = getDiffs(repository, commit);
			    filesChanged = diffs.size();
			    for (DiffEntry diff : diffs) {
			    	/*if (!diff.getChangeType().toString().equals("ADD")) {
			    		System.out.println(diff.getChangeType().toString());
			    	}*/
			    	if(diff.toString().contains(".java")) {
				    	//System.out.println(diff.toString());
				        for(Edit edit : df.toFileHeader(diff).toEditList()) {
				        	//System.out.println(edit.toString());
				        	
					            linesAdded += edit.getEndB() - edit.getBeginB();
					            linesDeleted += edit.getEndA() - edit.getBeginA();
				        }
				        jsonDataset.put("Name file", diff.toString().substring(14));
				    	jsonDataset.put("Lines added", linesAdded);		
				    	jsonDataset.put("Lines deleted", linesDeleted);
			    	}
			    	
			    	//System.out.println(jsonDataset);
			    }
		
		  			            
			} catch (IOException e1) {
			    throw new RuntimeException(e1);
			}
			
		}
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
		//System.out.println("qua: " + commitId.getName());
		try (RevWalk revWalk = new RevWalk(repository)) {
		  rev = revWalk.parseCommit(commitId);
		  //System.out.println("qui: " + rev.getName());
		}
		return rev;
	}
	
	public static List<DiffEntry> getDiffs(Repository repository, Commit commit) throws IOException {
		List<DiffEntry> diffs;
		RevCommit rev = castToRevCommit(repository, commit);
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repository);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setContext(0);
		df.setDetectRenames(true);
		if (rev.getParentCount() != 0) {
			RevCommit parent = (RevCommit) rev.getParent(0).getId();
			//System.out.println("Parent: " + parent.getId().toString());
			diffs = df.scan(parent.getTree(), rev.getTree());
		} else {
			RevWalk rw = new RevWalk(repository);
			ObjectReader reader = rw.getObjectReader();
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, reader, rev.getTree()));
		}
		
		return diffs;
	}
	
	
	public static void setMetric(List <Class> classes, JSONArray array) throws JSONException {
		for (Class c: classes) {
			for(int i = 0; i < array.length(); i++) {
				//System.out.println(array.getJSONObject(i));
				if(array.getJSONObject(i).has("FileName") && (c.getName().contains(array.getJSONObject(i).get("FileName").toString()))) {
					c.setLOC(Integer.parseInt(array.getJSONObject(i).get("LOC").toString()));
					//c.setLOCAdded(Integer.parseInt(array.getJSONObject(i).get("LOC_Added").toString()));
					c.setNauth(Integer.parseInt(array.getJSONObject(i).get("CountDevelopers").toString()));
					c.setNR(Integer.parseInt(array.getJSONObject(i).get("CountCommits").toString()));
				}
			}
		}
		
	}
	
	
}