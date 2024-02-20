package group1.cs4076_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private static ServerSocket servSock;
    private static final int PORT = 1234;
    private static int clientConnections = 0;

    public static void main(String[] args) {
        System.out.println("Opening port...\n");
        try
        {
            servSock = new ServerSocket(PORT);      //Step 1.
        }
        catch(IOException e)
        {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }

        do
        {
            run();
        }while (true);

    }

    private static void run()
    {
        Socket link = null;                        //Step 2.
        try
        {
            link = servSock.accept();               //Step 2.
            clientConnections++;
            BufferedReader in = new BufferedReader( new InputStreamReader(link.getInputStream())); //Step 3.
            PrintWriter out = new PrintWriter(link.getOutputStream(),true); //Step 3.

            String message = in.readLine();         //Step 4.
            System.out.println("Message received from client: " + clientConnections + "  "+ message);
            out.println("Echo Message: " + message);     //Step 4.
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    } // finish run method
}
