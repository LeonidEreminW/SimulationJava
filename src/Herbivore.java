public class Herbivore extends AbstractCreature{
    @Override
    public void makeMove() {
        action.execute(this,new Coordinates(1,1));
    }
    public Herbivore() {
        setImg("\uD83D\uDC07");
    }
}
