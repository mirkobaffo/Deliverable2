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






public class TestWekaEasy{
	
	static List<Integer> precision = new ArrayList<>();
	static List<Integer> recall = new ArrayList<>();
	static List<Integer> Auc = new ArrayList<>();
	static List<Integer> kappa = new ArrayList<>();
	static String name = "";
	
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
			if(treshold) {
				costSens.setCostSensitive(1);
			}
			else {
				costSens.setCostSensitive(2);
			}
			costSens.setTreshold((tresholdValue*100)/(tresholdValue+1));
			costSens.setFeatureSelection(true);
			costSens.setTrainingStep(z);
			costSens.setClassifier(name);
			costSens.setSampling("NoSampling");
			costSens.setEvaluation(eval);
			return costSens;
		 }
		catch (Exception e) {
			 e.printStackTrace();
			 return costSens;
		}
		
		//System.out.println("AUC CostSensitive = "+eval.areaUnderROC(1));
		//System.out.println("Kappa CostSensitive = "+eval.kappa());
		//System.out.println("Recall CostSensitive = "+eval.recall(1));
		//System.out.println("Precision CostSensitive = "+eval.precision(1));	
	}
	
	
	
	//I metodi makeTrainingSet e makeTesting set implementano il Walk Forward
	
	public static List<String> makeTrainingSet(List<Release> releases) throws Exception {
		List<String> trainingSet = new ArrayList<>();
		for (int i = 1; i <releases.size(); i++) {
			String s = new String();
			s = CsvWriter.csvForWeka(releases.subList(0,i),i);
			String path = CsvToArff.arffCreation(s);
			System.out.println("path: " + path);
			trainingSet.add(path);
		}
		
		return trainingSet;
		
	}
	
	
	public static List<String> makeTestingSet(List<Release> releases) throws Exception {
		List<String> testingSet = new ArrayList<>();
		for(int i = 1; i<releases.size(); i++) {
			String s = new String ();
			s = CsvWriter.csvForWeka(releases.subList(i, i+1),i+10);
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
			System.out.println("AUC SMOTE = "+eval.areaUnderROC(1));
			System.out.println("Kappa SMOTE = "+eval.kappa());
			System.out.println("Precision SMOTE = "+eval.precision(1)+ "\n");			
			System.out.println("Recall SMOTE = "+eval.recall(1)+ "\n");
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
			System.out.println("num: " + num);
			String percentage = String.valueOf(num);
			opts = new String[]{ "-B", "0","-S","1", "-Z", percentage};
			resample.setOptions(opts);
			fc.setFilter(resample);
			System.out.println("percentage: " + resample.getSampleSizePercent() + " Z: " + z);
			fc.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			eval.evaluateModel(fc, testing); //sampled
			oS.setFeatureSelection(true);
			oS.setTrainingStep(z);
			oS.setClassifier(name);
			oS.setSampling("OverSampling");
			oS.setEvaluation(eval);
			System.out.println("AUC oversampled = "+eval.areaUnderROC(1));
			System.out.println("Kappa oversampled = "+eval.kappa());
			System.out.println("Precision oversampled = "+eval.precision(1)+ "\n");			
			System.out.println("Recall oversampled = "+eval.recall(1)+ "\n");
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
		System.out.println("AUC undersampled = "+eval.areaUnderROC(1));
		System.out.println("Kappa undersampled = "+eval.kappa());
		System.out.println("Precision undersampled= "+eval.precision(1)+ "\n");			
		System.out.println("Recall undersampled= "+eval.recall(1)+ "\n");
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
		System.out.println("attributi filtrati: " + numAttrFiltered);
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
	    featureSelection.setSampling("NoSampling");
	    featureSelection.setEvaluation(evalClass);
		wekaList.add(featureSelection);
		System.out.println("AUC filtered = "+evalClass.areaUnderROC(1));
		System.out.println("Kappa filtered = "+evalClass.kappa());
		System.out.println("Recall filtered = "+evalClass.recall(1));
		System.out.println("Precision filtered = "+evalClass.precision(1));
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
		//System.out.println("defectiveInTraining: " + defectiveInTraining + " TrainingSet: " + trainingSet);
		if(defectiveInTraining == 0) {
			defectiveInTraining = 1;
		}
		List<WekaData> wekaList = new ArrayList<>();
		//load datasets
		DataSource source1 = new DataSource(trainingSet);
		Instances training = source1.getDataSet();
		DataSource source2 = new DataSource(testingSet);
		Instances testing = source2.getDataSet();
		int numAttrNoFilter = training.numAttributes();
		System.out.println("attributi non filtrati: " + numAttrNoFilter);
		training.setClassIndex(numAttrNoFilter - 1);
		testing.setClassIndex(numAttrNoFilter - 1);
		for(int i=0; i<3; i++) {
			Classifier classifier = getClassifier(i);
			//wekaList.addAll(featuresSelection(training, testing, defectiveInTraining, classifier, z, wekaList));
			featuresSelection(training, testing, defectiveInTraining, classifier, z, wekaList);
			classifier.buildClassifier(training);
			Evaluation evalClass = new Evaluation(testing);
			evalClass.evaluateModel(classifier, testing); 
			WekaData simple = new WekaData();
			simple.setTrainingStep(z);
			simple.setClassifier(name);
			simple.setSampling("NoSampling");
			simple.setEvaluation(evalClass);
			wekaList.add(simple);
			System.out.println("Classifier: " + name);
			System.out.println("AUC no filter = "+evalClass.areaUnderROC(1));
			System.out.println("Kappa no filter = "+evalClass.kappa());
			System.out.println("Recall no filter = "+evalClass.recall(1));
			System.out.println("Precision no filter = "+evalClass.precision(1));
		}
		return wekaList;
	}
	
	
}
