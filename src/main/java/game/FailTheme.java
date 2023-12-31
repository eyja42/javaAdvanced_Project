package game;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.rCore;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;

import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;


public class FailTheme {
    public static boolean tryAgain = false;

    public static void showFailTheme(Raylib rlj){
        while(!rlj.core.WindowShouldClose()){
            rlj.core.BeginDrawing();
            rlj.core.ClearBackground(Color.WHITE);
            rlj.text.DrawText("You Lose!", 680, 400, 30, Color.BLACK);
            Rectangle tryAgainBtn = new Rectangle(680, 590, 220, 90);
            rlj.shapes.DrawRectangleRounded(tryAgainBtn, 0.7f, 0, Color.BLACK);
            Rectangle backToMenuBtn = new Rectangle(680, 700, 220, 90);
            rlj.shapes.DrawRectangleRounded(backToMenuBtn, 0.7f, 0, Color.BLACK);
            rlj.text.DrawText("Try Again",725,620,30,Color.WHITE);
            rlj.text.DrawText("Back to Menu",690,730,30,Color.WHITE);

            if(rlj.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT)){
                Vector2 mousePosition = rCore.GetMousePosition();
                if(rlj.shapes.CheckCollisionPointRec(mousePosition,tryAgainBtn)) {
                    tryAgain = true;
                    break;
                }else if(rlj.shapes.CheckCollisionPointRec(mousePosition,backToMenuBtn)){
                    break;
                }
            }

            rlj.core.EndDrawing();
        }
    }
}
