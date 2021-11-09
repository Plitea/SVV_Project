package com.Tests;
import com.dragos.HttpServer;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;


    public class HttpServerTest{


        private HttpServer httpserver;
        private Socket cs;
        private ServerSocket ss;


        @Before
        public void setUp() {
            ss = null;

            try {
                
                ss = new ServerSocket(8080);
                
                try {
                        System.out.println("Waiting for connection on port: " + 8080);
                        cs = ss.accept();
                        httpserver = new HttpServer(cs);

                } catch (IOException e) {

                    System.err.println("Server failed to accept connection.");
                    System.exit(1);

                }

            } catch (IOException e) {

                System.err.println("Error listeing on port on port: " + 8080);
                System.exit(1);

            } finally {

                try {
                    
                    ss.close();
                    cs.close();
                    
                } catch (IOException e) {

                    System.err.println("Could not close port: " + 8080);
                    System.exit(1);

                }
            }
        }

        @Test
        public void testGetContentType() {
            assertEquals("Function does not return text/html", "text/html", httpserver.getContentType("home.html"));
            assertEquals("Function does not return other/css", "other/css", httpserver.getContentType("home.css"));
            assertEquals("Function does not return text/plain", "text/plain", httpserver.getContentType("text"));
        }

        @Test
        public void testReadFileData() throws IOException {
            File WEB_ROOT = new File("./HTML_files");
            File file = new File(WEB_ROOT, "home.html");
            assertNotNull("File was not found", httpserver.readFileData(file, (int)file.length()));
        }


    }
