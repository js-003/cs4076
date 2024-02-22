package group1.cs4076_project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class TPCApp extends Application {
    private TextField textbox;
    Label l = new Label("Response From Server Will Display Here");

    @Override
    public void start(Stage stage) throws IOException {
        GridPane G = new GridPane();
        Tab tabAdd = new Tab("Add Class");
        Tab tabRemove = new Tab("Remove Class");
        Tab tabDisplay = new Tab("Display Schedule");
        TabPane tabs = new TabPane();
        tabs.getTabs().add(tabAdd);
        tabs.getTabs().add(tabRemove);
        tabs.getTabs().add(tabDisplay);
        G.add(tabs,0,0);
        Scene scene = new Scene(G, 480, 300);
        stage.setTitle("Texting App");
        stage.setScene(scene);
        stage.show();
        try {
            host = InetAddress.getLocalHost();
        } catch (Exception e) {
            System.out.print("NO");
        }
        System.out.print("Starting Connectiopn");
       // startConnection();
    }

    public TextField text(String text) {
        textbox = new TextField();
        textbox.setPromptText(text);
        textbox.setFocusTraversable(false);
        textbox.setPrefWidth(50);
        return textbox;
    }


    public Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private static final int port = 1234;

    private static InetAddress host;

    public static String m = "";

/*    public void startConnection() throws IOException {
        try {
            clientSocket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            l.setText(m);
            out.print(m);
            String r = in.readLine();
        }catch(IOException e)
        {
            e.printStackTrace();
        }

    }


    public void stopConnection() throws IOException {
        clientSocket.close();
    }*/

    public static void main(String[] args) throws IOException {
            launch();
    }
}

