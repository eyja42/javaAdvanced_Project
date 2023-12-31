package game;

import com.raylib.java.Raylib;

import java.util.Scanner;

//测试类。用于测试多人游戏的网络通信（客户端）。
public class StartClient {
    public static void main(String[] args) {
        try{
            ClientMultiPlayer client = new ClientMultiPlayer();
            Scanner scanner = new Scanner(System.in);
            client.start();

            while(true){
                String s = scanner.nextLine();
                client.sendWorld();
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("客户端启动失败");
        }
    }

    public static void clientStart(Raylib rlj, World world, Player player){
        ClientMultiPlayer client = null;
        try{
            client = new ClientMultiPlayer();
            client.start();

            PlayTheme.startGame(rlj,world,player,false);
        }catch (Exception e) {
            e.printStackTrace();
            client.stop();
        }
    }
}
