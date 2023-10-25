package com.sim.elkSim.websocket;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.*;

@Component
@Service
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer {
    // 用于记录当前在线连接数的静态变量
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    // concurrent包的线程安全set
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    // 与某个客户端的链接会话
    private Session session;
    private String sid = "";

    /*
     *
     * 连接建立成功调用的方法
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);
        this.sid = sid;
        addOnlineCount();
        try {
            sendMessage("conn_success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();

    }

    @OnMessage
    public void OnMessage(String message, Session session) {
        HashSet<String> sids = new HashSet<>();
        for (WebSocketServer item : webSocketSet) {
            sids.add(item.sid);
        }
        try {
            sendMessage("user" + this.sid + "send Message:" + message, sids);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public static void sendMessage(String message, HashSet<String> toSids) throws IOException {
        for (WebSocketServer item : webSocketSet) {
            item.sendMessage(message);
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static int getOnlineCount() {
        return onlineCount.get();
    }

    public static void addOnlineCount() {
        onlineCount.getAndIncrement();
    }

    public static void subOnlineCount() {
        onlineCount.getAndDecrement();
    }

    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
        return webSocketSet;
    }

}
