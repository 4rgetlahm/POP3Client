package net.Arnas.POP3Client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Connection {

    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private POPInterpreter popInterpreter;

    public Connection(String IP, int port){
        try {
            popInterpreter = new POPInterpreter();

            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket socket = sslSocketFactory.createSocket(IP, port);

            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(POPType popType, String... args){
        try {
            POPMessage popMessage = new POPMessage(popType, Arrays.asList(args));
            socketWriter.println(popMessage);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public POPMessage readPOPMessage(){
        try{
            String line = socketReader.readLine();
            return popInterpreter.convertToPOPMessage(line);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String readSocket(){
        try{
            return socketReader.readLine();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String readSocketAll(){
        try{
            String read = "";
            String tempRead;
            while(!(tempRead = socketReader.readLine()).equals(".")){
                read += tempRead + "\n";
            }
            return read;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean pingPOPConnection(){
        sendMessage(POPType.NOOP);
        if(readPOPMessage().getPopType() == POPType.OK){
            return true;
        }
        return false;
    }
}
