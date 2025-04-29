import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractCreature  extends AbstractEntity{
    public abstract void makeMove();
    protected IMovement<AbstractEntity,Coordinates> action;
    public void setAction(IMovement<AbstractEntity,Coordinates> action1) {
        action = action1;
    }

}
