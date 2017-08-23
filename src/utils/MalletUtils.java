package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SvmLight2FeatureVectorAndLabel;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.pipe.iterator.SelectiveFileLineIterator;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.util.Randoms;

public class MalletUtils {

	public static InstanceList getInstancesFromTextFile(String fileName, Boolean instanceHasName) {

		InstanceList instances = new InstanceList(MalletUtils.getDefaultPipe());
		try {
			String lineRegex = "^(\\S*)[\\s,]*(.*)$";
			int nameOption = 0, labelOption = 1, dataOption = 2;
			if(instanceHasName){	
				lineRegex = "^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$";	
				nameOption = 1;
				labelOption = 2;
				dataOption = 3;
			}
			CsvIterator iterator = new CsvIterator (fileName, lineRegex, dataOption, labelOption, nameOption);
			instances.addThruPipe(iterator);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return instances;
	}
	
	public static InstanceList getInstancesFromLibSVMFile(String fileName){
		InstanceList instances = new InstanceList(MalletUtils.getLibSVMPipe());
		try {
			Reader fileReader = new InputStreamReader(new FileInputStream(fileName));
			instances.addThruPipe (new SelectiveFileLineIterator (fileReader, "^\\s*#.+"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instances;
	}
	
    public static InstanceList[] testTrainSplit(InstanceList instances, double trainingProportion) {

        InstanceList[] instanceLists = instances.split(new Randoms(), new double[] {trainingProportion, 1.0 - trainingProportion});                                                                          
        //Classifier classifier = trainClassifier( instanceLists[TRAINING] );
        //return new Trial(classifier, instanceLists[TESTING]);
        return instanceLists;
    }
    
	public static Pipe getDefaultPipe() {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Read data from File objects
		pipeList.add(new Input2CharSequence("UTF-8"));
		// Regular expression for what constitutes a token.
		Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{N}_]+");
		// Tokenize raw strings
		pipeList.add(new CharSequence2TokenSequence(tokenPattern));
		// Normalize all tokens to all lowercase
		pipeList.add(new TokenSequenceLowercase());
		// Remove stopwords from a standard English stoplist.
		pipeList.add(new TokenSequenceRemoveStopwords(false, false));
		// Rather than storing tokens as strings, convert them to integers by looking them up in an alphabet.
		pipeList.add(new TokenSequence2FeatureSequence());
		// Do the same thing for the "target" field
		pipeList.add(new Target2Label());
		// Now convert the sequence of features to a sparse vector, mapping feature IDs to counts.
		pipeList.add(new FeatureSequence2FeatureVector());
		// Print out the features and the label
		//pipeList.add(new PrintInputAndTarget());
		return new SerialPipes(pipeList);
	}

	public static Pipe getLibSVMPipe() {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		pipeList.add(new SvmLight2FeatureVectorAndLabel());
		//pipeList.add(new PrintInputAndTarget());
		return new SerialPipes(pipeList);
	}
	
	public static InstanceList binarizeLabels(InstanceList instances, String label) {
		
		InstanceList iListCopy = (InstanceList) instances.clone();

		LabelAlphabet blank = new LabelAlphabet();
		LabelAlphabet newAlpha = new LabelAlphabet();
		newAlpha.lookupIndex(label, true);
		newAlpha.lookupIndex("not_"+label, true);

		Noop pipe = new Noop(blank, newAlpha);
		InstanceList newIList = new InstanceList(pipe);

		//iterate through each instance and change the target based on the original value.
		for (int i = 0; i < iListCopy.size(); i++) {
		   Instance inst = iListCopy.get(i);

		   FeatureVector original = (FeatureVector) inst.getData();
		   Instance newInst = pipe.instanceFrom(new Instance(original, newAlpha, inst.getName(), inst.getSource()));
		   if (inst.getLabeling().toString().equals(label)) {  
		       newInst.setTarget(((LabelAlphabet) newIList.getTargetAlphabet()).lookupLabel(label));
		   } else {
		       newInst.setTarget(((LabelAlphabet) newIList.getTargetAlphabet()).lookupLabel("not_"+label));
		   }
		   newIList.add(newInst);
		}	
		return newIList;
	}

	public static void main(String[] args) {
		//InstanceList instances = MalletUtils.getInstancesFromTextFile("/Users/shashans/Work/tools/mallet-2.0.8/sample-data/numeric/zoo_features.tsv", false);
		InstanceList instances = MalletUtils.getInstancesFromLibSVMFile("/Users/shashans/Work/tools/mallet-2.0.8/sample-data/numeric/zoo.libsvm");
		System.out.println(instances.size());
		System.out.println(instances.get(0).getLabeling());
	}

}
