package com.sim.elkSim.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
public class ServerSocketConfig {

    private static Logger log = LoggerFactory.getLogger(ServerSocketConfig.class);

    public static ServerSocket serverSocket = null;

    private static final ThreadPoolExecutor threadPoll = new ThreadPoolExecutor(15, 15, 10L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Scheduled(initialDelay = 1000, fixedRate = Long.MAX_VALUE)
    public void socketCreate() {
        try {
            serverSocket = new ServerSocket(10000);
            log.info("Socket服务端开启");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("接收到客户端socket" + socket.getRemoteSocketAddress());
                threadPoll.execute(new ServerReceiveThread(socket));
            }
        } catch (IOException e) {
            log.info("Socket服务启动异常");
            e.printStackTrace();
        }
    }


}
