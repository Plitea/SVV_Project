package com.dragos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class HttpServerMain implements Runnable{
    static int port;
    static boolean runServer = true;
    static String  statusServer;
    static HttpServer server;
    static boolean connectionOk = false;



    public static void main(String[] args) {
        server = new HttpServer();
        while (!connectionOk) {
            choosePort();
        }

        Thread thread = new Thread(new HttpServerMain());
        thread.start();

        try {
            readState();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void readState() throws IOException {
        do {
            System.out.println("Select server state:  [1] -> RUNNING | [2] -> MAINTENANCE | [3] -> STOPPED");
            BufferedReader readerCommand = new BufferedReader(new InputStreamReader(System.in));
            String commandLine = readerCommand.readLine();
            verifyReadedState(commandLine);
        } while (true);
    }

    public static void verifyReadedState(String command) {
        switch (command) {
            case "1":

                server.setState(1);
                statusServer = "RUNNING";
                System.out.println("Connection created on port:" + port);
                System.out.println("Server state: " + statusServer);
                break;
            case "2":

                server.setState(2);
                statusServer = "MAINTENANCE";
                System.out.println("Server state: " + statusServer);
                break;
            case "3":

                server.setState(3);
                statusServer = "STOPPED";
                System.out.println("Server state: " + statusServer);
                break;
        }
    }

    public static void choosePort() {

        System.out.print("Select a port from [8000:10500] -> ");
        BufferedReader readerCommand =  new BufferedReader(new InputStreamReader(System.in));

        try {
            port = Integer.parseInt(readerCommand.readLine());
        } catch (NumberFormatException | IOException e) {
            System.out.println("Invalid port.");
            port =- 1;
        }

        if(server.setPort(port))
        {
            if(server.acceptServerPort())
            {
                System.out.println("The server started.");
                connectionOk = true;
                statusServer = "STOPPED";
            }
        }
    }

    @Override
    public void run() {
        while(runServer) {
            server.listenClients();
        }
    }
}
