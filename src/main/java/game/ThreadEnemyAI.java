package game;

import com.raylib.java.raymath.Raymath;
import com.raylib.java.raymath.Vector2;
import java.util.Iterator;

public class ThreadEnemyAI extends Thread{

    public volatile boolean exit = false;
    public volatile boolean pause = false;
    private final World world;
    private final Object threadLock;

    public void run(){
        while(!exit){
            synchronized (threadLock){
                if(pause){
                    try {
                        threadLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Iterator<Enemy> iter = world.enemies.iterator();
                while (iter.hasNext()) {
                    Enemy e = iter.next();
                    if(e.tick()){
                        //首先判定哪个玩家离自己最近
                        Iterator<Player> playerIter = world.players.iterator();
                        Player nearestPlayer = playerIter.next();
                        float nearestDist = Raymath.Vector2Distance(e.getPosition(),nearestPlayer.getPosition());
                        while (playerIter.hasNext()){
                            Player nxtPlayer = playerIter.next();
                            float nxtDist = Raymath.Vector2Distance(e.getPosition(),nxtPlayer.getPosition());
                            if(nxtDist<nearestDist){
                                nearestDist = nxtDist;
                                nearestPlayer = nxtPlayer;
                            }
                        }
                        //计算位移。如果位移小于速度则原地不动.
                        float dist = Raymath.Vector2Distance(e.getPosition(),nearestPlayer.getPosition());
                        if(dist > e.speed) {
                            float angle = Raymath.Vector2Angle(e.getPosition(),nearestPlayer.getPosition());
                            Vector2 shift = new Vector2(e.speed,0);
                            shift = Raymath.Vector2Rotate(shift,angle);
                            e.moveBy(shift);
                        }
                        //攻击判定
                        e.tryAttack(nearestPlayer);

                    }
                }
            }
            try {
                sleep(1000/Server.TARGET_FPS);
            } catch (InterruptedException ignored){
            }
//            System.out.println("EnemyAI run");
        }

    }

    public ThreadEnemyAI(World world,Object threadLock){
        this.world = world;
        this.threadLock = threadLock;
    }
}
