package com.shark.aio.conditionData.service;

import com.alibaba.fastjson.JSONObject;
import com.shark.aio.conditionData.mapper.ConditionMapping;
import com.shark.aio.util.Constants;
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


        System.out.println("=============" + (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "=============");
        System.out.println("收到" + ctx.channel().id() + "设备消息 ===> " + msg);

        // 解析物联网设备发来的数据
        JSONObject data = HJ212MsgUtils.dealMsg1((String) msg);
//        JSONObject data2 = HJ212MsgUtils.dealMsg2((String) msg);

        /**
         * 做自己的业务逻辑，分发数据，分析数据，持久化数据等。
         */

        if (data != null){
            System.out.println("设备消息解析JSON结果：" + data.toJSONString());
//        System.out.println("再将JSON数据进行分类：" + data2.toJSONString());
            try {
                String path = Constants.conditionData;//根据日期或某个算法自动生成
                File file = new File(path);
//            if(!file.exists()){
//                file.mkdirs();
//            }
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
//            req.setAttribute("error", "添加文件失败");
//            return Constants.FAILCODE;
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
