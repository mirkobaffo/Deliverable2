package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvMaker {
	
	private CsvMaker() {
		
	}

	public static void csvByWeka(List <WekaData> wList, List<Release> releases) throws IOException {
		String name = "/Users/mirko/Desktop/weka_data" + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Dataset");
			sb.append(",");
			sb.append("#trainingRelease");
			sb.append(",");
			sb.append("%training");
			sb.append(",");
			sb.append("%DefectiveInTraining");
			sb.append(",");
			sb.append("%DefectiveInTesting");
			sb.append(",");
			sb.append("Classifier");
			sb.append(",");
			sb.append("Balancing");
			sb.append(",");
			sb.append("FeatureSelection");
			sb.append(",");
			sb.append("Cost Sensitive");
			sb.append(",");
			sb.append("Sensitivity");
			sb.append(",");
			sb.append("TP");
			sb.append(",");
			sb.append("FP");
			sb.append(",");
			sb.append("TN");
			sb.append(",");
			sb.append("FN");
			sb.append(",");
			sb.append("Recall");
			sb.append(",");
			sb.append("Precision");
			sb.append(",");
			sb.append("AUC");
			sb.append(",");
			sb.append("Kappa");
			sb.append("\n");
			br.write(sb.toString());
			for (WekaData w : wList) {
				if(w.getEval() == null) {
					continue;
				}
				StringBuilder sb2 = new StringBuilder();
				sb2.append(w.getTrainingStep());
				sb2.append(",");
				sb2.append(w.getTrainingStep());
				sb2.append(",");
				sb2.append(((w.getTrainingStep())*100/7));
				sb2.append(",");
				sb2.append(Main.getPercentageDefectiveInTraining(releases, w.getTrainingStep()));
				sb2.append(",");
				sb2.append((releases.get(w.getTrainingStep()).getNumOfBuggyClass()*100)/releases.get(w.getTrainingStep()).getClasses().size());
				sb2.append(",");
				sb2.append(w.getClassifier());
				sb2.append(",");
				sb2.append(w.getSampling());
				sb2.append(",");
				sb2.append(w.getFeatureSelection());
				sb2.append(",");
				sb2.append(Main.getCostName(w));
				sb2.append(",");
				sb2.append(w.getTreshold());
				sb2.append(",");
				sb2.append(w.getEval().truePositiveRate(1));
				sb2.append(",");
				sb2.append(w.getEval().falsePositiveRate(1));
				sb2.append(",");
				sb2.append(w.getEval().falsePositiveRate(1));
				sb2.append(",");
				sb2.append(w.getEval().falseNegativeRate(1));
				sb2.append(",");
				sb2.append(w.getEval().recall(1));
				sb2.append(",");
				sb2.append(w.getEval().precision(1));
				sb2.append(",");
				sb2.append(w.getEval().areaUnderROC(1));
				sb2.append(",");
				sb2.append(w.getEval().kappa());
				sb2.append("\n");
				br.write(sb2.toString());
				}
			}	
		
	}
}
