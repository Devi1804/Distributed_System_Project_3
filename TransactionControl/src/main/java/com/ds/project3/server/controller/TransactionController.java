package com.ds.project3.server.controller;

import com.ds.project3.log.LogManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TransactionController {
    private static String path = "src/main/resources/logs/";
    private static String data;
    public static void main(String[] args) throws IOException{
        log(LogManager.START);
        listen();
    }

    private static void listen() throws IOException{
        try (ServerSocket servSock = new ServerSocket(2021)) {
            log(LogManager.START);
            System.out.println("[TC] started listening on 2021");
            Socket sock = null;
            sock = servSock.accept();
            System.out.println("[TC] Connection Established!");
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String str = br.readLine();
            data=str;
            sendDataGetLock(Integer.parseInt(data));
            int countPrep=sendPrepare();
            if(countPrep==2){
                
                int countCommit=sendCommit();
                if(countCommit==2){
                    data=data+1;
                }
            }
            sendresponse();
       }
    }
    private static void sendresponse() throws IOException{
        //Send commit/abort value command to node A
        try
        
         (Socket sock = new Socket("localhost", 2022)) {
            log("Unlock");
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(data);
            }
            
    }

    

    private static void sendDataGetLock(int data) throws IOException{

        //write data to own log
        log(LogManager.GET_LOCK);
        //Send data to node A
        try (Socket sock = new Socket("localhost", 2022)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(data);
        }
        //Send data to node B
        try (Socket sock = new Socket("localhost", 2023)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(data);
        }
    }

    private static int sendPrepare() throws IOException{

        int count=0;
        //Send data to node A
        count=count+NodeA("LogManager.PREPARE_A","LogManager.PREPARE_A_ACK");
        //Send data to node B
        count=count+NodeB("LogManager.PREPARE_B","LogManager.PREPARE_A_ACK");
        return count;
    }

    private static int sendCommit() throws IOException{

        int count=0;
        //Send data to node A
        count=count+NodeA("LogManager.COMMIT_A","LogManager.COMMIT_A_ACK");
        //Send data to node B
        count=count+NodeB("LogManager.COMMIT_B","LogManager.COMMIT_B_ACK");
        return count;
    }

    private static int NodeA(String logop, String logack) throws IOException{
        int count =0;
        //Send data to node A
        try
        
         (Socket sock = new Socket("localhost", 2022)) {
            log(logop);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(logop);
            }
        //listen to node A for ack();
            try (ServerSocket servSock = new ServerSocket(2021)) {
                Socket sock;
                sock = servSock.accept();
                InputStreamReader ip = new InputStreamReader(sock.getInputStream());
                BufferedReader br = new BufferedReader(ip);
                System.out.println("data value received from tx controller: "+br.read());
                log(logack);                ///add wait for some time to reproduce failure of a Node
                count+=1;
            }
        catch(Exception e){
            return count;
        }
        return count;
    }
    private static int NodeB(String logop, String logack) throws IOException{
        int count=0;
        try (Socket sock = new Socket("localhost", 2023)) {
            log(logop);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(logop);
        }
        //listen to node A for ack();
        try (ServerSocket servSock = new ServerSocket(2021)) {
            Socket sock;
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            System.out.println("data value received from tx controller: "+br.read());
            log(logack);
            count+=1;
        }
        catch(Exception e){
            return count;
        }
        return count;
    }


    private static void log(String op) {
        try (FileWriter fw = new FileWriter(path + "TransactionController.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(op);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
