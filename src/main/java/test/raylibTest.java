package test;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.rCore;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.textures.Image;
import com.raylib.java.textures.Texture2D;
import com.raylib.java.textures.rTextures;

public class raylibTest {
    public static int FRAME_MS = 10;    //表示一帧的毫秒数。

    static boolean Attacking = false;


    public static void main(String[] args) {
        int screenWidth = 960;
        int screenHeight = 600;

        Raylib rlj = new Raylib(screenWidth,screenHeight, "game");
        Image image = rTextures.LoadImage("src/main/resources/weapons/chopper.png");
        Texture2D texture = rTextures.LoadTextureFromImage(image);
        rTextures.UnloadImage(image);

        Rectangle dest = new Rectangle(0,0,texture.width,texture.height);
        rlj.core.SetTargetFPS(1000/FRAME_MS);

        int frame=0;
        int curAngle = 0;

        while(!rlj.core.WindowShouldClose()){
            //帧内变化计算

            
            rlj.core.BeginDrawing();
            rlj.core.ClearBackground(Color.WHITE);
            
            Vector2 mousePosition = rCore.GetMousePosition();
            
            /**
             * DrawTextureRec参数：
             * 第一项texture是2d纹理
             * 第二项是一个矩形，矩形的长和宽代表绘制区域大小，矩形的posX和posY表示纹理绘制的内部偏移量（不是绘制位置的偏移量！）
             * 第三项是绘制区域的左上角坐标
             * 第四项是蒙版颜色，白色就是原色调
             */
            //这个例子反映了第二项中矩形的坐标的作用
//            rlj.textures.DrawTextureRec(texture,new Rectangle(mousePosition.getX(),mousePosition.getY(),texture.width,texture.height),new Vector2(400,0),Color.WHITE);
            //第三项坐标的作用
            rlj.textures.DrawTextureRec(texture,new Rectangle(0,0,texture.width,texture.height),mousePosition,Color.WHITE);


            /**
             * DrawTexturePro参数：
             * 第一项texture是2d纹理
             * 第二项source是绘制区域大小
             * 第三项dest是绘制区域，控制缩放和偏移
             * 第四项是绘制偏移量
             * 第五项是旋转角度
             * 第六项是蒙版颜色，白色就是原色调
             */
//            rTextures.DrawTexturePro(texture,dest,new Rectangle(0,400,100,100),new Vector2(0,0),10f,Color.WHITE);


            rlj.core.EndDrawing();

        }
    }
}
