package chatProto.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server class:
 * - This class initiates the server-side functionality (single instance)
 * - Connection is established with in incoming client thread connection
 *      via the
 * - A session thread is created to handle server interaction for
 *      each client connection
 * - A list of sessions is saved in this server class and messages to
 *      facilitate the distribution of messages to all clients connected
 *      to the server
 */
public class Server {

    // Port Number
    private final int portNumber = 5000;
    private static int port; // Used in constructor

    /*
    * Creating the executor service that will handle the thread pooling
    * - This will handle the pool of session threads created to handle
    *   direct client->server communication
    */
    private ExecutorService executorService = Executors.newCachedThreadPool();

    // Creating list of sessions
    private List<Session> sessions;

    /**
     * Constructor for the Server class
     * @param port is the socket port number
     */
    public Server(int port) {
        this.port = portNumber;
    }

    /**
     * Server main method:
     * - Initiates the server by calling the startup function
     * @param args is the main arguments array (not used)
     */
    public static void main(String[] args) {

        // Start Server
        Server server = new Server(port);
        server.startServer();

    }

    /**
     * Server start function:
     * - Assigns sessions array
     * - Creates Server Socket
     * - Initiates wait for client connections
     * - Calls function to handle client connection acceptance
     */
    public void startServer(){
        sessions = new ArrayList<Session>(); // Set arraylist of sessions
        ServerSocket serverSocket = null; // Create empty server socket

        // Wait and connect with clients
        try {
            serverSocket = new ServerSocket(5000);
            acceptClients(serverSocket);
        }catch( IOException e) {
            System.out.println("Server error - unable to accept connection request(s)");
        }
    }

    /**
     * Accept Clients:
     * - Execute loop to accept incoming client connections and create
     *      session thread to handle further client server communication
     * @param serverSocket the server socket to "listen" for incoming client connections
     */
    private void acceptClients(ServerSocket serverSocket) {

        // Infinite loop - waiting for client connection attempts
        while(true) {
            try {
                /*
                    The server socket waits for client connection attempts and accepts them
                    - The function returns a new socket allowing communication with the client
                 */
                Socket socket = serverSocket.accept();

                // Create session thread for the client feeding the new socket and this server instance
                Session session = new Session(socket, this);

                // Executes new session thread
                executorService.execute(session);

                // Console message and session list addition
                sessions.add(session);
                System.out.println("New Client Connected!, total clients connected: " + sessions.size());

            }catch( IOException e ) {
                System.out.println("Server error - unable to accept connection request(s)");
            }
        }

    }

    /**
     * Getter for the list of session threads
     * @return the list of session threads
     */
    public List<Session> getSessions() {
        return sessions;
    }

}
