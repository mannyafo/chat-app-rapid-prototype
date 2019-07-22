package chatProto.client;

import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;

public class Client implements Runnable{

    // Initialise variables
    private Socket socket;
    private String userName;
    private LinkedList<String> pendingMessagesList;
    private boolean pendingMessageExists = false;

    public Client(Socket socket, String userName) {
        this.socket = socket;
        this.userName = userName;
        pendingMessagesList = new LinkedList<String>();
    }

    /**
     * Function to add a pending message to the pending message list
     * and set pending message flag to true
     * @param message is the message to add to the list
     */
    public void addPendingMessage(String message) {
        synchronized(pendingMessagesList) {
            pendingMessageExists = true;
            pendingMessagesList.push(message);
        }
    }

    @Override
    public void run() {
        // Console message
        System.out.println("Welcome :" + userName);

        try {
            // Create socket input and output streams
            PrintWriter outgoing = new PrintWriter(socket.getOutputStream(), false);
            InputStream incoming = socket.getInputStream();
            Scanner incomingScanner = new Scanner(incoming);

            // Loop to handle incoming and outgoing messages
            while( !socket.isClosed() ) {
                // If there are incoming messages
                if( incoming.available() > 0 ) {
                    if(incomingScanner.hasNextLine()) {
                        // Send the message to the user interface
                        try {
                            Platform.runLater(() ->
                                    UserInterface.addMessageToDisplay(incomingScanner.nextLine())
                            );
                        }catch( IllegalStateException e) {
                            e.printStackTrace();
                        }

                    }
                }

                // If the client has messages to send to the server
                if(pendingMessageExists) {
                    String nextSend = "";
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                    synchronized(pendingMessagesList) {
                        nextSend = pendingMessagesList.pop();
                        pendingMessageExists = !pendingMessagesList.isEmpty();
                    }
                    outgoing.println(timeStamp + " " + userName + " > " + nextSend);
                    outgoing.flush();
                }
            }
        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Getter for the user name of the client
     * @return the username of the client
     */
    public String getUserName() {
        return userName;
    }
}
