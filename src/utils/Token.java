package utils;

public class Token {
	
	private String surfaceForm;
	private String word;
	private String lemma;
	private String pos;
	private String ner;
	
	public Token(String surfaceForm, String word, String lemma, String pos, String ner){
		this.surfaceForm = surfaceForm;
		this.word=	word;
		this.lemma=	lemma;
		this.pos=	pos;
		this.ner=	ner;
	}
	
	public void display(){
		System.out.println(String.join("|", surfaceForm, word, lemma, pos, ner));
	}
	
	public String getWord() {
		return word;
	}

	public String getLemma() {
		return lemma;
	}

	public String getPos() {
		return pos;
	}

	public String getNer() {
		return ner;
	}

	public String getSurfaceForm() {
		return surfaceForm;
	}
	

}
