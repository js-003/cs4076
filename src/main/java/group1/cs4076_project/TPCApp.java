package group1.cs4076_project;

import javafx.application.Application;
import java.sql.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class TPCApp extends Application {
    private HashMap<String, String> List = new HashMap<>();
    private HashMap<String,String> hashMap = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        GridPane G = new GridPane();
        G.setAlignment(Pos.CENTER);
        G.add(buttonsMain(),0,1);
        VBox vBox = new VBox();
        HBox h = new HBox();
        Button stop = new Button("Stop");
        stop.setPrefWidth(180);
        stop.setPrefHeight(50);
        Text m = new Text("Select One Of The Buttons Below");
        m.setFont(new Font(20));
        m.setTextAlignment(TextAlignment.CENTER);
        h.setAlignment(Pos.CENTER);
        h.getChildren().add(m);
        G.add(h,0,0);
        vBox.getChildren().add(stop);
        vBox.setAlignment(Pos.CENTER);
        G.setVgap(40);
        G.add(vBox,0,2);
        stage.setResizable(false);
        stage.setTitle("Class Scheduler");
        stage.getIcons().add(new Image("file:icon.png"));
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
        add.setOnAction(actionEvent -> {
            try {
                addClass();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        hBox.setSpacing(40);
        return hBox;
    }

    DatePicker dp = new DatePicker();
    TextField rn = new TextField();
    TextField cn = new TextField();
    ChoiceBox<String> cb = new ChoiceBox<>();
    TextField mn = new TextField();
    TextField mc = new TextField();
    public void addClass() throws IOException {

        VBox vBox = new VBox();
        vBox.setSpacing(25);
        Stage s = new Stage();
        GridPane grid = new GridPane();
        mn.setPromptText("Enter Module Name: ");
        mc.setPromptText("Enter Module Code: ");
        dp.setPromptText("Select Date:");
        mn.setMaxWidth(245);
        mc.setMaxWidth(245);
        dp.setMaxWidth(245);
        rn.setMaxWidth(245);
        cn.setMaxWidth(245);
        rn.setPromptText("Enter A Room Number: ");
        cn.setPromptText("Enter A Class Name And Year: LM051-2022");
        dp.setEditable(false);
        cb.setValue("Class Time");
        cb.setMaxWidth(245);
        if(cb.getItems().isEmpty()) for(int i = 9 ; i<18; i++){
            cb.getItems().add(i+":00");
        }
        try{
            dataBase();
        }catch (Exception e){
            System.out.println("ERROR");
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
        vBox.getChildren().addAll(cb,dp,mn,mc,rn,cn,hBox);
        show.setOnAction(actionEvent -> {
            String x = "";
            Alert a1 = new Alert(Alert.AlertType.WARNING);
            while(true) {
                if (cn.getText().isEmpty()) {
                    a1.setContentText("Please enter a class name!");
                    a1.show();
                } else if (cn.getText().isEmpty()) {
                    a1.setContentText("Please enter a room number!");
                    a1.show();
                } else if (dp.getValue().equals(null)) {
                    a1.setContentText("Please select a date!");
                    a1.show();
                } else if (cb.getValue().equals("Class Time")) {
                    a1.setContentText("Please select a class time!");
                    a1.show();
                }else break;
            }
                x += mn.getText()+"_"+mc.getText()+"_"+cb.getValue()+ "_" + dp.getValue() + "_" +rn.getText();
                if(hashMap.toString().contains(cn.getText())){
                    if(hashMap.get(cn.getText()).contains(dp.getValue().toString()) && hashMap.get(cn.getText()).contains(cb.getValue()) ) {
                        a1.setContentText("The time selected is already taken on this date "+dp.getValue().toString()); a1.show();
                    }else hashMap.put(cn.getText(), hashMap.get(cn.getText()) + ";" + x); List.put(cn.getText(), List.get(cn.getText()) + ";" + x);
            }else hashMap.put(cn.getText(),x); List.put(cn.getText(),x);
            insertDB();
        });
        Stop.setOnAction(actionEvent -> {
            clear();
            s.close();
        });
        grid.add(vBox,0,0);
        s.setTitle("Add Class To Schedule");
        s.getIcons().add(new Image("file:/icon.png"));
        Scene scene = new Scene(grid,640,480);
        s.setScene(scene);
        s.show();
    }
    public void insertDB(){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/data",
                    "root",
                    "@Root_4076-"
            );
            Statement statement = connection.createStatement();
            String g = List.toString().replace("{","").replace("}","").replace("=","_");
            String[] data = g.split(", ");
            String dataString = "";
            int i = 0;
            while(i< data.length){
                String[] c = data[i].split("_");
                dataString = data[i].replaceAll("_",",").replaceAll("/","-");
                String[] Sclass ;
                if(dataString.contains(";")){
                    int j = 0;
                        Sclass = dataString.split(";");
                        while(j<Sclass.length-1){
                            String[] sclass = Arrays.toString(Sclass).replace("[","").replace("]","").split(",");
                            statement.executeUpdate("INSERT INTO CLASSES VALUES ("+"'"+sclass[0]+"',"+"'"+sclass[1]+"',"+"'"+sclass[2]+"',"+"'"+sclass[3]+"',"+"'"+sclass[4]+"',"+"'"+sclass[5]+"'"+")");
                            j++;
                        }
                    }else statement.executeUpdate("INSERT INTO CLASSES " + "VALUES ("+"'"+c[0]+"',"+"'"+c[1]+"',"+"'"+c[2]+"',"+"'"+c[3]+"',"+"'"+c[4]+"',"+"'"+c[5]+"'"+")");
                i++;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void clear(){
        rn.clear();
        cn.clear();
        dp.setValue(null);
        cb.setValue("Class Time");
        mn.clear();
        mc.clear();
    }
    public void dataBase(){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/data",
                    "root",
                    "@Root_4076-"
            );
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM CLASSES");
            while(resultSet.next()){
                String x = resultSet.getString("modulename")+"_"+resultSet.getString("modulecode")+"_"+resultSet.getString("time")+"_"+resultSet.getString("date")+"_"+resultSet.getString("roomnumber");
                if(hashMap.containsKey(resultSet.getString("classyear"))){
                    String tmp = hashMap.get(resultSet.getString("classyear"));
                    hashMap.put(resultSet.getString("classyear"),tmp+";"+x);
                }else hashMap.put(resultSet.getString("classyear"),x);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public MenuBar m(){
        MenuBar mb = new MenuBar();
        Menu options = new Menu("Options");
        MenuItem clear = new MenuItem("Clear");
        MenuItem checkCon = new MenuItem("Check Connection");
        options.getItems().add(checkCon);
        options.getItems().add(clear);
        clear.setOnAction(actionevent -> {
            clear();
        });
        mb.getMenus().add(options);
        return mb;
    }
    private VBox DisplayTab() {
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