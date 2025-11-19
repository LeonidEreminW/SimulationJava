package Main.Entity;

import Main.Coordinates;
import Main.EntityType;
import Main.Interfaces.IHunt;

public class Predator extends AbstractCreature{
    private IHunt<Coordinates> hunt;
        int damage = 0;

    @Override
    public void makeMove() {
        var pathToPrey = findPrey.find(getPosition(), EntityType.HERBIVORE);
        var speedPoints = speed;
        while (speedPoints>0 && pathToPrey!=null && !pathToPrey.isEmpty()) {
            speedPoints-=1;
            if (pathToPrey.size()==1){
                var cordinate = pathToPrey.remove();
                hunt.hunt(cordinate,damage);
                break;
            } else {
                move.move(this,pathToPrey.remove());
            }

        }
    }
    public void setHuntMethod(IHunt<Coordinates> action){
        hunt = action;
    }
    public Predator() {

        setImg("\uD83D\uDC05");
        setSpeed(2);
        setHealth(3);
        damage = 1;
        type = EntityType.PREDATOR;
    }
    

}
