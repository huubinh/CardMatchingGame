module com.project.cardmatchingserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.project.cardmatchingserver to javafx.fxml;
    exports com.project.cardmatchingserver;
}