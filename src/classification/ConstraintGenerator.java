package classification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.common.base.Preconditions;
import utils.QuantifierExpressions;
import utils.StanfordAnnotator;
import utils.Token;
import cc.mallet.types.Alphabet;
import cc.mallet.types.InstanceList;

public class ConstraintGenerator {

	/*
	 * Inputs:
	 * (0) learning task formulation [Concept & features labels]
	 * (1) a list of NL sentences describing constraints
	 * (2) lexicon (?)
	 * 
	 * Output:
	 * (1) List of Model constraints that can be fed to PRClassifier
	 * */

	public static HashMap<String,HashMap<String, Double>> parseFileToConstraints(InstanceList instances, String descriptionsFile) {

		HashMap<String,HashMap<String, Double>> constraintHashMap = new HashMap<String, HashMap<String,Double>>();
		//String constraintFile = "/Users/shashans/Work/tools/mallet-2.0.8/constraints.txt";
		try {
			List<String> lines = Files.readAllLines(Paths.get(descriptionsFile));
			//instances.getDataAlphabet().lookupIndex("");
			for(String line:lines){
				QuantificationConstraint constraint = parseLineToConstraint(line, instances);
				if(!constraint.getQuantifierExpression().equals(QuantifierExpressions.DEFAULT_QUANTIFIER_EXP)){
					constraintHashMap.put((String) instances.getDataAlphabet().lookupObject(constraint.getxIdx()), constraint.validateAndSetProbabilities());
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return constraintHashMap;
	}


	private static QuantificationConstraint parseLineToConstraint(String line, InstanceList instances) {

		Alphabet targetAlphabet = instances.getTargetAlphabet();
		Alphabet dataAlphabet = instances.getDataAlphabet();
		List<Token> tokens = StanfordAnnotator.annotate(line);
		System.out.println("\nParsing line "+line);

		//1.) Identify the label mentioned, and the token index
		Boolean foundLabel = false;
		int foundLabelIdx = -1;
		int foundLabelAtToken = -1;

		for(int tokIdx=0; tokIdx<tokens.size(); tokIdx++){
			for(int i=0 ; i< targetAlphabet.size(); i++){
				foundLabel = tokens.get(tokIdx).getWord().contains((String) targetAlphabet.lookupObject(i))	||
						tokens.get(tokIdx).getLemma().contains((String) targetAlphabet.lookupObject(i));
				//|| ((String) targetAlphabet.lookupObject(i)).contains(tokens.get(tokIdx).getWord())|| ((String) targetAlphabet.lookupObject(i)).contains(tokens.get(tokIdx).getLemma());

				if(foundLabel){
					foundLabelIdx = i;
					foundLabelAtToken = tokIdx;
					System.out.println("Found label at token:"+tokens.get(tokIdx).getWord()+ " corresponding to label "+(String) targetAlphabet.lookupObject(i));
					break;
				}
			}	

			if(foundLabel){
				break;
			}
		}

		//System.out.println("Label mentioned in constraint: "+line+" is :"+targetAlphabet.lookupObject(foundLabelIdx)+", corresponding to index: "+foundLabelIdx);

		//2.) Figure out quantifier expression, if any
		String quantifierExpression = QuantifierExpressions.DEFAULT_QUANTIFIER_EXP;

		for(int tokIdx=0; tokIdx<tokens.size(); tokIdx++){	
			for(int i=0; i<QuantifierExpressions.quantExpressionsAdverbs.length;i++){
				if(tokens.get(tokIdx).getWord().equals(QuantifierExpressions.quantExpressionsAdverbs[i])){
					quantifierExpression = QuantifierExpressions.quantExpressionsAdverbs[i];
					break;
				}
			}

			for(int i=0; i<QuantifierExpressions.quantExpressionsDeterminers.length;i++){
				if(tokens.get(tokIdx).getWord().equals(QuantifierExpressions.quantExpressionsDeterminers[i])){
					quantifierExpression = QuantifierExpressions.quantExpressionsDeterminers[i];
					break;
				}
			}

			if(!quantifierExpression.equals(QuantifierExpressions.DEFAULT_QUANTIFIER_EXP)){
				break;
			}
		}
		System.out.println("QExp: "+quantifierExpression);

		/*
		int i1= StringUtils.indexOfAny(line, QuantifierExpressions.quantExpressionsAdverbs);
		System.out.println("i1 is "+i1);
		if(i1!=-1){quantifierExpression = QuantifierExpressions.quantExpressionsAdverbs[i1];}
		int i2= StringUtils.indexOfAny(line, QuantifierExpressions.quantExpressionsDeterminers);
		System.out.println("i2 is "+i2);
		if(i2!=-1){quantifierExpression = QuantifierExpressions.quantExpressionsDeterminers[i2];}
		System.out.println("Adverb idx: "+i1+"\nDeterminder idx: "+i2);
		 */

		//3.) Figure out the x, here we assume that the data representation already contains evaluations of all possible features
		//String logicalForm = parseExplanationToFeature(line, tokens, dataAlphabet);
		Boolean foundFeature = false;
		int foundFeatureIdx = -1;
		int foundFeatureAtToken = -1;

		for(int tokIdx=0; tokIdx<tokens.size(); tokIdx++){
			for(int i=0 ; i< dataAlphabet.size(); i++){
				foundFeature = tokens.get(tokIdx).getWord().contains((String) dataAlphabet.lookupObject(i))	||
						tokens.get(tokIdx).getLemma().contains((String) dataAlphabet.lookupObject(i));
				//|| ((String) dataAlphabet.lookupObject(i)).contains(tokens.get(tokIdx).getWord())|| ((String) dataAlphabet.lookupObject(i)).contains(tokens.get(tokIdx).getLemma());

				if(foundFeature){
					foundFeatureIdx = i;
					foundFeatureAtToken = tokIdx;
					System.out.println("Found feature at token:"+tokens.get(tokIdx).getWord()+ " corresponding to feature "+(String) dataAlphabet.lookupObject(i));
					break;
				}
			}	

			if(foundFeature){
				break;
			}
		}

		String featureString = (String) dataAlphabet.lookupObject(foundFeatureIdx);
		//System.out.println("Feature mentioned in constraint: "+line+" is :"+featureString+", corresponding to index: "+foundFeatureIdx);

		//4.)Figure out the constraint type
		String type = classifyType(tokens, foundLabelAtToken, foundFeatureAtToken);
		Boolean negation = false;
		for(int tokIdx=0; tokIdx<tokens.size(); tokIdx++){
			if(tokens.get(tokIdx).getWord().equals("n't") || tokens.get(tokIdx).getWord().equals("not") || tokens.get(tokIdx).getWord().equals("no")){
				negation = true;
			}
		}
		System.out.println("Negation: "+negation);

		return new QuantificationConstraint(instances, foundLabelIdx, foundFeatureIdx, type, quantifierExpression, negation);
	}

	private static String parseExplanationToFeature(String line, List<Token>tokens, Alphabet dataAlphabet) {
		// In general, this is a semantic parser. Here, we just match against a predetermined list of features
		return null;
	}

	private static String classifyType(List<Token> tokens, int labelTokenIdx, int featureTokenIdx){
		Preconditions.checkState(labelTokenIdx!=featureTokenIdx, "labelTokenIdx should not match featureTokenIdx");

		//Feature-generation, and classify with a statistical classifier
		//List<Double> fVec = extractConstraintTypeFeatures(tokens, labelTokenIdx);
		//For now, lets try a simple rule based system
		return (featureTokenIdx > labelTokenIdx) ? "YX" : "XY";
	}


	private static List<Double> extractConstraintTypeFeatures(List<Token> tokens, int labelTokenIdx) {
		ArrayList<Double> features = new ArrayList<Double>();
		//Relative occurrence of if, that
		return null;
	}
}