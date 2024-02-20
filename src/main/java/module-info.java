module group1.cs4076_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens group1.cs4076_project to javafx.fxml;
    exports group1.cs4076_project;
}