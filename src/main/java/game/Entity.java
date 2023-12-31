package game;

import com.raylib.java.raymath.Vector2;

/**
 * 实体类。所有地图上可移动的物品都视作实体。
 *
 */
public class Entity {
    //全局entity ID分配器。初始化player时此值自增。
    public static int entityID = 0;

    //实体id。用于区分不同的实体。
    public int id = -1;
    //时间戳。用于联机模式的信息更新。
    public long timestamp = 0;

    //在地图中的位置。表示实体中心判定点的坐标。
    public float x;
    public float y;
    //所属地图。
    final World world;
    //碰撞体积。所有物体的碰撞体积都是一个圆形，这里记录圆形的半径。
    //默认数值48.0f是玩家/普通敌人的大小。
    public float radius = 48.0f;
    //贴图注册名。由TextureLoader在运行时加载具体图片资源
    public String textureString;

    public void moveTo(float x,float y){
        this.x = x;
        this.y = y;
    }

    public void moveBy(float x,float y){
        this.x += x;
        this.y += y;
        if(world!=null && world.isCollide(this)){
            this.x -= x;
            this.y -= y;
        }
        timestamp++;
    }

    public void moveBy(Vector2 v){
        this.x += v.x;
        this.y += v.y;
        if(world!=null && world.isCollide(this)){
            this.x -= v.x;
            this.y -= v.y;
        }
        timestamp++;
    }

    public Vector2 getPosition() {
        return new Vector2(this.x,this.y);
    }

    public Entity(int id,World world){
        this.id = id;
        entityID = Math.max(entityID,id+1);
        this.world = world;
    }

    public Entity(World world){
        this.world = world;
        this.id = entityID++;
    }
}
