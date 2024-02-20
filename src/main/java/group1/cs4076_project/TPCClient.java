package group1.cs4076_project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TPCClient extends Application {
    private TextField textbox;

    @Override
    public void start(Stage stage) throws IOException {
        GridPane G = new GridPane();
        G.setAlignment(Pos.CENTER);
        G.setHgap(10);
        G.setVgap(10);
        G.add(text("Enter a message: "), 0, 0);
        G.add(createInputKeys(), 0, 1);
        Scene scene = new Scene(G, 480, 300);
        stage.setTitle("Client Side");
        stage.setScene(scene);
        stage.show();
    }

    public TextField text(String text) {
        textbox = new TextField();
        textbox.setPromptText(text);
        textbox.setFocusTraversable(false);
        textbox.setPrefWidth(50);
        return textbox;
    }

    public GridPane createInputKeys() {
        GridPane buttons = new GridPane();
        buttons.setPadding(new Insets(10));
        buttons.setHgap(15);
        buttons.setAlignment(Pos.CENTER);
        Button send = createButton("Send");
        Button stop = createButton("Stop");
        buttons.add(send, 0, 1);
        buttons.add(stop, 1, 1);
        return buttons;
    }

    public void buttonPressed(String text) {
        if (text.equals("Send")) {
            message = textbox.getText();
        } else if (!text.equals("Start")) {

        }
    }

    public Button createButton(String buttonName) {
        Button button = new Button(buttonName);
        button.setPrefWidth(75);
        button.setOnAction(event -> buttonPressed(button.getText()));

        return button;
    }

    private static InetAddress host;
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }

        run();
    }

    public static Socket link = null;
    public static String message = null;
    public static String response = null;
    private static void run() {
        launch();
                      //Step 1.
        try {
            link = new Socket(host, PORT);        //Step 1.
            //link = new Socket( "192.168.0.59", PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));//Step 2.
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);     //Step 2.

            //Set up stream for keyboard entry...
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));


            System.out.println("Enter message to be sent to server: ");
            message = userEntry.readLine();
            out.println(message);        //Step 3.
            response = in.readLine();        //Step 3.
            System.out.println("\nSERVER RESPONSE> " + response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("\n* Closing connection... *");
                link.close();                //Step 4.
            } catch (IOException e) {
                System.out.println("Unable to disconnect/close!");
                System.exit(1);
            }
        }
    }
}