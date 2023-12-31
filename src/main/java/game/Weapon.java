package game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raylib.java.Raylib;
import com.raylib.java.textures.Image;
import com.raylib.java.textures.Texture2D;
import com.raylib.java.textures.rTextures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


// 后续改成enum格式的，来完成各个武器的初始化
public class Weapon implements Serializable {


    //武器碰撞箱数值。用于近战攻击判定等
    float width;
    float height;
    //武器贴图的注册名
    String textureString;
    //武器的基础攻击力。  最终攻击力=武器攻击力+玩家攻击力
    float atk;
    //武器的攻击速度。 最终攻速=玩家攻速+武器攻速；这个数值等于武器攻击动画中的每帧间隔
    int atkSpeed;
    //持有武器的玩家。
    Player player;
    //以下是武器的攻击动画相关
    //是否正在攻击
    private boolean attacking;
    //近战武器的攻击表现为武器图片矩形的角度&偏移量变化。
    //当前武器角度。注：近战武器攻击时朝向会固定。
    float angle;
    //当前武器伸出长度。
    float dist;
    //表示当前帧数。
    private int frameNum;
    private int tick;
    //记录武器攻击动画。武器攻击是通过调整贴图角度模拟出的
    ArrayList<Float> angles;
    ArrayList<Float> dists;

    public boolean isAttacking() {
        return attacking;
    }

    public float getAngleOffset(){
        return angles.get(frameNum);
    }
    public float getDistOffset(){
        return dists.get(frameNum);
    }

    //攻击方法。如果是远程会使用weaponAngle确定子弹角度
    public void attack(float weaponAngle){
        this.attacking=true;
    }

    public Weapon(){

    }

    //创建武器，并将武器绑定到player。
    public Weapon(Player player){
        width = 100;
        height = 64;
        textureString = "WEAPON_CHOPPER";
        atk=10;
        atkSpeed=1;
        this.player = player;
        attacking=false;
        angle=0.0f;
        dist=0.0f;
        frameNum=0;
        tick=0;

        angles = new ArrayList<Float>(Arrays.asList(0.0f,-30.0f,-60.0f,-30.0f,10.0f,40.0f,60.0f,-30.0f));
        dists = new ArrayList<Float>(Arrays.asList(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f));
//        angles = new ArrayList<Float>(Arrays.asList(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f));
//        dists = new ArrayList<Float>(Arrays.asList(0.0f,20.0f,40.0f,50.0f,50.0f,50.0f,30.0f,10.0f,5.0f,0.0f));

        player.weapon = this;
    }

    //从json文件中读取武器信息，并绑定到player。
    public Weapon(JsonObject json,Player player){
        width = json.get("width").getAsFloat();
        height = json.get("height").getAsFloat();
        textureString = json.get("textureString").getAsString();
        atk = json.get("atk").getAsFloat();
        atkSpeed = json.get("atkSpeed").getAsInt();
        this.player = player;
        attacking = json.get("attacking").getAsBoolean();
        angle = json.get("angle").getAsFloat();
        dist = json.get("dist").getAsFloat();
        frameNum = json.get("frameNum").getAsInt();
        tick = json.get("tick").getAsInt();
        angles = new ArrayList<Float>();
        JsonArray anglesJson = json.get("angles").getAsJsonArray();
        for(JsonElement angle : anglesJson){
            angles.add(angle.getAsFloat());
        }
        dists = new ArrayList<Float>();
        JsonArray distJson = json.get("dists").getAsJsonArray();
        for(JsonElement dist : distJson) {
            dists.add(dist.getAsFloat());
        }

        player.weapon = this;
    }

    /**
     * 将武器信息转换为json格式。用于保存。
     * @return
     */
    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("width",width);
        json.addProperty("height",height);
        json.addProperty("textureString",textureString);
        json.addProperty("atk",atk);
        json.addProperty("atkSpeed",atkSpeed);
        json.addProperty("attacking",attacking);
        json.addProperty("angle",angle);
        json.addProperty("dist",dist);
        json.addProperty("frameNum",frameNum);
        json.addProperty("tick",tick);
        JsonArray anglesJson = new JsonArray();
        for(Float angle : angles){
            anglesJson.add(angle);
        }
        json.add("angles",anglesJson);
        JsonArray distsJson = new JsonArray();
        for(Float dist : dists){
            distsJson.add(dist);
        }
        json.add("dists",distsJson);

        return json;
    }

    public void update(Weapon newWeapon){
        assert newWeapon.textureString == this.textureString && newWeapon.width == this.width && newWeapon.height == this.height;
        atk = newWeapon.atk;
        atkSpeed = newWeapon.atkSpeed;
        attacking = newWeapon.attacking;
        angle = newWeapon.angle;
        dist = newWeapon.dist;
        frameNum = newWeapon.frameNum;
        tick = newWeapon.tick;
    }

    //tick结算。只在主线程结算tick时被调用
    public void tick(){
        if(attacking){
            this.tick++;
            if(tick>=player.atkSpeed+atkSpeed){
                tick=0;
                frameNum++;
                if(frameNum>=angles.size() || frameNum>=dists.size()){
                    frameNum=0;
                    attacking=false;
                }
                if(frameNum == angles.size()/2){
                    player.world.playerAttack(player);
                }
//                angle = angle + angles.get(frameNum);
//                dist = dist + dists.get(frameNum);
            }
        }
    }

    //测试用方法
    public ArrayList<Float> getAnglesTest(){
        return angles;
    }
    //测试用方法
    public ArrayList<Float> getDistsTest(){
        return dists;
    }
    //测试用方法
    public int getTickTest(){
        return tick;
    }
}
