package com.sescity.vbase.service.bean;

public class VbaseRedisMsg {

    public static VbaseRedisMsg getInstance(String fromId, String toId, String type, String cmd, String serial, String content){
        VbaseRedisMsg vbaseRedisMsg = new VbaseRedisMsg();
        vbaseRedisMsg.setFromId(fromId);
        vbaseRedisMsg.setToId(toId);
        vbaseRedisMsg.setType(type);
        vbaseRedisMsg.setCmd(cmd);
        vbaseRedisMsg.setSerial(serial);
        vbaseRedisMsg.setContent(content);
        return vbaseRedisMsg;
    }

    private String fromId;

    private String toId;
    /**
     * req 请求, res 回复
     */
    private String type;
    private String cmd;

    /**
     * 消息的ID
     */
    private String serial;
    private Object content;

    private final static String requestTag = "req";
    private final static String responseTag = "res";

    public static VbaseRedisMsg getRequestInstance(String fromId, String toId, String cmd, String serial, Object content) {
        VbaseRedisMsg vbaseRedisMsg = new VbaseRedisMsg();
        vbaseRedisMsg.setType(requestTag);
        vbaseRedisMsg.setFromId(fromId);
        vbaseRedisMsg.setToId(toId);
        vbaseRedisMsg.setCmd(cmd);
        vbaseRedisMsg.setSerial(serial);
        vbaseRedisMsg.setContent(content);
        return vbaseRedisMsg;
    }

    public static VbaseRedisMsg getResponseInstance() {
        VbaseRedisMsg vbaseRedisMsg = new VbaseRedisMsg();
        vbaseRedisMsg.setType(responseTag);
        return vbaseRedisMsg;
    }

    public static VbaseRedisMsg getResponseInstance(String fromId, String toId, String cmd, String serial, Object content) {
        VbaseRedisMsg vbaseRedisMsg = new VbaseRedisMsg();
        vbaseRedisMsg.setType(responseTag);
        vbaseRedisMsg.setFromId(fromId);
        vbaseRedisMsg.setToId(toId);
        vbaseRedisMsg.setCmd(cmd);
        vbaseRedisMsg.setSerial(serial);
        vbaseRedisMsg.setContent(content);
        return vbaseRedisMsg;
    }

    public static boolean isRequest(VbaseRedisMsg vbaseRedisMsg) {
        return requestTag.equals(vbaseRedisMsg.getType());
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
