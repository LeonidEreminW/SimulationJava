import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractCreature  extends AbstractEntity{
    public abstract void makeMove();
    protected IMovement<AbstractEntity,Coordinates> action;
    protected int speed;
    protected int health;
    public void setAction(IMovement<AbstractEntity,Coordinates> action1) {
        action = action1;
    }
    protected void setSpeed(int speed) {
        this.speed = speed;
    }
    protected void setHealth(int health) {
        this.health = health;
    }

}
