package net.Arnas.POP3Client.Messages;

import net.Arnas.POP3Client.Attachements.Attachement;
import net.Arnas.POP3Client.MIME.MIMELoader;
import net.Arnas.POP3Client.MIME.MIMEMessage;

import java.util.ArrayList;
import java.util.List;

public class Message {
    private String contents;
    private boolean loaded = false;
    private int messageID;
    private boolean markedForDeletion = false;
    private String hash;
    private ArrayList<Attachement> attachements = new ArrayList<Attachement>();

    public Message(int messageID){
        this.messageID = messageID;
    }

    // this should be used when loading only part of message
    public Message(int messageID, String contents){
        this.contents = contents;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getContents() { return contents; }

    public void setContents(String contents) {
        this.contents = contents;
        MIMEMessage mimeMessage = MIMELoader.readMIMEMessageFromContent(this,null, this.contents);
        /*for(MIMEMessage message : mimeMessage.getChildren()){
            System.out.println("Boundary: " + message.getBoundary());
            System.out.println("Children count: " + message.getChildren().size());
            System.out.println("Children: ");
            for(MIMEMessage child : message.getChildren()){
                //System.out.println("Child child amount: " + child.getChildren().size());
                System.out.println("Child content:\n" + child.getContent());
            }
        }*/
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public void addContentLine(String line){
        this.contents += line;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    public List<Attachement> getAttachements(){
        return attachements;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


}
