package com.ds.project3.server.nodes;

import com.ds.project3.log.LogManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeB {

    private static String path = "src/main/resources/logs/";

    public static void main(String [] args) throws IOException {
            while(true)
                listen();

    }

    public static void send(String op) throws IOException {
        try (Socket sock = new Socket("localhost", 2021)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(op);
            log(op);

        }
    }

    private static void listen() throws IOException {
        try (ServerSocket servSock = new ServerSocket(2022)) {
            System.out.println("[NODE_B] started listening on 2022");
            Socket sock;
            sock = servSock.accept();
            System.out.println("[TC] Connection Established!");
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String opcall=br.readLine();
            if(opcall.contains("LOCK"))
                log(LogManager.GET_LOCK);
            else if(opcall.contains("PREPARE"))
            {
                log(LogManager.PREPARE_B);
                send(LogManager.PREPARE_B_ACK);
            }
            else if(opcall.contains("COMMIT")){
                log(LogManager.COMMIT_B);
                send(LogManager.COMMIT_ACK_B);
            }
        }
    }

    private static void log(String op) {
        try (FileWriter fw = new FileWriter(path + "NodeA.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(op);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
