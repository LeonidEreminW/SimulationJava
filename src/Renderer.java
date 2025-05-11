import java.util.Map;

public class Renderer {

    public void renderWorld(Map<Coordinates,AbstractEntity> world,int width,int height) {
        for (int i = 0; i < width; i++) {
            System.out.println();
            for (int j = 0; j < height; j++) {
                var entity = world.get(new Coordinates(i,j));
                if (entity != null) {
                    System.out.printf(" %s ", entity.getImg());
                }else{System.out.print(" \u2B1B ");}

            }
        }
        System.out.println("\n");
        System.out.println("- - - - - - - - - - - - - -");


    }
}
