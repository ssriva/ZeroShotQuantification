package classification;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.Trial;
import cc.mallet.classify.evaluate.ConfusionMatrix;
import cc.mallet.types.InstanceList;

public class Evaluator {

	public static void evaluateMultiClasses(Trial[] trials){
		for(Trial trial:trials){
			ConfusionMatrix cm = new ConfusionMatrix(trial);
			double avg_F1 = 0.0, avg_P = 0.0, avg_R =0.0;
			for(int i=0; i<trial.getClassifier().getLabelAlphabet().size();i++){
				avg_F1 += trial.getF1(i)*cm.getClassPrior(i);
				avg_P += trial.getPrecision(i)*cm.getClassPrior(i);
				avg_R += trial.getRecall(i)*cm.getClassPrior(i);
				//System.out.println("Prior: "+cm.getClassPrior(i));
				//System.out.println("F1 for class " + classifier.getLabelAlphabet().lookupLabel(i) + ": " + trial.getF1(i));
			}
			System.out.println("SUMMARY:");
			System.out.println("Avg F1: "+ avg_F1);
			System.out.println("Accuracy: " + trial.getAccuracy());
			System.out.println(cm);
		}
	}

	public static void evaluateForClassLabel(Trial[] trials, String label){

		double avg_F1 = 0.0, avg_P = 0.0, avg_R =0.0, avg_Accu =0.0;

		for(Trial trial:trials){	
			avg_F1 += trial.getF1(label);
			avg_P += trial.getPrecision(label);
			avg_R += trial.getRecall(label);
			avg_Accu += trial.getAccuracy();
		}

		avg_F1 = avg_F1/trials.length;
		avg_P = avg_P/trials.length;
		avg_R = avg_R/trials.length;
		avg_Accu = avg_Accu/trials.length;

		System.out.println("SUMMARY for class "+label+" over "+trials.length+" runs");
		System.out.println("Accuracy: " + avg_Accu);
		System.out.println("Avg F1: "+ avg_F1);
		System.out.println("Avg Precision: "+ avg_P);
		System.out.println("Avg Recall: "+ avg_R);

	}
}

