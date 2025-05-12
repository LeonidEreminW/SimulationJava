import java.util.Queue;

public interface IMovement<AbstractEntity,Coordinates>{
    void move(AbstractEntity abstractEntity, Coordinates coordinates);
}
