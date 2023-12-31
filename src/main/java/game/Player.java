package game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raylib.java.raymath.Vector2;

import java.io.Serializable;


public class Player extends Entity implements Serializable {
    public static final int PLAYER_SIZE = 48;
    public static final int FACING_LEFT = 0;
    public static final int FACING_RIGHT = 1;

    public float maxHealth;
    public float health;
    public float speed;
    //玩家攻击力。    最终攻击力=武器攻击力+玩家攻击力
    public float atk;
    //攻速。这个数值决定每个武器攻击的帧数之间的间隔帧数。
    public int atkSpeed;
    //玩家的得分。
    public int score;
    //玩家死亡信息。
    public boolean isDead;

    //玩家的武器。
    public Weapon weapon;

    //玩家的朝向。
    public int facing;

    public void moveUp(){
        moveBy(0,-speed);
    }
    public void moveDown(){
        moveBy(0,speed);
    }
    public void moveLeft(){
        this.facing = FACING_LEFT;
        moveBy(-speed,0);
    }
    public void moveRight(){
        this.facing = FACING_RIGHT;
        moveBy(speed,0);
    }
    //移动角色到指定位置。不进行碰撞检测。
    public void moveTo(Vector2 position){
        this.x = position.x;
        this.y = position.y;
        timestamp++;
    }

    public void attack(float weaponAngle){
        //远程攻击类型就创建子弹
//        Bullet b = new Bullet(this,aimingPosition);
//        world.bullets.add(b);
        //近战攻击，只用让武器开始播放攻击动画就行
        weapon.attack(weaponAngle);
        timestamp++;
    }

    public void causeDamage(Enemy e){
        e.takeDamage(this.atk+weapon.atk);
        if(e.isDead()){
            this.score += e.score;
        }
        timestamp++;
    }

    public void takeDamage(float dmg){
        this.health -= dmg;
        if(this.health<=0){
            this.health = 0;
            this.isDead = true;
        }
        timestamp++;
    }


    /**
     * 创建一个玩家角色，并在对应的世界中注册。
     * @param world 玩家所在世界
     */
    public Player(World world){
        super(world);
        x = 0;
        y = 0;
        this.textureString = "PLAYER_DEFAULT";

        maxHealth = 100;
        health = 100;
        speed = 2;
        atk = 10;
        atkSpeed = 5;
        facing = FACING_RIGHT;
        score = 0;
        new Weapon(this);

        world.players.add(this);
    }

    /**
     * 从json文件中读取玩家信息，并在对应的世界中注册。
     * @param json 角色数据
     * @param world 要创建角色的世界
     */
    public Player(JsonObject json, World world) {
        super(json.get("id").getAsInt(),world);

        try{
            x = json.get("x").getAsFloat();
            y = json.get("y").getAsFloat();
            timestamp = json.get("timestamp").getAsInt();
            textureString = json.get("textureString").getAsString();

            maxHealth = json.get("maxHealth").getAsFloat();
            health = json.get("health").getAsFloat();
            speed = json.get("speed").getAsFloat();
            atk = json.get("atk").getAsFloat();
            atkSpeed = json.get("atkSpeed").getAsInt();
            facing = json.get("facing").getAsInt();
            score = json.get("score").getAsInt();
            JsonObject weaponJson = json.get("weapon").getAsJsonObject();
            new Weapon(weaponJson, this);

            world.players.add(this);
        }catch (Exception e){
            throw new RuntimeException("读取玩家json失败");
        }

    }

    /**
     * 将玩家信息转换为json格式。
     */
    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("x",x);
        json.addProperty("y",y);
        json.addProperty("id",id);
        json.addProperty("timestamp",timestamp);
        json.addProperty("textureString",textureString);

        json.addProperty("maxHealth",maxHealth);
        json.addProperty("health",health);
        json.addProperty("speed",speed);
        json.addProperty("atk",atk);
        json.addProperty("atkSpeed",atkSpeed);
        json.addProperty("facing",facing);
        json.addProperty("score",score);
        json.add("weapon",weapon.toJson());

        return json;
    }

    //根据新的数据更新本玩家信息。用于联机模式
    public void update(Player newPlayer){
        assert newPlayer.id == this.id;
        //如果新的数据比本地数据旧，就不更新
        if(newPlayer.timestamp < this.timestamp){
            return;
        }
        x = newPlayer.x;
        y = newPlayer.y;
        timestamp = newPlayer.timestamp;
        if(newPlayer.isDead){
            this.isDead = true;
        }
        health = newPlayer.health;
        facing = newPlayer.facing;
        score = newPlayer.score;
        weapon.update(newPlayer.weapon);
    }

    public void updateCurPlayer(Player newPlayer){
        assert newPlayer.id == this.id;
        if(newPlayer.isDead){
            this.isDead = true;
        }
        this.health = Math.min(newPlayer.health, this.health);
        this.score = Math.max(newPlayer.score, this.score);
    }


}
