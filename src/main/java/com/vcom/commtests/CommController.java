package com.vcom.commtests;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import com.fazecast.jSerialComm.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.Console;
import java.net.URL;
import java.util.*;


public class CommController implements Initializable {
    public static final String NO_PORTS_MESSAGE = "No Com Ports Available";

    @FXML private ChoiceBox<String> cbComA;
    @FXML private ChoiceBox<String> cbComB;
    @FXML private Button btnStartStop;
    @FXML private TextArea txtConsole;
    @FXML private TextArea txtOnPhrases;
    @FXML private TextArea txtOffPhrases;
    @FXML private TextField txtBaudRate;

    private SerialPort[] ports;
    SerialPortComms ComA;
    SerialPortComms ComB;
    private boolean running = false;
    private boolean updateDisplay = true;
    private Set<String> onPhrasesSet;
    private Set<String> offPhrasesSet;

    @Override
    public void initialize(URL url, ResourceBundle rb){

        /* Sets up the port drop-downs*/
        updatePortCB();
    }


    /**
     * TODO: Might move the process to be persistent thus allowing manual pausing and starting without getting new port
     * TODO: Possible running indicator
     */
    public void prssStart(){
        if(!running) {
            int portA = cbComA.getSelectionModel().getSelectedIndex();
            int portB = cbComB.getSelectionModel().getSelectedIndex();
            int baud = Integer.parseInt(txtBaudRate.getText());
            ComA = new SerialPortComms(ports[portA], (String data) -> ConsoleOut(data), "Com A",baud);
            ComB = new SerialPortComms(ports[portB], (String data) -> ConsoleOut(data), "Com B",baud);
            btnStartStop.setText("Stop");
            UpdatePhrases();
            this.running = true;

        }else{
            ComA.CloseConnection();
            ComB.CloseConnection();
            btnStartStop.setText("Start");
            this.running = false;
        }
    }

    public void ConsoleOut(String data) {
        Platform.runLater(()->{
            String rawData = data.substring(4).strip();
            if (offPhrasesSet.contains(rawData)){
                txtConsole.appendText(data);
                updateDisplay = false;
                return;
            }else if(onPhrasesSet.contains(rawData)){
                txtConsole.appendText(data);
                updateDisplay = true;
            }

            if (updateDisplay){
                txtConsole.appendText(data);
            }

        });

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

    private void UpdatePhrases(){
        String onPhrasesTxt = txtOnPhrases.getText();
        String onPhrasesList[] = onPhrasesTxt.split("\\r?\\n");
        onPhrasesSet = new HashSet<String>(List.of(onPhrasesList));

        String offPhrasesTxt = txtOffPhrases.getText();
        String offPhrasesList[] = offPhrasesTxt.split("\\r?\\n");
        offPhrasesSet = new HashSet<String>(List.of(offPhrasesList));
    }



}