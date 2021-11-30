package com.dragos;

import java.util.StringTokenizer;
import java.util.Date;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;


public class HttpServer implements Runnable {

    protected Socket cs;    //client socket
    static ServerSocket ss;   //server socket

    private static final File WEB_ROOT = new File("C:\\Users\\drago\\IdeaProjects\\myhttpserver\\HTML_files");
    private static final String DEFAULT_FILE = "index.html";
    private static final String FILE_NOT_FOUND = "404_not_found.html";
    private static final String FILE_MAINTENANCE = "maintenance.html";
    private static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    static int serverPort = 0;
    public int conection;
    static int status = 0;

    public int getStateServer() {
        return status;
    }

    public void setClientSocket(Socket clientSocket) {
        this.cs = clientSocket;
    }

    public boolean setPort(int portNr) {
        try (ServerSocket ignored = new ServerSocket(portNr)) {
            serverPort = portNr;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setState(int state) {
        status = state;
    }

    public void setConnectionOK(int x) {
        this.conection = x;
    }

    public String getContentType(String fileReq) {

        if(fileReq.endsWith(".htm") || fileReq.endsWith(".html"))
            return "text/html";
        if(fileReq.endsWith(".css"))
            return "other/css";
        return "text/plain";
    }

    private void fileNotFound(PrintWriter printWriter, OutputStream dataOut) throws IOException {

        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
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

    public byte[] readFileData(File file,int fileLength) throws IOException {

        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];
        try{

            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        }finally{

            if (fileIn != null)
                fileIn.close();
        }
        return fileData;
    }


    public boolean acceptServerPort() {
        try{

            if(serverPort < 8000 || serverPort > 10500) {
                throw new Exception();
            }else {

                ss = new ServerSocket(serverPort);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }


    public void listenClients(){

        while(true){

            HttpServer connection;
            try {

                connection = new HttpServer();
                connection.setClientSocket(ss.accept());
                setConnectionOK(1);
                Thread thread = new Thread(connection);
                thread.start();
            }catch (IOException e) {

                setConnectionOK(0);
            }
        }
    }

    @Override
    public void run() {

        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileReq = null;

        try {

            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            out = new PrintWriter(cs.getOutputStream());
            dataOut = new BufferedOutputStream(cs.getOutputStream());

            String input = in.readLine();
            StringTokenizer parse= new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();

            fileReq = URLDecoder.decode(parse.nextToken().toLowerCase(),"UTF-8");  //get file request

            if(!method.equals("GET") && !method.equals("HEAD"))
            {
                System.out.println("501 Not implemented: " + method);
                writeFileData(METHOD_NOT_SUPPORTED,out,dataOut,"HTTP/1.1 501 Not Implemented");

            }else{

                if(method.equals("GET"))   {             //GET method -> return content

                    if(status == 1){

                        if(fileReq.endsWith("/"))
                            {
                                fileReq += DEFAULT_FILE;
                            }

                    writeFileData(fileReq,out,dataOut,"HTTP/1.1 200 OK");   //return content
                }else if(status == 2) {

                    fileReq = FILE_MAINTENANCE;
                    writeFileData(fileReq,out,dataOut,"HTTP/1.1 200 OK");

                }else if(status == 3){

                    // close  server
                    in.close();
                    out.close();
                    dataOut.close();
                    cs.close();
                }
                }
            }
        }catch(FileNotFoundException fnfe)
        {
            try{

                fileNotFound(out,dataOut);
                System.out.println("NOT FOUND: " + fileReq + " not found!");
            }catch(IOException ioe){

                System.err.println("+ File not found.  "+ioe.getMessage());
            }
        }catch(IOException ioe){

            System.err.println("+ Server error : "+ioe);
        }finally{
            try{

                in.close();
                out.close();
                dataOut.close();
                cs.close();
            }catch(Exception e)
            {
                System.err.println("+ Error closing stream:"+e.getMessage());
            }
        }
    }

    public void writeFileData(String nameOfFileReq,PrintWriter hearderOut,OutputStream bodyOut,String headerText)
    {
        File file = new File(WEB_ROOT,nameOfFileReq);
        int fileLength = (int)file.length();
        String content = getContentType(file.getName());

        String inputString = "";
        Charset charset = StandardCharsets.US_ASCII;
        byte[] fileData =  inputString.getBytes(charset);
        try {
            fileData = readFileData(file,fileLength);
        } catch (IOException e) {
            file = new File(WEB_ROOT,FILE_NOT_FOUND);
            fileLength = (int)file.length();
            content = getContentType(file.getName());
            try {
                fileData = readFileData(file,fileLength);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        if (hearderOut != null) {         //send Header
            hearderOut.println(headerText);
            hearderOut.println("main.Server");
            hearderOut.println("Date" + new Date());
            hearderOut.println("Content type" + content);
            hearderOut.println("Content length" + fileLength);

            hearderOut.println();   //blank line between headers and content
            hearderOut.flush(); //flush character output stream buffer
        }

        try {                           //send data
            bodyOut.write(fileData,0,fileLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bodyOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}