package com.dragos;

import java.io.*;
import java.net.*;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Files;
import java.util.StringTokenizer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;


public class HttpServer extends Thread {

    protected Socket cs;    //client socket

    private static final File webroot = new File("./HTML_files");
    private static final String default_page = "home.html";
    private static final String FILE_NOT_FOUND= "404_not_found.html";
    static final int PORT = 8080;

    private static String foundPath = "";

    private String getContentType(String fileReq) {

        if(fileReq.endsWith(".htm") || fileReq.endsWith(".html"))
            return "text/html";
        if(fileReq.endsWith(".css"))
            return "other/css";
        return "text/plain";

    }

    private void fileNotFound(PrintWriter printWriter, OutputStream dataOut) throws IOException {

        File file = new File(webroot, FILE_NOT_FOUND);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);

        printWriter.println("HTTP/1.1 404 File Not Found");
        printWriter.println("Server: Java HTTP Server");
        printWriter.println("Date: " + new Date());
        printWriter.println("Content-type: " + content);
        printWriter.println("Content-length: " + fileLength);
        printWriter.println();
        printWriter.flush();

        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();

    }

    private String parseSpaces(String fileReq) {

        if (fileReq.contains("%20")) {

            fileReq = fileReq.replace("%20", " ");
        }

        return fileReq;
    }


    private static void searchFileTree(String fileReq) throws IOException {

        Path root = Paths.get(webroot.toString());

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().contains(fileReq))
                    foundPath = file.toString();
                return FileVisitResult.CONTINUE;
            }
        }
        );
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {

        System.out.println("File: " + file);
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {

            fileIn = new FileInputStream(file);
            fileIn.read(fileData);

        } finally {

            if (fileIn != null)
                fileIn.close();

        }

        return fileData;

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

    private HttpServer(Socket clientSocet) {

        cs = clientSocet;
        start();

    }

    public void run() {

        System.out.println("New Communication Thread Started");
        PrintWriter out = null;
        BufferedReader in = null;
        BufferedOutputStream dataOut = null;
        String fileReq = null;

        try {

            out = new PrintWriter(cs.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));

            dataOut = new BufferedOutputStream(cs.getOutputStream());

            String inputLine = in.readLine();

            StringTokenizer tokens = new StringTokenizer(inputLine);
            String method = tokens.nextToken().toUpperCase();
            fileReq = parseSpaces(tokens.nextToken().toLowerCase());

            if (method.equals("GET")) { //GET method -> return content

                if (fileReq.endsWith("/")) {
                    fileReq += default_page;
                }

                File file = new File(webroot, fileReq);
                int fileLength = (int) file.length();
                String content = getContentType(fileReq);

                byte[] fileData = readFileData(file, fileLength);

                out.println("HTTP/1.1 200 OK");
                out.println("Server: Java HTTP Server");
                out.println("Date: " + new Date());
                out.println("Content-type: " + content);
                out.println("Content-length: " + fileLength);
                out.println();  //blank line between headers and content
                out.flush();    //flush character output stream buffer

                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
            }

            out.close();
            in.close();
            cs.close();     //close the client socket

        } catch (FileNotFoundException fnfe) {
            try {

                fileNotFound(out, dataOut);
            } catch (IOException ioe) {

                System.err.println("Error with FILE NOT FOUND exception : " + ioe.getMessage());
            }
        } catch (IOException e) {

            System.err.println("Communication Server Error");
            System.exit(1);
        }
    }
}