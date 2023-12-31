package game;

import com.raylib.java.Raylib;

import static com.raylib.java.core.input.Keyboard.KEY_EQUAL;

//测试类。用于测试多人游戏的网络通信（服务端）。
public class StartServer {
    static int screenWidth = 1600;
    static int screenHeight = 960;

    public static void main(String[] args) {
        Raylib rlj = new Raylib();
        rlj.core.InitWindow(screenWidth, screenHeight, "game");
        rlj.core.SetTargetFPS(120);
        rlj.core.SetExitKey(KEY_EQUAL);

        World world = new World("");
        serverStart(rlj, world, null);
    }

    public static void serverStart(Raylib rlj, World world, Player player){
        ServerMultiPlayer server = new ServerMultiPlayer(world);
        server.start();

        try{
            PlayTheme.startGame(rlj, world, player,false);
        }catch (Exception e){
            e.printStackTrace();
            server.stop();
        }
    }
}
