package com.maximova.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable{
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())
            )
        { while (true){
            String command = in.readUTF();

            if ("upload".equals(command)){
                uploading(out, in);
            }
            if ("download".equals(command)){
                downloading(out, in);
            }
            if ("check".equals(command)){
                checking(out, in);
            }
            if ("exit".equals(command)){

                out.writeUTF("DONE");
                disconnected();
                System.out.printf("Client %s is disconnected correctly\n", socket.getInetAddress());
                break;
            }
            System.out.println(command);
           // out.writeUTF(command); с этой строкой не загружался подряд второй файл
        }

        }  catch (SocketException socketException) {
            System.out.printf("Client %s is disconnected \n", socket.getInetAddress());
            socketException.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
        //проверка на существование файла
    private void checking(DataOutputStream out, DataInputStream in) {

        try{
            File file = new File("server/" + in.readUTF()); //read file name
            if (!file.exists()) {
                System.out.println("File is not found");
                out.writeUTF("No file");
        } else if (file.exists()) {
                System.out.println("File is found");
                out.writeUTF("File ready");
            }
    } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private void downloading(DataOutputStream out, DataInputStream in) throws IOException {

            try {
                File file = new File("server/" + in.readUTF()); //read file name

                long fileLength=file.length();
                Thread t1 = new Thread(() -> {
                    try {
                        FileInputStream fis  = new FileInputStream(file);
                        out.writeLong(fileLength);
                        int read = 0;
                        byte[] buffer = new byte[8 * 1024];

                        while ((read=fis.read(buffer)) != -1){
                            out.write(buffer,0,read);
                        }
                        out.flush();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                Thread t2 = new Thread(() -> {
                    try {
                        out.writeUTF("File is downloaded");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                t1.start();
                t1.join();
                t2.start();
                t2.join();

            }   catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch ( IOException e) {
                out.writeUTF("Download failed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    private void uploading(DataOutputStream out, DataInputStream in) throws IOException {
        try {
            File file = new File("server/" + in.readUTF()); //read file name

            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            long size = in.readLong();
            byte[] buffer = new byte[8 * 1024];
            for (int i = 0; i < (size + (8 * 1024 - 1)) / buffer.length; i++) {
                int read = in.read(buffer);
                fos.write(buffer, 0, read);
            }
            fos.close();
            out.writeUTF("OK");
        }    catch ( Exception e) {
            out.writeUTF("WRONG");
        }
    }

    private void disconnected() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
