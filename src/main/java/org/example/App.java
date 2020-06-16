package org.example;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class App
{
    public static CopyOnWriteArrayList<AsynchronousSocketChannel> channels = new CopyOnWriteArrayList<>();

    public static void ProcessIncomingMessages() throws InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        while(true) {
            System.out.println("Getting Messages");
            for (AsynchronousSocketChannel channel : channels) {
                try {
                    if(channel.isOpen()) {
                        Integer result = channel.read(buffer).get(1, TimeUnit.SECONDS);
                        if (result != -1) {
                            String message = new String(buffer.array(), StandardCharsets.UTF_8);
                            buffer.flip();
                            buffer.clear();
                            System.out.println(message);
                        } else {
                            channel.close();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Clearing connections");
            for (int i = channels.size()-1;i>=0;i--) {
                var ch = channels.get(i);
                if (!ch.isOpen()) {
                    channels.remove(ch);
                }
            }
            Thread.sleep(1000);
        }
    }

    public static void main( String[] args ) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        System.out.println( "Starting Server" );
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress("127.0.0.1",8080));
        Thread t = new Thread(()->{
            try {
                ProcessIncomingMessages();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        System.out.println("Waiting for clients");
        while(true) {
            AsynchronousSocketChannel worker = server.accept().get();
            channels.add(worker);
            System.out.println("Client connected");
        }
    }
}
