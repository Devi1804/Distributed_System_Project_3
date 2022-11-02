package com.ds.project3.server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TransactionControllerLog implements Runnable{

    Thread runner;
    public TransactionControllerLog() {
        this.runner = new Thread(this);
        this.runner.start();
    }
   @Override
    public void run()  {
        Socket sock;
        while(true){
        try (ServerSocket servSock = new ServerSocket(2031)) {
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String str = br.readLine();
            int port = Integer.parseInt(str);
            sock.close();
            sock = new Socket("localhost", port);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(TransactionController.data);
            sock.close();
            
        }catch(Exception e){}
        }
    }
}
