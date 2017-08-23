package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordAnnotator {

	public static Properties props = new Properties();
	public static StanfordCoreNLP pipeline;
    static{
    	props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
    	//props.setProperty("regexner.mapping", "data/regexner.txt");
    	pipeline = new StanfordCoreNLP(props);
    	
    }
    
	public static ArrayList<Token> annotate(String text) {
	    Annotation document = new Annotation(text);
	    pipeline.annotate(document);
	    ArrayList<Token> tokenList = new ArrayList<Token>();

	    for(CoreMap sentence: document.get(SentencesAnnotation.class)) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	    	String originalForm = token.originalText(); 
	        String word = token.get(TextAnnotation.class).toLowerCase();
	        String lemma = token.get(LemmaAnnotation.class);
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        String ner = token.get(NamedEntityTagAnnotation.class);
	        tokenList.add(new Token(originalForm, word, lemma, pos, ner));
	      }
	    }
	    return tokenList;
	}
	
	public static void main(String[] args){
		List<Token> tokens = StanfordAnnotator.annotate("Barack Obama is the 44th president of the USA, and America's president.");
		for(Token t:tokens){
			t.display();
		}
	}


}
