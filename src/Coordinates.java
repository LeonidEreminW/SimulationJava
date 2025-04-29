import java.util.Objects;

public class Coordinates {
    public int x;
    public int y;
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public int hashCode(){
        return Objects.hash(x,y);
    }
    @Override
    public boolean equals(Object obj){
        if (this == obj){return true;}
        if (obj instanceof Coordinates other){
            return this.x == other.x && this.y == other.y;
        }
       return false;
    }
}
