public interface IMovement<AbstractEntity,Coordinates>{
    void execute(AbstractEntity abstractEntity, Coordinates coordinates);
}
