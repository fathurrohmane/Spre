package tools.array;

/**
 * Created by Fathurrohman on 08-Dec-15.
 */
public class Array {

    public static void copy2D(double[][] src, double[][] target) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[0].length);
        }
    }

    public static void copy2D(int[][] src, int[][] target) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[0].length);
        }
    }

    public static void copy(double[] src, double[] target) {
        System.arraycopy(src, 0, target, 0, src.length);
    }

    public static void copy(int[] src, int[] target) {
        System.arraycopy(src, 0, target, 0, src.length);
    }

    public static void print(String title,double[][] array) {
        System.out.println(title + " :");
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("END of "+title);
    }

    public static void print(String title,int[][] array) {
        System.out.println(title + " :");
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("END of "+title);
    }

    public static void print(String title,int[] array) {
        System.out.println(title + " :");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
        }
        System.out.println();
        System.out.println("END of "+title);
    }

}
