package game;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class PlayerTest {

    @Test
    public void moveTest() {
        System.out.println("---player move---");
        World world = new World("");
        Player p = new Player(world);
        p.moveTo(500,500);
        assertEquals(p.x,500,0.1f);
        assertEquals(p.y,500,0.1f);
        Random r = new Random();
        float speed = r.nextFloat()*10;
        p.speed = speed;
        p.moveUp();
        assertEquals(p.y,500-speed,0.1f);
        p.moveDown();
        assertEquals(p.y,500,0.1f);
        p.moveLeft();
        assertEquals(p.x,500-speed,0.1f);
        p.moveRight();
        assertEquals(p.x,500,0.1f);
        System.out.println("---player move passed---");
    }


    @Test
    public void attack() {
        System.out.println("---player attack---");
        World world = new World();
        Player p = new Player(world);
        p.attack(0);
        System.out.println("---player attack passed---");
    }

    @Test
    public void causeDamage() {
        System.out.println("---player causeDamage---");
        World world = new World();
        Player p = new Player(world);
        Enemy e = new Enemy(world);
        float health = e.health;
        p.causeDamage(e);
        assertEquals(e.health,health-p.atk-p.weapon.atk,0.1f);
        System.out.println("---player causeDamage passed---");
    }
}