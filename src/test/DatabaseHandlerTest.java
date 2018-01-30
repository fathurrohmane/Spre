package test;

import data.database.DatabaseHandler;
import data.model.SoundFileInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DatabaseHandlerTest {

    @Test
    public void readFolder() {

        File[] files = new File("L:\\Cloud\\Dropbox\\TA\\Dataset\\DataTesting2").listFiles();
        SoundFileInfo soundFileInfo;

        assert files != null;
        soundFileInfo = DatabaseHandler.readFolder(files);

        assert soundFileInfo != null;
        for (SoundFileInfo.WordList wordList : soundFileInfo.getWordLists()
                ) {
            Assert.assertTrue(wordList.getTotal() >= 5);
        }

    }
}