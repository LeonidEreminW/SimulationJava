package Main.Entity;

import Main.*;
import Main.Interfaces.IFind;
import Main.Interfaces.IHunt;
import Main.Interfaces.IMovement;
import Main.Interfaces.IOnDeath;

public abstract class AbstractCreature  extends AbstractEntity{
    public abstract void makeMove();
    protected IMovement<AbstractEntity, Coordinates> move;
    protected IFind<Coordinates, EntityType> findPrey;
    protected IHunt<Coordinates> hunt;
    protected IOnDeath triggerOnDeath;
    protected int speed;
    protected int health;
    public void takeDamageBehavior(int damage){
        health -= damage;
        if(health <= 0){
            triggerOnDeath.onDeath();
        }
    }

    public void setMoveMethod(IMovement<AbstractEntity,Coordinates> action) {
        move = action;
    }
    public void setFindMethod(IFind<Coordinates,EntityType> action) {
        findPrey = action;
    }
    public void setHuntMethod(IHunt<Coordinates> action){
        hunt = action;
    }
    public void setOnDeathMethod(IOnDeath action){
        triggerOnDeath = action;
    }
    protected void setSpeed(int speed) {
        this.speed = speed;
    }
    protected void setHealth(int health) {
        this.health = health;
    }


}
