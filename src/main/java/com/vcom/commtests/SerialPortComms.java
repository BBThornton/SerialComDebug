package com.vcom.commtests;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.function.Consumer;

public class SerialPortComms {
    private static final int RESEND_ATTEMPTS = 10;
    //Static list of all the ports to allow for port to port communication
    static ArrayList<SerialPortComms> ports = new ArrayList<SerialPortComms>();


    private final SerialPort serialPort;
    private Consumer<String> echoCallback;
    private final String name;

    /**
     * TODO Check port is open/catch exception
     * @param serialPort
     */
    public SerialPortComms(SerialPort serialPort, Consumer<String> echo, String name) {
        this.serialPort = serialPort;
        this.serialPort.openPort();
        this.serialPort.addDataListener(new DataAvailableListener());
        this.echoCallback = echo;
        this.name = name;
        ports.add(this);

    }


    public void CloseConnection(){
        this.serialPort.closePort();
    }

    public void SendData(String data){
        //byte[] byteData = data.getBytes(StandardCharsets.UTF_8);
        int ret = this.serialPort.writeBytes(data.getBytes(),data.length());
        int attempts = 0;
        while (ret == -1 && attempts<RESEND_ATTEMPTS){
            attempts+=1;
            System.out.println("WRITE ISSUE");
            ret = this.serialPort.writeBytes(data.getBytes(),data.length());
        }

        if(ret == -1){
            System.out.println("Failed to send");
            return;
        }

        System.out.println("Sending to "+serialPort.getSystemPortName());



    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    private final class DataAvailableListener implements SerialPortDataListener {

        @Override
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
        @Override
        public void serialEvent(SerialPortEvent event)
        {
            if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                return;
            }
            byte[] newData = new byte[serialPort.bytesAvailable()];
            int numRead = serialPort.readBytes(newData, newData.length);

            //Find the port in the list that isnt this one
            SerialPortComms otherPort = ports.stream().filter(p -> !p.getSerialPort().getSystemPortName().equals(serialPort.getSystemPortName())).findFirst().get();
            String dataValues = new String(newData, StandardCharsets.UTF_8);
            // Forward the data
            String data;
            if(name.equals("Com A")){
                data = ("-> :" + dataValues);
            }else{
                data = ("<- :" + dataValues);
            }

            otherPort.SendData(dataValues);

            echoCallback.accept(data);


        }
    }

}
