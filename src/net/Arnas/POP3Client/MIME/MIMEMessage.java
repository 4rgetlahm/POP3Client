package net.Arnas.POP3Client.MIME;

import java.util.ArrayList;

public class MIMEMessage {
    private String mimeType;
    private String boundary;
    private String charSet;
    private String fileName;
    private String content;
    private ArrayList<MIMEMessage> children = new ArrayList<>();
    private MIMEMessage parent;

    public MIMEMessage(String mimeType, String boundary, String content, MIMEMessage parent){
        this.mimeType = mimeType;
        this.boundary = boundary;
        this.content = content;
        this.parent = parent;
    }

    public void addChild(MIMEMessage child) {
        this.children.add(child);
    }

    public ArrayList<MIMEMessage> getChildren() {
        return children;
    }

    public MIMEMessage getParent(){
        return parent;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getCharSet() {
        return charSet;
    }

    public String getFileName() {
        return fileName;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
