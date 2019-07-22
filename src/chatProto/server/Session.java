package chatProto.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
    private PrintWriter incoming;

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
        try {

            // Write output to socket
            incoming = new PrintWriter(socket.getOutputStream(), false);

            // Get input from socket
            Scanner outgoing = new Scanner(socket.getInputStream());

            // Communicate while the socket is open
            while( !socket.isClosed()) {

                /*
                 * If the client has written to the socket, display the contents
                 * to the other sessions
                 * Other sessions send to each connected client via their
                 * socket output stream
                 */
                if(outgoing.hasNextLine()) {

                    // Get input
                    String input = outgoing.nextLine();

                    // Check each live session
                    for(Session session : server.getSessions()) {

                        // Get the printwriter for that session
                        PrintWriter otherSessionInput = session.getIncoming();

                        /*
                         * Write the input to all other clients via the output
                         * Stream of their session
                         */
                        if( otherSessionInput!= null ) {
                            otherSessionInput.write(input + "\r\n");
                            otherSessionInput.flush();
                        }
                    }
                }
            }
        }catch( IOException e ) {
            System.out.println("chatProto.server.Session error!");
            e.printStackTrace();
        }
    }

    /**
     * Getter for the printwriter (allows writing to client terminal via socket)
     * @return the printwriter for this session to handle incoming messages
     */
    public PrintWriter getIncoming() {
        return incoming;
    }

}
