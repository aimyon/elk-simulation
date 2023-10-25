package com.sim.elkSim.core.utils;

import com.sim.elkSim.websocket_io.SocketIOServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;

public class SocketUtils {
    private SocketUtils() {
    }

    /**
     * socket以对象形式推送
     * @param title
     * @param obj
     */
    public static void sendMsg(String title,Object obj){
        Iterator<Map.Entry<String, SocketIOClient>> iterator = SocketIOServiceImpl.clientMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, SocketIOClient> next = iterator.next();
            String key = next.getKey();
            SocketIOClient value = next.getValue();
            SocketAddress remoteAddress = value.getRemoteAddress();

        }
        SocketIOServiceImpl.clientMap.forEach((key, val) ->
                val.sendEvent(title, obj)
        );

    }

    public static void sendMsg(BaseResp baseResp){
        String title = baseResp.getCmd();
        BaseResp resp = (BaseResp) baseResp.getData();;

        Iterator<Map.Entry<String, SocketIOClient>> iterator = SocketIOServiceImpl.clientMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, SocketIOClient> next = iterator.next();
            String key = next.getKey();
            SocketIOClient value = next.getValue();

        }
        String string = JSONObject.toJSONString(resp);

        SocketIOServiceImpl.clientMap.forEach((key, val) ->
                val.sendEvent(title, resp)
        );
    }

    public static void sendMsg(BaseResp baseResp,SocketIOClient client){
        String title = baseResp.getCmd();
        BaseResp resp = (BaseResp) baseResp.getData();
        String string = JSONObject.toJSONString(resp);
        client.sendEvent(title,resp);
    }
    /**
     * socket以字符串形式推送
     * @param title
     * @param obj
     */

    public static void sendMsgString(String title,Object obj){
        SocketIOServiceImpl.clientMap.forEach((key, val) ->
                val.sendEvent(title, JSON.toJSONString(obj))
        );
    }

    public static void sendMsg(Object obj){
        SocketIOServiceImpl.clientMap.forEach((key, val) ->
                sendMsg(BaseResp.ok("DATA", BaseResp.ok(obj, "ENT_INFO")),val)
        );
    }
}
