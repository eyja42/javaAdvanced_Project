package game;

import com.google.gson.*;
import com.raylib.java.raymath.Raymath;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class World{
    public float worldWidth = 3200;
    public float worldHeight = 2000;
    public List<Wall> walls;
    public CopyOnWriteArrayList<Enemy> enemies;
    public CopyOnWriteArrayList<Bullet> bullets;
    public CopyOnWriteArrayList<Player> players;

    private boolean allowSave = true;

    public World(){
        walls = new Vector<>();
        bullets = new CopyOnWriteArrayList<>();
        enemies = new CopyOnWriteArrayList<>();
        players = new CopyOnWriteArrayList<>();
    }

    /**
     * 从mapFile地图文件加载世界。
     * @param mapFile
     */
    public World(String mapFile){
        walls = new Vector<>();
        bullets = new CopyOnWriteArrayList<>();
        enemies = new CopyOnWriteArrayList<>();
        players = new CopyOnWriteArrayList<>();
        FileReader reader;
        try{
            reader = new FileReader(mapFile);
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();
            worldWidth = json.get("worldWidth").getAsFloat();
            worldHeight = json.get("worldHeight").getAsFloat();

            JsonArray wallArray = json.get("walls").getAsJsonArray();

            for(JsonElement wall:wallArray) {
                Wall w = gson.fromJson(wall, Wall.class);
                walls.add(w);
            }
        }catch (Exception e){
            System.out.println("地图文件读取失败");
        }
    }

    /**
     * 读取保存的游戏，返回World对象。
     * @param jsonFile 保存的游戏文件路径
     * @return World对象
     */
    public static World loadWorld(String jsonFile){
        try{
            String str = new String(Files.readAllBytes(Paths.get(jsonFile)));
            World world = fromJson(str);

            //一个存档只允许使用一次.删除原存档
            String tmp = "{\"valid\":false}";
            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
            writer.write(tmp);
            writer.close();

            world.allowSave = false;

            return world;
        }catch (Exception e){
            System.out.println("读取previousGame.json失败。检查文件格式");
            throw new RuntimeException(e);
        }

    }

    /**
     * 保存游戏到json文件。
     * @param target 目标json文件路径
     */
    public void saveWorld(String target) throws Exception{
        if(!this.allowSave) return;

        BufferedWriter writer = new BufferedWriter(new FileWriter(target));
        writer.write(this.toJson());
        writer.close();
    }

    //从json文件恢复world
    public static World fromJson(String str){
        World world = new World();
        Gson gson = new GsonBuilder().setLenient().create();
        JsonObject json = JsonParser.parseString(str).getAsJsonObject();
        if(!json.get("valid").getAsBoolean()) throw new RuntimeException("loadWorld: json文件invalid");
        JsonObject map = json.get("map").getAsJsonObject();
        world.worldWidth = map.get("worldWidth").getAsFloat();
        world.worldHeight = map.get("worldHeight").getAsFloat();
        //地图墙壁
        for(JsonElement wall:map.get("walls").getAsJsonArray()) {
            Wall w = gson.fromJson(wall, Wall.class);
            world.walls.add(w);
        }
        //所有玩家
        for(JsonElement player:json.get("players").getAsJsonArray()){
            new Player((JsonObject) player,world);
        }
//        JsonObject playerObject = json.get("player").getAsJsonObject();
//        new Player(playerObject,world);
        //敌人
        JsonArray enemyArray = json.get("enemies").getAsJsonArray();
        for(JsonElement enemy:enemyArray){
            JsonObject enemyObject = enemy.getAsJsonObject();
            new Enemy(enemyObject,world);
        }

        return world;
    }

    public String toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("valid",true);
        //保存玩家
        JsonArray playerArray = new JsonArray();
        for(Player player:this.players){
            playerArray.add(player.toJson());
        }
        json.add("players", playerArray);
        //保存敌人
        JsonArray enemyArray = new JsonArray();
        for(Enemy enemy:this.enemies){
            enemyArray.add(enemy.toJson());
        }
        json.add("enemies", enemyArray);
        //保存地图
        JsonObject map = new JsonObject();
        map.addProperty("worldWidth",this.worldWidth);
        map.addProperty("worldHeight",this.worldHeight);
        //墙壁信息
        JsonArray wallArray = new JsonArray();
        for(Wall wall:this.walls){
            wallArray.add(wall.toJson());
        }
        map.add("walls",wallArray);
        json.add("map", map);
        return json.toString();
    }

    //客户端联机模式更新地图。无条件更新除了主玩家数据之外的信息。
    public void updateClient(World newWorld,int curPlayerId){
        for(Player newPlayer:newWorld.players){
            boolean exist = false;
            for(Player player:this.players){
                if(player.id == newPlayer.id){
                    player.update(newPlayer);
                    exist = true;
                    break;
                }
            }
            if(!exist){
                this.players.add(newPlayer);
            }
        }
        //更新敌人
        this.enemies = newWorld.enemies;

    }

    //服务器更新地图
    public void updateServer(World newWorld, int curPlayerId){
        //更新玩家
        int maxPlayerId = (this.players.size() == 0)?0:this.players.get(this.players.size()-1).id;
        for(Player newPlayer:newWorld.players){
            boolean exist = false;
            for(Player player:this.players){
                if(player.id == newPlayer.id){
                    if(player.id != curPlayerId){
                        player.update(newPlayer);
                    }
                    exist = true;
                    break;
                }
            }
            if(!exist && maxPlayerId < newPlayer.id){
                this.players.add(newPlayer);
            }
        }
        //更新敌人
        int maxEnemyId = (this.enemies.size() == 0)?0:this.enemies.get(this.enemies.size()-1).id;
        for(Enemy newEnemy:newWorld.enemies){
            boolean exist = false;
            for(Enemy enemy:this.enemies){
                if(enemy.id == newEnemy.id){
                    enemy.update(newEnemy);
                    exist = true;
                    break;
                }
            }
            if(!exist && newEnemy.id > maxEnemyId){
                this.enemies.add(newEnemy);
            }
        }
    }




    //判断实体是否与墙相撞。
    public boolean isCollide(Entity e){
        if(e.x-e.radius<0 || e.x+e.radius> this.worldWidth || e.y-e.radius<0 || e.y+e.radius>this.worldHeight){
            return true;
        }
        for(Wall wall:walls){
            if(CheckCollisionCircleRec(e.getPosition(),e.radius, wall)){
                return true;
            }
        }
        return false;
    }

    private boolean CheckCollisionCircleRec(Vector2 center, float radius, Rectangle rec) {
        int recCenterX = (int)(rec.x + rec.width / 2.0F);
        int recCenterY = (int)(rec.y + rec.height / 2.0F);
        float dx = Math.abs(center.x - (float)recCenterX);
        float dy = Math.abs(center.y - (float)recCenterY);
        if (dx > rec.width / 2.0F + radius) {
            return false;
        } else if (dy > rec.height / 2.0F + radius) {
            return false;
        } else if (dx <= rec.width / 2.0F) {
            return true;
        } else if (dy <= rec.height / 2.0F) {
            return true;
        } else {
            float cornerDistanceSq = (dx - rec.width / 2.0F) * (dx - rec.width / 2.0F) + (dy - rec.height / 2.0F) * (dy - rec.height / 2.0F);
            return cornerDistanceSq <= radius * radius;
        }
    }

    /**
     * 近战武器的攻击帧判定。
     * 攻击范围是一个圆形，以玩家+武器偏移为圆心，半径为武器长度。
     */
    public void playerAttack(Player p){
        float weaponAngle = p.weapon.angle;
        Vector2 offset = new Vector2(30+p.weapon.getDistOffset()+((float)p.weapon.height/2), 0);
        offset = Raymath.Vector2Rotate(offset, weaponAngle);
        //计算攻击范围的圆心
        Vector2 weaponPos = p.getPosition();
        weaponPos = Raymath.Vector2Add(weaponPos, offset);

        Iterator<Enemy> iter = enemies.iterator();
        while (iter.hasNext()){
            Enemy e = iter.next();
            if(CheckCollisionCircles(weaponPos,p.weapon.height,e.getPosition(), e.radius)){
                p.causeDamage(e);
            }
            if(e.isDead()){
                enemies.remove(e);
            }
        }
    }

    public static boolean CheckCollisionCircles(Vector2 center1, float radius1, Vector2 center2, float radius2) {
        boolean collision = false;
        float dx = center2.x - center1.x;
        float dy = center2.y - center1.y;
        float distance = (float)Math.sqrt((dx * dx + dy * dy));
        if (distance <= radius1 + radius2) {
            collision = true;
        }

        return collision;
    }

//    public String toString(){
//        return "worldWidth:"+this.worldWidth+" worldHeight:"+this.worldHeight+" wall size:"+this.walls.size()+" players:"+this.players.size()+" enemies:"+this.enemies.size();
//    }
}
