package com.ds.project3.server.controller;

import com.ds.project3.log.LogManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TransactionController {
    private static String path = "src/main/resources/logs/";

    public static void main(String[] args) throws IOException{
        log(LogManager.START);
        listen();
        send();
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
            sendData(Integer.parseInt(str));
       }
    }
    private static void send() throws IOException{
        //Send Prepare command to node A
        try (Socket sock = new Socket("localhost", 2022)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(LogManager.PREPARE_A);
        }
        //Send prepare command to node B
        try (Socket sock = new Socket("localhost", 2023)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(LogManager.PREPARE_B);
        }
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

    private static void sendData(int data) throws IOException{

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
}
