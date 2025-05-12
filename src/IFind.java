import java.util.Queue;

public interface IFind<Coordinates,EntityType> {
    Queue<Coordinates> find(Coordinates coordinates, EntityType entityType);

}
