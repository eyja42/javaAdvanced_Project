package game;

import com.raylib.java.raymath.Vector2;

import java.util.Random;

public class EnemyFactory extends Thread {
    public static int MAX_ENEMY_COUNT = 48;
    private final World world;
    private final Random rand;
    //尝试生成怪物的间隔时间。后续可能根据游戏难度设置为动态调整
    private int genTick=500;
    private int tick = 0;
    public volatile boolean exit = false;
    public volatile boolean pause = false;
    private final Object threadLock;

    public void createEnemy(){
        tick++;
        if(tick>=genTick && world.enemies.size()<MAX_ENEMY_COUNT){
            for(int i=0;i<7;i++){
                Enemy e = new Enemy(world);
                //随机生成坐标。
                do {
                    int x = rand.nextInt((int) world.worldWidth);
                    int y = rand.nextInt((int) world.worldHeight);
                    e.moveTo(x, y);
                } while (!isValidPos(e));
            }
            tick=0;
        }
    }

    /**
     * 判定刷怪位置是否有效：不卡在墙里，且不在玩家半径100圆形范围内
     */
    private boolean isValidPos(Enemy e){
        if(world.isCollide(e))  return false;
        for(Player player:world.players){
            if(World.CheckCollisionCircles(e.getPosition(),e.radius,player.getPosition(),100)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void run(){
        while (!exit){
            synchronized (threadLock){
                if(pause){
                    try {
                        threadLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                createEnemy();
            }
            try {
                sleep(1000/Server.TARGET_FPS);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
//            System.out.println("EnemyFactory run");
        }
    }

    public void setGenTick(int genTick){
        this.genTick = genTick;
    }


    public EnemyFactory(World world,Object threadLock){
        this.world = world;
        this.threadLock = threadLock;
        long t = System.currentTimeMillis();
        rand = new Random(t);
    }
}
