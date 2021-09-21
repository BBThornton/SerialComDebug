module com.vcom.commtests {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;


    opens com.vcom.commtests to javafx.fxml;
    exports com.vcom.commtests;
}