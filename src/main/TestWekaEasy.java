package main;
/*
 *  How to use WEKA API in Java 
 *  Copyright (C) 2014 
 *  @author Dr Noureddin M. Sadawi (noureddin.sadawi@gmail.com)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it as you wish ... 
 *  I ask you only, as a professional courtesy, to cite my name, web page 
 *  and my YouTube Channel!
 *  
 */

import weka.attributeSelection.BestFirst;
//import required classes
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.bayes.NaiveBayes;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.*;
import weka.classifiers.lazy.IBk;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;






public class TestWekaEasy{
	
	static List<Integer> precision = new ArrayList<>();
	static List<Integer> recall = new ArrayList<>();
	static List<Integer> auc = new ArrayList<>();
	static List<Integer> kappa = new ArrayList<>();
	static String name = "";
	static String noSamplingString = "NoSampling";
	private static final Logger LOGGER = Logger.getLogger(TestWekaEasy.class.getName());

	
	
	private TestWekaEasy() {
		
	}
	
	
	
	private static CostMatrix createCostMatrix(double weightFalsePositive, double weightFalseNegative) {
	    CostMatrix costMatrix = new CostMatrix(2);
	    costMatrix.setCell(0, 0, 0.0);
	    costMatrix.setCell(1, 0, weightFalsePositive);
	    costMatrix.setCell(0, 1, weightFalseNegative);
	    costMatrix.setCell(1, 1, 0.0);
	    return costMatrix;
	}
	
	
	public static WekaData applyCostSensitive(Classifier c,Instances training,Instances testing,  int z, Boolean treshold, int tresholdValue){
		Evaluation eval = null; 
		WekaData costSens = new WekaData();

		CostSensitiveClassifier c1 = new CostSensitiveClassifier();
		try {	
			c1.setClassifier(c);
			c1.setCostMatrix(createCostMatrix(1.0, tresholdValue));
			c1.buildClassifier(training);
			c1.setMinimizeExpectedCost(treshold);
			eval = new Evaluation(testing,c1.getCostMatrix());
			eval.evaluateModel(c1, testing);
			if(Boolean.TRUE.equals(treshold)) {
				costSens.setCostSensitive(1);
			}
			else {
				costSens.setCostSensitive(2);
			}
			costSens.setTreshold((tresholdValue*100)/(tresholdValue+1));
			costSens.setFeatureSelection(true);
			costSens.setTrainingStep(z);
			costSens.setClassifier(name);
			costSens.setSampling(noSamplingString);
			costSens.setEvaluation(eval);
			return costSens;
		 }
		catch (Exception e) {
			 e.printStackTrace();
			 return costSens;
		}
			
	}
	
	
	
	//I metodi makeTrainingSet e makeTesting set implementano il Walk Forward
	
	public static List<String> makeTrainingSet(List<Release> releases) throws Exception {
		List<String> trainingSet = new ArrayList<>();
		for (int i = 1; i <releases.size(); i++) {
			String s = CsvWriter.csvForWeka(releases.subList(0,i),i);
			String path = CsvToArff.arffCreation(s);
			trainingSet.add(path);
		}
		
		return trainingSet;
		
	}
	
	
	public static List<String> makeTestingSet(List<Release> releases) throws Exception {
		List<String> testingSet = new ArrayList<>();
		for(int i = 1; i<releases.size(); i++) {
			String s = CsvWriter.csvForWeka(releases.subList(i, i+1),i+10);
			String path = CsvToArff.arffCreation(s);
			testingSet.add(path);
		}
		return testingSet;
		
	}
	
	
	
