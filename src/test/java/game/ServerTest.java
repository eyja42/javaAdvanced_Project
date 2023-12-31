package game;

import org.junit.Assert;
import org.junit.Test;

public class ServerTest {

    @Test
    public void test() throws Exception{
        System.out.println("---ServerTest---");
        World world = new World("");
        Player player = new Player(world);
        Server server = new Server(world,player);

        EnemyFactory enemyFactory = server.getEnemyFactoryTest();
        ThreadEnemyAI enemyAI = server.getThreadEnemyAITest();

        Assert.assertNotNull(enemyFactory);
        Assert.assertNotNull(enemyAI);
        server.pause();
        Assert.assertTrue(enemyFactory.pause);
        Assert.assertTrue(enemyAI.pause);

        server.resume();
        Assert.assertFalse(enemyFactory.pause);
        Assert.assertFalse(enemyAI.pause);

        server.close();
        Assert.assertTrue(enemyFactory.exit);
        Assert.assertTrue(enemyAI.exit);
        System.out.println("---ServerTest passed---");
    }

}