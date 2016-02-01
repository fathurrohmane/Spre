package tools;

import classification.Processor;

import java.io.File;

/**
 * Created by Fathurrohman on 28-Dec-15.
 * Class to execute sound process multithread
 */
public class Executor extends Thread {

    File soundFile;
    int cluster;
    String word;

    public Executor(String word, int cluster, File soundFile) {
        this.soundFile = soundFile;
        this.word = word;
        this.cluster = cluster;
    }

    @Override
    public void run() {
        Processor.startTraining(word, cluster, soundFile);
    }
}