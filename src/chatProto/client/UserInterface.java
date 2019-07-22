package chatProto.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;

/**
 * Client UI Class:
 * - Initiates client-side functionality
 * - Handles Login and Chat Screens
 *      - Login:
 *      1) Input user name
 *      2) Navigate to chat screen
 *      3) Close app
 *      - Chat:
 *      1) Text field for message entry
 *      2) Send messages
 *      3) Logout (back to login screen)
 * - Coordinates with the client thread to handle incoming and outgoing messages
 */
public class UserInterface extends Application {

    // Creating window and scenes
    Stage window;
    Scene loginScreen, chatScreen;

    // Create list to drive display
    private static ObservableList<String> messages;

    // ClientTest thread
    Client clientThread;
    String uiName = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Assigning primary stage
        window = primaryStage;

        /*
         *  Login Screen:
         *  - Input user name
         *  - Chat button -> navigate to chat
         *  - Close button -> close application
         */
            // Username input label and field
            Label username = new Label("Username");
            TextField nameField = new TextField();

            // Chat button functionality
            Button chatButton = new Button("Chat");
            chatButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {

                    // Start the client
                    try {

                        // Socket for server connection request
                        Socket socket = new Socket("localHost", 5000);

                        // Set the timeout time for attempting to connect to the server
                        socket.setSoTimeout(5000);

                        /*
                        * Start client thread to handle server communication
                        * - Pass socket created above and the username entered by the user
                        */
                        clientThread = new Client(socket, nameField.getText());
                        Thread client = new Thread(clientThread);
                        client.start();

                        // Set the ui name to display in the title of the chat window
                        uiName = clientThread.getUserName();

                    }catch(IOException ex) {
                        System.out.println("IO Error starting client!");
                        ex.printStackTrace();
                    }

                    // Execute scene transition and add username to window title
                    window.setScene(chatScreen);
                    window.setTitle("ChatRoom: " + uiName);

                    // Cleanup
                    nameField.clear();
                }
            });

            // Implement close button
            Button closeButton = new Button("Close App");
            closeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    System.exit(0);
                }
            });

            // Set login layout
            VBox loginLayout = new VBox(15);
            loginLayout.setAlignment(Pos.CENTER);
            loginLayout.getChildren().addAll(username, nameField, chatButton, closeButton);
            loginScreen = new Scene(loginLayout, 300, 300);

        /*
        * Chat Screen
        * - Input messages
        * - Send messages
        * - Logout (to login screen)
        * */
            // Message input box
            TextField messageBox = new TextField();
            messageBox.setPrefWidth(400);

            // Send button functionality
            Button sendButton = new Button("Send");
            sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
                public void handle(ActionEvent e) {

                    clientThread.addPendingMessage(messageBox.getText());
                    messageBox.setText(null);
                }
            });

            // Logout button functionality
            Button logoutButton = new Button("Logout");
            logoutButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    window.setScene(loginScreen);
                    window.setTitle("ChatRoom");

                }
            });

            // Layout for the lower section of the chat gui
            HBox footerLayout = new HBox();
            footerLayout.getChildren().addAll(messageBox, sendButton, logoutButton);
            footerLayout.setSpacing(5);
            footerLayout.setPadding(new Insets(0,0,5,5));

            // Listview displaying messages in the chat gui
            ListView<String> chatList = new ListView<String>();
            messages = FXCollections.observableArrayList();
            chatList.setItems(messages);
            chatList.setPrefWidth(600);
            chatList.setPrefHeight(550);

            // Overall layout of the chat gui
            VBox chatLayout = new VBox(10);
            chatLayout.getChildren().addAll(chatList,footerLayout);
            chatScreen = new Scene(chatLayout, 600, 400);

        /*
        * Window initialisation:
        */
        window.setScene(loginScreen);
        window.setTitle("ChatRoom");
        window.show();
    }

    /**
     * Add a string message to the messages observable arraylist
     * @param message is the message to add to the list
     */
    public static void addMessageToDisplay(String message) {
        messages.add(message);
    }

}
