package com.sbJslack.JSlack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class Metrics {
	private HashMap<Integer, ChatLog> mapChatLog;
	
	// A CHATLOG OBJECT CONTAINS EVENT AND NLP DIALOGFLOW ASSOCIATED WITH IT.
	// EVENT CAN IDENTIFIY THE USER INFORMATION OF THE CHAT
	public Metrics(HashMap<Integer, ChatLog> mapChatLog) {
		this.mapChatLog = mapChatLog;
	}
	
	// METHOD DISPLAYS ALL METRICS
	public void getMetricChatLog() throws Exception {
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("DISPLAYING METRICS");
		System.out.println("------------------------------------------------------------------");
		// FOR EACH CHALOG PRINT OUT USER ID, NAME, EMAIL, QUERY AND BOT RESPONSE
		for (Entry<Integer, ChatLog> entry : mapChatLog.entrySet()) {
			String id = entry.getValue().getEvent().getSender().getId();
			String userName = entry.getValue().getEvent().getSender().getUserName();
			String realName = entry.getValue().getEvent().getSender().getRealName();
			String email = entry.getValue().getEvent().getSender().getUserMail();
			String timeStamp = entry.getValue().getEvent().getTimeStamp();
			//String channel = entry.getValue().getEvent().getChannel();
			
			// PRINT OUT EVENT INFO FOR MESSAGE
			toStringEventInfo(id, userName, realName, email, timeStamp);
			
			// PRINT OUT CLIENT AND BOT CHATLOG
			toStringChatLog(userName, entry);
		}
		
		// LOGGING HOW MANY TIMES "SORRY, I DON'T UNDERSTAND WAS REPLIED BY THE BOT"
		toStringConfusion();
		
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("END OF METRICS");
		System.out.println("------------------------------------------------------------------");
	}

	public HashMap<Integer, ChatLog> getMapChatLog() {
		return mapChatLog;
	}

	public void setMapChatLog(HashMap<Integer, ChatLog> mapChatLog) {
		this.mapChatLog = mapChatLog;
	}
	
	public void toStringEventInfo(String id, String userName, String realName, String email, String timeStamp){
		 System.out.println("id:" + id + ", userName:" + userName + ", realName:" + realName + ", email:" + email + ", timeStamp:" + timeStamp); 
	}
	
	public void toStringChatLog(String userName, Entry<Integer, ChatLog> entry) throws Exception {
		System.out.println("userName: " + entry.getValue().getGoogleNlpMessage().getText()); 
		System.out.println("Bot: " + entry.getValue().getDialogflowAnswer().dialogflowAnswer); 
		System.out.println("SENTIMENT: " + entry.getValue().getGoogleNlpMessage().getSentiment()); 
	}
	
	// DISPLAY CONFUSION TRIGGERS
	// COUNTER FOR HOW MANY TIMES FALLBACK ANSWER WAS USED
	// TAKES IN ALL TEXT AND FINDS WHICH ONE TRIGGERED FALLBACK ANSWER
	public void toStringConfusion() {
		int counterConfusion = 0;
		System.out.println("\nCONFUSION TRIGGER"); 
		for (Entry<Integer, ChatLog> entry : mapChatLog.entrySet()) {
			//GET "SORRY, I DON'T UNDERSTAND."
			if(entry.getValue().getDialogflowAnswer().dialogflowAnswer.equals("Sorry, I don't understand.")) {
				System.out.println("QUERY: " + entry.getValue().getGoogleNlpMessage().getText()); 
				counterConfusion = counterConfusion + 1;
			}
		}
		System.out.println("CONFUSION TRIGGER: " + counterConfusion + " TIMES"); 
	}
}
