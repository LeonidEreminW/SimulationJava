import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Simulation {
    Map<Coordinates,AbstractEntity> worldMap = new HashMap<Coordinates,AbstractEntity>();
    public void startSimulation() {}
    public void pauseSimulation() {}
    public void nextTurn() {}
    private int worldHeight;
    private int worldWidth;
    private void createWorld() {
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                System.out.println("x=" + x + ", y=" + y);
                worldMap.put(new Coordinates(x,y),null);
            }
        }
    }
    private Renderer renderer = new Renderer();
    public void test() throws InterruptedException {
        worldHeight = 3;
        worldWidth = 3;
        createWorld();
        var testHerb = new Herbivore();
        setupCreature(testHerb,this::moveEntity);
        spawnEntity(testHerb,new Coordinates(2,2));
        renderer.renderWorld(worldMap,worldWidth,worldHeight);
        Thread.sleep(2000);
        testHerb.makeMove();
        renderer.renderWorld(worldMap,worldWidth,worldHeight);



    }
    private void spawnEntity(AbstractEntity entity, Coordinates coordinates) {
        entity.setPosition(coordinates);
        worldMap.put(coordinates,entity);
    }
    private void moveEntity(AbstractEntity entity, Coordinates coordinates) {
        var previousPosition = entity.getPosition();
        entity.setPosition(coordinates);
        worldMap.put(previousPosition,null);
        worldMap.put(coordinates,entity);
    }
    private void setupCreature(AbstractCreature creature, IMovement<AbstractEntity,Coordinates>action) {
        creature.setAction(action);
    }

}
