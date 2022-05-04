package net.Arnas.POP3Client.Messages;

import net.Arnas.POP3Client.Connection;
import net.Arnas.POP3Client.POPMessage;
import net.Arnas.POP3Client.POPType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class MessageStorage {
    //hashmap for storing message id and message
    private HashMap<Integer, Message> messageHashMap = new HashMap<Integer, Message>();
    private Connection connection;
    private File fileStorageDirectory;

    public MessageStorage(Connection connection){
        this.connection = connection;
        createFileStorage("/messages");
    }

    public Message getMessage(int messageID){
        return messageHashMap.get(messageID);
    }

    private void createFileStorage(String directoryPath){
        fileStorageDirectory = new File(directoryPath);
        if(!fileStorageDirectory.exists()){
            fileStorageDirectory.mkdir();
        }
    }

    private void createFileMessage(Message message){
        File file = new File(fileStorageDirectory + "/" + Integer.toString(message.getMessageID()) + ".txt");
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            fileWriter.write(message.getContents());
            fileWriter.close();
            System.out.println(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param messageID - message ID
     * @param linecount - amount of lines to read
     * @return list of lines read
     */
    public String peekMessage(int messageID, int linecount){
        try {
            connection.sendMessage(POPType.TOP, Integer.toString(messageID), Integer.toString(linecount));
            if(connection.readPOPMessage().getPopType() != POPType.OK){
                return null;
            }
            return connection.readSocketAll();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean loadMessage(int messageID){
        try {
            Message message = getOrCreateMessage(messageID);
            if(message.isLoaded()){
                return true;
            }
            connection.sendMessage(POPType.RETR, Integer.toString(messageID));
            if(connection.readPOPMessage().getPopType() != POPType.OK){
                return false;
            }
            message.setContents(connection.readSocketAll());
            message.setLoaded(true);
            createFileMessage(message);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteMessage(int messageID){
        try {
            Message message = getOrCreateMessage(messageID);
            connection.sendMessage(POPType.DELE, Integer.toString(messageID));
            if(connection.readPOPMessage().getPopType() != POPType.OK){
                return false;
            }
            message.setMarkedForDeletion(true);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean undeleteMessages(){
        try {
            connection.sendMessage(POPType.RSET);
            if(connection.readPOPMessage().getPopType() != POPType.OK){
                return false;
            }
            for(Message message : messageHashMap.values()){
                message.setMarkedForDeletion(false);
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getMessageHash(int messageID){
        try {
            Message message = getOrCreateMessage(messageID);
            connection.sendMessage(POPType.UIDL, Integer.toString(messageID));
            POPMessage popMessage = connection.readPOPMessage();
            if(popMessage.getPopType() != POPType.OK){
                return null;
            }
            message.setHash(popMessage.getArguments().get(1));
            return message.getHash();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Message getOrCreateMessage(int messageID){
        //if message doesn't exist, create a new one
        if(!messageHashMap.containsKey(messageID)) {
            Message message = new Message(messageID);
            messageHashMap.put(messageID, message);
        }
        return messageHashMap.get(messageID);
    }

}
