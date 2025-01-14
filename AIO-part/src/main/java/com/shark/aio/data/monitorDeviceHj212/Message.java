package com.shark.aio.data.monitorDeviceHj212;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lbx
 * @date 2023/3/28 - 21:13
 **/
public class Message {
    /**
     * 客户端ID
     */
    private String id;
    /**
     * 数据长度
     */
    private Integer len;

    /**
     * 接收的通讯数据body
     */
    private String content;

    /**
     * 消息类型
     */
    private Integer msgType;

    public Message() {
    }

    public Message(Object object) {
        String str = object.toString();
        JSONObject jsonObject = JSONObject.parseObject(str);
        msgType = Integer.valueOf(jsonObject.getString("msg_type"));
        content = jsonObject.getString("body");
        id = jsonObject.getString("unique_id");
        len = str.length();
    }

    public String toJsonString() {
        return "{" +
                "\"msg_type\": " + msgType + ",\n" +
                "\"body\": " + content +
                "}";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
}
