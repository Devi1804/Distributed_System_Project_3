package com.ds.project3.server.controller;

import com.ds.project3.log.LogManager;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TransactionController {
    private static String dir = System.getProperty("user.dir");
    private static String path = "/src/main/resources/logs/";
    public static int data = 10;

    public static void main(String[] args) throws IOException, InterruptedException {
        TransactionControllerLog tr = new TransactionControllerLog();
        File dirpath = new File(dir + path);
        if (!dirpath.exists())
            dirpath.mkdir();
        String lastLogOp = readlogOp();
        System.out.println(lastLogOp);
        switch (lastLogOp) {
            case "":
                listen();
                temp();
                break;
            case "GET_LOCK":
                temp();
                break;
            case "LogManager.PREPARE_A":
                temp();
                break;
            case "LogManager.PREPARE_B":
                temp();
                break;
            case "LogManager.PREPARE_A_ACK":
                temp();
                break;
            case "LogManager.PREPARE_B_ACK":
                docommit();
                break;
            case "LogManager.COMMIT_A":
                docommit();
                break;
            case "LogManager.COMMIT_A_ACK":
                docommit();
                break;
            case "LogManager.COMMIT_B":
                docommit();
                break;
            case "LogManager.COMMIT_B_ACK":
                docommit();
                break;
            default:
                listen();
                temp();
        }
        Thread.sleep(200000);
    }

    private static void listen() throws IOException {
        Socket sock = null;
        try (ServerSocket servSock = new ServerSocket(2021)) {
            log(LogManager.START);
            System.out.println("[TC] started listening on 2021");
            sock = servSock.accept();
            System.out.println("[TC] Connection Established!");
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            String str = br.readLine();
            data = Integer.parseInt(str);
            sendDataGetLock(data,LogManager.GET_LOCK);
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            sock.close();

        }
    }

    private static void temp() throws IOException, InterruptedException {
        int countPrep = sendPrepare();
        if (countPrep == 2) {
            int countCommit = sendCommit();
            data = data + 1;
        } else{
            sendDataGetLock(data,LogManager.UNLOCK);
        }
        sendresponse();
    }

    private static void docommit() throws IOException {
        data = data + 1;
        sendresponse();
    }

    private static void sendresponse() throws IOException {
        // Send commit/abort value command to node A
        try

        (Socket sock = new Socket("localhost", 2020)) {
            log(LogManager.UNLOCK);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(data);
        }

    }

    private static void sendDataGetLock(int data,String op) throws IOException, InterruptedException {

        // write data to own log
        log(op);
        // Send data to node A
        try (Socket sock = new Socket("localhost", 2022)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(op);
            sock.close();
        }

        Thread.sleep(1000);
        // Send data to node B
        try (Socket sock = new Socket("localhost", 2023)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(LogManager.GET_LOCK);
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int sendPrepare() throws IOException, InterruptedException {

        int count = 0;
        System.out.println("[TC] Preparing to send the message..");
        // Send data to node A
        Thread.sleep(4000);
        count = count + NodeA("LogManager.PREPARE_A", "LogManager.PREPARE_A_ACK");
        // Send data to node B
        count = count + NodeB("LogManager.PREPARE_B", "LogManager.PREPARE_B_ACK");
        return count;
    }

    private static int sendCommit() throws IOException, InterruptedException {

        int count = 0;
        System.out.println("[TC] Committing A..");
        // Send data to node A
        count = count + NodeA("LogManager.COMMIT_A", "LogManager.COMMIT_A_ACK");
        // Send data to node B
        System.out.println("[TC] Committing B..");
        Thread.sleep(4000);
        count = count + NodeB("LogManager.COMMIT_B", "LogManager.COMMIT_B_ACK");
        return count;
    }

    private static int NodeA(String logop, String logack) throws IOException {
        int count = 0;
        // Send data to node A
        try (Socket sock = new Socket("localhost", 2022)) {
            log(logop);
            System.out.println(logop);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(logop);
        } catch (Exception e) {
            return 0;
        }
        // listen to node A for ack();
        Socket sock = null;
        int timeout = 5050;
        try (ServerSocket servSock = new ServerSocket(2021)) {
            servSock.setSoTimeout(timeout);
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            log(logack);
            System.out.println(logop);
            count += 1;
        } catch (Exception e) {
            return count;
        }
        return count;
    }

    private static int NodeB(String logop, String logack) throws IOException {
        int count = 0;
        try (Socket sock = new Socket("localhost", 2023)) {
            log(logop);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(logop);
            System.out.println(logop);
        } catch (Exception e) {
            return 0;
        }
        // listen to node B for ack();
        Socket sock = null;
        int timeout = 5050;
        try (ServerSocket servSock = new ServerSocket(2021)) {
            servSock.setSoTimeout(timeout);
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            log(logack);
            System.out.println(logop);
            count += 1;
        } catch (Exception e) {
            return count;
        }
        return count;
    }

    private static void log(String op) {
        try (FileWriter fw = new FileWriter(dir + path + "TransactionController.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(op);
        } catch (IOException e) {
        }
    }

    private static String readlogOp() {
        String lastOp = "";
        try {
            FileReader fr = new FileReader(dir + path + "TransactionController.txt");
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
}
