package com.shark.aio.data.conditionData.service;

import com.alibaba.fastjson.JSONObject;
import com.shark.aio.data.conditionData.mapper.ConditionMapping;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author DaHuaJia
 * @Description 环保设备数据处理类，处理设备的上传数据
 * @Date 2022-10-09 11:08:57
 **/
@Slf4j
@Component
public class HJ212ServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private ConditionMapping conditionMapping;
    /**
     * 定义一个HashMap，用于保存所有的channel和设备ID的对应关系。
     */
    private static Map deviceInfo = new HashMap(64);

    /**
     * 消息读取
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        System.out.println("收到HJ212协议数据为 ===> " + msg);

        //CRC校验
        if(!HJ212MsgUtils.checkData((String)msg).equals("error")){
            // 解析物联网设备发来的数据
            JSONObject data = HJ212MsgUtils.dealMsg1((String) msg);
            //存储数据
            if (data != null){
                System.out.println("设备消息解析JSON结果：" + data.toJSONString());
                try {
                    SimpleDateFormat DataFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    String documentPath = "D:\\项目\\AIO\\data\\" + DataFormat.format(new Date()) ;//根据日期或某个算法自动生成
                    String filePath = documentPath + "\\conditionData.txt";
                    File document = new File(documentPath);
                    if(!document.exists()){
                        document.mkdirs();
                    }
                    File file = new File(filePath);

                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file,true));
                    out.write((data.toJSONString()+"\n").getBytes());
                    out.flush();
                    out.close();
//            ConditionFileEntity conditionFileEntity = null;
//            conditionFileEntity.setFileUrl(path);
//            conditionMapping.insertFileUrl(conditionFileEntity);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        }


    }

    /***
     * 超时关闭socket 连接
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读超时";
                    break;
                case WRITER_IDLE:
                    eventType = "写超时";
                    break;
                case ALL_IDLE:
                    eventType = "读写超时";
                    break;
                default:
                    eventType = "设备超时";
            }
            log.warn(ctx.channel().id() + " : " + eventType + "---> 关闭该设备");
            ctx.channel().close();
        }
    }

    /**
     * 异常处理, 出现异常关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("========= 链接出现异常！exceptionCaught.");
        ctx.fireExceptionCaught(cause);
        // 将通道从deviceInfo中删除
        deviceInfo.remove(ctx.channel().id());
        // 关闭通道链接，节省资源
        ctx.channel().close();
    }

    /**
     * 每加入一个新的链接，保存该通道并写入上线日志。该方法在channelRead方法之前执行
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // 获取设备ID
        String deviceId = "abc";
        // 将该链接保存到deviceInfo
        deviceInfo.put(ctx.channel().id(), deviceId);
        log.warn("========= " + ctx.channel().id() + "设备加入链接。");
    }

    /**
     * 每去除一个新的链接，去除该通道并写入下线日志
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // 将通道从deviceInfo中删除
        deviceInfo.remove(ctx.channel().id());
        log.warn("========= " + ctx.channel().id() + "设备断开链接。");
    }

}
