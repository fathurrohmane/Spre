package data.database;

import data.model.SoundFileInfo;
import data.pca.PCA;
import test.validator.hmm.HiddenMarkov;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fathurrohman on 17-Dec-15.
 */
public class DatabaseHandler {

    private static final String DATABASE_FOLDER = "database";
    private static final String CODEBOOK_FOLDER = "codebook";
    private static final String PCA_FOLDER = "pca";
    private static final String WORD_MODEL_FOLDER = "wordmodel";

    private static final String EXTENSION_CODEBOOK = ".cdb";
    private static final String EXTENSION_PCA = ".pca";
    private static final String EXTENSION_WORD_MODEL = ".mdl";

    private static File databasePath = null;

    /**
     * Save codebook object to files
     *
     * @param path name of codebook or word
     */

    public static void setDatabasePath(File path) {
        databasePath = path;
    }

    public static void saveCodebook(Codebook object) {
        try {
            String path = databasePath.getAbsolutePath().concat("\\" + CODEBOOK_FOLDER);
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String pathFile = folder.getAbsolutePath().concat("\\" + "codebook" + EXTENSION_CODEBOOK);
            File codebookFile = new File(pathFile);

            FileOutputStream fileOutputStream = new FileOutputStream(codebookFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(object);
            out.close();
            fileOutputStream.close();
            System.out.println("codebook" + " has been saved in " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePCA(PCA pca) {
        try {
            String path = databasePath.getAbsolutePath().concat("\\" + PCA_FOLDER);
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String pathFile = folder.getAbsolutePath().concat("\\" + "pca" + EXTENSION_PCA);
            File codebookFile = new File(pathFile);

            FileOutputStream fileOutputStream = new FileOutputStream(codebookFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(pca);
            out.close();
            fileOutputStream.close();
            System.out.println("PCA" + " has been saved in " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save Word Model object to files
     *
     * @param name   name of word model
     * @param object word model object
     */
    public static void saveWordModel(String name, WordModel object) {
        try {
            String path = databasePath.getAbsolutePath().concat("\\" + WORD_MODEL_FOLDER);
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(path + "\\" + name + EXTENSION_WORD_MODEL);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(object);
            out.close();
            fileOutputStream.close();
            System.out.println(name + " has been saved in " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load single codebook from database/files
     *
     * @param databaseDirectory name of codebook or word
     * @return Single codebook object
     */
    public static Codebook loadCodeBook(File databaseDirectory) {
        Codebook output = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(databaseDirectory + "\\" + CODEBOOK_FOLDER + "\\" + "codebook" + EXTENSION_CODEBOOK);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            output = (Codebook) in.readObject();
            System.out.println("Codebook" + " has been loaded " + databaseDirectory.getAbsolutePath());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Load pca from database/files
     *
     * @param databaseDirectory name of pca files
     * @return PCA object
     */
    public static PCA loadPCA(File databaseDirectory) {
        PCA output = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(databaseDirectory + "\\" + PCA_FOLDER + "\\" + "pca" + EXTENSION_PCA);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            output = (PCA) in.readObject();
            System.out.println("PCA" + " has been loaded " + databaseDirectory.getAbsolutePath());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Load single word model from database/files
     *
     * @param name name of word model or word
     * @return Single Word Model Object
     */
    public static WordModel loadWordModel(String name) {
        WordModel output = null;
        try {
            String path = new File("").getAbsolutePath().concat("\\" + DATABASE_FOLDER + "\\" + WORD_MODEL_FOLDER);
            FileInputStream fileInputStream = new FileInputStream(path + name + EXTENSION_WORD_MODEL);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            output = (WordModel) in.readObject();
            System.out.println(name + " has been loaded " + path);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Load all word model in database
     *
     * @return Array of Wordmodel object
     */
    public static WordModel[] loadAllWordModel() {
        System.out.println("Load All Word Model");
        ArrayList<WordModel> output = new ArrayList<>();
        int counter = 0;
        try {
            String path = new File("").getAbsolutePath().concat("\\" + DATABASE_FOLDER + "\\" + WORD_MODEL_FOLDER);
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles
                    ) {
                if (file.isFile()) {
                    System.out.println(counter++ + "." + file.getName());
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileInputStream);
                    output.add((WordModel) in.readObject());
                }
            }
            System.out.println("Load Completed with " + counter + "data");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (WordModel[]) output.toArray();
    }

    public static List<HiddenMarkov> loadAllWordModelToHMMs(File databaseDirectory) {
        //System.out.println("Load All Word Model");

        List<HiddenMarkov> output = new ArrayList<>();
        int counter = 0;
        try {
            File dirWordModel = new File(databaseDirectory.getAbsolutePath().concat("\\" + WORD_MODEL_FOLDER));
            File[] listOfFiles = dirWordModel.listFiles();
            for (File file : listOfFiles
                    ) {
                if (file.isFile()) {
                    System.out.println(counter++ + "." + file.getName());
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileInputStream);
                    WordModel wordModel = (WordModel) in.readObject();
                    output.add(new HiddenMarkov(wordModel));
                }
            }
            //System.out.println("Load Completed with " + counter + "data");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }
    // TODO: 05/02/2018  add implmentation to gget all word model list

    /**
     * Read this kind structure file
     * /root
     * //aceh
     * ///aceh_1.wav
     * ///aceh_2.wav
     * ///aceh_3.wav
     * //bandung
     * ///bandung_1.wav
     * ///bandung_2.wav
     * ///bandung_3.wav
     *
     * @param firstLevelDirectories root directory where the training/testing files
     */
    public static SoundFileInfo readFolder(File[] firstLevelDirectories) {
        if (firstLevelDirectories == null) {
            throw new NullPointerException();
        }

        SoundFileInfo soundFileInfo = new SoundFileInfo();
        // Browse all folder
        for (File firstLevelDirectory : firstLevelDirectories) {
            // if its a dir browse it
            if (firstLevelDirectory.isDirectory()) {
                String folderName = firstLevelDirectory.getName();
                System.out.println("Directory :" + folderName);
                // Get file list
                File[] secondLevelDirectories = firstLevelDirectory.listFiles();
                // If not empty
                if (secondLevelDirectories != null) {
                    // Save its name
                    int counter = 0;
                    for (File secondLevelDirectory : secondLevelDirectories) {
                        if (secondLevelDirectory.isFile()) {
                            soundFileInfo.addFilePath(secondLevelDirectory.getAbsolutePath());
                            counter++;
                        }
                    }
                    soundFileInfo.addWord(folderName, counter);
                    // If empty
                } else {
                    System.out.println("Directory :" + firstLevelDirectory.getName() + " is EMPTY");
                }
                // if not show error
            } else if (firstLevelDirectory.isFile()) {
                System.out.println("File Outside Folder wont be read");
                return null;
            }
        }
        return soundFileInfo;
    }
}
