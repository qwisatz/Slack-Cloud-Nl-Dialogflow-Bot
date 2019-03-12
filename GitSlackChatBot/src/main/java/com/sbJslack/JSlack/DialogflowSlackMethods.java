package com.sbJslack.JSlack;

import java.util.ArrayList;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;

public class DialogflowSlackMethods {
	// TAKES IN TEXT ARRAYLIST
	// IF THE DIALOGFLOW DOESN'T HAVE THE ANSWER THEN RETURNS BLANK 
	public static String getDialogflowFulfillment(String projectId, String sessionId, String languageCode, SessionsClient sessionsClient, ArrayList<String> texts) {
		SessionName session = SessionName.of(projectId, sessionId);
		String answer = "Sorry I don't understand.";
		
		for (String text : texts) {
			// Set the text (hello) and language code (en-US) for the query
			Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

			// Build the query with the TextInput
			QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

			// Performs the detect intent request
			DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

			// Display the query result
			QueryResult queryResult = response.getQueryResult();
			
			// FULFILLMENT TEXT IS THE RESPONSE
			answer = queryResult.getFulfillmentText();
		}
		
		if(answer.equals("")) {
			answer = "Sorry, I don't understand.";
		}
		
		return answer;
	}
}
