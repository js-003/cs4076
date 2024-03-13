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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TPCApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(buttonsMain(),0,1);
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button stop = new Button("Stop");
        stop.setOnAction(actionEvent -> {
            try {
                out.println("STOP");
                clientStop();
                stage.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
        host = InetAddress.getLocalHost();
        startConnection();
    }

    private HBox buttonsMain() {
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
        add.setOnAction(actionEvent -> addClass());
        rem.setOnAction(actionEvent -> {
                try {
                    Remove();
                }catch (IOException e) {
                    throw new RuntimeException(e);
                     }
        });
        dis.setOnAction(actionEvent -> display());
        hBox.setSpacing(40);
        return hBox;
    }

    private DatePicker date = new DatePicker();
    private TextField roomNum = new TextField();
    private TextField className = new TextField();
    private ChoiceBox<String> classTimes = new ChoiceBox<>();
    private TextField moduleName = new TextField();
    private TextField moduleCode = new TextField();
    private ChoiceBox<String> classType = new ChoiceBox<>();

    private void addClass(){
        VBox vBox = new VBox();
        classType.setValue("Class Type");
        if(classType.getItems().isEmpty()) {
            classType.getItems().addAll("Lecture", "Lab", "Tutorial");
        }
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
        classType.setMaxWidth(245);
        roomNum.setPromptText("Enter A Room Number: ");
        className.setPromptText("Enter A Class Name And Year: LM051-2022");
        date.setEditable(false);
        classTimes.setValue("Class Time");
        classTimes.setMaxWidth(245);
        if(classTimes.getItems().isEmpty()) for(int i = 9 ; i<18; i++){classTimes.getItems().add(i+":00");}
        vBox.setPrefWidth(640);
        vBox.getChildren().add(menuBar());
        HBox hBox = new HBox();
        grid.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(25);
        Button show = new Button("Save");
        Button Stop = new Button("Close");
        show.setPadding(new Insets(10));
        Stop.setPadding(new Insets(10));
        hBox.getChildren().addAll(show,Stop);
        vBox.getChildren().addAll(classType,classTimes, date,moduleName,moduleCode, roomNum,className,hBox);
        show.setOnAction(actionEvent -> {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Check your input");

        if (className.getText().isEmpty()) {
            alert.setContentText("Please enter a class name!");
            alert.showAndWait();
        } else if (roomNum.getText().isEmpty()) {
            alert.setContentText("Please enter a room number!");
            alert.show();
        } else if (date.getValue() == null) {
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
        } else if (classType.getValue().equals("Class Type")) {
            alert.setContentText("Please select a class type!");
            alert.show();
        }else {
            String check = "";
            String formatDate = date.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) ;
            String inputStore = className.getText() + "_" + classType.getValue() + "_" + moduleName.getText() + "_" + moduleCode.getText() + "_" + classTimes.getValue() + "_" + formatDate + "_" + roomNum.getText();
            out.println("ADD_" + inputStore);
            label:
            while(!check.equals("GOOD")|| !check.equals("TAKEN TIME")) {
                try {
                check = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
                switch (check) {
                    case "TAKEN TIME":
                        alert.setContentText("The time selected is already taken on this date " + formatDate);
                        alert.show();
                        break label;
                    case "GOOD":
                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.setContentText("Class slot added to " + className.getText() + "\nType: " + classType.getValue() + "\nModule Name: " + moduleName.getText()
                                + "\nModule Code: " + moduleCode.getText() + "\nTime: " + classTimes.getValue() + "\nDate: " + formatDate);
                        alert.show();
                        break label;
                    case "FIVE":
                        alert.setContentText("This course already has five modules");
                        alert.setAlertType(Alert.AlertType.WARNING);
                        alert.show();
                        break label;
                    case "GOOD SQL" :
                        break label;
                    case "NO SQL" :
                        alert.setHeaderText("Check your SQL connection");
                        alert.setAlertType(Alert.AlertType.ERROR);
                        alert.setContentText("SQL connection not established");
                        alert.showAndWait();
                        while (true) {
                            if (!alert.isShowing()) {
                                System.exit(1);
                                break;
                            }
                        }
                }
            }
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
        stage.getIcons().add(new Image("file:icon.png"));
        stage.setScene(scene);
        stage.show();
}

public void display(){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        Stage stage = new Stage();
        HBox hbox = new HBox();
        VBox vbox = new VBox();
        vbox.setSpacing(25);
        vbox.setPrefWidth(640);
        vbox.getChildren().add(menuBar());
        Button dis = new Button("Display");
        Button close = new Button("Close");
        className.setPromptText("Enter A Name And Year : eg. LM051-2022");
        className.setMaxWidth(245);
        moduleCode.setPromptText("Enter A Module Code: ");
        moduleCode.setMaxWidth(245);
        classTimes.setMaxWidth(245);
        classType.setMaxWidth(245);
        className.setMinWidth(240);
        classTimes.setValue("Class Times");
        if(classTimes.getItems().isEmpty()) for(int i = 9 ; i<18; i++){classTimes.getItems().add(i+":00");}
        classType.setValue("Class Type");
        classType.getItems().addAll("Lab","Lecture","Tutorial");
        dis.setPadding(new Insets(10));
        close.setPadding(new Insets(10));
        hbox.getChildren().addAll(dis,close);
        hbox.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(25);;
        vbox.getChildren().addAll(className,hbox);
        dis.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Check your input");
            if(className.getText().isEmpty()){
                alert.setContentText("Please enter a class name and year! eg. LM051-2022");
                alert.show();
            }else {
                out.println("DISPLAY_" + className.getText());
                String check = "";
                label:
                while (true) {
                    try {
                        check = in.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    switch (check) {
                        case "INVALID CLASS NAME":
                            alert.setContentText("No class exists with the name and year: " + className.getText());
                            alert.show();
                            break label;
                        case "GOOD":
                            alert.setHeaderText("Completed");
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setContentText("Class time table displayed");
                            alert.show();
                            break label;
                        case "GOOD SQL":
                            check = "";
                        case "NO SQL":
                            alert.setAlertType(Alert.AlertType.ERROR);
                            alert.setHeaderText("Check your SQL connection");
                            alert.setContentText("SQL connection not established");
                            alert.showAndWait();
                            while (true) {
                                if (!alert.isShowing()) {
                                    System.exit(1);
                                    break;
                                }
                            }
                    }
                }
            }
        });
        grid.add(vbox,0,0);
        Scene sc = new Scene(grid,640,480);
        stage.setScene(sc);
        stage.show();
}



    private void Remove() throws IOException {
        //dataBase();
        VBox vBox = new VBox();
        vBox.setSpacing(25);
        Stage stage = new Stage();
        GridPane grid = new GridPane();
        className.setPromptText("Enter A Class Name And Year: LM051-2022");
        className.setMaxWidth(245);
        date.setPromptText("Select Date:");
        date.setMaxWidth(245);
        date.setEditable(false);
        classTimes.setValue("Class Time");
        classTimes.setMaxWidth(245);    //fills in the choicebox options
        for(int i=9;i<18;i++){
            classTimes.getItems().add(i+ ":00");
        }
        vBox.setPrefWidth(640);
        vBox.getChildren().add(menuBar());
        HBox hBox = new HBox();
        grid.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(25);
        Button remove = new Button("Remove");
        Button stop = new Button("Stop");
        remove.setPadding(new Insets(10));
        stop.setPadding(new Insets(10));
        hBox.getChildren().addAll(remove,stop);                 //adding these horizontally
        vBox.getChildren().addAll(className,date,classTimes);   //adding these nodes vertically
        try{
        remove.setOnAction(actionEvent ->  {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(className.getText().isEmpty()) {
                alert.setContentText("Please enter a class name!");
                alert.show();
            } if (date.getValue()==null) { //ask about this
                alert.setContentText("Please select a date!");
                alert.show();
            }if (classTimes.getValue().equals("Class Time")) {
                alert.setContentText("Please select a class time!");
                alert.show();
            }else {

                String check = "";
                String formatDate = date.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) ;
                String inputStore = className.getText() + "_" + classTimes.getValue() + "_" + formatDate;
                out.println("REMOVE_" + inputStore);
                while (true) {
                    try {
                        check = in.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (check.equals("INVALID TIME")) {
                        alert.setContentText("The time selected is empty on this date " + date.getValue().toString());
                        alert.showAndWait();
                        break;
                    } else if (check.equals("INVALID CLASS NAME")) {
                        alert.setContentText("The time selected is empty on this date " + date.getValue().toString());
                        alert.showAndWait();
                        break;
                    }else if(check.equals("GOOD")){
                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.setContentText("Class Name: "+className.getText()+"\nDate: "+date.getValue()+"\nTime: "+classTimes.getValue());
                        alert.showAndWait();
                        break;
                    }
                }
            }
        });
            stop.setOnAction(actionEvent -> {
                clear();
                stage.close();
            });
            grid.add(vBox, 0, 0);
            grid.add(hBox,0,2);
            grid.setVgap(20);
            stage.setTitle("Remove a Class From Schedule");
            stage.getIcons().add(new Image("file:/icon.png"));
            Scene scene = new Scene(grid, 640, 480);
            stage.setScene(scene);
            stage.show();
        }catch(NullPointerException n){
            System.out.println("cannot be null");
        }



    }

    private void clear(){
        classType.setValue("Class Type");
        roomNum.clear();
        className.clear();
        date.setValue(null);
        classTimes.setValue("Class Time");
        moduleName.clear();
        moduleCode.clear();
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
        checkCon.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Connection established");
            if(clientSocket.isConnected()) {
                alert.setContentText("Connected to server!");
            }else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText("Connection Error");
                alert.setContentText("Not connected to server!");
            }
            alert.show();
        });
        menu.getMenus().add(options);
        return menu;
    }

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private static final int port = 1234;

    private static InetAddress host;

    public void startConnection() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Connection established");
        try {
            clientSocket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println("Connecting");
            String x = "";
            try{
                x = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (x.equals("NO SQL")) {
                alert.setHeaderText("Check your SQL Connection");
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("SQL connection not established");
                alert.showAndWait();
                while (true) {
                    if (!alert.isShowing()) {
                        System.exit(1);
                        break;
                    }
                }
            }else {
                x = "";
                alert.setContentText("You have connected successfully");
                alert.show();

            }
        }catch(IOException e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("Check your connection");
            alert.setContentText("You could not connect try again!");
            alert.showAndWait();
            while(true){
                if(!alert.isShowing()){
                    System.exit(1);
                    break;
                }
            }
        }
    }

    public void clientStop() throws IOException {
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
            launch();
    }
}