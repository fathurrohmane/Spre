package tools;

/**
 * Created by Taruna 98 on 01/08/2017.
 */
public interface ProcessListener {

    int BASIC = -1;
    int MFCC = 0;
    int PCA = 1;
    int VQ = 2;
    int HMM = 3;
    int TIMESTAMP = 4;

    /**
     * write process log
     * @param processType type of the process. MFCC = 0, PCA = 1 etc
     * @param context message
     */
    void writeLog(int processType, String context);
}
