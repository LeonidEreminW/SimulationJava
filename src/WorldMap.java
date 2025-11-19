import java.util.HashMap;
import java.util.Map;

public class WorldMap {
    private final int worldHeight;
    private final int worldWidth;
    private final Map<Coordinates,AbstractEntity> map = new HashMap<Coordinates,AbstractEntity>();
    private void createWorld() {

        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
//                System.out.println("x=" + x + ", y=" + y);
                map.put(new Coordinates(x,y),null);
            }
        }
    }
    public void putEntity(Coordinates coordinates,AbstractEntity entity) {
        map.put(coordinates,entity);
    }
    public WorldMap(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        createWorld();
    }
    public AbstractEntity getEntity(Coordinates coordinates) {
        return map.get(coordinates);
    }
    public Map<Coordinates,AbstractEntity> getMap() {
        return map;
    }
    public int getWorldWidth() {
        return worldWidth;
    }
    public int getWorldHeight() {
        return worldHeight;
    }
}
