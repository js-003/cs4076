package group1.cs4076_project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class TPCApp extends Application {
    private ArrayList<String> List = new ArrayList<>();
    private HashMap<String,String> hashMap = new HashMap<>();
    private TextField textbox;

    @Override
    public void start(Stage stage) throws IOException {
        GridPane G = new GridPane();
        G.setAlignment(Pos.CENTER);
        G.add(buttonsMain(),0,1);
        stage.setResizable(false);
        stage.setTitle("Class Scheduler");
        Scene scene = new Scene(G,640,480);
        stage.setScene(scene);
        stage.show();
        try {
            System.out.println("Starting Connection...");
            host = InetAddress.getLocalHost();
        } catch (Exception e) {
            System.out.println("NO");
        }
        System.out.println("Connected");
       // startConnection();
    }


    public HBox buttonsMain(){
        HBox hBox = new HBox();
        Button add = new Button("Add Class");
        Button rem = new Button("Remove Class");
        Button dis = new Button("Display Class Schedule");
        Insets n = new Insets(15);
        add.setPadding(n);
        add.setPrefWidth(150);
        rem.setPrefWidth(150);
        rem.setPadding(n);
        dis.setPadding(n);
        hBox.getChildren().add(add);
        hBox.getChildren().add(rem);
        hBox.getChildren().add(dis);
        add.setOnAction(actionEvent ->  addClass());
        hBox.setSpacing(40);
        return hBox;
    }
    DatePicker dp = new DatePicker();
    TextField text = new TextField();
    TextField cText = new TextField();
    ChoiceBox<String> cb = new ChoiceBox<>();
    public void addClass(){
        VBox vBox = new VBox();
        vBox.setSpacing(25);
        Stage s = new Stage();
        GridPane grid = new GridPane();
        dp.setPromptText("Select Date:");
        dp.setMaxWidth(245);
        text.setMaxWidth(245);
        cText.setMaxWidth(245);
        text.setPromptText("Enter A Room Number: ");
        cText.setPromptText("Enter A Class Name And Year: LM051-2022");
        dp.setEditable(false);
        cb.setValue("Class Time");
        cb.setMaxWidth(245);
        for(int i = 9 ; i<18; i++){
            cb.getItems().add(i+":00");
        }
        vBox.setPrefWidth(640);
        vBox.getChildren().add(m());
        HBox hBox = new HBox();
        grid.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(25);
        Button show = new Button("Save");
        Button Stop = new Button("Stop");
        show.setPadding(new Insets(10));
        Stop.setPadding(new Insets(10));
        hBox.getChildren().addAll(show,Stop);
        vBox.getChildren().addAll(cb,dp,text,cText,hBox);
        show.setOnAction(actionEvent -> {
            String x = "";
            Alert a1 = new Alert(Alert.AlertType.ERROR);
                if (cText.getText().isEmpty()) {
                    a1.setContentText("Please enter a class name!");
                    a1.show();
                } else if (text.getText().isEmpty()) {
                    a1.setContentText("Please enter a room number!");
                    a1.show();
                } else if (dp.getValue().equals(null)){
                    a1.setContentText("Please select a date");
                    a1.show();
                } else if(cb.getValue().equals("Class Time")){
                    a1.setContentText("Please select a class time");
                    a1.show();
                }
                    x += cText.getText() + " " + text.getText() + " " + dp.getValue() + " " + cb.getValue();
                    List.add(x);
            String g = cText.toString();
            hashMap.put(cText.toString(),x);
            for(int i = 0; i<hashMap.size();i++){
                        if(g.contains(cText.toString())){
                            g = hashMap.get(cText.toString());
                            g += text.getText() + " " + dp.getValue() + " " + cb.getValue();
                            hashMap.put(cText.toString(),g);
                            System.out.println(hashMap.toString());
                        }

                    //System.out.println(List);
                }
        });
        Stop.setOnAction(actionEvent -> Platform.exit());
        grid.add(vBox,0,0);
        s.setTitle("Add Class To Schedule");
        Scene scene = new Scene(grid,640,480);
        s.setScene(scene);
        s.show();
    }


    public MenuBar m(){
        MenuBar mb = new MenuBar();
        Menu options = new Menu("Options");
        MenuItem clear = new MenuItem("Clear");
        options.getItems().add(clear);

        clear.setOnAction(actionevent -> {
            text.clear();
            cText.clear();
            dp.setValue(null);
            cb.setValue("Class Time");
        });
        mb.getMenus().add(options);
        return mb;
    }
    private VBox DisplayTab(){
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        vBox.setSpacing(40);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        Button show = new Button("Show");
        Button Stop = new Button("Stop");

        hBox.getChildren().add(show);
        hBox.getChildren().add(Stop);
        show.setOnAction(actionEvent -> System.out.println("HELLO"));
        Stop.setOnAction(actionEvent -> Platform.exit());
        TextField textField = new TextField();
        textField.setPromptText("Enter Class Name And Year: LM051-2022");
        textField.setMaxWidth(230);
        vBox.getChildren().add(m());
        vBox.getChildren().add(textField);
        vBox.getChildren().add(hBox);
        return vBox;
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

