package com.study.im.codec;

import com.study.im.codec.proto.Message;
import com.study.im.codec.utils.ByteBufToMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义解码器
 *
 * @author lx
 * @date 2023/05/01
 */
public class MessageDecoder extends ByteToMessageDecoder {


    private static final int HEADER_LENGTH = 28;

    /**
     * 解码
     *
     * @param channelHandlerContext 通道处理程序上下文
     * @param byteBuf               字节缓冲区
     * @param out                  列表
     * @throws Exception 异常
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        // 规定：
        //-------------------------------------------------------------------------------------------------------------------------------
        //|   4byte  |    4byte   |     4byte    |  4byte |      4byte    |       4byte   |    4byte       |   nbyte   |   nbyte
        //--------------------------------------------------------------------------------------------------------------------------------
        //| Command  |  version  |  clientType  |  appId  |   messageType |  imeiLength  |  contentLength |   imei      | contnet
        //------------------------------------------------------------------------------------------------------------------------------


        // 如果可读字节数小于28，说明不是一条完整的消息，直接返回
        if (byteBuf.readableBytes() < HEADER_LENGTH) {
            return;
        }


        Message message = ByteBufToMessageUtils.transition(byteBuf);
        if (message == null) {
            return;
        }

        out.add(message);


    }
}
