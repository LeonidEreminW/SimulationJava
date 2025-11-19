package Main.Interfaces;

public interface IMovement<AbstractEntity,Coordinates>{
    void move(AbstractEntity abstractEntity, Coordinates coordinates);
}
