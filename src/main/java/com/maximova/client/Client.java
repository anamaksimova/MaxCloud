package com.maximova.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

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
            String message= textField.getText();
            sendMessage(message);
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
    /**
     * message sending
     * @param message String
     */

    private void sendMessage(String message) {
        try {
            out.writeUTF(message);
            String command = in.readUTF();
            System.out.println(command);

        } catch (EOFException eofException) {
            System.err.println("Reading command error from " + socket.getInetAddress());
            eofException.printStackTrace();
        }
        catch (SocketException socketException) {
            System.err.println("Socket error from " + socket.getInetAddress());
           // socketException.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }

}
