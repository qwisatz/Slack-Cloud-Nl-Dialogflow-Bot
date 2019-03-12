package com.sbJslack.JSlack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class SlackBot {

	// SLACKBOT TAKES IN MESSAGES AND REPLIES
	// SLACKBOT CONTAINS HASHMAP ANSWERS
	// SLACKBOT CONTAINS METHODS TO REPLY
	private String name;
	private HashMap<Integer, ChatLog> mapChatLog = new HashMap<Integer, ChatLog>();
	private GoogleNlp googleNlp;
	private Dialogflow dialogflow;
	private int chatNumber = 0;
	private Metrics metrics;
	private int counterConfusion = 0;

	public SlackBot(String name, String googleNlpJsonCredential, String credentials, String projectId) throws FileNotFoundException, IOException {
		this.name = name;
		// CHATLOG WILL HOLD NLPMESSAGE AND DIALOGFLOWANSWER
		this.setMapChatLog(new HashMap<Integer, ChatLog>());
		// DIALOG FLOW WILL RETURN RESPONSES
		this.dialogflow = new Dialogflow(credentials, projectId);
		// THE SETUP OF GOOGLENLP TO ANALYSE THE TEXT
		this.googleNlp = new GoogleNlp(googleNlpJsonCredential);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<Integer, ChatLog> getMapChatLog() {
		return mapChatLog;
	}

	public void setMapChatLog(HashMap<Integer, ChatLog> mapChatLog) {
		this.mapChatLog = mapChatLog;
	}

	public GoogleNlp getGoogleNlp() {
		return googleNlp;
	}

	public void setGoogleNlp(GoogleNlp googleNlp) {
		this.googleNlp = googleNlp;
	}

	public Dialogflow getDialogflow() {
		return dialogflow;
	}

	public void setDialogflow(Dialogflow dialogflow) {
		this.dialogflow = dialogflow;
	}

	public int getChatNumber() {
		return chatNumber;
	}
	public void setChatNumber(int chatNumber) {
		this.chatNumber = chatNumber;
	}
	
	
	public int getCounterConfusion() {
		return counterConfusion;
	}

	public void setCounterConfusion(int counterConfusion) {
		this.counterConfusion = counterConfusion;
	}

	public GoogleNlpMessage getGoogleNlpMessage(String text) throws Exception {
		// GETS GOOGLENLP TO HANDLE IT
		return googleNlp.getGoogleNlpMessage(googleNlp, text);
	}
	
	public void storeChatLog(int messageNumber, ChatLog chatLog) {
		// TAKES IN chatLog OBJECT THAT CONTAINS: ID, NLP, DIALOGFLOW
		mapChatLog.put(messageNumber, chatLog);
	}
	
	public void createStoreChatLog(int messageNumber,SlackMessagePosted event, GoogleNlpMessage googleNlpMessage, DialogflowAnswer dialogflowAnswer) throws Exception {
		// CREATES A NEW CHATLOG OBJECT FROM THE TEXT AND PUT IT INTO CHATLOG HASHMAP
		ChatLog chatLog = new ChatLog(messageNumber, event,  googleNlpMessage, dialogflowAnswer);
		// PUTS THE CREATED CHATLOG INTO HASHMAP MEMORY LOG
		this.storeChatLog(messageNumber, chatLog);
	}
	
	/*------------------------------------------------------------------*/
	// HANDLES MESSAGE BY RETURNING REPLY AND CREATING KPI METRICS
	/*------------------------------------------------------------------*/
	// TAKES IN CHATNUMBER, CHATMESSAGES START AT 0
	// CHATNUMBER IS THE KEY OF HASHMAP OF CHATLOG
	// UPDATE IT AT THE END OF METHOD
	public String handleMessage(int messageNumber, SlackMessagePosted event) throws Exception {
		String answer = "Sorry, I don't understand.";
		int hashKey = messageNumber;

		// CREATE GOOGLELPMESSAGE: CONTAINS SENTIMENT AND MAPENTITY
		GoogleNlpMessage googleNlpMessage = googleNlp.getGoogleNlpMessage(googleNlp, event.getMessageContent());
		
		// CREATE DIALOGFLOWANSWER: CONTAINS STRING OF THE ANSWER
		// DIALOGFLOW STATIC METHOD TAKES IN ARRAYLIST TO ANALYSE
		ArrayList<String> texts = new ArrayList<>();
		texts.add(event.getMessageContent());
		DialogflowAnswer dialogflowAnswer = dialogflow.getDialogflowAnswer(texts); 
		
		// INPUT SLACKMESSAGEPOSTED EVENT, NLPMESSAGE AND DIALOGFLOWMESSAGE INTO A NEW CHATLOG EVERY TIME
		// createStoreChatLog CREATES A CHATLOG AND PUTS IT INTO mapChatLog
		createStoreChatLog(hashKey, event,  googleNlpMessage, dialogflowAnswer);
		
		// HANDLE THE REPONSE OF THE CHATLOG
		if (event.getMessageContent().equals("yes")) {
			answer = "Our customer email is: customer.desk@cmail.com";
			// RESET CONFUSION COUNTER TRIGGER
			this.setCounterConfusion(0);
			
		} else if (event.getMessageContent().equals("KPI")) {
			// DISPLAY METRICS
			// METRICS METHOD
			getMetrics();
			
		} else {
			// GET CHAT LOG OF CURRENT MESSAGE
			answer = this.getMapChatLog().get(hashKey).getDialogflowAnswer().getDialogflowAnswer();
			// IF ANSWER IS I DON'T KNOW ADD TO CONFUSION COUNTER
			if (answer.equals("Sorry, I don't understand.")) {
				this.counterConfusion = counterConfusion +1;
			}
		}
		
		// KEY FOR NEXT VALUE IN HASHMAP OF CHATLOG
		int nextMessageNumber = messageNumber + 1;
		this.setChatNumber(nextMessageNumber);
		
		// CHECK COUNTER CONFUSION > 4, ASK IF YOU LIKE TO CONTACT AN AGENT
		if (counterConfusion > 4) {
			answer = getAgent();
		}
		return answer;
	}
	
	public void getMetrics() throws Exception {
		// SETUP METRICS
		this.metrics = new Metrics(mapChatLog);
		metrics.getMetricChatLog();
	}
	
	// IF BOT ANSWERS WITH I DON'T KNOW 4 TIMES, THEN AGENT IS CONTACTED
	public String getAgent() {
		String help = "Would you like to speak with an agent? Type 'yes' to connect with an agent.";
		return help;
	}
	
	// PRINT INFO DURING CHAT
	public void printInfoSlackUser(SlackMessagePosted event) {
		// RECIEVE MESSAGE SENDER DETAILS
		// RECIEVE MESSAGE
		System.out.println("\nTYPE: " + event.getEventType());
		System.out.println("CHANNEL: " + event.getChannel());
		System.out.println("ID: " + event.getSender().getId());
		System.out.println("NAME: " + event.getSender().getUserName());
		System.out.println("MESSAGE: " + event.getMessageContent());
	}
	
	// METHOD TO POST MESSAGE
	public void postMessage(String channel, String message, String token) {
		try {
			// https://slack.com/api/chat.postMessage
			Slack slack = Slack.getInstance();

			ChatPostMessageResponse postResponse = slack.methods().chatPostMessage(
					ChatPostMessageRequest.builder().token(token).channel(channel).text(message).build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}









































































