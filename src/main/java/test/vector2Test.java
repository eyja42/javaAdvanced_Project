package test;

import com.raylib.java.raymath.Raymath;
import com.raylib.java.raymath.Vector2;

public class vector2Test {

    public static void main(String[] args) {
        Vector2 test = new Vector2(90,0);
        test = Raymath.Vector2Rotate(test,180);
        System.out.println(test.getX()+" "+test.getY());
    }
}
