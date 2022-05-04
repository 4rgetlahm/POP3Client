package net.Arnas.POP3Client.Attachements;

public class Attachement {
    public String fileName;
    public String contentType;
    public byte[] content;

    public Attachement(String fileName, String contentType, byte[] content){
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }
}
