package com.sbJslack.JSlack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;

public class GoogleNlp {

	// AUTH TOKEN FOR GOOGLE CLOUD NLP
	private String googleNlpJsonCredential = "AvayaNlp-e422dbca92e5.json";
	private LanguageServiceClient language;
	private CredentialsProvider credentialsProvider;

	public GoogleNlp(String googleNlpJsonCredential) throws FileNotFoundException, IOException {
		// SET CREDENTIALS
		this.googleNlpJsonCredential = googleNlpJsonCredential;
		// BUILD LANGUAGE CLIENT USING CREDENTIALS
		createLanguageClient();
	}

	public void createLanguageClient() throws FileNotFoundException, IOException {
		credentialsProvider = FixedCredentialsProvider
				.create(ServiceAccountCredentials.fromStream(new FileInputStream(googleNlpJsonCredential)));
		LanguageServiceSettings.Builder languageServiceSettingsBuilder = LanguageServiceSettings.newBuilder();
		LanguageServiceSettings languageServiceSettings = languageServiceSettingsBuilder
				.setCredentialsProvider(credentialsProvider).build();

		language = LanguageServiceClient.create(languageServiceSettings);
	}

	public LanguageServiceClient getLanguage() {
		return language;
	}

	public void setLanguage(LanguageServiceClient language) {
		this.language = language;
	}

	public GoogleNlpMessage getGoogleNlpMessage(GoogleNlp googleNlp,String text) throws Exception {
		// CREATES A GOOGLENLPMESSAGE BY INPUTTING STRING
		// GETS SENTIMENT AND MAP OF ENTITIES
		GoogleNlpMessage googleNlpMessage = new GoogleNlpMessage(googleNlp.getLanguage(), text);
		
		return googleNlpMessage;
	}
}
