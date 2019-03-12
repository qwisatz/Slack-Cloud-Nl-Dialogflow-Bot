package com.sbJslack.JSlack;

import java.util.HashMap;

import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

public class GoogleNlpSlackMethods {

	public static float analyzeSentimentText(LanguageServiceClient language, Document doc, String text)throws Exception {
		Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
		return sentiment.getScore();
	}
	
	public static String classifyText(LanguageServiceClient language, Document doc, String text) throws Exception {
		String categoryOfText = "";
		ClassifyTextRequest request = ClassifyTextRequest.newBuilder().setDocument(doc).build();
		// detect categories in the given text
		ClassifyTextResponse response = language.classifyText(request);

		for (ClassificationCategory category : response.getCategoriesList()) {
			categoryOfText = category.getName();
		}
		
		return categoryOfText;
	}

	// RETURNS ENTITIES AND STORE THEM
	public static HashMap<Integer, Entity> analyzeEntitiesText(LanguageServiceClient language, Document doc, String text) throws Exception {
		HashMap<Integer, Entity> mapEntity = new HashMap<Integer, Entity>();
		int i = 0;
		
		AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF16).build();
		AnalyzeEntitiesResponse response = language.analyzeEntities(request);

		// Print the response
		for (Entity entity : response.getEntitiesList()) {
			mapEntity.put(i, entity);
			i = i+ 1;
		}
		
		return mapEntity;
	}
}
