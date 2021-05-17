package com.maximova.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
            if ("exit".equals(command)){
                break;
            }
            System.out.println(command);
            out.writeUTF(command);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
