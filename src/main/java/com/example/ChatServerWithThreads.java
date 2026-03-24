//David Saiontz 3/24/38 client for basic chat server
package com.example;

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.ArrayList;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program should allow the client to send it messages. The messages should then 
 * become visible to all other clients.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example). 
 * 
 * This version of the program creates a new thread for
 * every connection request.
 */
public class ChatServerWithThreads {

    public static final int LISTENING_PORT = 9876;
    static ArrayList<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();
    public static void main(String[] args) throws IOException{

        ServerSocket listener;  // Listens for incoming connections.
        Socket connection;      // For communication with the connecting program.
        

        /* Accept and process connections forever, or until some error occurs. */

        try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Listening on port " + LISTENING_PORT);
            while (true) {
                connection = listener.accept();
                ConnectionHandler handler = new ConnectionHandler(connection);
                handler.start();
                connections.add(handler);
            }
        }
        catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }

    }  // end main()


    /**
     *  Defines a thread that handles the connection with one
     *  client.
     */
    private static class ConnectionHandler extends Thread {
        Socket client;
        ObjectInputStream ois;
        ObjectOutputStream oos;
        ConnectionHandler(Socket socket) throws IOException {
            client = socket;
            ois = new ObjectInputStream(client.getInputStream());
            oos = new ObjectOutputStream(client.getOutputStream());
        }
        public void run() {
            String clientAddress = client.getInetAddress().toString();
            while(true) {
	            try {
                    String message = (String)ois.readObject();
                    System.out.println("Recieved: " + message);
                    if (message.equals("disconnect")){
                        System.out.println("Client disconnected");
                        connections.remove(connections.indexOf(this));
                        break;
                    }
                    for (ConnectionHandler connection : connections){
                        connection.oos.writeObject(message);
                        connection.oos.flush();
                    } 
	            } 
                catch (EOFException e){
                    System.out.println("Client disconnected");
                    connections.remove(connections.indexOf(this));
                    break;
                }catch (Exception e){
	                System.out.println("Error on connection with: " 
	                        + clientAddress + ": " + e);
                    
	            }
            }
            
        }
    }


}
