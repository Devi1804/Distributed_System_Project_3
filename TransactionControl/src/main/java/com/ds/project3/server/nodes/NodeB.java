package com.ds.project3.server.nodes;

import com.ds.project3.log.LogManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

public class NodeB {

    private static String dir = System.getProperty("user.dir");
    private static String path = "/src/main/resources/logs/";

    private static Timestamp timestamp;
    public static int data = 10;

    private static long allowedDelay = 5050;

    public static void main(String[] args) throws IOException, InterruptedException {
        File dirpath = new File(path);
        if (!dirpath.exists())
            dirpath.mkdir();
        String lastLogOp = readlogOp();
        System.out.println(lastLogOp);
        System.out.println(lastLogOp.isEmpty());
        if(!lastLogOp.isEmpty()){
            new NodeBLog();
        }
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
        System.out.println("[NODE_B] started listening on 2023");
        try (ServerSocket servSock = new ServerSocket(2023)) {

            sock = servSock.accept();
            System.out.println("[TC] Connection Established!");
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String opcall = br.readLine();
            System.out.println(opcall);
            sock.close();
            if (opcall.contains("GET_LOCK")) {
                sock.close();
                log(LogManager.GET_LOCK);
                timestamp = new Timestamp(System.currentTimeMillis());
            } else if (opcall.contains("PREPARE")) {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                long diff = ts.getTime() - timestamp.getTime();
                System.out.println("Time difference is: " + diff);
                if (diff <= allowedDelay) {
                    log(LogManager.PREPARE_B);
                    send(LogManager.PREPARE_B_ACK);
                }
                sock.close();
            } else if (opcall.contains("COMMIT")) {
                log(LogManager.COMMIT_B);
                data +=1;
                System.out.println("[NODE B] data value:"+data);
                send(LogManager.COMMIT_ACK_B);
                sock.close();
                log(LogManager.UNLOCK);
            }else {
                log(LogManager.UNLOCK);
            }
        } finally {
            sock.close();
        }
    }

    private static String readlogOp() {
        String lastOp = "";
        try {
            FileReader fr = new FileReader(dir + path + "NodeB.txt");
            BufferedReader br = new BufferedReader(fr);

            String line = "";
            while ((line = br.readLine()) != null) {
                lastOp = line;
            }
            br.close();
        } catch (IOException e) {
        }
        return lastOp;
    }

    public static void log(String op) {
        try (FileWriter fw = new FileWriter(dir + path + "NodeB.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(op);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
