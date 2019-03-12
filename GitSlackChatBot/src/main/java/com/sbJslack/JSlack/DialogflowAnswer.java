package com.sbJslack.JSlack;

import java.util.ArrayList;

import com.google.cloud.dialogflow.v2.SessionsClient;

public class DialogflowAnswer {

	String dialogflowAnswer;
	ArrayList<String> texts;
	String sessionId;
	String projectId;
	String languageCode;
	SessionsClient sessionsClient;
	
	// FOR ARRAYLISTS
	public DialogflowAnswer (String projectId, String sessionId, String languageCode, SessionsClient sessionsClient, ArrayList<String> texts) {
		//CALCULATE ANSWER FROM ARRAY OF TEXTS
		setDialogflowAnswer(DialogflowSlackMethods.getDialogflowFulfillment( projectId,  sessionId,  languageCode,  sessionsClient, texts));
	}

	public String getDialogflowAnswer() {
		return dialogflowAnswer;
	}

	public void setDialogflowAnswer(String dialogflowAnswer) {
		this.dialogflowAnswer = dialogflowAnswer;
	}
}
