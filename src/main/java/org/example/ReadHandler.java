package org.example;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ReadHandler implements CompletionHandler<Integer, Map<String, Object>> {


    @Override
    public void completed(Integer result, Map<String, Object> attachment) {
        System.out.println(result);
        if(result!=-1) {
            AsynchronousSocketChannel client = (AsynchronousSocketChannel) attachment.get("client");
            ByteBuffer buffer = (ByteBuffer) attachment.get("buffer");
            String msg = new String(buffer.array(), StandardCharsets.UTF_8);
            buffer.flip();
            System.out.println(msg);
            client.read(buffer, attachment, this);
        }
    }

    @Override
    public void failed(Throwable exc, Map<String, Object> attachment) {

    }
}
