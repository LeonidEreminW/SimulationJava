import java.util.Queue;

public class PrintUtilities {

    public static void print2DArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("\n");
    }

    public static void printPath(Queue<Coordinates> queue) {
        System.out.println("Содержимое очереди:");
        for (var element : queue) {
            if (element != null) {
                System.out.println("x " + element.x + "  y " + element.y);
            }

        }
    }
    public static void print(Object smth){
        System.out.println(smth);
    }

}
