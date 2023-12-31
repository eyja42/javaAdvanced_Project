package game;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class EnemyTest {

    @Test
    public void staticTest() {
        System.out.println("---staticTest---");
        Assert.assertEquals(Enemy.ENEMY_SIZE,48);
        Assert.assertEquals(Enemy.FACING_LEFT,0);
        Assert.assertEquals(Enemy.FACING_RIGHT,1);


        World world = new World();
        Enemy e = new Enemy(world);
        Enemy e2 = new Enemy(world,100,100);
        assertEquals(e2.x,100,0.1f);
        assertEquals(e2.y,100,0.1f);
        System.out.println("---staticTest passed---");
    }

    @Test
    public void moveBy(){
        System.out.println("---enemy moveBy---");
        World world = new World();
        Enemy e = new Enemy(world);
        e.moveBy(0.2f,0.4f);
        System.out.println("---enemy moveBy passed---");
    }

    @Test
    public void takeDamage() {
        System.out.println("---takeDamage---");
        World world = new World();
        Enemy e = new Enemy(world);
        float health = e.health;
        e.takeDamage(1);
        assertEquals(e.health,health-1,0.1f);
        System.out.println("---takeDamage passed---");
    }


    @Test
    public void testTick() {
        System.out.println("---testTick---");
        World world = new World();
        Enemy e = new Enemy(world);
        assertEquals(e.getTickTest(),0);
        e.tick();
        assertEquals(e.getTickTest(),1);
        System.out.println("---testTick passed---");
    }

    @Test
    public void testTryAttack() {
        System.out.println("---enemy testTryAttack---");
        World world = new World();
        Enemy e = new Enemy(world);
        Player player = new Player(world);
        e.moveTo(100,100);
        player.moveTo(100,100);
        float health = player.health;
        //没到攻击帧，不会造成伤害
        e.tryAttack(player);
        assertEquals(health-e.atk,player.health,0.1f);
        assertEquals(e.getAtkCDTest(),0);
        System.out.println("---enemy testTryAttack passed---");
    }

    @Test
    public void causeDamage() {
        System.out.println("---enemy causeDamage---");
        World world = new World();
        Enemy e = new Enemy(world);
        Random r = new Random();
        float atk = r.nextFloat()*10;
        e.atk = atk;
        Player player = new Player(world);
        float pHealth = player.health;
        e.causeDamage(player);
        assertEquals(player.health,pHealth-atk,0.1f);
        System.out.println("---enemy causeDamage passed---");
    }

    @Test
    public void testTakeDamage() {
        System.out.println("---testTakeDamage---");
        World world = new World();
        Enemy e = new Enemy(world);
        Random r = new Random();
        float val = r.nextFloat()*5;
        System.out.println("health val: "+val);
        e.health = val;
        float dmg = r.nextFloat()*5;
        e.takeDamage(dmg);
        assertEquals(e.health,val-dmg,0.1f);
    }

    @Test
    public void isDead() {
        System.out.println("---isDead---");
        World world = new World();
        Enemy e = new Enemy(world);
        Random r = new Random();
        float val = r.nextFloat()*3;
        e.health = val;
        e.takeDamage(val+r.nextFloat());
        assertTrue(e.isDead());
        System.out.println("---isDead passed---");
    }
}