import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Simulation {
    private final int worldHeight = 7;
    private final int worldWidth = 7;
    private final WorldMap worldMap = new WorldMap(worldWidth,worldHeight);
    private boolean isPaused = true;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService schedulerForPause = Executors.newSingleThreadScheduledExecutor();
    private final CopyOnWriteArrayList<AbstractCreature> creaturePool = new CopyOnWriteArrayList<>();
    private int simulationStep = 0;
    private final Renderer renderer = new Renderer();


    public void startSimulation() {
        spawnRandomCreature(3, EntityType.HERBIVORE);
        spawnRandomCreature(2, EntityType.PREDATOR);
        spawnRandomEnviroment(4,3,2);
        isPaused = false;

        simulationCircle();
        startPauseController();

    }
    private void simulationCircle(){
        int simulationInterval = 2;
        scheduler.scheduleAtFixedRate(this::nextTurn,0, simulationInterval, TimeUnit.SECONDS);

    }
    private void startPauseController() {
        schedulerForPause.scheduleAtFixedRate(() -> {
            try {
                if (System.in.available() > 0 && System.in.read() == ' ') {
                    pauseSimulation();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS); // проверка пробела каждые 100мс
    }
    public void pauseSimulation() {
        isPaused = !isPaused;
    }
    public void nextTurn() {
        Iterator<AbstractCreature> creatureIterator = creaturePool.iterator();
        if (creaturePool.stream().noneMatch(item->item.type == EntityType.HERBIVORE)) {
            renderer.renderWorld(worldMap.getMap(),worldWidth,worldHeight);
            isPaused = true;
            scheduler.shutdown();
        }
        if (!isPaused) {
            try {
                simulationStep++;
                System.out.println("=== Step " + simulationStep + " ===");

                while (creatureIterator.hasNext()) {
                    creatureIterator.next().makeMove();
                }

                renderer.renderWorld(worldMap.getMap(),worldWidth,worldHeight);
            } catch (Throwable t) {
                System.err.println("Exception during simulation step: " + t);
            }
        }

    }

    private void spawnRandomCreature(int count, EntityType type){
        for(int i = 0; i < count; i++){
            spawnCreature(new Coordinates(ThreadLocalRandom.current().nextInt(0, worldWidth), ThreadLocalRandom.current().nextInt(0, worldHeight)),type);
        }
    }
    private void spawnRandomEnviroment(int trees, int rocks, int grass){

        for(int i = 0; i < trees; i++){
            spawnRandomEntity(new Tree());
        }for(int i = 0; i < rocks; i++){
            spawnRandomEntity(new Rock());
        }for(int i = 0; i < grass; i++){
            spawnRandomEntity(new Grass());
        }
    }

    private void spawnEntity(AbstractEntity entity, Coordinates coordinates) {
        entity.setPosition(coordinates);
        worldMap.putEntity(coordinates,entity);
    }
    private void moveEntity(AbstractEntity entity, Coordinates coordinates) {
        var previousPosition = entity.getPosition();
        entity.setPosition(coordinates);
        worldMap.putEntity(previousPosition,null);
        worldMap.putEntity(coordinates,entity);

    }
    private void setupCreature(AbstractCreature creature, IMovement<AbstractEntity,Coordinates>movementAction,IFind<Coordinates,EntityType>findAction) {
        creature.setMoveMethod(movementAction);
        creature.setFindMethod(findAction);
        // register on-death callback so Simulation can clean up when creature dies
        creature.setOnDeathMethod(() -> onDeathHandler(creature));
        if(creature instanceof Herbivore){
            ((Herbivore) creature).setEatMethod(this::destroyGrass);
        }
        if(creature instanceof Predator){
            ((Predator) creature).setHuntMethod(this::provideDamage);
        }
    }
    private void spawnCreature(Coordinates coordinates, EntityType type){
        AbstractCreature creature = null;
        switch (type) {
            case HERBIVORE:
                creature = new Herbivore();
                setupCreature(creature,this::moveEntity,this::FindEntity);
                
            break;
            
            case PREDATOR:
                creature = new Predator();
                setupCreature(creature, this::moveEntity, this::FindEntity);
        
            default:
                break;
        }
        creaturePool.add(creature);
        spawnEntity(creature,coordinates);
        System.out.println(creaturePool.size());
    }
    private Queue<Coordinates> FindEntity(Coordinates start, EntityType type){
        return BreadthFirstSearch.find(start,type,worldMap);
    }
    
    public void destroyGrass(Coordinates coordinates) {
        worldMap.putEntity(coordinates,null);
        spawnRandomEntity(new Grass());
    }
    private void spawnRandomEntity(AbstractEntity entity) {
        var coordinates = new Coordinates(ThreadLocalRandom.current().nextInt(0, worldWidth), ThreadLocalRandom.current().nextInt(0, worldHeight));
        if (worldMap.getEntity(coordinates)==null) {
            System.out.println(worldMap.getEntity(coordinates)==null);
            spawnEntity(entity,coordinates);
        }else{
            spawnRandomEntity(entity);}

    }
    public void onDeathHandler(AbstractCreature creature) {
        var position = creature.getPosition();
        creaturePool.remove(creature);
        worldMap.putEntity(position,null);
        System.out.println(creature.getType()+" died at "+position.x+" "+position.y);
    }
    public void provideDamage(Coordinates preyCoordinates, int damage) {
        var entity = worldMap.getEntity(preyCoordinates);
        if(entity instanceof AbstractCreature){
            ((AbstractCreature) entity).takeDamageBehavior(damage);
        }
    }

}
