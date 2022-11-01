package com.ds.project3.server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TransactionControllerLog implements Runnable{

    Thread t;
    TransactionControllerLog() {
      t = new Thread();
      t.start();
   }
   public void run() {
      try {
         listen();
      }  catch (Exception e) {
    }
    }
private static void listen() throws IOException {
        Socket sock = null;
        while(true){
        try (ServerSocket servSock = new ServerSocket(2031)) {
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String str = br.readLine();
            TransactionController.data = Integer.parseInt(str);
            sock.close();
            sock = new Socket("localhost", 2022);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(TransactionController.data);
            sock.close();
        }catch(Exception e){}
        }
    }
}
