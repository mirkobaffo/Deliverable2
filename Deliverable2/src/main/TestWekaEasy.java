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

//import required classes
import weka.core.Instances;

import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.filters.supervised.instance.Resample;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.*;




public class TestWekaEasy{
	
	
	public static void wekaAction(String trainingSet, String testingSet, int z) throws Exception{
		//load datasets
				DataSource source1 = new DataSource(trainingSet);
				Instances training = source1.getDataSet();
				DataSource source2 = new DataSource(testingSet);
				Instances testing = source2.getDataSet();
	
				int numAttr = training.numAttributes();
				training.setClassIndex(numAttr - 1);
				testing.setClassIndex(numAttr - 1);

				NaiveBayes classifier = new NaiveBayes();

				classifier.buildClassifier(training);

				Evaluation eval = new Evaluation(testing);	

				eval.evaluateModel(classifier, testing); 
				
				System.out.println("recall = "+eval.recall(1) + "stiamo al passaggio: " + z);
				System.out.println("precision = "+eval.precision(1) + "stiamo al passaggio: " + z);
				System.out.println("AUC = "+eval.areaUnderROC(1) + "stiamo al passaggio: " + z);
				System.out.println("kappa = "+eval.kappa()+ "stiamo al passaggio: " + z);
			
				
	}
}
