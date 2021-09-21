package com.vcom.commtests;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.function.Consumer;

public class SerialPortComms {
    //Static list of all the ports to allow for port to port communication
    static ArrayList<SerialPortComms> ports = new ArrayList<SerialPortComms>();


    private final SerialPort serialPort;
    Consumer<String> echoCallback;

    /**
     * TODO Check port is open/catch exception
     * @param serialPort
     */
    public SerialPortComms(SerialPort serialPort, Consumer<String> echo) {
        this.serialPort = serialPort;
        this.serialPort.openPort();
        this.serialPort.addDataListener(new DataAvailableListener());
        this.echoCallback = echo;
        ports.add(this);

    }

    public void SendData(String data){
        byte[] byteData = data.getBytes(StandardCharsets.UTF_8);
        int ret = this.serialPort.writeBytes(byteData,byteData.length);
        if (ret == -1){
            System.out.println("WRITE ISSUE");
        }else{
            System.out.println("Wrote "+ret+" Bytes");
        }

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



            // Forward the data
            String data = (new String(newData, StandardCharsets.UTF_8));
            //Find the port in the list that isnt this one
            SerialPortComms otherPort = ports.stream().filter(p -> !p.getSerialPort().getSystemPortName().equals(serialPort.getSystemPortName())).findFirst().get();
            System.out.println(serialPort.getSystemPortName());
            otherPort.SendData(data);

            echoCallback.accept(data);
        }
    }

}
