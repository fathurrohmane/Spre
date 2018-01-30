package tools.array;

/**
 * Created by Fathurrohman on 08-Dec-15.
 */
public class Array {

    /**
     * Create new array that fit for adding source array and target array
     * Then copy source array and target array to new array
     *
     * @param one source array. x dimension must be same with target array
     * @param two target array.
     * @return result array with size [source.length + target.length][source[0].length]
     */
    public static double[][] addArray(double[][] one, double[][] two) {
        double[][] result;
        if (one != null && two != null) {
            if (one.length == 0 && two.length != 0) {
                return two;
            } else if (one.length != 0 && two.length == 0) {
                return one;
            } else if (one.length == 0 && two.length == 0) {
                throw new IllegalArgumentException("Zero array");
            } else if (one[0].length == two[0].length) {
                result = new double[one.length + two.length][one[0].length];
                copy2D(one, result);
                copy2D(two, result, one.length);
                return result;
            } else {
                throw new IllegalArgumentException("Array must be in the same dimension");
            }
        } else {
            throw new NullPointerException("Array source and target cant be null!");
        }
    }

    /**
     * Input one={a,b} two={c,d}
     * Output result={a,b,c,d}
     */
    public static double[] addToSideArray(double[] one, double[] two) {
        double[] result;

        if (two == null) {
            throw new IllegalArgumentException("Second array cant be null!");
        }
        if (one != null) {
            result = new double[one.length + two.length];
            System.arraycopy(one, 0, result, 0, one.length);
            System.arraycopy(two, 0, result, one.length, two.length);
        } else {
            result = new double[two.length];
            System.arraycopy(two, 0, result, 0, two.length);
        }
        return result;
    }

    public static void copy2D(double[][] src, double[][] target, int targetOffset) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, target[i + targetOffset], 0, src[0].length);

        }
    }

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

    /**
     * Normalize x dimension of an array
     * input
     * {1,2
     * 3,4,5
     * 6,7,
     * }
     * Output
     * {1,2,0
     * 3,4,5
     * 6,7,0
     * }
     *
     * @param input
     * @return
     */
    public static double[][] normalize(double[][] input) {
        int max = Integer.MIN_VALUE;

        for (double[] anInput : input) {
            if (max < anInput.length) {
                max = anInput.length;
            }
        }

        for (int i = 0; i < input.length; i++) {
            double[] newSizeArray = new double[max];
            System.arraycopy(input[i], 0, newSizeArray, 0, input[i].length);
            input[i] = newSizeArray;
        }
        return input;
    }

    public static void print(String title, double[][] array) {
        System.out.println(title + " :");
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("END of " + title);
    }

    public static void print(String title, int[][] array) {
        System.out.println(title + " :");
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("END of " + title);
    }

    public static void print(String title, int[] array) {
        System.out.println(title + " :");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
        }
        System.out.println();
        System.out.println("END of " + title);
    }

}
