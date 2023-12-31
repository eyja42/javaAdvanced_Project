package game;


import com.google.gson.JsonObject;
import com.raylib.java.shapes.Rectangle;

import java.io.Serializable;

public class Wall extends Rectangle implements Serializable{

    @Override
    public String toString() {
        return "Wall{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("x",x);
        json.addProperty("y",y);
        json.addProperty("width",width);
        json.addProperty("height",height);
        return json;
    }
}
