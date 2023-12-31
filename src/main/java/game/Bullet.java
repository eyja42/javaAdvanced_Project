package game;

public class Bullet extends Entity{
    public float atk;
    //子弹飞行速度
    public float speed;
    public float x;
    public float y;
    //子弹飞行方向
    public float angle;

    public Bullet(Player p, float weaponAngle){
        super(p.world);
        this.atk = p.atk;
        this.speed = 5.0f;
        this.x = p.getPosition().x;
        this.y = p.getPosition().y;
        this.angle = weaponAngle;
    }
}
