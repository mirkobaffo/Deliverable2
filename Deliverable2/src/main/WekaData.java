package main;

import weka.classifiers.Evaluation;

public class WekaData {

	private Evaluation eval;
	private String classifier;
	private Boolean featureSelection;
	private String sampling;
	//0 value is no costSensitive, 1 value is sensitive threshold, 2 value is sensitive learning 
	private int costSensitive;
	private int trainingStep;
	private int treshold;
	
	WekaData(){
		this.costSensitive = 0;
		this.featureSelection = false;
		this.classifier = "";
		this.sampling = "";
	}
	
	
	public Boolean getFeatureSelection() {
		return this.featureSelection;
	}
	
	
	public int getCostSensitive() {
		return this.costSensitive;
	}
	
	
	public String getClassifier() {
		return this.classifier;
	}
	
	
	public String getSampling() {
		return this.sampling;
	}
	
	
	public Evaluation getEval() {
		return this.eval;
	}
	
	
	public int getTrainingStep() {
		return this.trainingStep;
	}
	
	
	public int getTreshold() {
		return this.treshold;
	}
	
	
	public void setFeatureSelection(Boolean bool) {
		this.featureSelection = bool;
	}
	
	
	public void setCostSensitive(int n) {
		this.costSensitive = n;
	}
	
	
	public void setSampling(String sampling) {
		this.sampling = sampling;
	}
	
	
	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}
	
	
	public void setEvaluation(Evaluation eval) {
		this.eval = eval;
	}
	
	
	public void setTrainingStep(int z) {
		this.trainingStep = z;
	}
	
	
	public void setTreshold(int treshold) {
		this.treshold = treshold;
	}
	
	
}
