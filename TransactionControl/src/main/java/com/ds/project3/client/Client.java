package com.ds.project3.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    private static int data;

    public static void main(String[] args) throws IOException {
        data = 10;
        send();
        listen();
    }
    public static void send() throws IOException {
        try (Socket sock = new Socket("localhost", 2021)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(data);

        }
    }
    public static void listen() throws IOException{
        try (ServerSocket servSock = new ServerSocket(2020)) {
            Socket sock;
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String str = br.readLine();
            System.out.println("data value received from tx controller: "+Integer.parseInt(str));
        }
    }
}
