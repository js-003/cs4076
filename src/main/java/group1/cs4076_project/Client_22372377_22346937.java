package group1.cs4076_project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Client_22372377_22346937 extends Application {

    private TextField text = new TextField();
    private DatePicker date = new DatePicker();
    private TextField roomNum = new TextField();
    private TextField className = new TextField();
    private ChoiceBox<String> classTimes = new ChoiceBox<>();
    private TextField moduleName = new TextField();
    private TextField moduleCode = new TextField();
    private ChoiceBox<String> classType = new ChoiceBox<>();
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static final int port = 1234;
    private static InetAddress host;

    @Override
    public void start(Stage stage) throws IOException {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(TextboxMain(),0,1);
        HBox vBox = new HBox();
        HBox hBox = new HBox();
        Button stop = new Button("Stop");
        Button ent = new Button("Enter");
        ent.setOnAction(actionEvent ->{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            try {
                if(text.getText().toUpperCase().contains("ADD")||text.getText().toUpperCase().contains("DISPLAY")||text.getText().toUpperCase().contains("REMOVE")||text.getText().toUpperCase().contains("HELP")) {

                    if(text.getText().equalsIgnoreCase("HELP")) {
                        alert.setContentText("To add a class: add\nTo remove a class: remove\nTo display a class timetable: display");
                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Typing Commands Help");
                        alert.setTitle("Help");
                        alert.show();
                        text.clear();
                    }else {
                        switch (text.getText().toUpperCase()){
                            case "ADD" : out.println("CLIENT ADDING CLASS");addClass(); break;
                            case "REMOVE" : out.println("CLIENT REMOVING CLASS");Remove();break;
                            case "DISPLAY" : out.println("CLIENT DISPLAYING CLASS");display();break;
                        }
                        text.clear();
                    }
                } else throw new Server_22372377_22346937.IncorrectActionException("COMMAND ERROR");
            } catch (Server_22372377_22346937.IncorrectActionException e) {
                out.println(e.getMessage());
                alert.setTitle("Incorrect Action");
                alert.setHeaderText(e.getMessage());
                alert.setContentText("Please enter a command, enter 'help'");
                alert.showAndWait();
            }
        });
        stop.setOnAction(actionEvent -> {
            try {
                out.println("STOP");
                clientStop();
                stage.close();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Stopping went wrong");
                alert.setContentText("Stop Error");
                alert.showAndWait();
            }
        });
        stop.setPrefWidth(135);
        stop.setPrefHeight(30);
        Label mainMessage = new Label("Select One Of The Buttons Below");
        mainMessage.setFont(new Font(20));
        mainMessage.setTextAlignment(TextAlignment.CENTER);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(mainMessage);
        gridPane.add(hBox,0,0);
        ent.setPrefWidth(135);
        ent.setPrefHeight(30);
        vBox.getChildren().addAll(ent,stop);
        vBox.setSpacing(25);
        vBox.setAlignment(Pos.CENTER);
        gridPane.setVgap(40);
        gridPane.add(vBox,0,2);
        stage.setResizable(false);
        gridPane.setBackground(new Background(new BackgroundImage(new Image("file:ul.png"),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        stage.setTitle("Class Scheduler");
        stage.getIcons().add(new Image("file:ulico.png"));
        Scene scene = new Scene(gridPane,640,480);
        stage.setScene(scene);
        stage.show();
        host = InetAddress.getLocalHost();
        startConnection();
    }

    private HBox TextboxMain() {
        text.setPromptText("Input Command");
        HBox hBox = new HBox();
        hBox.setSpacing(40);
        hBox.setAlignment(Pos.CENTER);
        text.setPrefWidth(300);
        hBox.getChildren().add(text);
        return hBox;
    }

    private void addClass(){
        VBox vBox = new VBox();
        classType.setValue("Class Type");
        if(classType.getItems().isEmpty()) {
            classType.getItems().addAll("Lecture", "Lab", "Tutorial");
        }
        vBox.setSpacing(0);
        Stage stage = new Stage();
        GridPane grid = new GridPane();
        moduleName.setPromptText("Enter Module Name: ");
        moduleCode.setPromptText("Enter Module Code: ");
        date.setPromptText("Select Date:");
        classType.setMinWidth(245);
        date.setPrefWidth(245);
        classTimes.setMinWidth(245);
        roomNum.setPromptText("Enter A Room Number: ");
        className.setPromptText("Enter A Class Name And Year: LM051-2022");
        date.setEditable(false);
        classTimes.setValue("Class Time");
        if(classTimes.getItems().isEmpty()) for(int i = 9 ; i<18; i++){classTimes.getItems().add(i+":00");}
        vBox.setPrefWidth(640);
        vBox.getChildren().add(menuBar());
        HBox hBox = new HBox();
        grid.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(25);
        Button show = new Button("Add");
        Button Stop = new Button("Close");
        Stop.setPrefWidth(60);
        show.setPrefWidth(60);
        show.setPadding(new Insets(10));
        Stop.setPadding(new Insets(10));
        hBox.getChildren().addAll(show,Stop);
        Node[] f = {new Label("Class Type"),classType,new Label("Class Time"),classTimes,new Label("Class Date"),date,  new Label("Module Name"),moduleName
                ,new Label("Module Code"),moduleCode,new Label("Room Number"),roomNum, new Label("Class Name"),className};
        for(int i =0; i<f.length;i=i+2){
            VBox hBox1 = new VBox(f[i],f[i+1]);
            hBox1.setAlignment(Pos.CENTER_LEFT);
            hBox1.setMaxWidth(245);
            hBox1.setMinWidth(245);
            hBox1.setSpacing(5);
            vBox.getChildren().add(hBox1);
        }
        vBox.setSpacing(10);
        show.setOnAction(actionEvent -> {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
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
                String formatDate = date.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
                String inputStore = className.getText() + "_" + classType.getValue() + "_" + moduleName.getText() + "_" + moduleCode.getText() + "_" + classTimes.getValue() + "_" + formatDate + "_" + roomNum.getText();
                out.println("ADD_" + inputStore);
                label:
                while (true) {
                    try {
                        check = in.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    switch (check) {
                        case "TAKEN TIME":
                            alert.setTitle("Schedule Clash");
                            alert.setContentText("The time selected is already taken on this date " + formatDate);
                            alert.show();
                            break label;
                        case "GOOD":
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setTitle("Class Added");
                            alert.setContentText("Class slot added to " + className.getText() + "\nType: " + classType.getValue() + "\nModule Name: " + moduleName.getText()
                                    + "\nModule Code: " + moduleCode.getText() + "\nTime: " + classTimes.getValue() + "\nDate: " + formatDate);
                            alert.show();
                            break label;
                        case "FIVE":
                            alert.setTitle("Too Many Modules");
                            alert.setContentText("This course already has five modules");
                            alert.setAlertType(Alert.AlertType.WARNING);
                            alert.showAndWait();
                            break label;
                        case "GOOD SQL":
                            break label;
                        case "NO SQL":
                            alert.setTitle("SQL Error");
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
                            break;
                    }
                }
            }
    });
        Stop.setOnAction(actionEvent -> {
        clear();
        stage.close();
    });
        vBox.getChildren().add(hBox);
        stage.setResizable(false);
        grid.add(vBox,0,0);
        stage.setTitle("Add Class To Schedule");
        grid.setBackground(new Background(new BackgroundImage(new Image("file:ul.png"),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        Scene scene = new Scene(grid,640,480);
        stage.getIcons().add(new Image("file:ulico.png"));
        stage.setScene(scene);
        stage.show();
}

    private void display(){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        Stage stage = new Stage();
        HBox hbox = new HBox();
        VBox vbox = new VBox();
        vbox.setSpacing(25);
        vbox.setPrefWidth(640);
        stage.getIcons().add(new Image("file:ulico.png"));
        Button dis = new Button("Display");
        Button close = new Button("Close");
        className.setPromptText("Enter A Name And Year : eg. LM051-2022");
        className.setPrefWidth(245);
        dis.setPrefWidth(60);
        close.setPrefWidth(60);
        dis.setPadding(new Insets(10));
        close.setPadding(new Insets(10));
        hbox.getChildren().addAll(dis,close);
        hbox.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(25);
        VBox list = new VBox(new Label("Class Name"),className);
        list.setSpacing(5);
        list.setMaxWidth(245);
        vbox.getChildren().addAll(menuBar(),list,hbox);
        close.setOnAction(actionEvent ->{
            clear();
            stage.close();
        });
        dis.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
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
                            alert.setTitle("Invalid Class");
                            alert.setContentText("No class exists with the name and year: " + className.getText());
                            alert.show();
                            break label;
                        case "GOOD":
                            alert.setTitle("Classes Displayed");
                            alert.setHeaderText("Completed");
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setContentText("Class time table displayed");
                            alert.show();
                            break label;
                        case "GOOD SQL":
                            check = "";
                            break label;
                        case "NO SQL":
                            alert.setTitle("SQL Error");
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
                            break label;
                    }
                }
            }
        });
        grid.add(vbox,0,0);
        grid.setBackground(new Background(new BackgroundImage(new Image("file:ul.png"),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        Scene sc = new Scene(grid,640,480);
        stage.setScene(sc);
        stage.show();
    }

    private void Remove() {
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
        classTimes.setMaxWidth(245);
        if(classTimes.getItems().isEmpty()) {
            for (int i = 9; i < 18; i++) {
                classTimes.getItems().add(i + ":00");
            }
        }
        vBox.setPrefWidth(640);
        vBox.getChildren().add(menuBar());
        HBox hBox = new HBox();
        grid.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(25);
        Button remove = new Button("Remove");
        Button stop = new Button("Close");
        remove.setPrefWidth(80);
        stop.setPrefWidth(80);
        remove.setPadding(new Insets(10));
        stop.setPadding(new Insets(10));
        hBox.getChildren().addAll(remove,stop);
        Node[] f = {new Label("Class Time"),classTimes,new Label("Class Date"),date, new Label("Class Name"),className};
        for(int i =0; i<f.length;i=i+2){
            VBox hBox1 = new VBox(f[i],f[i+1]);
            hBox1.setAlignment(Pos.CENTER_LEFT);
            hBox1.setMaxWidth(245);
            hBox1.setMinWidth(245);
            hBox1.setSpacing(5);
            vBox.getChildren().add(hBox1);
        }
        remove.setOnAction(actionEvent ->  {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            if(className.getText().isEmpty()) {
                alert.setContentText("Please enter a class name!");
                alert.show();
            } if (date.getValue()==null) {
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
                label:
                while (true) {
                    try {
                        check = in.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    switch (check) {
                        case "INVALID TIME":
                            alert.setTitle("Invalid Time");
                            alert.setContentText("The time selected is empty on this date " + date.getValue().toString());
                            alert.showAndWait();
                            break label;
                        case "INVALID CLASS NAME":
                            alert.setTitle("Invalid Class");
                            alert.setContentText("The class name you have entered is invalid " + className.getText());
                            alert.showAndWait();
                            break label;
                        case "GOOD":
                            alert.setTitle("Class Removed");
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setContentText("Class Name: " + className.getText() + "\nDate: " + date.getValue() + "\nTime: " + classTimes.getValue());
                            alert.showAndWait();
                            break label;
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
            grid.setBackground(new Background(new BackgroundImage(new Image("file:ul.png"),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
            stage.getIcons().add(new Image("file:ulico.png"));
            Scene scene = new Scene(grid, 640, 480);
            stage.setScene(scene);
            stage.show();
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
            out.println("CHECKING CONNECTION");
            String check = "";
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connected");
            alert.setHeaderText("Connection established");
            while(true){
                try {
                    check=in.readLine();
                } catch (IOException e) {
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setTitle("Server Error");
                    alert.setHeaderText("Connection Error");
                    alert.setContentText("Not connected to server!");
                    alert.showAndWait();
                    while (true) {
                        if (!alert.isShowing()) {
                            System.exit(1);
                            break;
                        }
                    }
                    break;
                }
                if (check.equals("false")) {
                    alert.setContentText("Connected to server!");
                    alert.showAndWait();
                    break;
                }
            }

        });
        menu.getMenus().add(options);
        return menu;
    }

    private void startConnection() {
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
                alert.setTitle("SQL Error");
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
                alert.setTitle("Connected");
                alert.setContentText("You have connected successfully");
                alert.show();

            }
        }catch(IOException e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
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

    private void clientStop() throws IOException {
        clientSocket.close();
    }

    public static void main(String[] args){
            launch();
    }
}