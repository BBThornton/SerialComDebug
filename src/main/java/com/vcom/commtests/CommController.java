package com.vcom.commtests;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import com.fazecast.jSerialComm.*;
import javafx.scene.control.TextArea;

import java.io.Console;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;


public class CommController implements Initializable {
    public static final String NO_PORTS_MESSAGE = "No Com Ports Available";

    @FXML private ChoiceBox<String> cbComA;
    @FXML private ChoiceBox<String> cbComB;
    @FXML private Button btnStartStop;
    @FXML private TextArea txtConsole;
    @FXML private TextArea txtOnPhrases;
    @FXML private TextArea txtOffPhrases;

    private SerialPort[] ports;

    @Override
    public void initialize(URL url, ResourceBundle rb){

        /* Sets up the port drop downs*/
        updatePortCB();
    }


    /**
     * TODO: Might move the process to be persistent thus allowing manual pausing and starting without getting new port
     *
     */
    public void prssStart(){
        int portA = cbComA.getSelectionModel().getSelectedIndex();
        int portB = cbComB.getSelectionModel().getSelectedIndex();
        SerialPortComms ComA = new SerialPortComms(ports[portA], (String data) -> ConsoleOut(data));
        SerialPortComms ComB = new SerialPortComms(ports[portB], (String data) -> ConsoleOut(data));

    }

    public synchronized void ConsoleOut(String data) {
        txtConsole.appendText(data);
    }

    /**
     * Updates the list of available com ports
     * If there are no ports available will display a message in place of options
     * TODO: Store the selected comports and load them if they are available (SEE USER PREFS  java.util.prefs)
     * TODO 2: Get the "inuse" Status of the ports and display
     */
    private void updatePortCB(){
        this.ports = SerialPort.getCommPorts();
        ObservableList<String> options = FXCollections.observableArrayList();

        cbComA.setItems(options);
        cbComB.setItems(options);
        if(this.ports.length == 0){
            options.add(NO_PORTS_MESSAGE);
            cbComA.setValue(NO_PORTS_MESSAGE);
            cbComB.setValue(NO_PORTS_MESSAGE);
            return;
        }

        /* Load the ports as options*/
        for (SerialPort p:ports) {
            options.add(p.getSystemPortName());
        }

        if(ports.length == 1){
            cbComA.setValue(options.get(0));
            cbComB.setValue(options.get(0));
            return;
        }

        cbComA.setValue(options.get(0));
        cbComB.setValue(options.get(1));
    }

}