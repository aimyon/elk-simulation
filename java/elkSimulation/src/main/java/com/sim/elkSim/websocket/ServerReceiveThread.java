package com.sim.elkSim.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.Socket;

public class ServerReceiveThread implements Runnable {

    private Socket socket;

    private static Logger log = LoggerFactory.getLogger(ServerReceiveThread.class);

    public ServerReceiveThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int read = inputStream.read(bytes);
            String data = new String(bytes, 0, read);
            System.out.println(data);
            socket.close();
        } catch (Exception e) {
            log.info("接收数据异常socket关闭");
            e.printStackTrace();
        } finally {
            log.info("接受异常数据要怎么保留");
        }
    }
}
