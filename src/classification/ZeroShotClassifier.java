package classification;

import java.awt.Label;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import utils.MalletUtils;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntPRTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.Labeling;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.MalletProgressMessageLogger;

public class ZeroShotClassifier {

	static{
		((MalletLogger) MalletProgressMessageLogger.getLogger("Logger")).getRootLogger().setLevel(MalletLogger.LoggingLevels[2]);
	}

	public static void main(String[] args) {
		
		//Load data instances from file
		//InstanceList origInstances = MalletUtils.getInstancesFromTextFile("/Users/shashans/Work/tools/mallet-2.0.8/sample-data/numeric/zoo.txt", true);
		InstanceList origInstances = MalletUtils.getInstancesFromLibSVMFile("/Users/shashans/Work/tools/mallet-2.0.8/sample-data/numeric/zoo.libsvm");
		//InstanceList instances = MalletUtils.binarizeLabels(origInstances, classLabel);

		System.out.println("Set of labels:");
		for(int i=0; i<origInstances.getTargetAlphabet().size();i++){
			System.out.println(origInstances.getTargetAlphabet().lookupObject(i));
		}
		
		System.out.println("Set of features:");
		for(int i=0; i<origInstances.getDataAlphabet().size();i++){
			System.out.println(i+" "+origInstances.getDataAlphabet().lookupObject(i));
		}
		
		HashMap<String, HashMap<String, Double>> constraintHashMap = ConstraintGenerator.parseFileToConstraints(origInstances,"/Users/shashans/Desktop/descriptions1.txt");

		
		//String classLabel = "mammal";
		
		for(String classLabel:Arrays.stream(origInstances.getTargetAlphabet().toArray()).map(Object::toString).collect(Collectors.toList())){

			//Define training algorithm
			//		MaxEntTrainer trainer = new MaxEntTrainer();
			MaxEntPRTrainer trainer = new MaxEntPRTrainer();
			//trainer.setConstraintsFile("/Users/shashans/Work/tools/mallet-2.0.8/constraints.txt");
			trainer.setConstraintsHashMap(constraintHashMap);
			trainer.setMinIterations(5);
			trainer.setMaxIterations(100);
			trainer.setPGaussianPriorVariance(0.1);
			trainer.setQGaussianPriorVariance(1000);
			trainer.setUseValues(false);

			//runExperimentSplit(instances, trainer, 1, 0.2, classLabel);
			runExperimentSplit(origInstances, trainer, 1, 0.2, classLabel);
			//runExperimentSplitTrainCompleteData(instances, trainer, 1, 0.2, classLabel);
		}
		
	}

	public static void runExperimentSplitTrainCompleteData(InstanceList instances, ClassifierTrainer trainer, int numTrials, double trainProp, String classLabel){
	
		Trial[] trials = new Trial[numTrials];
		
		for(int i=0; i< numTrials; i++){
			//Test train split
			InstanceList[] instanceLists = MalletUtils.testTrainSplit(instances, trainProp);		
			InstanceList trainingInstances = instances;
			InstanceList testInstances = instanceLists[1];
			System.out.println("Size of training set: "+trainingInstances.size()+"\nSize of test set: "+testInstances.size());

			//Train and evaluate model			
			MaxEnt classifier = (MaxEnt) trainer.train(trainingInstances);
			trials[i] = new Trial(classifier, testInstances);
		}

		//Evaluator.evaluateMultiClasses(trials);
		Evaluator.evaluateForClassLabel(trials, classLabel);
	}
	
	public static void runExperimentSplit(InstanceList instances, ClassifierTrainer trainer, int numTrials, double trainProp, String classLabel){
		
		Trial[] trials = new Trial[numTrials];
		
		for(int i=0; i< numTrials; i++){
			//Test train split
			InstanceList[] instanceLists = MalletUtils.testTrainSplit(instances, trainProp);		
			InstanceList trainingInstances = instanceLists[0];
			InstanceList testInstances = instanceLists[1];
			System.out.println("Size of training set: "+trainingInstances.size()+"\nSize of test set: "+testInstances.size());

			//Train and evaluate model			
			MaxEnt classifier = (MaxEnt) trainer.train(trainingInstances);
			trials[i] = new Trial(classifier, testInstances);
		}

		//Evaluator.evaluateMultiClasses(trials);
		Evaluator.evaluateForClassLabel(trials, classLabel);
	}

}
