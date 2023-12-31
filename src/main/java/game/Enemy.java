package game;

import com.google.gson.JsonObject;

public class Enemy extends Entity{
    public static final int ENEMY_SIZE = 48;
    public static final int FACING_LEFT = 0;
    public static final int FACING_RIGHT = 1;

    public float maxHealth;
    public float health;
    public float speed;
    public float atk;
    //攻击频率。每隔atkSpeed个行动帧攻击一次
    public int atkSpeed;
    //朝向
    public int facing;
    //消灭敌人可获得的分数
    public float score;
    //执行行动的频率。
    private final int actionFreq;
    private boolean isDead;

    //自带的计时器。用于控制改变方向,攻击等行为
    private int tick=0;
    //攻击帧计数
    private int atkCD;


    /**
     * 创建一个敌人，并在对应的世界中注册。
     * @param world 对应的世界。
     */
    public Enemy(World world){
        super(world);
        this.x = 0;
        this.y = 0;
        textureString = "ENEMY_DEFAULT";

        maxHealth = 10;
        health = maxHealth;
        speed=2f;
        atk=1;
        atkSpeed=60;
        facing = FACING_RIGHT;
        score = 10;
        actionFreq = 5;
        isDead = false;
        atkCD = atkSpeed;

        world.enemies.add(this);
    }

    /**
     * 在指定坐标处创建一个敌人，在对应的世界中注册
     */
    public Enemy(World world, float x,float y){
        super(world);
        this.x = x;
        this.y = y;
        textureString = "ENEMY_DEFAULT";

        maxHealth = 10;
        health = maxHealth;
        speed=2f;
        atk=1;
        atkSpeed=60;
        facing = FACING_RIGHT;
        score = 10;
        actionFreq = 5;
        isDead = false;
        atkCD = atkSpeed;

        world.enemies.add(this);
    }

    /**
     * 使用json数据创建一个敌人，在对应的世界中注册
     */
    public Enemy(JsonObject json, World world){
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
            score = json.get("score").getAsFloat();
            actionFreq = json.get("actionFreq").getAsInt();
            isDead = json.get("isDead").getAsBoolean();

            world.enemies.add(this);
        }catch (Exception e) {
            throw new RuntimeException("Enemy json parse error");
        }

    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("id",id);
        json.addProperty("timestamp",timestamp);
        json.addProperty("textureString", textureString);

        json.addProperty("maxHealth", maxHealth);
        json.addProperty("health", health);
        json.addProperty("speed", speed);
        json.addProperty("atk", atk);
        json.addProperty("atkSpeed", atkSpeed);
        json.addProperty("facing", facing);
        json.addProperty("score", score);
        json.addProperty("actionFreq", actionFreq);
        json.addProperty("isDead", isDead);

        return json;
    }

    public void update(Enemy newEnemy){
        assert newEnemy.id == id;
        //如果新的敌人的时间戳比本敌人的时间戳旧，则不更新
        if(newEnemy.timestamp<= timestamp){
            return;
        }

        this.x = newEnemy.x;
        this.y = newEnemy.y;
        this.timestamp = newEnemy.timestamp;
        this.health = newEnemy.health;
        if(newEnemy.isDead()){
            this.isDead = true;
        }
    }

    public boolean tick(){
        tick++;
        if(tick>=actionFreq){
            tick=0;
            if(atkCD<atkSpeed){
                atkCD++;
            }
            return true;
        }
        return false;
    }

    public void tryAttack(Player nearestPlayer){
        //如果玩家和本敌人的碰撞箱相交且攻击cd完毕，则进行攻击。远程敌人不会进行攻击
        if(atkCD>=atkSpeed){
            if(World.CheckCollisionCircles(this.getPosition(),this.radius,nearestPlayer.getPosition(),nearestPlayer.radius)){
                causeDamage(nearestPlayer);
            }
        }
    }

    public void causeDamage(Player player){
        player.takeDamage(atk);
        atkCD=0;
    }

    public void takeDamage(float dmg){
        health -= dmg;
        if(health <= 0){
            isDead = true;
        }
    }

    public boolean isDead(){
        return isDead;
    }

    //测试用函数
    public int getAtkCDTest(){
        return this.atkCD;
    }
    //测试用函数
    public int getTickTest(){
        return this.tick;
    }
    //测试用函数
    public int getActionFreqTest(){
        return this.actionFreq;
    }

}
