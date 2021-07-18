package main;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Metrics {
	static String projName = "bookkeeper";
	static String projName2 = "libcloud";
	
	
	public static int numberOfBugFixedForRelease(Release release, Class c) {
		int counter = 0;
		if(c.getTicket()!= null) {
			for(Ticket t : c.getTicket()) {
				if(t.getFV() == release.getInt()) {
					counter = counter + 1;
				}
			}
		}
		return counter;
	}
	
	
	//Da fare per le classi
	public static long classAge(Class c) {
		ZoneId defaultZoneId = ZoneId.systemDefault();
	    LocalDate localDate = LocalDate.now();
	    Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		long difference_In_Week = ((date.getTime() - c.getDate().getTime())/ (1000 * 60 * 60 * 24 * 7));
		return difference_In_Week;	
	}
	
	
	public static int getAVGChg(Class c) {
		if(c.getRecurrence()==0) {
			c.setRecurrence(1);
		}
		return c.getSumChg()/c.getRecurrence();
	}	
	
	
	public static void getMetrics(List<Commit> commitList, List<Release> releaseList) throws IOException, JSONException, NoHeadException, GitAPIException, ParseException{
		
		JSONObject jsonDataset = new JSONObject();
		JSONArray array = new JSONArray();
		HashSet<String> countDevelopers;
        List<DiffEntry> diffs;
		Iterable<RevCommit> logs;
		//List<RevCommit> commitList = new ArrayList<>();
		int countDevs;
		int countAdded;
		Git git = Git.open(new File("/Users/mirko/git/" + projName + "/.git"));

		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/Users/mirko/git/" + projName + "/.git"))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
		Ref head = repository.exactRef("HEAD");

        // a RevWalk allows to walk over commits based on some filtering that is defined
        RevWalk walk = new RevWalk(repository);

        RevCommit newCommit = walk.parseCommit(head.getObjectId());
        RevTree tree = newCommit.getTree();
        // now use a TreeWalk to iterate over all files in the Tree recursively
        // you can set Filters to narrow down the results if needed
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        while (treeWalk.next()) {
            //System.out.println("found: " + treeWalk.getPathString());
            if (treeWalk.getPathString().endsWith(".java") || treeWalk.getPathString().endsWith(".scala") || treeWalk.getPathString().endsWith(".js")) {
                jsonDataset = new JSONObject();
                countDevelopers = new HashSet<String>();
                countDevs = 0;
                countAdded = 0;
                logs = new Git(repository).log().addPath(treeWalk.getPathString()).call();
                //int i = 0;
                /*for (RevCommit rev: logs) {
                	if (commitList.isEmpty() || !containsRev(rev, commitList)) {
                		commitList.add(rev);
                } */
                for (RevCommit rev: logs) {
                	//System.out.println("rev: " + rev);
                //for (int j = 0; j < commitList.size(); j++) {
	                countDevelopers.add(rev.getAuthorIdent().getEmailAddress());
	                countDevs++;
	                //System.out.println(commitList.get(j).getName());
	                //i++;
	                //countAdded = getAdded(repository, newCommit, rev);
	                //countAdded++;
	                //jsonDataset.put("LOC_added", countAdded);      		    
                }
                /*if (i == 10) {
                    System.exit(0);
                } */
                jsonDataset.put("FileName", treeWalk.getPathString());
                jsonDataset.put("CountDevelopers", countDevelopers.size());
                jsonDataset.put("CountCommits", countDevs);
                jsonDataset.put("LOC", GetDiffFromGit.countLines(treeWalk.getPathString()));
                array.put(jsonDataset);
                //jsonDataset.put("LOC_added", getAdded(repository, newCommit,));
                //commitDetails.put(jsonDataset);
                //System.out.println("Json: " + jsonDataset);
                //System.out.println("LOC: " + treeWalk.getPathString());
                }
        }
        for(Release release: releaseList) {
        	GetDiffFromGit.setMetric(release, array);
        }
        /*for(Release r: releaseList) {
            GetDiffFromGit.getAddedDeleted(repository, r, jsonDataset);
            //System.out.println(jsonDataset);        
        }*/
        
        
     
	}
	
	

}
