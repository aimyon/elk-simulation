package com.sim.elkSim.websocket_io;

import com.sim.elkSim.core.utils.BaseResp;
import com.sim.elkSim.core.utils.SocketUtils;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketIOServiceImpl implements ISocketIOService {

    /**
     * 存放已连接的客户端
     */
    public static final Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();
    /**
     * 自定义事件`push_data_event`,用于服务端与客户端通信
     */
    private static final String PUSH_DATA_EVENT = "push_data_event";
    private Logger log = LoggerFactory.getLogger(SocketIOServiceImpl.class);

    @Autowired
    private SocketIOServer socketIOServer;
    // 未创建Ioc容器

    /**
     * Spring IoC容器创建之后，在加载SocketIOServiceImpl Bean之后启动
     */
    @PostConstruct
    private void autoStartup() {
        start();
    }

    /**
     * Spring IoC容器在销毁SocketIOServiceImpl Bean之前关闭,避免重启项目服务端口占用问题
     */
    @PreDestroy
    private void autoStop() {
        stop();
    }


    @Override
    public void start() {
        // 监听客户端连接
        socketIOServer.addConnectListener(client -> {
            // 自定义事件`connected` -> 与客户端通信  （也可以使用内置事件，如：Socket.EVENT_CONNECT）
            String sessionId = client.getSessionId().toString();
            log.info("************ 客户端： " + getIpByClient(client) + " :" + sessionId + " 已连接 ************");
            client.sendEvent("connect", "你成功连接上了哦...");
            SocketUtils.sendMsg(BaseResp.ok("DATA", BaseResp.ok(sessionId, "SOCKET_ID")),client);
            clientMap.put(sessionId,client);
        });

        // 监听客户端断开连接
        socketIOServer.addDisconnectListener(client -> {
            log.info(client.getSessionId() + " *********************** " + "客户端已断开连接");
            clientMap.remove(client.getSessionId().toString());
            client.disconnect();
        });

        // 自定义事件`client_info_event` -> 监听客户端消息
        socketIOServer.addEventListener(PUSH_DATA_EVENT, String.class, (client, data, ackSender) -> {
            // 客户端推送`client_info_event`事件时，onData接受数据，这里是string类型的json数据，还可以为Byte[],object其他类型
            log.info(client.getSessionId() + " ************ 客户端：" + data);
        });

        // 启动服务
        socketIOServer.start();

    }

    @Override
    public void stop() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            socketIOServer = null;
        }
    }

    @Override
    public void pushMessageToUser(String userId, String msgContent) {
        SocketIOClient client = clientMap.get(userId);
        if (client != null) {
            client.sendEvent(PUSH_DATA_EVENT, msgContent);
        }
    }

    /**
     * 获取客户端url中的userId参数（这里根据个人需求和客户端对应修改即可）
     *
     * @param client: 客户端
     * @return: java.lang.String
     */
    private String getParamsByClient(SocketIOClient client) {
        // 获取客户端url参数（这里的userId是唯一标识）
        Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
        List<String> userIdList = params.get("userId");
        if (!CollectionUtils.isEmpty(userIdList)) {
            return userIdList.get(0);
        }
        return null;
    }

    /**
     * 获取连接的客户端ip地址
     *
     * @param client: 客户端
     * @return: java.lang.String
     */
    private String getIpByClient(SocketIOClient client) {
        String sa = client.getRemoteAddress().toString();
        String clientIp = sa.substring(1, sa.indexOf(":"));
        return clientIp;
    }

}
