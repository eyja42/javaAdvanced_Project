package game;

import com.raylib.java.Raylib;

import java.util.Scanner;

import static com.raylib.java.core.input.Keyboard.KEY_EQUAL;

//测试类。用于测试多人游戏的网络通信（客户端）。
public class StartClient {
    static int screenWidth = 1200;
    static int screenHeight = 800;
    static ClientMultiPlayer client;

    public static void main(String[] args) {
        Raylib rlj = new Raylib();
        rlj.core.InitWindow(screenWidth, screenHeight, "client");
        rlj.core.SetTargetFPS(Server.TARGET_FPS);
        rlj.core.SetExitKey(KEY_EQUAL);


        clientStart(rlj, null);
        System.out.println("client closed");
    }

    public static void clientStart(Raylib rlj, Player player){
        try{
            client = new ClientMultiPlayer();
            client.start();
            while(client.world==null){
                Thread.sleep(50);
            }

            PlayTheme.startGame(rlj, client.world, player,true,client);

            client.exit = true;
            client.interrupt();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
