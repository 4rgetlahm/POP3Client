package net.Arnas.POP3Client;

import net.Arnas.POP3Client.Messages.MessageStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Communicator {

    private Connection connection;
    private BufferedReader inputReader;
    private MessageStorage messageStorage;
    private boolean isAlive = true;

    public Communicator(Connection connection){
        this.connection = connection;
        inputReader = new BufferedReader(new InputStreamReader(System.in));
        messageStorage = new MessageStorage(connection);
    }

    public void start(){
        // wait for greeting
        POPMessage greeting = connection.readPOPMessage();
        if(greeting.getPopType() != POPType.OK){ // if greeting is good
            System.out.println("Error on connecting...");
        }
        while(!login()){
            System.out.println("Starting login process...");
        }
        System.out.println("You have successfully logged in!");
        System.out.println("You have " + getMessageCountAndSize()[0] + " total messages.");

        while(isAlive){
            selectOptionsMenu();
        }
    }

    private void selectOptionsMenu(){
        System.out.println("----------------------------MENU----------------------------");
        System.out.println("1 - Show message amount and their size");
        System.out.println("2 - List all messages and their size");
        System.out.println("3 - Download message");
        System.out.println("4 - Peek message (first two lines)");
        System.out.println("5 - Delete message");
        System.out.println("6 - Undelete messages");
        System.out.println("7 - Get message's unique hash listing");
        System.out.println("8 - Ping server :)");
        System.out.println("9 - Quit");
        System.out.println("------------------------------------------------------------");

        String input = "";
        int choice = 0;
        try {
            input = inputReader.readLine();
            choice = Integer.parseInt(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch(choice){
            case 1:
                int[] messageCountAndSize = getMessageCountAndSize();
                System.out.println("You have " + messageCountAndSize[0] + " total messages, their size: " + messageCountAndSize[1] + " octet");
                break;
            case 2:
                ArrayList<Integer> sizes = getMessageSizes();
                if(sizes == null){
                    return;
                }
                for(int i = 0; i != sizes.size(); i++){
                    System.out.println("Message " + (i+1) + " size is: " + sizes.get(i) + " octet");
                }
                break;
            case 3:
                int receiveMessageID = enterMessageID("Enter message ID that you want to receive:");
                System.out.println("Receiving message...");
                if(messageStorage.loadMessage(receiveMessageID)) {
                    System.out.println("Message received, check your file storage");
                } else{
                    System.out.println("Error occured trying to receive the message, are you sure it exists?");
                }
                break;
            case 4:
                int peekMessageID = 0;
                int lineCount = 0;
                try {
                    peekMessageID = enterMessageID("Enter message ID that you want to peek:");

                    System.out.println("Enter amount of lines to peek:");
                    input = inputReader.readLine();
                    lineCount = Integer.parseInt(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String peekResult = messageStorage.peekMessage(peekMessageID, lineCount);
                System.out.println(peekResult);
                break;
            case 5:
                int deleteMessageID = enterMessageID("Enter message ID that you want to delete:");
                if(messageStorage.deleteMessage(deleteMessageID)){
                    System.out.println("Message deleted");
                }
                else{
                    System.out.println("Error occurred on deleting message");
                }
                break;
            case 6:
                System.out.println("Undeleting messages");
                messageStorage.undeleteMessages();
                break;
            case 7:
                int uniqueMessageID = enterMessageID("Enter message ID that you want to unique hash from:");
                String hash = messageStorage.getMessageHash(uniqueMessageID);
                if(hash == null){
                    System.out.println("Error occurred on getting unique hash");
                } else{
                    System.out.println("Message hash is " + hash);
                }
            break;
            case 8:
                System.out.println("Pinging POP server...");
                if(connection.pingPOPConnection()){
                    System.out.println("Received response");
                }
                break;
            case 9:
                System.out.println("Quitting!");
                quit();
                break;
            default:
                System.out.println("Invalid option");
                break;
        }
    }

    private int enterMessageID(String messageToSend){
        String input;
        System.out.println(messageToSend);
        try {
            input = inputReader.readLine();
            return Integer.parseInt(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean login(){
        System.out.println("Enter your username:");

        if(!enterUsername()){
            return false;
        }

        System.out.println("Enter your password:");

        if(!enterPassword()){
            return false;
        }

        return true;
    }

    /**
     *
     * @return int array where [0] is message count, [1] is total message size in octets
     */
    private int[] getMessageCountAndSize(){
        int[] responseData = new int[2];
        connection.sendMessage(POPType.STAT);

        POPMessage response = connection.readPOPMessage();
        if(response.getPopType() == POPType.OK){
            try {
                responseData[0] = Integer.parseInt(response.getArguments().get(0));
                responseData[1] = Integer.parseInt(response.getArguments().get(1));
            } catch(Exception e){
                e.printStackTrace();
            }
            return responseData;
        }
        return null;
    }

    /**
     *
     * @return List with sizes of messages where index is (message ID - 1) and value is size
     */
    private ArrayList<Integer> getMessageSizes(){
        ArrayList<Integer> sizes = new ArrayList<>();
        connection.sendMessage(POPType.LIST);
        if(connection.readPOPMessage().getPopType() != POPType.OK){
            return null;
        }
        String response = connection.readSocketAll();
        for(String entry : response.split("\n")){ // split by newline e.g (1 200\n2 50\n.CRLF)
            System.out.println(entry);
            try {
                String[] split = entry.split(" ");
                sizes.add(Integer.parseInt(split[1]));
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return sizes;
    }

    /**
     *
     * @return true if username input was successful, otherwise false
     */
    private boolean enterUsername(){
        String username = "";
        try {
            username = inputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.sendMessage(POPType.USER, username);

        POPMessage response = connection.readPOPMessage();
        if(response.getPopType() == POPType.ERR || response.getPopType() != POPType.OK){
            System.out.println("Invalid username!");
            return false;
        }
        return true;
    }

    /**
     *
     * @return true if password input was successful, otherwise false
     */
    private boolean enterPassword(){
        String password = "";
        try {
            password = inputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.sendMessage(POPType.PASS, password);

        POPMessage response = connection.readPOPMessage();
        if(response.getPopType() == POPType.ERR || response.getPopType() != POPType.OK){
            System.out.println("Invalid password!");
            return false;
        }
        return true;
    }

    /**
     *
     * @return true if got good response from server after sending quit message, otherwise false
     */
    private boolean quit(){
        connection.sendMessage(POPType.QUIT);
        POPMessage response = connection.readPOPMessage();
        System.out.println(response);
        if(response.getPopType() != POPType.OK){
            System.out.println("Unable to quit!");
            return false;
        }
        isAlive = false;
        return true;
    }
}
