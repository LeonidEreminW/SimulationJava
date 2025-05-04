import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Simulation {
    Map<Coordinates,AbstractEntity> worldMap = new HashMap<Coordinates,AbstractEntity>();
    private int worldHeight = 5;
    private int worldWidth = 5;
    private boolean isPaused = true;
    private int simulationInterval = 3;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ArrayList<AbstractCreature> creaturePool = new ArrayList<>();

    public void startSimulation() {
        createWorld();
        spawnCreature(new Coordinates(0,0));
        isPaused = false;
        simulationCircle();

    }
    private void simulationCircle(){
        scheduler.scheduleAtFixedRate(this::nextTurn,0, simulationInterval, TimeUnit.SECONDS);

    }
    public void pauseSimulation() {}
    public void nextTurn() {
        for (var item : creaturePool) {
           item.makeMove();
        }
        renderer.renderWorld(worldMap,worldWidth,worldHeight);
    }



    private void createWorld() {
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
//                System.out.println("x=" + x + ", y=" + y);
                worldMap.put(new Coordinates(x,y),null);
            }
        }
    }
    private Renderer renderer = new Renderer();
    public void test() throws InterruptedException {
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
    private void spawnCreature(Coordinates coordinates){
        var testHerb = new Herbivore();
        setupCreature(testHerb,this::moveEntity);
        creaturePool.add(testHerb);
        spawnEntity(testHerb,coordinates);
    }

}
