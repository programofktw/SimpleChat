package client;

import java.io.*;
import client.*;
import common.*;

public class ClientConsole implements ChatIF{
    final public static int DEFAULT_PORT = 5555;

    ChatClient client;

    public ClientConsole(String host, int port){
        try{
            client = new ChatClient(host, port, this);
        }catch(IOException e){
            System.out.println("host = "+ host+ " port = " + port);
            System.out.println("Error : Can't setup connection! Termination client.");
            System.exit(1);
        }

    }

    public void accept()
    {
        try {
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));

            String message;

            while(true){
                message = fromConsole.readLine();
                client.handleMessageFromClientUI(message);
            }
        } catch(Exception ex){
            System.out.println("Unexpected error while reading form console!");
        }
    }


    public void display(String message){
        System.out.println("> "+message);
    }

    public static void main(String[] args){
        String host = "";
        int port = 0;

        try{
            host = "localhost";
            // host = args[0];
        } catch(ArrayIndexOutOfBoundsException e){
            host = "localhost";
        }

        ClientConsole chat = new ClientConsole(host, DEFAULT_PORT);
        chat.accept();
    }
}
