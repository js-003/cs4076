package group1.cs4076_project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;


public class TPCApp extends Application {
    private TextField textbox;

    @Override
    public void start(Stage stage)git remote add origin <remote_github_url>
    -git remote add origin <remote_github_url>
    {
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
        stop.setOnAction(event -> Platform.exit());
        buttons.add(send, 0, 1);
        buttons.add(stop, 1, 1);
        return buttons;
    }

    public void buttonPressed(String text) throws IOException {
        if (text.equals("Send")) {
            TPCClient c = new TPCClient();
            c.sendMessage(textbox.getText());
        } else Platform.exit();

    }

    public Button createButton(String buttonName) {
        Button button = new Button(buttonName);
        button.setPrefWidth(75);
        button.setOnAction(event -> {
            try {
                buttonPressed(button.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return button;
    }


    public static void main(String[] args) {
    launch();
    }
}

