package data;

import tools.ProcessListener;

public abstract class Process {

    private ProcessListener listener;
    private int processType = -1;

    public void setListener(int processType, ProcessListener listener) {
        this.listener = listener;
        this.processType = processType;
    }

    public void removeListener() {
        if (listener != null) {
            this.listener = null;
        }
    }

    protected void writeLog(String context) {
        if (listener != null) {
            listener.writeLog(processType, context);
        }
    }
}
