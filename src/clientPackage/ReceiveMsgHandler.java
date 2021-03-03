package clientPackage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 
 * @author Yuxuan Wang and Jian Sun
 *
 */

/**
 * 
 * This thread aims to continuously receive messages from server and print it to
 * console.
 *
 */
class ReceiveMsgHandler implements Runnable {
	/**
	 * Same socket in class Client.
	 */
	private Socket socket;

	/**
	 * Constructor used to initialize a ReiceiveMsgHandler
	 * 
	 * @param socket socket of client.
	 */
	public ReceiveMsgHandler(Socket socket) {
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
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			/*
			 * Getting InputStream through socket.
			 */
			in = socket.getInputStream();
			/*
			 * Converting byte input stream to character input stream using UTF-8;
			 */
			isr = new InputStreamReader(in, "UTF-8");
			/*
			 * Wrapping character input stream to buffered stream.
			 */
			br = new BufferedReader(isr);
			String message = null;
			/*
			 * Continuously reading messages from server.
			 */
			while ((message = br.readLine()) != null) {
				/*
				 * Print the message sent from server to console.
				 */
				System.out.println(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * Print a message when user leaves or server is offline.
			 */
			System.out.println("Connection lost. Please press Enter to end the application.");
			try {
				br.close();
				isr.close();
				in.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}