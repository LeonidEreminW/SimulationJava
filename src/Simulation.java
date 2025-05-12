import java.util.*;
import java.util.concurrent.*;

public class Simulation {
    Map<Coordinates,AbstractEntity> worldMap = new HashMap<Coordinates,AbstractEntity>();
    private int worldHeight = 5;
    private int worldWidth = 5;
    private boolean isPaused = true;
    private int simulationInterval =1;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ArrayList<AbstractCreature> creaturePool = new ArrayList<>();

    public void startSimulation() {
        createWorld();
        spawnCreature(new Coordinates(0,0));
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
            for (int x = 0; x < worldHeight; x++) {
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
//        worldMap.put(new Coordinates(4,1),new Tree());
//        worldMap.put(new Coordinates(0,0),new Tree());
//        worldMap.put(new Coordinates(3,3),new Tree());
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
    private void setupCreature(AbstractCreature creature, IMovement<AbstractEntity,Coordinates>movementAction,IFind<Coordinates,EntityType>findAction) {
        creature.setMoveMethod(movementAction);
        creature.setFindMethod(findAction);
    }
    private void spawnCreature(Coordinates coordinates){
        var testHerb = new Herbivore();
        setupCreature(testHerb,this::moveEntity,this::BFS);
        testHerb.setHunt(this::destroyGrass);
        creaturePool.add(testHerb);
        spawnEntity(testHerb,coordinates);
    }
    public Queue<Coordinates> BFS(Coordinates start, EntityType type) {
        int[][] distance = new int[worldHeight+2][worldWidth+2];
        var from = new HashMap<Coordinates,Coordinates>();
        var endPoint = new Coordinates(0,0);

        for (int i = 0; i < worldHeight+2; i++) {
            for (int j = 0; j < worldWidth+2; j++) {
                if (i == 0 || i == worldHeight + 1 || j == 0 || j == worldWidth +1 ) {
                    distance[i][j] = -1;
                }
                else {
                    var temp = worldMap.get(new Coordinates(i-1,j-1));
                    if (temp != null && temp.type == type){
                        endPoint = new Coordinates(i-1,j-1);
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
//        print2DArray(distance);
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
//                    System.out.println(distance[nextX][nextY]);
                    var tempCoordinate = new Coordinates(nextX-1,nextY-1);
                    queue.add(tempCoordinate);
                    from.put(tempCoordinate,current);
                }
            }


        }
        var path = getPath(from,endPoint);
//        print2DArray(distance);
//        printPath(path);
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
        return path.reversed();
    }

    public void destroyGrass(Coordinates coordinates) {
        worldMap.put(coordinates,null);
        System.out.println("Destroying grass");
        spawnRandomEntity(new Grass());
    }
    private void spawnRandomEntity(AbstractEntity entity) {
        var coordinates = new Coordinates(ThreadLocalRandom.current().nextInt(0, worldHeight),ThreadLocalRandom.current().nextInt(0, worldWidth));
        System.out.println("Spawning grass"+coordinates.x + " " + coordinates.y);
        if (worldMap.get(coordinates)==null) {
            spawnEntity(entity,coordinates);
        }else{
            spawnRandomEntity(entity);}

    }

    public void print2DArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("\n");
    }
    public static void printPath(Queue<Coordinates> queue) {
        System.out.println("Содержимое очереди:");
        for (var element : queue) {
            if(element != null) {
                System.out.println("x " + element.x + "  y " + element.y);
            }

        }
    }


    public void Test(){
        createWorld();
        spawnRandomEnviroment(3,2,1);
        spawnEntity(new Grass(), new Coordinates(3,4));
        renderer.renderWorld(worldMap,worldWidth,worldHeight);
        BFS(new Coordinates(0,0),EntityType.GRASS);

    }

}
