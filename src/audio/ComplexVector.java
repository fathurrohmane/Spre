package audio;

import audio.feature_extraction.FFT;

/**
 * Created by Fathurrohman on 04-Sep-15.
 */
public class ComplexVector {

    private int length;

    public double[] real;

    public double[] imaginary;

    public ComplexVector(double[] signal) {
        length = signal.length;

        real = new double[length];
        imaginary = new double[length];

        real = signal;

        FFT.transform(real, imaginary);

    }

}
