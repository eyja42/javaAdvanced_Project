package test;


import com.google.gson.*;
import game.Wall;

import java.io.*;
import java.util.ArrayList;

public class JsonTest {

    private static String json_path = "src/main/resources/map/map.json";

    public static void main(String[] args) throws IOException {
        File file = new File(json_path);
        FileReader reader = new FileReader(file);

        JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
        Gson gson = new Gson();

        ArrayList<Wall> wallArrayList = new ArrayList<>();
        for(JsonElement wall:jsonArray){
            Wall w = gson.fromJson(wall,Wall.class);
            wallArrayList.add(w);
        }

        //打印看下wallArrayList里面的内容
        for(Wall w:wallArrayList){
            System.out.println(w);
        }

    }
}
