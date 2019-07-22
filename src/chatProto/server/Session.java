package chatProto.server;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Session Thread:
 * - Paired with one connected client
 * - Handles incoming and outgoing messages for that client via socket
 * - Provides printwriter to other sessions to allow them to write to the
 *      socket ouputstream directly
 */
public class Session implements Runnable{

    // Initialise socket, server and printwriter
    private Socket socket;
    private Server server; // Reference to Server (spawns new session for each client)
    private PrintWriter sessionOut;

    /**
     * Session thread constructor
     * @param socket is the socket facilitating communication with the client
     * @param server is the main server instance storing the sessions array
     */
    public Session(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {

    }

}
