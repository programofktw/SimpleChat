package client;

import ocsf.client.*;
import common.*;
import java.io.*;

public class ChatClient extends AbstractClient{
    
    ChatIF clientUI;

    String loginID;

    public ChatClient(String host, int port, String login,ChatIF clientUI) {
        super(host, port);
        this.clientUI = clientUI;
        this.loginID =login;

        try{
            openConnection();
            sendToServer("#login " + login);
        } catch (IOException e){
            clientUI.display("CanNot open Connection. Awation command.");
        }

    }

    public void handleMessageFromServer(Object msg){
        clientUI.display(msg.toString());
    }

    public void handleMessageFromClientUI(String message) {
        if(message.startsWith("#login") && !isConnected()){
            try{
                openConnection();
            } catch(IOException e){
                clientUI.display("Cannot establish connection. Awating command.");
                return;
            }
        }

        if(message.startsWith("#quit")) quit();

        if(message.startsWith("#logoff")){
            try{
                closeConnection();
            }catch(IOException e){
                clientUI.display("Cannot logoff normally. Termination client");
                quit();
            }

            connectionClosed(false);
            return;
        }

        if(message.startsWith("#gethost")){
            clientUI.display("Current host: "+ getHost());
            return;
        }

        if(message.startsWith("#getport")){
            clientUI.display("Current port: " + getPort());
            return; 
        }

        if(message.startsWith("#sethost")){
            if(isConnected()) clientUI.display("Cannot change host wile connected.");
            else{
                try{
                    setHost(message.substring(9));
                    clientUI.display("Host set to : "+getHost());
                } catch(IndexOutOfBoundsException e){
                    clientUI.display("Invalid host: use #sethost <host>.");
                }
            }

            return;
        }

        if(message.startsWith("#setport")){
            if(isConnected()) 
                clientUI.display("Cannot change port while connected.");
            else{
                try{
                    int port = 0;
                    port = Integer.parseInt(message.substring(9));

                    if((port<1-24) || (port > 65535)){
                        clientUI.display("Invalid port number. Port unchaged");
                    }else{
                        setPort(port);
                        clientUI.display("Port set to "+ port);
                    }
                }catch(Exception e){
                    clientUI.display("Invalid port.Use #setport <port>. Port unchanged");
                }
        }
        return;
    }

    if((message.startsWith("#login"))||(!(message.startsWith("#")))){
        try{
            sendToServer(message);
        } catch( IOException e){
            clientUI.display("cannot send the message to the server. disconnectiong");

            try{
                closeConnection();
            }catch(IOException ex){
                clientUI.display("Cannot logoff normally. Terminating client.");
                quit();
            }
        }
        }else{
            clientUI.display("Invalid command");
        }
    }

    public void quit()
    {
        try {
            closeConnection();
        }catch(IOException e){}
        System.exit(0);
    }

    protected void connectionClosed(boolean isAbnormal){
        if(isAbnormal){clientUI.display("Abnormal termination of connection.");}
        else{clientUI.display("Connected closed");}

    }

    protected void connectionEstablished(){
        clientUI.display("Connection established with " + getHost()+" on port " + getPort());
    }

    protected void connectionException(Exception e){
        clientUI.display("Connection to server terminated.");
    }
}