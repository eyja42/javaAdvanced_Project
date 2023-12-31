package game;

import org.junit.Test;

public class BulletTest {

    @Test
    public void bulletGeneralTest(){
        System.out.println("---bulletGeneralTest---");
        World world = new World();
        Player player = new Player(world);
        Bullet b = new Bullet(player,0f);
        System.out.println("---bulletGeneralTest passed---");
    }

}