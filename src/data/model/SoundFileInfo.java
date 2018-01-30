package data.model;

import java.util.ArrayList;
import java.util.List;

public class SoundFileInfo {

    /**
     * All audio file location path e.g {"data/aceh/aceh.wav","data/aceh/aceh_1.wav",...}
     * This location path then used for loading audio file for either learning and testing
     */
    private List<String> filePath;

    /**
     * All audio name e.g {"Aceh","Bandung",...}
     * This name will be used for naming the WordModel (HMM). Used only for training
     * The number of audio files per name e.g {30,30,...}
     * This number will be used for
     */

    private List<WordList> wordLists;

    public SoundFileInfo(List<String> filePath, List<WordList> wordsList) {
        this.filePath = new ArrayList<>();
        this.filePath.addAll(filePath);
        this.wordLists = new ArrayList<>();
        this.wordLists.addAll(wordsList);
    }

    public SoundFileInfo() {
        this.filePath = new ArrayList<>();
        this.wordLists = new ArrayList<>();
    }

    public void addFilePath(String path) {
        this.filePath.add(path);
    }

    public void addWord(String word, int amount) {
        wordLists.add(new WordList(amount, word));
    }

    public List<WordList> getWordLists() {
        return wordLists;
    }

    public List<String> getFilePath() {
        return filePath;
    }

    public class WordList {
        private int total;
        private String word;

        private WordList(int total, String word) {
            this.total = total;
            this.word = word;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }
}
