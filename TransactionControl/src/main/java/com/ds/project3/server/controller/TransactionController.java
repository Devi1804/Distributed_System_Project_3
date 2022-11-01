package com.ds.project3.server.controller;

import com.ds.project3.log.LogManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TransactionController {
    private static String dir = System.getProperty("user.dir");
    private static String path = "/src/main/resources/logs/";
    private static int data = 10;

    private static final ScheduledExecutorService delayedTask =
            Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws IOException, InterruptedException {

        File dirpath = new File(path);
		if(!dirpath.exists())
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
            case "COMMIT_A":
                docommit();
                break;
            case "COMMIT_ACK_A":
                docommit();
                break;
            case "COMMIT_B":
                docommit();
                break;
            case "COMMIT_ACK_B":
                docommit();
                break;
            default:
                listen();
                temp();
        }
        deletefile();
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
            sendDataGetLock(data);
        }catch(Exception e){
            System.out.println(e.toString());
        } 
        finally {
            sock.close();

        }
    }
    private static void temp() throws IOException, InterruptedException {
        int countPrep = sendPrepare();
        if (countPrep == 2) {
            int countCommit = sendCommit();
            if (countCommit == 2) {
                data = data + 1;
            }
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

    private static void sendDataGetLock(int data) throws IOException, InterruptedException {

        // write data to own log
        log(LogManager.GET_LOCK);
        // Send data to node A
        try (Socket sock = new Socket("localhost", 2022)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(LogManager.GET_LOCK);
            sock.close();
        }

        Thread.sleep(1000);
        // Send data to node B
        try (Socket sock = new Socket("localhost", 2023)) {
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(LogManager.GET_LOCK);
            sock.close();
        }catch (Exception e){
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
        count = count + NodeB("LogManager.PREPARE_B", "LogManager.PREPARE_A_ACK");
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
        }catch (Exception e){
            e.printStackTrace();
        }
        // listen to node A for ack();
        Socket sock = null;
        int timeout = 5000;
        try (ServerSocket servSock = new ServerSocket(2021)) {
            servSock.setSoTimeout(timeout);
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            log(logack);
            count += 1;
        }  catch(SocketTimeoutException e) {
            return count;
        }catch (Exception e) {
            e.printStackTrace();
            return count;
        } finally {
            sock.close();
        }
        return count;
    }

    private static int NodeB(String logop, String logack) throws IOException {
        int count = 0;
        try (Socket sock = new Socket("localhost", 2023)) {
            log(logop);
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
            pw.println(logop);
        }catch (Exception e){
            e.printStackTrace();
        }
        // listen to node B for ack();
        Socket sock = null;
        int timeout = 5000;
        try (ServerSocket servSock = new ServerSocket(2021)) {
            servSock.setSoTimeout(timeout);
            sock = servSock.accept();
            InputStreamReader ip = new InputStreamReader(sock.getInputStream());
            BufferedReader br = new BufferedReader(ip);
            log(logack);
            count += 1;
        } catch(SocketTimeoutException e) {
            return count;
        }catch (Exception e) {
            e.printStackTrace();
            return count;
        } finally {
            sock.close();
        }
        return count;
    }

    private static void log(String op) {
        try (FileWriter fw = new FileWriter(dir+path + "TransactionController.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(op);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readlogOp() {
        String lastOp = "";
        try {
            FileReader fr = new FileReader(dir+ path + "TransactionController.txt");
            BufferedReader br = new BufferedReader(fr);

            String line = "";
            while ((line = br.readLine()) != null) {
                lastOp = line;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastOp;
    }

    private static void deletefile() {
        File file=new File(dir+ path + "TransactionController.txt");
        if(file.exists()){
            return;
        }
        else{
            file.delete();
        }
    }

}
