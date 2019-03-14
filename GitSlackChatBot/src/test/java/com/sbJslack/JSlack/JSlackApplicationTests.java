package com.sbJslack.JSlack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JSlackApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	/*------------------------------------------------------------------*/
	// TESTING GOOGLE NLP 
	/*------------------------------------------------------------------*/
	@Test
	public void testGoogleCloudNlp() throws Exception {
		// GOOGLE NLP CREDENTIALS
		String pathTokenGoogleCloudNl = "xxxxxxxxxxxxxxxxxx.json";
		// DIALOG FLOW CREDENTIALS AND PROJECT ID
		String pathTokenDialogflow = "xxxxxxxxxxxxxxxxxxxxx.json";
		String projectId = "xxxxxxxxxxxxxxxxxxxxxxx";
		
		// CREATE SLACKBOT OBJECT
		SlackBot slackBot = new SlackBot("slackBotNuigTestBot10", pathTokenGoogleCloudNl, pathTokenDialogflow, projectId);
		System.out.println("SLACKBOT OBJECT CREATED: " + slackBot.getName());
		
		String testString = "This is a very good test.";
		System.out.println("TEST: " + testString);
		
		// TEST GOOGLE NLP
		float sentiment = slackBot.getGoogleNlp().getGoogleNlpMessage(slackBot.getGoogleNlp(), testString).getSentiment();
		System.out.println("SENTIMENT = " + sentiment);
		
	}
	
	/*------------------------------------------------------------------*/
	// TESTING DIALOGFLOW
	/*------------------------------------------------------------------*/
	@Test
	public void testDialogflow() throws Exception {
		// GOOGLE NLP CREDENTIALS
		String pathTokenGoogleCloudNl = "xxxxxxxxxxxxxxxxxxxxx.json";
		// DIALOG FLOW CREDENTIALS AND PROJECT ID
		String pathTokenDialogflow = "xxxxxxxxxxxxxxxxxxxxxxxx.json";
		String projectId = "xxxxxxxxxxxxxxxxxxxx";
		
		// CREATE SLACKBOT OBJECT
		SlackBot slackBot = new SlackBot("slackBotNuigTestBot10", pathTokenGoogleCloudNl, pathTokenDialogflow, projectId);
		System.out.println("SLACKBOT OBJECT CREATED: " + slackBot.getName());
		
		String testString = "This is a dialogflow test.";
		System.out.println("TEST: " + testString);
		
		// TEST DIALOGFLOW
		ArrayList<String> arraylistText = new ArrayList<>(); 
		arraylistText.add(testString);
		String dialogString = slackBot.getDialogflow().getDialogflowAnswer(arraylistText).getDialogflowAnswer();
		System.out.println(dialogString);
		
	}

}

