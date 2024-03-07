package group1.cs4076_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;

public class TCPServer {

    private HashMap<String, String> dbNewAdd = new HashMap<>();
    private HashMap<String,String> dbStorage = new HashMap<>();

    static String[] data ;
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static ServerSocket serverSocket;
    private static final int PORT = 1234;
    private static int clientConnections = 0;

    public void connect(int port) throws IOException {//Step 2.
        dataBase();

        try {
            clientSocket = serverSocket.accept();               //Step 2.
            clientConnections++;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Step 3.
            out = new PrintWriter(clientSocket.getOutputStream(), true); //Step 3.
            boolean checker = true;
            while (checker) {
                String message = in.readLine();
                //message = message.replaceAll(";", "\n").replaceAll("=", "\n").replaceAll(", ", "\n");
                message = message.replace("{", "").replace("}", "");//Step 4.
                System.out.println("Message received from client: " + clientConnections + "\n" + message);
                data = message.split("_");
                switch (data[0]) {
                    case "ADD" -> {
                        add();
                    }
                    case "REMOVE" -> {
                    }
                    case "DISPLAY" -> {
                    }
                    case "STOP" -> {
                        checker = false;
                    }
                }
                out.println("Response from Server (Capitalized Message): " + data[0].toUpperCase());
            }//Step 4.
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void dataBase() {
        try {
            Connection dbconnection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/data",
                    "root",
                    "@Root_4076-"
            );
            Statement dbstatement = dbconnection.createStatement();
            ResultSet dbResultSet = dbstatement.executeQuery("SELECT * FROM CLASSES");
            while (dbResultSet.next()) {
                String dbReader = dbResultSet.getString("modulename") + "_" + dbResultSet.getString("modulecode") + "_" + dbResultSet.getString("time") + "_" + dbResultSet.getString("date") + "_" + dbResultSet.getString("roomnumber");
                if (dbStorage.containsKey(dbResultSet.getString("classyear"))) {
                    String tmp = dbStorage.get(dbResultSet.getString("classyear"));
                    dbStorage.put(dbResultSet.getString("classyear"), tmp + ";" + dbReader);
                } else dbStorage.put(dbResultSet.getString("classyear"), dbReader);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void ServerStop() throws IOException {
        clientSocket.close();
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Opening port...\n");
        try {
            serverSocket = new ServerSocket(PORT);      //Step 1.
        } catch (IOException e) {
            System.out.println("Unable to attach to port!");
        } finally {
            TCPServer t = new TCPServer();
            t.connect(PORT);
        }

    }

    private void insertDB() {
        try {
            Connection dbconnect = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/data",
                    "root",
                    "@Root_4076-"
            );
            Statement dbstatement = dbconnect.createStatement();
            dbstatement.executeUpdate("INSERT INTO CLASSES " + "VALUES (" + "'" + data[1] + "'," + "'" + data[2] + "'," + "'" + data[3] + "'," + "'" + data[4] + "'," + "'" + data[5] + "'," + "'" + data[6] + "'," + "'" + data[7] + "'" + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class IncorrectActionException extends Exception {
        public IncorrectActionException(String text) {
            super(text);
        }
    }

    public void add() throws IOException {
        String inputStore = data[2] + data[3] + data[4] + data[5] + data[6] + data[7];
        if (dbStorage.toString().contains(data[1])) {
            try {
                if (dbStorage.get(data[1]).contains(data[6]) && dbStorage.get(data[1]).contains(data[5])) {
                    out.println("TAKEN TIME");
                    throw new IncorrectActionException("Time slot taken for date");
                } else out.println("GOOD");
            } catch (IncorrectActionException e) {
                System.out.println(e.getMessage());
            }
        } else if (dbStorage.containsKey(data[1])) {
            dbStorage.put(data[1], dbStorage.get(data[1]) + ";" + inputStore);
            dbNewAdd.put(data[1], inputStore);
            insertDB();
        } else {
            dbStorage.put(data[1], inputStore);
            dbNewAdd.put(data[1], inputStore);
            insertDB();
        }
        dbNewAdd.clear();
    }
}

