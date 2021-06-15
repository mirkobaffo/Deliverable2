package main;

import java.util.Date;
import java.util.List;

public class Release {
	
	private String id;
	private String name;
	private Date date;
	private Integer number;
	private Commit commit;
	private List <Class> classes;
	private List <Commit> commitList;
	
	
	
	public Release(String id, String name, Date date, Integer number, Commit commit) throws Exception {
	    this.id = id; 
	    this.name = name;
	    this.date = date;
	    this.number = number;
	    this.commit = commit;
	  }

	Release() {
	
	}
	
	public String getId() {
		return id;
	}
	
	
	public String getName() {
		return name;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Integer getInt() {
		return number;
	}

	public Commit getCommit() {
		return commit;
	}
	
	
	public List<Class> getClasses(){
		return classes;
	}
	
	public List<Commit> getCommitList(){
		return commitList;
	}
	
	
	public static void setId(Release release, String id) {
		release.id = id;
	}
	
	public static void setName(Release release, String name) {
		release.name = name;
	}
	
	public static void setDate(Release release, Date date) {
		release.date = date;
	}
	
	public static void setNumber(Release release, Integer number) {
		release.number = number;
	}
	
	public void setCommit(Commit commit) {
		this.commit = commit;
	}
	
	public void setClasses(List<Class> classes) {
		this.classes = classes;
	}
	
	
	public void setCommitList(List<Commit> commitList) {
		this.commitList = commitList;
	}
}
