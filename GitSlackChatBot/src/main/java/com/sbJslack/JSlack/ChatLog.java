package com.sbJslack.JSlack;

import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class ChatLog {

	private int messageNumber;
	private GoogleNlpMessage googleNlpMessage;
	private DialogflowAnswer dialogflowAnswer;
	private SlackMessagePosted event;

	// CREATE A CHATLOG BY INPUTTING NLP AND DIALOG FLOW
	// CREATE CHAT NUMBER, EVERY TIME A MESSAGE COMES IN, NEW INT CHAT NUMBER GETS CREATED = messageNumber
	// THEN ADD CHATLOG TO SLACKBOT HASH MEMORY 
	public ChatLog(int messageNumber,SlackMessagePosted event, GoogleNlpMessage googleNlpMessage, DialogflowAnswer dialogflowAnswer) {
		this.messageNumber = messageNumber;
		this.event = event;
		this.googleNlpMessage = googleNlpMessage;
		this.dialogflowAnswer = dialogflowAnswer;
	}

	public GoogleNlpMessage getGoogleNlpMessage() {
		return googleNlpMessage;
	}

	public void setGoogleNlpMessage(GoogleNlpMessage googleNlpMessage) {
		this.googleNlpMessage = googleNlpMessage;
	}

	public SlackMessagePosted getEvent() {
		return event;
	}

	public void setEvent(SlackMessagePosted event) {
		this.event = event;
	}

	public int getMessageNumber() {
		return messageNumber;
	}

	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

	public DialogflowAnswer getDialogflowAnswer() {
		return dialogflowAnswer;
	}

	public void setDialogflowAnswer(DialogflowAnswer dialogflowAnswer) {
		this.dialogflowAnswer = dialogflowAnswer;
	}
	
	
}
