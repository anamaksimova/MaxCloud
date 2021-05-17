package com.maximova.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public Server(){
        ExecutorService service = Executors.newFixedThreadPool(4);
        try (ServerSocket server = new ServerSocket(6789)) {
            server.accept();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}