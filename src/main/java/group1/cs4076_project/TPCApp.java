package group1.cs4076_project;

import javafx.application.Application;
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
    private ArrayList<String> List = new ArrayList<>();
    private HashMap<String,String> hashMap = new HashMap<>();
    public TreeMap<String,String[]> db = new TreeMap<>();

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
        stage.getIcons().add(new Image("file:C:/Users/jakub/OneDrive/Pulpit/Studia/2/cs4076_project/src/main/java/group1/icon.png"));
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
    TextField text = new TextField();
    TextField cText = new TextField();
    ChoiceBox<String> cb = new ChoiceBox<>();

    public void addClass() throws IOException {
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
            Alert a1 = new Alert(Alert.AlertType.WARNING);
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
                x += text.getText() + " " + dp.getValue() + " " + cb.getValue();
                if(hashMap.toString().contains(cText.getText())){
                    if(hashMap.get(cText.getText()).contains(dp.getValue().toString()) && hashMap.get(cText.getText()).contains(cb.getValue()) ) {
                        a1.setContentText("The time selected is already taken on this date "+dp.getValue().toString()); a1.show();
                    }else hashMap.put(cText.getText(), hashMap.get(cText.getText()) + "," + x);
            }else hashMap.put(cText.getText(),x);

                String[] da = hashMap.toString().split(",")   ;
                System.out.println(Arrays.toString(da));
            List = new ArrayList<>();
            List.add(Arrays.toString(da).replace("[","").replace("]",""));
            try{
                CSV();
            }catch (Exception e){
                System.out.println("ERROR");
            }
        });
        Stop.setOnAction(actionEvent -> s.close());
        grid.add(vBox,0,0);
        s.setTitle("Add Class To Schedule");
        s.getIcons().add(new Image("file:C:/Users/jakub/OneDrive/Pulpit/Studia/2/cs4076_project/src/main/java/group1/icon.png"));
        Scene scene = new Scene(grid,640,480);
        s.setScene(scene);
        s.show();
    }
public void CsvReader() throws FileNotFoundException {
    //parsing a CSV file into Scanner class constructor
    Scanner sc = new Scanner(file);
    sc.useDelimiter(",");   //sets the delimiter pattern
    while (sc.hasNext())  //returns a boolean value
    {
        List.add(sc.next());  //find and returns the next complete token from this scanner
    }
    sc.close();
}
    File file = new File("C:/Users/jakub/OneDrive/Pulpit/Studia/2/cs4076_project/src/main/resources/group1/cs4076_project/data.csv");
    public void CSV() throws FileNotFoundException{
        CsvReader();
        PrintWriter print = new PrintWriter(file);
        for(int i = 0; i<List.size();i++){
            print.print(List.get(i));
        }
        print.close();
    }
    public MenuBar m(){
        MenuBar mb = new MenuBar();
        Menu options = new Menu("Options");
        MenuItem clear = new MenuItem("Clear");
        MenuItem checkCon = new MenuItem("Check Connection");
        options.getItems().add(checkCon);
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