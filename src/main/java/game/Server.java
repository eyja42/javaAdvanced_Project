package game;


import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 服务器类。负责处理游戏逻辑，维护各服务线程。
 *
 */
public class Server {
    private static EnemyFactory enemyFactory;
    private static ThreadEnemyAI threadEnemyAI;
    public static int TARGET_FPS = 120;

    private final Object enemyFactoryLock;
    private final Object enemyAILock;

    //向各客户端通信的ServerSocket
    private static final int PORT = 1145;
    ServerSocketChannel serverSocketChannel;

    //游戏暂停标志。为true时还可能是各线程未启动
    public boolean pause = true;


    public Server(World world,Player player) throws Exception {
        enemyFactoryLock = new Object();
        enemyAILock = new Object();
        enemyFactory = new EnemyFactory(world, enemyFactoryLock);
        threadEnemyAI = new ThreadEnemyAI(world, enemyAILock);

        enemyFactory.start();
        threadEnemyAI.start();
    }


    public void pause(){
        enemyFactory.pause = true;
        threadEnemyAI.pause = true;
    }

    public void resume() {
        synchronized (enemyFactoryLock){
            enemyFactory.pause = false;
            enemyFactoryLock.notify();
        }
        synchronized (enemyAILock){
            threadEnemyAI.pause = false;
            enemyAILock.notify();
        }
    }

    public void close(){
        enemyFactory.exit = true;
        threadEnemyAI.exit = true;

        //唤醒线程，让它们能够正常退出
        resume();
    }

    protected void finalize() {
        enemyFactory.exit = true;
        threadEnemyAI.exit = true;

        resume();
    }

    //测试用方法
    public EnemyFactory getEnemyFactoryTest(){
        return enemyFactory;
    }
    //测试用方法
    public ThreadEnemyAI getThreadEnemyAITest(){
        return threadEnemyAI;
    }
}
