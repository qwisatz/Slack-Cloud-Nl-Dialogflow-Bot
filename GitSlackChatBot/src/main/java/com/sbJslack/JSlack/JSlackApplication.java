package com.sbJslack.JSlack;

import java.io.IOException;

import javax.websocket.DeploymentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackChannelJoined;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackChannelJoinedListener;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

@SpringBootApplication
public class JSlackApplication {
	// CONFIGURE LOGGING

	public static void main(String[] args) throws IOException, DeploymentException {
		/*------------------------------------------------------------------*/
		// RUN SPRING BOOT
		/*------------------------------------------------------------------*/
		SpringApplication.run(JSlackApplication.class, args);
		/*------------------------------------------------------------------*/
		// INITIALISE TOKENS FOR A SLACK CHAT BOT
		/*------------------------------------------------------------------*/
		// SLACK BOT OAUTHENTICATION
		String tokenSlackBotOAuth = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		// SLACK BOT RESIDES IN THIS CHANNEL
		String slackChannel = "xxxxxxxx";
		// GOOGLE NLP CREDENTIALS
		String pathTokenGoogleCloudNl = "xxxxxxxxxxxxxxxxxxx.json";
		// DIALOG FLOW CREDENTIALS AND PROJECT ID
		String pathTokenDialogflow = "xxxxxxxxxxxxxxxxxxx.json";
		String projectId = "xxxxxxxxxxxxxxxxxxx";
		// SET AGENT BY INPUTTING HUMAN AGENT'S EMAIL
		String ccHumanAgentEmail = "xxxxxxxxxxxxxxxxxxx@xxxxxxxxxxxxxxxxxxx.com";

		/*------------------------------------------------------------------*/
		// SETUP SLACKBOT
		/*------------------------------------------------------------------*/
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("CREATING SLACK BOT");
		System.out.println("------------------------------------------------------------------");
		// CREATE SLACKBOT OBJECT
		SlackBot slackBot = new SlackBot("slackBotNuigTestBot10", pathTokenGoogleCloudNl, pathTokenDialogflow, projectId, ccHumanAgentEmail);
		System.out.println("SLACKBOT OBJECT CREATED: " + slackBot.getName());

		/*------------------------------------------------------------------*/
		// SIMPLE SLACK API SETUP AND CONNECTION
		/*------------------------------------------------------------------*/
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("CREATING SLACK SESSION");
		System.out.println("------------------------------------------------------------------");
		// nuigTestBot10
		SlackSession session = SlackSessionFactory.createWebSocketSlackSession(tokenSlackBotOAuth);
		// SET SESSION FOR BOT, USED IN CONTACTING AGENT
		slackBot.setSession(session);
		session.connect();

		System.out.println("\n------------------------------------------------------------------");
		System.out.println("CHANNEL INFO: " + slackChannel);
		System.out.println("------------------------------------------------------------------");
		// GET INFO ON CHANNEL SELECTED
		SlackChannel channel = session.findChannelByName(slackChannel);
		// SET CHANNEL FOR BOT, USED IN CONTACTING AGENT
		slackBot.setChannel(channel);
		System.out.println(channel.toString());

		System.out.println("\n------------------------------------------------------------------");
		System.out.println("INFO: SESSION");
		System.out.println("------------------------------------------------------------------");
		System.out.println(session.getBots().toString());
		// SET A CC AGENT
		SlackUser ccHumanAgent = session.findUserByEmail(ccHumanAgentEmail);

		/*------------------------------------------------------------------*/
		// CREATING EVENT LISTENER
		/*------------------------------------------------------------------*/
		RestTemplate restTemplate = new RestTemplate();

		SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener() {
			@Override
			public void onEvent(SlackMessagePosted event, SlackSession session) {
				/*------------------------------------------------------------------*/
				// CHECK IF HUMAN AGENT IS IN CHANNEL
				/*------------------------------------------------------------------*/
				// TO LISTEN TO CHANNEL JOINS/LEAVE, JOIN/LEAVE MESSAGES MUST BE TURNED ON IN
				// SETTINGS OF THE WORKSPACE
				// CHECKS IF AGENT IS IN CHANNEL BY GETTING JSON FROM conversations.members API
				// TOKEN = BOT'S TOKEN
				// CHANNEL = CHANNEL ID
				boolean agentInChannel;
				String apiConversationsMember = "https://slack.com/api/conversations.members?token="
						+ tokenSlackBotOAuth + "&channel=" + channel.getId() + "&pretty=1";

				String responseApiConversationsMember = restTemplate.getForObject(apiConversationsMember, String.class);
				if (responseApiConversationsMember.contains(ccHumanAgent.getId())) {
					agentInChannel = true;
				} else {
					agentInChannel = false;
				}

				/*------------------------------------------------------------------*/
				// PRINT METRICS
				/*------------------------------------------------------------------*/
				slackBot.printInfoSlackUser(event);

				/*------------------------------------------------------------------*/
				// BOT LOGIC
				/*------------------------------------------------------------------*/
				// IF AGENT IN CHANNEL, RETURN
				// ELSE BOT TALKS
				if (agentInChannel) {
					try {
						// LOG IN CONVERSATION BETWEEN CLIENT AND AGENT, BOT IS DEACTIVATED IN RESPONDING
						slackBot.createChatLogAgentPresent(slackBot.getChatNumber(), event);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return;
				} else {
					// THERE IS NO HUMAN AGENT PRESENT IN CHANNEL
					// BOT READS ITS OWN MESSAGES
					// TO PREVENT IT REPLYING TO ITSELF
					// IF SENDER IS A BOT, DON'T REPLY
					// ELSE SENDER IS A HUMAN, REPLY
					if (event.getSender().isBot()) {
						return;
					} else if (event.getMessageContent().contains("has left the channel")
							|| event.getMessageContent().contains("has joined the channel")) {
						return;
					} else if (!event.getSender().isBot()) {
						/*------------------------------------------------------------------*/
						// RETURN NLP ANALYSIS OF MESSAGE
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
							slackBot.postMessage(slackChannel, slackBot.handleMessage(slackBot.getChatNumber(), event),
									tokenSlackBotOAuth);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		System.out.println("\nEVENT LISTENER CREATED");

		/*------------------------------------------------------------------*/
		// ADD LISTENER TO SESSION
		/*------------------------------------------------------------------*/
		System.out.println("\n------------------------------------------------------------------");
		System.out.println("ADDING LISTENER TO SESSION");
		System.out.println("------------------------------------------------------------------");
		session.addMessagePostedListener(messagePostedListener);

		// *-----------------------------------------------------------------*/
		/*---------------------------END OF MAIN----------------------------*/
		/*------------------------------------------------------------------*/
	}
}
