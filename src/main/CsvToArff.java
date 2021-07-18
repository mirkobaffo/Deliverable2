package main;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveDuplicates;

public class CsvToArff {
	
	private static String[] proj = {"Bookkeeper","LIBCLOUD"};
	private static String path = "/Users/mirko/git/Deliverable2/Deliverable2/release3";
	
	public static String arffCreation(String path) throws Exception {
		File projectClasses = new File(path);
		String newPath = new String();
		if (projectClasses.exists()) {
			CSVLoader loader = new CSVLoader();
			loader.setFieldSeparator(",");
		    loader.setSource(projectClasses);
		    Instances data = loader.getDataSet();//get instances object
		    //System.out.println("path e data: " + data.enumerateAttributes());
		    /*for(int i = 0; i< data.numAttributes(); i ++) {
		    	System.out.println("l'attributo " + data.attribute(i).name() + "booleano: " + data.attribute(i).isNumeric());
		    }*/
		    
		    //data.deleteAttributeAt(0);//delete number of revision
		    //data.deleteAttributeAt(1);//delete class name
		    
		    //RemoveDuplicates rd = new RemoveDuplicates();
		    //rd.setInputFormat(data);
		    
		    //Instances subset = Filter.useFilter(data,rd);
		    
		    // save ARFF
		    ArffSaver saver = new ArffSaver();
		    saver.setInstances(data);//set the dataset we want to convert
		    //and save as ARFF
		    String path2 = path.substring(0, path.length()-4);
		    newPath = path2+"_Dataset.arff";
		    saver.setFile(new File(newPath));
		    saver.writeBatch();
		}
		
		return newPath;
	}
	
	
	public static void main(String[] args) {
		
		
		for(String s: proj) {	
		
			try {
				arffCreation(path);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}

}
