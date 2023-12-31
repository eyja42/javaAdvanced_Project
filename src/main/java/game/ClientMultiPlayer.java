package game;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ClientMultiPlayer extends Thread implements Multi{
    private Selector selector;
    private static final int PORT = 11451;
    private SocketChannel socketChannel;
    public volatile boolean exit = true;

    public World world;
    public volatile int curPlayerId;

    public ClientMultiPlayer() throws Exception{
            //创建选择器
            selector = Selector.open();
            //连接服务器
            socketChannel = SocketChannel.open(new InetSocketAddress(PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

    }

    //开启客户端线程。阻塞式，收到一个服务器传来的消息后进行一次处理
    public void run(){
        try{
            while(selector.select() > 0){
//                System.out.println("client 处理一次事件");
                //获取所有的就绪事件
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                //处理所有事件
                while(it.hasNext()){
                    java.nio.channels.SelectionKey key = it.next();
                    //处理事件
                    if(key.isReadable()){
                        //是数据传入事件。
                        handleRead(key);
                    }
                    //清空就绪事件
                    it.remove();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    //读取收到的world对象，更新本地游戏
    private void handleRead(SelectionKey key) throws Exception{
        SocketChannel sChannel = (SocketChannel) key.channel();
        try {
            //读取数据
            ByteBuffer buffer = ByteBuffer.allocate(ServerMultiPlayer.MAX_WORLD_BYTES);
            int len = 0;
            if ((len = sChannel.read(buffer)) > 0) {
                //读取到数据
                String str = new String(buffer.array(), 0, len, StandardCharsets.UTF_8);
                if (world == null) {
                    world = World.fromJson(str);
                } else {
                    synchronized (world) {
                        world.updateClient(World.fromJson(str), curPlayerId);
                    }
                }
                buffer.clear();
            }

            //向服务端同步数据
            sendWorld();
        }catch (Exception e){
            System.out.println("handleRead client failed:"+sChannel.socket().getRemoteSocketAddress());
            try{
                sChannel.close();
                key.cancel();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    //发送本地world对象到服务器
    public void sendWorld(){
        try{
            //将world序列化
            ByteBuffer buffer = ByteBuffer.allocate(ServerMultiPlayer.MAX_WORLD_BYTES);
            synchronized (world){
                buffer.put(world.toJson().getBytes(StandardCharsets.UTF_8));
            }
            buffer.flip();
            //检测socketChannel是否可用，可用则发送数据
            while(socketChannel.isConnectionPending()){
            }
            socketChannel.write(buffer);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurPlayerId(int curPlayerId){
        this.curPlayerId = curPlayerId;
    }

    public World getWorld(){
        synchronized (world){
            return world;
        }
    }
}
