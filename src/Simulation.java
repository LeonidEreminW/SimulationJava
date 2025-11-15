import java.util.*;
import java.util.concurrent.*;

public class Simulation {
    Map<Coordinates,AbstractEntity> worldMap = new HashMap<Coordinates,AbstractEntity>();
    private int worldHeight = 5;
    private int worldWidth = 5;
    private boolean isPaused = true;
    private int simulationInterval =2;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ArrayList<AbstractCreature> creaturePool = new ArrayList<>();

    public void startSimulation() {
        createWorld();
        spawnCreature(new Coordinates(0,0), EntityType.HERBIVORE);
        // spawnCreature(new Coordinates(4,4), EntityType.PREDATOR);
        spawnRandomEnviroment(3,2,1);
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
        for (var item : creaturePool) {
           item.makeMove();
        }
        renderer.renderWorld(worldMap,worldWidth,worldHeight);
    }



    private void createWorld() {

        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
//                System.out.println("x=" + x + ", y=" + y);
                worldMap.put(new Coordinates(x,y),null);
            }
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
    private Renderer renderer = new Renderer();

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
    private void setupCreature(AbstractCreature creature, IMovement<AbstractEntity,Coordinates>movementAction,IFind<Coordinates,EntityType>findAction, IHunt<Coordinates> huntAction) {
        creature.setMoveMethod(movementAction);
        creature.setFindMethod(findAction);
        creature.setHuntMethod(huntAction);
    }
    private void spawnCreature(Coordinates coordinates, EntityType type){
        AbstractCreature creature = null;
        switch (type) {
            case HERBIVORE:
                creature = new Herbivore();
                setupCreature(creature,this::moveEntity,this::BFS,this::destroyGrass);

            break;
            
            case PREDATOR:
                creature = new Predator();
                setupCreature(creature, this::moveEntity, this::BFS, null);
        
            default:
                break;
        }
        creaturePool.add(creature);
        spawnEntity(creature,coordinates);
        System.out.println(creaturePool.size());
    }
    public Queue<Coordinates> BFS(Coordinates start, EntityType type) {
        int[][] distance = new int[worldWidth+2][worldHeight+2];
        var from = new HashMap<Coordinates,Coordinates>();
        var endPoint = new Coordinates(0,0);
        boolean found = false;

        for (int i = 0; i < worldWidth+2; i++) {
            for (int j = 0; j < worldHeight+2; j++) {
                if (i == 0 || i == worldHeight + 1 || j == 0 || j == worldWidth +1 ) {
                    distance[i][j] = -1;
                }
                else {
                    var temp = worldMap.get(new Coordinates(i-1,j-1));
                    if (temp != null && temp.type == type){
                        endPoint = new Coordinates(i-1,j-1);
                        found = true;
                    }
//                    if(temp != null){
//                        System.out.println(temp.getClass().getName()+" "+ (new Coordinates(i,j).x) + " " + (new Coordinates(i,j).y) );
//                    }else{System.out.println("null" + " "+ (new Coordinates(i,j).x) + " " + (new Coordinates(i,j).y) );}
                    if(temp != null && (temp.type != type)) {
                        distance[i][j] = -1;
                        continue;
                    }
                    distance[i][j] = Integer.MAX_VALUE;
                }

            }
        }

        distance[start.y+1][start.x+1] = 0;
        Queue<Coordinates> queue = new LinkedList<>();
        queue.add(start);
        from.put(start,null);

        while (!queue.isEmpty()) {
            Coordinates current = queue.remove();
            for (var i : new int[]{-1,1}){
                var nextX = current.x+i+1;
                var nextY = current.y+1;
                if(distance[nextX][nextY] == Integer.MAX_VALUE){
                    distance[nextX][nextY] = distance[current.x+1][current.y+1]+1;
//                    System.out.println(distance[nextX][nextY]);
                    var tempCoordinate = new Coordinates(nextX-1,nextY-1);
                    queue.add(tempCoordinate);
                    from.put(tempCoordinate,current);
                }
            }

            for (var i : new int[]{-1,1}){
                var nextX = current.x+1;
                var nextY = current.y+i+1;
                if(distance[nextX][nextY] == Integer.MAX_VALUE){
                    distance[nextX][nextY] = distance[current.x+1][current.y+1]+1;
                    var tempCoordinate = new Coordinates(nextX-1,nextY-1);
                    queue.add(tempCoordinate);
                    from.put(tempCoordinate,current);
                }
            }


        }
        if (!found) return null;
        var path = getPath(from,endPoint);
        return path;



    }

    public Deque<Coordinates> getPath(HashMap<Coordinates,Coordinates> from, Coordinates end) {
        var path = new LinkedList<Coordinates>();
        path.add(end);
        var startPath = end;
        while  (startPath!= null){
            startPath = from.get(startPath);
            path.add(startPath);
        }
        path.removeLast();
        path.removeLast();
        Collections.reverse(path);

        return path;
    }

    public void destroyGrass(Coordinates coordinates) {
        worldMap.put(coordinates,null);
        System.out.println("Destroying grass");
        spawnRandomEntity(new Grass());
    }
    private void spawnRandomEntity(AbstractEntity entity) {
        var coordinates = new Coordinates(ThreadLocalRandom.current().nextInt(0, worldWidth), ThreadLocalRandom.current().nextInt(0, worldHeight));
        System.out.println("Spawning "+entity.getType()+" "+coordinates.x + " " + coordinates.y);
        if (worldMap.get(coordinates)==null) {
            System.out.println(worldMap.get(coordinates)==null);
            spawnEntity(entity,coordinates);
        }else{
            System.out.println("oops it's not empty");
            spawnRandomEntity(entity);}

    }

}
