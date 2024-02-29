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
import java.util.concurrent.TimeUnit;

public class TPCApp extends Application {
    private HashMap<String, String> dbNewAdd = new HashMap<>();
    private HashMap<String,String> dbStorage = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(buttonsMain(),0,1);
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button stop = new Button("Stop");
        stop.setPrefWidth(180);
        stop.setPrefHeight(50);
        Text mainMessage = new Text("Select One Of The Buttons Below");
        mainMessage.setFont(new Font(20));
        mainMessage.setTextAlignment(TextAlignment.CENTER);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(mainMessage);
        gridPane.add(hBox,0,0);
        vBox.getChildren().add(stop);
        vBox.setAlignment(Pos.CENTER);
        gridPane.setVgap(40);
        gridPane.add(vBox,0,2);
       //stage.setResizable(false);
        stage.setTitle("Class Scheduler");
        stage.getIcons().add(new Image("file:icon.png"));
        Scene scene = new Scene(gridPane,640,480);
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


    private HBox buttonsMain(){
        HBox hBox = new HBox();
        Button add = new Button("Add Class");
        Button rem = new Button("Remove Class");
        Button dis = new Button("Display Class Schedule");
        Insets in = new Insets(15);
        add.setPadding(in);
        add.setPrefWidth(150);
        rem.setPrefWidth(150);
        rem.setPadding(in);
        dis.setPadding(in);
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

    private DatePicker date = new DatePicker();
    private TextField roomNum = new TextField();
    private TextField className = new TextField();
    private ChoiceBox<String> classTimes = new ChoiceBox<>();
    private TextField moduleName = new TextField();
    private TextField moduleCode = new TextField();

    private void addClass() throws IOException {
        dataBase();
        VBox vBox = new VBox();
        vBox.setSpacing(25);
        Stage stage = new Stage();
        GridPane grid = new GridPane();
        moduleName.setPromptText("Enter Module Name: ");
        moduleCode.setPromptText("Enter Module Code: ");
        date.setPromptText("Select Date:");
        moduleName.setMaxWidth(245);
        moduleCode.setMaxWidth(245);
        date.setMaxWidth(245);
        roomNum.setMaxWidth(245);
        className.setMaxWidth(245);
        roomNum.setPromptText("Enter A Room Number: ");
        className.setPromptText("Enter A Class Name And Year: LM051-2022");
        date.setEditable(false);
        classTimes.setValue("Class Time");
        classTimes.setMaxWidth(245);
        if(classTimes.getItems().isEmpty()) for(int i = 9 ; i<18; i++){
            classTimes.getItems().add(i+":00");
        }
        vBox.setPrefWidth(640);
        vBox.getChildren().add(menuBar());
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
        vBox.getChildren().addAll(classTimes, date,moduleName,moduleCode, roomNum,className,hBox);
        show.setOnAction(actionEvent -> {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            if (className.getText().isEmpty()) {
                alert.setContentText("Please enter a class name!");
                alert.show();
            } else if (roomNum.getText().isEmpty()) {
                alert.setContentText("Please enter a room number!");
                alert.show();
            } else if (date.getValue().equals(null)) {
                alert.setContentText("Please select a date!");
                alert.show();
            } else if (classTimes.getValue().equals("Class Time")) {
                alert.setContentText("Please select a class time!");
                alert.show();
            } else if (moduleCode.getText().isEmpty()) {
                alert.setContentText("Please enter a module name!");
                alert.show();
            } else if (moduleName.getText().isEmpty()) {
                alert.setContentText("Please enter a module code!");
                alert.show();
            }
            if (!className.getText().isEmpty() && !roomNum.getText().isEmpty() && !date.getValue().equals(null) && !classTimes.getValue().equals("Class Time") && !moduleName.getText().isEmpty()&& !moduleCode.getText().isEmpty()){
                String inputStore = "";
                alert = new Alert(Alert.AlertType.WARNING);
                inputStore += moduleName.getText() + "_" + moduleCode.getText() + "_" + classTimes.getValue() + "_" + date.getValue() + "_" + roomNum.getText();
                if(dbStorage.toString().contains(className.getText())){
                    if(dbStorage.get(className.getText()).contains(date.getValue().toString()) && dbStorage.get(className.getText()).contains(classTimes.getValue()) ) {
                        alert.setContentText("The time selected is already taken on this date "+ date.getValue().toString()); alert.show();
                    }else {dbStorage.put(className.getText(), dbStorage.get(className.getText()) + ";" + inputStore); dbNewAdd.put(className.getText(),inputStore);insertDB();}
                }else {dbStorage.put(className.getText(),inputStore); dbNewAdd.put(className.getText(),inputStore);insertDB();}
            }
        });
        Stop.setOnAction(actionEvent -> {
            clear();
            stage.close();
        });
        grid.add(vBox,0,0);
        stage.setTitle("Add Class To Schedule");
        stage.getIcons().add(new Image("file:/icon.png"));
        Scene scene = new Scene(grid,640,480);
        stage.setScene(scene);
        stage.show();
    }
    private void insertDB(){
        try {
            Connection dbconnect = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/data",
                    "root",
                    "@Root_4076-"
            );
            Statement dbstatement = dbconnect.createStatement();
            String inputToDb = dbNewAdd.toString().replace("{","").replace("}","").replace("=","_");
            String[] data = inputToDb.split(", ");
            String dataString;
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
                            dbstatement.executeUpdate("INSERT INTO CLASSES VALUES ("+"'"+sclass[0]+"',"+"'"+sclass[1]+"',"+"'"+sclass[2]+"',"+"'"+sclass[3]+"',"+"'"+sclass[4]+"',"+"'"+sclass[5]+"'"+")");
                            j++;
                        }
                    }else dbstatement.executeUpdate("INSERT INTO CLASSES " + "VALUES ("+"'"+c[0]+"',"+"'"+c[1]+"',"+"'"+c[2]+"',"+"'"+c[3]+"',"+"'"+c[4]+"',"+"'"+c[5]+"'"+")");
                i++;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void clear(){
        roomNum.clear();
        className.clear();
        date.setValue(null);
        classTimes.setValue("Class Time");
        moduleName.clear();
        moduleCode.clear();
    }
    private void dataBase(){
        try {
            Connection dbconnection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/data",
                    "root",
                    "@Root_4076-"
            );
            Statement dbstatement = dbconnection.createStatement();
            ResultSet dbResultSet = dbstatement.executeQuery("SELECT * FROM CLASSES");
            while(dbResultSet.next()){
                String dbReader = dbResultSet.getString("modulename")+"_"+dbResultSet.getString("modulecode")+"_"+dbResultSet.getString("time")+"_"+dbResultSet.getString("date")+"_"+dbResultSet.getString("roomnumber");
                if(dbStorage.containsKey(dbResultSet.getString("classyear"))){
                    String tmp = dbStorage.get(dbResultSet.getString("classyear"));
                    dbStorage.put(dbResultSet.getString("classyear"),tmp+";"+dbReader);
                }else dbStorage.put(dbResultSet.getString("classyear"),dbReader);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private MenuBar menuBar(){
        MenuBar menu = new MenuBar();
        Menu options = new Menu("Options");
        MenuItem clear = new MenuItem("Clear");
        MenuItem checkCon = new MenuItem("Check Connection");
        options.getItems().add(checkCon);
        options.getItems().add(clear);
        clear.setOnAction(actionevent -> {
            clear();
        });
        menu.getMenus().add(options);
        return menu;
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
        vBox.getChildren().add(menuBar());
        vBox.getChildren().add(textField);
        vBox.getChildren().add(hBox);
        return vBox;
    }

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private static final int port = 1234;

    private static InetAddress host;

    private static String m = "";

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