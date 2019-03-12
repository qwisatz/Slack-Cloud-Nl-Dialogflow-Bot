package com.sbJslack.JSlack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.websocket.DeploymentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.rtm.RTMClient;
import com.github.seratch.jslack.api.rtm.RTMMessageHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ullink.slack.simpleslackapi.ChannelHistoryModule;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.ChannelHistoryModuleFactory;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

@SpringBootApplication
public class SlackApplication {
	// CONFIGURE LOGGING
	private static final Logger logger = LoggerFactory.getLogger(SlackApplication.class);

	public static void main(String[] args) throws IOException, DeploymentException {
		/*------------------------------------------------------------------*/
		// RUN SPRING BOOT 
		/*------------------------------------------------------------------*/
		SpringApplication.run(SlackApplication.class, args);
		
		/*------------------------------------------------------------------*/
		// INITIALISE TOKENS FOR A SLACK CHAT BOT
		/*------------------------------------------------------------------*/
		// SLACK BOT OAUTHENTICATION
		String tokenSlackBotOAuth = "xoxb-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		// SLACK BOT RESIDES IN THIS CHANNEL
		String slackChannel = "xxxxxxx-CHANNEL NAME WHERE BOT IS ADDED TO FOR COMMUNICATION-xxxxxxxx";
		// GOOGLE NLP CREDENTIALS
		String pathTokenGoogleCloudNl = "xxxxxxxxxxxxxxxxxx.json";
		// DIALOG FLOW CREDENTIALS AND PROJECT ID 
		String pathTokenDialogflow = "xxxxxxxxxxxxxxxxxxxxxxx.json";
		String projectId = "xxxxxxxxxxxxxxxxxxxxxx";
		
		/*------------------------------------------------------------------*/
		// SETUP SLACKBOT
		/*------------------------------------------------------------------*/
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("CREATING SLACK BOT");
		System.out.println("------------------------------------------------------------------");
		// CREATE SLACKBOT OBJECT
		SlackBot slackBot = new SlackBot("slackBotNuigTestBot10", pathTokenGoogleCloudNl, pathTokenDialogflow, projectId);
		System.out.println("SLACKBOT OBJECT CREATED: " + slackBot.getName());

		/*------------------------------------------------------------------*/
		// SIMPLE SLACK API SETUP AND CONNECTION
		/*------------------------------------------------------------------*/
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("CREATING SLACK SESSION");
		System.out.println("------------------------------------------------------------------");
		// nuigTestBot10
		SlackSession session = SlackSessionFactory.createWebSocketSlackSession(tokenSlackBotOAuth);
		session.connect();
		
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("CHANNEL INFO: " + slackChannel);
		System.out.println("------------------------------------------------------------------");
		// GET INFO ON CHANNEL SELECTED
		SlackChannel channel = session.findChannelByName(slackChannel);
		System.out.println(channel.toString());

		System.out.println("\n------------------------------------------------------------------");
		System.out.println("BOTS IN SLACK");
		System.out.println("------------------------------------------------------------------");
		System.out.println(session.getBots().toString());
		
		/*------------------------------------------------------------------*/
		// CREATING EVENT LISTENER
		/*------------------------------------------------------------------*/
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("CREATING EVENT LISTENER");
		System.out.println("------------------------------------------------------------------");

		SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener() {
			@Override
			public void onEvent(SlackMessagePosted event, SlackSession session) {
				// PRINT INFORMATION OF ALL POSTERS
				slackBot.printInfoSlackUser(event);

				// BOT READS ITS OWN MESSAGES
				// TO PREVENT IT REPLYING TO ITSELF
				// IF SENDER IS A BOT, DON'T REPLY
				// ELSE SENDER IS A HUMAN, REPLY
				if (event.getSender().isBot()) {
					return;
				} else if (!event.getSender().isBot()) {
					/*------------------------------------------------------------------*/
					//RETURN NLP ANALYSIS OF MESSAGE
					/*------------------------------------------------------------------*/
					System.out.println("\n------------------------------------------------------------------");
					System.out.println("GOOGLE CLOUD NLP ANALYSIS");
					System.out.println("------------------------------------------------------------------");
					try {
						System.out.println("GOONLPMESSAGE: " + slackBot.getGoogleNlpMessage(event.getMessageContent()).toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					System.out.println("\n------------------------------------------------------------------");
					System.out.println("DIALOGFLOW RESPONSE");
					System.out.println("------------------------------------------------------------------");
					/*------------------------------------------------------------------*/
					// POST MESSAGE USING DIALOGFLOW
					/*------------------------------------------------------------------*/
					// PRINTS CURRENT CHAT MESSAGE NUMBER
					// USED FOR STORING CHATLOG INTO CHATLOG HASHMAP
					System.out.println(slackBot.getChatNumber()); 
					try {
						slackBot.postMessage(slackChannel, slackBot.handleMessage(slackBot.getChatNumber(), event), tokenSlackBotOAuth);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		System.out.println("EVENT LISTENER CREATED");

		/*------------------------------------------------------------------*/
		// ADD LISTENER TO SESSION
		/*------------------------------------------------------------------*/
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("ADDING LISTENER TO SESSION");
		System.out.println("------------------------------------------------------------------");
		// add it to the session
		session.addMessagePostedListener(messagePostedListener);
		
		//*-----------------------------------------------------------------*/
		/*---------------------------END OF MAIN----------------------------*/ 
		/*------------------------------------------------------------------*/ 
	}
}
