package serverPackage;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Yuxuan Wang and Jian Sun
 */

/**
 * This is the class defined for server part of the application.
 */
public class Server {
    /**
     * Port used for server.
     */
    private static final int PORT = 10086;
    /**
     * Socket running on server.
     */
    private ServerSocket server;
    /**
     * A ConcurrentHashMap which is thread-safe for storing pair of object
     * ClientHandler and PrintWriter. Each ClientHandler has a unique PrintWriter.
     * This map can be considered as the array clients in states of questions.
     */
    private static Map<ClientHandler, PrintWriter> clientPrintWriterMap;
    /**
     * A thread pool for managing thread interacting with client side.
     */
    private ExecutorService threadPool;

    /**
     * Constructor of Server class.
     *
     */
    public Server() {
        try {
            /*
             * Initialization of member variables.
             */
            System.out.println("Init server...");
            server = new ServerSocket(PORT);
            clientPrintWriterMap = new ConcurrentHashMap<ClientHandler, PrintWriter>();
            threadPool = Executors.newFixedThreadPool(20);
            System.out.println("Init server done...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for starting server.
     */
    public void start() {
        Socket serverSocket = null;
        try {
            while (true) {
                /*
                 * The method accept of ServerScoket listening on port 10086. Waiting for
                 * connection by client.
                 */
                serverSocket = server.accept();
                /*
                 * When one connection is established, creating a thread clientHandler with
                 * serverScoket, which makes the thread can interact with client. So we can
                 * enter in the next loop for new connection.
                 */
                Runnable clientHandler = new ClientHandler(serverSocket);
                threadPool.execute(clientHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add a pair of clientHandler and PrintWriter given in param into the map.
     *
     * @param clientHandler an object ClientHandler that is the key.
     * @param pw            an object PrintWriter that is the value.
     */
    public static void addIntoMap(ClientHandler clientHandler, PrintWriter pw) {
        clientPrintWriterMap.put(clientHandler, pw);
    }

    /**
     * Send msg given in param to all clients. This method needs to be synchronized.
     *
     * @param msg message to send.
     */
    public static synchronized void sendMessage(String msg) {
        for (Map.Entry<ClientHandler, PrintWriter> entry : clientPrintWriterMap.entrySet()) {
            entry.getValue().println(msg);
        }
    }

    /**
     * Check if the map already contains a pair which has clientHandler as key and
     * has the property nickname given in param. Method used to see if the nickname
     * is already used.
     *
     * @param nickname nickname picked by client.
     * @return availability of the nickname.
     */
    public static boolean isInMap(String nickname) {
        return clientPrintWriterMap.containsKey(new ClientHandler(nickname));
    }

    /**
     * Removes the entry for the specified ClientHandler only if it is currently mapped to the specified PrintWriter.
     * @param clientHandler ClientHandler with which the specified PrintWriter is associated
     * @param pw PrintWriter expected to be associated with the specified ClientHandler
     * @return true if the value was removed
     */
    public static boolean removeFromMap(ClientHandler clientHandler, PrintWriter pw) {
        return clientPrintWriterMap.remove(clientHandler, pw);
    }

    /**
     * Function main.
     *
     * @param args
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("init server failed");
        }
    }

}
