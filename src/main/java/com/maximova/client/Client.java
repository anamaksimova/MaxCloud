package com.maximova.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Swing client - File Storage
 * Client command: upload filename|download filename
 */

public class Client {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    public Client() throws IOException {
        //init
       socket = new Socket("localhost", 6789);
       out = new DataOutputStream(socket.getOutputStream());
       in= new DataInputStream(socket.getInputStream());

       //create form
        JFrame frame = new JFrame("Окно загрузки");
        frame.setSize(300,300);

        JPanel panel = new JPanel(new GridLayout(2,1));

        JButton btnSend = new JButton("SEND");
        JTextField textField=new JTextField();
        btnSend.addActionListener(a ->{
            String[] cmd= textField.getText().split(" ");
            if ("upload".equals(cmd[0])){
                sendFile(cmd[1]);
            } else if ("download".equals(cmd[0])){
                getFile(cmd[1]);
            }
            
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                sendMessage("exit");
            }
        });

        panel.add(textField);
        panel.add(btnSend);
        frame.add(panel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    }

    private void getFile(String filename) {
        //TODO downloading
    }

    private void sendFile(String filename) {
        try{
            File file = new File("client/" + filename);
            if(!file.exists()){
                throw new FileNotFoundException();
            }

            long fileLength=file.length();
            FileInputStream fis = new FileInputStream(file);
            out.writeUTF("upload");
            out.writeUTF(filename);
            out.writeLong(fileLength);

            int read = 0;
            byte[] buffer = new byte[8*1024];
            while ((read=fis.read(buffer)) != -1){
                out.write(buffer,0,read);
            }
            out.flush();
            String status = in.readUTF();
            System.out.println("Sending status: "+ status);
        } catch (FileNotFoundException e) {
            System.err.println("File not found - /client/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * message sending
     * @param message String
     */

    private void sendMessage(String message) {
        try {
            out.writeUTF(message);
            String command = in.readUTF();
            System.out.println(command);
            if ("DONE".equals(command)){
                disconnected();
                System.out.printf("Client %s is disconnected correctly\n", socket.getInetAddress());
            }

        } catch (EOFException eofException) {
            System.err.println("Reading command error from " + socket.getInetAddress());
            eofException.printStackTrace();
        }
        catch (SocketException socketException) {
            System.out.printf("Client %s is disconnected \n", socket.getInetAddress());
            socketException.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnected() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }

}
