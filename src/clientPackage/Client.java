package clientPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 
 * @author Yuxuan Wang and Jian Sun
 *
 */

/**
 * 
 * This is the class defined for client part of the application.
 *
 */
public class Client {
	/**
	 * Socket used to connect with the ServerSocket in the server side.
	 */
	private Socket socket;
	/**
	 * Scanner used to read nickname.
	 */
	private Scanner scanner;
	/**
	 * PORT used to connect to server. This is the same as the one in server side.
	 */
	private static final int PORT = 10086;

	/**
	 * Constructor for initializing client.
	 *
	 */
	public Client() {
		try {
			/*
			 * Creating client instance.
			 */
			// System.out.println("connecting to server...");
			socket = new Socket("127.0.0.1", PORT);
			// System.out.println("connection established...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method used to start client.
	 */
	public void start() {
		/*
		 * Start two threads. One of them handles reception of message. The other
		 * handles sending of message.
		 */
		Runnable runReiceiveMsgHandler = new ReceiveMsgHandler(socket);
		Runnable runSendMsgHandler = new SendMsgHandler(socket);
		Thread tReiceiveMsgHandler = new Thread(runReiceiveMsgHandler);
		Thread tSendMsgHandler = new Thread(runSendMsgHandler);
		try {
			tSendMsgHandler.start();
			tReiceiveMsgHandler.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function main.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("init client failed...");
		}
	}

}
