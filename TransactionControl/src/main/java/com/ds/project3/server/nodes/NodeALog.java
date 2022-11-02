package com.ds.project3.server.nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.ds.project3.log.LogManager;

public class NodeALog implements Runnable{

    Thread runner;
    public NodeALog() {
        this.runner = new Thread(this);
        this.runner.start();
    }
   public void run() {
        Socket sock = null;
        while(true){
            try {
            sock = new Socket("localhost", 2031);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(2032);
            sock.close();
            ServerSocket servSock = new ServerSocket(2032);
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String str = br.readLine();
            NodeA.data = Integer.parseInt(str);
            NodeA.log(LogManager.COMMIT_A);
            System.out.println("[NODE B] data value:"+NodeA.data);
            sock.close();
            NodeA.log(LogManager.UNLOCK);
            break;
            
        }catch(Exception e){}
        }
    }
}
