public class Herbivore extends AbstractCreature{
    private IEat<Coordinates> eat;
    @Override
    public void makeMove() {
        var pathToPrey = findPrey.find(getPosition(),EntityType.GRASS);
        var speedPoints = speed;
        while (speedPoints>0 && pathToPrey!=null && !pathToPrey.isEmpty()) {
            speedPoints-=1;
            if (pathToPrey.size()==1){
                var cordinate = pathToPrey.remove();
                eat.eat(cordinate);
            } else {
                moveTo(pathToPrey.remove());
            }

        }
    }

    public void moveTo(Coordinates endPoint) {
        move.move(this,endPoint);
    }
    public void setEatMethod(IEat<Coordinates> action){
        eat = action;
    }
    public Herbivore() {
        setImg("\uD83D\uDC07");
        setSpeed(1);
        setHealth(3);
        type = EntityType.HERBIVORE;
    }
}
