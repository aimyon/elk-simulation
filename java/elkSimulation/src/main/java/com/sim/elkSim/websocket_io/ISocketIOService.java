package com.sim.elkSim.websocket_io;

/**
 * @Author: mnie
 * @Description:
 * @Date: Create in 11:17 上午 2021/8/13
 */
public interface ISocketIOService {
    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void stop();

    /**
     * 推送信息给指定客户端
     *
     * @param userId:     客户端唯一标识
     * @param msgContent: 消息内容
     */
    void pushMessageToUser(String userId, String msgContent);
}
