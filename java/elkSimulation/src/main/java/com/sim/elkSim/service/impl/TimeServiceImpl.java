//package com.aircas.elkSim.service.impl;
//
//import com.aircas.elkSim.core.utils.BaseResp;
//import com.aircas.elkSim.core.utils.SocketUtils;
//import com.aircas.elkSim.service.TimeService;
//import com.aircas.elkSim.time.Time;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//@Service
//public class TimeServiceImpl implements TimeService {
//    public static HashMap<String, Time> timeMap = Maps.newHashMap();
//
//    public static volatile boolean pauseButton = true;//默认true, 回放开始置为false,回放结束置为true,暂停置为true
//    public static volatile boolean playTimeButton = false;//回放，默认为false
//
//    public static volatile boolean backButton = false;
//    public static volatile boolean forwardButton = false;
//
//    public static volatile Integer timeSpeed;
//
//    private final AsyncService asyncService;
//
//    private final PlayBackService playBackService;
//
//    private final ExternalController externalController;
//
//    @Autowired
//    YlPlanMapper ylPlanMapper;
//
//
//    @Autowired
//    public TimeServiceImpl(ExternalController externalController, AsyncService asyncService, PlayBackService playBackService) {
//        this.externalController = externalController;
//        this.asyncService = asyncService;
//        this.playBackService = playBackService;
//    }
//
//    @Override
//    public BaseResp playTime(String ylCode, String startTime, Integer speed) {
//        playTimeButton = true;//开始回放
//        if (!pauseButton) {
//            return BaseResp.ok();
//        }
//        pauseButton = false;
//        timeSpeed = speed;
//
//        TransferInfo transferInfo = new TransferInfo();
//        DataInfo dataInfo = new DataInfo();
//        dataInfo.setBid("01120103");
//        dataInfo.setDid("01170100");
//        dataInfo.setSid("01120100");
//        if (Objects.isNull(timeMap.get(ylCode))) {
//            Time initTime = new Time();
//            initTime.setStartTime(startTime);
//            initTime.setNowTime(startTime);
//            initTime.setSpeed(speed);
//            timeMap.put(ylCode, initTime);
//        }
//        Calendar instance = Calendar.getInstance();
//        try {
//            instance.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeMap.get(ylCode).getNowTime()));
//        } catch (Exception e) {
//            return BaseResp.ok("想定未发送", 404);
//        }
//        SocketUtils.sendMsg("DATA",BaseResp.ok(null, "PLAY"));
//        // 异步播放时间
//        asyncService.playTime(ylCode, instance, dataInfo, transferInfo);
//        return BaseResp.ok();
//    }
//
//    @Override
//    public BaseResp pauseTime() {
//        pauseButton = true;
//        SocketUtils.sendMsg("DATA",BaseResp.ok(null, "PAUSE"));
//        return BaseResp.ok();
//    }
//
//    @Override
//    public BaseResp backTime() {
//        if(!backButton){
//            backButton = true;
//        }
//        SocketUtils.sendMsg("DATA",BaseResp.ok(null, "BACK"));
//        return BaseResp.ok();
//    }
//
//    @Override
//    public BaseResp forwardTime() {
//        if(!forwardButton){
//            forwardButton = true;
//        }
//        SocketUtils.sendMsg("DATA",BaseResp.ok(null, "FORWARD"));
//        return BaseResp.ok();
//    }
//
//    @Override
//    public BaseResp finishedTime(String ylCode) {
//        //播放结束，状态置默认值
//        clearTime(ylCode);
////        QueryWrapper<YlPlan> wrapperBs = new QueryWrapper<>();
////        wrapperBs.eq("\"YLCODE\"",ylCode);
////        ylPlanMapper.delete(wrapperBs);
//        SocketUtils.sendMsg("DATA",BaseResp.ok(null, "FINISHED"));
//        return BaseResp.ok();
//    }
//
//    /**
//     * 清除回放状态
//     * @param ylCode
//     */
//    private void clearTime(String ylCode){
//        TimeServiceImpl.timeMap.clear();
//        playTimeButton = false;//回放结束
//        pauseButton = true;
//        //回放结束，置为初始状态
//        backButton = false;
//        forwardButton = false;
//        AsyncService.newplayButton = false;
//    }
//    @Override
//    public BaseResp resetTime(String ylCode) {
//        pauseButton = true;
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        SocketUtils.sendMsg("DATA",BaseResp.ok(null, "RESET"));
//        TransferInfo transferInfo = new TransferInfo();
//        DataInfo dataInfo = new DataInfo();
//        Calendar instance = Calendar.getInstance();
//        Time time = timeMap.get(ylCode);
//        try {
//            instance.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time.getStartTime()));
//        } catch (Exception e) {
//            return BaseResp.ok("想定未发送", 404);
//        }
//        Map<String, Long> msg = Maps.newHashMap();
//        msg.put("train_time", instance.getTime().getTime() / 1000);
//        dataInfo.setMsg(msg);
//        transferInfo.setData(dataInfo);
//        String tranStr = JSON.toJSONString(transferInfo);
//        String tranStrUp = tranStr.toUpperCase();
//        String tranMsg = tranStrUp.replace("DATA", "data");
//        externalController.transMessage(tranMsg, false);
//        // 重置时间
//        time.setNowTime(time.getStartTime());
//        timeMap.put(ylCode, time);
//        // 重新初始化场景
//        playBackService.initPlayBackScene(ylCode);
//        return BaseResp.ok();
//    }
//
//    @Override
//    public BaseResp speed(Integer speed) {
//        timeSpeed = speed;
//        SocketUtils.sendMsg("DATA",BaseResp.ok(speed, "SPEED"));
//        return BaseResp.ok();
//    }
//}