	public static WekaData smoteSampling (Instances training, Instances testing, Integer defectiveInTraining, Classifier c, int z) {
		FilteredClassifier fc = new FilteredClassifier();
		WekaData smoteData = new WekaData();
		 try {	
			String[] opts;
			fc.setClassifier(c);
			SMOTE smote = new SMOTE();
			smote.setInputFormat(training);
			int num = 100*(training.numInstances() - 2*defectiveInTraining)/defectiveInTraining;
			if(num >500) {
				num = 500;
			}
			String percentage = String.valueOf(num);
			opts = new String[] {"-P", percentage,"-K","1","-C","0"};
			smote.setOptions(opts);
			fc.setFilter(smote);
			fc.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			eval.evaluateModel(fc, testing); //sampled
			smoteData.setFeatureSelection(true);
			smoteData.setTrainingStep(z);
			smoteData.setClassifier(name);
			smoteData.setSampling("Smote");
			smoteData.setEvaluation(eval);
			LOGGER.log(Level.INFO, "AUC SMOTE = "+eval.areaUnderROC(1));
			LOGGER.log(Level.INFO, "Kappa SMOTE = "+eval.kappa());
			LOGGER.log(Level.INFO, "Precision SMOTE = "+eval.precision(1)+ "\n");
			LOGGER.log(Level.INFO, "Recall SMOTE = "+eval.recall(1)+ "\n");
			return smoteData;
		 }
		 catch (Exception e) {
			 e.printStackTrace();
			 return smoteData;
		 }
	}
	
	
	public static WekaData overSampling(Instances training, Instances testing, Integer defectiveInTraining, Classifier c, int z) throws Exception {
		FilteredClassifier fc = new FilteredClassifier(); 
		WekaData oS = new WekaData();
		try {	
			String[] opts;
			fc.setClassifier(c);
			Resample resample = new Resample();
			resample.setInputFormat(training);
			int num = 100*(training.numInstances() - 2*defectiveInTraining)/defectiveInTraining;
			if(num >500) {
				num = 500;
			}
			String percentage = String.valueOf(num);
			opts = new String[]{ "-B", "0","-S","1", "-Z", percentage};
			resample.setOptions(opts);
			fc.setFilter(resample);
			fc.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			eval.evaluateModel(fc, testing); //sampled
			oS.setFeatureSelection(true);
			oS.setTrainingStep(z);
			oS.setClassifier(name);
			oS.setSampling("OverSampling");
			oS.setEvaluation(eval);
			LOGGER.log(Level.INFO, "AUC oversampled = "+eval.areaUnderROC(1));
			LOGGER.log(Level.INFO, "Kappa oversampled = "+eval.kappa());
			LOGGER.log(Level.INFO, "Precision oversampled = "+eval.precision(1)+ "\n");
			LOGGER.log(Level.INFO, "Recall oversampled = "+eval.recall(1)+ "\n");
			return oS;
		}
		catch (Exception e) {
			 e.printStackTrace();
			 return oS;
		}		
	}
		
	
	public static WekaData underSampling(Instances training, Instances testing, Classifier c, int z) throws Exception {
		Resample resample = new Resample();
		resample.setInputFormat(training);
		FilteredClassifier fc = new FilteredClassifier();
		fc.setClassifier(c);		
		SpreadSubsample  spreadSubsample = new SpreadSubsample();
		String[] opts = new String[]{ "-M", "1.0"};
		spreadSubsample.setOptions(opts);
		fc.setFilter(spreadSubsample);
		fc.buildClassifier(training);
		Evaluation eval = new Evaluation(testing);	
		eval.evaluateModel(fc, testing); //sampled
		WekaData uS = new WekaData();
		uS.setFeatureSelection(true);
		uS.setTrainingStep(z);
		uS.setClassifier(name);
		uS.setSampling("UnderSampling");
		uS.setEvaluation(eval);
		LOGGER.log(Level.INFO, "AUC undersampled = "+eval.areaUnderROC(1));
		LOGGER.log(Level.INFO, "Kappa undersampled = "+eval.kappa());
		LOGGER.log(Level.INFO, "Precision undersampled = "+eval.precision(1)+ "\n");
		LOGGER.log(Level.INFO, "Recall undersampled = "+eval.recall(1)+ "\n");
		return uS;
		
	}
	
	
	public static List<WekaData> featuresSelection(Instances training, Instances testing, int defectiveInTraining, Classifier c, int z, List<WekaData> wekaList) throws Exception {
		AttributeSelection filter = new AttributeSelection();
		CfsSubsetEval eval = new CfsSubsetEval();
		BestFirst search = new BestFirst();
		//set the algorithm to search 
		//set the filter to use the evaluator and search algorithm
		filter.setEvaluator(eval);
		filter.setSearch(search);
		//specify the dataset
		filter.setInputFormat(training);
		Instances filteredTraining = Filter.useFilter(training, filter);
		int numAttrFiltered = filteredTraining.numAttributes();
		String noFilterAtt = "attributi filtrati: " + numAttrFiltered;
		LOGGER.log(Level.INFO, noFilterAtt);
		filteredTraining.setClassIndex(numAttrFiltered - 1);
		Instances testingFiltered = Filter.useFilter(testing, filter);
		testingFiltered.setClassIndex(numAttrFiltered - 1);
		c.buildClassifier(filteredTraining);
		Evaluation evalClass = new Evaluation(testing);
	    evalClass.evaluateModel(c, testingFiltered);
	    WekaData featureSelection = new WekaData();
	    featureSelection.setFeatureSelection(true);
	    featureSelection.setTrainingStep(z);
	    featureSelection.setClassifier(name);
	    featureSelection.setSampling(noSamplingString);
	    featureSelection.setEvaluation(evalClass);
		wekaList.add(featureSelection);
		LOGGER.log(Level.INFO, "AUC filtered = "+evalClass.areaUnderROC(1));
		LOGGER.log(Level.INFO, "Kappa filtered = "+evalClass.kappa());
		LOGGER.log(Level.INFO, "Precision filtered = "+evalClass.precision(1)+ "\n");
		LOGGER.log(Level.INFO, "Recall filtered = "+evalClass.recall(1)+ "\n");
	    WekaData oS = overSampling(filteredTraining, testingFiltered, defectiveInTraining, c, z);
	    WekaData uS = underSampling(filteredTraining, testingFiltered, c, z);
	    WekaData smote = smoteSampling(filteredTraining, testingFiltered, defectiveInTraining, c, z);
		WekaData costSensitiveTreshold10 = applyCostSensitive(c,filteredTraining, testingFiltered, z, true, 10);
		WekaData costSensitiveTreshold5 = applyCostSensitive(c,filteredTraining, testingFiltered, z, true, 5);
		WekaData costSensitive = applyCostSensitive(c,filteredTraining, testingFiltered, z, false, 10);
		wekaList.add(oS);
		wekaList.add(uS);
		wekaList.add(smote);
		wekaList.add(costSensitiveTreshold10);
		wekaList.add(costSensitiveTreshold5);
		wekaList.add(costSensitive);
		
		return wekaList;
		
		
	}
	
	
	public static Classifier getClassifier(int i) {
		if(i == 0) {
			NaiveBayes classifier = new NaiveBayes();
			name = "NaiveBayes";
			return classifier;
		}
		
		else if(i == 1) {
			RandomForest classifier = new RandomForest();
			name = "RandomForest";
			return classifier;
		}
		else {
			IBk classifier = new IBk();
			name = "IBk";
			return classifier;
		}
	}
	
	
	public static List<WekaData> wekaAction(String testingSet, String trainingSet, int z, int defectiveInTraining) throws Exception {
		if(defectiveInTraining == 0) {
			defectiveInTraining = 1;
		}
		List<WekaData> wekaList = new ArrayList<>();
		//load datasets
		DataSource source1 = new DataSource(trainingSet);
		Instances training = source1.getDataSet();
		DataSource source2 = new DataSource(testingSet);
		Instances testing = source2.getDataSet();
		if(!training.attribute(0).isNumeric()) {
			WekaData fake = new WekaData();
			wekaList.add(fake);
			return wekaList;
		}
		int numAttrNoFilter = training.numAttributes();
		String noFilterAtt = "attributi non filtrati: " + numAttrNoFilter;
		LOGGER.log(Level.INFO, noFilterAtt);
		training.setClassIndex(numAttrNoFilter - 1);
		testing.setClassIndex(numAttrNoFilter - 1);
		for(int i=0; i<3; i++) {
			Classifier classifier = getClassifier(i);
			featuresSelection(training, testing, defectiveInTraining, classifier, z, wekaList);
			classifier.buildClassifier(training);
			Evaluation evalClass = new Evaluation(testing);
			evalClass.evaluateModel(classifier, testing); 
			WekaData simple = new WekaData();
			simple.setTrainingStep(z);
			simple.setClassifier(name);
			simple.setSampling(noSamplingString);
			simple.setEvaluation(evalClass);
			wekaList.add(simple);
			String classifierName = "Classifier: " + name;
			LOGGER.log(Level.INFO, classifierName);
			LOGGER.log(Level.INFO, "AUC no filter = "+evalClass.areaUnderROC(1));
			LOGGER.log(Level.INFO, "Kappa no filter = "+evalClass.kappa());
			LOGGER.log(Level.INFO, "Precision no filter = "+evalClass.precision(1)+ "\n");
			LOGGER.log(Level.INFO, "Recall no filter = "+evalClass.recall(1)+ "\n");
		}
		return wekaList;
	}
	
	
}
