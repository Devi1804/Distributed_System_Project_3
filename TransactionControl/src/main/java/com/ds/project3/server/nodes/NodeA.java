package com.ds.project3.server.nodes;

import com.ds.project3.log.LogManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeA {

    private static String dir = System.getProperty("user.dir");
    private static String path = "/src/main/resources/logs/";

    public static void main(String[] args) throws IOException, InterruptedException {
        File dirpath = new File(path);
        if (!dirpath.exists())
            dirpath.mkdir();
        while (true)
            listen();

    }

    public static void send(String op) throws IOException, InterruptedException {
        try (Socket sock = new Socket("localhost", 2021)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(op);
            log(op);
        }
    }

    private static void listen() throws IOException, InterruptedException {
        Socket sock = null;
        System.out.println("[NODE_A] started listening on 2022");

            try (ServerSocket servSock = new ServerSocket(2022)) {
                sock = servSock.accept();
                System.out.println("[TC] Connection Established!");
                InputStreamReader ip = new InputStreamReader(sock.getInputStream());
                BufferedReader br = new BufferedReader(ip);
                String opcall = br.readLine();
                sock.close();
                System.out.println(opcall);
                if (opcall.contains("LOCK")) {
                    sock.close();
                    log(LogManager.GET_LOCK);
                }
                else if (opcall.contains("PREPARE")) {
                    System.out.println("received prepare");
                    log(LogManager.PREPARE_A);
                    send(LogManager.PREPARE_A_ACK);
                    sock.close();
                } else if (opcall.contains("COMMIT")) {
                    log(LogManager.COMMIT_A);
                    send(LogManager.COMMIT_ACK_A);
                    sock.close();
                }
            }
    }

    private static void log(String op) {
        try (FileWriter fw = new FileWriter(dir + path + "NodeA.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(op);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
