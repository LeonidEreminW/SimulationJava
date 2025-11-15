public abstract class AbstractCreature  extends AbstractEntity{
    public abstract void makeMove();
    protected IMovement<AbstractEntity,Coordinates> move;
    protected IFind<Coordinates,EntityType> findPrey;
    protected IHunt<Coordinates> hunt;
    protected int speed;
    protected int health;
    protected int turns = 1;
    public void setMoveMethod(IMovement<AbstractEntity,Coordinates> action) {
        move = action;
    }
    public void setFindMethod(IFind<Coordinates,EntityType> action) {
        findPrey = action;
    }
    public void setHuntMethod(IHunt<Coordinates> action){
        hunt = action;
    }
    protected void setSpeed(int speed) {
        this.speed = speed;
    }
    protected void setHealth(int health) {
        this.health = health;
    }


}
