package com.sim.elkSim.core.utils;

public class BaseResp {

    private Integer code;
    private String message;
    private Object data;
    private String cmd;

    public static BaseResp ok(){
        BaseResp baseResp = new BaseResp();
        baseResp.setMessage("Success");
        baseResp.setCode(200);
        return baseResp;
    }
    public static BaseResp ok(Object object){
        BaseResp baseResp = new BaseResp();
        baseResp.setMessage("Success");
        baseResp.setCode(200);
        baseResp.setData(object);
        return baseResp;
    }
    public static BaseResp ok(Object object,String cmd){
        BaseResp baseResp = new BaseResp();
        baseResp.setMessage("Success");
        baseResp.setCode(200);
        baseResp.setData(object);
        baseResp.setCmd(cmd);
        return baseResp;
    }
    public static BaseResp ok(String message,Object object,String cmd){
        BaseResp baseResp = new BaseResp();
        baseResp.setMessage(message);
        baseResp.setCode(200);
        baseResp.setData(object);
        baseResp.setCmd(cmd);
        return baseResp;
    }
    public static BaseResp ok(Object object,String message,Integer code){
        BaseResp baseResp = new BaseResp();
        baseResp.setMessage(message);
        baseResp.setCode(code);
        baseResp.setData(object);
        return baseResp;
    }
    public static BaseResp ok(String message,Integer code){
        BaseResp baseResp = new BaseResp();
        baseResp.setMessage(message);
        baseResp.setCode(code);
        return baseResp;
    }

    //用于做发送消息的转换，不做返回
    public static BaseResp ok(String cmd, BaseResp object){
        BaseResp baseResp = new BaseResp();
        baseResp.setCmd(cmd);
        baseResp.setData(object);
        return baseResp;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
