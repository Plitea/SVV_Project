package com.dragos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServerMain extends HttpServer{
    public HttpServerMain(Socket clientSocet) {
        super(clientSocet);
    }

    public static void main(String[] args){
        ServerSocket ss = null;

        try {

            ss = new ServerSocket(PORT);
            System.out.println("Socket connection created.");

            try {
                while (true) {

                    System.out.println("Waiting for connection on port: " + PORT);
                    new HttpServer(ss.accept()); //stuck here until recieving a connection

                }

            } catch (IOException e) {

                System.err.println("Server failed to accept connection.");
                System.exit(1);

            }

        } catch (IOException e) {

            System.err.println("Error listeing on port on port: " + PORT);
            System.exit(1);

        } finally {

            try {

                assert ss != null;
                ss.close();   //closing the server socket connection if it's null
            } catch (IOException e) {
                System.err.println("Could not close port: " + PORT);
                System.exit(1);

            }
        }
    }
}
