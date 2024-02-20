package group1.cs4076_project;


import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TPCClient {
    public Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private static final int port = 1234;

    private static InetAddress host;

    public void startConnection() throws IOException {
        clientSocket = new Socket(host, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        System.out.println(msg);
        return in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        try {
            host = InetAddress.getLocalHost();
        }catch (Exception e){
            System.out.print("NO");
        }
        System.out.print("Starting Connectiopn");
        do{
            TPCClient c = new TPCClient();
            c.startConnection();
        }while(true);
    }

}
