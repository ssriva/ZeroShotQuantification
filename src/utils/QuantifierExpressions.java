package utils;

import java.util.HashMap;

public class QuantifierExpressions {

	public static final String[] quantExpressionsAdverbs = new String[] {
			"always", 
			"definitely",
			"certainly",
			//"almost always",
			"usually",
			"normally",
			"generally",
			"general",
			"often",
			"frequently",
			"sometimes",
			"occasionally",
			"seldom",
			"rarely",
			//"almost never",
			"never"
	};

	public static final String[] quantExpressionsDeterminers = new String[] {
			"all", 
			//"almost all",
			"most",
			"majority",
			"many",
			"some",
			"few"
			//"almost no",
			//"no"
	};
	
	public static HashMap<String,Double> quantExpressionProbs = new HashMap<String, Double>();
	static{
		quantExpressionProbs.put("always", 0.9);
		quantExpressionProbs.put("definitely", 0.98);
		quantExpressionProbs.put("certainly", 0.98);
		quantExpressionProbs.put("usually", 0.7);
		quantExpressionProbs.put("normally", 0.7);
		quantExpressionProbs.put("generally", 0.7);
		quantExpressionProbs.put("general", 0.7);
		quantExpressionProbs.put("often", 0.5);
		quantExpressionProbs.put("frequently", 0.3);
		quantExpressionProbs.put("sometimes", 0.3);
		quantExpressionProbs.put("occasionally", 0.2);
		quantExpressionProbs.put("seldom", 0.1);
		quantExpressionProbs.put("rarely", 0.1);
		quantExpressionProbs.put("never", 0.05);
		
		quantExpressionProbs.put("all", 1.0);
		quantExpressionProbs.put("most", 0.6);
		quantExpressionProbs.put("majority", 0.6);
		quantExpressionProbs.put("many", 0.4);
		quantExpressionProbs.put("some", 0.3);
		quantExpressionProbs.put("few", 0.2);
	}

	public static final String DEFAULT_QUANTIFIER_EXP = "DEFAULT_QUANTIFIER_EXP";
}
