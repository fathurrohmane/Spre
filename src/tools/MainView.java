package tools;

import java.util.List;

public interface MainView {
    void writeToTextAreaConsole(int processType, String input);

    void writeToTextAreaTrainedWordList(List<String> words);

    void writeProgress(int progress);

    void writeToLabelRecognitionRate(double rate);
}
