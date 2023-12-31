package game;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Vector;

import static org.junit.Assert.*;

public class WeaponTest {

    @Test
    public void attack() {
        System.out.println("---weapon attack---");
        World world = new World();
        Player player = new Player(world);
        Weapon weapon = new Weapon(player);
        assertFalse(weapon.isAttacking());
        weapon.attack(0);
        assertTrue(weapon.isAttacking());
        weapon.tick();
        assertEquals(1,weapon.getTickTest());
        System.out.println("---weapon attack passed---");
    }

    @Test
    public void tick() {
        System.out.println("---weapon tick---");
        World world = new World();
        Player player = new Player(world);
        Weapon weapon = new Weapon(player);
        ArrayList<Float> angles = weapon.getAnglesTest();
        ArrayList<Float> dists = weapon.getDistsTest();
        assertEquals(0,weapon.getTickTest());
        weapon.attack(0);
        for(int i=0;i<player.atkSpeed+weapon.atkSpeed;i++){
            weapon.tick();
        }
        assertEquals(0,weapon.getTickTest());
        assertEquals(weapon.getAngleOffset(),angles.get(1),0.01);
        assertEquals(weapon.getDistOffset(),dists.get(1),0.01);
        System.out.println("---weapon tick passed---");
    }
}