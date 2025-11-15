public class Predator extends AbstractCreature{
    @Override
    public void makeMove() {
        var pathToPrey = findPrey.find(getPosition(),EntityType.HERBIVORE);
        var speedPoints = speed;
        while (speedPoints>0 && pathToPrey!=null && !pathToPrey.isEmpty()) {
            speedPoints-=1;
            if (pathToPrey.size()==1){
                var cordinate = pathToPrey.remove();
                hunt.hunt(cordinate);
                break;
            } else {
                move.move(this,pathToPrey.remove());
            }

        }
    }
    public Predator() {

        setImg("\uD83D\uDC05");
        setSpeed(1);
        setHealth(3);
        type = EntityType.PREDATOR;
    }
    

}
