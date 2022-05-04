package net.Arnas.POP3Client.MIME;

import net.Arnas.POP3Client.Messages.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class MIMELoader {

    public static MIMEMessage readMIMEMessageFromContent(Message message, MIMEMessage parent, String messageContent){
        if(messageContent.indexOf("Content-Type") == -1){
            return getFirstParent(parent);
        }
        String contentTypeStart = messageContent.substring(messageContent.indexOf("Content-Type") + 14);
        String contentType = contentTypeStart.substring(0, contentTypeStart.indexOf(";"));
        if(contentType.contains("multipart")) {
            String boundary = "";
            if (messageContent.indexOf("boundary=\"") != -1) {
                boundary = contentTypeStart.substring(contentTypeStart.indexOf("boundary=\"") + 10, contentTypeStart.indexOf("boundary=\"") + 10 + 28);
                String content = contentTypeStart.substring(contentTypeStart.indexOf("--" + boundary), contentTypeStart.lastIndexOf("--" + boundary));
                String[] contentMessages = content.split("--" + boundary);
                MIMEMessage currentMessage = new MIMEMessage(contentType, boundary, content, parent);
                for(String str : contentMessages){
                    if(str.length() == 0){
                        continue;
                    }
                    currentMessage.addChild(readMIMEMessageFromContent(message, currentMessage, str));
                }
                return getFirstParent(currentMessage);
            }
        } else {
            String attachmentName = "";
            String content = contentTypeStart.substring(contentTypeStart.indexOf("\n\n") + 2).replace("\n", "");
            if(contentType.equalsIgnoreCase("text/plain")){
                System.out.println("Included plain text message:\n" + content);
            } else if(contentType.equalsIgnoreCase("text/html")){
                System.out.println("Included html text message:\n" + content);
            } else{
                int nameIndex = contentTypeStart.indexOf("name=\"");
                System.out.println("Attachment type: " + contentType);
                if(nameIndex != -1){
                    String nameCut = contentTypeStart.substring(contentTypeStart.indexOf("name=\"") + 6);
                    attachmentName = nameCut.substring(0, nameCut.indexOf("\""));
                    System.out.println("Attachement name: " + attachmentName);
                    createFileStorage("/messages/" + message.getMessageID() + "_attachements");
                    try (FileOutputStream stream = new FileOutputStream("/messages/" + message.getMessageID() + "_attachements/" + attachmentName)) {
                        byte[] decodedString = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));
                        stream.write(decodedString);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return getFirstParent(parent);
    }

    public static void createFileStorage(String directoryPath){
        File fileStorageDirectory = new File(directoryPath);
        if(!fileStorageDirectory.exists()){
            fileStorageDirectory.mkdir();
        }
    }

    public static MIMEMessage getFirstParent(MIMEMessage anyChild){
        MIMEMessage returnMessage = anyChild;
        if(returnMessage == null){
            return null;
        }
        do{
            if(returnMessage.getParent() == null){
                return returnMessage;
            }
            returnMessage = returnMessage.getParent();
        }
        while(returnMessage != null);
        return null;
    }
}
