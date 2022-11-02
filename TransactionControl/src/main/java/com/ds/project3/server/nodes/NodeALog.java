package com.ds.project3.server.nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.ds.project3.log.LogManager;

public class NodeALog implements Runnable{

    Thread t;
    NodeALog() {
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
            sock = new Socket("localhost", 2031);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(2032);
            sock.close();
        try (ServerSocket servSock = new ServerSocket(2032)) {
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String str = br.readLine();
            NodeA.data = Integer.parseInt(str);
            NodeB.log(LogManager.COMMIT_B);
            System.out.println("[NODE B] data value:"+NodeB.data+1);
            sock.close();
            break;
            
        }catch(Exception e){}
        }
    }
}
