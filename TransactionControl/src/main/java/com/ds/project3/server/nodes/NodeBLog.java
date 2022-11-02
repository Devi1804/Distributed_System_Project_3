package com.ds.project3.server.nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.ds.project3.log.LogManager;

public class NodeBLog implements Runnable{

    Thread runner;
   public NodeBLog() {
      this.runner = new Thread(this);
      this.runner.start();
   }

   @Override
    public void run() {
        Socket sock = null;
        while(true){
            try {
                sock = new Socket("localhost", 2031);
                PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
                pw.println(2033);
                sock.close();
                ServerSocket servSock = new ServerSocket(2033);
                sock = servSock.accept();
                InputStreamReader ip = new InputStreamReader(sock.getInputStream());
                BufferedReader br = new BufferedReader(ip);
                String str = br.readLine();
                NodeB.data = Integer.parseInt(str);
                NodeB.log(LogManager.COMMIT_B);
                System.out.println("[NODE B] data value:"+NodeB.data);
                sock.close();
                NodeB.log(LogManager.UNLOCK);
                break;

            }catch(Exception e){}
        }
    }
}

