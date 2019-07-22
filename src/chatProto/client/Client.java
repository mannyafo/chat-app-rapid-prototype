package chatProto.client;

import java.net.Socket;
import java.util.LinkedList;

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

    @Override
    public void run() {

    }

    public String getUserName() {
        return userName;
    }
}
