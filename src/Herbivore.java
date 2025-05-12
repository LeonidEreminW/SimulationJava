public class Herbivore extends AbstractCreature{
    private IDestroyGrass<Coordinates> eat;
    @Override
    public void makeMove() {
        var pathToPrey = findPrey.find(getPosition(),EntityType.GRASS);
        var speedPoints = speed;
        while (speedPoints>0&&pathToPrey!=null) {
            speedPoints-=1;
            if (pathToPrey.size()==1){
                var cordinate = pathToPrey.remove();
                eat.destroy(cordinate);
            }else{moveTo(pathToPrey.remove());}

        }
    }
    public void setHunt(IDestroyGrass<Coordinates> eat) {
        this.eat = eat;
    }

    public void moveTo(Coordinates endPoint) {
        move.move(this,endPoint);
    }
    public Herbivore() {
        setImg("\uD83D\uDC07");
        setSpeed(1);
        setHealth(3);
        type = EntityType.HERBIVORE;
    }
}
