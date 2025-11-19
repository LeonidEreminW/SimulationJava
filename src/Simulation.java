import java.util.*;
import java.util.concurrent.*;

public class Simulation {
    Map<Coordinates,AbstractEntity> worldMap = new HashMap<Coordinates,AbstractEntity>();
    private final int worldHeight = 7;
    private final int worldWidth = 7;
    private boolean isPaused = true;
    private final int simulationInterval =2;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final CopyOnWriteArrayList<AbstractCreature> creaturePool = new CopyOnWriteArrayList<>();
    private int simulationStep = 0;
    private final Renderer renderer = new Renderer();


    public void startSimulation() {
        createWorld();
        spawnRandomCreature(3, EntityType.HERBIVORE);
        spawnRandomCreature(2, EntityType.PREDATOR);
        spawnRandomEnviroment(4,3,2);
        isPaused = false;

        simulationCircle();

    }
    private void simulationCircle(){
        scheduler.scheduleAtFixedRate(this::nextTurn,0, simulationInterval, TimeUnit.SECONDS);

    }
    public void pauseSimulation() {
        isPaused = true;
    }
    public void continueSimulation() {
        isPaused = false;
    }
    public void nextTurn() {
        Iterator<AbstractCreature> creatureIterator = creaturePool.iterator();
        if (creaturePool.stream().noneMatch(item->item.type == EntityType.HERBIVORE)) {
            renderer.renderWorld(worldMap,worldWidth,worldHeight);
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

                renderer.renderWorld(worldMap,worldWidth,worldHeight);
            } catch (Throwable t) {
                System.err.println("Exception during simulation step: " + t);
                t.printStackTrace();
            }
        }

    }



    private void createWorld() {

        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
//                System.out.println("x=" + x + ", y=" + y);
                worldMap.put(new Coordinates(x,y),null);
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
        worldMap.put(coordinates,entity);
    }
    private void moveEntity(AbstractEntity entity, Coordinates coordinates) {
        var previousPosition = entity.getPosition();
        entity.setPosition(coordinates);
        worldMap.put(previousPosition,null);
        worldMap.put(coordinates,entity);

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
                setupCreature(creature,this::moveEntity,this::BFS);
                
            break;
            
            case PREDATOR:
                creature = new Predator();
                setupCreature(creature, this::moveEntity, this::BFS);
        
            default:
                break;
        }
        creaturePool.add(creature);
        spawnEntity(creature,coordinates);
        System.out.println(creaturePool.size());
    }
    public Queue<Coordinates> BFS(Coordinates start, EntityType type) {
        int h = worldHeight;
        int w = worldWidth;
        int[][] dist = new int[h][w];
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) dist[y][x] = -1;
        var from = new HashMap<Coordinates,Coordinates>();

        Queue<Coordinates> q = new LinkedList<>();
        q.add(start);
        dist[start.y][start.x] = 0;
        from.put(start, null);

        Coordinates foundTarget = null;
        int[] dx = {1,-1,0,0};
        int[] dy = {0,0,1,-1};

        while (!q.isEmpty()) {
            Coordinates cur = q.remove();
            var ent = worldMap.get(cur);
            if (ent != null && ent.type == type) {
                foundTarget = cur;
                break;
            }
            for (int k = 0; k < 4; k++) {
                int nx = cur.x + dx[k];
                int ny = cur.y + dy[k];
                if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                if (dist[ny][nx] != -1) continue; // visited
                var cell = worldMap.get(new Coordinates(nx, ny));
                // if cell is occupied by a blocking entity (not the target type), skip
                if (cell != null && cell.type != type) continue;
                dist[ny][nx] = dist[cur.y][cur.x] + 1;
                var next = new Coordinates(nx, ny);
                q.add(next);
                from.put(next, cur);
            }
        }

        if (foundTarget == null) return null;
        return getPath(from, foundTarget);
    }

    public Deque<Coordinates> getPath(HashMap<Coordinates,Coordinates> from, Coordinates end) {
        var path = new LinkedList<Coordinates>();
        var cur = end;
        while (cur != null) {
            path.addFirst(cur);
            cur = from.get(cur);
        }
        // first element is start position; remove it so queue contains steps to move
        if (!path.isEmpty()) path.removeFirst();
        return path;
    }
    
    public void destroyGrass(Coordinates coordinates) {
        worldMap.put(coordinates,null);
        spawnRandomEntity(new Grass());
    }
    private void spawnRandomEntity(AbstractEntity entity) {
        var coordinates = new Coordinates(ThreadLocalRandom.current().nextInt(0, worldWidth), ThreadLocalRandom.current().nextInt(0, worldHeight));
        if (worldMap.get(coordinates)==null) {
            System.out.println(worldMap.get(coordinates)==null);
            spawnEntity(entity,coordinates);
        }else{
            spawnRandomEntity(entity);}

    }
    public void onDeathHandler(AbstractCreature creature) {
        var position = creature.getPosition();
        creaturePool.remove(creature);
        worldMap.put(position,null);
        System.out.println(creature.getType()+" died at "+position.x+" "+position.y);
    }
    public void provideDamage(Coordinates preyCoordinates, int damage) {
        var entity = worldMap.get(preyCoordinates);
        if(entity instanceof AbstractCreature){
            ((AbstractCreature) entity).takeDamageBehavior(damage);
        }
    }

}
