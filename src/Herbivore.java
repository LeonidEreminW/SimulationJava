public class Herbivore extends AbstractCreature{
    @Override
    public void makeMove() {
        var pathToPrey = findPrey.find(getPosition(),EntityType.GRASS);
        var speedPoints = speed;
        while (speedPoints>0 && pathToPrey!=null && !pathToPrey.isEmpty()) {
            speedPoints-=1;
            if (pathToPrey.size()==1){
                var cordinate = pathToPrey.remove();
                hunt.hunt(cordinate);
            } else {
                moveTo(pathToPrey.remove());
            }

        }
    }

    public void moveTo(Coordinates endPoint) {
        move.move(this,endPoint);
    }
    public Herbivore() {
        setImg("\uD83D\uDC07");
        setSpeed(2);
        setHealth(3);
        type = EntityType.HERBIVORE;
    }
}
