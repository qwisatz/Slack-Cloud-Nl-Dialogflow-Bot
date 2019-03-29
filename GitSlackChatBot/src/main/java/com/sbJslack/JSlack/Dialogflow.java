package com.sbJslack.JSlack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
public class Dialogflow {

	String credentials;
	String projectId;
	CredentialsProvider credentialsProvider = null;
	String sessionId = UUID.randomUUID().toString();
	String languageCode = "en-US";
	SessionsClient sessionsClient;
	SessionsSettings sessionsSettings; 
	
	public Dialogflow(String credentials, String projectId) throws FileNotFoundException, IOException {
		this.credentials = credentials;
		this.projectId = projectId;
		// BUILD SESSION CLIENT USING CREDENTIALS
		createSessionsClient();
	}
	
	public void createSessionsClient() throws FileNotFoundException, IOException {
		credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(credentials)));
		SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
		sessionsSettings = settingsBuilder.setCredentialsProvider(credentialsProvider).build();
		
		sessionsClient = SessionsClient.create(sessionsSettings);
	}
	
	public DialogflowAnswer getDialogflowAnswer(ArrayList<String> texts) {
		// CONSTRUCT THE DIALOGFLOWANSWER
		// CREATES THE FULLFILLMENT TEXT
		DialogflowAnswer dialogflowAnswer = new DialogflowAnswer(projectId, sessionId, languageCode, sessionsClient, texts);
		
		return dialogflowAnswer;
	}
}