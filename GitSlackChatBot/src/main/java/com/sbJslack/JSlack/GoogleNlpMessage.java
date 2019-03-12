package com.sbJslack.JSlack;

import java.util.HashMap;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Document.Type;

public class GoogleNlpMessage {

	private LanguageServiceClient language;
	private Document doc;
	private String text;
	private float sentiment;
	private String category;
	private HashMap<Integer, Entity> mapEntity;
	
	
	public GoogleNlpMessage( LanguageServiceClient language, String text) throws Exception {
		// LANGUAGE CREATED IN GoogleNlp, USED FOR SETTING CREDENTIALS
		this.language = language;
		this.text = text;
		// EVERY NEW TEXT NEEDS TO CREATE A NEW DOCUMENT OF IT TO BE ANALYSED
		this.doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
		
		//ANALYSE SENTIMENT
		setSentiment(GoogleNlpSlackMethods.analyzeSentimentText(language, doc, text));
		
		// ANALYSE CATEGORY, CAN'T ANALYSE IF TOO LITTLE TOKENS
		// setCategory(GoogleNlpSlackMethods.classifyText(language, doc, text));

		// ANALYSE ENTITY
		setMapEntity(GoogleNlpSlackMethods.analyzeEntitiesText(language, doc, text));
		
	}
	
	@Override
	public String toString() {
		return "GoogleNlpMessage [text=" + text + ", sentiment=" + sentiment + ", mapEntity=" + mapEntity + "]";
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public float getSentiment() throws Exception {
		return sentiment;
	}

	public void setSentiment(float sentiment) throws Exception {
		this.sentiment = sentiment;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public LanguageServiceClient getLanguage() {
		return language;
	}

	public void setLanguage(LanguageServiceClient language) {
		this.language = language;
	}

	public HashMap<Integer, Entity> getMapEntity() {
		return mapEntity;
	}

	public void setMapEntity(HashMap<Integer, Entity> mapEntity) {
		this.mapEntity = mapEntity;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
}
