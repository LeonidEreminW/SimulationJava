import java.util.concurrent.ThreadLocalRandom;

public class Herbivore extends AbstractCreature{

    @Override
    public void makeMove() {
        var x = ThreadLocalRandom.current().nextInt(0, 4);
        var y = ThreadLocalRandom.current().nextInt(0, 4);
        action.execute(this,new Coordinates(x,y));
    }
    public Herbivore() {
        setImg("\uD83D\uDC07");
    }
}
