package com.sbJslack.JSlack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map.Entry;

public class SocketSlackBot {

	// USE ContactCentre FROM SpringGet
	public SocketSlackBot(SlackBot slackBot) throws Exception {
		final String host = "localhost";
		final int portNumber = 81;

		System.out.println("\n------------------------------------------------------------------");
		System.out.println("SLACK SERVER: FOR SENDING DATA");
		System.out.println("------------------------------------------------------------------");

		// CREATE SOCKET
		Socket socket = new Socket(host, portNumber);

		// BUFFERED READER READS SOCKET INPUTSTREAM, I.E. THE DATA COMING IN TO THIS
		// SERVER
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// PRINTWRITER USES SOCKET OUTPUT TO SEND THE DATA OUT TO THE OTHER SERVER
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		// READ RESPONSE AFTER CONNECTION
		System.out.println("server says:" + br.readLine());

		/*------------------------------------------------------------------*/
		// SENDING METRICS TO CONTACT CENTRE
		/*------------------------------------------------------------------*/
		String outputTextToCC = "SLACK BOT SERVER: I WILL NOW SEND OVER THE CHATLOG METRICS";
		// OUT SENDS THE STRING TO THE OTHER SERVER
		out.println(outputTextToCC);

		// GET METRIC CHATLOG, ITERATE OVER CHATLOG
		for (Entry<Integer, ChatLog> entry : slackBot.getMetrics().getMapChatLog().entrySet()) {

			String channelId = entry.getValue().getEvent().getChannel().getId();
			String channelName = entry.getValue().getEvent().getChannel().getName();
			String id = entry.getValue().getEvent().getSender().getId();
			String userName = entry.getValue().getEvent().getSender().getUserName();
			String realName = entry.getValue().getEvent().getSender().getRealName();
			String email = entry.getValue().getEvent().getSender().getUserMail();
			String timeStamp = entry.getValue().getEvent().getTimeStamp();

			out.println("channelId: " + channelId + ", channelName: " + channelName + ", idUser:" + id + ", userName:"
					+ userName + ", realName:" + realName + ", email:" + email + ", timeStamp:" + timeStamp);
			out.println(userName + ": " + entry.getValue().getGoogleNlpMessage().getText());
			try {
				out.println("Bot: " + entry.getValue().getDialogflowAnswer().dialogflowAnswer);
			} catch (Exception e) {}
			out.println("SENTIMENT: " + entry.getValue().getGoogleNlpMessage().getSentiment());

		}

		// COMMAND TO CLOSE THE WHILE LOOP ON OTHER SERVER FOR THE BUFFERED READING
		// REMOVE IF YOU WANT SERVER TO STAY ON
		out.println("FINISH");

		// READS RESPONSE FROM OTHER SERVER
		System.out.println("server says:" + br.readLine());
		br.close();
		socket.close();
	}
}
