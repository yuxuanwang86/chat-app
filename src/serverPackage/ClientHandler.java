package serverPackage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Yuxuan Wang and Jian Sun
 */

/**
 * This class implements the interface Runnable for interacting with server. An
 * instance of the class is a thread in server. The server can manage multiple
 * thread with this class.
 */
public class ClientHandler implements Runnable {
    /**
     * The socket which is handled by current thread.
     */
    private Socket socket;
    /**
     * Nickname of current client.
     */
    private String nickname;
    /**
     * When server receives message exit, client quits and this socket is closed.
     */
    private static final String END_MARK = "exit";

    /**
     * Constructor which initialize member variable socket.
     *
     * @param socket the socket which is handled by current thread.
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Constructor used for create a new object ClientHandler with only nickname.
     * This is used for checking if nickname is already used.
     *
     * @param nickname nickname of client
     */
    public ClientHandler(String nickname) {
        this.nickname = nickname;
    }

    /**
     * This is for checking if nickname is already used.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
        return result;
    }

    /**
     * This is for checking if nickname is already used.
     *
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientHandler other = (ClientHandler) obj;
        if (nickname == null) {
            return other.nickname == null;
        } else return nickname.equals(other.nickname);
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        PrintWriter pw = null;
        try {
            /*
             * Initializing OutputStream so that server can send message to client.
             */
            OutputStream out = socket.getOutputStream();
            /*
             * Converting byte output stream to character output stream using UTF-8 and so
             * can send it to server.
             */
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            /*
             * Wrapping character stream to buffered stream so can sed message line by line.
             */
            pw = new PrintWriter(osw, true);
            /*
             * Initializing InputStream so that server can receive message from client.
             */
            InputStream in = socket.getInputStream();
            /*
             * Converting byte input stream to character input stream using UTF-8;
             */
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            /*
             * Converting character input stream to buffered stream so that we can read
             * string line by line.
             */
            BufferedReader br = new BufferedReader(isr);
            /*
             * This part aims to pick user's nickname.
             * Receive the nickname of client and check if it exists already.
             * When the nickname is legal, client receives the message below. If not, user
             * should keep entering his nickname until it fits. If it doesn't fit, message
             * should be like: Nickname already exists! Please input a new one: or Nickname
             * cannot be empty! Please input a new one: .
             */
            pw.println("Please input your nickname: ");
            while (true) {
                nickname = br.readLine();
                /*
                Close this socket when client unexpectedly quits during input of nickname.
                 */
                if (nickname == null) {
                    try {
                        pw.close();
                        socket.close();
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Server.isInMap(nickname)) {
                    pw.println("Nickname already exists! Please input a new one: ");
                    continue;
                }
                if (nickname.trim().length() == 0) {
                    pw.println("Nickname cannot be empty! Please input a new one: ");
                    continue;
                }
                break;
            }
            /*
             * Add the pair of current object ClientHandler and PrintWriter to the map.
             */
            Server.addIntoMap(this, pw);
            /*
             * Notify all clients a new user.
             */
            Server.sendMessage(nickname + " just entered the chat room.");
            pw.println("-----------------------------------------");
            /*
             * Reading every message from clients.
             */
            String msg = null;
            while ((msg = br.readLine()) != null) {
                /*
                 * If received message is "exit", quitting the loop and finishing the conversation of
                 * current client. If not, send the message to all clients.
                 */
                if (END_MARK.equals(msg)) {
                    break;
                } else {
                    Server.sendMessage("\t" + nickname + " said: " + msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*
             * Current client left the conversation. Remove it from the map and notify the
             * others.
             */
            if (Server.removeFromMap(this, pw))
                Server.sendMessage(nickname + " just left the chat room.");
            /*
             * When current client disconnect in client side, it should also disconnect
             * in server side.
             */
            try {
                pw.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
