package kilfat.android_vk.model;

import java.util.List;

public class Message {
    private boolean out;
    private String message;
    private String photoIDs;
    private List<Message> fwdMessages;
    private long date;
    public boolean isOut() {
        return out;
    }

    public long getDate(){
        return this.date;
    }
    public String getMessage() {
        return message;
    }

    public String getPhotoIDs() {
        return photoIDs;
    }

    public boolean isFwd(){
        return (fwdMessages!=null)? true: false;
    }
    public void setFwdMessages(List<Message> fwdMessages){
        this.fwdMessages=fwdMessages;
    }

    public List<Message> getFwdMessages() {
        return fwdMessages;
    }

    public Message(String message,long date, String photoIDs, boolean out){
        this.message=message;
        this.photoIDs=photoIDs;
        this.out=out;
        this.date=date;
        this.fwdMessages=null;
    }

}
