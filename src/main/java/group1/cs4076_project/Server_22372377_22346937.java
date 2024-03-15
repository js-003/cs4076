package group1.cs4076_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Server_22372377_22346937 {

    private HashMap<String, String> dbNewAdd = new HashMap<>();
    private HashMap<String,String> dbStorage = new HashMap<>();
    private static String[] data ;
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static ServerSocket serverSocket;
    private static final int PORT = 1234;
    private static int clientConnections = 0;

    private void connect(int port) throws IOException {
        try {
            clientSocket = serverSocket.accept();
            clientConnections++;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            boolean checker = true;
            dataBase();
            while (checker) {
                String message = in.readLine();
                message = message.replace("{", "").replace("}", "");
                System.out.println("Message received from client: " + clientConnections + "\n" + message.replaceAll("_"," "));
                data = message.split("_");
                switch (data[0]) {
                    case "ADD" -> {
                        add();
                    }
                    case "REMOVE" -> {
                        remove();
                    }
                    case "DISPLAY" -> {
                        display();
                    }
                    case "STOP" -> {
                        checker = false;
                        ServerStop();
                    }
                    case "CHECKING CONNECTION" -> {
                        out.println(serverSocket.isClosed());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client could not connect!");
        }
    }

    private static void ServerStop() throws IOException {
        clientSocket.close();
        serverSocket.close();
    }
    private void dataBase() {
        try {
            Connection dbconnection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/data",
                    "root",
                    "@Root_4076-"
            );
            Statement dbstatement = dbconnection.createStatement();
            ResultSet dbResultSet = dbstatement.executeQuery("SELECT * FROM CLASSES ORDER BY date ASC;");
            while (dbResultSet.next()) {
                String dbReader = dbResultSet.getString("classtype")+"_"+dbResultSet.getString("modulename") + "_" + dbResultSet.getString("modulecode") + "_" + dbResultSet.getString("time") + "_" + dbResultSet.getString("date") + "_" + dbResultSet.getString("roomnumber");
                if (dbStorage.containsKey(dbResultSet.getString("classyear"))) {
                    String tmp = dbStorage.get(dbResultSet.getString("classyear"));
                    dbStorage.put(dbResultSet.getString("classyear"), tmp + ";" + dbReader);
                } else dbStorage.put(dbResultSet.getString("classyear"), dbReader);
            }
        } catch (SQLException e) {
            System.out.println("NO SQL CONNECTION");
            out.println("NO SQL");
        }
        System.out.println("SQL CONNECTED");
        out.println("GOOD SQL");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Opening port...\n");
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Unable to attach to port!");
        } finally {
            Server_22372377_22346937 t = new Server_22372377_22346937();
            t.connect(PORT);
            System.out.printf("Client %d connected!",clientConnections);
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
            System.out.println("SQL PROBLEM");
            out.println("NO SQL");
        }
        System.out.printf("INSERTED Class Name: %s, Class Type: %s, Module Name: %s, Module Code: %s, Time:  %s, Date: %s, Room Number: %s\n", data[1],data[2],data[3],data[4],data[5],data[6],data[7] );
        out.println("GOOD SQL");
    }

    public static class IncorrectActionException extends Exception {
        public IncorrectActionException(String text) {
            super(text);
        }
    }
    private void removeDB(){
        try {
            Connection dbconnect = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/data",
                    "root",
                    "@Root_4076-"
            );
            Statement removeStatement = dbconnect.createStatement();
             removeStatement.execute("DELETE FROM CLASSES WHERE classyear = '" + data[1]+"' AND date = '"+data[3]+"' AND time = '"+data[2]+"';");
        }catch (SQLException e){
            System.out.println("SQL PROBLEM");
            out.println("NO SQL");
        }
        System.out.printf("REMOVED Class Name: %s, Time:  %s, Date: %s\n", data[1],data[3],data[2]);
        out.println("GOOD SQL");
    }

    private void add() throws IOException {
        String inputStore = data[2] +"_"+ data[3] +"_" + data[4] +"_"+ data[5] +"_"+ data[6] +"_"+ data[7];
        if (dbStorage.containsKey(data[1])) {
            try {
                if (dbStorage.get(data[1]).contains(data[6]) && dbStorage.get(data[1]).contains(data[5])) {
                    out.println("TAKEN TIME");
                    throw new IncorrectActionException("Time slot taken for date");
                } else {
                    String[] checker = dbStorage.get(data[1]).replaceAll(";","_").split("_");
                    ArrayList<String> s = new ArrayList<>();
                    for(int i = 2; i<checker.length; i = i+6) {
                        if(!s.contains(checker[i])){
                            s.add(checker[i]);
                        }
                    }
                    if(s.size()<5){
                        out.println("GOOD");
                        dbStorage.put(data[1], dbStorage.get(data[1]) + ";" + inputStore);
                        dbNewAdd.put(data[1], inputStore);
                        insertDB();
                    }else if(s.contains(data[4])){
                        out.println("GOOD");
                        dbStorage.put(data[1], dbStorage.get(data[1]) + ";" + inputStore);
                        dbNewAdd.put(data[1], inputStore);
                        insertDB();
                    } else{
                        out.println("FIVE");
                        throw new IncorrectActionException("This course already has five modules");
                    }
                }
            } catch (IncorrectActionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            out.println("GOOD");
            dbStorage.put(data[1], inputStore);
            dbNewAdd.put(data[1], inputStore);
            insertDB();
        }
        dbNewAdd.clear();
    }
    private void remove() throws IOException {
        if (dbStorage.toString().contains(data[1])) {
            try {
                if (!dbStorage.get(data[1]).contains(data[3]) || !dbStorage.get(data[1]).contains(data[2])) {
                    out.println("INVALID TIME");
                    throw new IncorrectActionException("The time-slot for the given date is empty");
                }else {
                    out.println("GOOD");
                    dbStorage.clear();
                    removeDB();
                }
                dataBase();
            }  catch (IncorrectActionException e) {
                System.out.println(e.getMessage());
            }
        }else{
            out.println("INVALID CLASS NAME");
        }
    }

    private void display(){
        out.println("");
        String output = "";
        if (dbStorage.containsKey(data[1])){
            System.out.println("\nClasses for " +data[1]);
            out.println("GOOD");
            output =  dbStorage.get(data[1]);
            output = output.replaceAll(";", "\n").replaceAll("=", "\n").replaceAll(", ", "\n").replaceAll("_"," ");
            System.out.println(output);
        }else {
            out.println("INVALID CLASS NAME");
            System.out.println("There is no such class name and year in the database!");
        }
    }
}

