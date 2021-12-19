package com.dragos;

import java.io.IOException;

import static com.dragos.HttpServer.*;

public class GUI {

    public static void main(String[] args) throws InterruptedException {
        new Frame();
        HttpServer.setServerSocket(8080);
        System.out.println("Connection Socket Created");
        while(serverIsOpen) {

            Thread.sleep(1000);
            if(serverIsRunning) {
                try {
                    System.out.println("Waiting for Connection.\n");
                    new HttpServer(ss.accept());

                } catch (IOException e) {
                    System.err.println("Accept failed.");
                }
            }
        }
        try {
            ss.close();
        } catch (IOException e) {
            System.err.println("Could not close port: " + serverPort);
            System.exit(1);
        }
    }
}
