package classification;

import java.util.HashMap;
import utils.QuantifierExpressions;
import cc.mallet.types.InstanceList;

public class QuantificationConstraint {
	
	private InstanceList instances;
	private int yIdx;
	private int xIdx;
	private String type;
	private String quantifierExpression;
	private Boolean negation;
	private HashMap<String, Double> probs = new HashMap<String, Double>();;
	
	public QuantificationConstraint(InstanceList instances, int yIdx, int xIdx, String type, String quantifierExpression, Boolean negation){
		this.instances = instances;
		this.yIdx = yIdx;
		this.xIdx = xIdx;
		this.type = type;
		this.quantifierExpression = quantifierExpression;
		this.negation = negation;
	}
	
	protected HashMap<String, Double> validateAndSetProbabilities(){	
		//Populate hashmap with probability values for all target labels
		String label = (String) instances.getTargetAlphabet().lookupObject(this.yIdx);
		double p = QuantifierExpressions.quantExpressionProbs.get(this.quantifierExpression);
		p = this.negation ? 1.0 - p : p;
		probs.put(label, p);
		System.out.println("QExp: "+this.quantifierExpression+" corresponds to prob "+p);
		
		//For now, let's assume a uniform distribution over other labels
		double p1 = (1.0 - p)/(instances.getTargetAlphabet().size()-1);
		System.out.println("Other labels have probability "+p1);
		for(int i=0; i<instances.getTargetAlphabet().size(); i++){
			if(i!=this.yIdx){
				String label1 = (String) instances.getTargetAlphabet().lookupObject(i);
				probs.put(label1, p1);
			}
		}
		return probs;
	}

	public InstanceList getInstances() { return this.instances; }
	public int getyIdx() { return yIdx; }
	public int getxIdx() { return xIdx; }
	public String getType() {return type; }
	public String getQuantifierExpression() {return quantifierExpression;}
	public Boolean getNegation() { return negation; }
	public HashMap<String, Double> getProbs() {	return probs;}
	
}