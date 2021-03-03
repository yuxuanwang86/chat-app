package clientPackage;

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
 * This thread aims to send message to the server.
 *
 */
class SendMsgHandler implements Runnable {
	/**
	 * Scanner used to read input from user.
	 */
	private Scanner scanner;
	/**
	 * Same socket in class Client.
	 */
	private Socket socket;

	/**
	 * Constructor used to initialize a SendMsgHandler
	 * 
	 * @param socket socket of client.
	 */
	public SendMsgHandler(Socket socket) {
		super();
		this.socket = socket;
	}

	/**
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		/*
		 * All variables declared here because it can also be used in finally.
		 */
		OutputStream out = null;
		OutputStreamWriter osw = null;
		PrintWriter pw = null;
		try {
			/*
			 * By using getOutputStream of the socket to get an OutputStream to send message
			 * to server.
			 */
			out = socket.getOutputStream();
			/*
			 * Converting byte stream to character stream using UTF-8 and so can send it to
			 * server.
			 */
			osw = new OutputStreamWriter(out, "UTF-8");
			/*
			 * Wrapping character stream to buffered stream so can send message line by
			 * line.
			 */
			pw = new PrintWriter(osw, true);
			/*
			 * Creating a scanner to accept user's input.
			 */
			scanner = new Scanner(System.in);
			String msg = null;
			while (!socket.isClosed()) {
				msg = scanner.nextLine();
				pw.println(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pw.close();
				osw.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}