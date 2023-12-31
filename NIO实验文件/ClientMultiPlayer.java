package game;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ClientMultiPlayer extends Thread{
    private Selector selector;
    private static final int PORT = 8989;
    private SocketChannel socketChannel;

    private World world;

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
                System.out.println("client 处理一次事件");
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
                }
                //清空就绪事件
                it.remove();
            }

            sleep(1000/Server.TARGET_FPS);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    //读取收到的world对象，更新本地游戏
    private void handleRead(SelectionKey key) throws Exception{
        SocketChannel sChannel = null;
            sChannel = (SocketChannel) key.channel();
            //读取数据
            ByteBuffer buffer = ByteBuffer.allocate(ServerMultiPlayer.MAX_WORLD_BYTES);
            if(sChannel.read(buffer)>0){
                //读取到数据
                buffer.flip();
                byte[] data = buffer.array();
                world = (World) ByteUtil.toObject(ByteBuffer.wrap(data));
                System.out.println("client read world");
                System.out.println("client read world: " + world.toString());
            }

    }


    //发送本地world对象到服务器
    public void sendWorld(){
        try{
            //todo:这个用来检测是否发送的world。需要删除
            world.worldWidth = world.worldWidth+100;

            //将world序列化
            ByteBuffer buffer = ByteBuffer.wrap(ByteUtil.toBytes(world));
            socketChannel.write(buffer);
            System.out.println("client send world");
            System.out.println("client send world: " + world.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
