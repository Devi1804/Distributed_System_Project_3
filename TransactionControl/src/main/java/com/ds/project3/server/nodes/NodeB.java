package com.ds.project3.server.nodes;

import com.ds.project3.log.LogManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeB {

    private static String path = "src/main/resources/logs/";

    public static void main(String [] args) throws IOException {
        log(LogManager.START);
        listen();
    }

    private static void send(){

    }

    private static void listen() throws IOException {
        try (ServerSocket servSock = new ServerSocket(2023)) {
            System.out.println("[NODE_B] started listening on 2023");
            Socket sock;
            sock = servSock.accept();
            System.out.println("[TC] Connection Established!");
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            System.out.println("data value received from tx controller: "+Integer.parseInt(br.readLine()));
            log(LogManager.GET_LOCK);
        }
    }

    private static void log(String op) {
        try (FileWriter fw = new FileWriter(path + "NodeB.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(op);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
