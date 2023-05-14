package com.study.im.codec;


import com.alibaba.fastjson.JSONObject;
import com.study.im.codec.proto.MessagePack;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * @author: Chackylee
 * @description:
 **/
public class WebSocketMessageEncoder extends MessageToMessageEncoder<MessagePack> {

    private static Logger log = LoggerFactory.getLogger(WebSocketMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack msg, List<Object> out)  {

        try {
            String s = JSONObject.toJSONString(msg);
            ByteBuf byteBuf = Unpooled.directBuffer(8+s.length());
            byte[] bytes = s.getBytes();
            byteBuf.writeInt(msg.getCommand());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            out.add(new BinaryWebSocketFrame(byteBuf));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}