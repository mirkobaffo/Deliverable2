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
	
	
	private Metrics(){
		
	}
	
	
	public static int numberOfBugFixedForRelease(Release release, Class c) {
		int counter = 0;
		if(c.getTicket()!= null) {
			for(Ticket t : c.getTicket()) {
				if(t.getFV().equals(release.getInt())) {
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
		return ((date.getTime() - c.getDate().getTime())/ (1000 * 60 * 60 * 24 * 7));
	}
	
	
	public static int getAVGChg(Class c) {
		if(c.getRecurrence()==0) {
			c.setRecurrence(1);
		}
		return c.getSumChg()/c.getRecurrence();
	}	
	
}
