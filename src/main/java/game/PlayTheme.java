package game;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.rCore;
import com.raylib.java.raymath.Raymath;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.textures.Texture2D;
import com.raylib.java.textures.rTextures;

import java.util.Iterator;

import static com.raylib.java.core.input.Keyboard.*;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static game.Player.PLAYER_SIZE;


/**
 * 游戏主界面。
 */
public class PlayTheme {

    static int screenWidth = 1600;
    static int screenHeight = 960;
    static float worldWidth = 3200;
    static float worldHeight = 2000;

    //屏幕中心位置
    static Vector2 center;
    //全局暂停
    public static boolean pause = false;
    //图片资源加载器
    private static TextureLoader textureLoader;
    //主视角玩家
    static Player player;
    //主视角玩家id
    static int curPlayerId = -1;
    //服务器
    private static Server server;
    //玩家死亡指示
    public static boolean playerDead = false;
    //调试用属性
    static boolean DEBUG_MODE = false;


    /**
     * 游戏界面渲染进程。
     * @param rlj
     * @param world
     * @param player
     * @param multiPlayerMode 本项设为真将进入联机模式,不启动server
     */
    public static void startGame(Raylib rlj,World world,Player curPlayer,boolean multiPlayerMode,Multi multiServer) throws Exception{
        if(multiPlayerMode){
            rlj.core.SetTargetFPS(ServerMultiPlayer.TARGET_FPS);
        }else{
            rlj.core.SetTargetFPS(Server.TARGET_FPS);
        }

        rlj.core.SetExitKey(KEY_EQUAL);
        screenWidth = rlj.core.GetScreenWidth();
        screenHeight = rlj.core.GetScreenHeight();
        center = new Vector2((float) screenWidth / 2, (float) screenHeight / 2);

        player = curPlayer;
        synchronized (world){
            if(player==null){
                player = new Player(world);
                player.moveTo(100,100);
            }


            //渲染器从硬盘加载图片资源
            textureLoader = new TextureLoader();
            //连接服务器。
            if(!multiPlayerMode){
                server = new Server(world, player);
            }
        }
        curPlayerId = player.id;
        if(multiPlayerMode){
            multiServer.setCurPlayerId(curPlayerId);
        }

        pause = false;


        while(!rlj.core.WindowShouldClose()){

            if(!pause){
                //玩家死亡，游戏结束
                if(player.isDead){
                    playerDead = true;
                    break;
                }
                //处理玩家输入
                if(rCore.IsKeyDown(KEY_D)){
                    player.moveRight();
                }
                if(rCore.IsKeyDown(KEY_A)){
                    player.moveLeft();
                }
                if(rCore.IsKeyDown(KEY_W)){
                    player.moveUp();
                }
                if(rCore.IsKeyDown(KEY_S)){
                    player.moveDown();
                }
                Vector2 mousePosition = rCore.GetMousePosition();
                if(!player.weapon.isAttacking()){
                    player.weapon.angle = (float) Raymath.Vector2Angle(center, mousePosition);
                }
                if(rlj.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT)){
                    player.attack(player.weapon.angle);
                }
                player.weapon.tick();
            }
            //按下esc键切换全局暂停状态。联机时禁用暂停
            if(rlj.core.IsKeyPressed(KEY_ESCAPE) && !multiPlayerMode){
                pause = !pause;
                if(pause){
                    server.pause();
                }else{
                    server.resume();
                }
            }


            //绘制游戏界面
            //关于地图随着玩家视角移动的实现的逻辑：
            //在原本的位置上绘制，再在渲染时加上界面位置相对于整张地图的偏移量。
            rlj.core.BeginDrawing();
            rlj.core.ClearBackground(Color.GRAY);

            //这个是大地图左上角相较于游戏视图左上角的偏移
            Vector2 worldBorder1 =  new Vector2(screenWidth/2 - player.getPosition().x,screenHeight/2 - player.getPosition().y);

            synchronized (world){
                //地图信息，包括地图边界，墙壁
                rlj.shapes.DrawRectangleV(worldBorder1,new Vector2(world.worldWidth,world.worldHeight),Color.WHITE);
                for(Wall wall: world.walls){
                    rlj.shapes.DrawRectangleV(Raymath.Vector2Add(worldBorder1,new Vector2(wall.x,wall.y)),new Vector2(wall.width,wall.height),Color.BLACK);
                }

                //--------------------敌人绘制--------------------
                Iterator<Enemy> enemyIterator = world.enemies.iterator();
                while(enemyIterator.hasNext()){
                    Enemy e = enemyIterator.next();
                    Vector2 curEnemyPosition = Raymath.Vector2Add(worldBorder1,e.getPosition());
                    //因为getPosition()返回的是中心点坐标，而绘图时需要左上角坐标，所以需要减去一半的宽高
                    Texture2D enemyTexture = textureLoader.getTexture(e.textureString);
                    curEnemyPosition = Raymath.Vector2Add(curEnemyPosition,new Vector2(-enemyTexture.width/2,-enemyTexture.height/2));

                    if(e.facing == Enemy.FACING_RIGHT){
                        rlj.textures.DrawTextureRec(enemyTexture, new Rectangle(0, 0, enemyTexture.width, enemyTexture.height), curEnemyPosition, Color.WHITE);
                    }else if(e.facing == Enemy.FACING_LEFT){
                        rlj.textures.DrawTextureRec(enemyTexture, new Rectangle(0, 0, enemyTexture.width, enemyTexture.height), curEnemyPosition, Color.WHITE);
                    }
                }


                //--------------------画出玩家和武器--------------------
                // 玩家和武器拥有在地图中间，所以不用加上偏移量
                Texture2D playerTexture = textureLoader.getTexture(player.textureString);
                Rectangle dest = new Rectangle(center.x, center.y, playerTexture.width, playerTexture.height);
                if(player.facing == Player.FACING_RIGHT){
                    rTextures.DrawTexturePro(playerTexture, new Rectangle(0, 0, playerTexture.width, playerTexture.height), dest, new Vector2(PLAYER_SIZE, PLAYER_SIZE), 0, Color.WHITE);
                }else if(player.facing == Player.FACING_LEFT){
                    rTextures.DrawTexturePro(playerTexture, new Rectangle(0, 0, -playerTexture.width, playerTexture.height), dest, new Vector2(PLAYER_SIZE, PLAYER_SIZE), 0, Color.WHITE);
                }


                Texture2D weaponTexture = textureLoader.getTexture(player.weapon.textureString);
                dest = new Rectangle(center.x, center.y, weaponTexture.width, weaponTexture.height);

                if(player.weapon.angle<90 || player.weapon.angle>270){
                    rTextures.DrawTexturePro(weaponTexture, new Rectangle(0, 0, weaponTexture.width, weaponTexture.height), dest, new Vector2(-30-player.weapon.getDistOffset(), weaponTexture.height/2), player.weapon.angle+player.weapon.getAngleOffset(), Color.WHITE);
                }else{
                    rTextures.DrawTexturePro(weaponTexture, new Rectangle(0, 0, weaponTexture.width, -weaponTexture.height), dest, new Vector2(-30-player.weapon.getDistOffset(),weaponTexture.height/2), player.weapon.angle-player.weapon.getAngleOffset(), Color.WHITE);
                }

                //绘制其它玩家。
                for(Player p: world.players){
                    if(p.id != player.id){
                        Texture2D otherPlayerTexture = textureLoader.getTexture(p.textureString);
                        Vector2 otherPlayerPosition = Raymath.Vector2Add(worldBorder1,p.getPosition());
                        //因为getPosition()返回的是中心点坐标，而绘图时需要左上角坐标，所以需要减去一半的宽高。和敌人绘制一样
                        otherPlayerPosition = Raymath.Vector2Add(otherPlayerPosition,new Vector2(-otherPlayerTexture.width/2,-otherPlayerTexture.height/2));
                        if(p.facing == Player.FACING_RIGHT){
                            rlj.textures.DrawTextureRec(otherPlayerTexture, new Rectangle(0, 0, otherPlayerTexture.width, otherPlayerTexture.height), otherPlayerPosition, Color.WHITE);
                        }else if(p.facing == Player.FACING_LEFT){
                            rlj.textures.DrawTextureRec(otherPlayerTexture, new Rectangle(0, 0, otherPlayerTexture.width, otherPlayerTexture.height), otherPlayerPosition, Color.WHITE);
                        }
                    }
                }


                //最后写各种数值信息，避免被覆盖掉
                if(pause){
                    rlj.text.DrawText("PAUSE", 10, 10, 20, Color.BLACK);
                }else{
                    rlj.text.DrawText("Health:"+player.health+"/"+player.maxHealth, 10, 10, 20, Color.BLACK);
                    rlj.text.DrawText("Score:"+player.score, 10, 30, 20, Color.BLACK);
                    rlj.text.DrawText("x:"+player.getPosition().x+" y:"+player.getPosition().y, 10, 50, 20, Color.BLACK);
                }

                //调试模式
                if(DEBUG_MODE){
                    if(player.weapon.isAttacking()){
                        //绘制武器攻击范围。这部分代码是从world的playerAttack()拿过来的
                        Vector2 weaponPos = player.getPosition();
                        Vector2 offset = new Vector2(30+player.weapon.getDistOffset()+((float)weaponTexture.height/2), 0);
                        offset = Raymath.Vector2Rotate(offset, player.weapon.angle);
                        weaponPos = Raymath.Vector2Add(weaponPos, offset);
                        rlj.shapes.DrawCircleV(Raymath.Vector2Add(weaponPos,worldBorder1), weaponTexture.height, Color.RED);
                    }
                    //绘制所有玩家的碰撞箱
                    Iterator<Player> playersIterator = world.players.iterator();
                    while (playersIterator.hasNext()){
                        Player p = playersIterator.next();
                        rlj.shapes.DrawCircleV(Raymath.Vector2Add(p.getPosition(),worldBorder1), p.radius, Color.BLUE);
                    }

                    //绘制所有敌人的碰撞箱
                    Iterator<Enemy> enemyIterator2 = world.enemies.iterator();
                    while(enemyIterator2.hasNext()){
                        Enemy e = enemyIterator2.next();
                        rlj.shapes.DrawCircleV(Raymath.Vector2Add(e.getPosition(),worldBorder1), e.radius, Color.YELLOW);
                    }
                }
            }

            rlj.core.EndDrawing();

        }

        //关闭服务器
        if(server!=null){
            server.close();
        }


    }

}
