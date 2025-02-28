package server;

import java.io.*;
import ocsf.server.*;
import common.*;

public class EchoServer extends AbstractServer
{
    final public static int DEFAULT_PORT = 5555;

    ChatIF serverUI;
    
    private boolean closing = false;



    public EchoServer(int port, ChatIF serverUI) throws IOException
    {
        super(port);
        this.serverUI = serverUI;
        listen();
    }

    public void handleMessageFromClient(Object msg, ConnectionToClient client)
    {
        String command = (String) msg;

        serverUI.display("Message : \"" + command+"\" from " + client.getInfo("loginID") );

        if(command.startsWith("#login")){
            if(client.getInfo("loginID") == null){
                try{
                    client.setInfo("loginID", command.substring(7));
                    serverUI.display(client.getInfo("loginID") + " has logged on.");
                    sendToAllClients(client.getInfo("loginID") + " has logged on.");
                }catch(IndexOutOfBoundsException e){
                    try{
                        client.sendToClient("ERROR - Invalid login command. Disconnection.");
                    }catch(IOException ex){ }

                    try{
                        client.close();
                    }catch(IOException exc){
                        serverUI.display("ERROR - Cannot remove client.");
                    }
                    serverUI.display("No login, terminating client's connection.");
                }
            } else {
                    serverUI.display(client.getInfo("loginID") + " attempted to login twice");
                

                    try{
                        client.sendToClient("cannot login twice.");
                    }catch(IOException e){
                        try{
                            client.close();
                        }catch(IOException ex){
                            try{
                                client.close();
                            }catch(IOException exc){
                                serverUI.display("ERROR - Cannot remove client.");
                            }
                        }
                    }
            } 
        } else{ 
                if(client.getInfo("loginID") == null){
                    serverUI.display("Unknown client did not login. " + "Terminating connection.");
                    try{
                        client.sendToClient("No login recorded, disconnection from server.");
                    }catch (IOException e){ }
                    finally{
                        try{
                            client.close();
                        }catch( IOException ex){
                            serverUI.display("ERROR - cannot remove client");
                        }
                    }
                }else{
                    this.sendToAllClients(client.getInfo("loginID") + "> " + msg);
                }
        }
    }
        // System.out.println("Message recieved" + msg + " from "+ client);
        // this.sendToAllClients(msg);
    

    public void quit() {
        try{
            closing = true;
            sendToAllClients("Server is quitting.");
            sendToAllClients("You will be disconnected.");
            close();
        }catch( IOException e){ }
        System.exit(0);
    }

    protected void serverStarted()
    {
        if(getNumberOfClients()!= 0)
            sendToAllClients("Server has restarted accepting connections.");

        System.out.println("Server listening for connections on port " + getPort());
    }

    protected void serverStopped(){
        serverUI.display("Server has stopped listening for connections.");
        sendToAllClients("WARRING - Server has stopped accepting clients.");
    }


    protected void serverClosed(){
        serverUI.display("Server is closed.");
    }

    protected void clientConnected(ConnectionToClient client){
        serverUI.display("A new client is attempting to connect "+ "to the server.");
    }

    protected void clientDisconnected(ConnectionToClient client){
        disconnectionNotify(client);
    }

    public void handleMessageFromServerUI(String message){
        if(message.startsWith("#quit")) quit();

        if(message.startsWith("#stop")){
            if(isListening()){
                stopListening();
            }else{
                serverUI.display("Cannot stop the server befor it is restarted.");
            }
            return;
        }

        if(message.startsWith("#start")){
            closing = false;
            if(!isListening()){
                try{
                    listen();
                }catch (IOException e){
                    serverUI.display("Cannot listen. Terminating server.");
                    quit();
                }
            }else{
                serverUI.display("Server is already running.");
            }
            return;
        }

        if(message.startsWith("#close")){
            closing = true;
            try{
                sendToAllClients("server shutting down. You are beging disconected.");
                close();
            }catch(IOException e){
                serverUI.display("Cannot close normally. Terminatin server.");
                quit();
            }
        }

        if(message.startsWith("#getport")){
            serverUI.display("Current port : " + getPort());
            return;
        }

        if(message.startsWith("#setport")){
            if((getNumberOfClients()!=0 )|| (isListening())){
                serverUI.display("Cannot change port while clients are connected or while werver is listening");
            }else{
                try{
                    int port = 0;

                    port = Integer.parseInt(message.substring(9));

                    if((port<1024)|| (port > 65535)){
                        serverUI.display("Invalid port number. Port unchaged.");
                    }else{
                        setPort(port);
                        serverUI.display("Port set to " + port);
                    }
                }catch(Exception e){
                    serverUI.display("Invalid use of the #setport command.");
                    serverUI.display("Port unchanged.");
                }
            }
            return;
        }

        if(!(message.startsWith("#"))){
            serverUI.display("SERVER MESSAGE> " +message);
            sendToAllClients("SERVER MESSAGE> " +message);

        }else{
            serverUI.display("Invalid command.");
        }
    }

    private void disconnectionNotify(ConnectionToClient client){
        if(client.getInfo("loginID") != null){
            sendToAllClients(client.getInfo("loginID") + " has disconnected");        
            serverUI.display(client.getInfo("loginID") + " has disconnected");    
        }
    }
}
    
