package game;




import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

//多人游戏的server。没有暂停功能。
public class ServerMultiPlayer extends Thread{

    private Selector selector;
    private ServerSocketChannel ssChannel;
    private static final int PORT = 8989;
    public static final int MAX_WORLD_BYTES = 1024 * 10;

    private World world;

    public ServerMultiPlayer(World world){
        this.world = world;

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
            while(selector.select() > 0){
                System.out.println("server 处理一次事件");
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
                        //新建连接后立即向新连接发送world对象
                        ByteBuffer buffer = ByteBuffer.wrap(ByteUtil.toBytes(world));
                        assert buffer.array().length < MAX_WORLD_BYTES;
                        sChannel.write(buffer);

                    }else if(key.isReadable()){
                        //是数据传入事件。
                        handleRead(key);
                    }

                    //删除处理好的事件
                    it.remove();
                }

            }
            sleep(1000/Server.TARGET_FPS);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //处理客户端传来的world对象
    //todo:传来的world对象不能简单地复制，比如不能让被一个玩家杀死的敌人复活。考虑在world中写一个update方法
    private void handleRead(SelectionKey key){
        SocketChannel sChannel = null;
        try{
            sChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(MAX_WORLD_BYTES);
            sChannel.read(buffer);

            byte[] data = buffer.array();
            world = (World) ByteUtil.toObject(ByteBuffer.wrap(data));
            System.out.println("server receive");
            System.out.println("receive world:"+world.toString());

        }catch (Exception e){
            System.out.println("client offline: "+sChannel.socket().getRemoteSocketAddress());
            try {
                key.cancel();
                sChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //向所有client端广播最新的world对象，同步游戏。
    public void broadcastWorld() throws Exception{
        //todo:这个用来检测是否发送的world。需要删除
        world.worldWidth = world.worldWidth-100;

        System.out.println("server broadcast");
        System.out.println("curr world:"+world.toString());

        //序列化world对象
        ByteBuffer buffer;
        buffer = ByteBuffer.wrap(ByteUtil.toBytes(world));
        assert buffer.array().length < MAX_WORLD_BYTES;

        for(SelectionKey key:selector.keys()){
            Channel channel = key.channel();
            if(channel instanceof SocketChannel){
                try{
                    ((SocketChannel) channel).write(buffer);
                }catch (Exception e) {
                    System.out.println("client offline: " + ((SocketChannel) channel).socket().getRemoteSocketAddress());
                    try{
                        key.cancel();
                        ((SocketChannel) channel).close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

            }
        }
    }

}
