package game;

import com.google.gson.*;
import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.rCore;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;

import java.io.FileReader;

import static com.raylib.java.core.input.Keyboard.KEY_EQUAL;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;

public class StartTheme {
    static int screenWidth = 1600;
    static int screenHeight = 960;

    static boolean loadGameAvailable = false;


    public static void main(String[] args) throws Exception {
        Raylib rlj = new Raylib();
        rlj.core.InitWindow(screenWidth, screenHeight, "测试界面");
        rlj.core.SetTargetFPS(120);
        rlj.core.SetExitKey(KEY_EQUAL);

        Rectangle newGameButton = new Rectangle(680, 590, 220, 90);
        Rectangle resumeGameButton = new Rectangle(680, 700, 220, 90);


        while(!rlj.core.WindowShouldClose()){
            rlj.core.BeginDrawing();

            if(rlj.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT)){
                Vector2 mousePosition = rCore.GetMousePosition();
                if(rlj.shapes.CheckCollisionPointRec(mousePosition,newGameButton)){
                    World world = new World("src/main/resources/map/map.json");
                    rlj.core.EndDrawing();
                    startGame(rlj,world,null);
                }else if(loadGameAvailable && rlj.shapes.CheckCollisionPointRec(mousePosition,resumeGameButton)){
                    World world = World.loadWorld("src/main/resources/previousGame.json");
                    Player player = world.players.get(0);
                    rlj.core.EndDrawing();
                    startGame(rlj,world,player);
                }
            }
            if(FailTheme.tryAgain){
                FailTheme.tryAgain = false;
                World world = new World("src/main/resources/map/map.json");

                startGame(rlj,world,null);
            }


            loadGameAvailable = loadGameAvailable();
            rlj.core.ClearBackground(Color.WHITE);
            rlj.shapes.DrawRectangleRounded(newGameButton, 0.7f, 0, Color.BLACK);
            if(loadGameAvailable){
                rlj.shapes.DrawRectangleRounded(resumeGameButton, 0.7f, 0, Color.BLACK);
            }else{
                rlj.shapes.DrawRectangleRounded(resumeGameButton, 0.7f, 0, Color.GRAY);
            }
            rlj.text.DrawText("New Game",725,620,30,Color.WHITE);
            rlj.text.DrawText("Resume Game",695,730,30,Color.WHITE);

            rlj.core.EndDrawing();
        }
    }

    //开启游戏界面，同时承担退出游戏后的界面跳转
    private static void startGame(Raylib rlj, World world, Player player) throws Exception{
        PlayTheme.startGame(rlj, world, player,false,null);
        //如果玩家没死亡是中途退出则保存游戏
        if (PlayTheme.playerDead) {
            FailTheme.showFailTheme(rlj);
        } else {
            world.saveWorld("src/main/resources/previousGame.json");
        }

    }

    /**
     * 读取previousGame.json，判断是否有存档。
     */
    public static boolean loadGameAvailable(){
        FileReader reader;
        try{
            reader = new FileReader("src/main/resources/previousGame.json");
            JsonObject file = JsonParser.parseReader(reader).getAsJsonObject();
            if(file.get("valid").getAsBoolean()){
                return true;
            }else{
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
}
