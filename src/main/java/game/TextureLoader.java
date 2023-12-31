package game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raylib.java.textures.Image;
import com.raylib.java.textures.Texture2D;
import com.raylib.java.textures.rTextures;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端加载图片资源
 */
public class TextureLoader {
    public Map<String,Texture2D> textures;


    public TextureLoader (){
        FileReader reader;
        textures = new HashMap<>();
        try{
            reader = new FileReader("src/main/resources/textures.json");
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            //遍历jsonObject中的键值对并打印
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                Image tmp_image = rTextures.LoadImage(entry.getValue().getAsString());
                Texture2D tmp_texture = rTextures.LoadTextureFromImage(tmp_image);
                textures.put(entry.getKey(),tmp_texture);
            }
            System.out.println(textures);
        }catch (Exception e){
            System.out.println("图片资源目录json文件读取失败");
        }

    }

    public Texture2D getTexture(String name){
        return textures.get(name);
    }
}
