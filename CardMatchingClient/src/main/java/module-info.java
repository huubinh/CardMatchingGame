module com.project.cardmatchingclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.project.cardmatchingclient to javafx.fxml;
    opens com.project.cardmatchingclient.controllers to javafx.fxml;
    opens com.project.cardmatchingclient.models to javafx.base;

    exports com.project.cardmatchingclient;
}