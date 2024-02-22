package group1.cs4076_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static ServerSocket serverSocket;
    private static final int PORT = 1234;
    private static int clientConnections = 0;

    public static void connect(int port) throws IOException {//Step 2.
        try {
            clientSocket = serverSocket.accept();               //Step 2.
            clientConnections++;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Step 3.
            out = new PrintWriter(clientSocket.getOutputStream(), true); //Step 3.

            String message = in.readLine();         //Step 4.
            System.out.println("Message received from client: " + clientConnections + "  " + message);
            out.println("Response from Server (Capitalized Message): " + message.toUpperCase());     //Step 4.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() throws IOException {
        clientSocket.close();
        serverSocket.close();
    }
    public static void main(String[] args) throws IOException {
        System.out.println("Opening port...\n");
        try
        {
            serverSocket = new ServerSocket(PORT);      //Step 1.
        }
        catch(IOException e)
        {
            System.out.println("Unable to attach to port!");
        }finally {
            connect(PORT);
        }

    }
}

