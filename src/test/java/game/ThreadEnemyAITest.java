package game;

import com.raylib.java.Raylib;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ThreadEnemyAITest {
    private static final int TEST_ROUND = 100;


    @Test
    public void run() throws Exception {
        System.out.println("---ThreadEnemyAITest---");
        Raylib rlj = new Raylib();
        World world = new World();
        Object threadLock = new Object();
        ThreadEnemyAI enemyAI = new ThreadEnemyAI(world,threadLock);

        //测试在窗口未初始化时，调用run是否会造成异常。
        enemyAI.start();
        sleep(1000);
        enemyAI.exit = true;
        sleep(10);

        //当nearestPlayer与e接近重合时，检查移动是否会出现错误
        ThreadEnemyAI enemyAI2 = new ThreadEnemyAI(world,threadLock);
        rlj.core.InitWindow(100,100,"ThreadEnemyAITest");
        Enemy e1 = new Enemy(world,100,100);
        Player player = new Player(world);
        player.moveTo(100,100);
        enemyAI2.start();
        sleep(1000);
        Assert.assertEquals(e1.x,player.x,0.1f);
        Assert.assertEquals(e1.y,player.y,0.1f);
        enemyAI2.exit = true;

        //检查敌人移动的距离是否等于speed
//        World world2 = new World();
//        ThreadEnemyAI enemyAI3 = new ThreadEnemyAI(world2,threadLock);
//        player = new Player(world2);
//        Enemy e2 = new Enemy(world2,500,500);
//        Random random = new Random();
//        for(int i=0;i<TEST_ROUND;i++){
//            player.moveTo(random.nextFloat()*1000,random.nextFloat()*1000);
//        }

        System.out.println("---ThreadEnemyAITest passed---");
    }
}