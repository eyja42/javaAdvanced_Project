package game;

import com.raylib.java.Raylib;

import static com.raylib.java.core.input.Keyboard.KEY_EQUAL;

//测试类。用于测试多人游戏的网络通信（服务端）。
public class StartServer{
    static int screenWidth = 1200;
    static int screenHeight = 800;
    static ServerMultiPlayer server;

    public static void main(String[] args) {
        Raylib rlj = new Raylib();
        rlj.core.InitWindow(screenWidth, screenHeight, "server");
        rlj.core.SetTargetFPS(Server.TARGET_FPS);
        rlj.core.SetExitKey(KEY_EQUAL);

        World world = new World("src/main/resources/map/map.json");
        Enemy e1 = new Enemy(world,10,10);
        serverStart(rlj, world, null);
        System.out.println("server closed");
    }

    public static void serverStart(Raylib rlj, World world, Player player){
        server = new ServerMultiPlayer(world);
        server.start();

        try{
            PlayTheme.startGame(rlj, server.world, player,true,server);
        }catch (Exception e){
            e.printStackTrace();
        }
        server.exit = true;
        server.interrupt();
    }
}
