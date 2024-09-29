/**------------------------------------------
 Project 3: Messaging App
 Course: CS 342, Spring 2024
 System: IntelliJ and Windows 11 and macOS
 Student Author: Dana Fakhreddine and Viviana Lopez
 ---------------------------------------------**/

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    private String userId;
    private String message;
    ArrayList<String> receivers = new ArrayList<>();
    String sender;
    ArrayList<String> usernames = new ArrayList<>();
    ArrayList<String> conversations = new ArrayList<>();
    private Integer checkDupFlag;
    private Integer createChatFlag;
    private Integer sendMessageFlag;
    private Integer sendAllFlag;

    public Message(){
        userId = "";
        message = "";
        sender = "";
        checkDupFlag = 1; //user exists
        createChatFlag = 0; //no chat created
        sendMessageFlag = 0; //user is not sending a message
        sendAllFlag = 0; //user is not sending to all
    }

    //all the functions below give access to the private variables in the message class
    public void setSendAllFlag(Integer sendAllFlag){
        this.sendAllFlag =sendAllFlag;
    }

    public Integer getSendAllFlag(){
        return sendAllFlag;
    }

    public void setSendMessageFlag(Integer sendMessageFlag){
        this.sendMessageFlag =sendMessageFlag;
    }

    public Integer getSendMessageFlag(){
        return sendMessageFlag;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public String getSender(){
        return sender;
    }

    public void setCreateChatFlag(Integer createChatFlag){
        this.createChatFlag = createChatFlag;
    }

    public Integer getCreateChatFlag(){
        return createChatFlag;
    }

    public void setCheckDupFlag(Integer checkDupFlag){
        this.checkDupFlag = checkDupFlag;
    }

    public Integer getCheckDupFlag(){return checkDupFlag;}

    public String getUserId(){
        return userId;
    }

    public void setUserId(String username){
        userId = username;
    }
}
