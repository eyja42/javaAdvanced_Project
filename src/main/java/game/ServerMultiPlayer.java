package game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

//多人游戏的server。没有暂停功能。
public class ServerMultiPlayer extends Thread implements Multi{

    private Selector selector;
    private ServerSocketChannel ssChannel;
    private static final int PORT = 11451;
    public static final int MAX_WORLD_BYTES = 1024 * 1000;
    public volatile boolean exit = false;
    //世界对象同步频率。
    public static final int TARGET_FPS = 80;

    public World world;
    public volatile int curPlayerId;

    public ServerMultiPlayer(World world){
        synchronized (world){
            this.world = world;
        }

        try{
            selector = Selector.open();
            ssChannel = ServerSocketChannel.open();
            //绑定端口
            ssChannel.bind(new InetSocketAddress(PORT));
            //设置为非阻塞模式
            ssChannel.configureBlocking(false);
            ssChannel.register(selector, java.nio.channels.SelectionKey.OP_ACCEPT);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    //开启服务器监听。
    public void run(){
        try{
            while(selector.select() > 0 && !exit){
//                System.out.println("server 处理一次事件");
                //获取所有的就绪事件
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                //处理所有事件
                while(it.hasNext()){
                    java.nio.channels.SelectionKey key = it.next();
                    //处理事件
                    if(key.isAcceptable()) {
                        //是新建连接事件
                        java.nio.channels.SocketChannel sChannel = ssChannel.accept();
                        sChannel.configureBlocking(false);
                        sChannel.register(selector, java.nio.channels.SelectionKey.OP_READ);
                        //立即发送当前world，完成同步
                        sendWorld(sChannel);

                    }else if(key.isReadable()){
                        //是数据传入事件。
                        handleRead(key);
                    }

                    //删除处理好的事件
                    it.remove();
                }
            }

        }catch (Exception e){
//            e.printStackTrace();
            System.out.println("server interrupted");
        }
    }

    //处理客户端传来的world对象
    private void handleRead(SelectionKey key){
        SocketChannel sChannel = (SocketChannel) key.channel();
        try{
            ByteBuffer buffer = ByteBuffer.allocate(MAX_WORLD_BYTES);
            int len = 0;
            if((len = sChannel.read(buffer)) > 0){
                buffer.flip();
                String str = new String(buffer.array(), 0, len, StandardCharsets.UTF_8);
                synchronized (world){
                    world.updateServer(World.fromJson(str),curPlayerId);
                }
                buffer.clear();
            }

            //接收后向该client同步
            sendWorld(sChannel);
        }catch (Exception e){
            System.out.println("handleRead client failed: "+sChannel.socket().getRemoteSocketAddress());
            //客户端断开连接
            try {
                key.cancel();
                sChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //向所有client端广播最新的world对象，同步游戏。
    public void broadcastWorld(){

//        System.out.println("server broadcast");
//        System.out.println("curr world:"+world.toString());

        ByteBuffer buffer = ByteBuffer.allocate(MAX_WORLD_BYTES);
        synchronized (world){
            buffer.put(world.toJson().getBytes());
        }
        buffer.flip();

        for(SelectionKey key:selector.keys()){
            Channel channel = key.channel();
            if(channel instanceof SocketChannel){
                try{
                    while(((SocketChannel) channel).isConnectionPending()){
                    }
                    ((SocketChannel) channel).write(buffer);
                    continue;
                }catch (Exception e) {
                    System.out.println("connect client failed: " + ((SocketChannel) channel).socket().getRemoteSocketAddress());
                }

            }
        }
    }

    //向一个client发送world对象
    public void sendWorld(SocketChannel channel){
        ByteBuffer buffer = ByteBuffer.allocate(MAX_WORLD_BYTES);
        synchronized (world){
            buffer.put(world.toJson().getBytes());
        }
        buffer.flip();
        try{
            channel.write(buffer);
        }catch (Exception e){
            System.out.println("sendWorld failed: "+channel.socket().getRemoteSocketAddress());
        }
    }

    public void setCurPlayerId(int curPlayerId) {
        this.curPlayerId = curPlayerId;
    }
}
